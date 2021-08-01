import java.io.FileNotFoundException;
import java.io.IOException;


public class ClientMain {
    public static void main(String[] args) throws IOException {
        ClientApp clientApp = new ClientApp();
        clientApp.sendFile();
//        clientApp.removeFile();


    }
}