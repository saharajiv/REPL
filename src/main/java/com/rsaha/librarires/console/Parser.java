package com.rsaha.librarires.console;

import java.util.HashMap;
import java.util.Map;

public interface Parser{
	public static final String ERROR_MESSAGE = "The value provided does not support the data type given \n";	
	
	Map<String,Parser> DataTypeParserMap = new HashMap();
	
	 Number parse(String trimmedInput) ; 
	 
	 Number parseNumber(String imput);
}
