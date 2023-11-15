package application;

import java.io.IOException;
import java.util.Optional;

import gui.FinalizacaoController;
import gui.util.Alerts;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Finalizacao.fxml"));
			Parent parent = loader.load();
			Scene scene = new Scene(parent);
			stage.setResizable(false);			
			stage.setScene(scene);
			stage.setTitle("FOFUCHOS PET");
			stage.show();			
			Image image = new Image("/Icones/fofuchosLogotipo.png");
			stage.getIcons().add(image);	
			
			stage.setOnCloseRequest(evento -> {	
				evento.consume();
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			    alert.setTitle("Confirmação");
			    alert.setHeaderText(null);
			    alert.setContentText("Você tem certeza que deseja sair?\nClique em OK para sair.");

			    Optional<ButtonType> result = alert.showAndWait();
			    if (result.isPresent() && result.get() == ButtonType.OK) {
		    		FinalizacaoController fc = loader.getController();
					fc.salvamentoAutomatico();				
			    	
			        stage.close(); 
			        Platform.exit();
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("ERRO!!", null, "ERRO DESCONHECIDO, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
