/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OOP11;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author sqmson
 */
public class ConnectDB {
    public Connection con;
    public Statement statement1,statement2,statement3;
    public PreparedStatement pst;
    public ResultSet result;
    
    private final String path, user, pass;

    public String query;
    
    public ConnectDB(){
        
        path = "jdbc:mysql://localhost:3306/airportdb";
        user = "root";
        pass = "";
        
        query = null;
        con = null;
        statement1 = null;
        statement2 =null;
        pst = null;
        result = null;
        
        getConnection(); 
    }
    
    private void getConnection(){
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(path,user,pass);
            statement1 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        }catch(SQLException | ClassNotFoundException ex){
            System.out.println("Connection Failed!!!");
        }
    }
}
