package SchedulingSoftware;

import com.sun.rowset.CachedRowSetImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.*;

import static java.time.ZoneOffset.UTC;

public class DBInterface {

    //Setup variables to connect to MySQL database
    Connection conn = null;

    String driver = "com.mysql.jdbc.Driver";
    String db = "U04ifH";
    String url = "jdbc:mysql://52.206.157.109/" + db + "?useLegacyDatetimeCode=false";
    String user = "U04ifH";
    String pass = "53688251736";


    public void connectDB() throws ClassNotFoundException {
        //Open the db connection
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url,user,pass);
        } catch (SQLException e){
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }

    public void disconnectDB() {
        //Close the connection when finished
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }

    public ResultSet executeQuery(String stmt) {
        //Create a ResultSet variable to hold db results and a CachedRowSet to return
        ResultSet rs = null;
        CachedRowSetImpl crs = null;
        try {
            //Connect to Database
            connectDB();
            //Create a Java statement to send data
            Statement st = conn.createStatement();
            //Execute the query, and load results into a ResultSet
            rs = st.executeQuery(stmt);
            //Populate the CachedRowSet with the results
            crs = new CachedRowSetImpl();
            crs.populate(rs);
            //Close the ResultSet and Statement
            rs.close();
            st.close();
            //Close the DB connection
            disconnectDB();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Return the CachedRowSet
        return crs;
    }

    public void executeUpdate(String stmt) {
        try {
            //Connect to DB
            connectDB();
            //Create a Java Statement to send data
            Statement st = conn.createStatement();
            st.executeUpdate(stmt);
            //Close the Statement
            st.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Close the DB connection
        disconnectDB();
    }

    public int login(String username, String password) {
        //Create variable to hold loginCode returned. 0 is success, 1 is good credentials but an inactive user, and
        //2 is no record found or bad password. Initialized at -1 to indicate a connection failure if it is not updated.
        int loginCode = -1;
        //Create a query to pull data based on the username entered
        String loginQuery = "SELECT * FROM user WHERE userName = '" + username + "';";
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(loginQuery);
        //Initialize boolean to hold DB active flag
        boolean active = false;
        //Initialize int to confirm a userID is associated with the entered credentials
        int userID = 0;
        //Initialize String to hold DB userName as it is listed there for accurate transaction reporting
        String dbUsername = null;
        //Initialize String to hold DB password for comparison to entered password
        String dbPassword = null;
        try {
            //Iterate through results - should only ever be one, username should be unique
            while (rs.next()) {
                //Check the number in the active column for this user
                userID = rs.getInt("userID");
                active = rs.getBoolean("active");
                dbUsername = rs.getString("userName");
                dbPassword = rs.getString("password");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Make sure userID pulled is not null, if it is the username entered isn't valid, set loginCode to 2 and move on
        if (userID == 0){
            loginCode = 2;
        } else if (!password.equals(dbPassword)){
            //If user is valid but password is bad, set loginCode to 2 and write a record to the log for the failed attempt
            loginCode = 2;
            writeToLog(dbUsername, password, 1);
        } else {
            //If the user is active set loginCode to 0, write the username back to Controller and write a record to the log
            if (active) {
                loginCode = 0;
                Controller.setUserLoggedIn(dbUsername);
                writeToLog(dbUsername, password, 0);
            } else {
                //If the user isn't active, set loginCode to 1 and write a record to the log
                loginCode = 1;
                writeToLog(dbUsername, password, 2);
            }
        }
        //Return the loginCode
        return loginCode;
    }

    //Create method to write login attempts to log file. Statuses: 0 = successful, 1 = bad password, 2 = inactive user
    public void writeToLog(String user, String password, int status){
        //Create a new file object to use for a log
        File logFile = new File("ActivityLog.txt");
        //Initialize a BufferedWriter to write records to the log file
        BufferedWriter bw = null;
        //Grab the current timestamp to report in log
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //Open the BufferedWriter and start writing the log
        try {
            bw = new BufferedWriter(new FileWriter(logFile, true));
            //Determine what type of message to write based on the status passed in
            if (status == 0){
                bw.write(timestamp + " - SUCCESSFUL LOGIN - User: " + user);
                bw.newLine();
            } else if (status == 1){
                bw.write(timestamp + " - FAILED LOGIN ATTEMPT - BAD PASSWORD - User: " + user + " Password Entered: " + password);
                bw.newLine();
            } else {
                bw.write(timestamp + " - FAILED LOGIN ATTEMPT - INACTIVE USER - User: " + user);
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    //Create method to query Customer, Address, City, and Country tables to build Customer objects for TableView
    public ObservableList<Customer> buildCustomerList() {
        //Initialize an Observable list to hold all customers returned
        ObservableList<Customer> cxList = FXCollections.observableArrayList();
        //Initialize a String to hold the customer query, needs to be done in exact order, columns with duplicate names have to be pulled by index
        String customerQuery = "select cx.customerId, cx.customerName, cx.addressId, cx.active, a.address, a.address2, a.cityId, a.postalCode, a.phone, " +
                "ci.city, ci.countryId, co.country " +
                "from customer cx " +
                "join address a on cx.addressId = a.addressId " +
                "join city ci on a.cityId = ci.cityId " +
                "join country co on ci.countryID = co.countryID;";
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(customerQuery);
        try {
            //Iterate through results to create customers and add to list
            while (rs.next()) {
                //Initialize a new Customer object
                Customer cx = new Customer();

                //Update variables with info for next customer/address/city/country record in DB
                cx.setCustomerID(rs.getInt("customerId"));
                cx.setCustomerName(rs.getString("customerName"));
                cx.setAddressID(rs.getInt("addressID"));
                //TINYINT returns boolean values, convert to Yes/No for easier readability
                if (rs.getBoolean("active")) {
                    cx.setActive("Yes");
                } else {
                    cx.setActive("No");
                }
                cx.setAddress(rs.getString("address"));
                cx.setAddress2(rs.getString("address2"));
                cx.setCityID(rs.getInt("cityId"));
                cx.setPostalCode(rs.getString("postalCode"));
                cx.setPhone(rs.getString("phone"));
                cx.setCity(rs.getString("city"));
                cx.setCountryID(rs.getInt("countryId"));
                cx.setCountry(rs.getString("country"));
                //Add the Customer object to the cxList
                cxList.add(cx);
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Return the newly built ObservableList
        return cxList;
    }

    //Create method to query the appointment table to build Appointment objects for a TableView based on selected filters
    public ObservableList<Appointment> buildAppointmentList(String user, ZoneId timeZone, boolean onlyUsersAppts, boolean futureOnly) {
        //Initialize an Observable list to hold all appointments returned
        ObservableList<Appointment> apptList = FXCollections.observableArrayList();
        //Initialize a String to hold the query statement that will be used on the appointment table
        String apptQuery;
        //Four possible combinations of passed booleans, determine which one applies
        if (onlyUsersAppts && futureOnly) {
            apptQuery = "SELECT * FROM appointment WHERE contact = '" + user + "' and start > NOW();";
        } else if (!onlyUsersAppts && futureOnly) {
            apptQuery = "SELECT * FROM appointment WHERE start > NOW();";
        } else if (onlyUsersAppts && !futureOnly) {
            apptQuery = "SELECT * FROM appointment WHERE contact = '" + user + "';";
        } else {
            apptQuery = "SELECT * FROM appointment;";
        }
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(apptQuery);
        try {
            //Iterate through results to create Appointment objects and add them to the list
            while (rs.next()) {
                //Initialize a new Appointment object
                Appointment appt = new Appointment();
                //Update appointment variables with info pulled from DB
                appt.setAppointmentId(rs.getInt("appointmentId"));
                appt.setCustomerId(rs.getInt("customerId"));
                appt.setTitle(rs.getString("title"));
                appt.setDescription(rs.getString("description"));
                appt.setLocation(rs.getString("location"));
                appt.setContact(rs.getString("contact"));
                appt.setUrl(rs.getString("url"));
                appt.setFullCustomer(appt.getCustomerId());
                //Get the current start and end time in UTC from the DB
                ZonedDateTime startUTC = ZonedDateTime.ofInstant(rs.getTimestamp("start").toInstant(), UTC);
                ZonedDateTime endUTC = ZonedDateTime.ofInstant(rs.getTimestamp("end").toInstant(), UTC);
                //Convert start and end to the user's timezone and set the variables on apptstart
                ZonedDateTime startLocalTZ = startUTC.withZoneSameInstant(timeZone);
                ZonedDateTime endLocalTZ = endUTC.withZoneSameInstant(timeZone);
                appt.setStart(startLocalTZ);
                appt.setEnd(endLocalTZ);
                //Add the Appointment object to the apptList
                apptList.add(appt);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Return the newly built ObservableList
        return apptList;
    }

    public ObservableList<String>  buildApptCxList() {
        //Initialize an ObservableList to hold all customer IDs and names returned
        ObservableList<String> cxList = FXCollections.observableArrayList();
        //Initialize a String to hold the db query
        String cxQuery = "SELECT customerId, customerName FROM customer";
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(cxQuery);
        try {
            //Iterate through results to add customers to the list
            while (rs.next()) {
                int cxID = rs.getInt(1);
                String cxName = rs.getString(2);
                String customer = cxID + " - " + cxName;
                cxList.addAll(customer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cxList;

    }

    public ObservableList<String> buildUserList() {
        //Initialize an ObservableList to hold all active usernames returned
        ObservableList<String> userList = FXCollections.observableArrayList();
        //Initialize a String to hold the db query, looking for all users currently active in system
        String userQuery = "SELECT userName FROM user WHERE active = '1'";
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(userQuery);
        try {
            //Iterate through results to add usernames to the list
            while (rs.next()) {
                String user = rs.getString(1);
                userList.addAll(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Return the newly built ObservableList
        return userList;
    }

    //Create method to compute the customer ID - name String for loading a customer choice in the Modify Appointment screen
    public String getApptCxName(int ID) {
        //Initialize Strings to hold the result and the query
        String customer = null;
        String cxQuery = "SELECT customerName FROM customer WHERE customerId = '" + ID + "';";
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(cxQuery);
        try {
            while (rs.next()) {
                customer = ID + " - " + rs.getString(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return customer;
    }

    //Create method to build and return a customer object when given the ID
    public Customer getCustomerFromID(int ID) {
        //Initialize a new Customer object
        Customer cx = new Customer();
        //Create String to hold query statement
        String cxQuery = "select cx.customerId, cx.customerName, cx.addressId, cx.active, a.address, a.address2, a.cityId, a.postalCode, a.phone, " +
                "ci.city, ci.countryId, co.country " +
                "from customer cx " +
                "join address a on cx.addressId = a.addressId " +
                "join city ci on a.cityId = ci.cityId " +
                "join country co on ci.countryID = co.countryID " +
                "where cx.customerId = '" + ID + "';";
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(cxQuery);
        try {
            while (rs.next()) {
                //Update variables with info for the customer/address/city/country record in DB
                cx.setCustomerID(rs.getInt("customerId"));
                cx.setCustomerName(rs.getString("customerName"));
                cx.setAddressID(rs.getInt("addressID"));
                //TINYINT returns boolean values, convert to Yes/No for easier readability
                if (rs.getBoolean("active")) {
                    cx.setActive("Yes");
                } else {
                    cx.setActive("No");
                }
                cx.setAddress(rs.getString("address"));
                cx.setAddress2(rs.getString("address2"));
                cx.setCityID(rs.getInt("cityId"));
                cx.setPostalCode(rs.getString("postalCode"));
                cx.setPhone(rs.getString("phone"));
                cx.setCity(rs.getString("city"));
                cx.setCountryID(rs.getInt("countryId"));
                cx.setCountry(rs.getString("country"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cx;
    }

    //Create a method to pull all appointments scheduled for an agent and compare them to a given time period to make sure there are no overlaps
    public boolean checkForOverlaps(String user, ZonedDateTime apptStart, ZonedDateTime apptEnd, int apptId) {
        //Initialize a boolean to hold result
        boolean isOverlapping = false;
        //Initialize a String to hold the query for the appointment table
        String apptQuery;
        //First, convert start and end to UTC for comparison with DB times
        ZonedDateTime start = apptStart.withZoneSameInstant(UTC);
        ZonedDateTime end = apptEnd.withZoneSameInstant(UTC);
        //To save time, we will only look at appointments on the same date as the one being added, in the event the UTC conversion splits the appointment
        //between two dates then the query will need to search for both days
        String startDate = start.toString();
        String endDate = end.toString();
        //If the dates match, create a query with this date
        if (startDate.equals(endDate)) {
            String date = startDate.substring(0,startDate.indexOf("T"));
            apptQuery = "SELECT * FROM appointment WHERE DATE(start) LIKE '" + date + "%' OR DATE(end) LIKE '" + date + "%';";
        } else {
            String date1 = startDate.substring(0,startDate.indexOf("T"));
            String date2 = endDate.substring(0,endDate.indexOf("T"));
            apptQuery = "SELECT * FROM appointment WHERE DATE(start) LIKE '" + date1 + "%' OR DATE(start) LIKE '" + date2 + "%' " +
                    "OR DATE(end) LIKE '" + date2 + "%' OR DATE(end) LIKE '" + date2 + "%';";
        }
        //Execute the query then load the results into the ResultSet
        ResultSet rs = executeQuery(apptQuery);
        try {
            while (rs.next()) {
                //Get the DB start and end for the next appointment, convert them to Instants for comparison
                Instant startIns = rs.getTimestamp("start").toInstant();
                Instant endIns = rs.getTimestamp("end").toInstant();
                //Check to see if the new appointment overlaps any existing one - overlap is true if start is before DB start and end is after DB start
                //or if start is after DB start and DB end is before end or if start and end are an exact match to the DB values
                if ((start.toInstant().isBefore(startIns) && end.toInstant().isAfter(startIns)) ||
                        (start.toInstant().isAfter(startIns) && endIns.isBefore(end.toInstant())) ||
                        start.toInstant().equals(startIns) || end.toInstant().equals(endIns)){
                    //Confirm that the overlap is not from the appointment currently being modified before setting the overlap flag
                    if (apptId != rs.getInt("appointmentId")) {
                        isOverlapping = true;
                        //Break out of the while loop, finding one is enough to require the user pick a different time
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isOverlapping;
    }
}
