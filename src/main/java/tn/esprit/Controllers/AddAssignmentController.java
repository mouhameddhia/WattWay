package tn.esprit.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.converter.StringConverter;
import javafx.scene.control.*;
import tn.esprit.entities.Assignment;
import tn.esprit.entities.Car;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.AssignmentServices;
import tn.esprit.services.CarServices;
import tn.esprit.services.UserServices;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import tn.esprit.services.MechanicServices;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AddAssignmentController {

    @FXML
    private TextField descriptionAssignmentField;
    @FXML
    private ComboBox<Assignment.Status> statusAssignmentComboBox;
    @FXML
    private ComboBox<Car> carIdComboBox;
    @FXML
    private ListView<Mechanic> mechanicListView;
    @FXML
    private TableColumn<Assignment, String> mechanicsColumn;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private DatePicker datePicker;

    private final AssignmentServices assignmentServices = new AssignmentServices();
    private final CarServices carServices = new CarServices();
    private final MechanicServices mechanicServices = new MechanicServices();

    @FXML
    private void initialize() {
        loadMechanics();
        statusAssignmentComboBox.getItems().addAll(Assignment.Status.values());
        //statusAssignmentComboBox.getItems().addAll(Assignment.Status.values());
        loadCars();

        mechanicListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        mechanicListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Mechanic mechanic, boolean empty) {
                super.updateItem(mechanic, empty);
                if (empty || mechanic == null) {
                    setText(null);
                } else {
                    setText(mechanic.getNameMechanic() + " - " + mechanic.getSpecialityMechanic() + " (ID: " + mechanic.getIdMechanic() + ")");
                }
            }
        });
        carIdComboBox.setCellFactory(lv -> new ListCell<Car>() {
            @Override
            protected void updateItem(Car car, boolean empty) {
                super.updateItem(car, empty);
                if (empty || car == null) {
                    setText(null);
                } else {
                    setText(car.getModelCar() + " (" + car.getBrandCar() + ", " + car.getYearCar() + ")");
                }
            }
        });

    }
    private void loadCars() {
        try {
            List<Car> cars = carServices.getAllCarsUnderRepair();  
            ObservableList<Car> carList = FXCollections.observableArrayList(cars);

            if (carIdComboBox != null) {
                carIdComboBox.setItems(carList);
            } else {
                System.err.println("Error: carIdComboBox is null!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void loadMechanics() {
        try {
            List<Mechanic> mechanicsList = mechanicServices.returnList();
            ObservableList<Mechanic> mechanicsObservable = FXCollections.observableArrayList(mechanicsList);

            mechanicListView.setItems(mechanicsObservable);

            mechanicListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

            mechanicListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Mechanic mechanic, boolean empty) {
                    super.updateItem(mechanic, empty);
                    if (empty || mechanic == null) {
                        setText(null);
                    } else {
                        setText(mechanic.getNameMechanic() + " - " + mechanic.getSpecialityMechanic() + " (ID: " + mechanic.getIdMechanic() + ")");
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void saveAssignment() {
        String description = descriptionAssignmentField.getText();
        Assignment.Status status = statusAssignmentComboBox.getValue();
        Car selectedCar = carIdComboBox.getValue();
        ObservableList<Mechanic> selectedMechanics = mechanicListView.getSelectionModel().getSelectedItems();

        if (description.isEmpty() || status == null  || selectedMechanics.isEmpty()) {
            showAlert("Empty","Please fill all fields.");
            return;
        }
        if (selectedCar == null) {
            showAlert("Empty ID Car","Please select ID Car.");
            return;
        }
        if (datePicker.getValue() == null) {
            showAlert("Invalid Date", "Please select an assignment date.");
            return;
        }
        LocalDateTime selectedDate = datePicker.getValue().atStartOfDay();

        try {
            //int idUser = Integer.parseInt(idUserText);

            Assignment newAssignment = new Assignment();
            newAssignment.setDescriptionAssignment(description);
            newAssignment.setStatusAssignment(status);
            newAssignment.setIdCar(selectedCar.getIdCar());
            newAssignment.setMechanics(new ArrayList<>(selectedMechanics));
            newAssignment.setDateAssignment(selectedDate);

            assignmentServices.addP(newAssignment);
            closeWindow();
        } catch (NumberFormatException e) {
            System.out.println("Error: User ID must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        if (stage != null) {
            System.out.println("Closing window...");
            stage.close();
        } else {
            System.out.println("Error: Stage is null");
        }
    }
}
