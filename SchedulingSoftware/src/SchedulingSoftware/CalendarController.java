package SchedulingSoftware;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static java.time.ZoneOffset.UTC;


public class CalendarController {

    //Create references to GUI objects on page
    @FXML
    Label monthLbl;
    @FXML
    Tab weekTab;
    @FXML
    Tab monthTab;
    @FXML
    GridPane weekGridPane;
    @FXML
    GridPane monthGridPane;

    //Create an instance of Controller for use in this class
    Controller controllerReference = new Controller();
    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Create variables to hold the current date on the calendar, these will be the first day of the week and the first day of the month
    LocalDate currentWeekDate;
    LocalDate currentMonthDate;

    //Create ArrayLists of Nodes to keep track of all Labels added to the calendar so they can be removed if the user moves to the next week/month
    ArrayList<Node> weekLabels = new ArrayList<>();
    ArrayList<Node> monthLabels = new ArrayList<>();

    //Initialize an ArrayList to hold the VBoxes added in day slots for the week view and the month view to hold appointment info
    ArrayList<VBox> weekVBoxes = new ArrayList<>();
    ArrayList<VBox> monthVBoxes = new ArrayList<>();

    //Call the Logout button method from the Controller
    public void logoutBtnAction(ActionEvent event) {
        controllerReference.logoutBtnAction(event);
    }

    //Call the Back button method from the Controller
    public void backBtnAction(ActionEvent event) {
        controllerReference.backBtnAction(event);
    }

    //Create method to populate the calendar week
    public void populateWeek(LocalDate date) {
        LocalDate weekDate = date;
        //Move the date back until it is Sunday, unless the current date happens to be Sunday.
        while (!weekDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            weekDate = weekDate.minusDays(1);
        }
        //Once we've found our Sunday date, set this date as the current calendar week date to save it for Next/Previous button functionality
        currentWeekDate = weekDate;
        //Set the month and year in the label above the calendar
        monthLbl.setText(weekDate.getMonth() + " " + weekDate.getYear());
        //Check to see if the logged in user has any appointments this week
        String apptQuery = "SELECT * FROM appointment WHERE contact = '" + controllerReference.getUserLoggedIn() + "' AND DATE(start) >= '" +
                currentWeekDate + "' AND DATE(end) < '" + currentWeekDate.plusDays(7) + "' ORDER BY start;";
        //Execute the query and load the results into a ResultSet
        ResultSet rs = dbConnect.executeQuery(apptQuery);
        //Initialize two ArrayLists, these two are paired, one to keep a date object for comparison and the other to keep label text for the appointment
        ArrayList<LocalDate> dateList = new ArrayList<>();
        ArrayList<String> apptList = new ArrayList<>();
        //Iterate through any results and add them to ArrayLists
        try {
            while (rs.next()) {
                dateList.add(rs.getDate("start").toLocalDate());
                ZonedDateTime start = ZonedDateTime.ofInstant(rs.getTimestamp("start").toInstant(), UTC);
                ZonedDateTime end = ZonedDateTime.ofInstant(rs.getTimestamp("end").toInstant(), UTC);
                String title = rs.getString("title");
                int cxId = rs.getInt("customerId");
                //Get the user's saved timezone for conversion
                ZoneId timeZone = controllerReference.getTimeZoneId();
                //Add the appointment info to the ArrayList
                apptList.add("\n " + String.format("%02d",start.withZoneSameInstant(timeZone).getHour()) + ":" +
                        String.format("%02d",start.withZoneSameInstant(timeZone).getMinute()) + " - " +
                        String.format("%02d",end.withZoneSameInstant(timeZone).getHour()) + ":" +
                        String.format("%02d",end.withZoneSameInstant(timeZone).getMinute()) + "\n " + title + "\n With Cx #" + cxId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Iterate through the 7 days in this week to number the gridpane nodes appropriately and add in appointment info if any were found for that day
        for (int i = 0; i < 7; i++) {
            //Create a date label and add it to the top right corner of the grid pane
            Label lbl = new Label();
            lbl.setText(Integer.toString(weekDate.getDayOfMonth()));
            weekGridPane.setValignment(lbl, VPos.TOP);
            weekGridPane.setHalignment(lbl, HPos.RIGHT);
            weekGridPane.add(lbl,i,1);
            //Add the new node to the ArrayList of nodes currently on the calendar
            weekLabels.add(lbl);
            //Create a Vbox in case we need to add an appointment for this day
            VBox weekVB = new VBox();
            weekVB.setAlignment(Pos.TOP_CENTER);
            //Check to see if we have a saved appointment for this day
            for (int j = 0; j < dateList.size(); j++) {
                if (weekDate.equals(dateList.get(j))) {
                    Label apptLbl = new Label();
                    apptLbl.setText(apptList.get(j));
                    weekVB.getChildren().add(apptLbl);
                    //Add the label to the arraylist of labels so it can be cleaned up later
                    weekLabels.add(apptLbl);
                }
            }
            //Add the VBox to the arraylist of VBoxes so it can be cleaned up later
            weekVBoxes.add(weekVB);
            //Add the VBox to the grid pane
            weekGridPane.add(weekVB,i,1);
            //Add a day to the date to prepare for the next iteration
            weekDate = weekDate.plusDays(1);
        }
    }

    //Create method to populate the calendar month
    public void populateMonth(LocalDate date) {
        LocalDate monthDate = date.withDayOfMonth(1);
        //Set the month and year in the label above the calendar now before any changes are made
        monthLbl.setText(monthDate.getMonth() + " " + monthDate.getYear());
        //Set this date as the current calendar month date to save it for Next/Previous button functionality
        currentMonthDate = monthDate;
        //Move the date back until it is Sunday, unless the current date happens to be Sunday.
        while (!monthDate.getDayOfWeek().toString().equals("SUNDAY") ) {
            monthDate = monthDate.minusDays(1);
        }

        //Check to see if the logged in user has any appointments this month
        String apptQuery = "SELECT * FROM appointment WHERE contact = '" + controllerReference.getUserLoggedIn() + "' AND DATE(start) >= '" +
                monthDate + "' AND DATE(end) < '" + monthDate.plusDays(42) + "' ORDER BY start;";
        //Execute the query and load the results into a ResultSet
        ResultSet rs = dbConnect.executeQuery(apptQuery);
        //Initialize two ArrayLists, these two are paired, one to keep a date object for comparison and the other to keep label text for the appointment
        ArrayList<LocalDate> dateList = new ArrayList<>();
        ArrayList<String> apptList = new ArrayList<>();
        //Iterate through any results and add them to ArrayLists
        try {
            while (rs.next()) {
                dateList.add(rs.getDate("start").toLocalDate());
                ZonedDateTime start = ZonedDateTime.ofInstant(rs.getTimestamp("start").toInstant(), UTC);
                ZonedDateTime end = ZonedDateTime.ofInstant(rs.getTimestamp("end").toInstant(), UTC);
                //Get the user's saved timezone for conversion
                ZoneId timeZone = controllerReference.getTimeZoneId();
                //Add the appointment info to the ArrayList
                apptList.add(String.format("%02d",start.withZoneSameInstant(timeZone).getHour()) + ":" +
                        String.format("%02d",start.withZoneSameInstant(timeZone).getMinute()) + " - " +
                        String.format("%02d",end.withZoneSameInstant(timeZone).getHour()) + ":" +
                        String.format("%02d",end.withZoneSameInstant(timeZone).getMinute()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Iterate through 6 weeks of dates to build the calendar, create ints to keep track of which week and column we're on
        int row = 1;
        //Initialize a separate int to hold the column number so it can be reset for each week without loosing overall count
        int col = 0;
        for (int i = 0; i < 42; i++ ) {
            //Check to see if the row number needs to be updated, if it does, reset the col int
            if (i == 7 || i == 14 || i == 21 || i == 28 || i == 35) {
                row++;
                col = 0;
            }
            Label lbl = new Label();
            lbl.setText(Integer.toString(monthDate.getDayOfMonth()));
            monthGridPane.setValignment(lbl, VPos.TOP);
            monthGridPane.setHalignment(lbl, HPos.RIGHT);
            monthGridPane.add(lbl,col,row);
            //Add the new node to the ArrayList of nodes currently on the calendar
            monthLabels.add(lbl);
            //Create a Vbox in case we need to add an appointment for this day
            VBox monthVB = new VBox();
            monthVB.setAlignment(Pos.TOP_LEFT);
            //Check to see if we have a saved appointment for this day
            for (int j = 0; j < dateList.size(); j++) {
                if (monthDate.equals(dateList.get(j))) {
                    Label apptLbl = new Label();
                    apptLbl.setText(apptList.get(j));
                    monthVB.getChildren().add(apptLbl);
                    //Add the label to the arraylist of labels so it can be cleaned up later
                    monthLabels.add(apptLbl);
                }
            }
            //Add the VBox to the arraylist of VBoxes so it can be cleaned up later
            monthVBoxes.add(monthVB);
            //Add the VBox to the grid pane
            monthGridPane.add(monthVB,col,row);
            //Add a day to the month date and increment col to prepare for the next iteration
            monthDate = monthDate.plusDays(1);
            col++;
        }
    }

    //Create method to allow the user to jump directly to the appointment scheduling page without having to back out to the main menu first
    public void goToApptSchedulingBtnAction(ActionEvent event) {
        controllerReference.changeToApptSchedulingPage(event);
    }


    //Create logic for the Next button to move to the next week or month, depending on which tab they currently have selected
    public void nextBtnAction() {
        //Determine if the user is looking at the week or the month
        if (weekTab.isSelected()) {
            //Clear the existing labels off of the gridpane
            weekGridPane.getChildren().removeAll(weekLabels);
            weekGridPane.getChildren().removeAll(weekVBoxes);
            //Add 7 days to the current saved date then re-populate the calendar
            currentWeekDate = currentWeekDate.plusDays(7);
            populateWeek(currentWeekDate);
        } else {
            //Clear the existing labels off of the gridpane
            monthGridPane.getChildren().removeAll(monthLabels);
            monthGridPane.getChildren().removeAll(monthVBoxes);
            //Add 1 month to the current saved date then re-populate the calendar
            currentMonthDate = currentMonthDate.plusMonths(1);
            populateMonth(currentMonthDate);
        }
    }

    //Create logic for the Previous button to move to the previous week or month, depending on which tab they currently have selected
    public void previousBtnAction() {
        //Determine if the user is looking at the week or the month
        if (weekTab.isSelected()) {
            //Clear the existing labels off of the gridpane
            weekGridPane.getChildren().removeAll(weekLabels);
            weekGridPane.getChildren().removeAll(weekVBoxes);
            //Subtract 7 days from the current saved date then re-populate the calendar
            currentWeekDate = currentWeekDate.minusDays(7);
            populateWeek(currentWeekDate);
        } else {
            //Clear the existing labels off of the gridpane
            monthGridPane.getChildren().removeAll(monthLabels);
            monthGridPane.getChildren().removeAll(monthVBoxes);
            //Subtract 1 month from the current saved date then re-populate the calendar
            currentMonthDate = currentMonthDate.minusMonths(1);
            populateMonth(currentMonthDate);
        }
    }

    //Week and month calendars are independent of each other by design, if user clicks tab to change to week view, update the monthLbl
    public void updateWeekLbl() {
        if (weekTab.isSelected() && currentWeekDate != null) {
            monthLbl.setText(currentWeekDate.getMonth() + " " + currentWeekDate.getYear());
        }
    }
    //Week and month calendars are independent of each other by design, if user clicks tab to change to month view, update the monthLbl
    public void updateMonthLbl() {
         if (monthTab.isSelected() && currentMonthDate != null){
            monthLbl.setText(currentMonthDate.getMonth() + " " + currentMonthDate.getYear());
        }
    }
}
