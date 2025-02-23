package tn.esprit.services;

import org.json.JSONObject;
import tn.esprit.entities.Car;
import tn.esprit.utils.MyDatabase;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.asynchttpclient.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class CarServices implements IServiceH<Car>{
    private static final String API_URL = "https://vehicle-make-and-model-recognition.p.rapidapi.com/v1";
    private static final String API_KEY = "0a864a91d7msha1d35de01910cc6p19f755jsn9f219a71c447";
    private static final String API_HOST = "vehicle-make-and-model-recognition.p.rapidapi.com";
    private static final String IMG_BB_API_KEY = "ca7d6d020ee239834d7d224b04a1dda8"; // Replace with your ImgBB API key
    private static final String IMG_BB_UPLOAD_URL = "https://api.imgbb.com/1/upload";
    public CarServices() {}
    Connection conn= MyDatabase.getInstance().getCon();
    @Override
    public void addP(Car car)throws SQLException{}

    public List<Car> retrieve() throws SQLException {
       List<Car> cars = new ArrayList<Car>();
       String sql = "select * from car";
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(sql);
       while (rs.next()) {
           Car car = new Car();
           car.setIdCar(rs.getInt("idCar"));
           car.setModelCar(rs.getString("modelCar"));
           car.setBrandCar(rs.getString("brandCar"));
           car.setYearCar(rs.getInt("yearCar"));
           car.setPriceCar(rs.getFloat("priceCar"));
           car.setKilometrageCar(rs.getInt("kilometrageCar"));
           car.setStatusCar(rs.getString("statusCar"));
           car.setIdWarehouse(rs.getInt("idWarehouse"));
           car.setImgCar(rs.getString("imgCar"));
           cars.add(car);
       }
       return cars;

    }
    public List<Car> retrieveAvailable() throws SQLException {
        List<Car> cars = new ArrayList<Car>();
        String sql = "select * from car WHERE statusCar = 'available'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Car car = new Car();
            car.setIdCar(rs.getInt("idCar"));
            car.setModelCar(rs.getString("modelCar"));
            car.setBrandCar(rs.getString("brandCar"));
            car.setYearCar(rs.getInt("yearCar"));
            car.setPriceCar(rs.getFloat("priceCar"));
            car.setKilometrageCar(rs.getInt("kilometrageCar"));
            car.setStatusCar(rs.getString("statusCar"));
            car.setIdWarehouse(rs.getInt("idWarehouse"));
            car.setImgCar(rs.getString("imgCar"));
            cars.add(car);
        }
        return cars;

    }
    public int getNumberCarsByWarehouse(int idWarehouse) throws SQLException {
        int nbCars = 0;
        String query = "SELECT * FROM car WHERE idWarehouse = " + idWarehouse;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            nbCars++;
        }
        return nbCars;
    }
    @Override
    public void add(Car car) throws SQLException {
        String query = "INSERT INTO `car`(`modelCar`, `brandCar`, `yearCar`, `priceCar`, `statusCar`, `kilometrageCar`, `idWarehouse`,`imgCar`) " +
                "VALUES ('"+ car.getModelCar()+"','"+car.getBrandCar()+"','"+car.getYearCar()+"','"+car.getPriceCar()+"'" +
                ",'"+car.getStatusCar()+"','"+car.getKilometrageCar()+"','"+car.getIdWarehouse()+"','"+car.getImgCar()+"')";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Car added successfully");

    }

    @Override
    public void update(Car car) throws SQLException {
        String query="UPDATE `car` SET `modelCar`= ?,`brandCar`= ?,`yearCar`=?," +
                "`priceCar`= ?,`statusCar`= ? ,`kilometrageCar`=? , `idWarehouse`=?,`imgCar`=? WHERE idCar = ?";
        PreparedStatement prstmt= conn.prepareStatement(query);
        prstmt.setString(1, car.getModelCar());
        prstmt.setString(2, car.getBrandCar());
        prstmt.setInt(3, car.getYearCar());
        prstmt.setFloat(4, car.getPriceCar());
        prstmt.setString(5, car.getStatusCar());
        prstmt.setInt(6, car.getKilometrageCar());
        prstmt.setInt(9, car.getIdCar());
        prstmt.setString(8, car.getImgCar());
        prstmt.setInt(7, car.getIdWarehouse());
        prstmt.executeUpdate();
        System.out.println("Car updated successfully");

    }

    @Override
    public void delete(Car car)throws SQLException{}

    public void delete(int idCar) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM `car` WHERE idCar = '" + idCar + "'");
        System.out.println("Car deleted successfully");
    }
    public Car showCarById(int id) throws SQLException{
        Car car = new Car();
        String query="SELECT* FROM car WHERE idCar=?";
        PreparedStatement prsmt = conn.prepareStatement(query);
        ResultSet rs= prsmt.executeQuery();
        while (rs.next()) {
            car.setIdCar(rs.getInt("idCar"));
            car.setModelCar(rs.getString("modelCar"));
            car.setBrandCar(rs.getString("brandCar"));
            car.setYearCar(rs.getInt("yearCar"));
            car.setPriceCar(rs.getFloat("priceCar"));
            car.setStatusCar(rs.getString("statusCar"));
            car.setKilometrageCar(rs.getInt("kilometrageCar"));
            car.setIdWarehouse(rs.getInt("idWarehouse"));
            car.setImgCar(rs.getString("imgCar"));
        }
        return car;
    }
    public void updateStatusCar(String statusCar , int idCar) throws SQLException {
        String query = "UPDATE car SET statusCar=? WHERE idCar=?";
        PreparedStatement prsmt = conn.prepareStatement(query);
        prsmt.setString(1, statusCar);
        prsmt.setInt(2, idCar);
        prsmt.executeUpdate();
    }
    public List<Car> returnList() throws SQLException{
        return null;
    }
    //API REALM
    public String uploadImageIMGBB(String imagePath) {
        try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

            ListenableFuture<Response> future = client.preparePost(IMG_BB_UPLOAD_URL)
                    .addQueryParam("key", IMG_BB_API_KEY)
                    .addFormParam("image", encodedImage)
                    .execute();

            Response response = future.get(); // Wait for response

            if (response.getStatusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.getResponseBody());
                return jsonResponse.getJSONObject("data").getString("url"); // Get image URL
            } else {
                System.out.println("Upload failed: " + response.getResponseBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Car recognizeCar(String imageUrl) {
        Car car = new Car();
        try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
            ListenableFuture<Response> future = client.preparePost(API_URL)
                    .setHeader("x-rapidapi-key", API_KEY)
                    .setHeader("x-rapidapi-host", "vehicle-make-and-model-recognition.p.rapidapi.com")
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setBody("inputurl=" + imageUrl)
                    .execute();

            Response response = future.get();

            if (response.getStatusCode() == 200) {
                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.getResponseBody());
                if (jsonResponse.getString("status").equals("SUCCESS")) {
                    JSONObject vehicle = jsonResponse.getJSONObject("vehicle");

                    String make = vehicle.getString("make");
                    String model = vehicle.getString("model");
                    String years = vehicle.getString("years");
                    car.setBrandCar(make);
                    car.setYearCar(Integer.parseInt(years.substring(0,4)));

                    car.setModelCar(model);

                } else {
                    System.out.println("Recognition failed: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("API request failed: " + response.getResponseBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return car;
    }
}
