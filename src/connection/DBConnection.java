package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	public static Connection Conexao() {		
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/tcc_fofuchos";
		String user = "admin";
		String password = "P@nda100";
		
		try{
			conn = DriverManager.getConnection(url, user, password);
		}
		catch(SQLException erro) {
			System.out.println(erro);
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		return conn;
	}
}
