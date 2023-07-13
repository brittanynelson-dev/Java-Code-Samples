package SchedulingSoftware;

import javafx.beans.property.*;
import java.util.Date;

public class Customer {
    //Declare Customer table columns as variables
    private IntegerProperty customerID;
    private StringProperty customerName;
    private IntegerProperty addressID;
    private StringProperty active;
    //Declare Address table columns as variables, addressID will be reused
    private StringProperty address;
    private StringProperty address2;
    private IntegerProperty cityID;
    private StringProperty postalCode;
    private StringProperty phone;
    //Declare City table columns as variables, cityID will be reused
    private StringProperty city;
    private IntegerProperty countryID;
    //Declare Country table column as variable, countryID will be reused
    private StringProperty country;
    //Declare a variable to hold the full street, city, state/province, and postal code for TableVew display
    private StringProperty fullAddress;


    //Constructor statement, initialize all variables
    public Customer () {
        this.customerID = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty();
        this.addressID = new SimpleIntegerProperty();
        this.active = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.address2 = new SimpleStringProperty();
        this.cityID = new SimpleIntegerProperty();
        this.postalCode = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.city = new SimpleStringProperty();
        this.countryID = new SimpleIntegerProperty();
        this.country = new SimpleStringProperty();
        this.fullAddress = new SimpleStringProperty();
    }

    //Create a method to build and return the fullAddress variable
    public StringProperty fullAddressProperty() {
        String fullAddrStr = this.getAddress() + ", " + this.getCity() + ", " + this.getAddress2() + " " + this.getPostalCode();
        this.fullAddress.set(fullAddrStr);
        return fullAddress;
    }

    //Create get and set methods for all variables mapped to db columns
    //customerID
    public int getCustomerID() {
        return customerID.get();
    }
    public IntegerProperty customerIDProperty(){
        return customerID;
    }
    public void setCustomerID(int id) {
        this.customerID.set(id);
    }
    //customerName
    public String getCustomerName() {
        return customerName.get();
    }
    public StringProperty customerNameProperty() {
        return customerName;
    }
    public void setCustomerName(String name) {
        this.customerName.set(name);
    }
    //addressID
    public int getAddressID() {
        return addressID.get();
    }
    public IntegerProperty addressIDProperty() {
        return addressID;
    }
    public void setAddressID(int id) {
        this.addressID.set(id);
    }
    //active
    public String getActive() {
        return active.get();
    }
    public StringProperty activeProperty() {
        return active;
    }
    public void setActive (String active) {
        this.active.set(active);
    }
    //address
    public String getAddress() {
        return address.get();
    }
    public StringProperty addressProperty() {
        return address;
    }
    public void setAddress(String addr) {
        this.address.set(addr);
    }
    //address2
    public String getAddress2() {
        return address2.get();
    }
    public StringProperty address2Property() {
        return address2;
    }
    public void setAddress2(String addr) {
        this.address2.set(addr);
    }
    //cityID
    public int getCityID() {
        return cityID.get();
    }
    public IntegerProperty cityIDProperty() {
        return cityID;
    }
    public void setCityID(int id) {
        this.cityID.set(id);
    }
    //postalCode
    public String getPostalCode() {
        return postalCode.get();
    }
    public StringProperty postalCodeProperty() {
        return postalCode;
    }
    public void setPostalCode(String code) {
        this.postalCode.set(code);
    }
    //phone
    public String getPhone() {
        return phone.get();
    }
    public StringProperty phoneProperty() {
        return phone;
    }
    public void setPhone(String number) {
        this.phone.set(number);
    }
    //city
    public String getCity() {
        return city.get();
    }
    public StringProperty cityProperty() {
        return city;
    }
    public void setCity(String newCity) {
        this.city.set(newCity);
    }
    //countryID
    public int getCountryID() {
        return countryID.get();
    }
    public IntegerProperty countryIDProperty() {
        return countryID;
    }
    public void setCountryID(int id) {
        this.countryID.set(id);
    }
    //country
    public String getCountry() {
        return country.get();
    }
    public StringProperty countryProperty() {
        return country;
    }
    public void setCountry(String newCountry) {
        this.country.set(newCountry);
    }
}
