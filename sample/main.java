import test0.FileSystemProvider;
import test0.Output;
import test0.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import ff.API2.ApiHook;
import ff.API2.ApiHookDataBundle;
import ff.API2.ApiHookPoints;
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
	
	public static Config permanentConfiguration;
	public static ApiProvider apiProvider;
	public static HashMap<String, Object> userDetails;
	
	/*
	 * Load permanent configuration
	 */
	public static void loadConfig() throws Exception {
		String configFilePath = FileSystemProvider.getInstance().getConfigFilePath();
		permanentConfiguration = Config.loadOrNew(configFilePath);		
		Output.write("Loading configuration from " + configFilePath + " ...", Output.LogLevel.DEBUG);
	}
	
	/*
	 * Init API provider with auth data from the permanent configuration
	 */
	public static void loadApiProvider() {
		apiProvider = new ApiProvider(API_KEY);
		
		apiProvider.configuration.username = permanentConfiguration.username;
		apiProvider.configuration.password = permanentConfiguration.password;
		
		/* please, PLEASE use SSL
		 * this will be forced soon
		 */
		apiProvider.configuration.useSSL = true;
		
		// attach an hook to log requests
		apiProvider.registerHook(ApiHookPoints.BEFORE_REQUEST, new ApiHook() {
			@Override
			public void hook(ApiHookDataBundle data) {
				Output.write("[log] sending api request to " + (String)data.get("url") + " ...", Output.LogLevel.DEBUG);
			}
		});
		apiProvider.registerHook(ApiHookPoints.REQUEST_EXCEPTION, new ApiHook() {
			@Override
			public void hook(ApiHookDataBundle data) {
				Output.write("[log] error sending api request: " + ((Exception)data.get("exception")).getMessage(), Output.LogLevel.DEBUG);
			}
		});
	}
	
	public static void main(String []args) throws Exception {
		// load permanent configuration data
		loadConfig();
		
		// instantiate API provider
		loadApiProvider();
		
		// check authentication data. If invalid, ask the user for new data
		while (!checkAuthData()) {
			askUserData();
			permanentConfiguration.save();
		}
		
		// sample usage for the data. userDetails gets filled in checkAuthData()
		Output.write("");
		
		Output.write("Welcome " + 
				(String)userDetails.get("name") + " !");
		
		Output.write("your registration email is " + 
				(String)userDetails.get("email"));
		
		Output.write("bandwidth used: " + 
				userDetails.get("bandwidth") + " B / " + 
				userDetails.get("max_bandwidth") + " B");
    }
	
	/*
	 * Return true if current authentication data is valid
	 */
	public static boolean checkAuthData() throws Exception {
		// I may just not have the data at all
		if (apiProvider.configuration.username.isEmpty() || 
				apiProvider.configuration.password.isEmpty()) {
			return false;
		}
		
		// Launch an API request to the user.info command point to check authentication data
		ApiRequest request = apiProvider.createRequest();
		request.command = "user.info";
		
		ApiResponse response;
		try {
			// everything could go wrong here
			response = request.execute();
		}
		catch (Exception e) {
			Output.write("Auth data check failed: " + e.getMessage(), Output.LogLevel.WARNING);
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
			Output.write("Auth data check failed: " + ApiUtils.getCodeDescription(response.code), Output.LogLevel.WARNING);
			return false;
		}
	}
	
	/*
	 * Ask the user for authentication data input
	 */
	public static void askUserData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        Output.write("Please provide valid login credentials for " + 
        		apiProvider.configuration.endPoint, Output.LogLevel.INPUT_REQUEST);
        
        Output.write("Username: ", Output.LogLevel.INPUT_REQUEST);
        permanentConfiguration.username = br.readLine();
        
        Output.write("Password: ", Output.LogLevel.INPUT_REQUEST);
        permanentConfiguration.password = br.readLine();
        
        // Reload new settings in API provider
        apiProvider.setCredentials(permanentConfiguration.username, permanentConfiguration.password);
        
        // be kind
        Output.write("Thank you. I will now check the data.", Output.LogLevel.INPUT_REQUEST);
	}
}
