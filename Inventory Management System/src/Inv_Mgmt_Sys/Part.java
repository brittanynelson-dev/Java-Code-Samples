/**
 * Created by Brittany Ward for WGU class C482 - Software I  - Inventory Management Program.
 * Last edited 05/24/27
 */

package Inv_Mgmt_Sys;

public abstract class Part {
    private int partID;
    private String name;
    private double price;
    private int inStock;
    private int min;
    private int max;

    public Part(int ID, String name, double price, int inv, int min, int max){
        this.partID = ID;
        this.name = name;
        this.price = price;
        this.inStock = inv;
        this.min = min;
        this.max = max;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return this.price;
    }

    public void setInStock(int inv) {
        this.inStock = inv;
    }

    public int getInStock() {
        return this.inStock;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMin() {
        return this.min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return this.max;
    }

    public void setPartID(int ID) {
        this.partID = ID;
    }

    public int getPartID() {
        return this.partID;
    }

}
