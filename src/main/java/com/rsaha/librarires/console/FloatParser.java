package com.rsaha.librarires.console;

public class FloatParser extends NumberParser {

	public Number parseNumber(String input) {
		float i = 0;
		try{
			 i = Float.parseFloat(input);
		}catch(NumberFormatException ne){
			throw new RuntimeException(ERROR_MESSAGE+ne.getMessage());
		}
		return i;
	}

	

}
