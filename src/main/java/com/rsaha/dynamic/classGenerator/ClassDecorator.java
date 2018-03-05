package com.rsaha.dynamic.classGenerator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.rsaha.parser.ClassParser;

import static com.rsaha.util.UtilityClass.checkForKeywordSplitWithNewLine;

public class ClassDecorator extends Decorator{
	
	String [] methodNames = new String [100];
	String modifiedSource = null;
	int lengthOfPseudoCodeMethod ;
	int lastPositionOfSemicolon;
	
	public int decorate(String defaultPath, String defaultClassName,
			 StringBuilder source) throws Exception {
		int posOfSemicolon = 0;
		String sourcePartToBeChecked = null;
		removeAnyCommentedLineOrBlankLinesInTheBeginning(source);
		int lengthOfFirstLine = getTheFirstLine(source);
		if(source.length()<lengthOfFirstLine){
			sourcePartToBeChecked = source.toString();
		}else{
			sourcePartToBeChecked = source.substring(0,lengthOfFirstLine);
		}
		if(checkForKeywordSplitWithNewLine(sourcePartToBeChecked,IMPORT_KEYWORD,true)){
			int posOfImport = source.toString().lastIndexOf(IMPORT_KEYWORD);
			posOfSemicolon = source.toString().indexOf(";",posOfImport+1);
			insertDefaultImport(source,posOfSemicolon);
			posOfImport = source.toString().lastIndexOf(IMPORT_KEYWORD);
			//posOfSemicolon = source.toString().indexOf(";",posOfImport+1);
		}else if(checkForKeywordSplitWithNewLine(sourcePartToBeChecked,PACKAGE_KEYWORD,false)){
			posOfSemicolon = decorateWithDefaultImportWhenNoUserImports(source);
		}else{
			posOfSemicolon = decorateWithDefaultImportWhenNoUserImports(source);
		}
		source.insert(posOfSemicolon+1+totalLengthOfDefaultImport,"public class " + defaultClassName + " {\n");
		source.insert(posOfSemicolon+totalLengthOfDefaultImport+17+defaultClassName.length(),createPseudoCode());
		source.append("}\n");
		lastPositionOfSemicolon = posOfSemicolon+17+totalLengthOfDefaultImport+defaultClassName.length()+lengthOfPseudoCodeMethod;
		//System.out.println(source);
		return lastPositionOfSemicolon;
	}

    
	   


		private void removeAnyCommentedLineOrBlankLinesInTheBeginning(StringBuilder source) {
	    	String newSource = new String(source);
	    	boolean blockCommentStarted = false;
	    	int count = 0;
			for(String line : newSource.split("\n")){
				if(line.startsWith("//") || line.startsWith("/*") || line.startsWith("*/")){
					count = count+line.length();
				}
				if(line.startsWith("/*")){
					count = count+line.length();
					blockCommentStarted = true;
				}
				if(blockCommentStarted){
					count = count+line.length();
				}
				if(line.contains("*/")){
					count = count+ line.indexOf("*/");
					blockCommentStarted= false;
				}
				if(line.trim().length()==0){
					count = count+line.length();
				}
	    	}
			source.delete(0,count+1);
			//newSource = newSource.substring(count+1);
			
			//return count;
			//return new StringBuilder(newSource);					
		}
		
		private int getTheFirstLine(StringBuilder source) {
			String stLine = new String(source);
			String[] lines = stLine.split("\n");
			for(String line:lines){
				if(line.length()>0){
					return line.length();
				}
			}
			return 0;
		}	


		public String createPseudoCode(){
	    	final StringBuilder pseudoCode = new StringBuilder();
	    	pseudoCode.append("\n");
	    	pseudoCode.append("\t private void pseduoCode(){\n");
	    	pseudoCode.append("\t\t int a = (Integer)new ConsoleReader(\"int\").read();\n");
	    	pseudoCode.append("\t } \n");
	    	lengthOfPseudoCodeMethod = pseudoCode.length();
	    	return pseudoCode.toString();
	    }
	    
	    public String getModifiedSource(){
	    	return modifiedSource;
	    }
	
	    public String [] getMethodNames(){
	    	return methodNames;
	    }
		
	
	
	
	

	

}
