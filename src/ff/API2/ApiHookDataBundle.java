package ff.API2;

import java.util.HashMap;

public class ApiHookDataBundle extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	
	public static ApiHookDataBundle create() {
		return new ApiHookDataBundle();
	}
	
	@Override
	public ApiHookDataBundle put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
