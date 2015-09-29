package itSupport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/*
 * Issues:
 * -When you select an option from the menu, the screen stays blank until you adjust the size of it somewhat
 * -The recording new issue reports field seems to only accept the values of the first entry
 * -The readObject() for reading the list of issue reports hardly ever reads anything at all
 * -weird layout issues with the table when it does load
 * -All else is acceptable, I guess
 * 
 * 
 * It will display the table correctly if you select to do that first, but it wont do it again after that.
 * Submitting new entries on the other side also doesn't seem to work if you view the table beforehand.
 * Probably some kind of correlation there.
 */

public class ClientLayout extends JFrame {
	
	private static Container c;
	
	private JMenuBar menu;
	private JMenu menu1;
	private JMenuItem submitM;
	private JMenuItem viewM;
	
	private JPanel p1;
	private TextField techName;
	private TextField custName;
	private JComboBox details;
	private JComboBox priority;
	private JButton sub;
	
	private JTable view;
	private JPanel pView;
	private JScrollPane scroll;
	
	private InputStream is;
	private OutputStream os;
	private Socket connection;
	private PrintWriter out;
	private FileInputStream fis;
	private FileOutputStream fos;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	static File server = new File("./toServer.ser");
	static File client = new File("./toClient.ser");
	private static IssueReport temp;
	

	private static final long serialVersionUID = 7526472295622776147L; 
	
	ClientLayout(){
		super("Technician Bros (Est 1980)");
		
		try {
			ConnectToServer();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setSize(500,250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		c = getContentPane();
		
		//creating the menu
		/*
		 * The menu provides the core functionality of this system, navigating between the options
		 * and offering a consistency to the layout, 
		 * the ActionEvent's of this project were only the menu options and the submit report button
		 * the menu is positioned north in the frame, making it look just like a real program!
		 */
		menu = new JMenuBar();
		menu.setSize(500, 100);
		menu1 = new JMenu("Issues");
		menu.add(menu1);
		submitM = new JMenuItem("Submit Issues");
		viewM = new JMenuItem("View Issues");
		menu1.add(submitM);
		menu1.add(viewM);
		
		menu.setVisible(true);
		
		p1 = new JPanel();
		p1.setSize(500,200);
		
		//just creating a quick homepage thing, super quick
		//some JLabels and basic formatting
		JPanel p2 = new JPanel();
		p2.setSize(500, 200);
		p2.setBackground(Color.MAGENTA);
		JLabel l1= new JLabel("The Technician Bros");
		JLabel l2 = new JLabel("\nFixing computers since 1980");
		l1.setForeground(Color.WHITE);
		l1.setFont(new Font("Arial", Font.PLAIN, 30));
		l2.setForeground(Color.gray);
		l2.setFont(new Font("Serif", Font.PLAIN, 12));
		p2.add(l1, BorderLayout.CENTER);
		p2.add(l2, BorderLayout.SOUTH);
		p2.setVisible(true);
		
		pView = new JPanel();
		pView.setSize(500,200);
		
		c.add(menu, BorderLayout.NORTH);
		c.add(p2, BorderLayout.CENTER);
		setVisible(true);
		/* This stops the panels from being distorted by the window size being adjusted,
		 * however, there appear to be some visibility problems that I can only redeem by
		 * adjust the window size slightly to 'wake up' with the table, so I've left it out
		 * for now
		setResizable(false);*/
		p1.setVisible(false);
		
		//creating a handler class to avoid any more clutter in this class
		TheHandler handler = new TheHandler();
		
		//all menu options operate through the handler
		submitM.addActionListener(handler);
		viewM.addActionListener(handler);
		
	}
	
	private class TheHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			
			//submit seems to occasionally hit some kind of snag, maybe 1 out of every 3 turns right now, sorry about that
			if(event.getSource()==submitM){
				//declaring the values of each field
				pView.setVisible(false);
				
				//clear the frame and refresh the menu
				c.removeAll();
				c.add(menu, BorderLayout.NORTH);
				
				//fields featured in the submit report page
				techName = new TextField(8);
				custName = new TextField(8);
				//details summarised down to a basic keyword in this skeleton system, keywords may match up with some of the staff's specialties (see Technician class)
				details = new JComboBox(new String[]{"Select Keyword", "Windows 8 Navigation","Operating-System", "Hard-Drive", "Memory", "Misc-Software", "Misc-Hardware"});
				priority = new JComboBox(new String[]{"Select Priority","1 - Lowest","2 - Low","3 - Moderate","4 - High","5 - Highest"});
				sub = new JButton("Submit");
				
				//clears the panel within the frame
				p1.removeAll();
				
				//adding fields to the panel
				//just formats out the grid layout a little better without getting caught in formatting too much
				p1.add(new JLabel("Technician Name"));
				p1.add(techName);
				p1.add(new JLabel("Customer Name"));
				p1.add(custName);
				p1.add(new JLabel("Details"));
				p1.add(details);
				p1.add(new JLabel("Priority"));
				p1.add(priority);
				p1.add(new JLabel(""));
				p1.add(sub);
				
				c.add(p1, BorderLayout.CENTER);
				
				//establishing layout and colours
				p1.setLayout(new BorderLayout());
				p1.setBackground(Color.white);
				p1.setLayout(new GridLayout(5,2));
				
				//action listener for the submit button
				sub.addActionListener(new ActionListener(){
				
					//action for the submit button, records new issue report and resets all fields
					public void actionPerformed(ActionEvent e){
						//Gathering string values for generating the issue report
						//for whatever reason, on repeated submits, these string values stay the same as the first time
						String str1 = techName.getText();
						String str2 = custName.getText(); 
						//firstly finding the index, then using the index to find the string value at that point
						int sr3 = (int)details.getSelectedIndex();
						String str3 = (String) details.getItemAt(sr3);
						//using the index of the JComboBox as it is the same as the numeric option
						int str4 = (int)priority.getSelectedIndex();
						

						//very basic verification process listed here, could be easily made more complicated when needed
						String ver = "Please Correct The Following:\n";
						if(str1.length()<5||str2.length()<5||str3=="Select Keyword"||str3=="Select Keyword"){
							if(str1.length()<5){
								ver+="- Technician's name(5 character minimum)\n";
							}
							if(str2.length()<5){
								ver+="- Customer's name(5 character minimum)\n";						
							}
							if(str3=="Select Keyword"){
								ver+="- Select a keyword\n";
							}
							if(str4==0){
								ver+="- Select a priority\n";
							}
							JOptionPane.showMessageDialog(null, ver);
						}
						else{
							temp = new IssueReport(str1,str2,str3,str4);
							//attempt to reset values for next turn, doesn't seem to work
							techName.setText("");
							custName.setText(""); 
							details.setSelectedIndex(0);
							priority.setSelectedIndex(0);
							//calling the recording method
							Send();
							
							/* This seems to work, but something else is confusing the stream 
							 * and causing pretty huge trouble
							 * If you can see the issue pretty easily, just remove the comments from here 
							 * and the complementary bit in the doReq method in the Service class
							 * 
							 * Gah! What are these EOFExceptions all about?
							try {
								SetUpStreams();
								Scanner resp = new Scanner(connection.getInputStream());
								if(resp.hasNext()){
								String response = resp.nextLine();
								CloseStreams();
								JOptionPane.showMessageDialog(null, response);}
								else{
									CloseStreams();
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}*/
						}	
					}
				});
				//setting the frame as visible
				p1.setVisible(true);
				
			}
			if(event.getSource()==viewM){
				
				//for some reason, this gets loads of EOFException errors, 
				//it should show up within 3 or four clicks of the menu options
				/**HOWEVER you have to adjust the window size slightly for it to be viewable**/
				c.removeAll();
				pView.removeAll();
				c.add(menu, BorderLayout.NORTH);
				
				p1.setVisible(false);
				pView.setLayout(new BorderLayout());
				pView.setVisible(true);
				
				String[] cols = new String[]{"Technician", "Customer", "Details", "Priority", "Status"};
				//receive method generates the table entries from the server automatically
				String[][] entries = Receive();
				
				view = new JTable(entries, cols);
				scroll = new JScrollPane(view);
				pView.add(scroll);
				
				c.add(pView, BorderLayout.CENTER);
				
			}
		}
		
	}
	
	
	//method for connecting to the server, some quick console printing just to help keep track of things there
	private void ConnectToServer() throws UnknownHostException, IOException{
		System.out.println("Attempting Connection...");
		connection = new Socket("localhost", 12345);
		System.out.println("You are connected");
	}
	
	//method to establish necessary streams
	private void SetUpStreams() throws IOException{
		try{
			FileInputStream fis = new FileInputStream(server);
			FileOutputStream fos = new FileOutputStream(client);
			output = new ObjectOutputStream(fos);
			output.flush();
			input = new ObjectInputStream(fis);
			System.out.println("Streams ready");
		}catch(EOFException e){
			e.printStackTrace();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	//method to produce the data for the table
	private String[][] Receive(){
		ArrayList<IssueReport> lista = null;
		try{
			SetUpStreams();
			//sending basic string instruction to server for it to return the list of issues
			out = new PrintWriter(connection.getOutputStream());
			out.write("VIEW\n");
			out.flush();
			/*readObject here is incredibly erratic, sometimes works, 
			 * mostly brings the most recent entry back the same number of times as 
			 * 		there are entries in the database
			 */
			lista = (ArrayList<IssueReport>)input.readUnshared();
			CloseStreams();
		}catch(ClassNotFoundException e){
			System.out.println("Unrecognised object");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//converts list into multidimensional array for the tables
		if(lista!=null){
			String[][] list = new String[lista.size()][5];
			int i= 0;
			for(IssueReport a:lista){
				list[i][0] = a.getTechnician();
				list[i][1] = a.getCustomer();
				list[i][2] = a.getDetails();
				list[i][3] = String.valueOf(a.getPriority());
				list[i][4] = String.valueOf(a.isSolved());
				i++;
			}
			return list;
		}
		else{//just a sort of crappy thing to appear if an error occurs
			String[][] blank = new String[][]{new String[]{"Database", "Is", "Currently", "Not", "Loading"}};
			return blank;
		}
	}
	
	//method to close streams at the end
	private void CloseStreams(){
		System.out.println("Closing the streams");
		try{
			output.close();
			input.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//sending a new IssueReport to the server's database
	private void Send(){
		try{
			SetUpStreams();
			//this works the first time, after that it keeps sending the initial values that were entered
			//sending basic string value again to process the correct portion of the doReq method
			out = new PrintWriter(connection.getOutputStream());
			out.write("SUBMIT\n");
			out.flush();
			output.writeUnshared(temp);
			output.flush();
			CloseStreams();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}