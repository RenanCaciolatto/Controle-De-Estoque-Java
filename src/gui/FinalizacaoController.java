package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.DBConnection;
import gui.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
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
	ObservableList<Produto>  ListaAlteracao = FXCollections.observableArrayList();
	
	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		loadDate();
		setComboBoxDate();
		
	}
	
	@FXML
	private void setOnEditCommitHandler() {

	    setColumnEditHandler(colEmEstoque);
	    setColumnEditHandler(colEnchimentos);
	    setColumnEditHandler(colCortes);
	}
	
	private <T> void setColumnEditHandler(
	        TableColumn<Produto, T> coluna) {

	    coluna.setOnEditCommit(new EventHandler<CellEditEvent<Produto, T>>() {
	        @Override
	        public void handle(CellEditEvent<Produto, T> event) {
	            // Obtendo o item da linha afetada
	            Produto item = event.getRowValue();

	            // Obtendo os valores de todas as colunas na linha afetada
	            String codigoProduto = item.getCodigo();
	            String nomeProduto = item.getNomeProduto();
	            int valorEmEstoque = colEmEstoque.getCellData(item);
	            int valorEnchimentos = colEnchimentos.getCellData(item);
	            int valorCortes = colCortes.getCellData(item);

	            // Descobrindo qual coluna foi alterada
	            if (coluna == colEmEstoque) {
	                T novoValor = event.getNewValue();
	                if (novoValor instanceof Integer) {
	                    valorEmEstoque = (Integer) novoValor;
	                    System.out.println("A coluna Em Estoque foi alterada para: " + valorEmEstoque);
	                    ListaAlteracao.add(new Produto(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos, valorCortes));
	                }
	            
	            } else if (coluna == colEnchimentos) {
	            	T novoValor = event.getNewValue();
	                if (novoValor instanceof Integer) {
	                    valorEnchimentos = (Integer) novoValor;
	                    System.out.println("A coluna Enchimentos foi alterada para: " + valorEnchimentos);
	                    ListaAlteracao.add(new Produto(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos, valorCortes));
	                }
	            
	            } else if (coluna == colCortes) {
	            	T novoValor = event.getNewValue();
	                if (novoValor instanceof Integer) {
	                    valorCortes = (Integer) novoValor;
	                    System.out.println("A coluna Cortes foi alterada para: " + valorCortes);
	                    ListaAlteracao.add(new Produto(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos, valorCortes));
	                }
	            }
	            System.out.println(ListaAlteracao.toString());
	            
	        }
	    });
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
		
		colEmEstoque.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colEnchimentos.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		colCortes.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

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
		
		if(!ListaAlteracao.isEmpty()) {
			//COLOCAR PARA SALVAR ANTES DE MUDAR DE COLEÇÃO
		}
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
	
	@FXML
	private void updateStock() {
		if(!ListaAlteracao.isEmpty()) {
			int response = Alerts.showConfirmationAlert("CONFIRMAR?", "DESEJA SALVAR TODAS AS ALTERAÇÕES FEITAS?");
			switch(response) {
				case 1:
					ListaAlteracao.clear();
				case 2:
					for(int i = 0; i < ListaAlteracao.size(); i++) {
						Produto saveProduct = ListaAlteracao.get(i);
						String idProduto = saveProduct.getCodigo();
						int estoque = saveProduct.getEstoque();
						int enchimentos = saveProduct.getEnchimentos();
						int cortes = saveProduct.getCortes();
						query = "UPDATE produtos SET estoque = "+estoque+" enchimentos = "+enchimentos+" cortes = "+cortes+"WHERE codigoProduto = '"+idProduto+"'";
					} 
			}
		}
	}
}
