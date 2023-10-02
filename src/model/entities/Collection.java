package model.entities;

public class Collection {
	
	private String codigoColecao;
	private String nome;
	
	public Collection(String codigoColecao, String nome) {
		this.codigoColecao = codigoColecao;
		this.nome = nome;
	}

	public String getCodigoColecao() {
		return codigoColecao;
	}

	public String getNome() {
		return nome;
	}
	
}
