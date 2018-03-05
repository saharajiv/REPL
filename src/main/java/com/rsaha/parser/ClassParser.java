package com.rsaha.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.rsaha.dynamic.classGenerator.MethodVisitor;

public class ClassParser {
	String [] methodNames = new String [100];
	private String modifiedSource = null;
	private int lastPositionOfSemicolon ;
	
	

	public boolean parse(StringBuilder source) {
	    	String javaSource = source.toString();
	    	System.out.println(javaSource);
			CompilationUnit cu=null;
			try{
				cu = JavaParser.parse(javaSource);
			}catch(ParseProblemException parseException){
				System.out.println(parseException);
				decorateWithDefaultMethod(source);
	    		javaSource = source.toString();
	    		System.out.println("from exception block:JavaParser -");
	    		System.out.println(javaSource);
			}
			if(cu==null){
				cu = JavaParser.parse(javaSource);
			}
	        //new MethodVisitor().visit(cu, null);
			MethodVisitor mv = new MethodVisitor();
			mv.visit(cu, null);
			mv.methodNames.toArray(methodNames);
			modifiedSource = mv.modifiedSource; 
			return mv.methodExists;
	}

	private void decorateWithDefaultMethod(StringBuilder source) {
		source.insert(lastPositionOfSemicolon," public void defaultMethod() {\n");
		source.append("}\n");
	}
	 
	public int getLastPositionOfSemicolon() {
			return lastPositionOfSemicolon;
	}

	public void setLastPositionOfSemicolon(int lastPositionOfSemicolon) {
		this.lastPositionOfSemicolon = lastPositionOfSemicolon;
	}
	
	public String getModifiedSource() {
		return modifiedSource;
	}
	
	public String[] getMethodNames(){
		return methodNames;
	}

	 
	
		
	
}
