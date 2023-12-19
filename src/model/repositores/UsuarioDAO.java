package model.repositores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DBConnection;
import gui.util.Alerts;
import javafx.scene.control.Alert.AlertType;
import model.entities.Usuarios;

public class UsuarioDAO implements DAOFactory{
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	String query = null;
	
	public UsuarioDAO(){
	}
	
	public void insertQueryUsuario(Usuarios u) throws SQLException {
		query = "insert into usuario(login,senha) values(?,?);";
		connection = DBConnection.getConnection();
	    preparedStatement = connection.prepareStatement(query);
		try {
		    preparedStatement.setString(1, u.getLogin());
		    preparedStatement.setString(2, u.getSenha());

		    preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Alerts.showAlert("Erro!!", null, "ERRO NO BANCO DE DADOS: " + e.getMessage(), AlertType.ERROR);
		}
	}
}
