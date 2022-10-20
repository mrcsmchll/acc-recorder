package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import javafx.scene.control.PasswordField;

public class LoginController implements Initializable {

	private static final String USER = "sonia@manchinellimontajes.com.ar", PASS = "1234";

	private DbController dbControl = new DbController();
	
	@FXML
	private Label titleLbl;
	@FXML
	private Label dbStatusLbl;
	@FXML
	private Label userLbl;
	@FXML
	private TextField userTxtField;
	@FXML
	private Label passLbl;
	@FXML
	private PasswordField passField;
	@FXML
	private Button logIn;

	public void logIn(ActionEvent click) {
		if (userTxtField.getText().equals(USER) && passField.getText().equals(PASS)) {
			try {
				Node node = (Node) click.getSource();//obtengo la fuente de donde proviene el evento
				Stage logInStage = (Stage) node.getScene().getWindow();//obtengo desde el node la ventana en tipo stage extendiendo el control de la clase 
				Parent root = FXMLLoader.load(getClass().getResource("/application/Main.fxml"));
				Scene scene = new Scene(root);
				Stage primaryStage = new Stage();
				primaryStage.setTitle("MyFX");
				primaryStage.setScene(scene);
				primaryStage.show();
				logInStage.close();//cierre de ventana inicial

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			titleLbl.setText("Error");
			titleLbl.setTextFill(Color.RED);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (dbControl.isDbConnected()) {
			dbStatusLbl.setText("Conexión a la base de datos EXITOSA");
			dbStatusLbl.setTextFill(Color.GREEN);
		} else {
			dbStatusLbl.setText("No se logró conectar a la base de datos");
			dbStatusLbl.setTextFill(Color.RED);
		}
	}

}
