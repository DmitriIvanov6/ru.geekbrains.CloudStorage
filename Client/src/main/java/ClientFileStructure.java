import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ClientFileStructure {

    private File path;


    public String onStartPath() {
        path = new File("C://");
        return path.getAbsolutePath();
    }

    public List<String> onStartList() {
        path = new File("C://");
        List<String> contain = null;
        if (path.isDirectory()) {
            contain = checkDirectory(path);
        }
        return contain;
    }

    public ArrayList<String> checkDirectory(File file) {
        ArrayList<String> checkedFileList = new ArrayList<>();
        File[] fileList = file.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                if (!Files.isSymbolicLink(f.toPath()) && Files.isReadable(f.toPath()) && !f.isHidden()) {
                    checkedFileList.add(f.getName());
                }
            }
            return checkedFileList;
        }
        return null;
    }


}
