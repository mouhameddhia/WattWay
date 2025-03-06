package tn.esprit.entities;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Assignment {
    public enum Status {
        PENDING,
        COMPLETED;
    }
    private int idAssignment;
    private String descriptionAssignment;
    private Status statusAssignment;
    private int idCar;
    private List<Mechanic> mechanics;
    private String carModel;
    private LocalDateTime dateAssignment;
    private String googleCalendarEventId;

    public Assignment(int idAssignment, String descriptionAssignment, Status statusAssignment, int idCar) {
        this.idAssignment = idAssignment;
        this.descriptionAssignment = descriptionAssignment;
        this.statusAssignment = statusAssignment;
        this.idCar = idCar;
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

    public Status getStatusAssignment() {
        return statusAssignment;
    }

    public void setStatusAssignment(Status statusAssignment) {
        this.statusAssignment = statusAssignment;
    }

    public int getIdCar() { return idCar; }
    public void setIdCar(int idCar) { this.idCar = idCar; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public List<Mechanic> getMechanics() {
        return mechanics;
    }

    public void addMechanic(Mechanic mechanic) {
        this.mechanics.add(mechanic);
    }
    public void setMechanics(List<Mechanic> mechanics) {
        this.mechanics = mechanics;
    }
    public LocalDateTime getDateAssignment() {
        return dateAssignment;
    }

    public void setDateAssignment(LocalDateTime dateAssignment) {
        this.dateAssignment = dateAssignment;
    }
    public String getGoogleCalendarEventId() {
        return googleCalendarEventId;
    }

    public void setGoogleCalendarEventId(String googleCalendarEventId) {
        this.googleCalendarEventId = googleCalendarEventId;
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