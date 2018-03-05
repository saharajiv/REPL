package com.rsaha.dynamic.classes;

import java.util.ArrayList;
import java.util.List;


import com.rsaha.librarires.console.*;

class TesterClass{
	
	public void console(){
		int a = (Integer)new ConsoleReader("int").read();
		byte b = (Byte)new ConsoleReader("byte").read();
		System.out.println(a);
		System.out.println(b);
	}
	
}



public class TesterClass2 {
	List<String> l = new ArrayList<String>();
	
	public void loadValue(String element){
	 	l.add(element);
	}
}