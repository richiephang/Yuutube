
package yuutube;

import java.sql.*;

public class connection{
    
    protected static Connection con;
        public Connection getConnection() {
        try{
        //    Class.forName("com.mysql.cj.jdbc.Driver");
             con= DriverManager.getConnection("jdbc:mysql://localhost:3306/yuutube", "root", "");
            //Statement st = conn.createStatement();
            /*
            for debugging purpose, can be removed
            System.out.println("Connected to database in ConnectionMySQL.getConnection()");
            */
            return con;
        }catch (SQLException e) { 
            System.out.println(e);
        }
        return null;
    }
}
