package gui;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.entities.Usuarios;
import model.repositores.UsuarioDAO;

public class TelaLoginController implements Initializable {

	private Main mainApp; // Campo para armazenar a referência da classe principal

    // Método para configurar a referência da classe principal
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
	
	List<Usuarios> listaUsuarios = new ArrayList<>();
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		carregarLista();
	}
	
	@FXML
	private TextField usuario;
	@FXML
	private TextField senha;
	@FXML
	private Button botaoEnviar;
	
	// Carrega a lista de usuarios
	private void carregarLista() {
		String query = "SELECT * FROM usuario";
		try {
			ResultSet rs = usuarioDAO.selectQuery(query);
			while(rs.next()) {
				listaUsuarios.add(new Usuarios(rs.getString("login"), rs.getString("senha"))); 
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void testarLogin() {
		if(!verificarLogin()) {
			Alerts.showAlert("ERRO", null, "USUARIO OU SENHA INCORRETOS", AlertType.INFORMATION);
		}
		else {
			logadoComSucesso(usuario.getText());
		}
	}
	
	//Verificar o login
	public boolean verificarLogin() {
		if(usuario.getText().isEmpty() || senha.getText().isEmpty()) {
			Alerts.showAlert("CAMPO VAZIO", null, "CERTIFIQUE DE PREENCHER TODOS OS CAMPOS!", AlertType.INFORMATION);
			return false;
		}
		else {
			for (Usuarios user : listaUsuarios) {
				if (user.getLogin().equals(usuario.getText()) && user.getSenha().equals(senha.getText())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void logadoComSucesso(String username) {
		mainApp.abrirProximaTela(username);
	}
}
