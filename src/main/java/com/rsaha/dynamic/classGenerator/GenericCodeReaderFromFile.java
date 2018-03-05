package com.rsaha.dynamic.classGenerator;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.rsaha.parser.ClassParser;

import sun.misc.Unsafe;

import static com.rsaha.util.UtilityClass.checkForKeyword;
//import static com.rsaha.util.UtilityClass.checkForKeywordSplitWithNewLine;

/**
 * by Rajib Saha
 */
public class GenericCodeReaderFromFile extends Decorator{
	private static final String JAVA_FILE_EXTENSION = ".java";
	private static final String ENTRY_CLASS = "entry-class";
	private static final String SRC = "src";
	private static final String CLASS_KEYWORD = "class";
	private ClassDecorator classDecorator = new ClassDecorator();
	private ClassParser parser = new ClassParser();
	String defaultPath = "";
	String defaultClassName = "DefaultClass";
	String fullClassName = defaultPath.replace('.', '/') + "/" + defaultClassName;
	String className = null;
	String path = null;
	boolean packageDefined = false;
	boolean classDefined = false;
	boolean methodDefined = false;
	private static String filename = "config.properties";
	private static String srcPath;
	final StringBuilder source = new StringBuilder();
	/*private String entryClassOfApp;*/
	private static String fullPathOfEntryclass;
	private static Set<String> fullClassNames;
	
    public Class dynamicClassCreation(String fileName) throws Exception {
    	/*String sCurrentLine;
    	loadConfigFile();
    	File [] listOfFiles = listFilesRecursively(srcPath);
    	final StringBuilder source = new StringBuilder();
    	BufferedReader br = new BufferedReader(new  FileReader(fullPathOfEntryclass));
    	readSourceFile(source, br);
    	br.close();
    	*/
    	int posOfLastSlash = fileName.indexOf("/");
    	if(posOfLastSlash>0){
    		defaultPath = fileName.substring(0,posOfLastSlash);
    	}
    	decorateWithDefaultPackage(classDefined,packageDefined, defaultPath, source);
    	if(!classDefined){
    		fileName = fileName.trim();
    		if(fileName.contains(JAVA_FILE_EXTENSION)){
    			fileName = fileName.substring(0,fileName.length()-JAVA_FILE_EXTENSION.length());
    		}
    		fullClassName = fileName;
    		defaultClassName = fileName;
    		int lastPositionOfSemicolon= classDecorator.decorate(defaultPath, defaultClassName, source);
    		parser.setLastPositionOfSemicolon(lastPositionOfSemicolon);
    		methodDefined = parser.parse(source);
    	}else{
    		setFullClassName(packageDefined, defaultPath, className, path);
    		decorateWithDefaultImport(source);
    		methodDefined = parser.parse(source);
    	}
    	/*if(!methodDefined){
    		source.insert(27," public void defaultMethod() {\n");
    	}*/
    	
    	String modifiedSource = parser.getModifiedSource();
    	System.out.println(modifiedSource);
    	// A byte array output stream containing the bytes that would be written to the .class file
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + JAVA_FILE_EXTENSION), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return modifiedSource;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null)) {

            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className,
                                                       JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return simpleJavaFileObject;
            }
        };

        ToolProvider.getSystemJavaCompiler().getTask(
                null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();

        final byte[] bytes = byteArrayOutputStream.toByteArray();

        // use the unsafe class to load in the class bytes
        final Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        final Unsafe unsafe = (Unsafe) f.get(null);
        final Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes.length,this.getClass().getClassLoader(),this.getClass().getProtectionDomain());
        return aClass;
        //executeClass(aClass);
    }



	private void executeClass(final Class aClass)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class noparams[] = {};
		final Object o = aClass.newInstance();
        Method method = null;
        String [] methodNames = parser.getMethodNames();
        if(!methodDefined){
        	 method = aClass.getDeclaredMethod("defaultMethod", noparams);
        }else if(methodDefined){
        	method = aClass.getDeclaredMethod(methodNames[1], noparams);
        }
        Object returned = method.invoke(o, null);
	}



	private static void loadConfigFile() {
		Properties prop = new Properties();
    	InputStream input = null;
    	try {
    		input = GenericCodeReaderFromFile.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            System.out.println("Sorry, unable to load the config file " + filename);
    		    return;
    		}
    		prop.load(input);
    		srcPath = prop.getProperty(SRC);
    		String entryClassOfApp= prop.getProperty(ENTRY_CLASS);
    		fullPathOfEntryclass = srcPath+"/"+entryClassOfApp;
    	    //System.out.println(srcPath);
	        //System.out.println(fullPathOfEntryclass);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
        			input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
	}

	



	private void readSourceFile(BufferedReader br) throws IOException {
		String sCurrentLine;
		while((sCurrentLine = br.readLine())!=null){
			sCurrentLine = sCurrentLine.trim();
    		if(packageDefined == false || classDefined == false){
	    		if(sCurrentLine.trim().startsWith(PACKAGE_KEYWORD)){
	    			packageDefined = true;
	    			path = sCurrentLine.substring("package".length()+1,sCurrentLine.length()-1);
	    		}else if(checkForKeyword(sCurrentLine,CLASS_KEYWORD)){
	    			classDefined = true;
	    			int classIndex = sCurrentLine.indexOf(CLASS_KEYWORD);
	    			className = sCurrentLine.substring(classIndex+6,sCurrentLine.length()-1);
	    			sCurrentLine = sCurrentLine+classDecorator.createPseudoCode();
	    		}
    		}
    		
    		source.append(sCurrentLine.trim()+"\n");
    	}
	}

	

	private void setFullClassName(boolean packageDefined, String defaultPath, String className, String path) {
		if(packageDefined)
			fullClassName = path.replace('.', '/') + "/" + className;
		else
			fullClassName = defaultPath.replace('.', '/') + "/" + className;
		//return fullClassName;
	}

	private static void handleException(){
		
	}
	
	private static File[] listFilesRecursively(String path) throws IOException{
		List allFiles = new ArrayList();
        File folder = new File(path);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()){
                //System.out.println(path+"/"+file.getName());
                allFiles.add(file);
            }
            else if (file.isDirectory()){
            	listFilesRecursively(file.getPath());
            }
        }
        return (File[])allFiles.toArray(new File[allFiles.size()]);
    }
	
	
	/*
		private File[] listFiles() {
			File folder = new File(srcPath);
			File[] listOfFiles = folder.listFiles();
			    for (int i = 0; i < listOfFiles.length; i++) {
			      if (listOfFiles[i].isFile()) {
			        System.out.println("File :" + srcPath+listOfFiles[i].getName());
			      } else if (listOfFiles[i].isDirectory()) {
			        System.out.println("Directory " + listOfFiles[i].getName());
			      }
			    }
			return listOfFiles;
		}
	*/
	
	public static final void main(String... args) throws Exception {
       try{
		Map<String,GenericCodeReaderFromFile> javaCodeReaderFiles = createjavaCodeReaderFiles();
		Class [] classes = new Class[javaCodeReaderFiles.size()];
		int sourceFileNumber = 0;
		for(Map.Entry<String,GenericCodeReaderFromFile> genericCodeReaderFromFile:javaCodeReaderFiles.entrySet()){
			classes[sourceFileNumber] = genericCodeReaderFromFile.getValue().dynamicClassCreation(genericCodeReaderFromFile.getKey());
			sourceFileNumber++;
		}
		if(javaCodeReaderFiles.size()>0)
			javaCodeReaderFiles.entrySet().iterator().next().getValue().executeClass(classes[0]);//get(0).executeClass(classes[0]);
		else{
			System.out.println("No java files present in the "+srcPath+" location to execute");
		}
       }catch(IllegalAccessException iae){
    	   throw new RuntimeException("The class in the file is not accessible. Please define the Entry level class and method as public");
       }
	}
	
	private static Map<String,GenericCodeReaderFromFile> createjavaCodeReaderFiles() throws IOException{
		loadConfigFile();
    	File [] listOfFiles = listFilesRecursively(srcPath);
    	int lengthOfSrcPath=srcPath.length(); 
    	String fullyQualifiedClassName = null;
    	Map<String,GenericCodeReaderFromFile> codeReaderFiles = new HashMap<String,GenericCodeReaderFromFile>();
    	for(File file : listOfFiles){
	    	BufferedReader br = new BufferedReader(new  FileReader(file));
	    	GenericCodeReaderFromFile genericReaderFile = new GenericCodeReaderFromFile();
	    	
	    	genericReaderFile.readSourceFile(br);
	    	fullyQualifiedClassName = file.getPath().substring(lengthOfSrcPath+1);
	    	codeReaderFiles.put(fullyQualifiedClassName,genericReaderFile) ;
    	}
    	return codeReaderFiles;
	}
	
	private static String getClassNames(){
		return null;
	}

}