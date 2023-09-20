package model.entities;

public class Produto extends Colecao{
	private String codigo;
	private String nomeProduto;
	private Integer estoque;
	private Integer enchimentos;
	private Integer cortes;
	
	public Produto(String codigoColecao, String nome, String codigo, String nomeProduto, Integer estoque, Integer enchimentos,
			Integer cortes) {
		super(codigoColecao, nome);
		this.codigo = codigo;
		this.nomeProduto = nomeProduto;
		this.estoque = estoque;
		this.enchimentos = enchimentos;
		this.cortes = cortes;
	}

	public Integer getEstoque() {
		return estoque;
	}

	public void setEstoque(Integer estoque) {
		this.estoque = estoque;
	}

	public Integer getEnchimentos() {
		return enchimentos;
	}

	public void setEnchimentos(Integer enchimentos) {
		this.enchimentos = enchimentos;
	}

	public Integer getCortes() {
		return cortes;
	}

	public void setCortes(Integer cortes) {
		this.cortes = cortes;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getNomeProduto() {
		return nomeProduto;
	}
	
	
}
