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
    Socket socket = new Socket(SERVER_ADDR, SERVER_PORT);


    public ClientApp() throws IOException {
        //прием файлов

        int mark = Integer.SIZE / 8;


        try {

            (new Thread(() -> {

                while (true) {
                    try {
                        in = socket.getInputStream();
                        Thread.sleep(3000);
                        byte[] sizeArr = new byte[mark];
                        in.read(sizeArr, 0, mark);
                        int fileSize = ByteBuffer.wrap(sizeArr).getInt();
                        byte[] buffer = new byte[fileSize];
                        in.read(buffer, 0, buffer.length);
                        FileOutputStream fw = new FileOutputStream(DESTINATION + "test.txt");
                        fw.write(buffer, 0, fileSize);
                        fw.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            })).start();

        } finally {


        }

    }


    public void sendFile() throws IOException {
        DatabaseClient dbc = new DatabaseClient();
        try {
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
        long serverName = 0;
        DatabaseClient dbc = new DatabaseClient();
        //захардкодил
        serverName = 20210803234153L;
        int commandMark = Character.SIZE / 8;
        int nameMark = Long.SIZE / 8;
        byte[] buffer = ByteBuffer.allocate(commandMark + nameMark).putChar('d').putLong(serverName).array();
        OutputStream out = socket.getOutputStream();
        out.write(buffer, 0, buffer.length);
        System.out.println("Download command sent");


    }

}
