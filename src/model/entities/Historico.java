package model.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.ObservableList;

public class Historico{
	List <ProductDiario> lista = new ArrayList<>();
	Date date;
	
	public Historico(ObservableList<ProductDiario> lista, Date date) {
		this.lista = lista;
		this.date = date;
	}
	
	public List<ProductDiario> getLista() {
		return lista;
	}
	public Date getData() {
		return date;
	}
	
	
}
