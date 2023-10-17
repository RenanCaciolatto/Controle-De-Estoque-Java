package model.entities;

public class Colecao {
	
	private String codigoColecao;
	private String nome;
	
	public Colecao(String codigoColecao, String nome) {
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
