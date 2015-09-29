package itSupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Service implements Runnable {

	//declaring variables
	private Socket sock;
	//file locations for serialising
	private File server = new File("./toServer.ser");
	private File client = new File("./toClient.ser");
	//bufferedwriter and reader for efficient reading of external data from CSV tables
	private BufferedWriter writer;
	private BufferedReader reader;
	//scanner and printwriter for sending and receiving basic string commands
	private Scanner in;
	private PrintWriter out;
	private InputStream instream;
	private OutputStream outstream;
	private ArrayList<IssueReport> list; 
	//boolean for checking where the the writing a report to the database method has processed
	private static boolean recorded;//for the sending notification of a report being successfully submitted
	
	//constructor
	Service(Socket s){
		this.sock = s;
	}
	
	//declaring streams and basic communication for clients to send instructions
	public void run(){
			try {
				try{
					//declaring streams
					instream = sock.getInputStream();
					outstream = sock.getOutputStream();
					//basic console communication between client and server
					in = new Scanner(instream);
					out = new PrintWriter(outstream);
					processReq();
				} finally{
					sock.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	//process the client's instruction
	public void processReq(){
		String req = "";
		//loop keeps checking for new input from client side
		//client side either sends a request from the Send method to record a new report
		//...or from the Receive method to get all current reports for the table
		while(req != "QUIT"){
			if(in.hasNext()){
				req = in.nextLine();
				doReq(req);
			}
		}
	}
	
	//further processing of instruction, now being more specific
	public void doReq(String req){
		System.out.println(req);//just keeping track of what's being instructed
		if(req.equals("SUBMIT")){
			FileInputStream fis;
			try {
				try{
					//reads from client 
					recorded = false;
					String message = "";
					fis = new FileInputStream(client);
					ObjectInputStream ois = new ObjectInputStream(fis);
					//reads the serialized object
					Object b = ois.readUnshared();
					ois.close();
					//converts output to IssueReport format
					IssueReport a = (IssueReport) b;
					
					//method to check if a more skilled technician specialises in this field
					//read the technician class summary for further information
					//also see CheckTech method below
					a=CheckTech(a);
					//calls method to write content of report to the database
					writeDB(a);
					
					//removing this bit for submission because it frequent freezes the system,
					//not sure why, was working up until minutes ago
					/*
					if(recorded==true){
						message = a.getCustomer() + "'s report has been recorded";}
					else{
						message = "the record failed to process on the server";
					}
					//sending message of recording status
					PrintWriter release = new PrintWriter(sock.getOutputStream());
					release.flush();
					release.write(message);
					release.flush();
					release.close();
					*/
				} catch(EOFException e2){
					e2.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(req.equals("VIEW")){
			FileOutputStream fos;
			try {
				//gathers the database into a list which is sent to the client
				//converts list to an object and streams it to the client, who processes it for fit a table.
				list = readDB();
				fos = new FileOutputStream(server);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeUnshared(list);
				oos.flush();
				oos.close();
				System.out.println("VIEW Success!");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//checks the specialty technicians for whether the issue relates to one of their fields
	//if it does, the issue is automatically passed onto them
	//if not, the issue is kept with the recorder.
	public static IssueReport CheckTech(IssueReport a){
		try {
			BufferedReader readTech = new BufferedReader(new FileReader("./technicians.csv"));
			
			String line = readTech.readLine();
			while(line!=null){
				String[] lineP = line.split(",");
				String[] skills = lineP[2].split(" ");
				String compare = a.getDetails();
				for(String p:skills){
					System.out.print(p);
					if(p.equals(compare)){
						a.setTechnician(lineP[0]);
						System.out.print(lineP[0]);
						break;
					}
				}
				line=readTech.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}
		
	//writes a report to the database
	public void writeDB(IssueReport a){
		try {
			writer = new BufferedWriter(new FileWriter("./database.csv", true));
			writer.write(a.getTechnician() + "," + a.getCustomer() + "," 
						+a.getDetails() + "," + a.getPriority() + "," + "0");
			writer.newLine();
			writer.flush();
			recorded = true;
			

			/*out.write("Issue for "+a.getCustomer()+" has been registered.");
			out.flush();
			out.close()*/
			System.out.println("Success! " + a.getTechnician() + "," + a.getCustomer() + "," 
					+a.getDetails() + "," + a.getPriority() + "," + "0");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<IssueReport> readDB(){
		ArrayList<IssueReport> outputList = new ArrayList<IssueReport>();
		try {
			//reading from the database and returning a list to be sent over to the client
			reader = new BufferedReader(new FileReader("./database.csv"));
			String q = reader.readLine();
			while(q != null){
				System.out.println(q);
				String[] entry = q.split(",");
				//parsing the priority entry to an int for the constructor
				int ent = Integer.parseInt(entry[3]);
				IssueReport issue = new IssueReport(entry[0], entry[1], entry[2], ent);
				outputList.add(issue);
				q=reader.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputList;
	}
}
