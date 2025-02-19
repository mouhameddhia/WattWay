package tn.esprit.main;
import tn.esprit.services.MechanicServices;
import tn.esprit.utilities.MyDatabase;
import tn.esprit.entities.Mechanic;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        MyDatabase db1 = MyDatabase.getInstance();

        Mechanic mec = new Mechanic("mec2","specialty2");
        MechanicServices ms = new MechanicServices();
        try {
            ms.addP(mec);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}