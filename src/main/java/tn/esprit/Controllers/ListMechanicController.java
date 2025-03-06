package tn.esprit.Controllers;

import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.ExportPDFService;
import tn.esprit.services.MechanicServices;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListMechanicController {

    @FXML
    private TableView<Mechanic> mechanicsTableView;

    // We'll replace the multiple columns with a single "infoColumn" plus the existing "actionColumn"
    @FXML
    private TableColumn<Mechanic, Mechanic> infoColumn;

    @FXML
    private TableColumn<Mechanic, Void> actionColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button exportButton;

    private final MechanicServices mechanicServices;

    public ListMechanicController() {
        this.mechanicServices = new MechanicServices();
    }

    @FXML
    private void initialize() throws SQLException {
        // 1) Setup the "infoColumn" to display the entire Mechanic object
        infoColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

        // 2) Create a custom cell that shows an avatar + name + email
        infoColumn.setCellFactory(col -> new TableCell<Mechanic, Mechanic>() {
            private final HBox container = new HBox(10);
            private final ImageView avatarView = new ImageView();
            private final VBox textContainer = new VBox(2);
            private final Label nameLabel = new Label();
            private final Label emailLabel = new Label();

            {
                // Make the avatar circular, 40x40
                avatarView.setFitWidth(40);
                avatarView.setFitHeight(40);
                Circle clip = new Circle(20, 20, 20);
                avatarView.setClip(clip);

                // Example styling: bold white for name, gray for email
                nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                emailLabel.setStyle("-fx-text-fill: #aaaaaa;");

                textContainer.getChildren().addAll(nameLabel, emailLabel);
                container.getChildren().addAll(avatarView, textContainer);
                container.setStyle("-fx-alignment: center-left;");
            }

            @Override
            protected void updateItem(Mechanic mechanic, boolean empty) {
                super.updateItem(mechanic, empty);
                if (empty || mechanic == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(mechanic.getNameMechanic());
                    emailLabel.setText(mechanic.getEmailMechanic());

                    // Load the mechanic's avatar from mechanic.getImgMechanic()
                    if (mechanic.getImgMechanic() != null && !mechanic.getImgMechanic().isEmpty()) {
                        File file = new File(mechanic.getImgMechanic());
                        if (file.exists()) {
                            Image avatar = new Image(file.toURI().toString(), 40, 40, true, true);
                            avatarView.setImage(avatar);
                        } else {
                            avatarView.setImage(null);
                        }
                    } else {
                        avatarView.setImage(null);
                    }

                    setGraphic(container);
                }
            }
        });

        // 3) Keep the action column for Update & Delete
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button();
            private final Button deleteButton = new Button();

            {
                // Load update icon from resources and set it to the update button
                Image updateImage = new Image(getClass().getResourceAsStream("/icons/update-icon.png"));
                ImageView updateIcon = new ImageView(updateImage);
                updateIcon.setFitHeight(16); // Adjust icon height
                updateIcon.setPreserveRatio(true);
                updateButton.setGraphic(updateIcon);

                // Load delete icon from resources and set it to the delete button
                Image deleteImage = new Image(getClass().getResourceAsStream("/icons/delete-icon.png"));
                ImageView deleteIcon = new ImageView(deleteImage);
                deleteIcon.setFitHeight(16); // Adjust icon height
                deleteIcon.setPreserveRatio(true);
                deleteButton.setGraphic(deleteIcon);

                // Optionally set preferred widths for the buttons
                updateButton.setPrefWidth(50);
                deleteButton.setPrefWidth(50);
                updateButton.getStyleClass().add("primary-button");
                deleteButton.getStyleClass().add("delete-button");

                updateButton.setOnAction(event -> {
                    Mechanic mechanic = getTableView().getItems().get(getIndex());
                    updateMechanic(mechanic);
                });

                deleteButton.setOnAction(event -> {
                    Mechanic mechanic = getTableView().getItems().get(getIndex());
                    try {
                        deleteMechanic(mechanic);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, updateButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        // 4) Double-click row -> showMechanicDetails
        mechanicsTableView.setRowFactory(tv -> {
            TableRow<Mechanic> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Mechanic mechanic = row.getItem();
                    showMechanicDetails(mechanic);
                }
            });
            return row;
        });

        // Finally, load mechanics into the table
        refreshMechanicsTable();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchMechanics();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void showMechanicDetails(Mechanic mechanic) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDetailsMechanic.fxml"));
            Parent root = loader.load();

            ShowDetailsMechanicController controller = loader.getController();
            controller.initData(mechanic);

            Stage stage = new Stage();
            stage.setTitle("Mechanic Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchMechanics() throws SQLException {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            mechanicsTableView.setItems(FXCollections.observableArrayList(mechanicServices.returnList()));
        } else {
            ObservableList<Mechanic> filteredList = FXCollections.observableArrayList();
            for (Mechanic m : mechanicServices.returnList()) {
                if (m.getNameMechanic().toLowerCase().contains(searchText) ||
                        m.getEmailMechanic().toLowerCase().contains(searchText) ||
                        String.valueOf(m.getCarsRepaired()).contains(searchText)) {
                    filteredList.add(m);
                }
            }
            mechanicsTableView.setItems(filteredList);
        }
    }

    private void updateMechanic(Mechanic mechanic) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateMechanicInterface.fxml"));
            Parent root = loader.load();
            UpdateMechanicController controller = loader.getController();
            controller.initData(mechanic);
            Stage stage = new Stage();
            stage.setTitle("Update Mechanic");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> {
                try {
                    refreshMechanicsTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteMechanic(Mechanic mechanic) throws SQLException {
        mechanicServices.delete(mechanic);
        mechanicsTableView.getItems().remove(mechanic);
        System.out.println("Deleted mechanic with ID: " + mechanic.getIdMechanic());
    }

    @FXML
    private void addMechanic() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMechanic.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Mechanic");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> {
                try {
                    refreshMechanicsTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshMechanicsTable() throws SQLException {
        ObservableList<Mechanic> updatedMechanics = FXCollections.observableArrayList(mechanicServices.returnList());
        mechanicsTableView.setItems(updatedMechanics);
    }

    @FXML
    private void switchToAssignments() {
        // Get the current root node from an element in your current scene (e.g., mechanicsTableView)
        Parent currentRoot = mechanicsTableView.getScene().getRoot();

        // Create a fade out transition for the current scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                // Load the new interface from FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListAssignmentInterface.fxml"));
                Parent newRoot = loader.load();

                // Get the stage and replace the scene root with the new interface
                Stage stage = (Stage) currentRoot.getScene().getWindow();
                stage.getScene().setRoot(newRoot);
                stage.setTitle("List of Assignments");

                // Create a fade in transition for the new scene
                FadeTransition fadeIn = new FadeTransition(Duration.millis(100), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }


    @FXML
    private void openMechanicStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MechanicStatistics.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mechanic Statistics");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Mechanics Report");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) exportButton.getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                ExportPDFService exportService = new ExportPDFService();
                exportService.exportMechanicsToPDF(file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "PDF exported successfully to " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
