import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientApp {
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private final String FILE_PATH = "C:/JavaNew/testpic.jpg";


    public ClientApp() throws IOException {

        try (Socket socket = new Socket(SERVER_ADDR, SERVER_PORT)) {
            File myFile = new File(FILE_PATH);
            int fileSize = (int) myFile.length();
            System.out.println(fileSize);
            FileInputStream fr = new FileInputStream(FILE_PATH);
            BufferedInputStream bis = new BufferedInputStream(fr);
            // выделение под метку о размере файла
            int mark = Integer.SIZE / 8;
            byte[] buffer = ByteBuffer.allocate(mark + fileSize).putInt(fileSize).array();
            bis.read(buffer, mark, buffer.length - mark);
            OutputStream out = socket.getOutputStream();
            out.write(buffer, 0, buffer.length);
            System.out.println("Done");
            out.close();
        }
    }
}
