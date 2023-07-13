package SchedulingSoftware;

import javafx.beans.property.*;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class Appointment {
    //Declare appointment table columns as variables
    private IntegerProperty appointmentId;
    private IntegerProperty customerId;
    private StringProperty title;
    private StringProperty description;
    private StringProperty location;
    private StringProperty contact;
    private StringProperty url;
    private SimpleObjectProperty<ZonedDateTime> start;
    private SimpleObjectProperty<ZonedDateTime> end;
    //Create a variable to return customer ID and name from customer table
    private StringProperty fullCustomer;
    //Create variables to hold user friendly formatted ZonedDateTime properties as strings
    private StringProperty startStr;
    private StringProperty endStr;
    //Create variables to hold date as well as start and end hours and minutes as Strings -- this is only used when temporarily saving appointment form info
    private String savedCx;
    private String savedDate;
    private String savedStartHour;
    private String savedStartMin;
    private String savedEndHour;
    private String savedEndMin;
    //Set a boolean flag to determine whether the appointment has saved data or now
    private boolean hasSavedData = false;

    //Constructor statement, initialize all variables
    public Appointment() {
        this.appointmentId = new SimpleIntegerProperty();
        this.customerId = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.contact = new SimpleStringProperty();
        this.url = new SimpleStringProperty();
        this.start = new SimpleObjectProperty<>();
        this.end = new SimpleObjectProperty<>();
        this.fullCustomer = new SimpleStringProperty();
        this.startStr = new SimpleStringProperty();
        this.endStr = new SimpleStringProperty();
    }

    //Get a reference to the DBInterface for use in this class
    DBInterface dbConnect = new DBInterface();

    //Define methods to set and return the fullCustomer property
    public StringProperty fullCustomerProperty() {
        return fullCustomer;
    }
    public void setFullCustomer(int id) {
        //Initialize a String to hold the customer info
        String fullCxInfo = null;
        //Prepare a query statement and execute it
        String cxNameQuery = "SELECT customerName FROM customer WHERE customerId = '" + id + "'";
        ResultSet rs = dbConnect.executeQuery(cxNameQuery);
        try {
            while (rs.next()) {
                fullCxInfo = this.getCustomerId() + " - " + rs.getString(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.fullCustomer.set(fullCxInfo);
    }

    //Define methods to set and return the date/time String properties for saving form info
    public void setSavedStrings(String cx, String date, String startHour, String startMin, String endHour, String endMin) {
        this.savedCx = cx;
        this.savedDate = date;
        this.savedStartHour = startHour;
        this.savedStartMin = startMin;
        this.savedEndHour = endHour;
        this.savedEndMin = endMin;
    }

    public String getSavedCx() {
        return savedCx;
    }
    public String getSavedDate() {
        return savedDate;
    }
    public String getSavedStartHour() {
        return savedStartHour;
    }
    public String getSavedStartMin() {
        return savedStartMin;
    }
    public String getSavedEndHour() {
        return savedEndHour;
    }
    public String getSavedEndMin() {
        return savedEndMin;
    }

    //Create a getter and setter method for the hasSavedData boolean
    public boolean getHasSavedData() {
        return hasSavedData;
    }
    public void setHasSavedData(boolean bool) {
        this.hasSavedData = bool;
    }

    //Create get and set methods for all variables mapped to db columns
    //appointmentId
    public int getAppointmentID() {
        return appointmentId.get();
    }
    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }
    public void setAppointmentId(int id) {
        this.appointmentId.set(id);
    }
    //customerId
    public int getCustomerId() {
        return customerId.get();
    }
    public IntegerProperty customerIdProperty() {
        return customerId;
    }
    public void setCustomerId(int id) {
        this.customerId.set(id);
    }
    //title
    public String getTitle() {
        return title.get();
    }
    public StringProperty titleProperty() {
        return title;
    }
    public void setTitle(String newTitle) {
        this.title.set(newTitle);
    }
    //description
    public String getDescription() {
        return description.get();
    }
    public StringProperty descriptionProperty() {
        return description;
    }
    public void setDescription(String desc) {
        this.description.set(desc);
    }
    //location
    public String getLocation() {
        return  location.get();
    }
    public StringProperty locationProperty() {
        return location;
    }
    public void setLocation(String loc) {
        this.location.set(loc);
    }
    //contact
    public String getContact() {
        return contact.get();
    }
    public StringProperty contactProperty() {
        return contact;
    }
    public void setContact(String newContact) {
        this.contact.set(newContact);
    }
    //url
    public String getUrl() {
        return url.get();
    }
    public StringProperty urlProperty() {
        return url;
    }
    public void setUrl(String newUrl) {
        this.url.set(newUrl);
    }
    //start
    public ZonedDateTime getStart() {
        return start.get();
    }
    public StringProperty startProperty() {
        return startStr;
    }
    public void setStart(ZonedDateTime date) {
        this.start.set(date);
        String formattedStart = formatDateTime(date);
        this.startStr.set(formattedStart);
    }
    //end
    public ZonedDateTime getEnd() {
        return end.get();
    }
    public StringProperty endProperty() {
        return endStr;
    }
    public void setEnd(ZonedDateTime date) {
        this.end.set(date);
        String formattedEnd = formatDateTime(date);
        this.endStr.set(formattedEnd);
    }

    public String formatDateTime(ZonedDateTime date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = dtf.format(date);
        return formattedDateTime;
    }
}
