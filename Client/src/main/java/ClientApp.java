import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientApp {
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private final String FILE_PATH = "C:/JavaNew/testpic.jpg";
    private InputStream in = null;
    private final String DESTINATION = "C:/JavaNew/zRecieved/";


    public void sendFile() throws IOException {
        DatabaseClient dbc = new DatabaseClient();
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT)) {
            File myFile = new File(FILE_PATH);
            int fileSize = (int) myFile.length();
            String fileName = myFile.getName();
            long serverName = serverName();
            //Заносим в бд инфу о файле
            dbc.sendFileSQL(fileName, fileSize, serverName);
            FileInputStream fr = new FileInputStream(FILE_PATH);
            BufferedInputStream bis = new BufferedInputStream(fr);
            // выделение под метку о команде, размере файла и размере имени s - save, r - remove, d - download
            int mark = Integer.SIZE / 8;
            int nameMark = Long.SIZE / 8;
            int commandMark = Character.SIZE / 8;
            byte[] buffer = ByteBuffer.allocate(commandMark + mark + nameMark + fileSize).putChar('s').putInt(fileSize).putLong(serverName).array();
            bis.read(buffer, commandMark + mark + nameMark, buffer.length - commandMark - mark - nameMark);
            OutputStream out = socket.getOutputStream();
            out.write(buffer, 0, buffer.length);
            System.out.println("Upload is done");

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public long serverName() {
        Date date = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmss");
        return Long.parseLong(formatDate.format(date));
    }

    public void removeFile() throws IOException {
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT)) {
            DatabaseClient dbc = new DatabaseClient();
            int nameMark = Long.SIZE / 8;
            int commandMark = Character.SIZE / 8;
            // Тут пока захардкодим сервернейм, потом будем подтягивать из базы, но это уже с визуальным интерфейсом
            long serverName = 20210802013522L;
            byte[] buffer = ByteBuffer.allocate(commandMark + nameMark).putChar('r').putLong(serverName).array();
            OutputStream out = socket.getOutputStream();
            out.write(buffer, 0, buffer.length);
            dbc.removeFileSQL(serverName);
            System.out.println("File was deleted");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void DownloadFile(String fileName) throws IOException {
        DatabaseClient dbc = new DatabaseClient();
        //захардкодил
        long serverName = 20210808002240L;
        int commandMark = Character.SIZE / 8;
        int nameMark = Long.SIZE / 8;
        int mark = Integer.SIZE / 8;
        boolean dFlag = true;
        byte[] buffer = ByteBuffer.allocate(commandMark + nameMark).putChar('d').putLong(serverName).array();
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT)) {
            OutputStream out = socket.getOutputStream();
            out.write(buffer, 0, buffer.length);
            System.out.println("Download command was sent");
            while (dFlag) {
                try {
                    in = socket.getInputStream();
                    byte[] sizeArr = new byte[mark];
                    in.read(sizeArr, 0, mark);
                    int fileSize = ByteBuffer.wrap(sizeArr).getInt();
                    buffer = new byte[fileSize];
                    in.read(buffer, 0, buffer.length);
                    FileOutputStream fw = new FileOutputStream(DESTINATION + "test.jpg");
                    fw.write(buffer, 0, fileSize);
                    dFlag = false;
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Download is completed");
                    in.close();
                    out.close();
                }
            }


        }

    }
}