package itSupport;

public class Technician {

	/* basic technician class contains 
	 * a name, a password, and a list of keywords which represent that technicians specialty
	 * because the system seems to be collapsing in on itself, I've only kept two technicians in the technicians table
	 * these represent the two senior figures of the company.
	 */
	private String name;
	private String password;
	//keywords separated by whitespace
	private String keywords;
	
	Technician(){
		this.name="N/A";
		this.password="1234";
		this.keywords="";
	}
	
	Technician(String a, String b, String c){
		this.name="";
		this.password="";
		this.keywords="";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	
}
