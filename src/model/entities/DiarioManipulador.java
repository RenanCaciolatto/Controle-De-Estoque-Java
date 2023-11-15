package model.entities;

public class DiarioManipulador {
	ProdutoDiario produtoDiario;
	int dia;
	String mes;
	int ano;
	
	public DiarioManipulador(ProdutoDiario produtoDiario, int dia, String mes, int ano) {
		super();
		this.produtoDiario = produtoDiario;
		this.dia = dia;
		this.mes = mes;
		this.ano = ano;
	}

	public ProdutoDiario getProdutoDiario() {
		return produtoDiario;
	}

	public int getDia() {
		return dia;
	}

	public String getMes() {
		return mes;
	}

	public int getAno() {
		return ano;
	}
	
	
}
