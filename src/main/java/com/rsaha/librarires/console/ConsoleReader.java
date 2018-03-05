package com.rsaha.librarires.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleReader<T extends Number> {
	private String dataType ;
	private static Scanner scanner = new Scanner(System.in);
	
	public ConsoleReader(String dataType){
		this.dataType = dataType;
	}
	
    public static void main(String[] args) {
    	//new ConsoleReader("int").read();
    }

	public Number read() {
        String input = scanner.nextLine();
        String trimmedInput = input.trim();
        if(dataType.equals("String")){
        	return 0;
        }else{
	        Parser parser = ParserFactory.getParser(dataType);
        	//Parser parser = ParserFactory.getParser(dataType);
	        return parser.parse(trimmedInput);
        }
	}
	
	public void finalize(){
		 scanner.close();
	}
	
	private boolean parseShort(String trimmedInput) {
		boolean parsed = true;
		try{
			short i = Short.parseShort(trimmedInput);
		}catch(NumberFormatException ne){
			parsed = false;	
		}
		return parsed;
	}

}






