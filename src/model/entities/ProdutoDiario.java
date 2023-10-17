package model.entities;

public class ProdutoDiario {
	private String product;
	private String quantity;	
	
	public ProdutoDiario(String product) {		
	}
	
	public ProdutoDiario(String product, String quantity) {
		this.product = product;
		this.quantity = quantity;
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
	
	@Override
	public String toString() {		
		return "Produto: "+product+"; Quantidade: "+quantity;
	}
}
