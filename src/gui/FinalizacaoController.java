package gui;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import gui.util.Alerts;
import gui.util.Constraints;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import model.entities.DiarioManipulador;
import model.entities.Historico;
import model.entities.Produto;
import model.entities.ProdutoDiario;
import model.repositores.HistoricoDAO;
import model.repositores.ProductDAO;

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
	
	public FinalizacaoController() {}
	
	ProductDAO queryDB = new ProductDAO();
	String query = null;
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	Produto produto = null;
	double tamanhoMedioCadaCelula = 25.0;

	ObservableList<Produto> ListaProdutos = FXCollections.observableArrayList();
	ObservableList<String> ListaProdutosString = FXCollections.observableArrayList();
	ObservableList<String> ListaColecao = FXCollections.observableArrayList();

	List<String> listaAlteracoes = new ArrayList<>();
	
	@Override
	public void initialize(java.net.URL url, ResourceBundle rb) {
		// TELA DIÁRIO
		definirTabelaDiario();
		Constraints.setTextFieldInteger(quantidadeProdutoDiario);
		tabelaDiario.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null && newSelection.getProduct() != null && newSelection.getQuantity() != null) {
				String product = newSelection.getProduct();
				String quantity = newSelection.getQuantity();

				if (!product.isEmpty() && !quantity.isEmpty()) {
					produtoDiario.setText(product);
					resultadoListView.setVisible(false);
					quantidadeProdutoDiario.setText(quantity);
				}
			}
		});

		resultadoListView.setItems(FXCollections.observableArrayList());
	    resultadoListView.setVisible(false);

	    resultadoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selecionarItem(newValue));

	    produtoDiario.textProperty().addListener((observable, oldValue, newValue) -> realizarBusca(newValue));

		// TELA ESTOQUE
		carregarDados();
		definirDadosDaComboBox();
		manipuladorDeCommit();

		// TELA HISTORICO
		iniciarComboBoxDias();
		iniciarComboBoxMeses();
		iniciarComboBoxAnos();
		
		// SALVAR AUTOMATICO
		salvamentoAutomatico();
		
	}
	
	// CARREGA A TABELA COM AS INFORMAÇÕES DO BANCO E DEFINE AS ULTIMAS 3 COLUNAS
	// COMO EDITAVEIS
	private void carregarDados() {
		try {
			colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
			colNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
			colEmEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
			colEnchimentos.setCellValueFactory(new PropertyValueFactory<>("enchimentos"));
			colCortes.setCellValueFactory(new PropertyValueFactory<>("cortes"));
		
		
			colEmEstoque.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
			colEnchimentos.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
			colCortes.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
			
			ListaProdutos.clear();
			query = "SELECT * FROM produtos";
			resultSet = queryDB.selectQuery(query);

			while (resultSet.next()) {
				ListaProdutos.add(new Produto(resultSet.getString("codigoProduto"), resultSet.getString("nomeProduto"),
						resultSet.getInt("estoque"), resultSet.getInt("enchimentos"), resultSet.getInt("cortes"),
						resultSet.getString("observacoes")));
				tabelaProduto.setItems(ListaProdutos);
			}
			
			//Carregar a lista de produtos apenas com os nomes
			for(Produto produtos : ListaProdutos) {
				ListaProdutosString.add(produtos.getNomeProduto());
			}
		} catch (NumberFormatException e) {
			Alerts.showAlert("TIPO INCOMPATÍVEL", null, "ERRO: O CONTEÚDO DA TABELA DEVE SER UM NÚMERO INTEIRO!!\nTIPO DE ERRO: "+e.getMessage(), AlertType.ERROR);
		}
		catch (SQLException er) {
			Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + er.getMessage(), AlertType.ERROR);
		}
	}

	// CARREGA A LISTA COM TODOS OS PRODUTOS QUE VIER DO BANCO
	private void atualizarTabela() {
		tabelaProduto.setItems(ListaProdutos);
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
			Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
		} catch (Exception e) {
			Alerts.showAlert("ERRO!!", null, "ERRO DESCONHECIDO, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
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
			salvamentoAutomatico();
			tabelaProduto.setItems(ListaProdutos);
		} catch (SQLException e) {
			Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
		} catch (Exception e) {
			Alerts.showAlert("ERRO!!", null, "ERRO DESCONHECIDO, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
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
				Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
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
			String textoObservacoes = observacoes.getText();

			query = "UPDATE produtos SET observacoes = '" + textoObservacoes + "' WHERE codigoProduto = '"
					+ codigoProduto + "'";
			listaAlteracoes.add(query);	
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
	private <T> void manipuladorDeCommitPorColuna(TableColumn<Produto, T> coluna) {
		coluna.setOnEditCommit(new EventHandler<CellEditEvent<Produto, T>>() {
			@Override
			public void handle(CellEditEvent<Produto, T> event) {

				Produto item = event.getRowValue();
				// Obtendo os valores de todas as colunas na linha afetada
				String codigoProduto = item.getCodigo();
				int valorEmEstoque = colEmEstoque.getCellData(item);
				int valorEnchimentos = colEnchimentos.getCellData(item);
				int valorCortes = colCortes.getCellData(item);
				
				int posicao = 0;
				for(int i = 0; i< ListaProdutos.size(); i++) {
					
					if(ListaProdutos.get(i).getCodigo() == codigoProduto) {
						posicao = i;
					}
				}
				
				T novoValor = event.getNewValue();
				if (novoValor instanceof Integer) {
					// Descobrindo qual coluna foi alterada
					if (coluna == colEmEstoque) {
						valorEmEstoque = (Integer) novoValor;
						
						ListaProdutos.get(posicao).setEstoque(valorEmEstoque);
						
						query = "UPDATE produtos SET estoque = " + valorEmEstoque + " WHERE codigoProduto = '" + codigoProduto
								+ "'";
						listaAlteracoes.add(query);

					} else if (coluna == colEnchimentos) {
						valorEnchimentos = (Integer) novoValor;
						
						ListaProdutos.get(posicao).setEnchimentos(valorEnchimentos);;
						
						query = "UPDATE produtos SET enchimentos = " + valorEnchimentos + " WHERE codigoProduto = '" + codigoProduto + "'";
						listaAlteracoes.add(query);

					} else if (coluna == colCortes) {
						valorCortes = (Integer) novoValor;
						
						ListaProdutos.get(posicao).setCortes(valorCortes);;
						
						query = "UPDATE produtos SET cortes = " + valorCortes + " WHERE codigoProduto = '" + codigoProduto + "'";						
						listaAlteracoes.add(query);
					}
				}
				
			}
		});
		atualizarTabela();
	}

	// ENVIA UM ALERTA PERGUNTANDO SE REALMENTE DESEJA EFETUAR AS ALTERAÇÕES
	@FXML
	private void atualizarEstoque() {
		if(Alerts.showConfirmationAlert("SALVAR DIARIO",
				"DESEJA SALVAR TODOS OS ITENS NA DATA DE HOJE " + dtf.format(LocalDate.now()) + "?") == 2) {
			salvamentoAutomatico();
		}
	}

	/*
	 * 
	 * PÁGINA DE DIÁRIO
	 * 
	 */

	Historico historico = new Historico();
	HistoricoDAO historicoRepo = new HistoricoDAO();

	
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	@FXML
	private TextField produtoDiario;
	@FXML
    private ListView<String> resultadoListView;
	@FXML
	private TextField quantidadeProdutoDiario;
	@FXML
	private TableView<ProdutoDiario> tabelaDiario;
	@FXML
	private TableColumn<ProdutoDiario, String> colNomeProdutoDiario;
	@FXML
	private TableColumn<ProdutoDiario, String> colQuantidadeDiario;
	@FXML
	private Button botaoInserirDiario;
	@FXML
	private Button botaoSalvar;
	@FXML
	private Button botaoDescartar;
	@FXML
	private Button botaoLimpar;

	ObservableList<ProdutoDiario> ListaDiarioTotal = FXCollections.observableArrayList();
	ObservableList<ProdutoDiario> ListaDiario = FXCollections.observableArrayList();
	List<DiarioManipulador> listaAlteracoesDiario = new ArrayList<>();
	
	
	private void definirTabelaDiario() {
		colNomeProdutoDiario.setCellValueFactory(new PropertyValueFactory<>("product"));
		colQuantidadeDiario.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		try {
			if(ListaDiarioTotal.isEmpty()) {
				LocalDate data = LocalDate.now();
				String query = "SELECT * FROM historico WHERE dataAlteracao = '"+dtf.format(data).toString()+"'";
				resultSet = historicoRepo.selectQuery(query);
				
				while(resultSet.next()) {
					ListaDiarioTotal.add(new ProdutoDiario(resultSet.getString("nomeProduto"), resultSet.getString("quantidade"), dtf.format(data).toString()));
				}
			}
		}
		catch(SQLException e) {			
			Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
		}
		
		atualizarTabelaDiario();
	}
	
	@FXML
	private void iniciarBusca() {
		limparTudo();
		
	    resultadoListView.setItems(FXCollections.observableArrayList());
	    resultadoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selecionarItem(newValue));
	    
	    // Atualizar a ListView quando o texto na TextField muda
	    produtoDiario.textProperty().addListener((observable, oldValue, newValue) -> realizarBusca(newValue));
	}

	private void realizarBusca(String filtro) {
	    ObservableList<String> resultadosFiltrados = ListaProdutosString.filtered(
	            produto -> produto.toLowerCase().contains(filtro.toLowerCase()));

	    // Atualizar a ListView com os resultados filtrados
	    resultadoListView.setItems(resultadosFiltrados);
	    resultadoListView.prefHeightProperty().bind(
	            Bindings.min(
	                    Bindings.size(resultadoListView.getItems()).multiply(tamanhoMedioCadaCelula),
	                    100.0
	            )
	    );
	    // Exibir ou ocultar a ListView conforme necessário
	    resultadoListView.setVisible(!resultadosFiltrados.isEmpty());
	}

	private void selecionarItem(String itemSelecionado) {
	    // Verificar se o item selecionado não é nulo antes de definir o texto na TextField
	    if (itemSelecionado != null) {
	        // Atualizar a TextField com o item selecionado
	        produtoDiario.setText(itemSelecionado);

	        // Ocultar a ListView
	        resultadoListView.setVisible(false);
	    }
	}

	@FXML
	private void limparTudo() {
	    produtoDiario.setText("");
	    quantidadeProdutoDiario.setText("");
	    resultadoListView.setItems(FXCollections.observableArrayList());
	    resultadoListView.setVisible(false);// Se quantidadeProdutoDiario é um campo de texto, também limpe-o
	}
	
	private void atualizarTabelaDiario() {
		colNomeProdutoDiario.setCellValueFactory(new PropertyValueFactory<>("product"));
		colQuantidadeDiario.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		ListaDiario.clear();
		ListaDiario.addAll(ListaDiarioTotal);

		tabelaDiario.setItems(ListaDiario);
		}

	@FXML
	private void eventoInserirDiario() {
		if (produtoDiario.getText().isEmpty() || quantidadeProdutoDiario.getText().isEmpty()) {
			Alerts.showAlert("CAMPO VAZIO", null, "CERTIFIQUE DE PREENCHER TODOS OS CAMPOS!", AlertType.INFORMATION);			
		} else {			
			Integer posicao = null;
			boolean has = false;
			for (int j = 0; j < ListaDiarioTotal.size(); j++) {
				if (ListaDiarioTotal.get(j).getProduct().equals(produtoDiario.getText().toUpperCase())) {
					has = true;
					posicao = j;
				}
			}
			if (has == false) {
				LocalDate data = LocalDate.now();
				ListaDiarioTotal.add(
						new ProdutoDiario(produtoDiario.getText().toUpperCase(), quantidadeProdutoDiario.getText(), dtf.format(data).toString()));					
				
				int dia = LocalDate.now().getDayOfMonth();
				String mes = LocalDate.now().getMonth().toString();
				mes = historicoRepo.traduct(mes);
				int ano = LocalDate.now().getYear();

				listaAlteracoesDiario.add(new DiarioManipulador(new ProdutoDiario(produtoDiario.getText().toUpperCase(), quantidadeProdutoDiario.getText(), dtf.format(data).toString()),dia, mes, ano));
				
			} else {
				ListaDiarioTotal.get(posicao).setQuantity(quantidadeProdutoDiario.getText());

				LocalDate data = LocalDate.now();
				String query = "UPDATE historico SET quantidade = "+ ListaDiarioTotal.get(posicao).getQuantity() +" WHERE dataAlteracao = '"+dtf.format(data).toString()+
						"' AND nomeProduto = '"+ListaDiarioTotal.get(posicao).getProduct()+"'";
				
				listaAlteracoes.add(query);				
			}
			
		}
		limparTudo();
		atualizarTabelaDiario();
	}

	@FXML
	private void botaoDiarioPressionado() {
		if (!tabelaDiario.getItems().isEmpty()) {
			if(Alerts.showConfirmationAlert("SALVAR DIARIO",
					"DESEJA SALVAR TODOS OS ITENS NA DATA DE HOJE " + dtf.format(LocalDate.now()) + "?") == 2) {
				salvamentoAutomatico();
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
				boolean achou = false;
				for (int i = ListaDiarioTotal.size() - 1; i >= 0; i--) {
					if(ListaDiarioTotal.get(i).getProduct().equals(produtoDiario.getText())) {
						String nomeProduto = ListaDiarioTotal.get(i).getProduct();
						String dataAlteracao =ListaDiarioTotal.get(i).getDataAlteracao();
						
						query = "delete from historico Where nomeProduto = '"+ nomeProduto +"' AND dataAlteracao = '"+ dataAlteracao +"'";
						ListaDiarioTotal.remove(i);
						listaAlteracoes.add(query);
						achou = true;
						break;
					}				
											
					if(achou == false) {
						String nomeProduto = tabelaDiario.getSelectionModel().getSelectedItem().getProduct();
						LocalDate data = LocalDate.now();
						String dataAlteracao = dtf.format(data).toString();
						query = "delete from historico Where nomeProduto = '"+ nomeProduto +"' AND dataAlteracao = '"+ dataAlteracao +"'";
						listaAlteracoes.add(query);
					}
				}
			}
			produtoDiario.setText("");
			resultadoListView.setVisible(false);
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
	private ComboBox<Integer> Dias;
	@FXML
	private ComboBox<String> Meses;
	@FXML
	private ComboBox<Integer> Anos;
	@FXML
	private Button botaoBuscar;
	@FXML
	private Button botaoPdf;
	@FXML
	private TableView<ProdutoDiario> TabelaHistorico;
	@FXML
	private TableColumn<ProdutoDiario, String> colDiaHistorico;
	@FXML
	private TableColumn<ProdutoDiario, String> colProdutoHistorico;
	@FXML
	private TableColumn<ProdutoDiario, Integer> colquantidadeProdutoHistorico;

	ProdutoDiario productDiario;
	ObservableList<Integer> TodosDias = FXCollections.observableArrayList();
	ObservableList<String> TodosMeses = FXCollections.observableArrayList();
	ObservableList<Integer> TodosAnos = FXCollections.observableArrayList();
	ObservableList<ProdutoDiario> listaHistorico = FXCollections.observableArrayList();
	String PdfDMA = null;
	
	public void iniciarComboBoxDias() {
		TodosDias.add(null);
		for(int i = 1; i <= 31; i ++) {
			TodosDias.add(i);
		}
		Dias.setItems(TodosDias);
	}
	
	public void iniciarComboBoxMeses() {
		TodosMeses.add(null);
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
			Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	private void carregarDadosHistorico() {
		if(Dias.getSelectionModel().getSelectedItem() != null && Meses.getSelectionModel().getSelectedItem() != null && Anos.getSelectionModel().getSelectedItem() != null) {
			query = "select dataAlteracao, nomeProduto, quantidade from historico where dia = "+ Dias.getSelectionModel().getSelectedItem() +
					" AND mes = '" + Meses.getSelectionModel().getSelectedItem() + "' AND ano = " + Anos.getSelectionModel().getSelectedItem();
			
			try {
				resultSet = historicoRepo.selectQuery(query);
				
				colDiaHistorico.setCellValueFactory(new PropertyValueFactory<>("dataAlteracao"));
				colProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("product"));
				colquantidadeProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("quantity"));
				
				listaHistorico.clear();
				while (resultSet.next()) {
					String data = resultSet.getString("dataAlteracao");
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

							productDiario = new ProdutoDiario(nomeProduto, quantidadeAtualizada, data);
							listaHistorico.set(i, productDiario);
							produtoEncontrado = true;
							break;
						}
					}

					if (!produtoEncontrado) {
						productDiario = new ProdutoDiario(nomeProduto, quantidadeFormat, data);
						listaHistorico.add(productDiario);
					}
				}
				PdfDMA = "Diário " + Dias.getSelectionModel().getSelectedItem() + " de " + Meses.getSelectionModel().getSelectedItem();
				
				TabelaHistorico.setItems(listaHistorico);
			} catch (SQLException e) {
				Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
			}
		}
		else if (Meses.getSelectionModel().getSelectedItem() != null && Anos.getSelectionModel().getSelectedItem() != null) {
			query = "SELECT nomeProduto, quantidade, dataAlteracao FROM historico WHERE mes = '" + Meses.getSelectionModel().getSelectedItem().toUpperCase()
					+ "' AND ano = " + Anos.getSelectionModel().getSelectedItem();
			try {
				resultSet = historicoRepo.selectQuery(query);

				colDiaHistorico.setCellValueFactory(new PropertyValueFactory<>("dataAlteracao"));
				colProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("product"));
				colquantidadeProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("quantity"));

				listaHistorico.clear();
				while (resultSet.next()) {
					String data = resultSet.getString("dataAlteracao");
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

							productDiario = new ProdutoDiario(nomeProduto, quantidadeAtualizada, data);
							listaHistorico.set(i, productDiario);
							produtoEncontrado = true;
							break;
						}
					}

					if (!produtoEncontrado) {
						productDiario = new ProdutoDiario(nomeProduto, quantidadeFormat, data);
						listaHistorico.add(productDiario);
					}
				}
				PdfDMA = "Mensal " + Meses.getSelectionModel().getSelectedItem();
				TabelaHistorico.setItems(listaHistorico);
			} catch (SQLException e) {
				Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
			}
		}
		else if (Anos.getSelectionModel().getSelectedItem() != null){
			query = "SELECT nomeProduto, quantidade, dataAlteracao FROM historico WHERE ano = " + Anos.getSelectionModel().getSelectedItem();
			try {
				resultSet = historicoRepo.selectQuery(query);

				colDiaHistorico.setCellValueFactory(new PropertyValueFactory<>("dataAlteracao"));
				colProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("product"));
				colquantidadeProdutoHistorico.setCellValueFactory(new PropertyValueFactory<>("quantity"));

				listaHistorico.clear();
				while (resultSet.next()) {
					String data = resultSet.getString("dataAlteracao");
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

							productDiario = new ProdutoDiario(nomeProduto, quantidadeAtualizada, data);
							listaHistorico.set(i, productDiario);
							produtoEncontrado = true;
							break;
						}
					}

					if (!produtoEncontrado) {
						productDiario = new ProdutoDiario(nomeProduto, quantidadeFormat, data);
						listaHistorico.add(productDiario);
					}
				}
				PdfDMA = "Anual " + Anos.getSelectionModel().getSelectedItem();
				TabelaHistorico.setItems(listaHistorico);
			} catch (SQLException e) {
				Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
			}
		} else {
			Alerts.showAlert("CAMPO VAZIO", null,
					"CERTIFIQUE-SE DE PREENCHER TODOS OS CAMPOS ANTES DE EXECUTAR A BUSCA", AlertType.INFORMATION);
		}
	}
	
	@FXML
	public  void criarPDF() {
		if(!listaHistorico.isEmpty()) {
			final String DESTINO = "C:\\Users\\caios.CAIO\\Downloads\\relatorio "+ PdfDMA +".pdf"; 
			try {
				PdfDocument pdf = new PdfDocument(new PdfWriter(DESTINO));
		        Document doc = new Document(pdf);

		        Paragraph titulo = new Paragraph("Relatório " + PdfDMA)
		                .setFontSize(18)
		                .setBold()
		                .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER);

		        doc.add(titulo);
		        doc.add(new Paragraph(""));

		        Table tabela = new Table(3).useAllAvailableWidth().setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER);
		        
		        tabela.addCell(new Cell().add(new Paragraph("Data de Alteração").setBold()));
		        tabela.addCell(new Cell().add(new Paragraph("Nome do Produto").setBold()));
		        tabela.addCell(new Cell().add(new Paragraph("Quantidade").setBold()));

		        for (ProdutoDiario produto : listaHistorico) {
		            tabela.addCell(new Cell().add(new Paragraph(produto.getDataAlteracao())));
		            tabela.addCell(new Cell().add(new Paragraph(produto.getProduct())));
		            tabela.addCell(new Cell().add(new Paragraph(produto.getQuantity())));
		        }

		        doc.add(tabela);

		        doc.close();
		        
		        Alerts.showAlert("CONFIRMAÇÃO", null, "PDF GERADO COM SUCESSO, SEU ARQUIVO PDF SE ENCONTRA NA PASTA: "+DESTINO, AlertType.CONFIRMATION);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		}
		
		else {
			Alerts.showAlert("CAMPO VAZIO", null,
					"CERTIFIQUE-SE DE EFETUAR UMA BUSCA PARA PODER GERAR O PDF DOS RESULTADOS", AlertType.INFORMATION);
		}
	}
	
	/*
	 * EVENTO DE FECHAMENTO DO APP
	 */
	
	public void salvamentoAutomatico(){
		Timer timer = new Timer();
	    TimerTask tarefa = new TimerTask() {
	        @Override
	        public void run() {
	        	Platform.runLater(() -> {
		        	if(!listaAlteracoes.isEmpty()) {
		        		listaAlteracoes.forEach(que -> {
							try {
								ProductDAO productRepository = new ProductDAO();
								productRepository.updateQuery(que);
														
							} catch (SQLException e) {
								Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
							} 
						});	
		        		listaAlteracoes.clear();
		        	}
		        	if(!listaAlteracoesDiario.isEmpty()) {
						for(DiarioManipulador d : listaAlteracoesDiario) {
							try {
								historicoRepo.insertQueryProdutoDiario(d);
							} catch (SQLException e) {
								Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
							}
						}
						listaAlteracoesDiario.clear();
		        	}
	        	});
	        }
	    };
	    
	    timer.scheduleAtFixedRate(tarefa, 0, 180000);
	}
	
}
