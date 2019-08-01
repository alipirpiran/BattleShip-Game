package battleShip.core.server.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionForDB {
    private final static String DB_URL = "jdbc:sqlite:" + "src" + File.separator + "battleShip" + File.separator + "core" + File.separator + "server" +File.separator+ "Database" +
            File.separator + "database.db";

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
