/**
 * Created by Brittany Ward for WGU class C482 - Software I - Inventory Management Program.
 * Last edited 05/24/27
 */

package Inv_Mgmt_Sys;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.geometry.Insets;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Set up a new Group to contain all other UI Layout and objects
        Group root = new Group();

        //Construct main scene and link stylesheet
        Scene main = new Scene(root, 1600, 600, Color.LIGHTGRAY);
        main.getStylesheets().add("Inv_Mgmt_Sys/MainStyleSheet.css");

        //Keep default focus on root to avoid forced focus on TextFields or TableView upon opening the window
        root.requestFocus();


/********************************************************************************************************************
 * Set up an AnchorPane to hold the content of the main screen. Add labels and buttons to it.                       *
 *******************************************************************************************************************/
        //Set up an AnchorPane to manage content on the screen
        AnchorPane ap = new AnchorPane();
        ap.setPrefSize(1600, 600);

        //Create a label for the program title
        Label topLabel = CreateScreenLabel("Inventory Management System");
        //Setting the topLabel location on screen
        topLabel.setTranslateX(50);
        topLabel.setTranslateY(50);

        //Creating a Button object for the Exit button
        Button exitBtn = new Button("Exit");
        exitBtn.setMinSize(140, 45);
        //Setting the exitBtn location on the screen
        exitBtn.setTranslateX(1375);
        exitBtn.setTranslateY(530);
        //Add code so the Exit button will close the program when clicked
        exitBtn.setOnMouseClicked(e -> {
            primaryStage.close();
        });

        //Create the rectangles
        Rectangle rect = CreateRectangle();
        Rectangle rectR = CreateRectangle();
        //Move rect to the correct position
        rect.setX(50);
        rect.setY(125);
        //Move rectR to the correct position
        rectR.setX(825);
        rectR.setY(125);

        //Label for the Parts table
        Label partLabel = new Label("Parts");
        partLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 24));
        partLabel.setTranslateX(85);
        partLabel.setTranslateY(160);

        //Label for the Products table
        Label prodLabel = new Label("Products");
        prodLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 24));
        prodLabel.setTranslateX(875);
        prodLabel.setTranslateY(160);

        //Create the Parts Search button
        Button partSearchBtn = CreateButton("Search");
        partSearchBtn.setTranslateX(415);
        partSearchBtn.setTranslateY(155);

        //Create the Parts Search TextField
        TextField partSearch = new TextField();
        partSearch.setPrefSize(200, 10);
        partSearch.setTranslateX(535);
        partSearch.setTranslateY(160);

        //Create the Product Search button
        Button prodSearchBtn = CreateButton("Search");
        prodSearchBtn.setTranslateX(1200);
        prodSearchBtn.setTranslateY(155);

        //Create the Product Search TextField
        TextField prodSearch = new TextField();
        prodSearch.setPrefSize(200, 10);
        prodSearch.setTranslateX(1320);
        prodSearch.setTranslateY(160);

        //Create the Parts Add button
        Button partAddBtn = CreateButton("Add");
        partAddBtn.setTranslateX(335);
        partAddBtn.setTranslateY(390);

        //Create the Parts Modify button
        Button partModBtn = CreateButton("Modify");
        partModBtn.setTranslateX(455);
        partModBtn.setTranslateY(390);

        //Create the Parts Delete button
        Button partDelBtn = CreateButton("Delete");
        partDelBtn.setTranslateX(575);
        partDelBtn.setTranslateY(390);

        //Create the Product Add button
        Button prodAddBtn = CreateButton("Add");
        prodAddBtn.setTranslateX(1135);
        prodAddBtn.setTranslateY(390);

        //Create the Product Modify button
        Button prodModBtn = CreateButton("Modify");
        prodModBtn.setTranslateX(1255);
        prodModBtn.setTranslateY(390);

        //Create the Product Delete button
        Button prodDelBtn = CreateButton("Delete");
        prodDelBtn.setTranslateX(1375);
        prodDelBtn.setTranslateY(390);


        //Add all current items to the AnchorPane
        ap.getChildren().addAll(rect, rectR, topLabel, exitBtn, partLabel, prodLabel, partSearch, partSearchBtn,
                prodSearchBtn, prodSearch, partAddBtn, partModBtn, partDelBtn, prodAddBtn, prodModBtn, prodDelBtn);

        //Add the AnchorPane to root
        root.getChildren().addAll(ap);

        //Link the main scene to the stage and display it
        primaryStage.setScene(main);
        primaryStage.show();


/********************************************************************************************************************
 * Create and configure the Parts Table.                                                                            *
 *******************************************************************************************************************/

        //Create a new Inventory object to hold parts and products for the program
        Inventory currentInv = new Inventory();
        //Add some default parts to the Inventory
        currentInv.addPart(new Inhouse(getNextPartID(), "Part 1", 5.00, 5, 0, 10, 123));
        currentInv.addPart(new Outsourced(getNextPartID(), "Part 2", 10.00, 10, 5, 20, "Company A"));
        currentInv.addPart(new Inhouse(getNextPartID(), "Part 3", 15.00, 12, 10, 50, 456));
        currentInv.addPart(new Outsourced(getNextPartID(), "Part 4", 18.00, 16, 5, 25, "Company B"));

        //Create a new ArrayList for the Parts table
        final ObservableList<Part> partData = FXCollections.observableArrayList();

        //Get parts from the currentInv Inventory object and add them to the partData Observable Array List
        int i = 0;
        Part lastPart;
        do {
            lastPart = currentInv.lookupPart(i);
            if (lastPart != null) {
                partData.add(lastPart);
            }
            i++;
        } while (lastPart != null);

        //Create a TableView to hold the partData on screen
        TableView<Part> partTable = new TableView<Part>(partData);

        //Set the size of the table and restrict it to the four columns created
        partTable.setPrefSize(660,154);
        partTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Allow table to be edited after creation
        partTable.setEditable(true);

        //Add columns
        TableColumn partIDCol = new TableColumn("Part ID");
        partIDCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("partID"));

        TableColumn partNameCol = new TableColumn("Part Name");
        partNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));

        TableColumn partInvCol = new TableColumn("Inventory Level");
        partInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("inStock"));

        TableColumn partPriceCol = new TableColumn ("Price/Cost Per Unit");
        partPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        //Add the data and columns to the table
        partTable.setItems(partData);
        partTable.getColumns().addAll(partIDCol, partNameCol, partInvCol, partPriceCol);

        //Position the partTable on the screen
        partTable.setTranslateX(80);
        partTable.setTranslateY(210);

        //Add the parts table to the AnchorPane
        ap.getChildren().addAll(partTable);


        //Create a confirmation message to be displayed if a user clicks the delete button for a part
        Text delPartConf = CreateErrMsg("Are you sure you would like to permanently delete this part?");
        delPartConf.setTranslateX(300);
        delPartConf.setTranslateY(450);

        //Add a confirmation "Yes" button to be displayed when a user clicks the delete button
        Button yesDelPart = CreateConfButton("Yes");
        yesDelPart.setTranslateX(565);
        yesDelPart.setTranslateY(460);

        //Add a confirmation "No" button to be displayed when a user clicks the delete button
        Button noDelPart = CreateConfButton("No");
        noDelPart.setTranslateX(635);
        noDelPart.setTranslateY(460);


        //Add delete button logic to remove a selected part from the list
        partDelBtn.setOnMouseClicked(e -> {
            Part selectedPart = partTable.getSelectionModel().getSelectedItem();
            //Verify a part is selected, otherwise do nothing
            if (selectedPart == null) {
                return;
            }

            //Display confirmation dialogue and buttons to verify this is what the user intends to do
            ap.getChildren().addAll(delPartConf, yesDelPart, noDelPart);

            //If the Yes button is clicked remove confirmation dialogue and delete the part
            yesDelPart.setOnMouseClicked(e2 -> {
                ap.getChildren().removeAll(delPartConf, yesDelPart, noDelPart);
                currentInv.deletePart(selectedPart);
                partTable.getItems().remove(selectedPart);
            });

            //If the No button is clicked remove confirmation dialogue but do not delete the part
            noDelPart.setOnMouseClicked(e3 -> {
                ap.getChildren().removeAll(delPartConf, yesDelPart, noDelPart);
            });

        });

        //Create a new ObservableArrayList to hold the matching entries from the PartData list for searching
        ObservableList<Part> filteredPartData = FXCollections.observableArrayList();

        //Create Part Search button functionality
        partSearchBtn.setOnMouseClicked(e -> {
            //Get data entered in the partSearch text field
            String partSearchData = partSearch.getText().toLowerCase();

            //Clear the filtered list to prepare for the next search
            filteredPartData.clear();

            //If the part search text field is searched blank, make sure we are no longer viewing a filtered list
            if (partSearch.getText().isEmpty() || partSearch.getText() == null) {
                partTable.setItems(partData);
                return;
            }

            //Build the filtered list to be displayed in the table
            for (int j = 0; j < partData.size(); j++) {
                if (partData.get(j).getName().toLowerCase().contains(partSearchData)) {
                    filteredPartData.add(partData.get(j));
                }
            }

            //Set the filtered list to display in the partTable
            partTable.setItems(filteredPartData);

        });


/********************************************************************************************************************
 * Create and configure the Products Table.                                                                         *
 *******************************************************************************************************************/

        //Populate the product table with some default data
        currentInv.addProduct(new Product(getNextProdID(), "Product 1", 5.00, 5, 1, 20));
        currentInv.addProduct(new Product(getNextProdID(), "Product 2", 10.00, 10, 1, 30));
        currentInv.addProduct(new Product(getNextProdID(), "Product 3", 15.00, 15, 15, 35));
        currentInv.addProduct(new Product(getNextProdID(), "Product 4", 18.00, 16, 0, 20));

        //Add a part to each product
        currentInv.lookupProduct(0).addAssociatedPart(partData.get(0));
        currentInv.lookupProduct(1).addAssociatedPart(partData.get(1));
        currentInv.lookupProduct(2).addAssociatedPart(partData.get(2));
        currentInv.lookupProduct(3).addAssociatedPart(partData.get(3));

        //Create a new ArrayList for the Products table
        final ObservableList<Product> prodData = FXCollections.observableArrayList();

        //Get products from the currentInv Inventory object and add them to the prodData Observable Array List
        int k = 0;
        Product lastProd;
        do {
            lastProd = currentInv.lookupProduct(k);
            if (lastProd != null) {
                prodData.add(lastProd);
            }
            k++;
        } while (lastProd != null);


        //Create a TableView to hold the prodData on the screen
        TableView<Product> prodTable = new TableView<Product>(prodData);

        //Set the size of the table and restrict it to the four columns created
        prodTable.setPrefSize(660,154);
        prodTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Allow table to be edited after creation
        prodTable.setEditable(true);

        //Add columns
        TableColumn prodIDCol = new TableColumn("Product ID");
        prodIDCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("productID"));

        TableColumn prodNameCol = new TableColumn("Product Name");
        prodNameCol.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));

        TableColumn prodInvCol = new TableColumn("Inventory Level");
        prodInvCol.setCellValueFactory(new PropertyValueFactory<Product, Integer>("inStock"));

        TableColumn prodPriceCol = new TableColumn ("Price/Cost Per Unit");
        prodPriceCol.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));

        //Add the data and columns to the table
        prodTable.setItems(prodData);
        prodTable.getColumns().addAll(prodIDCol, prodNameCol, prodInvCol, prodPriceCol);

        //Position the partTable on the screen
        prodTable.setTranslateX(865);
        prodTable.setTranslateY(210);

        //Add the product table to the AnchorPane
        ap.getChildren().addAll(prodTable);

        //Create an error message to be displayed if a product has parts assigned to it when a user attempts to delete it
        Text prodHasParts = CreateErrMsg("This product cannot be deleted while there are parts assigned to it.");
        prodHasParts.setTranslateX(1060);
        prodHasParts.setTranslateY(460);

        //Create a confirmation message to be displayed if a user clicks the delete button for a product
        Text delProdConf = CreateErrMsg("Are you sure you would like to permanently delete this product?");
        delProdConf.setTranslateX(1080);
        delProdConf.setTranslateY(450);

        //Add a confirmation "Yes" button to be displayed when a user clicks the delete button
        Button yesDelProd = CreateConfButton("Yes");
        yesDelProd.setTranslateX(1370);
        yesDelProd.setTranslateY(460);

        //Add a confirmation "No" button to be displayed when a user clicks the delete button
        Button noDelProd = CreateConfButton("No");
        noDelProd.setTranslateX(1440);
        noDelProd.setTranslateY(460);

        //Add delete button logic to remove a selected product from the list
        prodDelBtn.setOnMouseClicked(e -> {
            Product selectedProd = prodTable.getSelectionModel().getSelectedItem();
            //Verify there is a product selected, otherwise do nothing
            if (selectedProd == null) {
                return;
            }

            //Display confirmation dialogue and buttons to verify this is what the user intends to do
            ap.getChildren().addAll(delProdConf, yesDelProd, noDelProd);

            //If the Yes button is clicked remove confirmation dialogue and delete the product if it has no parts
            yesDelProd.setOnMouseClicked(e2 -> {
                ap.getChildren().removeAll(delProdConf, yesDelProd, noDelProd);
                //Verify the product does not currently have any parts assigned to it
                ap.getChildren().removeAll(prodHasParts); //Remove in case it is still on screen from previous check
                if (selectedProd.lookupAssociatedPart(0) != null) {
                    ap.getChildren().addAll(prodHasParts);
                    return;
                }

                //Delete the product if it has passed all other checks
                int removedProdID = selectedProd.getProductID();
                int removedProdIndex = currentInv.findIndexFromID(removedProdID);
                currentInv.removeProduct(removedProdIndex);
                prodTable.getItems().remove(selectedProd);

            });

            //If the No button is clicked remove confirmation dialogue but do not delete the product
            noDelProd.setOnMouseClicked(e3 -> {
                ap.getChildren().removeAll(delProdConf, yesDelProd, noDelProd);
            });
        });

        //Create a new ObservableArrayList to hold the matching entries from the PartData list
        ObservableList<Product> filteredProdData = FXCollections.observableArrayList();

        //Create Product Search button functionality
        prodSearchBtn.setOnMouseClicked(e -> {
            //Get data entered in the prodSearch text field
            String prodSearchData = prodSearch.getText().toLowerCase();

            //If the prod search text field is searched blank, make sure we are no longer viewing a filtered list
            if (prodSearch.getText().isEmpty() || prodSearch.getText() == null) {
                prodTable.setItems(prodData);
                return;
            }

            //Clear the filtered list to prepare for the next search
            filteredProdData.clear();

            //Build the filtered list to be displayed in the table
            for (int j = 0; j < prodData.size(); j++) {
                if (prodData.get(j).getName().toLowerCase().contains(prodSearchData)) {
                    filteredProdData.add(prodData.get(j));
                }
            }

            //Set the filtered list to display in the partTable
            prodTable.setItems(filteredProdData);

        });



/********************************************************************************************************************
 * Create and configure the Add Parts screen                                                                        *
 *******************************************************************************************************************/

        //Create a new AnchorPane to hold this window's content
        AnchorPane addPartAP = new AnchorPane();
        addPartAP.setPrefSize(750, 600);
        addPartAP.setId("PartAP");

        //Create a new scene for this window and link the main style sheet to it
        Scene addPart = new Scene(addPartAP, 750, 600);
        addPart.getStylesheets().add("Inv_Mgmt_Sys/MainStyleSheet.css");


        //Create a label for the screen title
        Label addPartLabel = CreateScreenLabel("Add Part");
        //Setting the Add Part Label location on screen
        addPartLabel.setTranslateX(40);
        addPartLabel.setTranslateY(25);

        //Create a new ToggleGroup to manage Radio Buttons for In-House and Outsourced part options
        final ToggleGroup partGroup = new ToggleGroup();

        //Create a Radio Button for Inhouse Parts
        RadioButton partInBtn = new RadioButton();
        partInBtn.setToggleGroup(partGroup);
        partInBtn.setSelected(true);
        partInBtn.setTranslateX(240);
        partInBtn.setTranslateY(30);
        //Add a label to the newly created button
        Label partInLbl = CreatePartLabel("In-House");
        partInLbl.setTranslateX(295);
        partInLbl.setTranslateY(30);

        //Create a Radio Button for Outsourced Parts
        RadioButton partOutBtn = new RadioButton();
        partOutBtn.setToggleGroup(partGroup);
        partOutBtn.setTranslateX(430);
        partOutBtn.setTranslateY(30);
        //Add a label to the newly created button
        Label partOutLbl = CreatePartLabel("Outsourced");
        partOutLbl.setTranslateX(485);
        partOutLbl.setTranslateY(30);

        //Create left column labels
        Label partID = CreatePartLabel("ID");
        partID.setTranslateY(100);

        Label partName = CreatePartLabel("Name");
        partName.setTranslateY(170);

        Label partInv = CreatePartLabel("Inv");
        partInv.setTranslateY(240);

        Label partPC = CreatePartLabel("Price/Cost");
        partPC.setTranslateY(310);

        Label partMax = CreatePartLabel("Max");
        partMax.setTranslateY(380);

        Label partMin = CreatePartLabel("Min");
        partMin.setTranslateX(460);
        partMin.setTranslateY(380);

        //Turned off by default, will appear when In-House is selected
        Label partMach = CreatePartLabel("Machine ID");
        partMach.setTranslateY(450);

        //Turned off by default, will appear when Outsourced is selected
        Label partComp = CreatePartLabel("Company Name");
        partComp.setTranslateY(450);

        //Create right column text fields
        TextField partIDText = CreatePartTextField("Auto-Gen - Disabled");
        partIDText.setText("Auto-Gen - Disabled");
        partIDText.setDisable(true);
        partIDText.setTranslateY(90);

        TextField partNameText = CreatePartTextField("Part Name");
        partNameText.setTranslateY(160);

        TextField partInvText = CreatePartTextField("Inv");
        partInvText.setTranslateY(230);

        TextField partPCText = CreatePartTextField("Price/Cost");
        partPCText.setTranslateY(300);

        TextField partMaxText = CreatePartTextField("Max");
        partMaxText.setPrefWidth(150);
        partMaxText.setTranslateY(370);

        TextField partMinText = CreatePartTextField("Min");
        partMinText.setPrefWidth(150);
        partMinText.setTranslateX(530);
        partMinText.setTranslateY(370);

        //Turned on by default, will disappear when Outsourced is selected
        TextField partMachText = CreatePartTextField("Mach ID");
        partMachText.setPrefWidth(190);
        partMachText.setTranslateY(440);

        //Turned off by default, will appear when Outsourced is selected
        TextField partCompText = CreatePartTextField("Comp Nm");
        partCompText.setPrefWidth(190);
        partCompText.setTranslateY(440);

        //Create Save button
        Button addPartSave = CreateButton("Save");
        addPartSave.setTranslateX(420);
        addPartSave.setTranslateY(510);

        //Create Cancel button
        Button addPartCancel = CreateButton("Cancel");
        addPartCancel.setTranslateX(570);
        addPartCancel.setTranslateY(510);

        //Create a confirmation message to be displayed if a user clicks the cancel button for a part
        Text canPartConf = CreateErrMsg("Are you sure you would like to cancel and lose all data entered?");
        canPartConf.setTranslateX(70);
        canPartConf.setTranslateY(583);

        //Add a confirmation "Yes" button to be displayed when a user clicks the cancel button
        Button yesCanPart = CreateConfButton("Yes");
        yesCanPart.setTranslateX(560);
        yesCanPart.setTranslateY(560);

        //Add a confirmation "No" button to be displayed when a user clicks the delete button
        Button noCanPart = CreateConfButton("No");
        noCanPart.setTranslateX(630);
        noCanPart.setTranslateY(560);

        //Set up the logic to switch the machine and company name fields when toggling between In-House and Outsourced
        partInBtn.setOnMouseClicked(e -> {
            addPartAP.getChildren().addAll(partMach, partMachText);
            addPartAP.getChildren().removeAll(partComp, partCompText);
        });

        partOutBtn.setOnMouseClicked(e -> {
            addPartAP.getChildren().addAll(partComp, partCompText);
            addPartAP.getChildren().removeAll(partMach, partMachText);
        });

        //Create an error message that will be displayed if the user leaves the part name field blank
        Text nameNullPart = CreateErrMsg("You must enter a name for the part.");
        nameNullPart.setTranslateX(110);
        nameNullPart.setTranslateY(220);

        //Create an error message that will be displayed if the user enters an invalid number in the inv field
        Text invBadIntPart = CreateErrMsg("The inventory amount must be a whole number greater than or equal to zero.");
        invBadIntPart.setTranslateX(110);
        invBadIntPart.setTranslateY(290);

        //Create an error message that will be displayed if the user enters an invalid number in the min or max field
        Text minMaxBadIntPart = CreateErrMsg("The minimum and maximum must be entered as whole numbers greater than or " +
                "equal to zero.");
        minMaxBadIntPart.setTranslateX(110);
        minMaxBadIntPart.setTranslateY(430);

        //Create an error message that will be displayed if the minimum amount is greater than the maximum
        Text minOverMax = CreateErrMsg("The minimum amount must be lower than the maximum amount.");
        minOverMax.setTranslateX(110);
        minOverMax.setTranslateY(430);

        //Create an error message that will be displayed if the inventory amount is not within the allowed limits
        Text invNotAllowed = CreateErrMsg("The inventory level must be between the minimum and maximum allowed");
        invNotAllowed.setTranslateX(110);
        invNotAllowed.setTranslateY(290);

        //Create an error message that will be displayed if the user enters an invalid number in the price field
        Text invalidDoublePart = CreateErrMsg("You must enter a number with no letters or symbols. The format should be " +
                "'1.00' or '1', not $1.00.");
        invalidDoublePart.setTranslateX(90);
        invalidDoublePart.setTranslateY(360);

        //Create an error message that will be displayed if an inhouse part has a blank machine ID field
        Text noMachID = CreateErrMsg("You must enter a machine ID as a whole number with no letters or symbols.");
        noMachID.setTranslateX(110);
        noMachID.setTranslateY(500);

        //Create an error message that will be displayed if an outsourced part has a blank company name field
        Text noCompName = CreateErrMsg("You must enter the company name of the part manufacturer.");
        noCompName.setTranslateX(110);
        noCompName.setTranslateY(500);

        //Allow this new screen to be set when the Add Part button is clicked on the main screen
        partAddBtn.setOnMouseClicked(e -> {
            //Clear the filteredPartData list to remove any current searches and set the table back to its normal list
            filteredPartData.clear();
            partTable.setItems(partData);
            partSearch.clear();

            //Clear the filteredProdData list to remove any current searches and set the table back to its normal list
            filteredProdData.clear();
            prodTable.setItems(prodData);
            prodSearch.clear();

            //Reset the auto-gen ID field to prevent issues coming from a modify screen
            partIDText.setText("Auto-Gen - Disabled");

            //Add the scene to the stage and add all default elements to the Add Part AnchorPane
            primaryStage.setScene(addPart);
            addPartAP.getChildren().addAll(addPartLabel, partInBtn, partInLbl, partOutBtn, partOutLbl, partID, partName,
                    partInv, partPC, partMax, partMin, partMach,  partIDText, partNameText, partInvText, partPCText,
                    partMaxText, partMinText, partMachText, addPartSave, addPartCancel);
        });

        //Switch back to the main screen once the Save button is hit
        addPartSave.setOnMouseClicked(e -> {
            //Remove any error messages that may still be on screen from a previous check
            addPartAP.getChildren().removeAll(nameNullPart, invBadIntPart, minMaxBadIntPart, invalidDoublePart,
                    minOverMax, invNotAllowed, noMachID, noCompName);

            //Verify the name field is not null
            String newPartName = partNameText.getText();
            if (newPartName == null || newPartName.isEmpty()) {
                addPartAP.getChildren().addAll(nameNullPart);
                return;
            }

            //Include format check for the Inv field, must be a whole number greater than or equal to zero
            int newPartInv = checkInt(partInvText.getText());
            if (newPartInv == -1) {
                addPartAP.getChildren().addAll(invBadIntPart);
                partInvText.setText("0"); //Default to zero for user
                return;
            }

            //Include a format check for the Min and Max fields, must be whole numbers greater than or equal to zero
            int newPartMin = checkInt(partMinText.getText());
            int newPartMax = checkInt(partMaxText.getText());
            if (newPartMin == -1 || newPartMax == -1) {
                addPartAP.getChildren().addAll(minMaxBadIntPart);
                return;
            }

            //Include a format check for the Price field, must be an int or double with no letters or symbols
            double newPartPrice = checkDouble(partPCText.getText());
            if (newPartPrice == -1) {
                addPartAP.getChildren().addAll(invalidDoublePart);
                return;
            }

            //Verify that the minimum amount entered is not greater than the maximum amount
            boolean minUnderMax = verifyMinMax(newPartMin, newPartMax);
            if (!minUnderMax) {
                addPartAP.getChildren().addAll(minOverMax);
                return;
            }

            //Verify that the inventory level entered is between the minimum and maximum allowed
            boolean invAllowed = checkInventoryLvl(newPartInv, newPartMin, newPartMax);
            if (!invAllowed) {
                addPartAP.getChildren().addAll(invNotAllowed);
                return;
            }


            //Determine whether this is an Inhouse or Outsourced part and create a new part
            if (partInBtn.isSelected()) {
                //Verify the machine ID is valid, must be a whole number greater than or equal to zero
                int newPartMach = checkInt(partMachText.getText());
                if (newPartMach == -1) {
                    addPartAP.getChildren().addAll(noMachID);
                    return;
                }
                //Create the new part
                Inhouse newPart = new Inhouse(getNextPartID(), newPartName, newPartPrice, newPartInv, newPartMin,
                        newPartMax, newPartMach);
                currentInv.addPart(newPart);
                partData.add(newPart);
            }
            else {
                //Verify the company name field is not blank
                String newCompName = partCompText.getText();
                if (newCompName == null || newCompName.isEmpty()) {
                    addPartAP.getChildren().addAll(noCompName);
                    return;
                }
                //Create the new part
                Outsourced newPart = new Outsourced(getNextPartID(), newPartName, newPartPrice, newPartInv,newPartMin,
                        newPartMax, newCompName);
                currentInv.addPart(newPart);
                partData.add(newPart);
            }

            //Clear all data typed into text fields
            partNameText.clear();
            partInvText.clear();
            partPCText.clear();
            partMaxText.clear();
            partMinText.clear();
            partMachText.clear();
            partCompText.clear();

            //Remove all main screen error messages in case they are still on screen from a previous check before
            //getting into this screen
            ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd, noDelProd);

            //Set the scene back to main and remove all objects from anchor panes
            primaryStage.setScene(main);
            addPartAP.getChildren().clear();
        });

        //Switch back to the main screen after the cancel button is hit
        addPartCancel.setOnMouseClicked(e -> {
            //Display confirmation dialogue to verify this is what the user intends to do
            addPartAP.getChildren().addAll(canPartConf, yesCanPart, noCanPart);

            //If Yes is selected, clear all data from this screen and return to the main screen
            yesCanPart.setOnMouseClicked(e2 -> {

                //Clear all data in text fields
                partIDText.clear();
                partNameText.clear();
                partInvText.clear();
                partPCText.clear();
                partMaxText.clear();
                partMinText.clear();
                partMachText.clear();
                partCompText.clear();

                //Remove all main screen error messages in case they are still on screen from a previous check before
                //getting into this screen
                ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd,
                        noDelProd);

                //Set the scene back to main and remove all objects from the anchor pane
                primaryStage.setScene(main);
                addPartAP.getChildren().clear();
            });

            //If No is selected remove the confirmation dialogue but do not remove the part
            noCanPart.setOnMouseClicked(e3 -> {
                addPartAP.getChildren().removeAll(canPartConf, yesCanPart, noCanPart);
            });
        });


/********************************************************************************************************************
 * Create and configure the Modify Parts screen, this screen will reuse many of the elements created for the Add    *
 * Parts screen                                                                                                     *
 *******************************************************************************************************************/

        //Create a new AnchorPane to hold this window's content
        AnchorPane modPartAP = new AnchorPane();
        modPartAP.setPrefSize(750, 600);
        modPartAP.setId("PartAP");

        //Create a new scene for this window and link the main style sheet to it
        Scene modPart = new Scene(modPartAP, 750, 600);
        modPart.getStylesheets().add("Inv_Mgmt_Sys/MainStyleSheet.css");

        //Create a label for the screen title
        Label modPartLabel = CreateScreenLabel("Modify Part");
        //Setting the Add Part Label location on screen
        modPartLabel.setTranslateX(40);
        modPartLabel.setTranslateY(25);

        //Create a new ToggleGroup to manage Radio Buttons for In-House and Outsourced part options
        final ToggleGroup partModGroup = new ToggleGroup();

        //Create a Radio Button for inhouse parts
        RadioButton partInModBtn = new RadioButton();
        partInModBtn.setToggleGroup(partModGroup);
        partInModBtn.setTranslateX(240);
        partInModBtn.setTranslateY(30);

        //Create a Radio Button for Outsourced parts
        RadioButton partOutModBtn = new RadioButton();
        partOutModBtn.setToggleGroup(partModGroup);
        partOutModBtn.setTranslateX(430);
        partOutModBtn.setTranslateY(30);

        //Set up the logic to switch the machine and company name fields when toggling between In-House and Outsourced
        partInModBtn.setOnMouseClicked(e -> {
            modPartAP.getChildren().addAll(partMach, partMachText);
            modPartAP.getChildren().removeAll(partComp, partCompText);
        });

        partOutModBtn.setOnMouseClicked(e -> {
            modPartAP.getChildren().addAll(partComp, partCompText);
            modPartAP.getChildren().removeAll(partMach, partMachText);
        });

        //Create Save button
        Button modPartSave = CreateButton("Save");
        modPartSave.setTranslateX(420);
        modPartSave.setTranslateY(510);

        //Create Cancel button
        Button modPartCancel = CreateButton("Cancel");
        modPartCancel.setTranslateX(570);
        modPartCancel.setTranslateY(510);

        //Create a confirmation message to be displayed if a user clicks the cancel button for a part
        Text canModPartConf = CreateErrMsg("Are you sure you would like to cancel and discard all changes?");
        canModPartConf.setTranslateX(120);
        canModPartConf.setTranslateY(583);

        //Add a confirmation "Yes" button to be displayed when a user clicks the cancel button
        Button yesCanModPart = CreateConfButton("Yes");
        yesCanModPart.setTranslateX(560);
        yesCanModPart.setTranslateY(560);

        //Add a confirmation "No" button to be displayed when a user clicks the delete button
        Button noCanModPart = CreateConfButton("No");
        noCanModPart.setTranslateX(630);
        noCanModPart.setTranslateY(560);


        //Allow this new screen to be set when the Modify Part button is clicked on the main screen
        partModBtn.setOnMouseClicked(e -> {
            //Confirm that a part is currently selected when the button is clicked. If no part is selected, do nothing
            Part selectedPart = partTable.getSelectionModel().getSelectedItem();
            if (selectedPart == null) {
                return;
            }

            //Clear the filteredPartData list to remove any current searches and set the table back to its normal list
            filteredPartData.clear();
            partTable.setItems(partData);
            partSearch.clear();

            //Clear the filteredProdData list to remove any current searches and set the table back to its normal list
            filteredProdData.clear();
            prodTable.setItems(prodData);
            prodSearch.clear();

            //Populate text fields with data from the selected part
            partIDText.setText(Integer.toString(selectedPart.getPartID()));
            partNameText.setText(selectedPart.getName());
            partPCText.setText(Double.toString(selectedPart.getPrice()));
            partInvText.setText(Integer.toString(selectedPart.getInStock()));
            partMinText.setText(Integer.toString(selectedPart.getMin()));
            partMaxText.setText(Integer.toString(selectedPart.getMax()));

            //Determine if the part is inhouse or outsourced and set default button
            if (selectedPart instanceof Inhouse) {
                partMachText.setText(Integer.toString(((Inhouse) selectedPart).getMachineID()));
                partInModBtn.setSelected(true);
                modPartAP.getChildren().addAll(partMach, partMachText);
                modPartAP.requestFocus();       //This prevents the partMachText field from grabbing default focus
            }
            else {
                partCompText.setText(((Outsourced) selectedPart).getCompanyName());
                partOutModBtn.setSelected(true);
                modPartAP.getChildren().addAll(partComp, partCompText);
                modPartAP.requestFocus();       //This prevents the partCompText field from grabbing default focus
            }


            //Switch to the modPart scene and add appropriate children to the anchor pane
            primaryStage.setScene(modPart);
            //Add all default elements to the Mod Part AnchorPane when this screen is launched
            modPartAP.getChildren().addAll(modPartLabel, partInModBtn, partInLbl, partOutModBtn, partOutLbl, partID, partName,
                    partInv, partPC, partMax, partMin, partIDText, partNameText, partInvText, partPCText,
                    partMaxText, partMinText, modPartSave, modPartCancel);
        });


        //Switch back to the main screen once the Save button is hit on the Modify Part screen
        modPartSave.setOnMouseClicked(e -> {
            //Remove any error messages that may still be on screen from a previous check
            modPartAP.getChildren().removeAll(nameNullPart, invBadIntPart, minMaxBadIntPart, invalidDoublePart,
                    minOverMax, invNotAllowed);

            //Verify the name field is not null
            String newPartName = partNameText.getText();
            if (newPartName == null || newPartName.isEmpty()) {
                modPartAP.getChildren().addAll(nameNullPart);
                return;
            }

            //Include format check for the Inv field, must be a whole number greater than or equal to zero
            int newPartInv = checkInt(partInvText.getText());
            if (newPartInv == -1) {
                modPartAP.getChildren().addAll(invBadIntPart);
                partInvText.setText("0"); //Default to zero for user
                return;
            }

            //Include a format check for the Min and Max fields, must be whole numbers greater than or equal to zero
            int newPartMin = checkInt(partMinText.getText());
            int newPartMax = checkInt(partMaxText.getText());
            if (newPartMin == -1 || newPartMax == -1) {
                modPartAP.getChildren().addAll(minMaxBadIntPart);
                return;
            }

            //Get the part ID, no need to check format since this field can't be changed by the user
            int modPartID = Integer.parseInt(partIDText.getText());

            //Get the Part we're working with
            Part updatePart = getPart(modPartID, partData);

            //Include a format check for the Price field, must be an int or double with no letters or symbols
            double newPartPrice = checkDouble(partPCText.getText());
            if (newPartPrice == -1) {
                modPartAP.getChildren().addAll(invalidDoublePart);
                return;
            }

            //Verify that the minimum amount entered is not greater than the maximum amount
            boolean minUnderMax = verifyMinMax(newPartMin, newPartMax);
            if (!minUnderMax) {
                modPartAP.getChildren().addAll(minOverMax);
                return;
            }

            //Verify that the inventory level entered is between the minimum and maximum allowed
            boolean invAllowed = checkInventoryLvl(newPartInv, newPartMin, newPartMax);
            if (!invAllowed) {
                modPartAP.getChildren().addAll(invNotAllowed);
                return;
            }

            //Check to see if the part is set to Inhouse or Outsourced and check to see if its type has been changed
            if (updatePart instanceof Inhouse && partInModBtn.isSelected()) {
                //Verify the machine ID is valid, must be a whole number greater than or equal to zero
                int modPartMach = checkInt(partMachText.getText());
                if (modPartMach == -1) {
                    modPartAP.getChildren().addAll(noMachID);
                    return;
                }
                //Update part information with the data from the text fields
                updatePart.setName(newPartName);
                updatePart.setPrice(newPartPrice);
                updatePart.setInStock(newPartInv);
                updatePart.setMin(newPartMin);
                updatePart.setMax(newPartMax);
                ((Inhouse) updatePart).setMachineID(modPartMach);
            }

            //Check to see if the part is currently outsourced and was previously outsourced
            else if (updatePart instanceof Outsourced && partOutModBtn.isSelected()) {
                //Verify the company name field is not blank
                String modCompName = partCompText.getText();
                if (modCompName == null || modCompName.isEmpty()) {
                    modPartAP.getChildren().addAll(noCompName);
                    return;
                }
                updatePart.setName(newPartName);
                updatePart.setPrice(newPartPrice);
                updatePart.setInStock(newPartInv);
                updatePart.setMin(newPartMin);
                updatePart.setMax(newPartMax);
                ((Outsourced) updatePart).setCompanyName(modCompName);
            }

            //If the part has been switched from Inhouse to Outsourced or vice versa, a new part will need to be
            //created and the existing part will need to be deleted
            else {
                //Determine whether this is an Inhouse or Outsourced part and create a new part
                if (partInBtn.isSelected()) {
                    //Verify the machine ID is valid, must be a whole number greater than or equal to zero
                    int modPartMach = checkInt(partMachText.getText());
                    if (modPartMach == -1) {
                        modPartAP.getChildren().addAll(noMachID);
                        return;
                    }
                    //Create a new Inhouse part
                    Inhouse newPart = new Inhouse(modPartID, newPartName, newPartPrice, newPartInv, newPartMin,
                            newPartMax, modPartMach);
                    //Delete the existing outsourced part
                    currentInv.deletePart(updatePart);
                    currentInv.addPart(newPart);
                }
                else {
                    //Verify the company name field is not blank
                    String modCompName = partCompText.getText();
                    if (modCompName == null || modCompName.isEmpty()) {
                        modPartAP.getChildren().addAll(noCompName);
                        return;
                    }
                    //Create the new outsourced part
                    Outsourced newPart = new Outsourced(modPartID, newPartName, newPartPrice, newPartInv,newPartMin,
                            newPartMax, modCompName);
                    //Delete the existing inhouse part
                    currentInv.deletePart(updatePart);
                    currentInv.addPart(newPart);
                }
            }

            //Clear all data typed into text fields
            partNameText.clear();
            partInvText.clear();
            partPCText.clear();
            partMaxText.clear();
            partMinText.clear();
            partMachText.clear();
            partCompText.clear();

            //Remove all main screen error messages in case they are still on screen from a previous check before
            //getting into this screen.
            ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd, noDelProd);

            //Clear the current prodData table to refresh it with updated info
            partData.clear();

            //Get products from the currentInv Inventory object and add them to the prodData Observable Array List
            int a = 0;
            Part currentPart;
            do {
                currentPart = currentInv.lookupPart(a);
                if (currentPart != null) {
                    partData.add(currentPart);
                }
                a++;
            } while (currentPart != null);

            //Set the scene back to main and remove all objects from anchor panes
            primaryStage.setScene(main);
            modPartAP.getChildren().clear();
        });


        //Switch back to the main screen after the cancel button is hit
        modPartCancel.setOnMouseClicked(e -> {
            //Display confirmation dialogue to verify this is what the user intends to do
            modPartAP.getChildren().addAll(canModPartConf, yesCanModPart, noCanModPart);

            //If Yes is selected, clear all data from this screen and return to the main screen
            yesCanModPart.setOnMouseClicked(e2 -> {

                //Clear all data in text fields
                partIDText.clear();
                partNameText.clear();
                partInvText.clear();
                partPCText.clear();
                partMaxText.clear();
                partMinText.clear();
                partMachText.clear();
                partCompText.clear();

                //Remove all main screen error messages in case they are still on screen from a previous check before
                //getting into this screen
                ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd, noDelProd);

                //Set the scene back to main and remove all objects from anchor panes
                primaryStage.setScene(main);
                modPartAP.getChildren().clear();
            });

            //If No is clicked, remove the dialogue but do not remove any part data
            noCanModPart.setOnMouseClicked(e3 -> {
                modPartAP.getChildren().removeAll(canModPartConf, yesCanModPart, noCanModPart);
            });
        });


/********************************************************************************************************************
 * Create and configure the Add Product Screen                                                                      *
 *******************************************************************************************************************/

        //Create a new AnchorPane to hold this window's content
        AnchorPane addProdAP = new AnchorPane();
        addProdAP.setPrefSize(1400, 800);
        addProdAP.setId("ProdAP");

        //Create a new scene for this window and link the main style sheet to it
        Scene addProd = new Scene(addProdAP, 1400, 800);
        addProd.getStylesheets().add("Inv_Mgmt_Sys/MainStyleSheet.css");

        //Create the outer Rectangle for this screen
        Rectangle prodRect = CreateRectangle();
        prodRect.setWidth(1350);
        prodRect.setHeight(750);
        prodRect.setTranslateX(25);
        prodRect.setTranslateY(20);

        //Add a title label to the top of the screen
        Label addProdLabel = CreateScreenLabel("Add Product");
        addProdLabel.setTranslateX(80);
        addProdLabel.setTranslateY(100);

        //Add labels for the left hand column of data
        Label prodID = CreateProdLabel("ID");
        prodID.setTranslateY(250);

        Label prodName = CreateProdLabel("Name");
        prodName.setTranslateY(320);

        Label prodInv = CreateProdLabel("Inv");
        prodInv.setTranslateY(390);

        Label prodPrice = CreateProdLabel("Price");
        prodPrice.setTranslateY(460);

        Label prodMax = CreateProdLabel("Max");
        prodMax.setTranslateY(530);

        Label prodMin = CreateProdLabel("Min");
        prodMin.setTranslateX(300);
        prodMin.setTranslateY(530);

        //Create text fields for right data column
        TextField prodIDText = CreateProdTextField("Auto-Gen - Disabled");
        prodIDText.setText("Auto-Gen - Disabled");
        prodIDText.setDisable(true);
        prodIDText.setPrefWidth(240);
        prodIDText.setTranslateY(240);

        TextField prodNameText = CreateProdTextField("Product Name");
        prodNameText.setPrefWidth(240);
        prodNameText.setTranslateY(310);

        TextField prodInvText = CreateProdTextField("Inv");
        prodInvText.setTranslateY(380);

        TextField prodPriceText = CreateProdTextField("Price");
        prodPriceText.setTranslateY(450);

        TextField prodMaxText = CreateProdTextField("Max");
        prodMaxText.setTranslateY(520);

        TextField prodMinText = CreateProdTextField("Min");
        prodMinText.setTranslateX(350);
        prodMinText.setTranslateY(520);

        //Create Part Search button
        Button addProdSearchBtn = CreateButton("Search");
        addProdSearchBtn.setTranslateX(800);
        addProdSearchBtn.setTranslateY(100);

        //Create Part Search TextField
        TextField addProdSearch = new TextField();
        addProdSearch.setPrefSize(250, 10);
        addProdSearch.setTranslateX(930);
        addProdSearch.setTranslateY(105);

        //Create Part Search TableView
        TableView<Part> addPartTable = new TableView<Part>(partData);

        //Set the size of the table and restrict it to the four columns created
        addPartTable.setPrefSize(660,154);
        addPartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //Allow table to be edited after creation
        addPartTable.setEditable(true);

        //Add a new Table Columns for this table
        TableColumn addPartIDCol = new TableColumn("Part ID");
        addPartIDCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("partID"));

        TableColumn addPartNameCol = new TableColumn("Part Name");
        addPartNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));

        TableColumn addPartInvCol = new TableColumn("Inventory Level");
        addPartInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("inStock"));

        TableColumn addPartPriceCol = new TableColumn ("Price Per Unit");
        addPartPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        //Add the data and columns to the table
        addPartTable.setItems(partData);
        addPartTable.getColumns().addAll(addPartIDCol, addPartNameCol, addPartInvCol, addPartPriceCol);

        //Position the partTable on the screen
        addPartTable.setTranslateX(620);
        addPartTable.setTranslateY(170);

        //Create the Add button for the Add Part table
        Button addPartBtn = CreateButton("Add");
        addPartBtn.setTranslateX(1130);
        addPartBtn.setTranslateY(340);

        //Create a new ObservableList to hold data about the parts a product consists of
        ObservableList<Part> prodPartData = FXCollections.observableArrayList();

        //Create the Product Part TableView
        TableView<Part> prodPartTable = new TableView<Part>(prodPartData);

        //Set the size of the table and restrict it to the four columns created
        prodPartTable.setPrefSize(660,154);
        prodPartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //Allow table to be edited after creation
        prodPartTable.setEditable(true);

        //Create new columns for this table
        TableColumn prodPartIDCol = new TableColumn("Part ID");
        prodPartIDCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("partID"));

        TableColumn prodPartNameCol = new TableColumn("Part Name");
        prodPartNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));

        TableColumn prodPartInvCol = new TableColumn("Inventory Level");
        prodPartInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("inStock"));

        TableColumn prodPartPriceCol = new TableColumn ("Price Per Unit");
        prodPartPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        //Add the data and columns to the table
        prodPartTable.setItems(prodPartData);
        prodPartTable.getColumns().addAll(prodPartIDCol, prodPartNameCol, prodPartInvCol, prodPartPriceCol);

        //Position the product's part table on the screen
        prodPartTable.setTranslateX(620);
        prodPartTable.setTranslateY(450);

        //Create Delete button for the product's part table
        Button prodPartDelBtn = CreateButton("Delete");
        prodPartDelBtn.setTranslateX(1130);
        prodPartDelBtn.setTranslateY(620);

        //Create the Add Product Save button
        Button addProdSaveBtn = CreateButton("Save");
        addProdSaveBtn.setTranslateX(1010);
        addProdSaveBtn.setTranslateY(680);

        //Create the Cancel button
        Button prodCancelBtn = CreateButton("Cancel");
        prodCancelBtn.setTranslateX(1130);
        prodCancelBtn.setTranslateY(680);

        //Create an error message that will be displayed if the user leaves the product name field blank
        Text nameNullProd = CreateErrMsg("You must enter a name for the product.");
        nameNullProd.setTranslateX(60);
        nameNullProd.setTranslateY(372);

        //Create an error message that will be displayed if the inventory amount is not within the allowed limits
        Text invNotAllowedProd = CreateErrMsg("The inventory level must be between the minimum and maximum allowed.");
        invNotAllowedProd.setTranslateX(60);
        invNotAllowedProd.setTranslateY(440);

        //Create an error message that will be displayed if the user enters an invalid number in the inv field
        Text invBadIntProd = CreateErrMsg("The inventory amount must be a whole number greater than or equal to zero.");
        invBadIntProd.setTranslateX(60);
        invBadIntProd.setTranslateY(440);

        //Create an error message that will be displayed if the minimum amount is greater than the maximum
        Text minOverMaxProd = CreateErrMsg("The minimum amount must be lower than the maximum amount.");
        minOverMaxProd.setTranslateX(60);
        minOverMaxProd.setTranslateY(585);

        //Create an error message that will be displayed if the user enters an invalid number in the min or max field
        Text minMaxBadIntProd = CreateErrMsg("The minimum and maximum must be entered as whole numbers greater than or " +
                "equal to zero.");
        minMaxBadIntProd.setTranslateX(60);
        minMaxBadIntProd.setTranslateY(585);

        //Create an error message that will be displayed if the user enters an invalid number in the price field
        Text invalidDoubleProd = CreateErrMsg("You must enter a number with no letters or symbols. The format should be " +
                "'1.00' or '1'.");
        invalidDoubleProd.setTranslateX(40);
        invalidDoubleProd.setTranslateY(510);

        //Create an error message that will be displayed if a product's price is less than the sum of its part prices
        Text priceTooLow = CreateErrMsg("A product's price must be equal to or greater than the cost of its parts.");
        priceTooLow.setTranslateX(40);
        priceTooLow.setTranslateY(510);

        //Create an error message that will be displayed if there are no parts assigned to the product
        Text noParts = CreateErrMsg("A product must have at least one part component assigned.");
        noParts.setTranslateX(620);
        noParts.setTranslateY(430);

        //Create a confirmation message to be displayed if a user clicks the cancel button for a product
        Text canProdConf = CreateErrMsg("Are you sure you would like to cancel and lose all current data entered?");
        canProdConf.setTranslateX(620);
        canProdConf.setTranslateY(755);

        //Add a confirmation "Yes" button to be displayed when a user clicks the cancel button
        Button yesCanProd = CreateConfButton("Yes");
        yesCanProd.setTranslateX(1120);
        yesCanProd.setTranslateY(732);

        //Add a confirmation "No" button to be displayed when a user clicks the cancel button
        Button noCanProd = CreateConfButton("No");
        noCanProd.setTranslateX(1190);
        noCanProd.setTranslateY(732);

        //Create a confirmation message to be displayed when a user clicks the Delete button to remove a part
        Text delProdPartConf = CreateErrMsg("Are you sure?");
        delProdPartConf.setTranslateX(1250);
        delProdPartConf.setTranslateY(640);

        //Add a confirmation "Yes" button to be displayed when a user clicks the Delete button to remove a part
        Button yesDelProdPart = CreateConfButton("Yes");
        yesDelProdPart.setTranslateX(1240);
        yesDelProdPart.setTranslateY(650);

        //Add a confirmation "No" button to be displayed when a user clicks the Delete button to remove a part
        Button noDelProdPart = CreateConfButton("No");
        noDelProdPart.setTranslateX(1305);
        noDelProdPart.setTranslateY(650);


        //Configure the search button functionality
        addProdSearchBtn.setOnMouseClicked(e -> {
            //Get data entered in the partSearch text field
            String prodPartSearchData = addProdSearch.getText().toLowerCase();

            //Create a new ObservableArrayList to hold the matching entries from the PartData list
            ObservableList<Part> filteredProdPartData = FXCollections.observableArrayList();

            //Clear the filtered list to prepare for the next search
            filteredProdPartData.clear();

            //If the part search text field is searched blank, make sure we are no longer viewing a filtered list
            if (addProdSearch.getText().isEmpty() || addProdSearch.getText() == null) {
                addPartTable.setItems(partData);
                return;
            }

            for (int l = 0; l < partData.size(); l++) {
                if (partData.get(l).getName().toLowerCase().contains(prodPartSearchData)) {
                    filteredProdPartData.add(partData.get(l));
                }
            }

            //Set the filtered list to display in the partTable
            addPartTable.setItems(filteredProdPartData);

        });

        //Configure the Add button functionality and populate the product's part table with the added parts
        addPartBtn.setOnMouseClicked(e -> {
            //Confirm that a part is currently selected when the button is clicked. If no part is selected, do nothing
            Part selectedPart = addPartTable.getSelectionModel().getSelectedItem();
            if (selectedPart == null) {
                return;
            }
            else {
                prodPartData.add(selectedPart);
            }
        });

        //Configure the Delete button functionality to remove a selected part from the list
        prodPartDelBtn.setOnMouseClicked(e -> {
            //Verify a part is selected, otherwise do nothing
            Part selectedPart = prodPartTable.getSelectionModel().getSelectedItem();
            if (selectedPart == null) {
                return;
            }

            //Display confirmation dialogue and buttons
            addProdAP.getChildren().addAll(delProdPartConf, yesDelProdPart, noDelProdPart);

            //If Yes is selected, delete the part
            yesDelProdPart.setOnMouseClicked(e2 -> {
                addProdAP.getChildren().removeAll(delProdPartConf, yesDelProdPart, noDelProdPart);

                prodPartTable.getItems().remove(selectedPart);
                prodPartData.remove(selectedPart);
            });

            //If No is selected, remove the dialogue but do not remove the part
            noDelProdPart.setOnMouseClicked(e3 -> {
                addProdAP.getChildren().removeAll(delProdPartConf, yesDelProdPart, noDelProdPart);
            });

        });


        //Allow this new screen to be set when the Add Product button is clicked on the main screen
        prodAddBtn.setOnMouseClicked(e -> {
            //Clear the filteredPartData list to remove any current searches and set the table back to its normal list
            filteredPartData.clear();
            partTable.setItems(partData);
            partSearch.clear();

            //Clear the filteredProdData list to remove any current searches and set the table back to its normal list
            filteredProdData.clear();
            prodTable.setItems(prodData);
            prodSearch.clear();

            //Set the scene to addProd and add all elements to the addProdAP anchor pane
            primaryStage.setScene(addProd);
            addProdAP.getChildren().addAll(prodRect, addProdLabel, prodID, prodName, prodInv, prodPrice, prodMax,
                    prodMin, prodIDText, prodNameText, prodInvText, prodPriceText, prodMaxText, prodMinText,
                    addProdSearchBtn, addProdSearch, addPartTable, addPartBtn, prodPartTable, prodPartDelBtn,
                    addProdSaveBtn, prodCancelBtn);

        });


        //Switch back to the main screen once the Save button is hit on the Add Product screen
        addProdSaveBtn.setOnMouseClicked(e -> {
            //Remove any error messages that may still be on screen from a previous check
            addProdAP.getChildren().removeAll(nameNullProd, invBadIntProd, minMaxBadIntProd, invalidDoubleProd,
                    minOverMaxProd, invNotAllowedProd, noParts, priceTooLow);

            //Verify the name field is not null
            String newProdName = prodNameText.getText();
            if (newProdName == null || newProdName.isEmpty()) {
                addProdAP.getChildren().addAll(nameNullProd);
                return;
            }

            //Include format check for the Inv field, must be a whole number greater than or equal to zero
            int newProdInv = checkInt(prodInvText.getText());
            if (newProdInv == -1) {
                addProdAP.getChildren().addAll(invBadIntProd);
                prodInvText.setText("0"); //Default to zero for user
                return;
            }

            //Include a format check for the Min and Max fields, must be whole numbers greater than or equal to zero
            int newProdMin = checkInt(prodMinText.getText());
            int newProdMax = checkInt(prodMaxText.getText());
            if (newProdMin == -1 || newProdMax == -1) {
                addProdAP.getChildren().addAll(minMaxBadIntProd);
                return;
            }

            //Include a format check for the Price field, must be an int or double with no letters or symbols
            double newProdPrice = checkDouble(prodPriceText.getText());
            if (newProdPrice == -1) {
                addProdAP.getChildren().addAll(invalidDoubleProd);
                return;
            }

            //Verify that the minimum amount entered is not greater than the maximum amount
            boolean minUnderMax = verifyMinMax(newProdMin, newProdMax);
            if (!minUnderMax) {
                addProdAP.getChildren().addAll(minOverMaxProd);
                return;
            }

            //Verify that the inventory level entered is between the minimum and maximum allowed
            boolean invAllowed = checkInventoryLvl(newProdInv, newProdMin, newProdMax);
            if (!invAllowed) {
                addProdAP.getChildren().addAll(invNotAllowedProd);
                return;
            }

            //Verify the product has at least one part assigned to it
            if (prodPartData.size() == 0){
                addProdAP.getChildren().addAll(noParts);
                return;
            }

            //Verify that the Price entered is equal to or greater than the sum of all associated parts
            if(newProdPrice < calculatePartPrice(prodPartData)) {
                addProdAP.getChildren().addAll(priceTooLow);
                return;
            }

            //Create a new product and add it to the current inventory and the main screen product table
            Product newProd = new Product (getNextProdID(), newProdName, newProdPrice, newProdInv, newProdMin, newProdMax);
            //Add all parts currently in the prodPartData list to the product
            for (int r = 0; r < prodPartData.size(); r++) {
                newProd.addAssociatedPart(prodPartData.get(r));
            }
            //Add the product to the currentInv Inventory object and the prodData list for the Product Table
            currentInv.addProduct(newProd);
            prodData.add(newProd);

            //Clear all data entered into text fields
            prodNameText.clear();
            prodPriceText.clear();
            prodInvText.clear();
            prodMinText.clear();
            prodMaxText.clear();

            //Remove all parts from the prodPartTable
            prodPartData.clear();

            //Remove all main screen error messages in case they are still on screen from a previous check before
            //getting into this screen
            ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd, noDelProd);

            //Set scene back to main and remove all objects from the addProdAP anchor pane
            primaryStage.setScene(main);
            addProdAP.getChildren().clear();
        });

        //Switch back to the main screen once the Cancel button is hit on the Add Product screen
        prodCancelBtn.setOnMouseClicked(e -> {
            //Display confirmation dialogue to verify this is what the user intends to do
            addProdAP.getChildren().addAll(canProdConf, yesCanProd, noCanProd);

            //If Yes is clicked clear all data from this screen and return to the main screen
            yesCanProd.setOnMouseClicked(e2 -> {

                //Clear all data entered into text fields
                prodNameText.clear();
                prodPriceText.clear();
                prodInvText.clear();
                prodMinText.clear();
                prodMaxText.clear();

                //Remove all parts from the prodPartTable
                prodPartData.clear();

                //Remove all main screen error messages in case they are still on screen from a previous check before
                //getting into this screen
                ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd,
                        noDelProd);

                //Set the scene to main and remove all objects from the addProdAP anchor pane
                primaryStage.setScene(main);
                addProdAP.getChildren().clear();

            });

            //If No is clicked, remove the dialogue but do not remove any product data
            noCanProd.setOnMouseClicked(e3 -> {
                addProdAP.getChildren().removeAll(canProdConf, yesCanProd, noCanProd);
            });

        });


/********************************************************************************************************************
 * Create and configure the Modify Product screen, this screen will reuse many of the elements created for the Add  *
 * Parts screen                                                                                                     *
 *******************************************************************************************************************/

        //Create a new AnchorPane to hold this window's content
        AnchorPane modProdAP = new AnchorPane();
        modProdAP.setPrefSize(1400, 800);
        modProdAP.setId("ProdAP");

        //Create a new scene for this window and link the main style sheet to it
        Scene modProd = new Scene(modProdAP, 1400, 800);
        modProd.getStylesheets().add("Inv_Mgmt_Sys/MainStyleSheet.css");

        //Add a title label to the top of the screen
        Label modProdLabel = CreateScreenLabel("Modify Product");
        modProdLabel.setTranslateX(80);
        modProdLabel.setTranslateY(100);

        //Create the Modify Product Save button
        Button modProdSaveBtn = CreateButton("Save");
        modProdSaveBtn.setTranslateX(1010);
        modProdSaveBtn.setTranslateY(680);

        //Create the Modify Product Cancel button
        Button modProdCancelBtn = CreateButton("Cancel");
        modProdCancelBtn.setTranslateX(1130);
        modProdCancelBtn.setTranslateY(680);

        //Create the Modify Product Delete button
        Button modProdPartDelBtn = CreateButton("Delete");
        modProdPartDelBtn.setTranslateX(1130);
        modProdPartDelBtn.setTranslateY(620);

        //Create a confirmation message to be displayed if the user clicks the cancel button for a product
        Text canModProdConf = CreateErrMsg("Are you sure you would like to cancel and discard all changes?");
        canModProdConf.setTranslateX(680);
        canModProdConf.setTranslateY(755);

        //Add a confirmation "Yes" button to be displayed when the user clicks the cancel button
        Button yesCanModProd = CreateConfButton("Yes");
        yesCanModProd.setTranslateX(1120);
        yesCanModProd.setTranslateY(732);

        //Add a confirmation "No" button to be displayed when the user clicks the cancel button
        Button noCanModProd = CreateConfButton("No");
        noCanModProd.setTranslateX(1190);
        noCanModProd.setTranslateY(732);

        //Create a confirmation message to be displayed when a user clicks the Delete button to remove a part
        Text delModProdPartConf = CreateErrMsg("Are you sure?");
        delModProdPartConf.setTranslateX(1250);
        delModProdPartConf.setTranslateY(640);

        //Add a confirmation "Yes" button to be displayed when a user clicks the Delete button to remove a part
        Button yesDelModProdPart = CreateConfButton("Yes");
        yesDelModProdPart.setTranslateX(1240);
        yesDelModProdPart.setTranslateY(650);

        //Add a confirmation "No" button to be displayed when a user clicks the Delete button to remove a part
        Button noDelModProdPart = CreateConfButton("No");
        noDelModProdPart.setTranslateX(1305);
        noDelModProdPart.setTranslateY(650);


        //Configure the Delete button functionality to remove a selected part from the list
        modProdPartDelBtn.setOnMouseClicked(e -> {
            //Verify a part is selected, otherwise do nothing
            Part selectedPart = prodPartTable.getSelectionModel().getSelectedItem();
            if (selectedPart == null) {
                return;
            }

            //Display confirmation dialogue and buttons
            modProdAP.getChildren().addAll(delModProdPartConf, yesDelModProdPart, noDelModProdPart);

            //If Yes is selected delete the part
            yesDelModProdPart.setOnMouseClicked(e2 -> {
                modProdAP.getChildren().removeAll(delModProdPartConf, yesDelModProdPart, noDelModProdPart);

                prodPartTable.getItems().remove(selectedPart);
                prodPartData.remove(selectedPart);
            });

            //If No is selected remove the dialogue but don't remove the part
            noDelModProdPart.setOnMouseClicked(e3 -> {
                modProdAP.getChildren().removeAll(delModProdPartConf, yesDelModProdPart, noDelModProdPart);
            });
        });


        //Allow this new screen to be set when the Modify Product button is clicked on the main screen
        prodModBtn.setOnMouseClicked(e -> {
            //Confirm that a Product is currently selected when the button is clicked. If no prod is selected, do nothing
            Product selectedProd = prodTable.getSelectionModel().getSelectedItem();
            if (selectedProd == null) {
                return;
            }

            //Clear the filteredPartData list to remove any current searches and set the table back to its normal list
            filteredPartData.clear();
            partTable.setItems(partData);
            partSearch.clear();

            //Clear the filteredProdData list to remove any current searches and set the table back to its normal list
            filteredProdData.clear();
            prodTable.setItems(prodData);
            prodSearch.clear();

            //Populate text fields with data from the selected product
            prodIDText.setText(Integer.toString(selectedProd.getProductID()));
            prodNameText.setText(selectedProd.getName());
            prodPriceText.setText(Double.toString(selectedProd.getPrice()));
            prodInvText.setText(Integer.toString(selectedProd.getInStock()));
            prodMinText.setText(Integer.toString(selectedProd.getMin()));
            prodMaxText.setText(Integer.toString(selectedProd.getMax()));

            //Populate the prodPartTable with associated parts
            Part returnedPart;
            int index = 0;
            do {
                returnedPart = selectedProd.lookupAssociatedPart(index);
                if (returnedPart != null) {
                    prodPartData.add(returnedPart);
                }
                else {
                    break;
                }
                index++;
            } while (returnedPart != null);

            //Switch to the Modify Product screen
            primaryStage.setScene(modProd);
            //Add all visible elements to the modProdAP AnchorPane when this screen is launched
            modProdAP.getChildren().addAll(prodRect, modProdLabel, prodID, prodName, prodInv, prodPrice, prodMax,
                    prodMin, prodIDText, prodNameText, prodInvText, prodPriceText, prodMaxText, prodMinText,
                    addProdSearchBtn, addProdSearch, addPartTable, addPartBtn, prodPartTable, modProdPartDelBtn,
                    modProdSaveBtn, modProdCancelBtn);
        });

        //Switch back to the main screen once the Save button is hit on the Modify Product screen
        modProdSaveBtn.setOnMouseClicked(e -> {
            //Remove any error messages that may still be on screen from a previous check
            modProdAP.getChildren().removeAll(nameNullProd, invBadIntProd, minMaxBadIntProd, invalidDoubleProd,
                    minOverMaxProd, invNotAllowedProd, noParts, priceTooLow);

            //Get the product we're updating
            Product updateProd = getProduct(Integer.parseInt(prodIDText.getText()), prodData);

            //Verify the name field is not null
            String updateProdName = prodNameText.getText();
            if (updateProdName == null || updateProdName.isEmpty()) {
                modProdAP.getChildren().addAll(nameNullProd);
                return;
            }

            //Include format check for the Inv field, must be a whole number greater than or equal to zero
            int updateProdInv = checkInt(prodInvText.getText());
            if (updateProdInv == -1) {
                modProdAP.getChildren().addAll(invBadIntProd);
                prodInvText.setText("0"); //Default to zero for user
                return;
            }

            //Include a format check for the Min and Max fields, must be whole numbers greater than or equal to zero
            int updateProdMin = checkInt(prodMinText.getText());
            int updateProdMax = checkInt(prodMaxText.getText());
            if (updateProdMin == -1 || updateProdMax == -1) {
                modProdAP.getChildren().addAll(minMaxBadIntProd);
                return;
            }

            //Include a format check for the Price field, must be an int or double with no letters or symbols
            double updateProdPrice = checkDouble(prodPriceText.getText());
            if (updateProdPrice == -1) {
                modProdAP.getChildren().addAll(invalidDoubleProd);
                return;
            }

            //Verify that the minimum amount entered is not greater than the maximum amount
            boolean minUnderMax = verifyMinMax(updateProdMin, updateProdMax);
            if (!minUnderMax) {
                modProdAP.getChildren().addAll(minOverMaxProd);
                return;
            }

            //Verify that the inventory level entered is between the minimum and maximum allowed
            boolean invAllowed = checkInventoryLvl(updateProdInv, updateProdMin, updateProdMax);
            if (!invAllowed) {
                modProdAP.getChildren().addAll(invNotAllowedProd);
                return;
            }

            //Verify the product has at least one part assigned to it
            if (prodPartData.size() == 0){
                modProdAP.getChildren().addAll(noParts);
                return;
            }

            //Verify that the Price entered is equal to or greater than the sum of all associated parts
            if(updateProdPrice < calculatePartPrice(prodPartData)) {
                modProdAP.getChildren().addAll(priceTooLow);
                return;
            }

            //Update attributes with the data from the text fields
            updateProd.setName(updateProdName);
            updateProd.setPrice(updateProdPrice);
            updateProd.setInStock(updateProdInv);
            updateProd.setMin(updateProdMin);
            updateProd.setMax(updateProdMax);

            //Remove any associated parts from the product to avoid duplicates
            boolean removed;
            do {
                removed = updateProd.removeAssociatedPart(0);
            } while (removed);
            //Add the current parts to the product from the prodPartTable
            for (int n = 0; n < prodPartData.size(); n++) {
                updateProd.addAssociatedPart(prodPartData.get(n));
            }

            //Clear all data entered into text fields
            prodNameText.clear();
            prodPriceText.clear();
            prodInvText.clear();
            prodMinText.clear();
            prodMaxText.clear();

            //Remove all parts from the prodPartTable
            prodPartData.clear();

            //Remove all main screen error messages in case they are still on screen from a previous check before
            //getting into this screen
            ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd, noDelProd);

            //Clear the current prodData table to refresh it with updated info
            prodData.clear();

            //Get products from the currentInv Inventory object and add them to the prodData Observable Array List
            int j = 0;
            Product currentProd;
            do {
                currentProd = currentInv.lookupProduct(j);
                if (currentProd != null) {
                    prodData.add(currentProd);
                }
                j++;
            } while (currentProd != null);

            //Set the scene back to main and remove all objects from the anchor pane
            primaryStage.setScene(main);
            modProdAP.getChildren().clear();
        });

        //Switch back to the main screen once the Cancel button is hit on either the Add or Modify Product screen
        modProdCancelBtn.setOnMouseClicked(e -> {
            //Display confirmation dialogue to verify this is what the user intends to do
            modProdAP.getChildren().addAll(canModProdConf, yesCanModProd, noCanModProd);

            //If Yes is clicked clear all data from this screen and return to the main screen
            yesCanModProd.setOnMouseClicked(e2 -> {

                //Clear all data entered into text fields
                prodNameText.clear();
                prodPriceText.clear();
                prodInvText.clear();
                prodMinText.clear();
                prodMaxText.clear();

                //Remove all parts from the prodPartTable
                prodPartData.clear();

                //Remove all main screen error messages in case they are still on screen from a previous check before
                //getting into this screen
                ap.getChildren().removeAll(prodHasParts, delPartConf, yesDelPart, noDelPart, delProdConf, yesDelProd,
                        noDelProd);

                //Set the scene to main and remove all objects from the anchor pane
                primaryStage.setScene(main);
                modProdAP.getChildren().clear();
            });

            //If No is clicked remove the dialogue but don't remove any product data
            noCanModProd.setOnMouseClicked(e3 -> {
                modProdAP.getChildren().removeAll(canModProdConf, yesCanModProd, noCanModProd);
            });
        });


    }


    /*******************************************************************************************************************
     * Other methods called from the start method                                                                      *
     ******************************************************************************************************************/

    //This method is called to create the blue title labels for the top of each screen
    public static Label CreateScreenLabel(String text){
        //Create a new label object
        Label lbl = new Label(text);
        //Set the font, style, and color for the text
        lbl.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 26));
        lbl.setTextFill(Color.MIDNIGHTBLUE);

        return lbl;
    }


    //This method is called to create both of the main screen rectangles surrounding the inner parts and products tables
    public static Rectangle CreateRectangle() {
        //Create a new rectangle object
        Rectangle rect = new Rectangle(725, 375);

        //Set the height and width of the rounded edges
        rect.setArcWidth(20.0);
        rect.setArcHeight(20.0);

        //Make the rectangles transparent with a border
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.GREY);

        return rect;
    }


    //This method provides an efficient way to create standard buttons with the same dimensions
    public static Button CreateButton(String name){
        Button btn = new Button(name);
        btn.setMinSize(100, 40);
        return btn;
    }


    //This method creates the labels for the Add Part and Modify Part screens
    public static Label CreatePartLabel (String text){
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        lbl.setTranslateX(110);

        return lbl;
    }


    //This method creates the labels for the Add Product and Modify Product screens
    public static Label CreateProdLabel (String text){
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        lbl.setTranslateX(60);

        return lbl;
    }


    //This method creates the TextFields for the Add Part and Modify Part screens
    public static TextField CreatePartTextField (String text){
        TextField tf = new TextField();
        tf.setPromptText(text);
        tf.setFont(Font.font("Calibri", FontPosture.ITALIC, 18));
        tf.setPrefWidth(250);
        tf.setPadding(new Insets(10));
        tf.setTranslateX(260);
        tf.setOnMouseClicked(e -> {
            tf.setFont(Font.font("Calibri", FontPosture.REGULAR, 18));
        });

        return tf;
    }


    //This method creates the TextFields for the Add Part and Modify Part screens
    public static TextField CreateProdTextField (String text){
        TextField tf = new TextField();
        tf.setPromptText(text);
        tf.setFont(Font.font("Calibri", FontPosture.ITALIC, 18));
        tf.setPrefWidth(120);
        tf.setPadding(new Insets(10));
        tf.setTranslateX(155);
        tf.setOnMouseClicked(e -> {
            tf.setFont(Font.font("Calibri", FontPosture.REGULAR, 18));
        });

        return tf;
    }


    //This variable and method keep track of the last assigned Part ID and increments it when called
    int currentPartID = 0;
    public int getNextPartID() {
        currentPartID++;
        return currentPartID;
    }


    //This variable and method keep track of the last assigned Product ID and increments it when called
    int currentProdID = 0;
    public int getNextProdID() {
        currentProdID++;
        return currentProdID;
    }


    //This method will return a part in an observable array list based on its part ID number
    public Part getPart(int ID, ObservableList<Part> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPartID() == ID) {
                return list.get(i);
            }
        }
            return null;
    }


    //This method will return a product in an observable array list based on its product ID number
    public Product getProduct(int ID, ObservableList<Product> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProductID() == ID) {
                return list.get(i);
            }
        }
        return null;
    }


    //This method will create an error message Text object when passed a String
    public Text CreateErrMsg (String text) {
        Text newMsg = new Text(text);
        newMsg.setFill(Color.RED);
        return newMsg;
    }


    //This method will verify that the inventory value is between the entered minimum and maximum values
    public boolean checkInventoryLvl (int inv, int min, int max) {
        boolean allowed = false;
        if (inv >= min && inv <= max) {
            allowed = true;
        }

        return allowed;
    }


    //This method will verify the minimum field is not greater than the maximum field. This also verifies the maximum
    //field is greater than or equal to the minimum field.
    public boolean verifyMinMax(int min, int max) {
        boolean verified = false;
        if (min <= max) {
            verified = true;
        }

        return verified;
    }


    //This method verifies a valid double was entered in a text box
    public double checkDouble(String num) {
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    //This method verifies a valid int was entered in a text box
    public int checkInt(String num) {
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    //This method creates the confirmation buttons to be used with the Delete and Cancel buttons
    public static Button CreateConfButton(String name){
        Button btn = new Button(name);
        btn.setMinSize(50, 30);
        return btn;
    }


    //This method calculates the price sum when given an ArrayList of parts
    public static double calculatePartPrice(ObservableList<Part> partList) {
        double sum = 0;
        for (int i = 0; i < partList.size(); i++){
            sum += partList.get(i).getPrice();
        }
        return sum;
    }


    public static void main(String[] args) {

        launch(args);

    }
}