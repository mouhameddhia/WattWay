package tn.esprit.entities;

import java.sql.Date;

public class Response {
    private int idResponse;
    private String message;
    private Date dateResponse;
    private TYPERESPONSE typeResponse;
    private int idUser;
    private int idSubmission;



    public enum TYPERESPONSE {ACKNOWLEDGMENT, RESOLUTION, CLARIFICATIONREQUEST}


    public Response() {}


    public Response(int idResponse, String message, Date dateResponse, TYPERESPONSE typeResponse, int idUser, int idSubmission) {
        this.idResponse = idResponse;
        this.message = message;
        this.dateResponse = dateResponse;
        this.typeResponse = typeResponse;
        this.idUser = idUser;
        this.idSubmission = idSubmission;
    }

    public Response(String message, Date dateResponse, TYPERESPONSE typeResponse, int idUser, int idSubmission) {
        this.message = message;
        this.dateResponse = dateResponse;
        this.typeResponse = typeResponse;
        this.idUser = idUser;
        this.idSubmission = idSubmission;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setIdSubmission(int idSubmission) {
        this.idSubmission = idSubmission;
    }

    public int getIdResponse() {
        return idResponse;
    }

    public void setIdResponse(int idResponse) {
        this.idResponse = idResponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateResponse() {
        return dateResponse;
    }

    public void setDateResponse(Date dateResponse) {
        this.dateResponse = dateResponse;
    }

    public TYPERESPONSE getTypeResponse() {
        return typeResponse;
    }

    public void setTypeResponse(TYPERESPONSE typeResponse) {
        this.typeResponse = typeResponse;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getIdSubmission() {
        return idSubmission;
    }
}

