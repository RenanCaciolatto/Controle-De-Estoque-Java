package model.repositores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DBConnection;

public class ProductRepository {
	Connection connection = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	public ProductRepository(){
	}
	
	public void updateEstoque(String codigo, int estoque) {
		String query = "UPDATE produtos SET estoque = ? WHERE codigoProduto = ?";
		
		connection = DBConnection.Conexao();
		if(connection != null) {
			try {
				ps = connection.prepareStatement(query);
				ps.setInt(1, estoque);
				ps.setString(2, codigo);
				
				ps.executeUpdate();
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			catch(Exception e) {
				 System.out.println(e.getMessage());
			}
			finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		 }
		 
	}
	
	public void updateEnchimentos(String codigo, int enchimentos) {
		String query = "UPDATE produtos SET enchimentos = ? WHERE codigoProduto = ?";
		
		connection = DBConnection.Conexao();
		if(connection != null) {
			try {
				ps = connection.prepareStatement(query);
				ps.setInt(1, enchimentos);
				ps.setString(2, codigo);
				
				ps.executeUpdate();
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			catch(Exception e) {
				 System.out.println(e.getMessage());
			}
			finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		 }		 
	}
	
	public void updateCortes(String codigo, int cortes) {
		String query = "UPDATE produtos SET cortes = ? WHERE codigoProduto = ?";
		
		connection = DBConnection.Conexao();
		if(connection != null) {
			try {
				ps = connection.prepareStatement(query);
				ps.setInt(1, cortes);
				ps.setString(2, codigo);
				
				ps.executeUpdate();
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			catch(Exception e) {
				 System.out.println(e.getMessage());
			 }
			finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		 }
		
	}
	
	public void updateObservacoes(String codigo, String observacoes) {
		String query = "UPDATE produtos SET observacoes = ? WHERE codigoProduto = ?";
		
		connection = DBConnection.Conexao();
		if(connection != null) {
			try {
				ps = connection.prepareStatement(query);
				ps.setString(1, observacoes);
				ps.setString(2, codigo);
				
				ps.executeUpdate();
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			catch(Exception e) {
				 System.out.println(e.getMessage());
			 }
			finally {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		 }
	}
}
