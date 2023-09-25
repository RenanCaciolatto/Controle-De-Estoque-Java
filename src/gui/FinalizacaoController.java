package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import connection.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Produto;

public class FinalizacaoController implements Initializable{
	@FXML
	private ComboBox<String> comboboxProduto;
	@FXML
	private TableView<Produto> tabelaProduto;
	@FXML
	private TableColumn<Produto, String> colCodigo;
	@FXML
	private TableColumn<Produto, String> colNomeProduto;
	@FXML
	private TableColumn<Produto, Integer> colEmEstoque;
	@FXML
	private TableColumn<Produto, Integer> colEnchimentos;
	@FXML
	private TableColumn<Produto, Integer> colCortes;
	
	String query = null;
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	Produto produto = null;
	
	ObservableList<Produto>  ListaProdutos = FXCollections.observableArrayList();
	ObservableList<String>  ListaColecao = FXCollections.observableArrayList();
	
	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		loadDate();
		setComboBoxDate();
	}
	
	private void refreshTable() {
		try {
			ListaProdutos.clear();
			
			query = "SELECT * FROM produtos";
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				ListaProdutos.add(new Produto(
					resultSet.getString("codigoProduto"),
					resultSet.getString("nomeProduto"),
					resultSet.getInt("estoque"),
					resultSet.getInt("enchimentos"),
					resultSet.getInt("cortes")));
					tabelaProduto.setItems(ListaProdutos);
			}
			
		}
		catch(SQLException e) {
			Logger.getLogger(FinalizacaoController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	private void loadDate() {
		connection = DBConnection.Conexao();
		refreshTable();
		
		colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		colNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
		colEmEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
		colEnchimentos.setCellValueFactory(new PropertyValueFactory<>("enchimentos"));
		colCortes.setCellValueFactory(new PropertyValueFactory<>("cortes"));

	}
	
	private void setComboBoxDate() {
		try { 
			query = "SELECT * FROM colecao";
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				ListaColecao.add(resultSet.getString("nomeColecao"));
				
			}
			comboboxProduto.setItems(ListaColecao);
		}
		catch(SQLException e) {
			System.out.println(e);
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	@FXML
	private void setTableDataFromComboBox() {
		ListaProdutos.clear();
		try {
			String nomeColecao = comboboxProduto.getSelectionModel().getSelectedItem();
			
			query = "SELECT idColecao FROM colecao WHERE nomeColecao = '"+nomeColecao+"'";
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
						
			while(resultSet.next()) {
				String id = resultSet.getString("idColecao");				
				query = "SELECT * FROM produtos where idColecao = "+id;
				preparedStatement = connection.prepareStatement(query);
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					ListaProdutos.add(new Produto(
						resultSet.getString("codigoProduto"),
						resultSet.getString("nomeProduto"),
						resultSet.getInt("estoque"),
						resultSet.getInt("enchimentos"),
						resultSet.getInt("cortes")));
				}				
			}

			colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
			colNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
			colEmEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
			colEnchimentos.setCellValueFactory(new PropertyValueFactory<>("enchimentos"));
			colCortes.setCellValueFactory(new PropertyValueFactory<>("cortes"));
			
			tabelaProduto.setItems(ListaProdutos);
		}
		catch(SQLException e) {
			System.out.println(e);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}