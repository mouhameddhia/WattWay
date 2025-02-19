package tn.esprit.entities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Submission {
    private int idSubmission;
    private String description;
    private  STATUS status;  // Should match ENUM values: 'Pending', 'Under Review', 'Responded', 'Closed'
    private URGENCYLEVEL urgencyLevel; // Should match ENUM values: 'Low', 'Medium', 'High'
    private Date dateSubmission;
    private int idCar;
    private int idUser;
    private List<Response> responses = new ArrayList<>(); // Initialize to an empty list

    // Getters and Setters
    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
    public void setIdCar(int idCar) {
        this.idCar = idCar;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setCarId(int carId) {
    }

    public void setUserId(int userId) {
    }



    public enum STATUS {
       PENDING,APPROVED,RESPONDED
        // Pending, Approved, Responded
    }

    public enum URGENCYLEVEL {
        //Low, Medium, High
        LOW,MEDIUM,HIGH
    }

    public Submission(String description, Date dateSubmission, int idCar, int idUser) {
        this.description = description;
        this.dateSubmission = dateSubmission;
        this.idCar = idCar;
        this.idUser = idUser;
    }

    public Submission(String description, STATUS status, URGENCYLEVEL urgencyLevel, Date dateSubmission, int idCar, int idUser) {
        this.description = description;
        this.status = status;
        this.urgencyLevel = urgencyLevel;
        this.dateSubmission = dateSubmission;
        this.idCar = idCar;
        this.idUser = idUser;
    }

    public Submission(int idSubmission, String description, Date dateSubmission, int idCar, int idUser) {
        this.idSubmission = idSubmission;
        this.description = description;
        this.dateSubmission = dateSubmission;
        this.idCar = idCar;
        this.idUser = idUser;
    }

    public Submission(int idSubmission, String description, STATUS status, URGENCYLEVEL urgencyLevel, Date dateSubmission, int idCar, int idUser) {
        this.idSubmission = idSubmission;
        this.description = description;
        this.status = status;
        this.urgencyLevel = urgencyLevel;
        this.dateSubmission = dateSubmission;
        this.idCar = idCar;
        this.idUser = idUser;
    }

    public Submission() {}


    public int getIdSubmission() {
        return idSubmission;
    }

    public void setIdSubmission(int idSubmission) {
        this.idSubmission = idSubmission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public URGENCYLEVEL getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(URGENCYLEVEL urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public Date getDateSubmission() {
        return dateSubmission;
    }

    public void setDateSubmission(Date dateSubmission) {
        this.dateSubmission = dateSubmission;
    }



    public int getIdCar() {
        return idCar;
    }

    public int getIdUser() {
        return idUser;
    }



}
