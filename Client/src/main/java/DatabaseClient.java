import java.sql.*;

public class DatabaseClient {
    private static String url = "jdbc:postgresql://localhost:5432/CloudStorage";
    private static String user = "postgres";
    private static String pass = "Killthem86";
    private Statement stmt = null;
    private Connection connection = null;


    public void connectSQL() throws SQLException, ClassNotFoundException {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pass);
    }
    public void disconnectSQL() throws SQLException {
        connection.close();
    }
    public void sendFileSQL(String name, int size, long serverName) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO test.nickname (name, size, servername) VALUES (?, ?, ?);");
            ps.setString(1, name);
            ps.setInt(2, size);
            ps.setLong(3, serverName);
            ps.executeUpdate();
        } finally {
            disconnectSQL();
        }



    }
    public void removeFileSQL (Long serverName) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM test.nickname WHERE test.nickname.servername = ?");
            ps.setLong(1, serverName);
            ps.executeUpdate();
        } finally {
            disconnectSQL();
        }

    }





}
