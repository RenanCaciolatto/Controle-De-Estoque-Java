package model.repositores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductRepository implements Repositorio{
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	String query = null;
	
	public ProductRepository(){
	}
	
}
