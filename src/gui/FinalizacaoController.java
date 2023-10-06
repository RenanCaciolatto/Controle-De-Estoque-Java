package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import gui.util.Alerts;
import gui.util.Constraints;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import model.entities.Historico;
import model.entities.Product;
import model.entities.ProductDiario;
import model.repositores.HistoricoRepository;
import model.repositores.ProductRepository;

public class FinalizacaoController implements Initializable {
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

	ProductRepository queryDB = new ProductRepository();
	String query = null;
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	Product produto = null;

	ObservableList<Product> ListaProdutos = FXCollections.observableArrayList();
	ObservableList<String> ListaColecao = FXCollections.observableArrayList();
	ObservableList<Product> ListaAlteracaoEstoque = FXCollections.observableArrayList();
	ObservableList<Product> ListaAlteracaoEnchimentos = FXCollections.observableArrayList();
	ObservableList<Product> ListaAlteracaoCortes = FXCollections.observableArrayList();
	ObservableList<Product> ListaAlteracaoObservacoes = FXCollections.observableArrayList();

	// COMANDOS QUE SERÃO EXECUTADOS ASSIM QUE EXECUTAR O PROGRAMA
	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		// TELA DIÁRIO
		Constraints.setTextFieldInteger(quantidadeProdutoDiario);
		tabelaDiario.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

			if (!tabelaDiario.getSelectionModel().getSelectedItem().getProduct().isEmpty()) {
				produtoDiario.setText(tabelaDiario.getSelectionModel().getSelectedItem().getProduct());
				quantidadeProdutoDiario.setText(tabelaDiario.getSelectionModel().getSelectedItem().getQuantity());
			}
		});

		// TELA ESTOQUE
		loadDate();
		setComboBoxDate();
		setOnEditCommitHandler();

		// TELA HISTORICO
		startComboBoxMonths();
		startComboBoxYears();
	}

	// CARREGA A LISTA COM TODOS OS PRODUTOS QUE VIER DO BANCO
	private void refreshTable() {
		try {
			ListaProdutos.clear();
			query = "SELECT * FROM produtos";
			resultSet = queryDB.selectQuery(query);

			while (resultSet.next()) {
				ListaProdutos.add(new Product(resultSet.getString("codigoProduto"), resultSet.getString("nomeProduto"),
						resultSet.getInt("estoque"), resultSet.getInt("enchimentos"), resultSet.getInt("cortes"),
						resultSet.getString("observacoes")));
				tabelaProduto.setItems(ListaProdutos);
			}
		} catch (SQLException e) {
			Logger.getLogger(FinalizacaoController.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	// CARREGA A TABELA COM AS INFORMAÇÕES DO BANCO E DEFINE AS ULTIMAS 3 COLUNAS
	// COMO EDITAVEIS
	private void loadDate() {
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
			resultSet = queryDB.selectQuery(query);

			while (resultSet.next()) {
				ListaColecao.add(resultSet.getString("nomeColecao"));
			}
			comboboxProduto.setItems(ListaColecao);
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// EVENTO PARA CARREGAR OS DADOS DA TABELA DE ACORDO COM A COLEÇÃO SELECIONADA
	// NA COMBOBOX
	@FXML
	private void setTableDataFromComboBox() {
		ListaProdutos.clear();
		try {
			String nomeColecao = comboboxProduto.getSelectionModel().getSelectedItem();
			query = "SELECT idColecao FROM colecao WHERE nomeColecao = '" + nomeColecao + "'";
			resultSet = queryDB.selectQuery(query);

			while (resultSet.next()) {
				String id = resultSet.getString("idColecao");
				query = "SELECT * FROM produtos where idColecao = " + id;
				resultSet = queryDB.selectQuery(query);

				while (resultSet.next()) {
					ListaProdutos
							.add(new Product(resultSet.getString("codigoProduto"), resultSet.getString("nomeProduto"),
									resultSet.getInt("estoque"), resultSet.getInt("enchimentos"),
									resultSet.getInt("cortes"), resultSet.getString("observacoes")));
				}
			}
			colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
			colNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
			colEmEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
			colEnchimentos.setCellValueFactory(new PropertyValueFactory<>("enchimentos"));
			colCortes.setCellValueFactory(new PropertyValueFactory<>("cortes"));

			tabelaProduto.setItems(ListaProdutos);
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	// CARREGA A TXTFIELD DE OBSERVAÇÕES DE ACORDO COM A LINHA SELECIONADA
	@FXML
	private void setObservacoesData() {
		if (!tabelaProduto.getSelectionModel().getSelectedItems().isEmpty()) {
			String codigoSelecionado = tabelaProduto.getSelectionModel().getSelectedItem().getCodigo();
			try {
				query = "SELECT observacoes FROM produtos WHERE codigoProduto = '" + codigoSelecionado + "'";
				resultSet = queryDB.selectQuery(query);
				while (resultSet.next()) {
					String observacoesBanco = resultSet.getString("observacoes");
					observacoes.setText(observacoesBanco);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// SALVA A ALTERAÇÃO FEITA NA TXTFIELD DE OBSERVAÇÕES
	@FXML
	private void setOndEditObservacoes() {
		if (!tabelaProduto.getSelectionModel().getSelectedItems().isEmpty()) {
			Product item = tabelaProduto.getSelectionModel().getSelectedItem();

			// Obtendo os valores de todas as colunas na linha afetada
			String codigoProduto = item.getCodigo();
			String nomeProduto = item.getNomeProduto();
			int valorEmEstoque = colEmEstoque.getCellData(item);
			int valorEnchimentos = colEnchimentos.getCellData(item);
			int valorCortes = colCortes.getCellData(item);
			String textoObservacoes = observacoes.getText();

			ListaAlteracaoObservacoes.add(new Product(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos,
					valorCortes, textoObservacoes));
			System.out.println(ListaAlteracaoObservacoes.toString());
		}
	}

	// TESTA TODAS AS COLUNAS EDITAVEIS PARA VERIFICAR SE OCORREU O EVENTO
	@FXML
	private void setOnEditCommitHandler() {
		setColumnEditHandler(colEnchimentos);
		setColumnEditHandler(colEmEstoque);
		setColumnEditHandler(colCortes);
	}

	// VERIFICA O EVENTO OCORRIDO NA COLUNA EDITADA E SALVA EM UMA LISTA DE ESPERA
	// PARA SALVAR
	// TRATAR EXCEÇÃO
	private <T> void setColumnEditHandler(TableColumn<Product, T> coluna) {
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

				T novoValor = event.getNewValue();
				if (novoValor instanceof Integer) {
					// Descobrindo qual coluna foi alterada
					if (coluna == colEmEstoque) {

						valorEmEstoque = (Integer) novoValor;
						System.out.println("A coluna Em Estoque foi alterada para: " + valorEmEstoque);
						ListaAlteracaoEstoque.add(new Product(codigoProduto, nomeProduto, valorEmEstoque,
								valorEnchimentos, valorCortes, textoObservacoes));

					} else if (coluna == colEnchimentos) {

						valorEnchimentos = (Integer) novoValor;
						System.out.println("A coluna Enchimentos foi alterada para: " + valorEnchimentos);
						ListaAlteracaoEnchimentos.add(new Product(codigoProduto, nomeProduto, valorEmEstoque,
								valorEnchimentos, valorCortes, textoObservacoes));

					} else if (coluna == colCortes) {

						valorCortes = (Integer) novoValor;
						System.out.println("A coluna Cortes foi alterada para: " + valorCortes);
						ListaAlteracaoCortes.add(new Product(codigoProduto, nomeProduto, valorEmEstoque,
								valorEnchimentos, valorCortes, textoObservacoes));

					}
				}
			}
		});
	}

	// ENVIA UM ALERTA PERGUNTANDO SE REALMENTE DESEJA EFETUAR AS ALTERAÇÕES
	@FXML
	private void updateStock() {
		if (!ListaAlteracaoEstoque.isEmpty() || !ListaAlteracaoEnchimentos.isEmpty() || !ListaAlteracaoCortes.isEmpty()
				|| !ListaAlteracaoObservacoes.isEmpty()) {
			int response = Alerts.showConfirmationAlert("CONFIRMAR?", "DESEJA SALVAR TODAS AS ALTERAÇÕES FEITAS?");
			switch (response) {
			case 1:
				ListaAlteracaoEstoque.clear();
				ListaAlteracaoEnchimentos.clear();
				ListaAlteracaoCortes.clear();
				ListaAlteracaoObservacoes.clear();
				break;
			case 2:
				if (!ListaAlteracaoEstoque.isEmpty()) {
					for (int i = 0; i < ListaAlteracaoEstoque.size(); i++) {
						Product saveProduct = ListaAlteracaoEstoque.get(i);
						String idProduto = saveProduct.getCodigo();
						int estoque = saveProduct.getEstoque();

						query = "UPDATE produtos SET estoque = " + estoque + " WHERE codigoProduto = '" + idProduto
								+ "'";
						queryDB.updateQuery(query);

						ListaAlteracaoEstoque.clear();
					}
				}
				if (!ListaAlteracaoEnchimentos.isEmpty()) {
					for (int i = 0; i < ListaAlteracaoEnchimentos.size(); i++) {
						Product saveProduct = ListaAlteracaoEnchimentos.get(i);
						String idProduto = saveProduct.getCodigo();
						int enchimentos = saveProduct.getEnchimentos();

						query = "UPDATE produtos SET enchimentos = " + enchimentos + " WHERE codigoProduto = '"
								+ idProduto + "'";
						queryDB.updateQuery(query);

						ListaAlteracaoEnchimentos.clear();
					}
				}
				if (!ListaAlteracaoCortes.isEmpty()) {
					for (int i = 0; i < ListaAlteracaoCortes.size(); i++) {
						Product saveProduct = ListaAlteracaoCortes.get(i);
						String idProduto = saveProduct.getCodigo();
						int cortes = saveProduct.getCortes();

						query = "UPDATE produtos SET cortes = " + cortes + " WHERE codigoProduto = '" + idProduto + "'";
						queryDB.updateQuery(query);

						ListaAlteracaoCortes.clear();
					}
				}
				if (!ListaAlteracaoObservacoes.isEmpty()) {
					for (int i = 0; i < ListaAlteracaoObservacoes.size(); i++) {
						Product saveProduct = ListaAlteracaoObservacoes.get(i);
						String idProduto = saveProduct.getCodigo();
						String observacao = saveProduct.getObservacoes();

						query = "UPDATE produtos SET observacoes = '" + observacao + "' WHERE codigoProduto = '"
								+ idProduto + "'";
						queryDB.updateQuery(query);

						ListaAlteracaoObservacoes.clear();
					}
				}
				break;
			}
		}
	}

	/*
	 * 
	 * PÁGINA DE DIÁRIO
	 * 
	 */

	Historico historico = new Historico();
	HistoricoRepository historicoRepo = new HistoricoRepository();

	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	@FXML
	private TextField produtoDiario;
	@FXML
	private TextField quantidadeProdutoDiario;
	@FXML
	private TableView<ProductDiario> tabelaDiario;
	@FXML
	private TableColumn<Product, String> colNomeProdutoDiario;
	@FXML
	private TableColumn<Product, Integer> colQuantidadeDiario;
	@FXML
	private Button botaoSalvar;

	ObservableList<ProductDiario> ListaDiarioTotal = FXCollections.observableArrayList();
	ObservableList<ProductDiario> ListaDiario = FXCollections.observableArrayList();

	private void refreshTableDiario() {
		colNomeProdutoDiario.setCellValueFactory(new PropertyValueFactory<>("product"));
		colQuantidadeDiario.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		ListaDiario.clear();
		ListaDiario.addAll(ListaDiarioTotal);

		for (int i = 0; i < ListaDiario.size(); i++) {
			tabelaDiario.setItems(ListaDiario);
		}
	}

	private void alertEmptyValues() {
		Alerts.showAlert("CAMPO VAZIO", null, "CERTIFIQUE DE PREENCHER TODOS OS CAMPOS!", AlertType.INFORMATION);
	}

	@FXML
	private void insertOnActionDiario() {
		if (produtoDiario.getText().isEmpty() || quantidadeProdutoDiario.getText().isEmpty()) {
			alertEmptyValues();
		} else {
			Integer posicao = null;
			boolean has = false;
			for (int j = 0; j < ListaDiario.size(); j++) {
				if (ListaDiario.get(j).getProduct().equals(produtoDiario.getText().toUpperCase())) {
					has = true;
					posicao = j;
				}
			}
			if (has == false) {
				ListaDiarioTotal.add(
						new ProductDiario(produtoDiario.getText().toUpperCase(), quantidadeProdutoDiario.getText()));
			} else {
				ListaDiarioTotal.get(posicao).setQuantity(quantidadeProdutoDiario.getText());
			}
		}
		refreshTableDiario();
	}

	@FXML
	private void onButtonDiarioPressed() {
		int response = Alerts.showConfirmationAlert("SALVAR DIARIO",
				"DESEJA SALVAR TODOS OS ITENS NA DATA DE HOJE " + dtf.format(LocalDate.now()) + "?");
		switch (response) {
		case 1:
			ListaDiarioTotal.clear();
			break;
		case 2:

			LocalDate data = LocalDate.now();
			String mes = LocalDate.now().getMonth().toString();
			mes = historicoRepo.traduct(mes);
			int ano = LocalDate.now().getYear();

			historico = new Historico(ListaDiarioTotal, dtf.format(data).toString(), mes, ano);
			historicoRepo.insertQuery(historico);
			break;
		}
	}

	/*
	 * 
	 * PÁGINA DE HISTÓRICO
	 * 
	 */

	@FXML
	private ComboBox<String> Months;
	@FXML
	private ComboBox<Integer> Years;
	@FXML
	private Button buttonBuscar;
	@FXML
	private TableView<ProductDiario> TableHistorico;
	@FXML
	private TableColumn<ProductDiario, String> colProdutoHistorico;
	@FXML
	private TableColumn<ProductDiario, Integer> colquantidadeProdutoHistorico;
	ObservableList<String> AllMonths = FXCollections.observableArrayList();
	ObservableList<Integer> AllYears = FXCollections.observableArrayList();
	ObservableList<ProductDiario> listaHistorico = FXCollections.observableArrayList();
	
	public void startComboBoxMonths() {
		AllMonths.add("Janeiro");
		AllMonths.add("Fevereiro");
		AllMonths.add("Março");
		AllMonths.add("Abril");
		AllMonths.add("Maio");
		AllMonths.add("Junho");
		AllMonths.add("Julho");
		AllMonths.add("Agosto");
		AllMonths.add("Setembro");
		AllMonths.add("Outubro");
		AllMonths.add("Novembro");
		AllMonths.add("Dezembro");

		Months.setItems(AllMonths);
	}
	
	public void startComboBoxYears() {
		query = "SELECT * FROM historico";
		try {
			resultSet = historicoRepo.selectQuery(query);
			
			while(resultSet.next()) {					
				int ano = resultSet.getInt("ano");
				if(!AllYears.contains(ano)) {
					AllYears.add(ano);
				}				
			}
			Years.setItems(AllYears);
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@FXML
	private void loadDataHistorico() {		
	    if (!Months.getSelectionModel().getSelectedItem().isEmpty() && Years.getSelectionModel().getSelectedItem() != null) {			
	        query = "SELECT * FROM historico WHERE mes = '"+Months.getSelectionModel().getSelectedItem().toUpperCase()+"' AND ano = "+Years.getSelectionModel().getSelectedItem();
	        try {				
	            resultSet = historicoRepo.selectQuery(query);

	            colProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("product"));
	            colquantidadeProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("quantity"));

	            listaHistorico.clear();
	            while (resultSet.next()) {
	                String nomeProduto = resultSet.getString("nomeProduto");
	                Integer quantidade = resultSet.getInt("quantidade");
	                String quantidadeFormat = quantidade.toString();

	                listaHistorico.add(new ProductDiario(nomeProduto, quantidadeFormat));
	            }

	            TableHistorico.setItems(listaHistorico);
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    else {
			Alerts.showAlert("CAMPO VAZIO", null, "CERTIFIQUE-SE DE PREENCHER TODOS OS CAMPOS ANTES DE EXECUTAR A BUSCA", AlertType.INFORMATION);
		}
	}
}
