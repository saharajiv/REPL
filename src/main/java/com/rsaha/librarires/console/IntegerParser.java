package com.rsaha.librarires.console;

public class IntegerParser extends NumberParser{
	
	public Integer parseNumber(String input) {
		int i = 0;
		try{
			 i = Integer.parseInt(input);
		}catch(NumberFormatException ne){
			throw new RuntimeException(ERROR_MESSAGE+ne.getMessage());
		}
		return i;
	}
}