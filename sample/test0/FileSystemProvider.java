package test0;

import java.io.File;

public class FileSystemProvider {
	public static final String PATH_APP_NAME = "ff.API.demo";
	
	protected String basePath = null;
	
	/*
	 * Singleton!
	 */
	public static FileSystemProvider instance = null;
	public static FileSystemProvider getInstance() {
		if (instance == null) {
			instance = new FileSystemProvider();			
		}
		return instance;
	}
	
	/*
	 * Ensure a directory is existing
	 * create it if needed
	 */
	protected static boolean checkAndCreate(String path) {
		File varDir = new File(path);

		if (varDir.exists()) {
			return true;
		}
		
		boolean successful = varDir.mkdir();
		return successful;
	}
	
	/*
	 * Initialize with basePath (defaults on desktop for test purposes)
	 */
	public FileSystemProvider() {
		this.basePath = System.getProperty("user.home") + "\\Desktop\\" + FileSystemProvider.PATH_APP_NAME;
		checkAndCreate(this.basePath);
	}
	
	/*
	 * Get base folder for program data
	 */
	public String getBasePath() {
		return this.basePath;
	}
	
	/*
	 * Get config file path
	 */
	public String getConfigFilePath() {
		return this.getBasePath() + "\\config.xml";
	}
}
