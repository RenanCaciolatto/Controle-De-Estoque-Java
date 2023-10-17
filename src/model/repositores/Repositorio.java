package model.repositores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DBConnection;

public interface Repositorio {	
	
	public default ResultSet selectQuery(String querySelect) {
		ResultSet resultSet = null;
		
		try {
			Connection connection = DBConnection.Conexao();
			PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
			resultSet = preparedStatement.executeQuery();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return resultSet;
	}
	
	public default void updateQuery(String query) {		
		Connection connection = DBConnection.Conexao();
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		 
	}
}
