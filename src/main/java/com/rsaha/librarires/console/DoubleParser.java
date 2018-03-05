package com.rsaha.librarires.console;

public class DoubleParser extends NumberParser{
	
	public Double parseNumber(String input) {
		double i = 0;
		try{
			 i = Double.parseDouble(input);
		}catch(NumberFormatException ne){
			throw new RuntimeException(ERROR_MESSAGE+ne.getMessage());
		}
		return i;
	}
}