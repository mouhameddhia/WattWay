package tn.esprit.entities;
import java.util.ArrayList;
import java.util.List;

public class Mechanic {

    public enum Speciality {
        ELECTRICIAN,
        MECHANIC,
        SOFTWARE
    }
    private int idMechanic;
    private String nameMechanic;
    private Speciality specialityMechanic;
    private List<Assignment> assignments;
    private String imgMechanic;
    private String emailMechanic;
    private int carsRepaired;



    // Constructor
    public Mechanic(int idMechanic, String nameMechanic, Speciality specialityMechanic, String imgMechanic, String emailMechanic, int carsRepaired) {
        this.idMechanic = idMechanic;
        this.nameMechanic = nameMechanic;
        this.specialityMechanic = specialityMechanic;
        this.assignments = new ArrayList<>();
        this.imgMechanic = imgMechanic;
        this.emailMechanic = emailMechanic;
        this.carsRepaired = carsRepaired;
    }
    public Mechanic(String nameMechanic, Speciality specialityMechanic) {
        this.nameMechanic = nameMechanic;
        this.specialityMechanic = specialityMechanic;
    }
    public Mechanic() {
        this.assignments = new ArrayList<>();
    }


    public int getIdMechanic() {
        return idMechanic;
    }

    public void setIdMechanic(int idMechanic) {
        this.idMechanic = idMechanic;
    }

    public String getNameMechanic() {
        return nameMechanic;
    }

    public void setNameMechanic(String nameMechanic) {
        this.nameMechanic = nameMechanic;
    }

    public Speciality getSpecialityMechanic() {
        return specialityMechanic;
    }

    public void setSpecialityMechanic(Speciality specialityMechanic) {
        this.specialityMechanic = specialityMechanic;
    }

    public List<Assignment> getAssignments() { return assignments ;}
    public String getImgMechanic() { return imgMechanic; }
    public void setImgMechanic(String imgMechanic) { this.imgMechanic = imgMechanic; }

    public String getEmailMechanic() { return emailMechanic; }
    public void setEmailMechanic(String emailMechanic) { this.emailMechanic = emailMechanic; }

    public int getCarsRepaired() { return carsRepaired; }
    public void setCarsRepaired(int carsRepaired) { this.carsRepaired = carsRepaired; }


    public void setAssignment(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public void addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
    }

    /*
    // toString method
    @Override
    public String toString() {
        return "Mechanic{" +
                "idMechanic=" + idMechanic +
                ", nameMechanic='" + nameMechanic + '\'' +
                ", specialityMechanic='" + specialityMechanic + '\'' +
                '}';
    }
     */
}
