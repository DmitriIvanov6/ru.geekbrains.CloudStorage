import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class ControllerLoginWindow {

    private final DatabaseClient dbClient = new DatabaseClient();

    @FXML
    private Label errorMessage = new Label();

    @FXML
    private TextField login = new TextField();

    @FXML
    TextField password = new TextField();

    @FXML
    public void ready(ActionEvent event) throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        if (dbClient.checkLoginAndPswrdSQL(login.getText(), PasswordEncoder.pswrdEncoding(password.getText()))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainWindow.fxml"));
            Parent mainWindow = loader.load();
            ControllerMainWindow cMW = loader.getController();
            cMW.getUserIdFromLogin(dbClient.getUserId(login.getText()));
            Scene mainWindowScene = new Scene(mainWindow, 600, 500);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainWindowScene);
            window.setTitle("Войдите или зарегистрируйтесь");
            window.show();
        }
        errorMessage.setVisible(true);
    }

    @FXML
    public void register(ActionEvent event) throws IOException {
        Parent regWindow = FXMLLoader.load(getClass().getResource("/regWindow.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Регистрация");
        window.setScene(new Scene(regWindow, 540.0, 300.0));
        window.show();
    }
}
