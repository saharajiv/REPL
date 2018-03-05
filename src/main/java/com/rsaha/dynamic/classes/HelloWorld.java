public class HelloWorld{
	
	public static void hello(){
		System.out.println("Welcome to Java-Scripting... Scripting in Java");
		for(int i = 0;i<5;i++){
			System.out.println(i);
		}
		System.out.print("Enter a number: ");
		int a = read();
		System.out.println("The no. entered is "+a);
	}
}

