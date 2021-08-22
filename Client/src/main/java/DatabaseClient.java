import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseClient {
    private final static String url = "jdbc:postgresql://localhost:5432/CloudStorage";
    private final static String user = "postgres";
    private final static String pass = "Killthem86";
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

    public void removeFileSQL(long serverName) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM test.nickname WHERE test.nickname.servername = ?");
            ps.setLong(1, serverName);
            ps.executeUpdate();
        } finally {
            disconnectSQL();
        }

    }

    public long downloadFileSQL(String fileName) throws SQLException, ClassNotFoundException {
        long serverName = 0L;
        try{
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM test.nickname WHERE name = ?");
            ps.setString(1, fileName);
            ResultSet resultSet =  ps.executeQuery();
            while (resultSet.next()) {
                serverName = resultSet.getLong("servername");
            }
        } finally {
            disconnectSQL();
        }
        return serverName;
    }

    public ArrayList<String> getFilesSql() throws SQLException, ClassNotFoundException {
        ArrayList<String> files = new ArrayList();
        try{
            connectSQL();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM test.nickname");
            while (resultSet.next()) {
                files.add(resultSet.getString("name"));
            }
            return files;

        } finally {
            disconnectSQL();
        }
    }

    public boolean checkFileName(String fileName) throws SQLException, ClassNotFoundException {
        List<String> fileList = getFilesSql();
        for(String value : fileList) {
            if ( value.equals(fileName)){
                return false;
            }
        }
        return true;
    }



}
