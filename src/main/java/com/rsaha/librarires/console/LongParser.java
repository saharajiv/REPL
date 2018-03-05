package com.rsaha.librarires.console;

public class LongParser extends NumberParser{
	
	public Long parseNumber(String input) {
		long i = 0;
		try{
			 i = Long.parseLong(input);
		}catch(NumberFormatException ne){
			throw new RuntimeException(ERROR_MESSAGE+ne.getMessage());
		}
		return i;
	}
}