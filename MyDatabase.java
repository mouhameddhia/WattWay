package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private final String URL = "jdbc:mysql://localhost:3307/WattWay1.0";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection conn = null;

    private static MyDatabase instance;
    private MyDatabase() {
        try {
            this.conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConn() {
        return conn;
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();

        }

        return instance;
    }


}
