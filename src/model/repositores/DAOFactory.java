package model.repositores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DBConnection;

public interface DAOFactory {	
	
	public default ResultSet selectQuery(String querySelect) throws SQLException {
		ResultSet resultSet = null;
		Connection conn = DBConnection.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement(querySelect);
		
		try {			
			resultSet = preparedStatement.executeQuery();			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return resultSet;		
	}
	
	public default void updateQuery(String query) throws SQLException {		
		Connection connection = DBConnection.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		try {			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
