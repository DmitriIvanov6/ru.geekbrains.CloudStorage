import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
public class ServerApp {
    private final int port = 8189;
    private String DESTINATION = "C:/JavaNew/";


    public ServerApp() throws IOException {
        int port = this.port;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Waiting");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                InputStream in = socket.getInputStream();
                int commandMark = Character.SIZE / 8;
                byte[] commandArr = new byte[commandMark];
                in.read(commandArr, 0,commandMark);
                char command = ByteBuffer.wrap(commandArr).getChar();
                switch (command) {
                    case('s') :
                        // Сохранение файла
                        // выделение под метку о размере файла и размере имени
                        int mark = Integer.SIZE / 8;
                        int nameMark = Long.SIZE / 8;
                        byte[] sizeArr = new byte[mark];
                        byte[] fileNameArr = new byte[nameMark];
                        in.read(sizeArr, 0, mark);
                        int fileSize = ByteBuffer.wrap(sizeArr).getInt();
                        in.read(fileNameArr, 0, nameMark);
                        long fileName = ByteBuffer.wrap(fileNameArr).getLong();
                        FileOutputStream fw = new FileOutputStream(DESTINATION + fileName);
                        byte[] buffer = new byte[fileSize];
                        in.read(buffer, 0, buffer.length);
                        fw.write(buffer, 0, fileSize);
                        in.close();
                        fw.close();
                        break;
                    case ('r') :
                        int nameMark2 = Long.SIZE / 8;
                        byte[] fileNameArr2 = new byte[nameMark2];
                        in.read(fileNameArr2, 0, nameMark2);
                        long fileName2 = ByteBuffer.wrap(fileNameArr2).getLong();
                        File fileToRemove = new File("C:/JavaNew/" + fileName2);
                        fileToRemove.delete();
                        System.out.println("File was deleted");
                        in.close();
                        break;
                }
            }
        }

    }



}
