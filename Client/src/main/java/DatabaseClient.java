import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseClient {
    private final static String url = "jdbc:postgresql://localhost:5432/CloudStorage";
    private final static String user = "postgres";
    private final static String pass = "Killthem86";
    private Connection connection = null;


    public void connectSQL() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(url, user, pass);
    }

    public void disconnectSQL() throws SQLException {
        connection.close();
    }

    public void sendFileSQL(String name, long size, long serverName, long userId) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO storage.files (name, size, servername, user_id) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setLong(2, size);
            ps.setLong(3, serverName);
            ps.setLong(4, userId);
            ps.executeUpdate();
        } finally {
            disconnectSQL();
        }
    }

    public void removeFileSQL(long serverName) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM storage.files WHERE storage.files.servername = ?");
            ps.setLong(1, serverName);
            ps.executeUpdate();
        } finally {
            disconnectSQL();
        }

    }

    public long getServerNameSQL(String fileName, long userId) throws SQLException, ClassNotFoundException {
        long serverName = 0L;
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM storage.files WHERE name = ? AND user_id = ?");
            ps.setString(1, fileName);
            ps.setLong(2, userId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                serverName = resultSet.getLong("servername");
            }
        } finally {
            disconnectSQL();
        }
        return serverName;
    }

    public ArrayList<String> getFilesSql(long userId) throws SQLException, ClassNotFoundException {
        try {
            ArrayList<String> files = new ArrayList<>();
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM storage.files WHERE user_id = ?");
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                files.add(resultSet.getString("name"));
            }
            return files;
        } finally {
            disconnectSQL();
        }
    }

    public boolean checkFileName(String fileName, long userId) throws SQLException, ClassNotFoundException {
        List<String> fileList = getFilesSql(userId);
        if (fileList != null) {
            for (String value : fileList) {
                if (value.equals(fileName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkLoginAndPswrdSQL(String login, String pswrd) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM storage.users WHERE login = ? AND password = ?");
            ps.setString(1, login);
            ps.setString(2, pswrd);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } finally {
            disconnectSQL();
        }
    }

    public long getUserId(String login) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM storage.users WHERE login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getLong("id") : 0;
        } finally {
            disconnectSQL();
        }
    }

    public boolean chekUsername(String username) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM storage.users WHERE login = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } finally {
            disconnectSQL();
        }
    }

    public void registerSQL(String username, String pswrd) throws SQLException, ClassNotFoundException {
        try {
            connectSQL();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO storage.users (login, password) VALUES (?, ?)");
            ps.setString(1, username);
            ps.setString(2, pswrd);
            ps.executeUpdate();
        } finally {
            disconnectSQL();
        }

    }

}
