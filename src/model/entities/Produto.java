package model.entities;

public class Produto{
	private String codigo;
	private String nomeProduto;
	private Integer estoque;
	private Integer enchimentos;
	private Integer cortes;
	
	public Produto(String codigo, String nomeProduto, Integer estoque, Integer enchimentos, Integer cortes) {
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
