package tn.esprit.entities;

public class Mechanic {
    private int idMechanic;
    private String nameMechanic;
    private String specialityMechanic;

    // Constructor
    public Mechanic(int idMechanic, String nameMechanic, String specialityMechanic) {
        this.idMechanic = idMechanic;
        this.nameMechanic = nameMechanic;
        this.specialityMechanic = specialityMechanic;
    }
    public Mechanic(String nameMechanic, String specialityMechanic) {
        this.nameMechanic = nameMechanic;
        this.specialityMechanic = specialityMechanic;
    }
    // Default Constructor
    public Mechanic() {
    }

    // Getters and Setters
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

    // toString method
    @Override
    public String toString() {
        return "Mechanic{" +
                "idMechanic=" + idMechanic +
                ", nameMechanic='" + nameMechanic + '\'' +
                ", specialityMechanic='" + specialityMechanic + '\'' +
                '}';
    }
}
