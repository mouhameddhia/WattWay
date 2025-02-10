package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Wattway {

    private final String URL="jdbc:mysql://localhost:3306/wattway";
    private final String USERNAME="root";
    private final String PASSWORD="";
    private Connection conn;

    public Connection getConn() {
        return conn;
    }
    private static Wattway instance;

    public Wattway() {
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Wattway getInstance() {
        if(instance == null) {
            instance = new Wattway();
        }
        return instance;
    }
}
