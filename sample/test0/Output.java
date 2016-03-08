package test0;

public class Output {
	
	public static enum LogLevel {INFO, WARNING, ERROR, INPUT_REQUEST, DEBUG};
	
	public static void write(String text, LogLevel level) {
		System.out.println(text);
	}
	
	public static void write(String text) {
		write(text, LogLevel.INFO);
	}
}
