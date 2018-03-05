package com.rsaha.librarires.console;

public class ByteParser extends NumberParser {
	
	public Byte parseNumber(String input) {
		byte i = 0;
		try{
			 i = Byte.parseByte(input);
		}catch(NumberFormatException ne){
			throw new RuntimeException(ERROR_MESSAGE+ne.getMessage());
		}
		return i;
	}

	
	

}
