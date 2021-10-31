import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerApp {
    private final int port = 8189;

    private static final int SIZE_MARK = Long.SIZE / 8;
    private static final int NAME_MARK = Long.SIZE / 8;
    private static final int COMMAND_MARK = Character.SIZE / 8;
    private static final int BUFFER_SIZE = (int) (32 * Math.pow(10, 3));

    private static final String SERVER_FOLDER = "C://ServerDir/";


    public ServerApp() throws IOException {
        int port = this.port;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Waiting");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                InputStream in = socket.getInputStream();
                byte[] fileNameArr;
                byte[] buffer;
                long fileName;
                byte[] sizeArr;
                long fileSize;
                File newDir;
                FileOutputStream fw;
                byte[] commandArr = new byte[COMMAND_MARK];
                in.read(commandArr, 0, COMMAND_MARK);
                char command = ByteBuffer.wrap(commandArr).getChar();
                switch (command) {
                    case ('s'):
                        sizeArr = new byte[SIZE_MARK];
                        fileNameArr = new byte[NAME_MARK];
                        in.read(sizeArr, 0, SIZE_MARK);
                        fileSize = ByteBuffer.wrap(sizeArr).getLong();
                        in.read(fileNameArr, 0, NAME_MARK);
                        fileName = ByteBuffer.wrap(fileNameArr).getLong();
                        newDir = new File(SERVER_FOLDER);
                        newDir.mkdirs();
                        fw = new FileOutputStream(newDir.getAbsolutePath() + "\\" + fileName);
                        buffer = new byte[BUFFER_SIZE];
                        for (int i = 0; i < bufferCount(fileSize) - 1; i++) {
                            in.read(buffer);
                            fw.write(buffer);
                            System.out.println(i);
                        }
                            int lastBuffer = (int)(fileSize - BUFFER_SIZE*(bufferCount(fileSize) - 1) );
                            buffer =  new byte[lastBuffer];
                            in.read(buffer);
                            fw.write(buffer);
                        System.out.println("Upload is completed");
                        in.close();
                        fw.close();
                        break;

                    case ('r'):
                        // Удаление файла
                        fileNameArr = new byte[NAME_MARK];
                        in.read(fileNameArr, 0, NAME_MARK);
                        fileName = ByteBuffer.wrap(fileNameArr).getLong();
                        File fileToRemove = new File(SERVER_FOLDER + fileName);
                        fileToRemove.delete();
                        System.out.println("File was deleted");
                        in.close();
                        break;
                    case ('d'):
                        //Скачака файла
                        System.out.println("Command received");
                        fileNameArr = new byte[NAME_MARK];
                        in.read(fileNameArr, 0, NAME_MARK);
                        fileName = ByteBuffer.wrap(fileNameArr).getLong();
                        File fileToTransfer = new File(SERVER_FOLDER + String.valueOf(fileName));
                        FileInputStream fi = new FileInputStream(fileToTransfer);
                        OutputStream out = socket.getOutputStream();
                        BufferedInputStream bis = new BufferedInputStream(fi);
                        fileSize = fileToTransfer.length();

                        byte[] bufferData = ByteBuffer.allocate(SIZE_MARK).putLong(fileSize).array();
                        out.write(bufferData);

                        buffer = new byte[BUFFER_SIZE];
                        for (int i = 0; i < bufferCount(fileSize) - 1; i++) {
                            bis.read(buffer);
                            out.write(buffer);
                            System.out.println(i);
                        }
                        lastBuffer = (int)(fileSize - BUFFER_SIZE*(bufferCount(fileSize) - 1) );
                        buffer =  new byte[lastBuffer];
                        bis.read(buffer);
                        out.write(buffer);
                        System.out.println("Download is completed");
                        out.close();
                        in.close();
                        fi.close();
                        break;
                }
            }
        }

    }

    private static long bufferCount(long fileSize) {
        return fileSize % BUFFER_SIZE != 0 ? fileSize / BUFFER_SIZE + 1 : fileSize / BUFFER_SIZE;
    }


}
