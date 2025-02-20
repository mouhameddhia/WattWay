package tn.esprit.main;

import tn.esprit.services.MechanicServices;
import tn.esprit.utilities.MyDatabase;
import tn.esprit.entities.Mechanic;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        MyDatabase db1 = MyDatabase.getInstance();

        Mechanic mec = new Mechanic("mec2", Mechanic.Speciality.MECHANIC);

        MechanicServices ms = new MechanicServices();
        try {
            ms.addP(mec);
            System.out.println("Mechanic successfully added: " + mec);
        } catch (SQLException e) {
            System.out.println("Error adding mechanic: " + e.getMessage());
        }
    }
}
