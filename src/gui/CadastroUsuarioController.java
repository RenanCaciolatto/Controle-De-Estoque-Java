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

public class CadastroUsuarioController implements Initializable{

	@FXML
	private Button cadastrar;
	@FXML
	private Button voltar;
	@FXML
	private TextField usuario;
	@FXML
	private TextField senha;
	List<Usuarios> listaUsuarios = new ArrayList<>();
	List<String> nomesUsuarios = new ArrayList<>();
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	
	private Main mainApp; // Campo para armazenar a referência da classe principal

    // Método para configurar a referência da classe principal
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		carregarLista();
		for(Usuarios u : listaUsuarios) {
			String nomes = u.getLogin();			
			nomesUsuarios.add(nomes);
		}
	}
	
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
	private void cadastrarUsuario() throws SQLException {
		
		if(usuario.getText().isEmpty() || senha.getText().isEmpty()) {
			Alerts.showAlert("CAMPO VAZIO", null, "CERTIFIQUE DE PREENCHER TODOS OS CAMPOS!", AlertType.INFORMATION);
		}
		else if(nomesUsuarios.contains(usuario.getText())) {
			Alerts.showAlert("USUÁRIO CADASTRADO", null, "O USUÁRIO JÁ ESTÁ CADASTRADO NO BANCO DE DADOS", AlertType.INFORMATION);
		}
		else {
			mainApp.abrirTelaConfirmarAdministrador(new Usuarios(usuario.getText(),senha.getText()));
			
		}
	}

	@FXML
	private void telaDeLogin() {
		mainApp.abrirTelaLogin();
	}
}
