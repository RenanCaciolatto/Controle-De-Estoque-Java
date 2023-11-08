package model.entities;

public class ProdutoDiario {
	private String product;
	private String quantity;
	private String dataAlteracao;
	
	public ProdutoDiario(String product) {		
	}
	
	public ProdutoDiario(String product, String quantity, String dataAlteracao) {
		this.product = product;
		this.quantity = quantity;
		this.dataAlteracao = dataAlteracao;
	}
	
	public String getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(String dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	public void sumQuantity(ProdutoDiario produto, int quantidade) {
		produto.setQuantity(produto.getQuantity() + quantidade);
	}
	
}
