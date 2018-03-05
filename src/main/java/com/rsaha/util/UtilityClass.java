package com.rsaha.util;

public class UtilityClass {
	
	public static boolean checkForKeywordSplitWithNewLine(String sCurrentLine, String keyword,boolean scrapCommentTags) {
		//StringBuilder sb = new StringBuilder(sCurrentLine);
		for(String line : sCurrentLine.split("\n")){
			String modifiedLine = line;
			if(scrapCommentTags)
				 modifiedLine = scrapCommentTags(line);
			if(checkForKeyword(modifiedLine,keyword))				
				return true;
		}
		return false;
	}
	
	
	private static String scrapCommentTags(String line) {
		if(line.length()>2){
			if(line.substring(0, 2).equals("//") || line.substring(0, 2).equals("/*") || line.substring(0, 2).equals("*/"))
				return line.substring(2);
		}
		return line;	
	}


	public static boolean checkForKeyword(String sCurrentLine,String keyword) {
		//String [] keywords = sCurrentLine.split(" ");
		StringBuilder sb = new StringBuilder(sCurrentLine);
		for(String word : sCurrentLine.split(" ")){
			if(word.equals(keyword))
				return true;
		}
		return false;
	}

}
