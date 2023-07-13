package SchedulingSoftware;

import java.sql.ResultSet;
import java.time.ZonedDateTime;
import static java.time.ZoneOffset.UTC;

public class Reminder {

    //Declare variables to be stored for class
    private int reminderId;
    private ZonedDateTime reminderDate;
    private int snoozeIncrement;
    private int appointmentId;
    private String reminderCol;

    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Constructor statement, initialize all variables at default values
    public Reminder() {
        this.reminderId = 0;
        this.reminderDate = null;
        this.snoozeIncrement = 0;
        this.appointmentId = 0;
        this.reminderCol = "";
    }

    //Getters and setters for all variables pulled from DB
    public int getReminderId() {
        return reminderId;
    }
    public void setReminderId(int id) {
        this.reminderId = id;
    }
    public ZonedDateTime getReminderDate() {
        return reminderDate;
    }
    public void setReminderDate(ZonedDateTime date) {
        this.reminderDate = date;
    }
    public int getSnoozeIncrement() {
        return snoozeIncrement;
    }
    public void setSnoozeIncrement(int increment) {
        this.snoozeIncrement = increment;
    }
    public int getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(int id) {
        this.appointmentId = id;
    }
    public String getReminderCol() {
        return reminderCol;
    }
    public void setReminderCol(String reminderCol) {
        this.reminderCol = reminderCol;
    }

    //Create method to calculate how many minutes are left until the appointment
    public ZonedDateTime getApptTime() {
        //Initialize a String to hold the results
        ZonedDateTime apptTime = null;
        //Prepare a query string
        String timeQuery = "SELECT start from appointment WHERE appointmentId = '" + this.appointmentId + "';";
        //Execute the query and load the results into a ResultSet
        ResultSet rs = dbConnect.executeQuery(timeQuery);
        try {
            while (rs.next()) {
                //Get the appt time from the DB
                ZonedDateTime apptTimeUTC = ZonedDateTime.ofInstant(rs.getTimestamp(1).toInstant(), UTC);
                //Convert it to user's timeZone and set it to the variable
                apptTime = apptTimeUTC.withZoneSameInstant(controllerReference.getTimeZoneId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return apptTime;
    }
}
