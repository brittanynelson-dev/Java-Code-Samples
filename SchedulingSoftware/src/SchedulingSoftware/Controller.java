/**
 * Scheduling Software for WGU course C195 Software II - Advanced Java Concepts
 * Created By Brittany Nelson
 * Creation Date: 01/25/2018
 * Last Updated: 03/22/2018
 */

package SchedulingSoftware;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Scanner;


public class Controller {

    //Get user location to determine appropriate language errors
    Locale location = Locale.getDefault();
    //DEBUG ONLY
    //Locale location = Locale.UK;
    //Locale location = Locale.TAIWAN;

    //Get a reference to the DBInterface for use on pages
    DBInterface dbConnect = new DBInterface();

    //Create a variable to hold the user that is currently logged in for reporting purposes
    static String userLoggedIn;

    //Create a variable to hold the user's current TimeZone
    static ZoneId timeZone;

/**************************************************************************************************************
 * LoginPage Logic
***************************************************************************************************************/

    //Create references to GUI objects on LoginPage
    @FXML
    TextField usernameTF;
    @FXML
    PasswordField passwordPF;
    @FXML
    Label enUserPassBlankLbl;
    @FXML
    Label esUserPassBlankLbl;
    @FXML
    Label frUserPassBlankLbl;
    @FXML
    Label altFrUserPassBlankLbl;
    @FXML
    Label enUserPassMatchLbl;
    @FXML
    Label esUserPassMatchLbl;
    @FXML
    Label frUserPassMatchLbl;
    @FXML
    Label altFrUserPassMatchLbl;
    @FXML
    Label enInactiveUserLbl;
    @FXML
    Label esInactiveUserLbl;
    @FXML
    Label frInactiveUserLbl;
    @FXML
    Label altFrInactiveUserLbl;
    @FXML
    Label enDBErrorLbl;
    @FXML
    Label esDBErrorLbl;
    @FXML
    Label frDBErrorLbl;
    @FXML
    Label altFrDBErrorLbl;


    //Create logic to check login credentials when the Submit button on the Login Page is clicked
    public void submitBtnAction(ActionEvent event) {

        //Clear any existing error messages
        enUserPassBlankLbl.setVisible(false);
        esUserPassBlankLbl.setVisible(false);
        frUserPassBlankLbl.setVisible(false);
        altFrUserPassBlankLbl.setVisible(false);
        enUserPassMatchLbl.setVisible(false);
        esUserPassMatchLbl.setVisible(false);
        frUserPassMatchLbl.setVisible(false);
        altFrUserPassMatchLbl.setVisible(false);
        enInactiveUserLbl.setVisible(false);
        esInactiveUserLbl.setVisible(false);
        frInactiveUserLbl.setVisible(false);
        altFrInactiveUserLbl.setVisible(false);
        enDBErrorLbl.setVisible(false);
        esDBErrorLbl.setVisible(false);
        frDBErrorLbl.setVisible(false);
        altFrDBErrorLbl.setVisible(false);

        //Get text from TextFields
        String user = usernameTF.getText();
        String pass = passwordPF.getText();

        //If username or password box is empty, print error messages on screen
        if (user.isEmpty() || pass.isEmpty()) {
            //Set English error for all users
            enUserPassBlankLbl.setVisible(true);
            //If in US, set Spanish error
            if (location.getCountry() == "US") {
                esUserPassBlankLbl.setVisible(true);
                return;
                //If user is in UK (GB), set French error
            } else if (location.getCountry() == "GB") {
                frUserPassBlankLbl.setVisible(true);
                return;
                //If user is in any other country, trigger all 3 language errors
            } else {
                esUserPassBlankLbl.setVisible(true);
                altFrUserPassBlankLbl.setVisible(true);
                return;
            }
        }

        //Connect to the database to verify login info is correct and the user is currently active
        int loginCode = dbConnect.login(user,pass);
        //Check the result of loginCode and display appropriate error message
        if (loginCode == 0) {
            //Passed login checks, call method to change the scene to MainPage
            changeToMainPage(event);
            //Need to pull the user's timezone for use with appointments and calendar
            getTimeZone();
        } else if (loginCode == 1) {
            //Username and password are valid, but the user is not active. Display error messages.
            //Set English error for all users
            enInactiveUserLbl.setVisible(true);
            //If in US, set Spanish error
            if (location.getCountry() == "US") {
                esInactiveUserLbl.setVisible(true);
                return;
                //If user is in UK (GB), set French error
            } else if (location.getCountry() == "GB") {
                frInactiveUserLbl.setVisible(true);
                return;
                //If user is in any other country, trigger all 3 language errors
            } else {
                esInactiveUserLbl.setVisible(true);
                altFrInactiveUserLbl.setVisible(true);
                return;
            }
        } else if (loginCode == 2) {
            //Username and password combination were not found. Display error messages.
            //Set English error for all users
            enUserPassMatchLbl.setVisible(true);
            //If in US, set Spanish error
            if (location.getCountry() == "US") {
                esUserPassMatchLbl.setVisible(true);
                return;
                //If user is in UK (GB), set French error
            } else if (location.getCountry() == "GB") {
                frUserPassMatchLbl.setVisible(true);
                return;
                //If user is in any other country, trigger all 3 language errors
            } else {
                esUserPassMatchLbl.setVisible(true);
                altFrUserPassMatchLbl.setVisible(true);
                return;
            }
        } else {
            //Login failed, either unable to reach DB or unable to update variable due to program fault. Display error messages.
            //Set English error for all users
            enDBErrorLbl.setVisible(true);
            //If in US, set Spanish error
            if (location.getCountry() == "US") {
                esDBErrorLbl.setVisible(true);
                return;
                //If user is in UK (GB), set French error
            } else if (location.getCountry() == "GB") {
                frDBErrorLbl.setVisible(true);
                return;
                //If user is in any other country, trigger all 3 language errors
            } else {
                esDBErrorLbl.setVisible(true);
                altFrDBErrorLbl.setVisible(true);
                return;
            }
        }
    }

    //Method to pull the user's timezone from the AccountTZs.txt file or create a new default entry if they've never logged in before
    public void getTimeZone() {
        //First, check to see if there is a saved record for this user already, initialize a boolean to determine if this is the first login
        boolean firstLogin = true;
        File tzFile = new File("AccountTZs.txt");
        //If the file exists, we need to read it to see if there's a record for this user already
        if (tzFile.exists()) {
            //Initialize a BufferedReader to read the contents of the file
            BufferedReader br = null;
            try {
                //Set up br to read the file
                br = new BufferedReader(new FileReader(tzFile));
                //Initialize a String to hold the current line read
                String currentLine;
                //While the currentLine is not null, read it
                while ((currentLine = br.readLine()) != null) {
                    //Create a Scanner to break the line based on a comma delimiter
                    Scanner sc = new Scanner(currentLine).useDelimiter(",");
                    //Extract values from the currentLine
                    String username = sc.next();
                    String fileTZ = sc.next();

                    //If we find a match on username, set timeZone to the associated zoneId
                    if (username.equals(userLoggedIn)) {
                        timeZone = ZoneId.of(fileTZ);
                        //Set firstLogin to false, they already have a record from a previous login
                        firstLogin = false;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                //Close the BufferedReader
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        //If a previous record was not found, we need to create a new record for this user in the AccountsTZs.txt file
        if (firstLogin) {
            //Set the timeZone to the system default for the user
            timeZone = ZoneId.systemDefault();
            //Initialize a BufferedWriter to write records to the file
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(tzFile, true));
                //Append a new entry to the file
                bw.write(userLoggedIn + "," + timeZone + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                //Close the BufferedWriter
                try {
                    bw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

/***************************************************************************************************************
 * MainPage Logic
***************************************************************************************************************/

    //Call method to load CustomerMaintenancePage when appropriate button is clicked
    public void cxMaintBtnAction(ActionEvent event) {
        changeToCxMaintPage(event);
    }

    //Create logic to load ApptSchedulingPage when appropriate button is clicked
    public void apptSchedBtnAction(ActionEvent event) {
        changeToApptSchedulingPage(event);
    }

    //Create logic to load CalendarViewPage when appropriate button is clicked
    public void calViewBtnAction(ActionEvent event) {
        try {
            FXMLLoader calLoader = new FXMLLoader(getClass().getResource("CalendarViewPage.fxml"));
            Parent calendar = calLoader.load();
            Scene calScene = new Scene(calendar, 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(calScene);
            //Get a reference to the CalendarController and populate the calendars on that page
            CalendarController calController = calLoader.getController();
            LocalDate date = LocalDate.now();
            calController.populateWeek(date);
            calController.populateMonth(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Create logic to load ReportsPage when appropriate button is clicked
    public void reportsBtnAction(ActionEvent event) {
        try {
            Parent reports = FXMLLoader.load(getClass().getResource("ReportsPage.fxml"));
            Scene reportsScene = new Scene(reports, 1000, 600);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(reportsScene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Create logic to load SetupPage when appropriate button is clicked
    public void setupBtnAction(ActionEvent event) {
        try {
            FXMLLoader setupLoader = new FXMLLoader(getClass().getResource("SetupPage.fxml"));
            Parent setup = setupLoader.load();
            Scene setupScene = new Scene(setup, 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(setupScene);
            //Set the current timezone for the user as the default in the Combobox
            SetupController setupController = setupLoader.getController();
            setupController.setDefaultTimeZone(timeZone);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

/*************************************************************************************************************
 * Multi-Page/Reusable methods
 *************************************************************************************************************/

    //Create method to set the userLoggedIn variable, will be called from DBInterface upon login
    public static void setUserLoggedIn(String username) {
        userLoggedIn = username;
    }

    //Create method to get the userLoggedIn variable, can be called by any controller
    public static String getUserLoggedIn() {
        return userLoggedIn;
    }

    //Create method to set the timeZone variable from other controllers
    public static void setTimeZone(String tz) {
        timeZone = ZoneId.of(tz);
    }

    //Create method to return the user's timeZone as a ZoneId object
    public static ZoneId getTimeZoneId() {
        return timeZone;
    }

    //Create logic to load the MainPage when passed an ActionEvent from a button
    public void changeToMainPage(ActionEvent event) {
        try {
            Parent main = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            Scene mainScene = new Scene(main, 1000, 600);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Create logic to load the CustomerMaintenancePage when passed an ActionEvent from a button
    public void changeToCxMaintPage(ActionEvent event) {
        try {
            FXMLLoader cxMaintLoader = new FXMLLoader(getClass().getResource("CxMaintenancePage.fxml"));
            Parent cxMaint = cxMaintLoader.load();
            Scene cxMaintScene = new Scene(cxMaint, 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(cxMaintScene);
            //Get a reference to the CxMaintController and populate the TableView on that page
            CxMaintController cxMaintController = cxMaintLoader.getController();
            cxMaintController.populateCxTable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void changeToApptSchedulingPage(ActionEvent event) {
        try {
            FXMLLoader apptSchedLoader = new FXMLLoader(getClass().getResource("ApptSchedulingPage.fxml"));
            Parent apptSched = apptSchedLoader.load();
            Scene apptSchedScene = new Scene(apptSched, 1000, 600);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(apptSchedScene);
            //Get a reference to the ApptSchedulingController and populate the TableView on that page
            ApptSchedulingController apptSchedController = apptSchedLoader.getController();
            //Pass the logged in user as well as the default view settings - only this user's appointments and only future ones
            apptSchedController.populateApptTable(userLoggedIn,true,true);
            apptSchedController.setDefaultRadioBtns();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Initialize variables to hold saved info from a ApptDataEntryPage when a user jumps to the add/modify customer screen
    static Appointment savedAppt;
    static boolean isNewAppt;

    //Create getter and setter methods for both variables
    public void setSavedAppt(Appointment appt) {
        savedAppt = appt;
    }
    public Appointment getSavedAppt() {
        return savedAppt;
    }

    public void setIsNewAppt(boolean isNew) {
        isNewAppt = isNew;
    }
    public boolean getIsNewAppt() {
        return isNewAppt;
    }

    //When the Logout button is clicked, close the stage then the program
    public void logoutBtnAction (ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    //When the Back button is clicked, return the the MainPage
    public void backBtnAction (ActionEvent event) {
        changeToMainPage(event);
    }


    @FXML
    public void initialize() {

    }

}