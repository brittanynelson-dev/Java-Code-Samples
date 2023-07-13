package SchedulingSoftware;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static java.time.ZoneOffset.UTC;


public class ReportsController {

    //Create references to GUI objects on the page
    @FXML
    Label numApptTypesLbl;
    @FXML
    Button prevYearBtn;
    @FXML
    Button curYearBtn;
    @FXML
    Button nextYearBtn;
    @FXML
    Label conSchedLbl;
    @FXML
    Button futOnlyBtn;
    @FXML
    Button monthAndFutBtn;
    @FXML
    Button yearAndFutBtn;
    @FXML
    Label cxDBLbl;
    @FXML
    Button activeOnlyBtn;
    @FXML
    Button allCxBtn;

    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Create a variable to hold the selected year for the Number of Appointment Types By Month report
    String selectedYear;
    //Create a variable to hold a query with the selected timeframe for the Consultant Schedules Report
    String conSchedQuery;
    //Create a variable to hold the active only or all choice for the customer report
    boolean activeOnly;

    //Call the Logout button method from the Controller
    public void logoutBtnAction(ActionEvent event) {
        controllerReference.logoutBtnAction(event);
    }

    //Call the Back button method from the Controller
    public void backBtnAction(ActionEvent event) {
        controllerReference.backBtnAction(event);
    }

    //Create method to display the available options for the Number of Appointments By Month report
    public void numApptTypesReportBtnAction(ActionEvent event) {
        //Clear any existing report option labels and buttons on the page
        hideAllReportOptions();
        //Get the current year, previous year, and next year to set button text for report options and pass to report query
        String yearCurr = Integer.toString(LocalDate.now().getYear());
        String yearPrev = Integer.toString(LocalDate.now().minusYears(1).getYear());
        String yearNext = Integer.toString(LocalDate.now().plusYears(1).getYear());
        //Display the label describing the button choice
        numApptTypesLbl.setVisible(true);
        //Display buttons to choose between previous year, current year, or next year
        prevYearBtn.setText(yearPrev);
        prevYearBtn.setVisible(true);
        curYearBtn.setText(yearCurr);
        curYearBtn.setVisible(true);
        nextYearBtn.setText(yearNext);
        nextYearBtn.setVisible(true);
    }

    //Create methods for each of the year buttons to set the selected year appropriately
    public void prevYearBtnAction(ActionEvent event) {
        selectedYear = prevYearBtn.getText();
        generateReportAlert();
    }
    public void curYearBtnAction(ActionEvent event) {
        selectedYear = curYearBtn.getText();
        generateReportAlert();
    }
    public void nextYearBtnAction(ActionEvent event) {
        selectedYear = nextYearBtn.getText();
        generateReportAlert();
    }

    //Create method to download and save the Number of Appointment Types By Month report as a .txt file
    public void createApptTypesByMonthReport() {
        //Create an ArrayList of valid appointment types
        ArrayList<String> apptTypes = new ArrayList<>();
        apptTypes.addAll(Arrays.asList("Presales Consultation","Initial Consultation","Standard Consultation","Customer Retention","Other Appointment"));
        //Create an ArrayList to hold month names for writing to file
        ArrayList<String> months = new ArrayList<>();
        months.addAll(Arrays.asList("0","January","February","March","April","May","June","July","August","September","October","November","December"));
        //Create an ArrayList to hold result Strings, these will eventually be written to the file
        ArrayList<String> apptTypeResults = new ArrayList<>();

        //This loop will build the queries and get results for the file we're going to write, outer loop runs 12 times, once for each month in the year
        for (int i = 1; i < 13; i++) {
            //This inner loop will run 5 times, once for each appointment type
            for (int j = 0; j < 5; j++) {
                //Build the query to get the count of this appointment type for the month
                String apptQuery = "SELECT COUNT(*) FROM appointment WHERE title = '" + apptTypes.get(j) + "' AND DATE(start) LIKE '" + selectedYear + "-" + String.format("%02d",i) + "%';";
                //Execute the query and load results into a ResultSet
                ResultSet rs = dbConnect.executeQuery(apptQuery);
                try {
                    while (rs.next()){
                        //First, check to see if we've started a new month, if we have write a line indicating the month and year the data belongs to
                        if (j==0) {
                            apptTypeResults.add("\n\n------------------------------------------------------\n");
                            apptTypeResults.add(months.get(i) + " " + selectedYear + "\n");
                            apptTypeResults.add("------------------------------------------------------\n\n");
                        }
                        //To keep the report neatly formatted, we want all counts to line up vertically. Adding tabs to achieve this based on title length
                        if (apptTypes.get(j).length() > 18) {
                            apptTypeResults.add(apptTypes.get(j) + ":\t\t\t\t" + rs.getInt(1) + "\n");
                        } else {
                            apptTypeResults.add(apptTypes.get(j) + ":\t\t\t\t\t" + rs.getInt(1) + "\n");
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        //Now that the data has been pulled, present user with a FileChooser to select their download location
        FileChooser fc = new FileChooser();
        //Set an extension filter to only show .txt and set a default filename
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files","*txt"));
        fc.setInitialFileName("Appointment_Types_By_Month_" + selectedYear + ".txt");
        //Show the save file dialog
        File file = fc.showSaveDialog(numApptTypesLbl.getScene().getWindow());

        if (file != null) {
            //We have a file and all the data we need so we're ready to write the file, initialize a buffered writer for this task
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
                //Iterate through the ArrayList to write each line to the file
                for (String str : apptTypeResults) {
                    bw.write(str);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    //Create method to display the available options for the Consultant Schedules Report
    public void consultantSchedulesBtnAction(ActionEvent event) {
        //Clear any existing report option labels and buttons on the page
        hideAllReportOptions();
        //Display the label and option buttons to determine which type of report the user wants
        conSchedLbl.setVisible(true);
        futOnlyBtn.setVisible(true);
        monthAndFutBtn.setVisible(true);
        yearAndFutBtn.setVisible(true);
    }

    //Create methods for each of the timeframe buttons to set the query appropriately
    public void futOnlyBtnAction(ActionEvent event) {
        //We can use the NOW() function directly in the query, it will automatically convert to UTC time
        conSchedQuery = "SELECT * FROM appointment WHERE start >= NOW() ORDER BY contact ASC, start ASC;";
        createConScheduleReport();
    }
    public void monthAndFutBtnAction(ActionEvent event) {
        //Get today's date to grab the first of this month for the query
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);
        conSchedQuery = "SELECT * FROM appointment WHERE start >= '" + firstOfMonth + "' ORDER BY contact ASC, start ASC;";
        createConScheduleReport();
    }
    public void yearAndFutBtnAction(ActionEvent event) {
        //Get today's date to grab the first of this year for the query
        LocalDate firstOfYear = LocalDate.now().withDayOfYear(1);
        conSchedQuery = "SELECT * FROM appointment WHERE start >= '" + firstOfYear + "' ORDER BY contact ASC, start ASC;";
        createConScheduleReport();
    }

    //Create method to download and save the Consultant Schedules report as a .txt file
    public void createConScheduleReport() {
        //Create an ArrayList to hold all lines that will be written to the file
        ArrayList<String> reportLines = new ArrayList<>();
        //Create a variable to hold the current contact so we can add a few extra lines when it switches to the next consultant
        String currentContact = null;
        //Query the DB using the previously set string and load the results into a ResultSet
        ResultSet rs = dbConnect.executeQuery(conSchedQuery);
        //Iterate through the results and use them to build a string of relevant data for each appointment
        try {
            while (rs.next()) {
                //If the contact has changed, include a few special lines to indicate we're switching users
                if (!rs.getString("contact").equals(currentContact)) {
                    currentContact = rs.getString("contact");
                    reportLines.add("\n\n---------------------------------------------\n");
                    reportLines.add("Appointment schedule for: " + currentContact + "\n");
                    reportLines.add("---------------------------------------------\n\n");
                }
                //Get useful relevant values for consultant's schedules
                ZonedDateTime start = ZonedDateTime.ofInstant(rs.getTimestamp("start").toInstant(), UTC);
                ZonedDateTime end = ZonedDateTime.ofInstant(rs.getTimestamp("end").toInstant(), UTC);
                String title = rs.getString("title");
                int cxID = rs.getInt("customerId");
                //Add a line to the array list for this appointment
                reportLines.add(start.toLocalDate().toString() + " - " + String.format("%02d",start.getHour()) + ":" +
                        String.format("%02d",start.getMinute()) + " to " + String.format("%02d",end.getHour()) + ":" +
                        String.format("%02d",end.getMinute()) + " - " + title + " with customer #" + cxID + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Now that the data has been pulled, present user with a FileChooser to select their download location
        FileChooser fc = new FileChooser();
        //Set an extension filter to only show .txt and set a default filename
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files","*txt"));
        fc.setInitialFileName("Consultant_Schedules_Report.txt");
        //Show the save file dialog
        File file = fc.showSaveDialog(conSchedLbl.getScene().getWindow());

        if (file != null) {
            //We have a file and all the data we need so we're ready to write the file, initialize a buffered writer for this task
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
                //Iterate through the ArrayList to write each line to the file
                for (String str : reportLines) {
                    bw.write(str);
                }
                //Add a final line advising that all times are in UTC
                bw.write("\n\n*All appointment times are listed in UTC (GMT+0).");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    //Create method to display the available options for the Customer Database Report
    public void cxDBReportBtnAction(ActionEvent event) {
        //Clear any existing report option labels and buttons on the page
        hideAllReportOptions();
        cxDBLbl.setVisible(true);
        activeOnlyBtn.setVisible(true);
        allCxBtn.setVisible(true);
    }

    //Create methods for both of the customer report option buttons to set the activeOnly boolean appropriately
    public void activeOnlyBtnAction(ActionEvent event) {
        activeOnly = true;
        createCustomerDBReport();
    }
    public void allCxBtnAction(ActionEvent event) {
        activeOnly = false;
        createCustomerDBReport();
    }

    //Create method to download and save the Customer Database Report as a .csv file
    public void createCustomerDBReport() {
        //Create an ArrayList to hold all lines that will be written to the file
        ArrayList<String> customers = new ArrayList<>();
        //Prepare the query statement to grab all customers in DB along with their related address info
        String cxQuery = "select cx.customerId, cx.customerName, cx.active, cx.createDate, cx.createdBy, cx.lastUpdate, cx.lastUpdateBy, a.addressId, " +
                "a.address, a.address2, a.postalCode, a.phone, a.createDate, a.createdBy, a.lastUpdate, a.lastUpdateBy, ci.cityId, ci.city, " +
                "ci.createDate, ci.createdBy, ci.lastUpdate, ci.lastUpdateBy, co.countryId, co.country, co.createDate, co.createdBy, co.lastUpdate, " +
                "co.lastUpdateBy " +
                "from customer cx " +
                "join address a on cx.addressId = a.addressId " +
                "join city ci on a.cityId = ci.cityId " +
                "join country co on ci.countryID = co.countryID;";
        //If activeOnly is true, we need to update the query to only pull active customers
        if (activeOnly) {
            cxQuery = cxQuery.replace(";"," WHERE cx.active = '1';");
        }
        //Execute the query and load the results into a ResultSet
        ResultSet rs = dbConnect.executeQuery(cxQuery);
        //Iterate through the results and add customers to the ArrayList
        try {
            while (rs.next()) {
                int cxID = rs.getInt("customerId");
                String cxName = rs.getString("customerName").replace(","," ");
                boolean active = rs.getBoolean("active");
                String cxCreateDate = rs.getTimestamp(4).toString();
                String cxCreatedBy = rs.getString(5);
                String cxLastUpdate = rs.getTimestamp(6).toString();
                String cxLastUpdatedBy = rs.getString(7);
                int addressID = rs.getInt("addressId");
                String address = rs.getString("address").replace(","," ");
                String address2 = rs.getString("address2").replace(","," ");
                String postalCode = rs.getString("postalCode").replace(","," ");
                String phone = rs.getString("phone");
                String addrCreateDate = rs.getTimestamp(13).toString();
                String addrCreatedBy = rs.getString(14);
                String addrLastUpdate = rs.getTimestamp(15).toString();
                String addrLastUpdatedBy = rs.getString(16);
                int cityID = rs.getInt("cityId");
                String city = rs.getString("city").replace(","," ");
                String cityCreateDate = rs.getTimestamp(19).toString();
                String cityCreadtedBy = rs.getString(20);
                String cityLastUpdate = rs.getTimestamp(21).toString();
                String cityLastUpdatedBy = rs.getString(22);
                int countryID = rs.getInt("countryId");
                String country = rs.getString("country");
                String coCreateDate = rs.getTimestamp(25).toString();
                String coCreatedBy = rs.getString(26);
                String coLastUpdate = rs.getTimestamp(27).toString();
                String coLastUpdatedBy = rs.getString(28);

                customers.add(cxID +","+ cxName +","+ active +","+ cxCreateDate +","+ cxCreatedBy +","+ cxLastUpdate +","+ cxLastUpdatedBy +",,"+
                    addressID +","+ address +","+ address2 +","+ postalCode +","+ phone +","+ addrCreateDate +","+ addrCreatedBy +","+
                    addrLastUpdate +","+ addrLastUpdatedBy +",,"+ cityID +","+ city +","+ cityCreateDate +","+ cityCreadtedBy +","+
                    cityLastUpdate +","+ cityLastUpdatedBy +",,"+ countryID +","+ country +","+ coCreateDate +","+ coCreatedBy +","+ coLastUpdate +","+
                    coLastUpdatedBy +"\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Now that the data has been pulled, present user with a FileChooser to select their download location
        FileChooser fc = new FileChooser();
        //Set an extension filter to only show .txt and set a default filename
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Separated Values files","*csv"));
        fc.setInitialFileName("Customer_Database_Report.csv");
        //Show the save file dialog
        File file = fc.showSaveDialog(conSchedLbl.getScene().getWindow());

        if (file != null) {
            //We have a file and all the data we need so we're ready to write the file, initialize a buffered writer for this task
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
                //Add a header line naming all of the fields displayed in the report
                bw.write("Customer ID,Customer Name,Active,Creation Date,Created By,Last Update,Last Updated By,,Address ID,Street Address," +
                        "State/Province,Postal Code,Phone Number,Creation Date,Created By,Last Update,Last Updated By,,City ID,City,Creation Date," +
                        "Created By,Last Update,Last Updated By,,Country ID,Creation Date,CreatedBy,Last Update,Last Updated By\n");
                //Iterate through the ArrayList to write each line to the file
                for (String str : customers) {
                    bw.write(str);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    //Create method to clear all report option buttons and labels, this gets executed when one of the main report buttons are clicked to clean up the screen
    public void hideAllReportOptions() {
        numApptTypesLbl.setVisible(false);
        prevYearBtn.setVisible(false);
        curYearBtn.setVisible(false);
        nextYearBtn.setVisible(false);
        conSchedLbl.setVisible(false);
        futOnlyBtn.setVisible(false);
        monthAndFutBtn.setVisible(false);
        yearAndFutBtn.setVisible(false);
        cxDBLbl.setVisible(false);
        activeOnlyBtn.setVisible(false);
        allCxBtn.setVisible(false);
    }

    //Create a method to display a warning popup before running reports that could take awhile to process
    public void generateReportAlert() {
        Alert reportAlert = new Alert(Alert.AlertType.CONFIRMATION, "The report you have selected may take awhile to process. " +
                "Would you like to continue?");
        reportAlert.setTitle("Warning!");
        reportAlert.setHeaderText(null);
        reportAlert.setGraphic(null);
        reportAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                createApptTypesByMonthReport();
            }
        });
    }
}
