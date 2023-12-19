package application;

import java.util.Optional;

import gui.CadastroUsuarioController;
import gui.ConfirmarAdministradorController;
import gui.FinalizacaoController;
import gui.TelaLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.entities.Usuarios;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Chame o método para abrir a tela de login
        abrirTelaLogin();
    }

    // Método para abrir a tela de login
    public void abrirTelaLogin() {
        try {
        	primaryStage.close();
        	
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/TelaLogin.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Configurar o controlador
            TelaLoginController telaLoginController = loader.getController();
            telaLoginController.setMainApp(this); // Passe a referência da classe principal, se necessário

            primaryStage.setScene(scene);
            primaryStage.setTitle("Tela de Login");
            primaryStage.show();
            Image image = new Image("/Icones/fofuchosLogotipo.png");
            primaryStage.getIcons().add(image);
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
	        novaStage.getIcons().add(image);

	        FinalizacaoController fc = loader.getController();
	        fc.setUsuario(username);
	        novaStage.setOnCloseRequest(evento -> {
	            evento.consume();
	            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	            alert.setTitle("Confirmação");
	            alert.setHeaderText(null);
	            alert.setContentText("Você tem certeza que deseja sair?\nClique em OK para sair.");

	            Optional<ButtonType> result = alert.showAndWait();
	            if (result.isPresent() && result.get() == ButtonType.OK) {	                
	                fc.salvamentoAutomatico();

	                novaStage.close();
	                abrirTelaLogin();
	            }
	        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void abrirTelaCadastro() {
    	try {
    		// Feche o Stage atual
            primaryStage.close();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/CadastroUsuario.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Configurar o controlador
            CadastroUsuarioController cadastroUsuarioController = loader.getController();
            cadastroUsuarioController.setMainApp(this); // Passe a referência da classe principal, se necessário

            primaryStage.setScene(scene);
            primaryStage.setTitle("Tela de Cadastro");
            primaryStage.show();
            Image image = new Image("/Icones/fofuchosLogotipo.png");
            primaryStage.getIcons().add(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void abrirTelaConfirmarAdministrador(Usuarios user) {
    	try {
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ConfirmarAdministrador.fxml"));
	        Parent root = loader.load();
	        Scene scene = new Scene(root);
	        
	        ConfirmarAdministradorController cac = loader.getController();
	        cac.setMainApp(this); // Passe a referência da classe principal, se necessário
	        
	        cac.setUsuario(user);
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Tela de Cadastro");
	        primaryStage.show();
	        Image image = new Image("/Icones/fofuchosLogotipo.png");
	        primaryStage.getIcons().add(image);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
