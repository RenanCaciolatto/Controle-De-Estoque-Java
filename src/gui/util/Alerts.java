package gui.util;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerts {

	public static void showAlert(String title, String header, String content, AlertType type) {
		Alert alerts = new Alert(type);
		alerts.setTitle(title);
		alerts.setHeaderText(header);
		alerts.setContentText(content);
		alerts.show();
	}
	
	public static int showConfirmationAlert(String title, String content) {
		Alert alerts = new Alert(AlertType.CONFIRMATION);
		alerts.setTitle(title);
		alerts.setHeaderText(null);
		alerts.setContentText(content);
		
		ButtonType botaoUm = new ButtonType("CANCELAR");
		ButtonType botaoDois = new ButtonType("DESCARTAR");
		ButtonType botaoTres = new ButtonType("SALVAR");
		alerts.getButtonTypes().setAll(botaoUm, botaoDois, botaoTres);
		
		AtomicInteger result = new AtomicInteger(0);
		
		alerts.showAndWait().ifPresent(response ->{
			if(response == botaoUm) {
				alerts.close();
				result.set(0);
			}
			else if (response == botaoDois) {
				result.set(1);
			}
			else if(response == botaoTres){
				result.set(2);
			}
		});
		return result.get();
	}
}