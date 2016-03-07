package ff.API2;

import java.util.HashMap;

public class ApiUtils {

	private static HashMap<Long, String> codeDescriptions = null;
    
	/*
	 * Returns a textual description for the given response code
	 */
	public static String getCodeDescription(Long code) {
		if (codeDescriptions == null) {
			initializeCodeDescriptionsHashMap();
		}
		if (codeDescriptions.containsKey(code)) {
			return codeDescriptions.get(code);
		}
		return "unknown error (#"+code.toString()+")";
	}
	
	protected static void initializeCodeDescriptionsHashMap() {
		codeDescriptions = new HashMap<Long, String>();
		codeDescriptions.put((long)0, "unknown error");
		codeDescriptions.put((long)1, "success");
		codeDescriptions.put((long)100, "generic error");
		codeDescriptions.put((long)101, "unknownresource unavailable");
		codeDescriptions.put((long)102, "authentication failed");
		codeDescriptions.put((long)103, "invalid app key");
		codeDescriptions.put((long)104, "disabled app key");
		codeDescriptions.put((long)105, "access denied");
		codeDescriptions.put((long)106, "invalid command");
		codeDescriptions.put((long)107, "quota limit exceeded");
		codeDescriptions.put((long)200, "API service unavailable");
		codeDescriptions.put((long)500, "internal error");
	}
}
