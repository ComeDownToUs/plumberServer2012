package itSupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	
	BufferedWriter writer;
	BufferedReader reader;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		/**										SOME NOTES
		 * ===============================================================================================
		 * ==	If an option appears to have not done anything, try adjust the the size of the window, 
		 * 		not sure why that solves it, but it does quite often
		 * ==	pretty buggy, tried throwing some extra things on today and it's basically wrecked 
		 * 		the whole structure. sorry.
		 * ==	Covers basic functionality of displaying the records in a table
		 * ==	Also assigns specialist technicians to deal with certain problem types
		 * ==	Contains code to return a message that the report has been recorded to the database, 
		 * 		but some stream related error makes it extra volatile so I have commented it out
		 * ==	EOFException seemed to occur an awful lot today in particular, some sort of explanation 
		 * 		as to what was happening there would be greatly appreaciated
		 * 
		 * 					Thank you for taking the time to read these notes, 
		 * 					now sit back and prepare to enjoy the assignment!
		 */
		
		
		ClientLayout clay = new ClientLayout();
		
	}

}
