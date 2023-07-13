/**
 * Scheduling Software for WGU course C195 Software II - Advanced Java Concepts
 * Created By Brittany Nelson
 * Creation Date: 01/25/2018
 * Last updated: 03/22/2018
 */

package SchedulingSoftware;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import static java.time.ZoneOffset.UTC;


public class Main extends Application {

    //Initialize a Reminder object to store the next scheduled appointment reminder for the user logged in
    Reminder nextScheduledAppt;

    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Instantiate the LoginPage scene
        Parent login = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        Scene loginScene = new Scene(login, 1000, 600);

        //Set the stage to show the login scene
        primaryStage.setTitle("Schedule Assistant");
        primaryStage.setScene(loginScene);
        primaryStage.show();

        //Make sure all running tasks/threads are killed when the application is closed
        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });

        //Schedule a timer to execute the method to update the next scheduled appointment every 20 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //Call the method to run the query and update the appointment variable
                getNextAppointment();
                //If the appointment is not null and its start time is within the next 15 minutes, display a message
                if (nextScheduledAppt.getReminderDate() != null && nextScheduledAppt.getReminderDate().isBefore(ZonedDateTime.now())) {
                    Platform.runLater(() -> {
                        //Create Snooze and Dismiss buttons
                        ButtonType snooze = new ButtonType("Snooze", ButtonBar.ButtonData.OK_DONE);
                        ButtonType dismiss = new ButtonType("Dismiss", ButtonBar.ButtonData.CANCEL_CLOSE);
                        //Determine if this is a 15 minute reminder or an immediate reminder to determine what time of message will be displayed
                        if (nextScheduledAppt.getSnoozeIncrement() > 0) {
                            //Get the time of the appointment to display in the alert
                            ZonedDateTime apptTime = nextScheduledAppt.getApptTime();
                            //Create a reminder popup with the hour and minute of the scheduled appointment
                            Alert remAlert = new Alert(Alert.AlertType.CONFIRMATION, "You have an appointment scheduled at " +
                                    String.format("%02d",apptTime.getHour()) + ":" + String.format("%02d",apptTime.getMinute()) + ".",snooze,dismiss);
                            remAlert.setTitle("Scheduled Appointment Starting Soon");
                            remAlert.setHeaderText(null);
                            remAlert.setGraphic(null);
                            remAlert.showAndWait()
                                    .ifPresent(response -> {
                                //If snooze is clicked, delete the current reminder and create a new one 5 minutes from now
                                if (response == snooze) {
                                    //Prepare the delete statement for the existing reminder
                                    String remDelete = "DELETE FROM reminder WHERE reminderId = '" + nextScheduledAppt.getReminderId() + "';";
                                    //Execute the delete statement
                                    dbConnect.executeUpdate(remDelete);
                                    //Determine the correct time for the new reminder, 5 minutes from now or at the appointment time if it's closer
                                    //Create a ZonedDateTime to hold the new time and an int to hold the new snooze increment
                                    ZonedDateTime newRemTime;
                                    int newSnoozeIncrement;
                                    if (nextScheduledAppt.getReminderDate().plusMinutes(5).isBefore(apptTime)) {
                                        newRemTime = nextScheduledAppt.getReminderDate().plusMinutes(5).withZoneSameInstant(UTC);
                                        newSnoozeIncrement = nextScheduledAppt.getSnoozeIncrement() - 5;
                                    } else {
                                        newRemTime = apptTime.withZoneSameInstant(UTC);
                                        newSnoozeIncrement = 0;
                                    }
                                    //Create a DateTimeFormatter to put the time in the correct format for writing to the DB
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                                    //Create an insert statement for the new reminder
                                    String remInsert = "INSERT INTO reminder VALUES ('" + nextScheduledAppt.getReminderId() + "','" +
                                            dtf.format(newRemTime) + "','" + newSnoozeIncrement + "','1','" + nextScheduledAppt.getAppointmentId() + "','" +
                                            controllerReference.getUserLoggedIn() + "',NOW(),'" + nextScheduledAppt.getReminderCol() + "');";
                                    //Execute the insert statement
                                    dbConnect.executeUpdate(remInsert);
                                //If Dismiss is clicked, delete the reminder so the user won't get more popups for it
                                } else if (response == dismiss){
                                    //Prepare the delete statement for the existing reminder
                                    String remDelete = "DELETE FROM reminder WHERE reminderId = '" + nextScheduledAppt.getReminderId() + "';";
                                    //Execute the delete statement
                                    dbConnect.executeUpdate(remDelete);
                                }
                            });
                        } else {
                            Alert remAlert = new Alert(Alert.AlertType.INFORMATION, "You have an appointment scheduled for right now.");
                            remAlert.setTitle("Scheduled Appointment Starting Now");
                            remAlert.setHeaderText(null);
                            remAlert.setGraphic(null);
                            remAlert.show();
                        }
                    });
                }
            }
           //Give the user 10 seconds to login before we start looking for their info
        }, Date.from(ZonedDateTime.now().toInstant().plusMillis(10000)), 20000);
    }

    //Create method to query the database to find the next scheduled appointment for a logged in user
    public void getNextAppointment() {
        Reminder rem = new Reminder();
        //Prepare a query statement
        String remQuery = "SELECT * FROM reminder WHERE appointmentId in (SELECT appointmentId FROM appointment WHERE contact ='" +
                controllerReference.getUserLoggedIn() + "' AND start > NOW()) ORDER BY reminderDate ASC;";
        //Execute the query and load the results into a ResultSet
        ResultSet rs = dbConnect.executeQuery(remQuery);
        try {
            //Only look at the first row of data
            if(rs.next()) {
                //Get the results and add them to the reminder
                rem.setReminderId(rs.getInt("reminderId"));
                rem.setSnoozeIncrement(rs.getInt("snoozeIncrement"));
                rem.setAppointmentId(rs.getInt("appointmentId"));
                rem.setReminderCol(rs.getString("remindercol"));
                //Get the reminder time in UTC from the DB
                ZonedDateTime remTimeUTC = ZonedDateTime.ofInstant(rs.getTimestamp("reminderDate").toInstant(), UTC);
                //Convert the UTC time to the user's time zone and set it for the reminder
                ZonedDateTime remTimeLocalTZ = remTimeUTC.withZoneSameInstant(controllerReference.getTimeZoneId());
                rem.setReminderDate(remTimeLocalTZ);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        nextScheduledAppt = rem;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
