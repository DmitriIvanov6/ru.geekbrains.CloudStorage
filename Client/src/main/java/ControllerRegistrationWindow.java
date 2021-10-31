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

public class ControllerRegistrationWindow {

    private final DatabaseClient dbClient = new DatabaseClient();

    @FXML
    private Label errorMessageUsername = new Label();

    @FXML
    private Label errorMessageEmptyField = new Label();

    @FXML
    private TextField login = new TextField();

    @FXML
    TextField password = new TextField();

    @FXML
    private void register(ActionEvent event) throws SQLException, ClassNotFoundException, IOException, NoSuchAlgorithmException {
        if (!login.getText().isEmpty() && !password.getText().isEmpty()) {
            if (!dbClient.chekUsername(login.getText())) {
                dbClient.registerSQL(login.getText(), PasswordEncoder.pswrdEncoding(password.getText()) );
                Parent regWindow = FXMLLoader.load(getClass().getResource("/loginWindow.fxml"));
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setTitle("Войдите или зарегистрируйтесь");
                window.setScene(new Scene(regWindow, 540.0, 300.0));
                window.show();
            } else {
                errorMessageEmptyField.setVisible(false);
                errorMessageUsername.setVisible(true);
            }
        } else {
            errorMessageUsername.setVisible(false);
            errorMessageEmptyField.setVisible(true);
        }

    }

}
