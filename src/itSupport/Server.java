package itSupport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException{
		//establishing port to connect through
		ServerSocket serve = new ServerSocket(12345);
		
		System.out.println("Waiting for clients.");
		
		//capable of multithreading
		while(true){
			
			Socket sock = serve.accept();
			
			System.out.println("A client has connected.");
			
			//all functions included in this method for the conveniece of the multithreading process
			//most of the activity happens in here
			Service service = new Service(sock);
			
			Thread thread = new Thread(service);
			
			thread.start();
		
		}
		
	}
}
