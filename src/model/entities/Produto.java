package model.entities;

import java.io.Serializable;

public class Produto implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String codigo;
	private String nomeProduto;
	private Integer estoque;
	private Integer enchimentos;
	private Integer cortes;
	private String observacoes;
	
	
	public Produto() {
	}
	
	public Produto(String codigo, String nomeProduto, Integer estoque, Integer enchimentos, Integer cortes, String observacoes) {
		this.codigo = codigo;
		this.nomeProduto = nomeProduto;
		this.estoque = estoque;
		this.enchimentos = enchimentos;
		this.cortes = cortes;
		this.observacoes = observacoes;
	}
	
	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("codigo: "+codigo+"\n");
		sb.append("nome: "+nomeProduto+"\n");
		sb.append("estoque: "+estoque+"\n");
		sb.append("enchimentos: "+enchimentos+"\n");
		sb.append("cortes: " + cortes + "\n");
		sb.append("observacoes: " + observacoes + "\n");
		return sb.toString();
	}
	
}
