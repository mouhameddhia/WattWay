package tn.esprit.controller;

import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import tn.esprit.entities.Bill;
import tn.esprit.entities.Car;
import tn.esprit.services.BillServices;
import tn.esprit.services.CarServices;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.HashMap;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class ShowBillUserInterface {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/BrowseCar.fxml"));
    @FXML
    private Label brandCar;
    @FXML
    private Label dateBill;
    @FXML
    private Label idBill;
    @FXML
    private Label kilometrageCar;
    @FXML
    private Label modelCar;
    @FXML
    private Label totalAmountBill;
    @FXML
    private Label yearCar;
    @FXML
    private Label exitMessage;
    private static final String API_KEY = "8f32ca8b8383e81985184ffe1ae7d658b660c08dc3d362b2f835b8ccffab48fd";
    private static final Integer TEMPLATE_ID = 1342722;
    private static final String API_SECRET = "96651c2170f5a6e93d8fa2e24211c8328f44bb8b70ee534444dda13cc84046f5";

    @FXML
    void payBill(ActionEvent event) {
        BillServices bs = new BillServices();
        Car car;
        CarServices cs = new CarServices();

        if (Float.parseFloat(totalAmountBill.getText()) > 10000) {
            openPDFInBrowser(generatePDF(Integer.parseInt(idBill.getText()), dateBill.getText(), brandCar.getText() + " " + modelCar.getText(), Float.parseFloat(totalAmountBill.getText())));
        }
        try {
            bs.payBill(Integer.parseInt(idBill.getText()));
            cs.updateStatusCar("not available",bs.getCarByBillId(Integer.parseInt(idBill.getText())).getIdCar());
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        exitMessage.setVisible(true);
    }

    public void setData(Bill bill) {
        Car car = new Car();
        BillServices bs = new BillServices();
        try {
            car = bs.getCarByBillId(bill.getIdBill());
            idBill.setText(Integer.toString(bill.getIdBill()));
            dateBill.setText(bill.getDateBill().toString());
            totalAmountBill.setText(Float.toString(bill.getTotalAmountBill()));
            brandCar.setText(car.getBrandCar());
            modelCar.setText(car.getModelCar());
            yearCar.setText(Integer.toString(car.getYearCar()));
            kilometrageCar.setText(Integer.toString(car.getKilometrageCar()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String generatePDF(int id, String date, String car, float total) {
        String apiUrl = "https://us1.pdfgeneratorapi.com/api/v4/documents/generate";
        JSONObject jsonPayload = new JSONObject();
        JSONObject template = new JSONObject();
        template.put("id", TEMPLATE_ID);
        JSONObject data = new JSONObject();
        data.put("idBill", id);
        data.put("dateBill", date);
        data.put("car", car.replace(",", ""));
        data.put("priceCar", total / 1.08);
        data.put("dealerFees", 500);
        data.put("deliveryFees", 500);
        data.put("registrationFees", 150);
        data.put("taxBill", (total * 0.08) / 1.08);
        data.put("totalAmountBill", total);

        template.put("data", data);

        jsonPayload.put("template", template);
        jsonPayload.put("format", "pdf");
        jsonPayload.put("output", "url");

        System.out.println(jsonPayload.toString());
        try {
            HttpClient client = HttpClient.newHttpClient();
            String jwt = generateJWT(API_KEY, API_SECRET);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + jwt)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getString("response");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





    public static String generateJWT(String apiKey, String apiSecret) {
        Key key = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());

        String jwt = Jwts.builder()
                .setIssuer(apiKey)
                .setSubject("pdfgeneratorapi")
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }
    public static void openPDFInBrowser(String pdfUrl) {
        try {
            URI uri = new URI(pdfUrl);
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(uri);
            } else {
                System.out.println("Desktop is not supported on this platform.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to open PDF in browser.");
        }
    }

}
