package model;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class MySQL implements Serializable{

    public String ip;
    public String port;
    public String pw;
    public String un;
    public String dbname;
    
    public String dump;
    public String mysqlExe;

    private static Connection connection;

    public static void createConnection() throws Exception {
        if (connection == null) {
            String userHome = System.getProperty("user.home");
            Path dbDir = Paths.get(userHome, "FlexGymDb");
            FileInputStream inputStream = new FileInputStream(dbDir + "\\dbInfo.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            MySQL db = (MySQL) objectInputStream.readObject();
            objectInputStream.close();

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + db.ip + ":" + db.port + "/" + db.dbname, db.un, db.pw);
            System.out.println("connection created");
        }
    }

    public static ResultSet executeSearch(String query) throws Exception {
        createConnection();
        return connection.createStatement().executeQuery(query);
    }

    public static Integer executeIUD(String query) throws Exception {
        createConnection();
        return connection.createStatement().executeUpdate(query);
    }

    public static Connection getConnection() {
        return connection;
    }
}
