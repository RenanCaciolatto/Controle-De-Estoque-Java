package application;

import java.io.IOException;

import gui.FinalizacaoController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	@SuppressWarnings("null")
	@Override
	public void start(Stage stage) {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/gui/Finalizacao.fxml"));					
			Scene scene = new Scene(parent);
			stage.setResizable(false);
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            @Override
	            public void handle(WindowEvent event) {
            	 try {
            	        FinalizacaoController fc = new FinalizacaoController();
            	        fc.OnCloseRequest();
            	    } catch (Exception ex) {
            	        ex.printStackTrace();
            	        System.out.println(ex);
            	    }
	            }
            });
			
			stage.setScene(scene);
			stage.setTitle("FOFUCHOS PET");
			stage.show();			
			Image image = new Image("/Icones/fofuchosLogotipo.png");
			stage.getIcons().add(image);				
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
