/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Public;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Alfie
 */
public class Connect {
    public Connection con;
    public Statement st;
    public PreparedStatement pst;
    public String query;
    public ResultSet rs;
    
    private final String cs, user, password;
    
    public Connect(){
        cs = "jdbc:mysql://localhost:3306/airportdb";
        user = "root";
        password = "";
        
        getConnection();
    }
    
    private void getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(cs,user,password);
            st =con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Database connected.");
        } catch (SQLException | ClassNotFoundException e){
            System.err.println("Database connection failed: " + e.getMessage());
        }
            
    }
}