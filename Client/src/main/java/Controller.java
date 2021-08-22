import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller {

    private final ClientFileStructure cfs = new ClientFileStructure();

    private ObservableList<String> fileList = FXCollections.observableArrayList();
    private final DatabaseClient dbClient = new DatabaseClient();
    private String tempFilePath;
    private final ClientApp clientApp = new ClientApp();

    @FXML
    private ListView<String> downloadList = new ListView<>();

    @FXML
    private ListView<String> clientFiles = new ListView<>();

    @FXML
    private TextField clientPath = new TextField();

    @FXML
    private Button btnUpload = new Button();


    @FXML
    public void refresh() throws SQLException, ClassNotFoundException {
        ArrayList<String> source = dbClient.getFilesSql();
        downloadList.getItems().removeAll(source);
        downloadList.getItems().addAll(source);
    }


    @FXML
    public void getFileName() {
        String fileName = downloadList.getSelectionModel().getSelectedItem();

    }



    @FXML
    public void uploadGUI() throws IOException {
       clientApp.sendFile(clientPath.getText());
       btnUpload.setDisable(true);
    }

    @FXML
    public void showRoot() {
        clientPath.clear();
        fileList = clientFiles.getItems();
        clientFiles.getItems().removeAll(fileList);
        clientPath.appendText(cfs.onStartPath());
        clientFiles.getItems().addAll(cfs.onStartList());
    }

    @FXML
    public String getFileNameUpload() {
        return clientFiles.getSelectionModel().getSelectedItem();

    }

    @FXML
    public void choose() {
        if (!clientPath.getText().endsWith("\\")) {
            clientPath.clear();
            clientPath.appendText(tempFilePath);
        }

        String filePath = clientPath.getText();
        String fileName = getFileNameUpload();
        File file = new File(filePath + fileName);
        System.out.println(file.getPath());
        if (file.isDirectory()) {
            btnUpload.setDisable(true);
            fileList = clientFiles.getItems();
            clientFiles.getItems().removeAll(fileList);
            clientFiles.getItems().addAll(file.list());
            clientPath.appendText(fileName + "\\");

        } else {
            btnUpload.setDisable(false);
            tempFilePath = clientPath.getText();
            clientPath.appendText(fileName);
        }

    }

}



