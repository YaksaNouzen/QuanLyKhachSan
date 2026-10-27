package Module_TaiChinh.Dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {

            String url =
            "jdbc:sqlserver://127.0.0.1:1434;"
            + "databaseName=QLHDKhachSan;"
            + "encrypt=true;"
            + "trustServerCertificate=true";

            String user = "sa";
            String pass = "12345";

            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    
    
    }
    
    public static void main(String[] args){

        Connection con = DBConnection.getConnection();

        if (con != null) {
            System.out.println("KET NOI DATABASE THANH CONG!");
        } else {
            System.out.println("KET NOI THAT BAI!");
        }
    }
}
    
    