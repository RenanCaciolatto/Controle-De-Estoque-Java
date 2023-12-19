package model.entities;

public class Usuarios {
	private String login;
	private String senha;
	
	public Usuarios(String login, String senha) {
		this.login = login;
		this.senha = senha;
	}

	public String getLogin() {
		return login;
	}

	public String getSenha() {
		return senha;
	}	
	
}
