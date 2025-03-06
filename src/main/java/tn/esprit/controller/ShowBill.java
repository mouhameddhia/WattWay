package tn.esprit.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Bill;
import tn.esprit.entities.Car;
import tn.esprit.entities.Warehouse;
import tn.esprit.services.BillServices;
import tn.esprit.services.CarServices;
import tn.esprit.services.WarehouseServices;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ShowBill {

    @FXML
    private AnchorPane updateAnchorPane;
    @FXML
    private TableColumn<Bill, Void> tableViewDeleteBill;
    @FXML
    private TableColumn<Bill,Void> tableViewUpdateBill;
    @FXML
    private ImageView imageViewBill;
    @FXML
    private TableColumn<Bill, Void> tableViewCarDetailsBill;

    @FXML
    private TableColumn<Bill, Date> tableViewDateBill;

    @FXML
    private TableColumn<Bill, String> tableViewStatusBill;

    @FXML
    private TableView<Bill> tableViewBill;

    @FXML
    private TableColumn<Bill, Float> tableViewTotalAmountBill;
    @FXML
    private TableColumn<Car, String> tableViewBrandCar;
    @FXML
    private TableView<Car> tableViewCar;
    @FXML
    private TableColumn<Car, Float> tableViewKilometrageCar;
    @FXML
    private TableColumn<Car, String> tableViewModelCar;
    @FXML
    private TableColumn<Car, String> tableViewStatusCar;
    @FXML
    private TableColumn<Car, String> tableViewStoredWarehouse;
    @FXML
    private TableColumn<Car, Integer> tableViewYearCar;
    @FXML
    private TableColumn<Car, Float> tableViewPriceCar;
    @FXML
    private CheckBox updateStatusBill;
    @FXML
    private LineChart<String, Float> lineChartBill;
    @FXML
    private MenuButton menuButtonYearBill;
    int importBillId=-1;
    LocalDate importDateId=null;
    int importCarId=-1;
    @FXML
    private TextField updateTotalAmountBill;
    @FXML
    private TableColumn<Bill, String> tableViewClientNameBill;
    public void initialize(){
        BillServices bs = new BillServices();
        try {
            List<Integer> years= bs.getYearsBill();
            for(Integer year:years){
                MenuItem menuItem = new MenuItem(Integer.toString(year));
                menuItem.setOnAction(event -> {
                        menuButtonYearBill.setText(Integer.toString(year));
                        updateLineChart(year);
                });
                menuButtonYearBill.getItems().add(menuItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        imageViewBill.setImage(null);
        tableViewCar.setVisible(false);
        updateAnchorPane.setVisible(false);
        updateAnchorPane.setManaged(false);
        try {
            ObservableList<Bill> observableList = FXCollections.observableArrayList(bs.retrieve());
            tableViewBill.setItems(observableList);
            tableViewDateBill.setCellValueFactory(new PropertyValueFactory<>("dateBill"));
            tableViewClientNameBill.setCellValueFactory(cellData -> {
                try {
                    String name=bs.getUserNameByBillId(cellData.getValue().getIdBill());
                    return new SimpleStringProperty(name);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            tableViewStatusBill.setCellValueFactory(cellData -> {
                int statusBill = cellData.getValue().getStatusBill();
                String status = null;
                if (statusBill == 1) {
                    status = "Paid";
                }
                else {
                    status = "Not Paid";
                }
                return new SimpleStringProperty(status);
            });
            tableViewTotalAmountBill.setCellValueFactory(new PropertyValueFactory<>("totalAmountBill"));

            tableViewCarDetailsBill.setCellFactory(tc -> new TableCell<>() {
                private final Button detailsButton = new Button("Show Details");

                {
                    detailsButton.setOnAction(event -> {
                        try {
                            int billId = tableViewBill.getItems().get(getIndex()).getIdBill();
                            Car car = bs.getCarByBillId(billId);
                            showTableView(car);
                        } catch (SQLException e) {
                            e.printStackTrace(); // Log the error instead of throwing
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        Bill bill = tableViewBill.getItems().get(getIndex());
                        boolean isPaid = bill.getStatusBill() == 1; // Paid -> No button

                        if (isPaid) {
                            setGraphic(null);
                        } else {
                            setGraphic(detailsButton);
                        }
                    }
                }
            });
            tableViewDeleteBill.setCellFactory(tc -> new TableCell<>() {
                private final Button deleteButton = new Button("Delete");
                {
                    deleteButton.setOnAction(event -> {
                        Bill bill=new Bill();
                        bill= tableViewBill.getItems().get(getIndex());
                        try {
                            bs.delete(bill.getIdBill());
                            initialize();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }


                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                        HBox hBox = new HBox(deleteButton);
                        hBox.setAlignment(Pos.CENTER);
                        setGraphic(hBox);
                    }
                }

            });
            tableViewUpdateBill.setCellFactory(tc -> new TableCell<>() {
                private final Button importButton = new Button("Update");

                {
                    importButton.setOnAction(event -> {
                        Bill bill=new Bill();
                        bill= tableViewBill.getItems().get(getIndex());
                        updateTotalAmountBill.setText(Float.toString(bill.getTotalAmountBill()));
                        importBillId=bill.getIdBill();
                        importDateId=bill.getDateBill();
                        importCarId=bill.getIdCar();
                        if (bill.getStatusBill() == 1)
                            updateStatusBill.setSelected(true);
                        else
                            updateStatusBill.setSelected(false);
                        updateAnchorPane.setVisible(true);
                        updateAnchorPane.setManaged(true);

                    });
                }


                @Override
                protected void updateItem (Void item,boolean empty){
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        importButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                        HBox hBox = new HBox(importButton);
                        hBox.setAlignment(Pos.CENTER);
                        setGraphic(hBox);
                    }
                }
            });
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void showTableView(Car car){
        CarServices cs = new CarServices();
        ObservableList<Car> observableList = FXCollections.observableArrayList(car);
        tableViewCar.setItems(observableList);
        tableViewBrandCar.setCellValueFactory(new PropertyValueFactory<>("brandCar"));
        tableViewModelCar.setCellValueFactory(new PropertyValueFactory<>("modelCar"));
        tableViewStatusCar.setCellValueFactory(new PropertyValueFactory<>("statusCar"));
        tableViewKilometrageCar.setCellValueFactory(new PropertyValueFactory<>("kilometrageCar"));
        tableViewYearCar.setCellValueFactory(new PropertyValueFactory<>("yearCar"));
        tableViewPriceCar.setCellValueFactory(new PropertyValueFactory<>("priceCar"));
        if(car.getImgCar()!=null){
            loadImageFromPath(car.getImgCar());
        }
        else{
            imageViewBill.setImage(null);
        }
        tableViewStoredWarehouse.setCellValueFactory(cellData -> {
            WarehouseServices warehouseServices=new WarehouseServices();
            int warehouseId = cellData.getValue().getIdWarehouse();
            String address = null;
            try {
                address = warehouseServices.getWarehouseAddressById(warehouseId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return new SimpleStringProperty(address);
        });

        tableViewCar.setVisible(true);
    }
    void loadImageFromPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            Image image = new Image(file.toURI().toString());
            imageViewBill.setImage(image);

        } else {
            System.out.println("File not found: " + path);
        }
    }
    @FXML
    void updateBill(ActionEvent event) {
        int flag;
        Alert alert= new Alert(Alert.AlertType.INFORMATION);
        if(updateTotalAmountBill.getText().length()==0 || Float.parseFloat(updateTotalAmountBill.getText())<0){
            alert.setTitle("Fatal error");
            alert.setHeaderText("Bill cannot be set");
            alert.setContentText("Total amount is a positive number");
            alert.showAndWait();
        }
        else{
            if(updateStatusBill.isSelected())
                flag=1;
            else
                flag=0;
            Bill bill = new Bill(flag,importCarId,Float.parseFloat(updateTotalAmountBill.getText()),importDateId,importBillId);
            BillServices bs = new BillServices();
            try {
                bs.update(bill);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        initialize();
    }
    @FXML
    void openAddCar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddCar.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Car");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void openAddWarehouse(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddWarehouse.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Warehouse");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void openShowBill(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ShowBill.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("List of bills");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateLineChart(int year) {
        BillServices bs = new BillServices();
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        XYChart.Series<String, Float> series = new XYChart.Series<>();
        try {
            series.setName("Total revenue in "+year+": "+bs.sumBillByYear(year)+"DT");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        lineChartBill.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) lineChartBill.getXAxis();
        xAxis.setCategories(FXCollections.observableArrayList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"));
        for (int i = 0; i < 12; i++) {
            try {
                series.getData().add(new XYChart.Data<>(months[i], bs.sumBillByMonth(year, i + 1)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        lineChartBill.getData().add(series);
    }
}
