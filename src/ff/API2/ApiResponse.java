package ff.API2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ApiResponse {
	public static final long SUCCESS_CODE = 1;
	
	/*
	 * True if the http request has been completed and a response
	 * has been received
	 */
	public boolean requestCompleted = false;
	
	/*
	 * True if (requestCompleted) AND the result code is 1 ('success')
	 */
	public boolean success = false;
	
	/*
	 * Code should be 1 for 'success'
	 */
	public Long code = (long)0;
	
	/*
	 * Textual comment
	 */
	public String message = "";
	
	/*
	 * Response data will be held as as JSONObject
	 */
	public boolean hasData = false;
	public JSONObject data = null;

	/*
	 * Here just for backward compatibility
	 */
	public String apiVersion = "";
	public String callId = "";
	
	/*
	 * Raw (json not unserialized) response data
	 */
	public String raw = null;
	
	public static ApiResponse fromString(String raw) throws Exception {
		ApiResponse r = new ApiResponse(raw);
		r.loadJson(raw);
		return r;
	}
	
	public ApiResponse() {
		// nope
	}
	
	public ApiResponse(String raw) {
		this.raw = raw;
	}
	
	/*
	 * Load data from a raw json-formatted response
	 */
	public void loadJson(String raw) throws Exception {
		JSONObject parsedJson = (JSONObject)new JSONParser().parse(raw);
		this.code = (Long)parsedJson.get("code");
		this.message = (String) parsedJson.get("message");
		this.apiVersion = (String) parsedJson.get("apiVersion");
		this.callId = (String) parsedJson.get("id");
		
		String jsonData = (String)parsedJson.get("data");
		if (jsonData.length() > 0) {
			this.data = (JSONObject)new JSONParser().parse((String) parsedJson.get("data"));
		}
		else {
			this.data = null;
		}
		
		// fill rapid check fields
		this.hasData = data != null;
		this.requestCompleted = true;
		this.success = this.code == ApiResponse.SUCCESS_CODE;
	}
}
