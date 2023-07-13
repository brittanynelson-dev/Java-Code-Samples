package Inv_Mgmt_Sys;

/**
 * Created by Brittany Ward for WGU class C482 - Software I - Inventory Management Program.
 * Last edited 05/24/27
 */
public class Outsourced extends Part {
    private String companyName;

    public Outsourced (int ID, String name, double price, int inv, int min, int max, String comp) {
        super(ID, name, price, inv, min, max);
        this.companyName = comp;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String comp) {
        this.companyName = comp;
    }
}
