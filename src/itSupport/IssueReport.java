package itSupport;

import java.io.Serializable;

public class IssueReport implements Serializable {
	private String technician;
	private String customer;
	private String details;
	private int priority;
	private boolean solved;
	
	IssueReport(){
		this.technician = "N/A";
		this.customer = "N/A";
		this.details = "N/A";
		this.priority = 0;
		this.solved = false;
	}
	
	IssueReport(String a, String b, String c, int d){
		this.technician = a;
		this.customer = b;
		this.details = c;
		this.priority = d;
		this.solved = false;
	}

	

	public String getTechnician() {
		return technician;
	}

	public void setTechnician(String technician) {
		this.technician = technician;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}


}
