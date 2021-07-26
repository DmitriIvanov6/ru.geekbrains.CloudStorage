import java.io.FileNotFoundException;
import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        try {
            new ClientApp();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}