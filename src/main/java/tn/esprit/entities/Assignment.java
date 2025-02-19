package tn.esprit.entities;

public class Assignment {
    private int idAssignment;
    private String descriptionAssignment;
    private String statusAssignment;
    private int idUser;
    private int idMechanic;

    // Constructor
    public Assignment(int idAssignment, String descriptionAssignment, String statusAssignment, int idUser, int idMechanic) {
        this.idAssignment = idAssignment;
        this.descriptionAssignment = descriptionAssignment;
        this.statusAssignment = statusAssignment;
        this.idUser = idUser;
        this.idMechanic = idMechanic;
    }

    // Default Constructor
    public Assignment() {
    }

    // Getters and Setters
    public int getIdAssignment() {
        return idAssignment;
    }

    public void setIdAssignment(int idAssignment) {
        this.idAssignment = idAssignment;
    }

    public String getDescriptionAssignment() {
        return descriptionAssignment;
    }

    public void setDescriptionAssignment(String descriptionAssignment) {
        this.descriptionAssignment = descriptionAssignment;
    }

    public String getStatusAssignment() {
        return statusAssignment;
    }

    public void setStatusAssignment(String statusAssignment) {
        this.statusAssignment = statusAssignment;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdMechanic() {
        return idMechanic;
    }

    public void setIdMechanic(int idMechanic) {
        this.idMechanic = idMechanic;
    }

    // toString method
    @Override
    public String toString() {
        return "Assignment{" +
                "idAssignment=" + idAssignment +
                ", descriptionAssignment='" + descriptionAssignment + '\'' +
                ", statusAssignment=" + statusAssignment +
                ", idUser=" + idUser +
                ", idMechanic=" + idMechanic;
    }
}