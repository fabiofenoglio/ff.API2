import test0.FileSystemProvider;
import test0.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import ff.API2.ApiHook;
import ff.API2.ApiProvider;
import ff.API2.ApiRequest;
import ff.API2.ApiResponse;
import ff.API2.ApiUtils;

public class main
{
	/*
	 * API_KEY must be a valid registered API key.
	 * The following key is a public, limited access one
	 * you can user just for testing
	 */
	public static final String API_KEY = "6015c941571fdde37fec1f485568a7ff";
	
	public static enum LogLevel {INFO, WARNING, ERROR, INPUT_REQUEST, DEBUG};
	
	public static Config permanentConfiguration;
	public static ApiProvider apiProvider;
	public static HashMap<String, Object> userDetails;
	
	/*
	 * Load permanent configuration
	 */
	public static void loadConfig() throws Exception {
		String configFilePath = FileSystemProvider.getInstance().getConfigFilePath();
		permanentConfiguration = Config.loadOrNew(configFilePath);		
		w("Loading configuration from " + configFilePath + " ...", LogLevel.DEBUG);
	}
	
	/*
	 * Init API provider with auth data from the permanent configuration
	 */
	public static void loadApiProvider() {
		if (apiProvider == null) {
			apiProvider = new ApiProvider(API_KEY);
			
			// please, PLEASE use SSL
			apiProvider.configuration.useSSL = true;
			
			// attach an hook to log requests
			apiProvider.registerHook("beforeRequest", new ApiHook() {
				@Override
				public void hook(HashMap<String, Object> data) {
					w("sending api request to " + (String)data.get("url") + " ...", LogLevel.DEBUG);
				}
			});
		}
		
		apiProvider.configuration.username = permanentConfiguration.username;
		apiProvider.configuration.password = permanentConfiguration.password;
	}
	
	public static void main(String []args) throws Exception {
		// load permanent configuration data
		loadConfig();
		
		// instantiate api provider
		loadApiProvider();
		
		// check auth data. If invalid, ask the user for new data
		while (!checkAuthData()) {
			askUserData();
			permanentConfiguration.save();
		}
		
		// sample usage for the data. userDetails gets filled in checkAuthData()
		w("");
		
		w("Welcome " + 
				(String)userDetails.get("name") + " !");
		
		w("your registration email is " + 
				(String)userDetails.get("email"));
		
		w("bandwidth used: " + 
				userDetails.get("bandwidth") + " B / " + 
				userDetails.get("max_bandwidth") + " B");
    }
	
	/*
	 * Return true if current auth data is valid
	 */
	public static boolean checkAuthData() throws Exception {
		// I may just not have the data at all
		if (apiProvider.configuration.username.isEmpty() || 
				apiProvider.configuration.password.isEmpty()) {
			return false;
		}
		
		// Launch an API request to the user.info command point to check auth data
		ApiRequest request = apiProvider.createRequest();
		request.command = "user.info";
		
		ApiResponse response;
		try {
			// everything could go wrong here
			response = request.execute();
		}
		catch (Exception e) {
			w("Auth data check failed: " + e.getMessage(), LogLevel.WARNING);
			return false;
		}
		
		// import user detail fields
		userDetails = new HashMap<String, Object>();
		
		if (response.success) {
			userDetails.put("name", (String)response.data.get("name"));
			userDetails.put("email", (String)response.data.get("email"));
			userDetails.put("bandwidth", response.data.get("bandwidth"));
			userDetails.put("data_size", response.data.get("data_size"));
			userDetails.put("max_data_size", response.data.get("max_data_size"));
			userDetails.put("max_bandwidth", response.data.get("max_bandwidth"));
			return true;
		}
		else {
			// request failed
			w("Auth data check failed: " + ApiUtils.getCodeDescription(response.code), LogLevel.WARNING);
			return false;
		}
	}
	
	/*
	 * Ask the user for auth data input
	 */
	public static void askUserData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        w("Please provide valid login credentials for " + 
        		apiProvider.configuration.endPoint, LogLevel.INPUT_REQUEST);
        
        w("Username : ", LogLevel.INPUT_REQUEST);
        permanentConfiguration.username = br.readLine();
        
        w("Password : ", LogLevel.INPUT_REQUEST);
        permanentConfiguration.password = br.readLine();
        
        // Reload new settings in API provider
        loadApiProvider();
        
        // be kind
        w("Thank you. I will now check the data.", LogLevel.INPUT_REQUEST);
	}
	
	/*
	 * Wrapper for println just in case I'll decide to filter the output
	 */
	public static void w(String text, LogLevel level) {
		System.out.println(text);
	}
	
	public static void w(String text) {
		w(text, LogLevel.INFO);
	}
}
