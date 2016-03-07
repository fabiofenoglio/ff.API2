package ff.API2;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiProvider {
	/*
	 * ApiProvider's configuration will be accessed by [ApiRequest]s
	 */
	public ApiConfiguration configuration;
	
	/*
	 * Keyed list of hook callbacks
	 */
	protected HashMap<String, ArrayList<ApiHook>> hooks;
	
	public ApiProvider(String apiKey) {
		this.hooks = new HashMap<String, ArrayList<ApiHook>>();
		
		this.configuration = new ApiConfiguration();
		this.configuration.apiKey = apiKey;
	}
	
	/*
	 * Create an ApiRequest with default parameters
	 */
	public ApiRequest createRequest() {
		ApiRequest r = new ApiRequest(this);
		return r;
	}
	
	/*
	 * Get a list of hook callbacks by key
	 */
	public ArrayList<ApiHook> getHooks(String key) {
		return this.hooks.get(key);
	}
	
	/*
	 * Returns true if at least on callback is registered
	 * for the given key
	 */
	public boolean hasHooks(String key) {
		return this.hooks.containsKey(key);
	}
	
	/*
	 * Add a callback with the given key
	 */
	public void registerHook(String key, ApiHook hook) {
		if (!this.hooks.containsKey(key)) {
			this.hooks.put(key, new ArrayList<ApiHook>());
		}
		this.hooks.get(key).add(hook);
	}
}
