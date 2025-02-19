package tn.esprit.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private final String URL = "jdbc:mysql://localhost:3306/wattway";
    private final String USER = "root";
    private final String PASS = "";
    private Connection conn;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            conn = DriverManager.getConnection(URL,USER,PASS);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static MyDatabase getInstance() {
        if (instance ==null)
            instance = new MyDatabase();
        return instance;
    }
    public Connection getConnection() {
        return conn;
    }
}

