/**
 * Created by Brittany Ward for WGU class C482 - Software I  - Inventory Management Program.
 * Last edited 05/24/27
 */

package Inv_Mgmt_Sys;

import java.util.ArrayList;

public class Inventory {
    ArrayList<Product> products = new ArrayList<Product>();
    ArrayList<Part> allParts = new ArrayList<Part>();

    public void addProduct(Product prod) {
        products.add(prod);
    }

    public boolean removeProduct (int index) {
        boolean removed = false;

        try {
            products.remove(index);
            removed = true;
        } catch (IndexOutOfBoundsException e) {
            removed = false;
        }

        return removed;
    }

    public Product lookupProduct (int ID) {
        Product prod;

        try {
            prod = products.get(ID);
        } catch (IndexOutOfBoundsException e) {
            prod = null;
        }

        return prod;
    }

    public void updateProduct (int ID) {
        Product prod = products.get(ID);

    }

    public void addPart (Part part) {
        allParts.add(part);
    }

    public boolean deletePart (Part part){
        boolean removed = false;
        for (int i = 0; i < allParts.size(); i++) {
            if (part.equals(allParts.get(i))) {
                allParts.remove(i);
                removed = true;
            }
        }
        return removed;
    }

    public Part lookupPart (int ID) {
        Part part;

        try {
            part = allParts.get(ID);
        } catch (IndexOutOfBoundsException e) {
            part = null;
        }

        return part;
    }

    public void updatePart (int ID) {
        Part part = allParts.get(ID);
    }


    //This method allows you to find the Inventory index number using a product's ID number
    public int findIndexFromID(int ID) {
        boolean found = false;
        int index = 0;

        for (int i = 0; i < products.size() && !found;)
        {
            int testCase = products.get(i).getProductID();
            if (testCase == ID)
            {
                index = i;
                found = true;
            }
            else
            {
                i++;
            }
        }

        if (found)
        {
            return index;
        }
        else
        {
            System.out.println("Product not found.");
            return -1;
        }
    }







}
