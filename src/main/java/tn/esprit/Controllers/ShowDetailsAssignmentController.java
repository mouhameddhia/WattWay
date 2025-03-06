package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Assignment;
import tn.esprit.entities.Car;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.CarServices;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ShowDetailsAssignmentController {

    @FXML
    private Label descriptionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ListView<String> mechanicListView;
    @FXML
    private Button closeButton;

    // Car Details
    @FXML
    private Label carModelLabel;
    @FXML
    private Label carBrandLabel;
    @FXML
    private Label carYearLabel;
    @FXML
    private Label carPriceLabel;
    @FXML
    private Label carStatusLabel;
    @FXML
    private Label carKilometrageLabel;
    @FXML
    private ImageView carImageView;
    @FXML
    private Label dateLabel;

    private final CarServices carServices = new CarServices();

    public void initData(Assignment assignment) {
        descriptionLabel.setText(assignment.getDescriptionAssignment());
        statusLabel.setText(assignment.getStatusAssignment().name());

        // Display assigned mechanics
        for (Mechanic mechanic : assignment.getMechanics()) {
            mechanicListView.getItems().add(mechanic.getNameMechanic() + " - " + mechanic.getSpecialityMechanic().name());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = assignment.getDateAssignment().format(formatter);
        dateLabel.setText(formattedDate);

        // Load and display car details
        try {
            Car car = carServices.getCarById(assignment.getIdCar());
            if (car != null) {
                carModelLabel.setText(car.getModelCar());
                carBrandLabel.setText(car.getBrandCar());
                carYearLabel.setText(String.valueOf(car.getYearCar()));
                carPriceLabel.setText(car.getPriceCar() + " $");
                carStatusLabel.setText(car.getStatusCar());
                carKilometrageLabel.setText(car.getKilometrageCar() + " km");

                if (car.getImgCar() != null && !car.getImgCar().isEmpty()) {
                    Image carImage = new Image(car.getImgCar());
                    carImageView.setImage(carImage);
                }
            } else {
                carModelLabel.setText("Unknown");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
