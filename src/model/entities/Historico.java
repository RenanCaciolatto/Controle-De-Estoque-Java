package model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class Historico implements Serializable{
	private static final long serialVersionUID = 1L;
	
	List <ProdutoDiario> lista = new ArrayList<>();
	String date;
	int dia;
	String month;
	int year;
	
	public Historico() {
	}
	
	public Historico(List<ProdutoDiario> lista, String date,int dia, String month, int year) {
		this.lista = lista;
		this.date = date;
		this.dia = dia;
		this.month = month;
		this.year = year;
	}

	public Historico(ObservableList<ProdutoDiario> lista, String date) {
		this.lista = lista;
		this.date = date;
	}
	
	public List<ProdutoDiario> getLista() {
		return lista;
	}
	public String getData() {
		return date;
	}

	public String getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}
	
	
}
