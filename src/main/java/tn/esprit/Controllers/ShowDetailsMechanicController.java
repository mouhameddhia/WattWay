package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Mechanic;

import java.io.File;

public class ShowDetailsMechanicController {

    @FXML
    private Label nameMechanicLabel;
    @FXML
    private Label specialityMechanicLabel;
    @FXML
    private Label emailMechanicLabel;
    @FXML
    private Label carsRepairedLabel;
    @FXML
    private ImageView mechanicImageView;

    private Mechanic mechanic;

    public void initData(Mechanic mechanic) {
        this.mechanic = mechanic;

        nameMechanicLabel.setText(mechanic.getNameMechanic());
        specialityMechanicLabel.setText(mechanic.getSpecialityMechanic().name());
        emailMechanicLabel.setText(mechanic.getEmailMechanic());
        carsRepairedLabel.setText(String.valueOf(mechanic.getCarsRepaired()));

        // Load Image
        if (mechanic.getImgMechanic() != null && !mechanic.getImgMechanic().isEmpty()) {
            File file = new File(mechanic.getImgMechanic());
            if (file.exists()) {
                mechanicImageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) nameMechanicLabel.getScene().getWindow();
        stage.close();
    }
}
