package com.rsaha.dynamic.classGenerator;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import sun.misc.Unsafe;

/**
 * by Rajib Saha
 */
public class RunTimeReturnIntrospector {

	String [] methodNames = new String [100];
	String modifiedSource = null;
	public Map<String,String> modifiedSources= new LinkedHashMap();
	
    public void dynamicClassCreation() throws ClassNotFoundException, IllegalAccessException, InstantiationException, URISyntaxException, NoSuchFieldException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException {
    	boolean checkPackage = false;
    	boolean checkClass = false;
    	boolean checkMethod = false;
    	Class noparams[] = {};
    	String defaultPath = "com.repl";
    	String defaultClassName = "DefaultClass";
    	String className = null;
    	String path = null;
    	final StringBuilder source = new StringBuilder();
        String fullClassName = defaultPath.replace('.', '/') + "/" + defaultClassName;
    	String sCurrentLine;
    	BufferedReader br = new BufferedReader(new FileReader("src/main/resource/DynamicJavaFileForTest"));
    	while((sCurrentLine = br.readLine())!=null){
    		if(checkPackage == false || checkClass == false){
	    		if(sCurrentLine.startsWith("package")){
	    			checkPackage = true;
	    			path = sCurrentLine.substring(8,sCurrentLine.length()-1);
	    		}else if(sCurrentLine.contains("class")){
	    			checkClass = true;
	    			int classIndex = sCurrentLine.indexOf("class");
	    			className = sCurrentLine.substring(classIndex+6,sCurrentLine.length()-2);
	    		}
    		}
    		
    		source.append(sCurrentLine+"\n");
    	}
    	if(checkClass ==false){
    		source.insert(0,"package " + defaultPath + ";\n");
    		//source.append("package " + defaultPath + ";");
            source.insert(18,"public class " + defaultClassName + " {\n");
            source.append("}\n");
            //System.out.println(source);
            checkMethod = checkForMethod(source);
    	}else{
    		if(checkPackage)
    			fullClassName = path.replace('.', '/') + "/" + className;
    		else
    			fullClassName = defaultPath.replace('.', '/') + "/" + className;
    		//System.out.println(source);
    		checkMethod = checkForMethod(source);
    	}
    	if(!checkMethod){
    		//source.insert(27," public void defaultMethod() {\n");
    		//source.append("}\n");
    	}
    	br.close();
    	//System.out.println(modifiedSource);
    	// A byte array output stream containing the bytes that would be written to the .class file
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + ".java"), SOURCE) {

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

        final Object o = aClass.newInstance();
        Method method = null;
        if(!checkMethod){
        	 method = aClass.getDeclaredMethod("defaultMethod", noparams);
        }else if(checkMethod){
        	method = aClass.getDeclaredMethod(methodNames[0], noparams);
        }
        Object returnObject = method.invoke(o, null);
        //System.out.println(o);
       System.out.println(returnObject.getClass());

    }

    private boolean checkForMethod(StringBuilder source) {
    	String javaSource = source.toString();
    	
		CompilationUnit cu=null;
		try{
			cu = JavaParser.parse(javaSource);
		}catch(ParseProblemException parseException){
			//System.out.println(parseException);
			source.insert(45," public void defaultMethod() {\n");
    		source.append("}\n");
    		javaSource = source.toString();
		}
		if(cu==null){
			cu = JavaParser.parse(javaSource);
		}
        //new MethodVisitor().visit(cu, null);
		MethodVisitor mv = new MethodVisitor();
		mv.visit(cu, null);
		mv.methodNames.toArray(methodNames);
		//modifiedSource = mv.modifiedSource; 
		return mv.methodExists;
	}
    

    private static class MethodVisitor extends VoidVisitorAdapter{
        public boolean methodExists = false;
    	public List methodNames = new ArrayList<String>();
    	public List methodStatements = new ArrayList<String>();
    	public Map<String,String> modifiedSources= new LinkedHashMap();
    	public int noOfClasses ;
    	
    	@Override
    	public void visit(final CompilationUnit n, Object arg) {
    		super.visit(n, arg);
    		int methodCount = 0;
    		NodeList<TypeDeclaration<?>> nL = n.getTypes();
    		Iterator<TypeDeclaration<?>> it = nL.iterator();
    		noOfClasses = nL.size();
    		while(it.hasNext()){
    			ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration)it.next();
    			Iterator iterator = classDeclaration.getMembers().iterator();
    			if(noOfClasses>1){
    				EnumSet classModifiers = classDeclaration.getModifiers();
    				if(classModifiers.contains(Modifier.PUBLIC)){
    					System.out.println(classDeclaration.getName());
    					modifiedSources.put(classDeclaration.getName().asString(), classDeclaration.toString());
    				}
    			}
    			while(iterator.hasNext()){
    				Object withinClass = iterator.next();
    				if(withinClass instanceof MethodDeclaration){
    					MethodDeclaration methodDeclaration = (MethodDeclaration)withinClass;
    					if(!methodDeclaration.getName().toString().equals("defaultMethod"))
    						methodExists = true;
    					methodNames.add(methodDeclaration.getName().toString());
    				}else{
    					if(withinClass instanceof ConstructorDeclaration){
    						System.out.println("Not doign anything for now");
    					}
    				}
    			}
    			
    		}
    		//modifiedSource = n.toString();
    		
    	}
    	
    	@SuppressWarnings("unchecked")
		@Override
        public void visit(final MethodDeclaration n, final Object arg) {
    		//System.out.println(n.toString());
            List<Node> nodes = n.getBody().get().getChildNodes();
            for(Node node:nodes){
            	if(node.getChildNodes().get(0)!=null){
            		javaBlockExecutor(node);
            		if(node.getChildNodes().get(0).getChildNodes() == null || node.getChildNodes().get(0).getChildNodes().size()<1)
            				continue;
            		String expr = node.getChildNodes().get(0).getChildNodes().get(0).toString();
            		if(expr.equals("print") || expr.equals("println")){
            			//SimpleName name = ((MethodCallExpr)(node.getChildNodes().get(0))).getName();
            			List<Node> nodeOperated = node.getChildNodes().get(0).getChildNodes();
            			List<Node> clonedNode = new ArrayList<Node>();
            			clonedNode.addAll(nodeOperated);
            			node.getChildNodes().get(0).remove();
            			SimpleName alteredNode = (SimpleName)clonedNode.get(0);
            			alteredNode.setIdentifier("System.out."+alteredNode.getIdentifier());
            			//clonedNode.add(alteredNode);
            			((MethodCallExpr)node.getChildNodes().get(0)).setName(alteredNode);
            			((MethodCallExpr)node.getChildNodes().get(0)).setArguments(((MethodCallExpr)node.getChildNodes().get(0)).getArguments());
            			//node.getChildNodes().get(0).getChildNodes().addAll(clonedNode);
            			//node.getChildNodes().get(0).getChildNodes();
            			
            				
            		}
            	}
            }
            //System.out.println("Modified :\n "+n.toString());
    	}

		private void javaBlockExecutor(Node node) {
			if(node instanceof IfStmt){
				ifStmtBlcokValidation(node);
			}else if(node instanceof WhileStmt){
				ifStmtBlcokValidation(node);
			}else if (node instanceof ForeachStmt){
				ifStmtBlcokValidation(node);
			}else if(node instanceof Statement){
				return;
			}
		}

		private void ifStmtBlcokValidation(Node node) {
			Statement statement = ((IfStmt) node).getThenStmt();
			for(Node ifNode : statement.getChildNodes()){
				
			}
		}
    	
    	/*@Override
        public void visit(MethodCallExpr methodCall, Object arg)
        {
    		System.out.print("Method call: " + methodCall.getName() + "\n");
    		if(methodCall.equals("print")){
    			//methodCall.replace();
    			methodCall.getParentNode();
    		}
            List<Expression> args = methodCall.getArguments();
            if (args != null)
                handleExpressions(args);
            
        }
    	*/

        private void handleExpressions(List<Expression> expressions){
            for (Expression expr : expressions)
            {
                if (expr instanceof MethodCallExpr)
                    visit((MethodCallExpr) expr, null);
                else if (expr instanceof BinaryExpr)
                {
                    BinaryExpr binExpr = (BinaryExpr)expr;
                    handleExpressions(Arrays.asList(binExpr.getLeft(), binExpr.getRight()));
                }
            }
        }
    }

    

	public static final void main(String... args) throws ClassNotFoundException, URISyntaxException, NoSuchFieldException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException {
        new RunTimeReturnIntrospector().dynamicClassCreation();
    }

}