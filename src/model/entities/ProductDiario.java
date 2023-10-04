package model.entities;

public class ProductDiario {
	private String product;
	private String quantity;	
	
	public ProductDiario(String product) {		
	}
	
	public ProductDiario(String product, String quantity) {
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
	
	
}
