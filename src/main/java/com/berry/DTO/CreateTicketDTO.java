package com.berry.DTO;

public class CreateTicketDTO implements DTO{
	private double amount;
	private String description;
	private String type;
	
	public CreateTicketDTO() {}

	public CreateTicketDTO(double amount, String description, String type) {
		this.amount = amount;
		this.description = description;
		this.type = type;
	}

	@Override
	public boolean noFieldEmpty() {
		if (this.amount == 0.0) {
			return false;
		} else if (this.description == null || this.description.trim().equals("") == true) {
			return false;
		} else if (this.type == null || this.type.trim().equals("") == true) {
			return false;
		}
		
		return true;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}	
}
