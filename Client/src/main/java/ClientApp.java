import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientApp {
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private InputStream in = null;
    private final DatabaseClient dbc = new DatabaseClient();

    private static final int SIZE_MARK = Long.SIZE / 8;
    private static final int NAME_MARK = Long.SIZE / 8;
    private static final int COMMAND_MARK = Character.SIZE / 8;
    private static final int BUFFER_SIZE = (int) (32 * Math.pow(10, 3));

    public static final String DEST_DIR = "C://DownloadsCloudStorage";

    // s - save, r - remove, d - download

    public void sendFile(String filePath, long userId) throws IOException {
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT)) {
            File myFile = new File(filePath);
            long fileSize = myFile.length();
            String fileName = myFile.getName();
            long serverName = serverName();
            OutputStream out = socket.getOutputStream();
            //Проверим совпадение по имени файла, если есть, то переименуем
            if (!dbc.checkFileName(fileName, userId)) {
                fileName = renameFileDB(fileName, userId);
            }
            FileInputStream fr = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fr);
            byte[] bufferData = ByteBuffer.allocate(COMMAND_MARK + SIZE_MARK + NAME_MARK).putChar('s').putLong(fileSize).putLong(serverName).array();
            out.write(bufferData, 0, bufferData.length);
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int i = 0; i < bufferCount(fileSize) - 1; i++) {
                bis.read(buffer);
                out.write(buffer);
                System.out.println(i);
            }
            int lastBuffer = (int) (fileSize - BUFFER_SIZE * (bufferCount(fileSize) - 1));
            buffer = new byte[lastBuffer];
            bis.read(buffer);
            out.write(buffer);
            //Заносим в бд инфу о файле
            dbc.sendFileSQL(fileName, fileSize, serverName, userId);
            System.out.println("Upload is done");
            fr.close();
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
    }

    public long serverName() {
        Date date = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmss");
        return Long.parseLong(formatDate.format(date));
    }

    public void removeFile(String fileName, long userId) throws IOException {
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT)) {
            long serverName = dbc.getServerNameSQL(fileName, userId);
            byte[] buffer = ByteBuffer.allocate(COMMAND_MARK + NAME_MARK).putChar('r').putLong(serverName).array();
            OutputStream out = socket.getOutputStream();
            out.write(buffer, 0, buffer.length);
            dbc.removeFileSQL(serverName);
            System.out.println("File was deleted");
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
    }

    public void downloadFile(String fileName, long userId) throws IOException, SQLException, ClassNotFoundException {
        long serverName = dbc.getServerNameSQL(fileName, userId);
        boolean dFlag = true;
        byte[] buffer = ByteBuffer.allocate(COMMAND_MARK + NAME_MARK).putChar('d').putLong(serverName).array();
        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT)) {
            OutputStream out = socket.getOutputStream();
            out.write(buffer);
            System.out.println("Download command was sent");
            while (dFlag) {
                try {
                    in = socket.getInputStream();
                    byte[] sizeArr = new byte[SIZE_MARK];
                    in.read(sizeArr, 0, SIZE_MARK);
                    long fileSize = ByteBuffer.wrap(sizeArr).getLong();
                    File newDir = new File(DEST_DIR);
                    newDir.mkdirs();
                    fileName = checkFilenameOnDisk(fileName);
                    FileOutputStream fw = new FileOutputStream(newDir.getAbsolutePath() + "\\" + fileName);
                    buffer = new byte[BUFFER_SIZE];
                    for (int i = 0; i < bufferCount(fileSize) - 1; i++) {
                        in.read(buffer);
                        fw.write(buffer);
                        System.out.println(i);
                    }
                    int lastBuffer = (int) (fileSize - BUFFER_SIZE * (bufferCount(fileSize) - 1));
                    buffer = new byte[lastBuffer];
                    in.read(buffer);
                    fw.write(buffer);
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

    private static long bufferCount(long fileSize) {
        return fileSize % BUFFER_SIZE != 0 ? fileSize / BUFFER_SIZE + 1 : fileSize / BUFFER_SIZE;
    }

    public String renameFile(String fileName) {
        StringBuilder sb;
        String[] name = fileName.split("\\.", 2);
        sb = new StringBuilder();
        sb.append(name[0]).append("(1)").append(".");
        for (int i = 1; i < name.length; i++) {
            sb.append(name[i]);
        }
        return sb.toString();
    }

    public String renameFileDB(String fileName, long userId) throws SQLException, ClassNotFoundException {
        while (!dbc.checkFileName(fileName, userId)) {
            fileName = renameFile(fileName);
        }
        return fileName;
    }

    public String checkFilenameOnDisk(String fileName) {
        File checkFile = new File(DEST_DIR + "\\" + fileName);
        while (checkFile.exists()) {
            fileName = renameFile(fileName);
            checkFile = new File(DEST_DIR + "\\" + fileName);
        }
        return fileName;
    }

}