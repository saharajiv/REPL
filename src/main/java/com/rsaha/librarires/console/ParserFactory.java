package com.rsaha.librarires.console;

import java.util.HashMap;
import java.util.Map;

public class ParserFactory<T extends Number>{
	String[] dataTypes = {"int","float","double","long","byte"};
	Parser [] parserObjects = {new IntegerParser(),new FloatParser(),new DoubleParser(),new LongParser(),new ByteParser()};
	private static Map <String,Parser> dataTypeParserMap = null;
	private static ParserFactory parserFactory = new ParserFactory();
	
	private ParserFactory() {
		initializeParserMap();
	}
	
	private void initializeParserMap() {
		dataTypeParserMap = new HashMap<String, Parser>();
		for(int i = 0;i<dataTypes.length;i++){
			dataTypeParserMap.put(dataTypes[i], parserObjects[i]);
		}
	}

	public static ParserFactory getParserFactory(){
		synchronized (ParserFactory.class) {
			return parserFactory;	
		}
	}
	
	public static  Parser getParser(String dataType){
		return dataTypeParserMap.get(dataType);
	}
}


