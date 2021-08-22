import java.io.File;
import java.util.ArrayList;

public class ClientFileStructure {

    private File path;


    public String onStartPath() {
        path = new File("C://");
        return path.getAbsolutePath();
    }

    public String[] onStartList() {
        path = new File("C://");
        String[] contain = new String[0];
        if (path.isDirectory()) {
            contain = path.list();
        }
        return contain;
    }


}
