package ff.API2;

public class ApiConfiguration {
	/*
	 * Auth data
	 */
	public String username = "";
	public String password = "";
	
	public String apiKey = "";
	
	public String endPoint = "https://api.fabiofenoglio.it";
	
	/*
	 * useSSL will add https protocol only if endPoint does not already
	 * specify one
	 */
	public boolean useSSL = true;
}
