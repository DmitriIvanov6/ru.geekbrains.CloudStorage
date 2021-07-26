import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
public class ServerApp {
    private final int port = 8189;
    private String DESTINATION = "C:/JavaNew/recievedfile";


    public ServerApp() throws IOException {
        int port = this.port;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                // выделение под метку о размере файла
                int mark = Integer.SIZE / 8;
                byte[] sizeArr = new byte[mark];
                System.out.println("Waiting");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                InputStream in = socket.getInputStream();
                FileOutputStream fw = new FileOutputStream(DESTINATION);
                in.read(sizeArr, 0, 4);
                int fileSize = ByteBuffer.wrap(sizeArr).getInt();
                byte[] buffer = new byte[fileSize];
                in.read(buffer, 0, buffer.length);
                fw.write(buffer, 0, fileSize);
                in.close();
                fw.close();
            }
        }
    }

}
