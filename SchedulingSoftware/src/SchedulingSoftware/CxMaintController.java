package SchedulingSoftware;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.Optional;

public class CxMaintController {

    //Create references to GUI objects on page
    @FXML
    TableView<Customer> cxTableView;
    @FXML
    TableColumn<Customer, Integer> cxIDCol;
    @FXML
    TableColumn<Customer, String> cxNameCol;
    @FXML
    TableColumn<Customer, String> cxPhoneCol;
    @FXML
    TableColumn<Customer, String> cxAddressCol;
    @FXML
    TableColumn<Customer, String> cxCountryCol;
    @FXML
    TableColumn<Customer, String> cxActiveCol;


    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Create a boolean to pass to the CxDataEntryPage for add new or modifying customer
    boolean isNew;

    //Create variable to hold the customer currently selected in the TableView to be passed to the cxDataEntryPage
    Customer selectedCx = null;

    //Create method to build an ObservableList of customers to display
    public void populateCxTable() {
        ObservableList<Customer> cxList = dbConnect.buildCustomerList();
        //Set the customer list to the cxTableView
        cxTableView.setItems(cxList);
    }

    //Create logic for the Add New button, set isNew flag to true then call method to load CxDataEntryPage
    public void cxAddBtnAction(ActionEvent event) {
        isNew = true;
        changeToCxDataPage(event, selectedCx);
    }

    //Create logic for the Modify button
    public void cxModBtnAction(ActionEvent event) {
        ///Verify a customer is selected
        selectedCx = cxTableView.getSelectionModel().getSelectedItem();
        if (selectedCx == null) {
            generateErrorMsg();
        } else {
            //Set isNew to false and call the method to change to the Data Entry Page
            isNew = false;
            changeToCxDataPage(event, selectedCx);
        }
    }

    //Create logic for the Delete button
    public void cxDelBtnAction(ActionEvent event) {
        //Verify a customer is selected
        selectedCx = cxTableView.getSelectionModel().getSelectedItem();
        if (selectedCx == null) {
            generateErrorMsg();
        } else {
            //Display a confirmation dialog to make sure the user really wants to delete this customer
            Alert cxAlert = new Alert(Alert.AlertType.CONFIRMATION);
            cxAlert.setTitle("Confirm Deletion");
            cxAlert.setHeaderText(null);
            cxAlert.setGraphic(null);
            cxAlert.setContentText("Are you sure you want to delete customer #" + selectedCx.getCustomerID() + " - " +
                            selectedCx.getCustomerName() + "?\n" + "This action cannot be reversed.");
            cxAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    //Initialize a boolean to hold whether or not the city can be deleted
                    boolean canDeleteCity = false;
                    //First, we need to see if this address is linked to any other customer records, if it is, it shouldn't be deleted
                    String addressQuery = "SELECT count(*) FROM customer where addressID = '" + selectedCx.getAddressID() + "';";
                    boolean canDeleteAddress = checkForLinks(addressQuery);
                    if (canDeleteAddress) {
                        //If we can delete the address, check to see if we can delete the city too
                        String cityQuery = "SELECT count(*) FROM address where cityID = '" + selectedCx.getCityID() + "';";
                        canDeleteCity = checkForLinks(cityQuery);
                        //Got the city info, now process the address deletion
                        String deleteAddress = "DELETE FROM address WHERE addressId = '" + selectedCx.getAddressID() + "';";
                        dbConnect.executeUpdate(deleteAddress);
                    }
                    if (canDeleteCity) {
                        //If we can delete the city, check to see if we can delete the country too
                        String countryQuery = "SELECT count(*) FROM city where countryID = '" + selectedCx.getCountryID() + "';";
                        boolean canDeleteCountry = checkForLinks(countryQuery);
                        //If canDeleteCountry is true, process the country deletion
                        if (canDeleteCountry) {
                            String deleteCountry = "DELETE FROM country WHERE countryId = '" + selectedCx.getCountryID() + "';";
                            dbConnect.executeUpdate(deleteCountry);
                        }
                        //Finished with the country, now process the city deletion
                        String deleteCity = "DELETE FROM city WHERE cityId = '" + selectedCx.getCityID() + "';";
                        dbConnect.executeUpdate(deleteCity);
                    }
                    //Now that address, city, and country have been checked, delete the customer record from the DB
                    String deleteCustomer = "DELETE FROM customer WHERE customerID = '" + selectedCx.getCustomerID() + "';";
                    dbConnect.executeUpdate(deleteCustomer);
                    //Deletion complete, re-load the page to reflect the change in the displayed table
                    controllerReference.changeToCxMaintPage(event);
                }
            });
        }
    }

    //Create logic to load CxDataEntryPage when passed an event, determine menu label to display based on variable set
    public void changeToCxDataPage(ActionEvent event, Customer cx) {
        try {
            FXMLLoader cxDataLoader = new FXMLLoader(getClass().getResource("CxDataEntryPage.fxml"));
            Parent cxData = cxDataLoader.load();
            Scene cxDataScene = new Scene(cxData, 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(cxDataScene);
            //Get a reference to the CxDataEntryController
            CxDataEntryController cxDataController = cxDataLoader.getController();
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
        alert.setTitle("No Customer Selected");
        alert.setHeaderText(null);
        alert.setContentText("You must select a customer in the table before performing that action.");
        alert.showAndWait();
    }

    //Method to check whether an address, city, or country is linked to more than one customer in the DB
    public boolean checkForLinks(String query) {
        //Initialize a boolean to hold the result of the check
        boolean canDelete = false;
        //Initialize a ResultSet and execute the query using the passed statement
        ResultSet rs = dbConnect.executeQuery(query);
        try {
            while (rs.next()) {
                if (rs.getInt(1) > 1) {
                    canDelete = false;
                } else {
                    canDelete = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return canDelete;
    }

    @FXML
    public void initialize() {
        cxIDCol.setCellValueFactory(cellData -> cellData.getValue().customerIDProperty().asObject());
        cxNameCol.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        cxPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        cxAddressCol.setCellValueFactory(cellData -> cellData.getValue().fullAddressProperty());
        cxCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        cxActiveCol.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
    }
}
