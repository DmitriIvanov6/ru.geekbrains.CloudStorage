import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerApp {
    private final int port = 8189;



    public ServerApp() throws IOException {
        int port = this.port;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                int mark = Integer.SIZE / 8;
                int nameMark = Long.SIZE / 8;
                System.out.println("Waiting");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                InputStream in = socket.getInputStream();
                int commandMark = Character.SIZE / 8;
                byte[] fileNameArr;
                byte[] buffer;
                long fileName;
                byte[] commandArr = new byte[commandMark];
                in.read(commandArr, 0, commandMark);
                char command = ByteBuffer.wrap(commandArr).getChar();
                switch (command) {
                    case ('s'):
                        // Сохранение файла
                        byte[] sizeArr = new byte[mark];
                        fileNameArr = new byte[nameMark];
                        in.read(sizeArr, 0, mark);
                        int fileSize = ByteBuffer.wrap(sizeArr).getInt();
                        in.read(fileNameArr, 0, nameMark);
                        fileName = ByteBuffer.wrap(fileNameArr).getLong();
                        File newDir = new File("C://ServerDir");
                        newDir.mkdirs();
                        FileOutputStream fw = new FileOutputStream(newDir.getAbsolutePath() + "\\" + fileName);
                        buffer = new byte[fileSize];
                        in.read(buffer, 0, buffer.length);
                        fw.write(buffer, 0, fileSize);
                        System.out.println("Upload is completed");
                        in.close();
                        fw.close();
                        break;
                    case ('r'):
                        // Удаление файла
                        fileNameArr = new byte[nameMark];
                        in.read(fileNameArr, 0, nameMark);
                        fileName = ByteBuffer.wrap(fileNameArr).getLong();
                        File fileToRemove = new File("C://ServerDir/" + fileName);
                        fileToRemove.delete();
                        System.out.println("File was deleted");
                        in.close();
                        break;
                    case ('d'):
                        //Скачака файла
                        System.out.println("command received");
                        fileNameArr = new byte[nameMark];
                        in.read(fileNameArr, 0, nameMark);
                        fileName = ByteBuffer.wrap(fileNameArr).getLong();
                        File fileToTransfer = new File("C://ServerDir/" + String.valueOf(fileName));
                        FileInputStream fi = new FileInputStream(fileToTransfer);
                        buffer = ByteBuffer.allocate(mark + (int) fileToTransfer.length()).putInt((int) fileToTransfer.length()).array();
                        BufferedInputStream bis = new BufferedInputStream(fi);
                        bis.read(buffer, mark, buffer.length - mark);
                        OutputStream out = socket.getOutputStream();
                        out.write(buffer, 0, buffer.length);
                        System.out.println("Download is completed");
                        out.close();
                        in.close();
                        fi.close();
                        break;
                }
            }
        }

    }


}
