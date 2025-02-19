package tn.esprit.entities;
import java.util.ArrayList;
import java.util.List;

public class Mechanic {
    private int idMechanic;
    private String nameMechanic;
    private String specialityMechanic;
    private List<Assignment> assignments;

    // Constructor
    public Mechanic(int idMechanic, String nameMechanic, String specialityMechanic) {
        this.idMechanic = idMechanic;
        this.nameMechanic = nameMechanic;
        this.specialityMechanic = specialityMechanic;
        this.assignments = new ArrayList<>();
    }
    public Mechanic(String nameMechanic, String specialityMechanic) {
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

    public String getSpecialityMechanic() {
        return specialityMechanic;
    }

    public void setSpecialityMechanic(String specialityMechanic) {
        this.specialityMechanic = specialityMechanic;
    }

    public List<Assignment> getAssignments() { return assignments ;}

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
