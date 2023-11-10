package gui;

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

	ObservableList<Produto> ListaProdutos = FXCollections.observableArrayList();
	ObservableList<String> ListaColecao = FXCollections.observableArrayList();

	List<String> listaAlteracoes = new ArrayList<>();
	
	@Override
	public void initialize(java.net.URL url, ResourceBundle rb) {
		// TELA DIÁRIO
		atualizarTabelaDiario();
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
	private int atualizarEstoque() {
		//if (true) {
			//Alerts.showConfirmationAlert("CONFIRMAR?", "DESEJA SALVAR TODAS AS ALTERAÇÕES FEITAS?");			
		//}
		return 0;
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
			
			try {
				if (has == false) {
					LocalDate data = LocalDate.now();
					ListaDiarioTotal.add(
							new ProdutoDiario(produtoDiario.getText().toUpperCase(), quantidadeProdutoDiario.getText(), dtf.format(data).toString()));					
					
					int dia = LocalDate.now().getDayOfYear();
					String mes = LocalDate.now().getMonth().toString();
					mes = historicoRepo.traduct(mes);
					int ano = LocalDate.now().getYear();
	
					historicoRepo.insertQueryProdutoDiario(new ProdutoDiario(produtoDiario.getText().toUpperCase(), quantidadeProdutoDiario.getText(), dtf.format(data).toString()),dia, mes, ano);
				} else {
					ListaDiarioTotal.get(posicao).setQuantity(quantidadeProdutoDiario.getText());
					
					LocalDate data = LocalDate.now();
					String query = "UPDATE historico SET quantidade = "+ ListaDiarioTotal.get(posicao).getQuantity() +" WHERE dataAlteracao = '"+dtf.format(data).toString()+
							"' AND nomeProduto = '"+ListaDiarioTotal.get(posicao).getProduct()+"'";
					
					listaAlteracoes.add(query);				
				}
			}
			catch(SQLException e) {
				Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
			}
		}
		atualizarTabelaDiario();
	}

	@FXML
	private void botaoDiarioPressionado() {
		if (!tabelaDiario.getItems().isEmpty()) {
			Alerts.showConfirmationAlert("SALVAR DIARIO",
					"DESEJA SALVAR TODOS OS ITENS NA DATA DE HOJE " + dtf.format(LocalDate.now()) + "?");
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
	private ComboBox<Integer> Dias;
	@FXML
	private ComboBox<String> Meses;
	@FXML
	private ComboBox<Integer> Anos;
	@FXML
	private Button botaoBuscar;
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

	public void iniciarComboBoxDias() {
		TodosDias.add(null);
		for(int i = 1; i <= 31; i ++) {
			TodosDias.add(i);
		}
		Dias.setItems(TodosDias);
	}
	
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
			Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	private void carregarDadosHistorico() {
		if(Dias.getSelectionModel().getSelectedItem() != null && !Meses.getSelectionModel().getSelectedItem().isEmpty() && Anos.getSelectionModel().getSelectedItem() != null) {
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
				TabelaHistorico.setItems(listaHistorico);
			} catch (SQLException e) {
				Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
			}
			
		}
		else if (!Meses.getSelectionModel().getSelectedItem().isEmpty() && Anos.getSelectionModel().getSelectedItem() != null) {
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
				TabelaHistorico.setItems(listaHistorico);
			} catch (SQLException e) {
				Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
			}
		} else {
			Alerts.showAlert("CAMPO VAZIO", null,
					"CERTIFIQUE-SE DE PREENCHER TODOS OS CAMPOS ANTES DE EXECUTAR A BUSCA", AlertType.INFORMATION);
		}
	}
	
	public void salvamentoAutomatico(){
		Timer timer = new Timer();
	    TimerTask tarefa = new TimerTask() {
	        @Override
	        public void run() {
	        	System.out.println("rodou");
	        	if(!listaAlteracoes.isEmpty()) {
	        		listaAlteracoes.forEach(que -> {
						try {
							ProductDAO productRepository = new ProductDAO();
							productRepository.updateQuery(que);
													
						} catch (SQLException e) {
							Alerts.showAlert("ERRO!!", null, "ERRO NO BANCO DE DADOS, TENTE NOVAMENTE!\nERRO: " + e.getMessage(), AlertType.ERROR);
						} 
					});	        		
	        	}
	        	
	        }
	    };
	    
	    timer.scheduleAtFixedRate(tarefa, 0, 180000);
	}
	
}
