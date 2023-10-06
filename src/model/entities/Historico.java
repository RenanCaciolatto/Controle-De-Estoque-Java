package model.entities;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class Historico{
	List <ProductDiario> lista = new ArrayList<>();
	String date;
	String month;
	int year;
	
	public Historico() {
	}
	
	public Historico(List<ProductDiario> lista, String date, String month, int year) {
		this.lista = lista;
		this.date = date;
		this.month = month;
		this.year = year;
	}

	public Historico(ObservableList<ProductDiario> lista, String date) {
		this.lista = lista;
		this.date = date;
	}
	
	public List<ProductDiario> getLista() {
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
