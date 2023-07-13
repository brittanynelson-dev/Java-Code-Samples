package SchedulingSoftware;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.ZoneOffset.UTC;

import java.util.*;


public class ApptDataEntryController {

    //Create references to GUI objects on page
    @FXML
    Label addApptLbl;
    @FXML
    Label modApptLbl;
    @FXML
    ComboBox apptCxCB;
    @FXML
    ComboBox apptTypeCB;
    @FXML
    ComboBox apptAgentCB;
    @FXML
    TextArea apptDescTA;
    @FXML
    TextField apptLocTF;
    @FXML
    TextField apptUrlTF;
    @FXML
    DatePicker apptDatePicker;
    @FXML
    ComboBox apptStartHourCB;
    @FXML
    ComboBox apptStartMinCB;
    @FXML
    ComboBox apptEndHourCB;
    @FXML
    ComboBox apptEndMinCB;


    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Create variables to hold info entered on this page
    int appointmentID;
    String apptCustomer;
    int customerID;
    String apptType;
    String apptAgent;
    String apptDesc;
    String apptLoc;
    String apptUrl;
    String apptDate;
    String apptStartHour;
    String apptStartMin;
    String apptEndHour;
    String apptEndMin;
    ZonedDateTime fullStart;
    ZonedDateTime fullEnd;
    int reminderID;

    //Initialize a boolean to be set if the user decides to jump to the Customer Data Entry page
    boolean isNew;

    //Initialize a variable to hold the appointment being modified if the user goes into the Modify screen
    Appointment modAppt;

    //Create method to set the modAppt
    public void setModAppt(Appointment appt) {
        modAppt = appt;
    }

    //Create a method to set the default Agent/Contact field to the user currently logged in for new appointments
    public void setAgentField(String user) {
        apptAgentCB.setValue(user);
    }

    //Create method to prepopulate textfield data with info pulled from DB for an appointment
    public void loadAppointmentData() {
        appointmentID = modAppt.getAppointmentID();
        apptTypeCB.setValue(modAppt.getTitle());
        apptAgentCB.setValue(modAppt.getContact());
        apptDescTA.setText(modAppt.getDescription());
        apptLocTF.setText(modAppt.getLocation());
        apptUrlTF.setText(modAppt.getUrl());
        //If this appointment has saved date, we will need to load it differently than regular data
        if (modAppt.getHasSavedData()) {
            apptCxCB.setValue(modAppt.getSavedCx());
            if (!modAppt.getSavedDate().isEmpty()) {
                apptDatePicker.setValue(LocalDate.parse(modAppt.getSavedDate()));
            }
            apptStartHourCB.setValue(modAppt.getSavedStartHour());
            apptStartMinCB.setValue(modAppt.getSavedStartMin());
            apptEndHourCB.setValue(modAppt.getSavedEndHour());
            apptEndMinCB.setValue(modAppt.getSavedEndMin());
        } else {
            apptCxCB.setValue(dbConnect.getApptCxName(modAppt.getCustomerId()));
            //Get the date value from Start, date will always be the same for start and end due to business hours rule
            if (modAppt.getStart() != null) {
                apptDatePicker.setValue(modAppt.getStart().toLocalDate());
                //Pull hour and minute ints from Start, convert to a String and format to 2 digits
                apptStartHourCB.setValue(String.format("%02d",modAppt.getStart().getHour()));
                apptStartMinCB.setValue(String.format("%02d",modAppt.getStart().getMinute()));
            }
            if (modAppt.getEnd() != null) {
                //Pull hour and minute ints from End, convert to a String and format to 2 digits
                apptEndHourCB.setValue(String.format("%02d",modAppt.getEnd().getHour()));
                apptEndMinCB.setValue(String.format("%02d",modAppt.getEnd().getMinute()));
            }
        }
    }

    //Get all information entered on page, remove any problematic special characters from textfields as they will cause issues writing to db
    public void getFormInfo() {
        //Get all items selected from drop downs and entered in text fields
        apptCustomer = (String) apptCxCB.getSelectionModel().getSelectedItem();
        //If a valid selection was made, parse the customer ID from it
        if (apptCustomer != null && !apptCustomer.isEmpty()) {
            customerID = Integer.parseInt(apptCustomer.substring(0,apptCustomer.indexOf(" ")));
        } else {
            customerID = 0;
        }
        apptType = (String) apptTypeCB.getSelectionModel().getSelectedItem();
        apptAgent = (String) apptAgentCB.getSelectionModel().getSelectedItem();
        apptDesc = apptDescTA.getText().replaceAll("['\";]","");
        apptLoc = apptLocTF.getText().replaceAll("['\";]","");
        apptUrl = apptUrlTF.getText().replaceAll("['\";]","");
        //If no date was chosen set apptDate to an empty string, otherwise parse a string from the date chosen
        if (apptDatePicker.getValue() == null) {
            apptDate = "";
        } else {
            apptDate = apptDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        apptStartHour = (String) apptStartHourCB.getSelectionModel().getSelectedItem();
        apptStartMin = (String) apptStartMinCB.getSelectionModel().getSelectedItem();
        apptEndHour = (String) apptEndHourCB.getSelectionModel().getSelectedItem();
        apptEndMin = (String) apptEndMinCB.getSelectionModel().getSelectedItem();
        //Create ZonedDateTime objects for the start and end variables if valid data is entered
        String datePattern = "yyyy-MM-dd HH:mm VV";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
        if (!apptDate.isEmpty() && apptStartHour != null && apptStartMin != null) {
            fullStart = ZonedDateTime.parse(apptDate + " " + apptStartHour + ":" + apptStartMin + " " + controllerReference.getTimeZoneId(),dtf);
        }
        if (!apptDate.isEmpty() && apptEndHour != null && apptEndMin != null) {
            fullEnd = ZonedDateTime.parse(apptDate + " " + apptEndHour + ":" + apptEndMin + " " + controllerReference.getTimeZoneId(), dtf);
        }
    }

    //Save all current form data into a static Appointment object held by the primary Controller to save it while moving to the CxDataEntryPage and back
    public void saveFormInfo() {
        //Initialize a new Appointment object to hold data
        Appointment savedAppt = new Appointment();
        //Write form data to savedAppointment
        getFormInfo();
        //Write the saved form info into the savedAppt, we don't need to check validity at this point, just save what's there
        savedAppt.setSavedStrings(apptCustomer,apptDate,apptStartHour,apptStartMin,apptEndHour,apptEndMin);
        savedAppt.setAppointmentId(appointmentID);
        savedAppt.setTitle(apptType);
        savedAppt.setContact(apptAgent);
        savedAppt.setDescription(apptDesc);
        savedAppt.setLocation(apptLoc);
        savedAppt.setUrl(apptUrl);
        //Set the savedAppointment object and isNewAppt boolean on Controller
        controllerReference.setSavedAppt(savedAppt);
        if (addApptLbl.isVisible()) {
            controllerReference.setIsNewAppt(true);
        } else {
            controllerReference.setIsNewAppt(false);
        }
        //Set the hasSavedData flag to true
        savedAppt.setHasSavedData(true);
    }

    //Call the Logout button method from the Controller
    public void logoutBtnAction(ActionEvent event) {
        controllerReference.logoutBtnAction(event);
    }

    //Create logic for the Back button on the CxDataEntryPage
    public void apptDataBackBtnAction(ActionEvent event) {
        //First, get the data entered in the fields and ComboBoxes
        getFormInfo();
        //Check to see if the fields are empty. If they are, call the changeToAppointmentSchedPage method from Controller
        if (apptCustomer==null && apptType==null && (apptAgent==null || apptAgent.equals(controllerReference.getUserLoggedIn())) &&
                apptDesc.isEmpty() && apptLoc.isEmpty() && apptUrl.isEmpty() && apptDate.isEmpty() && apptStartHour==null &&
                apptStartMin==null && apptEndHour==null && apptEndMin==null) {
            controllerReference.changeToApptSchedulingPage(event);
        //Check to see if this is the same data currently entered in the db for this appointment - no changes made
        } else if (modApptLbl.isVisible() && customerID==modAppt.getCustomerId() && apptType.equals(modAppt.getTitle()) &&
                apptAgent.equals(modAppt.getContact()) && apptDesc.equals(modAppt.getDescription()) && apptLoc.equals(modAppt.getLocation()) &&
                apptUrl.equals(modAppt.getUrl()) && fullStart.equals(modAppt.getStart()) && fullEnd.equals(modAppt.getEnd())){
            controllerReference.changeToApptSchedulingPage(event);
        } else {
            //If there is anything new entered in textfields, display an alert to have user confirm they want to discard all data on page
            Alert apptAlert = new Alert(Alert.AlertType.CONFIRMATION);
            apptAlert.setTitle("Warning!");
            apptAlert.setHeaderText(null);
            apptAlert.setGraphic(null);
            apptAlert.setContentText("Data has not been saved. Do you wish to discard your changes and return to the Appointment Scheduling screen?");
            apptAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    controllerReference.changeToApptSchedulingPage(event);
                }
            });
        }
    }

    //Create logic to jump to the CxDataEntryPage with the New Customer button
    public void newCxBtnAction(ActionEvent event) {
        //Set the isNew flag to true
        isNew = true;
        //Save the current form info so no data will be lost
        saveFormInfo();
        //Create a null cx to pass then call the method to change to the CxDataEntryPage
        Customer cx = null;
        changeToCxDataEntryPage(event,cx);
    }

    //Create logic to jump to the CxDataEntryPage with the New Customer button
    public void modCxBtnAction(ActionEvent event) {
        //If a customer is selected, proceed. Otherwise, display an error message
        if (apptCxCB.getSelectionModel().getSelectedItem() == null) {
            String cxAlert = "You must select a customer from the dropdown to modify first.";
            generateAlert(cxAlert);
            return;
        }
        //Set the isNew flag to false
        isNew = false;
        //Save the current form info so no data will be lost
        saveFormInfo();
        //Build the selected customer object to pass to the cx mod page
        Customer selectedCx = dbConnect.getCustomerFromID(customerID);
        changeToCxDataEntryPage(event,selectedCx);
    }

    //Create logic to load the customer's address on file into the location box
    public void useCxAddressBtnAction(ActionEvent event) {
        //If a customer is selected, proceed. Otherwise, display an error message
        if (apptCxCB.getSelectionModel().getSelectedItem() == null) {
            String cxAlert = "You must select a customer from the dropdown first to use their address.";
            generateAlert(cxAlert);
            return;
        }
        //Parse the cxID from the apptCxCB field
        customerID = Integer.parseInt(((String) apptCxCB.getSelectionModel().getSelectedItem()).substring(0,
                ((String) apptCxCB.getSelectionModel().getSelectedItem()).indexOf(" ")));
        //Get the customer using the parsed ID
        Customer cx = dbConnect.getCustomerFromID(customerID);
        //Set the Location field to the customer address
        apptLocTF.setText(cx.fullAddressProperty().get() + ", " + cx.getCountry());
    }

    //Create logic for the Submit button on the CxDataEntryPage, collect all entered info, verify correct format, then write to DB
    public void submitBtnAction(ActionEvent event) {
        //Get all currently entered data
        getFormInfo();
        //Verify a customer has been selected
        if (apptCustomer==null) {
            String cxAlert = "You must select a customer from the dropdown box to create an appointment for. If this appointment is for a new customer, " +
                    "please click the New Customer button to go to the Customer Maintenance screen.";
            generateAlert(cxAlert);
            return;
        }
        //Very an appointment type has been selected
        if (apptType==null) {
            String typeAlert = "You must select an appointment type from the dropdown. If your appointment doesn't fit into one of the designated types, " +
                    "please select 'Other Appointment'.";
            generateAlert(typeAlert);
            return;
        }
        //Verify a user has been selected for the contact field
        if (apptAgent==null) {
            String agentAlert = "You must select the agent that will be attending this appointment with the customer from the dropdown.";
            generateAlert(agentAlert);
            return;
        }
        //Verify a location has been entered
        if (apptLoc.isEmpty()) {
            String locAlert = "You must enter the location for the appointment. If the meeting will be held via telephone or internet, " +
                    "you should enter 'Virtual' for the location.";
            generateAlert(locAlert);
            return;
        }
        //Verify if a URL is entered it is 255 characters or less as required by DB
        if (apptUrl.length() > 255) {
            String urlAlert = "The URL field is limited to 255 characters. If you need to enter a longer URL for your meeting, it is recommended " +
                    "to enter it in the description field instead";
            generateAlert(urlAlert);
            return;
        }
        //Description field is not required, but advisable to enter something here. Display confirmation to verify user wants to leave it blank
        if (apptDesc.isEmpty()) {
            Alert descWarning = new Alert(Alert.AlertType.CONFIRMATION,"An appointment description is not required, but it is recommended " +
                    "to enter one. Do you want to proceed anyway?");
            descWarning.setTitle("Warning!");
            descWarning.setHeaderText(null);
            descWarning.setGraphic(null);
            //If user clicks Cancel or closes the window, stop here. Otherwise, proceed to the next check.
            Optional<ButtonType> result = descWarning.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.YES) {
                return;
            }
        }
        //Verify a date has been selected and is valid
        if (apptDate.isEmpty()) {
            String dateAlert = "You must select the date this appointment will be held on.";
            generateAlert(dateAlert);
            return;
        } else if (apptDatePicker.getValue().getDayOfWeek().equals(SUNDAY)) {
            //Assuming company is only open Monday-Saturday, display an error if the appointment is scheduled for a Sunday
            String dayAlert = "Current business rules do not allow agents to schedule appointments on Sundays, please select a different date " +
                    "for your appointment.";
            generateAlert(dayAlert);
            return;
        }
        //Verify a Start hour and minute have been selected and are valid
        String startTimeAlert = "You must select a valid start time between the hours of 9:00AM and 4:59PM";
        if (apptStartHour==null || apptStartMin==null) {
            generateAlert(startTimeAlert);
            return;
        } else if (Integer.parseInt(apptStartHour) < 9 || Integer.parseInt(apptStartHour) > 16) {
            generateAlert(startTimeAlert);
            return;
        }
        //Verify an End hour and minute have been selected and are valid
        String endTimeAlert = "You must select a valid end time between the hours of 9:01AM and 5:00PM";
        if (apptEndHour==null || apptEndMin==null) {
            generateAlert(endTimeAlert);
            return;
        } else if (Integer.parseInt(apptEndHour) < 9 || Integer.parseInt(apptEndHour) > 17) {
            generateAlert(endTimeAlert);
            return;
        }
        //Verify start time is before end time and appointment is at least 1 minute long
        if (ChronoUnit.MINUTES.between(fullStart,fullEnd) < 1) {
            String timeAlert = "An appointment must be at least 1 minute long and the start time must be earlier than the end time.";
            generateAlert(timeAlert);
            return;
        }
        //Verify the customer is currently active in the DB
        Customer cx = dbConnect.getCustomerFromID(customerID);
        if (cx.getActive().equals("No")) {
            String activeAlert = "The customer you have selected is currently inactive. Please modify the customer to reflect that they are now in an " +
                    "active status before scheduling a new appointment for them.";
            generateAlert(activeAlert);
            return;
        }
        //If the appointment is being scheduled for the past, display a warning but allow the user to proceed if they choose to
        if(fullStart.isBefore(ZonedDateTime.now())) {
            Alert timeWarning = new Alert(Alert.AlertType.CONFIRMATION,"The appointment you scheduled appears to be in the past. " +
                    "Do you want to proceed anyway?");
            timeWarning.setTitle("Warning!");
            timeWarning.setHeaderText(null);
            timeWarning.setGraphic(null);
            timeWarning.setResult(ButtonType.CANCEL);
            //If user clicks Cancel or closes the window, stop here. Otherwise, proceed to the next check.
            Optional<ButtonType> result = timeWarning.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return;
            }
        }

        //Get the username of the logged in user to pass to insert/update statements and final check
        String user = controllerReference.getUserLoggedIn();

        //Verify the user does not already have an appointment scheduled for this time period
        boolean isOverlapping = dbConnect.checkForOverlaps(user, fullStart, fullEnd, appointmentID);
        if (isOverlapping) {
            String overlapAlert = "The appointment time you selected conflicts with another appointment on your schedule. Please select a different time " +
                    "or modify/delete the other appointment first if it is invalid.";
            generateAlert(overlapAlert);
            return;
        }

        //All entered data has been validated, time to write to the DB.
        //First, convert the start and end ZonedDateTimes to properly formatted Strings
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedStart = dtf.format(fullStart.withZoneSameInstant(UTC));
        String formattedEnd = dtf.format(fullEnd.withZoneSameInstant(UTC));

        //Determine if this appointment is new or if it already exists and just needs to be updated
        if (addApptLbl.isVisible()) {
            //Prepare the appointment insert statement, since we're on the New Appointment screen, this will always be a new insert
            String apptInsert = "INSERT INTO appointment VALUES ('" + appointmentID + "','" + customerID + "','" + apptType + "','" +
                    apptDesc + "','" + apptLoc + "','" + apptAgent + "','" + apptUrl + "','" + formattedStart + "','"
                    + formattedEnd + "',NOW(),'" + user + "',NOW(),'" + user + "');";
            //Execute the insert to add the new appointment to the DB
            dbConnect.executeUpdate(apptInsert);
            //Generate a new reminderId, all appointments will automatically have a 15 minute reminder created
            generateNewReminderID();
            //Calculate the correct time for the reminder (15 minutes before appointment start time)
            ZonedDateTime reminderTime = fullStart.minusMinutes(15);
            String formattedReminderTime = dtf.format(reminderTime.withZoneSameInstant(UTC));
            //Prepare a reminder insert statement
            String reminderInsert = "INSERT INTO reminder VALUES ('" + reminderID + "','" + formattedReminderTime +
                    "','15','1','" + appointmentID + "','" + user + "',NOW(),'" + apptType + "');";
            dbConnect.executeUpdate(reminderInsert);

            //All done, clear all fields to prevent confusion
            clearBtnAction(event);
            generateNewApptID();
            //Initialize a String to pass to a confirmation message
            String confirmModAppt = apptType + " for customer " + apptCustomer + " has been added to the database successfully.";
            generateInfoDialog(confirmModAppt);

        //If this is a modification instead of a new appointment, it needs to be updated instead of inserted
        } else {
            //Initialize some Strings to hold portions of the update statement to ensure only necessary fields are updated
            String updateCxId;
            String updateTitle;
            String updateDesc;
            String updateLoc;
            String updateContact;
            String updateUrl;
            String updateStart;
            String updateEnd;

            //Check if fields have been updated and set statement components appropriately, also update the modAppt object to track changes after submit
            if (customerID==modAppt.getCustomerId()) {
                updateCxId = "";
            } else {
                updateCxId = "customerId = '" + customerID + "', ";
                modAppt.setCustomerId(customerID);
            }
            if (apptType.equals(modAppt.getTitle())) {
                updateTitle = "";
            } else {
                updateTitle = "title = '" + apptType + "', ";
                modAppt.setTitle(apptType);
            }
            if (apptDesc.equals(modAppt.getDescription())) {
                updateDesc = "";
            } else {
                updateDesc = "description = '" + apptDesc + "', ";
                modAppt.setDescription(apptDesc);
            }
            if (apptLoc.equals(modAppt.getLocation())) {
                updateLoc = "";
            } else {
                updateLoc = "location = '" + apptLoc + "', ";
                modAppt.setLocation(apptLoc);
            }
            if (apptAgent.equals(modAppt.getContact())) {
                updateContact = "";
            } else {
                updateContact = "contact = '" + apptAgent + "', ";
                modAppt.setContact(apptAgent);
            }
            if (apptUrl.equals(modAppt.getUrl())) {
                updateUrl = "";
            } else {
                updateUrl = "url = '" + apptUrl + "', ";
                modAppt.setUrl(apptUrl);
            }
            //Determine if the time has been changed, if it hasn't we don't need to worry about changing the reminder
            if (!fullStart.equals(modAppt.getStart()) || !fullEnd.equals(modAppt.getEnd())) {
                //Delete any existing reminders for this appointment
                String deleteReminders = "DELETE FROM reminder WHERE appointmentId = '" + appointmentID + "';";
                dbConnect.executeUpdate(deleteReminders);
                //Create a new reminder with the updated time and current appointment type
                generateNewReminderID();
                //Calculate the correct time for the reminder (15 minutes before appointment start time)
                ZonedDateTime reminderTime = fullStart.minusMinutes(15);
                String formattedReminderTime = dtf.format(reminderTime.withZoneSameInstant(UTC));
                //Prepare a reminder insert statement
                String reminderInsert = "INSERT INTO reminder VALUES ('" + reminderID + "','" + formattedReminderTime +
                        "','15','1','" + appointmentID + "','" + user + "',NOW(),'" + apptType + "');";
                dbConnect.executeUpdate(reminderInsert);
                //Set the datetime update Strings to update the record with new data
                updateStart = "start = '" + formattedStart + "', ";
                updateEnd = "end = '" + formattedEnd + "', ";
                //Update the appointment object as well
                modAppt.setStart(fullStart);
                modAppt.setEnd(fullEnd);
            } else {
                //Set the datetime update Strings to empty
                updateStart = "";
                updateEnd = "";
            }
            //Initialize a String to hold the full update statement and build it from the components
            String apptUpdate = "UPDATE appointment SET " + updateCxId + updateTitle + updateDesc + updateLoc + updateContact + updateUrl +
                    updateStart + updateEnd + "lastUpdate = NOW(), lastUpdateBy = '" + user + "' WHERE appointmentId = '" + appointmentID + "';";
            dbConnect.executeUpdate(apptUpdate);

            //All done, display a confirmation message that the has been updated successfully
            String confirmModAppt = apptType + " for customer " + apptCustomer + " has been updated successfully.";
            generateInfoDialog(confirmModAppt);
        }
    }

    //Clear all data entered on page
    public void clearBtnAction(ActionEvent event) {
        //If this is a new appointment, clear everything. If it is existing, set back to what was there.
        if (addApptLbl.isVisible()) {
            apptCxCB.getSelectionModel().clearSelection();
            apptTypeCB.getSelectionModel().clearSelection();
            apptAgentCB.getSelectionModel().clearSelection();
            apptDescTA.clear();
            apptLocTF.clear();
            apptUrlTF.clear();
            apptDatePicker.setValue(null);
            apptStartHourCB.getSelectionModel().clearSelection();
            apptStartMinCB.getSelectionModel().clearSelection();
            apptEndHourCB.getSelectionModel().clearSelection();
            apptEndMinCB.getSelectionModel().clearSelection();
        } else {
            //Set back to what's currently in the DB
            loadAppointmentData();
        }
    }

    //Activate the Add New Customer Label
    public void setAddApptLbl() {
        addApptLbl.setVisible(true);
    }

    //Activate the Modify Customer Label
    public void setModApptLbl() {
        modApptLbl.setVisible(true);
    }

    //Create method to generate an Error alert when info is blank or not filled out correctly for a customer
    public void generateAlert (String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR,text);
        alert.setTitle("Error Processing Request");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //Create method to generate a success popup to confirm a requested action was completed
    public void generateInfoDialog (String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,text);
        alert.setTitle("Request Processed Successfully");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //Create method to generate the next unused number for a new appointmentId
    public void generateNewApptID() {
        //Initialize an int to hold the result
        int apptID = 0;
        //Initialize String to hold query to get the current highest appointmentId in DB
        String apptIDQuery = "SELECT MAX(appointmentId) from appointment;";
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(apptIDQuery);
        //Get the result and add 1 for the next appointment ID number
        try {
            while (rs.next()) {
                apptID = rs.getInt("MAX(appointmentId)") + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        appointmentID = apptID;
    }

    //Create method to generate the next unused number for a new reminderId
    public void generateNewReminderID() {
        //Initialize an int to hold the result
        int remID = 0;
        //Initialize String to hold query to get the current highest appointmentId in DB
        String remIDQuery = "SELECT MAX(reminderId) from reminder;";
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(remIDQuery);
        //Get the result and add 1 for the next customer ID number
        try {
            while (rs.next()) {
                remID = rs.getInt("MAX(reminderId)") + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reminderID = remID;
    }

    //Create method to save the current form info then load the CxDataEntryPage
    public void changeToCxDataEntryPage(ActionEvent event, Customer cx) {
        try {
            FXMLLoader cxDataLoader = new FXMLLoader(getClass().getResource("CxDataEntryPage.fxml"));
            Parent cxData = cxDataLoader.load();
            Scene cxDataScene = new Scene(cxData, 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(cxDataScene);
            //Get a reference to the CxDataEntryController
            CxDataEntryController cxDataController = cxDataLoader.getController();
            //Activate the appropriate Back button on customer page to be able to return to this page
            cxDataController.switchBackBtns();

            //Set the appropriate header for adding a new customer or modifying an existing one
            if (isNew) {
                cxDataController.setAddCxLbl();
                cxDataController.generateNewCxID();
            } else {
                cxDataController.setModCxLbl();
                cxDataController.setModCustomer(cx);
                cxDataController.loadCustomerData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        //Build a list of customers for the apptCxCB
        apptCxCB.setItems(dbConnect.buildApptCxList());

        //Load valid appointment type options into the Appointment Type ChoiceBox
        apptTypeCB.getItems().clear();
        apptTypeCB.getItems().addAll("Presales Consultation", "Initial Consultation", "Standard Consultation", "Customer Retention",
                "Other Appointment");

        //Build a list of users for the apptAgentCB
        apptAgentCB.setItems(dbConnect.buildUserList());

        //Initialize ObservableLists to hold valid options for the hours and minutes drop down boxes
        ObservableList<String> hours = FXCollections.observableArrayList("00","01","02","03","04","05","06","07","08","09","10","11","12",
                "13","14","15","16","17","18","19","20","21","22","23");
        ObservableList<String> minutes = FXCollections.observableArrayList("00","01","02","03","04","05","06","07","08","09","10","11","12",
                "13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39",
                "40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59");
        //Load valid time options into start and end time comboboxes.
        apptStartHourCB.setItems(hours);
        apptStartMinCB.setItems(minutes);
        apptEndHourCB.setItems(hours);
        apptEndMinCB.setItems(minutes);
    }
}
