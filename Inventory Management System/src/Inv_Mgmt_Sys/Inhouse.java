package Inv_Mgmt_Sys;

/**
 * Created by Brittany Ward for WGU class C482 - Software I  - Inventory Management Program.
 * Last edited 05/24/27
 */

public class Inhouse extends Part {

    private int machineID;

    public Inhouse(int ID, String name, double price, int inv, int min, int max, int machID) {
        super(ID, name, price, inv, min, max);
        this.machineID = machID;
    }

    public void setMachineID(int ID) {
        this.machineID = ID;
    }

    public int getMachineID() {
        return this.machineID;
    }
}
