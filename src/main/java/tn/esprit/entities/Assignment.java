package tn.esprit.entities;
import java.util.ArrayList;
import java.util.List;

public class Assignment {
    private int idAssignment;
    private String descriptionAssignment;
    private String statusAssignment;
    private int idUser;
    private List<Mechanic> mechanics;

    public Assignment(int idAssignment, String descriptionAssignment, String statusAssignment, int idUser) {
        this.idAssignment = idAssignment;
        this.descriptionAssignment = descriptionAssignment;
        this.statusAssignment = statusAssignment;
        this.idUser = idUser;
        this.mechanics = new ArrayList<>();
    }

    public Assignment() {
        this.mechanics = new ArrayList<>();
    }

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

    public List<Mechanic> getMechanics() {
        return mechanics;
    }

    public void addMechanic(Mechanic mechanic) {
        this.mechanics.add(mechanic);
    }
    public void setMechanics(List<Mechanic> mechanics) {
        this.mechanics = mechanics;
    }
    /*
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
    */
}