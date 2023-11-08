package application;

import java.io.IOException;
import gui.FinalizacaoController;
import gui.util.Alerts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) {
		try {
			FinalizacaoController fc = new FinalizacaoController();
			
			Parent parent = FXMLLoader.load(getClass().getResource("/gui/Finalizacao.fxml"));					
			Scene scene = new Scene(parent);
			stage.setResizable(false);			
			stage.setScene(scene);
			stage.setTitle("FOFUCHOS PET");
			stage.show();			
			Image image = new Image("/Icones/fofuchosLogotipo.png");
			stage.getIcons().add(image);	
			
		} catch (IOException e) {
			Alerts.showAlert("ERRO!!", null, "ERRO DESCONHECIDO, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
