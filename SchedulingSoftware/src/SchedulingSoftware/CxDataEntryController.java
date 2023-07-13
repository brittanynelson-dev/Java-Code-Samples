package SchedulingSoftware;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CxDataEntryController {

    //Create references to GUI objects on page
    @FXML
    Label addCxLbl;
    @FXML
    Label modCxLbl;
    @FXML
    TextField cxIDTF;
    @FXML
    TextField cxNameTF;
    @FXML
    TextField cxPhoneTF;
    @FXML
    TextField cxStreetTF;
    @FXML
    TextField cxCityTF;
    @FXML
    TextField cxStateTF;
    @FXML
    TextField cxZipTF;
    @FXML
    ComboBox cxCountryCB;
    @FXML
    ComboBox cxActiveFlagCB;
    @FXML
    Button cxBackBtn;
    @FXML
    Button apptBackBtn;

    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Create variables to hold info entered on this page
    String cxID;
    String cxName;
    String cxPhone;
    String cxStreet;
    String cxCity;
    String cxState;
    String cxZip;
    String cxCountry;
    String cxActiveFlag;

    //Initialize a variable to hold the customer being modified if the user goes into the Modify screen
    Customer modCustomer;

    public void setModCustomer(Customer cx) {
        modCustomer = cx;
    }

    //Create method to prepopulate textfield data with info pulled from DB for a customer
    public void loadCustomerData() {
        cxIDTF.setText(Integer.toString(modCustomer.getCustomerID()));
        cxNameTF.setText(modCustomer.getCustomerName());
        cxPhoneTF.setText(modCustomer.getPhone());
        cxStreetTF.setText(modCustomer.getAddress());
        cxCityTF.setText(modCustomer.getCity());
        cxStateTF.setText(modCustomer.getAddress2());
        cxZipTF.setText(modCustomer.getPostalCode());
        cxCountryCB.setValue(modCustomer.getCountry());
        cxActiveFlagCB.setValue(modCustomer.getActive());
    }


    //Get all information entered on page, remove any problematic special characters from textfields as they will cause issues writing to db
    public void getFormInfo() {
        cxID = cxIDTF.getText();
        cxName = cxNameTF.getText().replaceAll("['\";]","");
        cxPhone = cxPhoneTF.getText().replaceAll("['\";]","");
        cxStreet = cxStreetTF.getText().replaceAll("['\";]","");
        cxCity = cxCityTF.getText().replaceAll("['\";]","");
        cxState = cxStateTF.getText().replaceAll("['\";]","");
        cxZip = cxZipTF.getText().replaceAll("['\";]","");
        cxCountry = (String) cxCountryCB.getSelectionModel().getSelectedItem();
        cxActiveFlag = (String) cxActiveFlagCB.getSelectionModel().getSelectedItem();
    }

    //Call the Logout button method from the Controller
    public void logoutBtnAction(ActionEvent event) {
        controllerReference.logoutBtnAction(event);
    }

    //Create logic for the standard Back button on the CxDataEntryPage
    public void cxDataBackBtnAction(ActionEvent event) {
        //First, get the data entered in the fields and ComboBoxes
        getFormInfo();
        //Check to see if the fields are empty. If they are, call the changeToCxMaintPage method in Controller
        if (cxName.isEmpty() && cxPhone.isEmpty() && cxStreet.isEmpty() && cxCity.isEmpty() && cxState.isEmpty()
                && cxZip.isEmpty() && cxCountry==null && cxActiveFlag==null) {
            controllerReference.changeToCxMaintPage(event);
        //Check to see if this is the same data currently entered in the db for the customer - no changes made
        } else if (modCxLbl.isVisible() && cxName.equals(modCustomer.getCustomerName()) && cxPhone.equals(modCustomer.getPhone()) &&
                cxStreet.equalsIgnoreCase(modCustomer.getAddress()) && cxCity.equalsIgnoreCase(modCustomer.getCity()) &&
                cxState.equalsIgnoreCase(modCustomer.getAddress2()) && cxZip.equalsIgnoreCase(modCustomer.getPostalCode()) &&
                cxCountry.equals(modCustomer.getCountry()) && cxActiveFlag.equals(modCustomer.getActive())) {
            controllerReference.changeToCxMaintPage(event);
        } else {
            //If there is anything new entered in textfields, display an alert to have user confirm they want to discard all data on page
            Alert cxAlert = new Alert(Alert.AlertType.CONFIRMATION);
            cxAlert.setTitle("Warning!");
            cxAlert.setHeaderText(null);
            cxAlert.setGraphic(null);
            cxAlert.setContentText("Data has not been saved. Do you wish to discard your changes and return to the Customer Maintenance screen?");
            //If user clicks OK, load the CxMaintenancePage
            cxAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    controllerReference.changeToApptSchedulingPage(event);
                }
            });
        }
    }

    //Create method to hide the regular Back button and instead display the one to return to the Appointment Data Entry screen
    public void switchBackBtns() {
        cxBackBtn.setVisible(false);
        apptBackBtn.setVisible(true);
    }

    //Create logic for the Back button on the CxDataEntryPage - to be set only if coming from the ApptDataEntryPage
    public void apptCxBackBtnAction(ActionEvent event) {
        //First, get the data entered in the fields and ComboBoxes
        getFormInfo();
        //Check to see if the fields are empty. If they are, call the changeToCxMaintPage method in Controller
        if (cxName.isEmpty() && cxPhone.isEmpty() && cxStreet.isEmpty() && cxCity.isEmpty() && cxState.isEmpty()
                && cxZip.isEmpty() && cxCountry==null && cxActiveFlag==null) {
            //Call the method to change the page
            changeToApptDataEntryPage(event);
            //Check to see if this is the same data currently entered in the db for the customer - no changes made
        } else if (modCxLbl.isVisible() && cxName.equals(modCustomer.getCustomerName()) && cxPhone.equals(modCustomer.getPhone()) &&
                cxStreet.equalsIgnoreCase(modCustomer.getAddress()) && cxCity.equalsIgnoreCase(modCustomer.getCity()) &&
                cxState.equalsIgnoreCase(modCustomer.getAddress2()) && cxZip.equalsIgnoreCase(modCustomer.getPostalCode()) &&
                cxCountry.equals(modCustomer.getCountry()) && cxActiveFlag.equals(modCustomer.getActive())) {
            //Call the method to change the page
            changeToApptDataEntryPage(event);
        } else {
            //If there is anything new entered in textfields, display an alert to have user confirm they want to discard all data on page
            Alert cxAlert = new Alert(Alert.AlertType.CONFIRMATION);
            cxAlert.setTitle("Warning!");
            cxAlert.setHeaderText(null);
            cxAlert.setGraphic(null);
            cxAlert.setContentText("Data has not been saved. Do you wish to discard your changes and return to the Appointment Data Entry screen?");
            //If user clicks OK, load the ApptDataEntryPage
            cxAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    changeToApptDataEntryPage(event);
                }
            });
        }
    }

    //Create logic for the Submit button on the CxDataEntryPage, collect all entered info, verify correct format, then write to DB
    public void submitBtnAction(ActionEvent event) {
        getFormInfo();
        //Verify a customer name has been entered
        if (cxName.isEmpty()) {
            String nameAlert = "Customer name cannot be blank.";
            generateAlert(nameAlert);
            return;
        }
        //Verify a phone number has been entered and appears valid.
        boolean validPhone = validatePhoneNumber(cxPhone);
        if (cxPhone.isEmpty()|| !validPhone) {
            String phoneAlert = "Phone number cannot be blank and must be entered in ITU-T E.123 standard international format. \n\n" +
                "You must enter a country code with a + before it and only use optional spaces between groups of numbers. Valid phone numbers must be " +
                "between 7 and 15 digits. \n\n" +
                "For example, if your customer is in the United States with a phone number of (123)111-2222, you would enter the number as +1 123 111 2222 " +
                "or +1 1231112222";
            generateAlert(phoneAlert);
            return;
        }
        //Verify a street address and city have been entered
        if (cxStreet.isEmpty() || cxCity.isEmpty()) {
            String stCityAlert = "Street Address and City/Town/Village cannot be blank.";
            generateAlert(stCityAlert);
            return;
        }
        //Verify something is entered in the State/Province field
        if (cxState.isEmpty()) {
            String stateAlert = "The State/Province field cannot be blank. If there are no states or provinces in your customer's country, " +
                    "please enter 'NA' in this field.";
            generateAlert(stateAlert);
            return;
        }
        //Verify a country has been selected
        if (cxCountry==null) {
            String countryAlert = "You must select a country for your customer.";
            generateAlert(countryAlert);
            return;
        }
        //Verify a postal code is entered, since company has offices in US and UK, we also validate format for those locations
        //First, initialize a boolean to hold whether it is an invalid code for US or UK, default to false
        boolean isInvalid = false;
        if (cxZip.isEmpty()) {
            String postalAlert = "Postal code cannot be blank.";
            generateAlert(postalAlert);
            return;
        } else if (cxCountry.equals("United States")) {
            //Numbers only, either 5 digits or 9 digits in format 12345-6789
            String usRegex = "^[0-9]{5}(?:-[0-9]{4})?$";
            isInvalid = validatePostalCode(cxZip,usRegex);
        } else if (cxCountry.equals("United Kingdom")) {
            //Regex provided by UK government - source: https://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom#Validation
            String ukRegex = "^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])))) [0-9][A-Za-z]{2})$";
            isInvalid = validatePostalCode(cxZip,ukRegex);
        }
        //If the code entered is invalid for US or UK customer, display appropriate error message
        if (isInvalid && cxCountry.equals("United States")) {
            String usCodeAlert = "The postal code you entered is not valid for the United States. Zip codes must be all numbers and consist of " +
                    "5 digits or 9 digits with a hyphen separating the final four digits (ex. 12345 or 12345-6789.";
            generateAlert(usCodeAlert);
            return;
        } else if (isInvalid && cxCountry.equals("United Kingdom")) {
            String ukCodeAlert = "The postal code you entered is not valid for the United Kingdom. Postal codes must consist of letters and numbers " +
                    "ranging from 6 to 8 characters including a space between the two parts of the code. \n\n" +
                    "For more information about UK postal codes, please navigate to the following PDF document in your Internet browser: \n\n" +
                    "https://www.mrs.org.uk/pdf/postcodeformat.pdf";
            generateAlert(ukCodeAlert);
            return;
        }
        //Verify they have set the customer to active or inactive
        if (cxActiveFlag==null) {
            String activeAlert = "You must select a status of either Active or Inactive for your customer.";
            generateAlert(activeAlert);
            return;
        }
        //Finally, verify no unrestricted field has greater than 50 characters to avoid write issues to the DB
        if (cxName.length() > 50 || cxStreet.length() > 50 || cxCity.length() > 50 || cxState.length() > 50) {
            String lengthAlert = "Fields cannot contain more than 50 characters. Please check your entries for length.";
            generateAlert(lengthAlert);
            return;
        }
        //Convert the user friendly yes/no active field to a 1 or 0 for insert
        String cxActiveDBVar;
        if (cxActiveFlag.equals("Yes")) {
            cxActiveDBVar = "1";
        } else {
            cxActiveDBVar = "0";
        }

        //All entered data has been validated, time to write to the DB.
        //Get the username of the logged in user to pass to insert/update statements
        String user = controllerReference.getUserLoggedIn();

        //First, determine if this is a new cx or existing one.
        if (addCxLbl.isVisible()) {
            //First, need to determine if the entered country is already in the DB or if it's new. New will return 0, existing will return ID
            String countryQuery = "SELECT countryId from country WHERE country = '" + cxCountry + "';";
            int countryID = checkIfIDExists(countryQuery);
            //Next, check if the city exists already
            String cityQuery = "SELECT cityID from city WHERE city = '" + cxCity + "' and countryId = '" + countryID + "';";
            int cityID = checkIfIDExists(cityQuery);
            //Finally check if the exact address exists already
            String addrQuery = "SELECT addressId FROM address WHERE address = '" + cxStreet + "' and address2 = '" + cxState + "' " +
                    "and postalCode = '" + cxZip + "' and phone = '" + cxPhone + "' and cityID = '" + cityID + "';";
            int addrID = checkIfIDExists(addrQuery);

            //Determine whether we need to update the country table with a new country
            if (countryID==0) {
                countryID = generateCountryID();
                //Create the insert statement and execute it (countryId,country,createDate,createdBy,lastUpdate,lastUpdateBy)
                String insertCountry = "INSERT INTO country VALUES ('" + countryID + "','" + cxCountry + "',NOW(),'" +
                        user + "',NOW(),'" + user + "');";
                dbConnect.executeUpdate(insertCountry);
            } else {
                //If we're linking the new cx to an existing country, we need to update the last updated fields of the country
                String updateCountry = "UPDATE country SET lastUpdate = NOW(), lastUpdateBy = '" + user + "';";
                dbConnect.executeUpdate(updateCountry);
            }
            //Determine whether we need to update the city table with a new city
            if (cityID==0) {
                cityID = generateCityID();
                //Create the insert statement and execute it (cityId,city,countryId,createDate,createdBy,lastUpdate,lastUpdateBy)
                String insertCity = "INSERT INTO city VALUES ('" + cityID + "','" + cxCity + "','" + countryID + "',NOW(),'" +
                        user + "',NOW(),'" + user + "');";
                dbConnect.executeUpdate(insertCity);
            } else {
                //If we're linking the new cx to an existing city, we need to update the last updated fields of the city
                String updateCity = "UPDATE city SET lastUpdate = NOW(), lastUpdateBy = '" + user + "';";
                dbConnect.executeUpdate(updateCity);
            }
            //Determine whether we need to update the address table with a new address
            if (addrID==0) {
                addrID = generateAddressID();
                //Create the insert statement and execute it (addressId,address,address2,cityId,postalCode,phone,createDate,createdBy,lastUpdate,lastUpdateBy)
                String insertAddress = "INSERT INTO address VALUES ('" + addrID + "','" + cxStreet + "','" + cxState + "','" +
                        cityID + "','" + cxZip + "','" + cxPhone + "',NOW(),'" + user + "',NOW(),'" + user + "');";
                dbConnect.executeUpdate(insertAddress);
            } else {
                //If we're linking the new cx to an existing address, we need to update the last updated fields of the address
                String updateAddress = "UPDATE address SET lastUpdate = NOW(), lastUpdateBy = '" + user + "';";
                dbConnect.executeUpdate(updateAddress);
            }
            //Prepare customer insert statement, since we're on the add cx screen, this will always be a new customer
            String insertCX = "INSERT INTO customer VALUES ('" + cxID + "','" + cxName + "','" + addrID + "','" + cxActiveDBVar + "',NOW(),'" +
                    user + "',NOW(),'" + user + "');";
            dbConnect.executeUpdate(insertCX);

            //All done, clear all fields to prevent confusion
            clearBtnAction(event);
            generateNewCxID();
            //Initialize a String to pass to a confirmation message
            String confirmAddCx = "Customer #" + cxID + " - " + cxName + " has been added to the database successfully.";
            generateInfoDialog(confirmAddCx);

        //If this is a customer modification instead of a new customer, they need to be updated instead of inserted
        } else {
            //Initialize some ints with their current values to hold IDs
            int countryID = modCustomer.getCountryID();
            int cityID = modCustomer.getCityID();
            int addressID = modCustomer.getAddressID();

            //Determine if the country has been changed, first initialize an int to hold the existing countryId
            int oldCountryID = countryID;
            String sameCountryQuery = "SELECT country FROM country WHERE countryId = '" + oldCountryID + "';";
            //Execute the query and load the results into a ResultSet
            ResultSet countryRS = dbConnect.executeQuery(sameCountryQuery);
            //Initialize a boolean to hold the result
            boolean countryIsDifferent = false;
            try {
                while (countryRS.next()) {
                    if (countryRS.getString(1).equalsIgnoreCase(cxCountry)) {
                        countryIsDifferent = false;
                    } else {
                        countryIsDifferent = true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //If the country is different, we need to know if it already exists or if it needs to be inserted
            if (countryIsDifferent) {
                //New will return 0, existing will return ID
                String countryQuery = "SELECT countryId from country WHERE country = '" + cxCountry + "';";
                countryID = checkIfIDExists(countryQuery);
                //Determine whether we need to update the country table with a new country
                if (countryID == 0) {
                    countryID = generateCountryID();
                    //Create the insert statement and execute it (countryId,country,createDate,createdBy,lastUpdate,lastUpdateBy)
                    String insertCountry = "INSERT INTO country VALUES ('" + countryID + "','" + cxCountry + "',NOW(),'" +
                            user + "',NOW(),'" + user + "');";
                    dbConnect.executeUpdate(insertCountry);
                } else {
                    //If we're linking the cx to an existing country, we need to update the last updated fields of the country
                    String updateCountry = "UPDATE country SET lastUpdate = NOW(), lastUpdateBy = '" + user + "' WHERE countryId = '" + countryID + "';";
                    dbConnect.executeUpdate(updateCountry);
                }
                //If the country is different than what was pulled from DB, update the customer object as well
                modCustomer.setCountry(cxCountry);
                modCustomer.setCountryID(countryID);
            }

            //Determine if the city has been changed, first initialize an int to hold the existing cityId
            int oldCityID = cityID;
            String sameCityQuery = "SELECT city, countryId FROM city WHERE cityId = '" + oldCityID + "';";
            ResultSet cityRS = dbConnect.executeQuery(sameCityQuery);
            //Initialize a boolean to hold the result
            boolean cityIsDifferent = false;
            try {
                while (cityRS.next()) {
                    //Compare what's typed to DB info
                    if (cityRS.getString(1).equalsIgnoreCase(cxCity) && cityRS.getInt(2) == oldCityID) {
                        cityIsDifferent = false;
                    } else {
                        cityIsDifferent = true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //If the city is different, we need to know if it already exists or if it needs to be inserted
            if (cityIsDifferent) {
                //New will return 0, existing will return ID
                String cityQuery = "SELECT cityId from city WHERE city = '" + cxCity + "';";
                cityID = checkIfIDExists(cityQuery);
                //Determine whether we need to update the city table with a new city
                if (cityID == 0) {
                    cityID = generateCityID();
                    //Create the insert statement and execute it (cityId,city,countryId,createDate,createdBy,lastUpdate,lastUpdateBy)
                    String insertCity = "INSERT INTO city VALUES ('" + cityID + "','" + cxCity + "','" + countryID + "',NOW(),'" +
                            user + "',NOW(),'" + user + "');";
                    dbConnect.executeUpdate(insertCity);
                } else {
                    //If we're linking the new cx to an existing city, we need to update the last updated fields of the city
                    String updateCity = "UPDATE city SET lastUpdate = NOW(), lastUpdateBy = '" + user + "' WHERE cityId = '" + cityID + "';";
                    dbConnect.executeUpdate(updateCity);
                }
                //If the city is different than what was pulled from DB, update the customer object fields on page as well
                modCustomer.setCity(cxCity);
                modCustomer.setCityID(cityID);
            }

            //Determine if the address has been changed, first initialize an int to hold the existing addressId
            int oldAddressID = addressID;
            String sameAddressQuery = "SELECT address, address2, cityID, postalCode, phone FROM address WHERE addressId = '" + oldAddressID + "';";
            ResultSet addrRS = dbConnect.executeQuery(sameAddressQuery);
            //Initialize a boolean to hold the result
            boolean addressIsDifferent = false;
            try {
                while (addrRS.next()) {
                    //Compare the five checked fields
                    if (addrRS.getString(1).equalsIgnoreCase(cxStreet) && addrRS.getString(2).equalsIgnoreCase(cxState) &&
                            addrRS.getInt(3) == cityID && addrRS.getString(4).equalsIgnoreCase(cxZip) &&
                            addrRS.getString(5).equals(cxPhone)) {
                        addressIsDifferent = false;
                    } else {
                        addressIsDifferent = true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //If the address is different, we need to know if it already exists or if it needs to be inserted
            if (addressIsDifferent) {
                //New will return 0, existing will return ID
                String addrQuery = "SELECT addressId FROM address WHERE address = '" + cxStreet + "' and address2 = '" + cxState + "' " +
                        "and postalCode = '" + cxZip + "' and phone = '" + cxPhone + "' and cityID = '" + cityID + "';";
                addressID = checkIfIDExists(addrQuery);
                //Determine whether we need to update the address table with a new address
                if (addressID == 0) {
                    addressID = generateAddressID();
                    //Create the insert statement and execute it (addressId,address,address2,cityId,postalCode,phone,createDate,createdBy,lastUpdate,lastUpdateBy)
                    String insertAddress = "INSERT INTO address VALUES ('" + addressID + "','" + cxStreet + "','" + cxState + "','" +
                            cityID + "','" + cxZip + "','" + cxPhone + "',NOW(),'" + user + "',NOW(),'" + user + "');";
                    dbConnect.executeUpdate(insertAddress);
                } else {
                    //If we're linking the cx to an existing address, we need to update the last updated fields of the address
                    String updateAddress = "UPDATE address SET lastUpdate = NOW(), lastUpdateBy = '" + user + "' WHERE addressId = '" + addressID + "';";
                    dbConnect.executeUpdate(updateAddress);
                }
                //If the address is different than what was pulled from DB, update the customer object fields on page as well
                modCustomer.setAddress(cxStreet);
                modCustomer.setAddress2(cxState);
                modCustomer.setPostalCode(cxZip);
                modCustomer.setPhone(cxPhone);
                modCustomer.setAddressID(addressID);
            }

            //Update the customer record. Even if there are no changes made, we still want to update the last update fields to show a user verified info
            //Check to see which fields (if any) have been updated and adjust the UPDATE statement accordingly
            String updateName;
            String updateAddressID;
            String updateActive;
            if (modCustomer.getCustomerName().equals(cxName)) {
                updateName = "";
            } else {
                updateName = "customerName = '" + cxName + "', ";
                //Update the customer object as well
                modCustomer.setCustomerName(cxName);
            }
            if (modCustomer.getAddressID()==addressID) {
                updateAddressID = "";
            } else {
                updateAddressID = "addressID = '" + addressID + "', ";
            }
            if (modCustomer.getActive().equals(cxActiveFlag)) {
                updateActive = "";
            } else {
                updateActive = "active = '" + cxActiveDBVar + "', ";
                //Update the customer object as well
                modCustomer.setActive(cxActiveFlag);
            }
            //Prepare the customer UPDATE statement
            String updateCustomer = "UPDATE customer SET " + updateName + updateAddressID + updateActive + "lastUpdate = NOW(), lastUpdateBy = '" +
                    user + "' WHERE customerId = '" + cxID + "';";
            dbConnect.executeUpdate(updateCustomer);

            //Now that all updates have been done, we need to delete any records from country, city, and address tables that aren't linked to any
            //other customers. We only need to check this if the item was updated for this customer
            if (addressIsDifferent) {
                String deleteAddressQuery = "SELECT count(*) FROM customer WHERE addressId = '" + oldAddressID + "';";
                boolean canDeleteAddress = checkIfOkToDelete(deleteAddressQuery);
                if (canDeleteAddress) {
                    String deleteAddressStmt = "DELETE FROM address WHERE addressId = '" + oldAddressID + "';";
                    dbConnect.executeUpdate(deleteAddressStmt);
                }
            }
            if (cityIsDifferent) {
                String deleteCityQuery = "SELECT count(*) FROM address WHERE cityId = '" + oldCityID + "';";
                boolean canDeleteCity = checkIfOkToDelete(deleteCityQuery);
                //If canDeleteCity is true, prepare a delete statement and execute it
                if (canDeleteCity) {
                    String deleteCityStmt = "DELETE FROM city WHERE cityId = '" + oldCityID + "';";
                    dbConnect.executeUpdate(deleteCityStmt);
                }
            }
            if (countryIsDifferent) {
                String deleteCountryQuery = "SELECT count(*) FROM city WHERE countryId = '" + oldCountryID + "';";
                boolean canDeleteCountry = checkIfOkToDelete(deleteCountryQuery);
                //If canDeleteCountry is true, prepare a delete statement and execute it
                if (canDeleteCountry) {
                    String deleteCountryStmt = "DELETE FROM country WHERE countryId = '" + oldCountryID + "';";
                    dbConnect.executeUpdate(deleteCountryStmt);
                }
            }

            //All done, display a confirmation message that the customer has been updated successfully
            String confirmModCx = "Customer #" + cxID + " - " + cxName + " has been updated successfully.";
            generateInfoDialog(confirmModCx);
        }
    }

    //Clear all data entered on page
    public void clearBtnAction(ActionEvent event) {
        //If this is a new customer, clear everything. If it is existing, set back to what was there.
        if (addCxLbl.isVisible()) {
            cxNameTF.clear();
            cxPhoneTF.clear();
            cxStreetTF.clear();
            cxCityTF.clear();
            cxStateTF.clear();
            cxZipTF.clear();
            cxCountryCB.getSelectionModel().clearSelection();
            cxActiveFlagCB.getSelectionModel().clearSelection();
        } else {
            //Set back to what's currently in the DB
            loadCustomerData();
        }
    }

    //Activate the Add New Customer Label
    public void setAddCxLbl() {
        addCxLbl.setVisible(true);
    }

    //Activate the Modify Customer Label
    public void setModCxLbl() {
        modCxLbl.setVisible(true);
    }

    //Create method to generate an Error alert when info is blank or not filled out correctly for a customer
    public void generateAlert (String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Processing Request");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    //Create method to generate a success popup to confirm a requested action was completed
    public void generateInfoDialog (String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Request Processed Successfully");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    //Create method to prepopulate the Customer ID number with a new number
    public void generateNewCxID() {
        //Initialize an int to hold the result
        int cxID = 0;
        //Initialize String to hold query to get the current highest customerID in DB
        String cxIDQuery = "SELECT MAX(customerId) from customer;";
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(cxIDQuery);
        //Get the result and add 1 for the next customer ID number
        try {
            while (rs.next()) {
                cxID = rs.getInt("MAX(customerId)") + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        cxIDTF.setText(Integer.toString(cxID));
    }

    //Method to check if an address currently exists in the DB
    public int checkIfIDExists(String query) {
        //Initialize an int at 0 hold result if ID already exists
        int existingID = 0;
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(query);
        try {
            while (rs.next()) {
                existingID = rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return existingID;
    }

    //Method to see if the country/city/address ID passed in is linked to any other records in the DB
    public boolean checkIfOkToDelete (String query) {
        boolean canDelete = false;
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(query);
        try {
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    canDelete = true;
                } else {
                    canDelete = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return canDelete;
    }

    //Method to generate an address ID for new addresses
    public int generateAddressID() {
        int addressID = 0;
        //Initialize String to hold query to get current highest addressID in DB
        String addrIDQuery = "SELECT MAX(addressID) from address";
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(addrIDQuery);
        //Get the result and add 1 for the next address ID number
        try {
            while (rs.next()) {
                addressID = rs.getInt("MAX(addressID)") + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return addressID;
    }

    //Method to generate a city ID for new cities
    public int generateCityID() {
        int cityID = 0;
        //Initialize String to hold query to get current highest cityID in DB
        String cityIDQuery = "SELECT MAX(cityID) from city";
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(cityIDQuery);
        //Get the result and add 1 for the next address ID number
        try {
            while (rs.next()) {
                cityID = rs.getInt("MAX(cityID)") + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cityID;
    }

    //Method to generate a country ID for new countries
    public int generateCountryID() {
        int countryID = 0;
        //Initialize String to hold query to get current highest countryID in DB
        String countryIDQuery = "SELECT MAX(countryID) from country";
        //Execute the query and get the results
        ResultSet rs = dbConnect.executeQuery(countryIDQuery);
        //Get the result and add 1 for the next address ID number
        try {
            while (rs.next()) {
                countryID = rs.getInt("MAX(countryID)") + 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return countryID;
    }


    //Create a method to validate phone number. Since this is international, all numbers must be entered in ITU-T E.123 format.
    //+ before country code and only spaces between groups of numbers. Numbers must be between 7 and 15 digits.
    public boolean validatePhoneNumber(String phone) {
        //Initialize a boolean to hold the result of validation
        boolean isGood;
        //Create regex String to validate above requirements
        String regex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
        //Create a Pattern for comparison to the passed in phone number
        Pattern formatCheck = Pattern.compile(regex);
        Matcher matcher = formatCheck.matcher(phone);
        if (matcher.matches()) {
            isGood = true;
        } else {
            isGood = false;
        }
        return isGood;
    }

    public boolean validatePostalCode(String code, String regex) {
        //Initialize a boolean to hold the result of validation
        boolean isBad;
        //Create a Pattern for comparison to the passed in phone number
        Pattern formatCheck = Pattern.compile(regex);
        Matcher matcher = formatCheck.matcher(code);
        if (matcher.matches()) {
            isBad = false;
        } else {
            isBad = true;
        }
        return isBad;
    }

    public void changeToApptDataEntryPage(ActionEvent event) {
        try {
            FXMLLoader apptDataLoader = new FXMLLoader(getClass().getResource("ApptDataEntryPage.fxml"));
            Parent apptData = apptDataLoader.load();
            Scene apptDataScene = new Scene(apptData, 1000, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(apptDataScene);
            //Get a reference to the ApptDataEntryController
            ApptDataEntryController apptDataController = apptDataLoader.getController();


            //Set the saved appointment and load its data
            apptDataController.setModAppt(controllerReference.getSavedAppt());
            apptDataController.loadAppointmentData();
            //Set the appropriate header for adding a new appointment or modifying an existing one
            if (controllerReference.getIsNewAppt()) {
                apptDataController.setAddApptLbl();
            } else {
                apptDataController.setModApptLbl();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        //Clear any existing entries in the Select Country ComboBox
        cxCountryCB.getItems().clear();
        //Get a list of all ISO countries
        String[] locales = Locale.getISOCountries();
        ArrayList<String> countries = new ArrayList<>();
        //Get the display names and add them to the countries ArrayList
        for (String countryList : locales) {
            Locale obj = new Locale("",countryList);
            countries.add(obj.getDisplayCountry());
        }
        //Sort the countries alphabetically by display name for easier selection
        Collections.sort(countries);
        //Populate the ComboBox with the results
        cxCountryCB.getItems().addAll(countries);

        //Load Yes/No options into the Active Flag ChoiceBox
        cxActiveFlagCB.getItems().clear();
        cxActiveFlagCB.getItems().addAll("Yes", "No");
    }
}
