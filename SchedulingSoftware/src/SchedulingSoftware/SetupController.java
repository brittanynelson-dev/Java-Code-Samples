package SchedulingSoftware;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.io.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class SetupController {

    //Create reference to GUI objects on page
    @FXML
    ComboBox timeZoneCB;

    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();

    public void setDefaultTimeZone(ZoneId zone) {
        timeZoneCB.setValue(zone.toString());
    }

    //Call the Logout button method from the Controller
    public void logoutBtnAction(ActionEvent event) {
        controllerReference.logoutBtnAction(event);
    }

    //Call the Back button method from the Controller
    public void backBtnAction(ActionEvent event) {
        controllerReference.backBtnAction(event);
    }

    //Create method for the Save button to update the user's time zone
    public void saveBtnAction(ActionEvent event) {
        //First, get the zoneId selected by the user and store it in a variable
        String selectedTimeZone = (String) timeZoneCB.getSelectionModel().getSelectedItem();
        //Get the user logged in and their saved timezone
        String user = controllerReference.getUserLoggedIn();
        ZoneId savedZoneId = controllerReference.getTimeZoneId();
        //String savedTimeZone = savedZoneId.toString();
        //Update the AccountTZs.txt file with the new timeZone for this user
        File tzFile = new File("AccountTZs.txt");
        //Initialize an empty String and null BufferedReader and BufferedWriter to be able to update the file
        String origContent = "";
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(tzFile));
            //Read all lines from file into origContent
            String currentLine = br.readLine();
            while (currentLine != null) {
                origContent = origContent + currentLine + System.lineSeparator();
                currentLine = br.readLine();
            }
            //Replace the current entry for the user with the updated time zone
            //String currentData =
            String newContent = origContent.replaceAll(user+","+savedZoneId,user+","+selectedTimeZone);
            //Write the updated content into the file
            bw = new BufferedWriter(new FileWriter(tzFile));
            bw.write(newContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the reader and writer
            try {
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //Update the timeZone variable for the user
        controllerReference.setTimeZone(selectedTimeZone);

        //Display a message letting the user know the update was successful
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Request Processed Successfully");
        alert.setHeaderText(null);
        alert.setContentText("Your time zone has been updated successfully.");
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        //Clear any existing entries in the Select Country ComboBox
        timeZoneCB.getItems().clear();
        //Get a list of all zone IDs and put them in an ArrayList
        Set<String> zones = ZoneId.getAvailableZoneIds();
        ArrayList<String> zoneIds = new ArrayList(zones);
        //Sort the zones alphabetically for easier selection
        Collections.sort(zoneIds);
        //Populate the ComboBox with the results
        timeZoneCB.getItems().addAll(zoneIds);
    }
}
