/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Credentials;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author c0633176
 */
public class Credentials {
    public static Connection getConnection() throws SQLException {

        String url = "jdbc:as400:174.79.32.158";
        String username = "IBM87";
        String password = "IBM87";

        Connection conn = null;
        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Class Not found  exception" + e.getMessage());
        }
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("SQL Error found " + e.getMessage());
        }
        return conn;
    }
}
    

