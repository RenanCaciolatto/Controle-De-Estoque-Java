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
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import model.entities.Product;
import model.repositores.ProductRepository;

public class FinalizacaoController implements Initializable{
	@FXML
	private ComboBox<String> comboboxProduto;
	@FXML
	private TableView<Product> tabelaProduto;
	@FXML
	private TableColumn<Product, String> colCodigo;
	@FXML
	private TableColumn<Product, String> colNomeProduto;
	@FXML
	private TableColumn<Product, Integer> colEmEstoque;
	@FXML
	private TableColumn<Product, Integer> colEnchimentos;
	@FXML
	private TableColumn<Product, Integer> colCortes;
	@FXML 
	private TextArea observacoes;
	
	String query = null;
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	Product produto = null;
	ProductRepository PR = new ProductRepository();
	
	ObservableList<Product>  ListaProdutos = FXCollections.observableArrayList();
	ObservableList<String>  ListaColecao = FXCollections.observableArrayList();
	ObservableList<Product>  ListaAlteracaoEstoque = FXCollections.observableArrayList();
	ObservableList<Product>  ListaAlteracaoEnchimentos = FXCollections.observableArrayList();
	ObservableList<Product>  ListaAlteracaoCortes = FXCollections.observableArrayList();
	ObservableList<Product>  ListaAlteracaoObservacoes = FXCollections.observableArrayList();
	
	// COMANDOS QUE SERÃO EXECUTADOS ASSIM QUE EXECUTAR O PROGRAMA
	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		loadDate();
		setComboBoxDate();
		setOnEditCommitHandler();
	}
	
	// CARREGA A LISTA COM TODOS OS PRODUTOS QUE VIER DO BANCO
	private void refreshTable() {
		try {
			ListaProdutos.clear();
			
			query = "SELECT * FROM produtos";
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				ListaProdutos.add(new Product(
					resultSet.getString("codigoProduto"),
					resultSet.getString("nomeProduto"),
					resultSet.getInt("estoque"),
					resultSet.getInt("enchimentos"),
					resultSet.getInt("cortes"),
					resultSet.getString("observacoes")));
					tabelaProduto.setItems(ListaProdutos);
			}
			
		}
		catch(SQLException e) {
			Logger.getLogger(FinalizacaoController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	// CARREGA A TABELA COM AS INFORMAÇÕES DO BANCO E DEFINE AS ULTIMAS 3 COLUNAS COMO EDITAVEIS
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
	
	// CARREGA A COMBOBOX COM AS COLEÇÕES QUE VEM DO BANCO
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
	
	// EVENTO PARA CARREGAR OS DADOS DA TABELA DE ACORDO COM A COLEÇÃO SELECIONADA NA COMBOBOX
	@FXML
	private void setTableDataFromComboBox() {
		ListaProdutos.clear();
		
		if(!ListaAlteracaoEstoque.isEmpty() || !ListaAlteracaoEnchimentos.isEmpty() || !ListaAlteracaoCortes.isEmpty()) {
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
					ListaProdutos.add(new Product(
						resultSet.getString("codigoProduto"),
						resultSet.getString("nomeProduto"),
						resultSet.getInt("estoque"),
						resultSet.getInt("enchimentos"),
						resultSet.getInt("cortes"),
						resultSet.getString("observacoes")));
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
	
	// CARREGA A TXTFIELD DE OBSERVAÇÕES DE ACORDO COM A LINHA SELECIONADA
	@FXML
	private void setObservacoesData() {
		if(!tabelaProduto.getSelectionModel().getSelectedItems().isEmpty()) {
			String codigoSelecionado = tabelaProduto.getSelectionModel().getSelectedItem().getCodigo();
			String query = "SELECT observacoes FROM produtos WHERE codigoProduto = ?";
			try {
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, codigoSelecionado);
				
				resultSet = preparedStatement.executeQuery();				
				while(resultSet.next()) {
					String observacoesBanco = resultSet.getString("observacoes");
					
					observacoes.setText(observacoesBanco);
				}
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	// SALVA A ALTERAÇÃO FEITA NA TXTFIELD DE OBSERVAÇÕES
	@FXML
	private void setOndEditObservacoes() {
		if(!tabelaProduto.getSelectionModel().getSelectedItems().isEmpty()) {
			Product item = tabelaProduto.getSelectionModel().getSelectedItem();
			
			// Obtendo os valores de todas as colunas na linha afetada
            String codigoProduto = item.getCodigo();
            String nomeProduto = item.getNomeProduto();
            int valorEmEstoque = colEmEstoque.getCellData(item);
            int valorEnchimentos = colEnchimentos.getCellData(item);
            int valorCortes = colCortes.getCellData(item);
            String textoObservacoes = observacoes.getText();
            
			ListaAlteracaoObservacoes.add(new Product(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos, valorCortes, textoObservacoes));
			System.out.println(ListaAlteracaoObservacoes.toString());
		}
	}
	
	// TESTA TODAS AS COLUNAS EDITAVEIS PARA VERIFICAR SE OCORREU O EVENTO
	@FXML
	private void setOnEditCommitHandler() {

	    setColumnEditHandler(colEmEstoque);
	    setColumnEditHandler(colEnchimentos);
	    setColumnEditHandler(colCortes);
	}
	
	// VERIFICA O EVENTO OCORRIDO NA COLUNA EDITADA E SALVA EM UMA LISTA DE ESPERA PARA SALVAR
	private <T> void setColumnEditHandler(
	        TableColumn<Product, T> coluna) {

	    coluna.setOnEditCommit(new EventHandler<CellEditEvent<Product, T>>() {
	        @Override
	        public void handle(CellEditEvent<Product, T> event) {
	            Product item = event.getRowValue();

	            // Obtendo os valores de todas as colunas na linha afetada
	            String codigoProduto = item.getCodigo();
	            String nomeProduto = item.getNomeProduto();
	            int valorEmEstoque = colEmEstoque.getCellData(item);
	            int valorEnchimentos = colEnchimentos.getCellData(item);
	            int valorCortes = colCortes.getCellData(item);
	            String textoObservacoes = observacoes.getText();
	            // Descobrindo qual coluna foi alterada
	            if (coluna == colEmEstoque) {	            	
	                T novoValor = event.getNewValue();
	                
	                if (novoValor instanceof Integer) {
	                    valorEmEstoque = (Integer) novoValor;
	                    System.out.println("A coluna Em Estoque foi alterada para: " + valorEmEstoque);
	                    ListaAlteracaoEstoque.add(new Product(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos, valorCortes, textoObservacoes));
	                    
	                    System.out.println(ListaAlteracaoEstoque.toString());	                    
	                }
	            
	            } else if (coluna == colEnchimentos) {
	            	T novoValor = event.getNewValue();
	            	
	                if (novoValor instanceof Integer) {
	                    valorEnchimentos = (Integer) novoValor;
	                    System.out.println("A coluna Enchimentos foi alterada para: " + valorEnchimentos);
	                    ListaAlteracaoEnchimentos.add(new Product(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos, valorCortes, textoObservacoes));
	                
	                    System.out.println(ListaAlteracaoEnchimentos.toString());	                    
	                }
	            
	            } else if (coluna == colCortes) {
	            	T novoValor = event.getNewValue();
	            	
	                if (novoValor instanceof Integer) {
	                    valorCortes = (Integer) novoValor;
	                    System.out.println("A coluna Cortes foi alterada para: " + valorCortes);
	                    ListaAlteracaoCortes.add(new Product(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos, valorCortes, textoObservacoes));
	                
	                    System.out.println(ListaAlteracaoCortes.toString());	                    
	                }
	            }	            
	        }
	    });
	}
	
	// ENVIA UM ALERTA PERGUNTANDO SE REALMENTE DESEJA EFETUAR AS ALTERAÇÕES
	@FXML
	private void updateStock() {
		if(!ListaAlteracaoEstoque.isEmpty() || !ListaAlteracaoEnchimentos.isEmpty() || !ListaAlteracaoCortes.isEmpty() || !ListaAlteracaoObservacoes.isEmpty()) {
			int response = Alerts.showConfirmationAlert("CONFIRMAR?", "DESEJA SALVAR TODAS AS ALTERAÇÕES FEITAS?");
			switch(response) {
				case 1:
					ListaAlteracaoEstoque.clear();
					ListaAlteracaoEnchimentos.clear();
					ListaAlteracaoCortes.clear();
					ListaAlteracaoObservacoes.clear();
				case 2:
					if(!ListaAlteracaoEstoque.isEmpty()) {
						for(int i = 0; i < ListaAlteracaoEstoque.size(); i++) {
							Product saveProduct = ListaAlteracaoEstoque.get(i);
							String idProduto = saveProduct.getCodigo();
							int estoque = saveProduct.getEstoque();
							
							PR.updateEstoque(idProduto, estoque);
						}
					}
					
					if(!ListaAlteracaoEnchimentos.isEmpty()) {
						for(int i = 0; i < ListaAlteracaoEnchimentos.size(); i++) {
							Product saveProduct = ListaAlteracaoEnchimentos.get(i);
							String idProduto = saveProduct.getCodigo();
							int enchimentos = saveProduct.getEnchimentos();
							
							PR.updateEnchimentos(idProduto, enchimentos);
						}
					}
					
					if(!ListaAlteracaoCortes.isEmpty()) {
						for(int i = 0; i < ListaAlteracaoCortes.size(); i++) {
							Product saveProduct = ListaAlteracaoCortes.get(i);
							String idProduto = saveProduct.getCodigo();
							int enchimentos = saveProduct.getCortes();
							
							PR.updateCortes(idProduto, enchimentos);
						}
					}
					
					if(!ListaAlteracaoObservacoes.isEmpty()) {
						for(int i = 0; i < ListaAlteracaoObservacoes.size(); i++) {
							Product saveProduct = ListaAlteracaoObservacoes.get(i);
							String idProduto = saveProduct.getCodigo();
							String observacao = saveProduct.getObservacoes();
							
							PR.updateObservacoes(idProduto, observacao);
						}
					}
			}
		}
	}
}
