package SchedulingSoftware;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class ApptSchedulingController {

    //Create references to GUI objects on page
    @FXML
    TableView<Appointment> apptTableView;
    @FXML
    TableColumn<Appointment, String> apptCxCol;
    @FXML
    TableColumn<Appointment, String> apptTypeCol;
    @FXML
    TableColumn<Appointment, String> apptAgentCol;
    @FXML
    TableColumn<Appointment, String> apptStartCol;
    @FXML
    TableColumn<Appointment, String> apptEndCol;
    @FXML
    RadioButton yourApptsRB;
    @FXML
    RadioButton allApptsRB;
    @FXML
    RadioButton upcomingApptsRB;
    @FXML
    RadioButton allTimeApptsRB;

    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Create a boolean to pass to the ApptDataEntryPage for add new or modifying appointment
    boolean isNew;

    //Create variable to hold the appointment currently selected in the TableView to be passed to the ApptDataEntryPage
    Appointment selectedAppt = null;

    //Create method to build an ObservableList of customers to display
    public void populateApptTable(String user, boolean onlyUsersAppts, boolean futureOnly) {
        ObservableList<Appointment> apptList = dbConnect.buildAppointmentList(user, controllerReference.getTimeZoneId(), onlyUsersAppts, futureOnly);
        //Set the appointment list to the apptTableView
        apptTableView.setItems(apptList);
    }

    //Create a method to set the default radio button selections
    public void setDefaultRadioBtns() {
        yourApptsRB.setSelected(true);
        upcomingApptsRB.setSelected(true);
    }

    //Create logic for the Refresh button; call the method to populate the table and pass in appropriate booleans
    public void refreshBtnAction (ActionEvent event) {
        //Initialize two booleans to hold results of checks
        boolean onlyUserAppts;
        boolean futureOnly;
        //There are four possible radio button combinations, determine which is correct and set booleans accordingly
        if (yourApptsRB.isSelected() && upcomingApptsRB.isSelected()) {
            onlyUserAppts = true;
            futureOnly = true;
        } else if (allApptsRB.isSelected() && upcomingApptsRB.isSelected()) {
            onlyUserAppts = false;
            futureOnly = true;
        } else if (yourApptsRB.isSelected() && allTimeApptsRB.isSelected()) {
            onlyUserAppts = true;
            futureOnly = false;
        } else {
            onlyUserAppts = false;
            futureOnly = false;
        }
        //Clear all current table data
        apptTableView.getItems().clear();
        //Call the populate method with the new variables
        populateApptTable(controllerReference.getUserLoggedIn(),onlyUserAppts,futureOnly);
    }

    //Create logic for the More Info button
    public void moreInfoBtnAction(ActionEvent event) {
        ///Verify an appointment is selected
        selectedAppt = apptTableView.getSelectionModel().getSelectedItem();
        if (selectedAppt == null) {
            generateErrorMsg();
        } else {
            //Display an information message with additional appointment fields that aren't displayed in the Tableview due to space limitations
            Alert moreInfo = new Alert(Alert.AlertType.INFORMATION);
            moreInfo.setTitle("More Information About The Selected Appointment");
            moreInfo.setHeaderText(null);
            moreInfo.setGraphic(null);
            moreInfo.setContentText("Details about the " + selectedAppt.getTitle() + " appointment for " + selectedAppt.fullCustomerProperty().get() + ":\n\n" +
                "Description:   " + selectedAppt.getDescription() + "\n\n" +
                "Location:      " + selectedAppt.getLocation() + "\n\n" +
                "URL:           " + selectedAppt.getUrl() + "\n");
            moreInfo.getDialogPane().setMinWidth(800);
            moreInfo.showAndWait();
        }
    }

    //Create logic for the Add New button, set isNew flag to true then call method to load CxDataEntryPage
    public void apptAddBtnAction(ActionEvent event) {
        isNew = true;
        changeToApptDataPage(event, selectedAppt);
    }

    //Create logic for the Modify button
    public void apptModBtnAction(ActionEvent event) {
        ///Verify an appointment is selected
        selectedAppt = apptTableView.getSelectionModel().getSelectedItem();
        if (selectedAppt == null) {
            generateErrorMsg();
        } else {
            //Set isNew to false and call the method to change to the Data Entry Page
            isNew = false;
            changeToApptDataPage(event, selectedAppt);
        }
    }

    //Create logic for the Delete button
    public void apptDelBtnAction(ActionEvent event) {
        //Verify an appointment is selected
        selectedAppt = apptTableView.getSelectionModel().getSelectedItem();
        if (selectedAppt == null) {
            generateErrorMsg();
        } else {
            //Display a confirmation dialog to make sure the user really wants to delete this appointment
            Alert apptAlert = new Alert(Alert.AlertType.CONFIRMATION);
            apptAlert.setTitle("Confirm Deletion");
            apptAlert.setHeaderText(null);
            apptAlert.setGraphic(null);
            apptAlert.setContentText("Are you sure you want to delete this appointment for customer #" + selectedAppt.fullCustomerProperty().get() +
                    "?\n" + "This action cannot be reversed.");
            apptAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    //Prepare a delete statement for the appointment table and execute it
                    String deleteAppt = "DELETE FROM appointment WHERE appointmentId = '" + selectedAppt.getAppointmentID() + "';";
                    dbConnect.executeUpdate(deleteAppt);
                    //Now that the appointment has been deleted, also delete any reminders for it
                    String deleteReminders = "DELETE FROM reminder WHERE appointmentId = '" + selectedAppt.getAppointmentID() + "';";
                    dbConnect.executeUpdate(deleteReminders);
                    //Deletion complete, fire the Refresh button action to reload the page with the currently selected filters
                    refreshBtnAction(event);
                }
            });
        }
    }

    //Call the Controller method to change to the CustomerMaintenancePage
    public void cxMaintBtnAction(ActionEvent event) {
        controllerReference.changeToCxMaintPage(event);
    }

    //Create logic to load ApptDataEntryPage when passed an event, determine menu label to display based on variable set
    public void changeToApptDataPage(ActionEvent event, Appointment appt) {
        try {
            FXMLLoader apptDataLoader = new FXMLLoader(getClass().getResource("ApptDataEntryPage.fxml"));
            Parent apptData = apptDataLoader.load();
            Scene apptDataScene = new Scene(apptData, 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(apptDataScene);
            //Get a reference to the ApptDataEntryController
            ApptDataEntryController apptDataController = apptDataLoader.getController();
            //Set the appropriate header for adding a new customer or modifying an existing one
            if (isNew) {
                apptDataController.setAddApptLbl();
                apptDataController.generateNewApptID();
                apptDataController.setAgentField(controllerReference.getUserLoggedIn());
            } else {
                apptDataController.setModApptLbl();
                apptDataController.setModAppt(appt);
                apptDataController.loadAppointmentData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Call the Logout button method from the Controller
    public void logoutBtnAction(ActionEvent event) {
        controllerReference.logoutBtnAction(event);
    }

    //Call the Back button method from the Controller
    public void backBtnAction(ActionEvent event) {
        controllerReference.backBtnAction(event);
    }

    //Method to display an error message if no customer is selected when attempting to modify or delete
    public void generateErrorMsg () {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Appointment Selected");
        alert.setHeaderText(null);
        alert.setContentText("You must select an appointment in the table before performing that action.");
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        apptCxCol.setCellValueFactory(cellData -> cellData.getValue().fullCustomerProperty());
        apptTypeCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        apptAgentCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        apptStartCol.setCellValueFactory(cellData -> cellData.getValue().startProperty());
        apptEndCol.setCellValueFactory(cellData -> cellData.getValue().endProperty());
    }
}
