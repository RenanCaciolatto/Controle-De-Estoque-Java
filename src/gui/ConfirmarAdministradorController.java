package gui;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class ConfirmarAdministradorController implements Initializable{
	private Main mainApp; 
	private Usuarios usuario;
	private String senhaADM;
	UsuarioDAO usuarioDAO = new UsuarioDAO();	
	
	@FXML
	private TextField usuarioAdministrador;
	@FXML
	private TextField senhaAdministrador;
	@FXML
	private Button botaoVerificar;
	@FXML
	private Button botaoVoltar;
	
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String query = "SELECT senha FROM usuario where login = 'admin'";
		try {
			ResultSet rs = usuarioDAO.selectQuery(query);
			while(rs.next()) {
				senhaADM = rs.getString("senha");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void cadastrarUsuario() {
		try {
			if(usuarioAdministrador.getText().isEmpty() || senhaAdministrador.getText().isEmpty()) {
				Alerts.showAlert("CAMPO VAZIO", null, "CERTIFIQUE DE PREENCHER TODOS OS CAMPOS!", AlertType.INFORMATION);
			}
			else if(usuarioAdministrador.getText().equals("admin") && senhaAdministrador.getText().equals(senhaADM)) {
				System.out.println(senhaADM);
				usuarioDAO.insertQueryUsuario(this.usuario);
				mainApp.abrirTelaLogin();
				Alerts.showAlert("USUÁRIO CADASTRADO", null, "USUÁRIO CADASTRADO COM SUCESSO", AlertType.INFORMATION);
			}
			else {
				Alerts.showAlert("ERRO", null, "O USUÁRIO INFORMADO NÃO EXISTE OU NÃO É UM ADMINISTRADOR!", AlertType.INFORMATION);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void voltarAoCadastro() {
		mainApp.abrirTelaCadastro();
	}

	public Usuarios getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuarios usuario) {
		this.usuario = usuario;
	}

	
}
