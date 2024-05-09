package com.example.vart;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnector
{
    public static Connection connect() {
        String database = "VART";
        String username = "root";
        String password = "root123";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = null;
        String ConnectionURL = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vart", username, password);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

}
