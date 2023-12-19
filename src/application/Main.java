package application;

import java.util.Optional;

import gui.FinalizacaoController;
import gui.TelaLoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Chame o método para abrir a tela de login
        abrirTelaLogin();
    }

    // Método para abrir a tela de login
    private void abrirTelaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaLogin.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Configurar o controlador, se necessário
            TelaLoginController telaLoginController = loader.getController();
            telaLoginController.setMainApp(this); // Passe a referência da classe principal, se necessário

            primaryStage.setScene(scene);
            primaryStage.setTitle("Tela de Login");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para abrir a próxima tela após o login bem-sucedido
    public void abrirProximaTela(String username) {
        try {
            // Feche o Stage atual
            primaryStage.close();

            // Crie um novo Stage para a próxima tela
            Stage novaStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Finalizacao.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
          
            novaStage.setResizable(false);
            novaStage.setScene(scene);
            novaStage.setTitle("FOFUCHOS PET");
            novaStage.show();
	        Image image = new Image("/Icones/fofuchosLogotipo.png");
	        primaryStage.getIcons().add(image);

	        primaryStage.setOnCloseRequest(evento -> {
	            evento.consume();
	            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	            alert.setTitle("Confirmação");
	            alert.setHeaderText(null);
	            alert.setContentText("Você tem certeza que deseja sair?\nClique em OK para sair.");

	            Optional<ButtonType> result = alert.showAndWait();
	            if (result.isPresent() && result.get() == ButtonType.OK) {
	                FinalizacaoController fc = loader.getController();

	                // Passe o valor do usuário para o controlador da próxima página
	                fc.setUsuario(username);
	                fc.salvamentoAutomatico();

	                primaryStage.close();
	                Platform.exit();
	            }
	        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
