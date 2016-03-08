package ff.API2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ApiRequest {
	/*
	 * Parent provider will provide configuration
	 */
	ApiProvider provider;
	
	/*
	 * Command point (eg. 'user.info')
	 */
	public String command = "";
	
	/*
	 * Custom GET and POST data
	 */
	public HashMap<String, Object> get;
	public HashMap<String, Object> post;
	
	public ApiRequest(ApiProvider provider) {
		this.provider = provider;
		
		this.get = new HashMap<String, Object>();
		this.post = new HashMap<String, Object>();
		
		this.addPost("ja_u", this.provider.configuration.username);
		this.addPost("ja_p", this.provider.configuration.password);
	}

	/*
	 * Add a key-value pair to POST data
	 */
	public void addPost(String key, Object value) {
		this.post.put(key, value);
	}

	/*
	 * Add a key-value pair to GET data
	 */
	public void addGet(String key, Object value) {
		this.get.put(key, value);
	}
	
	/*
	 * Launch the request and return the response
	 */
	public ApiResponse execute() throws Exception {
		HttpURLConnection connection = null;
		try {
		    URL url = new URL(this.buildUri());
		    
		    connection = (HttpURLConnection)url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", 
		        "application/x-www-form-urlencoded");

		    String postData = this.getPostData();
		    if (postData.length() > 0) {
		    	connection.setRequestProperty(
	    			"Content-Length", 
			        Integer.toString(postData.getBytes().length)
		        );
		    }

		    connection.setUseCaches(false);
		    connection.setDoOutput(true);
		    
		    // hook point: beforeRequest
		    Integer hookKey = ApiHookPoints.BEFORE_REQUEST;
		    if (this.provider.hasHooks(hookKey)) {
		    	this.provider.processHook(hookKey, ApiHookDataBundle.create()
	    			.put("url", url.toString())
	    			.put("request", this)
	    			.put("connection", connection)
    			);
		    }

		    // Output stream : send request
		    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		    wr.writeBytes(postData);
		    wr.close();
	
		    // Input stream: receive response
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); 
		    String line;
		    while((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    
		    // hook point: afterRequest
		    hookKey = ApiHookPoints.AFTER_REQUEST;
		    if (this.provider.hasHooks(hookKey)) {
		    	this.provider.processHook(hookKey, ApiHookDataBundle.create()
	    			.put("url", url.toString())
	    			.put("request", this)
	    			.put("connection", connection)
	    			.put("response", response.toString())
    			);
		    }
		    
		    // Build ApiResponse from raw response string
		    return ApiResponse.fromString(response.toString());
		  } 
		catch (Exception e) {
			// hook point: requestException
			Integer hookKey = ApiHookPoints.REQUEST_EXCEPTION;
		    if (this.provider.hasHooks(hookKey)) {
		    	this.provider.processHook(hookKey, ApiHookDataBundle.create()
	    			.put("request", this)
	    			.put("connection", connection)
	    			.put("exception", e)
    			);
		    }
		    
			if(connection != null) {
				connection.disconnect();
				connection = null;
		    }
			throw e;
		} 
		finally {
			if(connection != null) {
				connection.disconnect();
				connection = null;
		    }
		}
	}
	
	/*
	 * Serialize POST data
	 */
	protected String getPostData() throws UnsupportedEncodingException {
		String uri = "";
		
		if (this.post.size() > 0) {
			for (String key: this.post.keySet()) {
				uri += encodeRaw(key) + "=" + encodeRaw(this.post.get(key)) + "&";
			}
			uri = uri.substring(0, uri.length() - 1);
		}
		
		return uri;
	}
	
	/*
	 * URLencode wrapper
	 */
	protected String encodeRaw(Object raw) throws UnsupportedEncodingException {
		return URLEncoder.encode(raw.toString(), "UTF-8");
	}
	
	/*
	 * Build full uri (protocol, endpoint, command and GET parameters)
	 */
	protected String buildUri() throws UnsupportedEncodingException {
		String uri = provider.configuration.endPoint;
		
		if (! uri.startsWith("http")) {
			uri = (provider.configuration.useSSL ? "https://" : "http://") + uri;
		}
		
		if (! uri.endsWith("/")) {
			uri = uri + "/";
		}
		
		uri += provider.configuration.apiKey + "/" + this.command;
		
		if (this.get.size() > 0) {
			uri += "?";
			for (String key: this.get.keySet()) {
				uri += encodeRaw(key) + "=" + encodeRaw(this.get.get(key)) + "&";
			}
			uri = uri.substring(0, uri.length() - 1);
		}
		
		if (uri.startsWith("http:")) {
			System.out.println("[" + this.getClass().getPackage().getName() + 
					" warning] you should really use SSL connection. Open http connections will soon be prohibited.");
		}
		
		return uri;
	}
}
