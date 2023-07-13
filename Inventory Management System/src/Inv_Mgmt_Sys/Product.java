/**
 * Created by Brittany Ward for WGU class C482 - Software I  - Inventory Management Program.
 * Last edited 05/24/27
 */

package Inv_Mgmt_Sys;

import java.util.ArrayList;

public class Product {
    private ArrayList<Part> associatedParts;
    private int productID;
    private String name;
    private double price;
    private int inStock;
    private int min;
    private int max;

    public Product(int pID, String name, double price, int inv, int min, int max) {
        this.associatedParts = new ArrayList<Part>();
        this.productID = pID;
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

    public void setPrice(double price) {this.price = price;}

    public double getPrice() {return this.price;}

    public void setInStock(int iLvl) {
        this.inStock = iLvl;
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

    public void addAssociatedPart(Part part) {
        this.associatedParts.add(part);
    }

    public boolean removeAssociatedPart(int ID) {
        boolean removed;

        try {
            associatedParts.remove(ID);
            removed = true;
        } catch (IndexOutOfBoundsException e) {
            removed = false;
        }

        return removed;
    }

    public Part lookupAssociatedPart(int ID) {
        Part part;
        try {
            part = associatedParts.get(ID);
        } catch (IndexOutOfBoundsException e) {
            part = null;
        }

        return part;
    }

    public void setProductID(int pID) {
        this.productID = pID;
    }

    public int getProductID() {
        return this.productID;
    }


}

