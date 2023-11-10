package model.repositores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DBConnection;
import gui.util.Alerts;
import javafx.scene.control.Alert.AlertType;
import model.entities.Historico;
import model.entities.ProdutoDiario;

public class HistoricoDAO implements DAOFactory{
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	String query = null;
	
	public String traduct(String mesTraduzido) {
	    String traductedMonth;
	    switch(mesTraduzido.toLowerCase()) {
	        case "january":
	            traductedMonth = "Janeiro";
	            break;
	        case "february":
	            traductedMonth = "Fevereiro";
	            break;
	        case "march":
	            traductedMonth = "Março";
	            break;
	        case "april":
	            traductedMonth = "Abril";
	            break;
	        case "may":
	            traductedMonth = "Maio";
	            break;
	        case "june":
	            traductedMonth = "Junho";
	            break;
	        case "july":
	            traductedMonth = "Julho";
	            break;
	        case "august":
	            traductedMonth = "Agosto";
	            break;
	        case "september":
	            traductedMonth = "Setembro";
	            break;
	        case "october":
	            traductedMonth = "Outubro";
	            break;
	        case "november":
	            traductedMonth = "Novembro";
	            break;
	        case "december":
	            traductedMonth = "Dezembro";
	            break;
	        default:
	            traductedMonth = "Mês desconhecido";
	            break;
	    }
	    return traductedMonth.toUpperCase();
	}
	
	public void insertQueryProdutoDiario(ProdutoDiario produto, int dia, String month, int year) throws SQLException {
		query = "INSERT INTO historico(nomeProduto, quantidade, dataAlteracao,dia, mes, ano) VALUES(?,?,?,?,?,?)";
		connection = DBConnection.getConnection();
	    preparedStatement = connection.prepareStatement(query);
		try {
		    preparedStatement.setString(1, produto.getProduct());
		    preparedStatement.setInt(2, Integer.parseInt(produto.getQuantity()));
		    preparedStatement.setString(3, produto.getDataAlteracao());
		    preparedStatement.setInt(4, dia);
		    preparedStatement.setString(5, month);
		    preparedStatement.setInt(6, year);

		    preparedStatement.executeUpdate();
		} catch (SQLException e) {
		    Alerts.showAlert("ERROR", null, "ERRO DESCONHECIDO: "+ e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void insertQuery(Historico historico) throws SQLException {
		for (int i = 0; i < historico.getLista().size(); i++) {
			query = "INSERT INTO historico(nomeProduto, quantidade, dataAlteracao, dia, mes, ano) VALUES(?,?,?,?,?)";
			connection = DBConnection.getConnection();
		    preparedStatement = connection.prepareStatement(query);
			try {
				int quantidade = Integer.parseInt(historico.getLista().get(i).getQuantity());
			    
			    preparedStatement.setString(1, historico.getLista().get(i).getProduct());
			    preparedStatement.setInt(2, quantidade);
			    preparedStatement.setString(3, historico.getData());
			    preparedStatement.setString(4, historico.getMonth());
			    preparedStatement.setInt(5, historico.getYear());

			    preparedStatement.executeUpdate();
			} catch (SQLException e) {
			    Alerts.showAlert("ERROR", null, "ERRO DESCONHECIDO: "+ e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
