package tn.esprit.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.MechanicServices;

import java.sql.SQLException;
import java.util.List;
import javafx.application.Platform;

public class MechanicStatisticsController {

    @FXML
    private BarChart<String, Number> barChart;

    private MechanicServices mechanicServices = new MechanicServices();

    @FXML
    public void initialize() {
        try {
            // Retrieve all mechanics from the database and add data to chart.
            List<Mechanic> mechanics = mechanicServices.returnList();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            barChart.setLegendVisible(false);

            for (Mechanic mechanic : mechanics) {
                XYChart.Data<String, Number> dataItem = new XYChart.Data<>(mechanic.getNameMechanic(), mechanic.getCarsRepaired());
                series.getData().add(dataItem);
            }
            barChart.getData().add(series);

            // Set the x-axis tick labels to white.
            CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
            xAxis.setTickLabelFill(Color.WHITE);
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();

            yAxis.setMinorTickCount(0);
            yAxis.setTickUnit(1);


            // Force CSS to be applied and layout to occur so that nodes are created.
            barChart.applyCss();
            barChart.layout();

            // Attach mouse-click handlers after the chart nodes are created.
            Platform.runLater(() -> {
                for (XYChart.Data<String, Number> dataItem : series.getData()) {
                    if (dataItem.getNode() != null) {
                        System.out.println("Attaching click handlers");
                        dataItem.getNode().setOnMouseClicked(event -> {
                            // Find the mechanic based on the x-axis value (mechanic name).
                            Mechanic selectedMechanic = mechanics.stream()
                                    .filter(m -> m.getNameMechanic().equals(dataItem.getXValue()))
                                    .findFirst().orElse(null);
                            if (selectedMechanic != null) {
                                System.out.println("Clicked on mechanic: " + selectedMechanic.getNameMechanic());
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDetailsMechanic.fxml"));
                                    Parent root = loader.load();
                                    ShowDetailsMechanicController detailsController = loader.getController();
                                    detailsController.initData(selectedMechanic);
                                    Stage stage = new Stage();
                                    stage.setScene(new Scene(root));
                                    stage.setTitle("Mechanic Details");
                                    stage.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("No Mechanic selected");
                            }
                        });
                    } else {
                        System.out.println("Node for " + dataItem.getXValue() + " not available yet.");
                    }
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
