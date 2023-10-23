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
import model.entities.Produto;
import model.entities.ProdutoDiario;
import model.repositores.HistoricoRepository;
import model.repositores.ProductRepository;

public class FinalizacaoController implements Initializable {
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
	@FXML
	private TextArea observacoes;

	public FinalizacaoController() {
	}
	
	ProductRepository queryDB = new ProductRepository();
	String query = null;
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	Produto produto = null;

	ObservableList<Produto> ListaProdutos = FXCollections.observableArrayList();
	ObservableList<String> ListaColecao = FXCollections.observableArrayList();
	ObservableList<Produto> ListaAlteracaoEstoque = FXCollections.observableArrayList();
	ObservableList<Produto> ListaAlteracaoEnchimentos = FXCollections.observableArrayList();
	ObservableList<Produto> ListaAlteracaoCortes = FXCollections.observableArrayList();
	ObservableList<Produto> ListaAlteracaoObservacoes = FXCollections.observableArrayList();

	// COMANDOS QUE SERÃO EXECUTADOS ASSIM QUE EXECUTAR O PROGRAMA
	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		// TELA DIÁRIO
		Constraints.setTextFieldInteger(quantidadeProdutoDiario);
		tabelaDiario.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null && newSelection.getProduct() != null && newSelection.getQuantity() != null) {
				String product = newSelection.getProduct();
				String quantity = newSelection.getQuantity();

				if (!product.isEmpty() && !quantity.isEmpty()) {
					produtoDiario.setText(product);
					quantidadeProdutoDiario.setText(quantity);
				}
			}
		});

		// TELA ESTOQUE
		carregarDados();
		definirDadosDaComboBox();
		manipuladorDeCommit();

		// TELA HISTORICO
		iniciarComboBoxMeses();
		iniciarComboBoxAnos();
	}

	// CARREGA A LISTA COM TODOS OS PRODUTOS QUE VIER DO BANCO
	private void atualizarTabela() {
		try {
			ListaProdutos.clear();
			query = "SELECT * FROM produtos";
			resultSet = queryDB.selectQuery(query);

			while (resultSet.next()) {
				ListaProdutos.add(new Produto(resultSet.getString("codigoProduto"), resultSet.getString("nomeProduto"),
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
	private void carregarDados() {
		atualizarTabela();

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
	private void definirDadosDaComboBox() {
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
	private void definirTabelaPelaComboBox() {
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
							.add(new Produto(resultSet.getString("codigoProduto"), resultSet.getString("nomeProduto"),
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
	private void definirObservacoes() {
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
	private void definirObservacoesAoEditar() {
		if (!tabelaProduto.getSelectionModel().getSelectedItems().isEmpty()) {
			Produto item = tabelaProduto.getSelectionModel().getSelectedItem();

			// Obtendo os valores de todas as colunas na linha afetada
			String codigoProduto = item.getCodigo();
			String nomeProduto = item.getNomeProduto();
			int valorEmEstoque = colEmEstoque.getCellData(item);
			int valorEnchimentos = colEnchimentos.getCellData(item);
			int valorCortes = colCortes.getCellData(item);
			String textoObservacoes = observacoes.getText();

			ListaAlteracaoObservacoes.add(new Produto(codigoProduto, nomeProduto, valorEmEstoque, valorEnchimentos,
					valorCortes, textoObservacoes));
		}
	}

	// TESTA TODAS AS COLUNAS EDITAVEIS PARA VERIFICAR SE OCORREU O EVENTO
	@FXML
	private void manipuladorDeCommit() {
		manipuladorDeCommitPorColuna(colEnchimentos);
		manipuladorDeCommitPorColuna(colEmEstoque);
		manipuladorDeCommitPorColuna(colCortes);
	}

	// VERIFICA O EVENTO OCORRIDO NA COLUNA EDITADA E SALVA EM UMA LISTA DE ESPERA
	// PARA SALVAR
	// TRATAR EXCEÇÃO
	private <T> void manipuladorDeCommitPorColuna(TableColumn<Produto, T> coluna) {
		coluna.setOnEditCommit(new EventHandler<CellEditEvent<Produto, T>>() {
			@Override
			public void handle(CellEditEvent<Produto, T> event) {

				Produto item = event.getRowValue();
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
						ListaAlteracaoEstoque.add(new Produto(codigoProduto, nomeProduto, valorEmEstoque,
								valorEnchimentos, valorCortes, textoObservacoes));

					} else if (coluna == colEnchimentos) {

						valorEnchimentos = (Integer) novoValor;
						ListaAlteracaoEnchimentos.add(new Produto(codigoProduto, nomeProduto, valorEmEstoque,
								valorEnchimentos, valorCortes, textoObservacoes));

					} else if (coluna == colCortes) {

						valorCortes = (Integer) novoValor;
						ListaAlteracaoCortes.add(new Produto(codigoProduto, nomeProduto, valorEmEstoque,
								valorEnchimentos, valorCortes, textoObservacoes));

					}
				}
			}
		});
	}

	// ENVIA UM ALERTA PERGUNTANDO SE REALMENTE DESEJA EFETUAR AS ALTERAÇÕES
	@FXML
	private int atualizarEstoque() {
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
						Produto saveProduct = ListaAlteracaoEstoque.get(i);
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
						Produto saveProduct = ListaAlteracaoEnchimentos.get(i);
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
						Produto saveProduct = ListaAlteracaoCortes.get(i);
						String idProduto = saveProduct.getCodigo();
						int cortes = saveProduct.getCortes();

						query = "UPDATE produtos SET cortes = " + cortes + " WHERE codigoProduto = '" + idProduto + "'";
						queryDB.updateQuery(query);

						ListaAlteracaoCortes.clear();
					}
				}
				if (!ListaAlteracaoObservacoes.isEmpty()) {
					for (int i = 0; i < ListaAlteracaoObservacoes.size(); i++) {
						Produto saveProduct = ListaAlteracaoObservacoes.get(i);
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
			return response;
		}
		return 0;
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
	private TableView<ProdutoDiario> tabelaDiario;
	@FXML
	private TableColumn<Produto, String> colNomeProdutoDiario;
	@FXML
	private TableColumn<Produto, Integer> colQuantidadeDiario;
	@FXML
	private Button botaoSalvar;
	@FXML
	private Button botaoDescartar;

	ObservableList<ProdutoDiario> ListaDiarioTotal = FXCollections.observableArrayList();
	ObservableList<ProdutoDiario> ListaDiario = FXCollections.observableArrayList();

	private void atualizarTabelaDiario() {
		colNomeProdutoDiario.setCellValueFactory(new PropertyValueFactory<>("product"));
		colQuantidadeDiario.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		ListaDiario.clear();
		ListaDiario.addAll(ListaDiarioTotal);

		for (int i = 0; i < ListaDiario.size(); i++) {
			tabelaDiario.setItems(ListaDiario);
		}
	}

	private void alertaCampoVazio() {
		Alerts.showAlert("CAMPO VAZIO", null, "CERTIFIQUE DE PREENCHER TODOS OS CAMPOS!", AlertType.INFORMATION);
	}

	@FXML
	private void eventoInserirDiario() {
		if (produtoDiario.getText().isEmpty() || quantidadeProdutoDiario.getText().isEmpty()) {
			alertaCampoVazio();
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
						new ProdutoDiario(produtoDiario.getText().toUpperCase(), quantidadeProdutoDiario.getText()));
			} else {
				ListaDiarioTotal.get(posicao).setQuantity(quantidadeProdutoDiario.getText());
			}
		}
		atualizarTabelaDiario();
	}

	@FXML
	private void botaoDiarioPressionado() {
		if (!tabelaDiario.getItems().isEmpty()) {
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
		} else {
			Alerts.showAlert("CAMPO VAZIO", null,
					"CERTIFIQUE-SE DE PREENCHER TODOS OS CAMPOS ANTES DE EXECUTAR A BUSCA", AlertType.INFORMATION);
		}
	}

	@FXML
	private void botaoDescartarPressionado() {
		if (tabelaDiario.getSelectionModel().getSelectedItem() != null) {
			produtoDiario.setText(tabelaDiario.getSelectionModel().getSelectedItem().getProduct());
			quantidadeProdutoDiario.setText(tabelaDiario.getSelectionModel().getSelectedItem().getQuantity());

			if (!produtoDiario.getText().isEmpty() && !quantidadeProdutoDiario.getText().isEmpty()) {
				for (int i = ListaDiarioTotal.size() - 1; i >= 0; i--) {
					if (ListaDiarioTotal.get(i).getProduct().equals(produtoDiario.getText())) {
						ListaDiarioTotal.remove(i);
						break;
					}
				}
			}
			produtoDiario.setText("");
			quantidadeProdutoDiario.setText("");
			atualizarTabelaDiario();
		}
	}
	/*
	 * 
	 * PÁGINA DE HISTÓRICO
	 * 
	 */

	@FXML
	private ComboBox<String> Meses;
	@FXML
	private ComboBox<Integer> Anos;
	@FXML
	private Button botaoBuscar;
	@FXML
	private TableView<ProdutoDiario> TabelaHistorico;
	@FXML
	private TableColumn<ProdutoDiario, String> colProdutoHistorico;
	@FXML
	private TableColumn<ProdutoDiario, Integer> colquantidadeProdutoHistorico;

	ProdutoDiario productDiario;
	ObservableList<String> TodosMeses = FXCollections.observableArrayList();
	ObservableList<Integer> TodosAnos = FXCollections.observableArrayList();
	ObservableList<ProdutoDiario> listaHistorico = FXCollections.observableArrayList();

	public void iniciarComboBoxMeses() {
		TodosMeses.add("Janeiro");
		TodosMeses.add("Fevereiro");
		TodosMeses.add("Março");
		TodosMeses.add("Abril");
		TodosMeses.add("Maio");
		TodosMeses.add("Junho");
		TodosMeses.add("Julho");
		TodosMeses.add("Agosto");
		TodosMeses.add("Setembro");
		TodosMeses.add("Outubro");
		TodosMeses.add("Novembro");
		TodosMeses.add("Dezembro");

		Meses.setItems(TodosMeses);
	}

	public void iniciarComboBoxAnos() {
		query = "SELECT * FROM historico";
		try {
			resultSet = historicoRepo.selectQuery(query);

			while (resultSet.next()) {
				int ano = resultSet.getInt("ano");
				if (!TodosAnos.contains(ano)) {
					TodosAnos.add(ano);
				}
			}
			Anos.setItems(TodosAnos);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@FXML
	private void carregarDadosHistorico() {
		if (!Meses.getSelectionModel().getSelectedItem().isEmpty()
				&& Anos.getSelectionModel().getSelectedItem() != null) {
			query = "SELECT * FROM historico WHERE mes = '" + Meses.getSelectionModel().getSelectedItem().toUpperCase()
					+ "' AND ano = " + Anos.getSelectionModel().getSelectedItem();
			try {
				resultSet = historicoRepo.selectQuery(query);

				colProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("product"));
				colquantidadeProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("quantity"));

				listaHistorico.clear();
				while (resultSet.next()) {
					String nomeProduto = resultSet.getString("nomeProduto");
					Integer quantidade = resultSet.getInt("quantidade");
					String quantidadeFormat = quantidade.toString();

					boolean produtoEncontrado = false;

					for (int i = 0; i < listaHistorico.size(); i++) {
						if (listaHistorico.get(i).getProduct().equals(nomeProduto)) {
							String quantidadeCarregada = listaHistorico.get(i).getQuantity();
							Integer formatter = Integer.parseInt(quantidadeCarregada);
							Integer quantidadeTotal = formatter + quantidade;
							String quantidadeAtualizada = quantidadeTotal.toString();

							productDiario = new ProdutoDiario(nomeProduto, quantidadeAtualizada);
							listaHistorico.set(i, productDiario);
							produtoEncontrado = true;
							break;
						}
					}

					if (!produtoEncontrado) {
						productDiario = new ProdutoDiario(nomeProduto, quantidadeFormat);
						listaHistorico.add(productDiario);
					}
				}
				TabelaHistorico.setItems(listaHistorico);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} else {
			Alerts.showAlert("CAMPO VAZIO", null,
					"CERTIFIQUE-SE DE PREENCHER TODOS OS CAMPOS ANTES DE EXECUTAR A BUSCA", AlertType.INFORMATION);
		}
	}
	
	public void OnCloseRequest() {
		if(!ListaDiario.isEmpty() || !ListaDiarioTotal.isEmpty()) {
			botaoDiarioPressionado();
			System.out.println("Executado!");
		}
		System.out.println(ListaAlteracaoEstoque.toString());
		if (!ListaAlteracaoEstoque.isEmpty() || !ListaAlteracaoEnchimentos.isEmpty() || !ListaAlteracaoCortes.isEmpty()
				|| !ListaAlteracaoObservacoes.isEmpty()) {
			atualizarEstoque();
		}
		
	}
}
