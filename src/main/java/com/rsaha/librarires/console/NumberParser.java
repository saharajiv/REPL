package com.rsaha.librarires.console;

public abstract class NumberParser implements Parser{
	
	public Number parse(String trimmedInput) {
		if(trimmedInput.equals(""))
			return 0;
		else return this.parseNumber(trimmedInput);
	}

	
}
