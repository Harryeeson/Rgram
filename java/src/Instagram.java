/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 * 
 * Harrison Yee 862023089
 * 
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.math.BigInteger;

import java.sql.Time;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class Instagram{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public Instagram(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + Instagram.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		Instagram esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new Instagram (dbname, dbport, user, "");

			boolean keepon = true;

			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Display feed");
				System.out.println("2. View user photos");
				System.out.println("3. Upload photos");
				System.out.println("5. Comment on photo");
				System.out.println("6. Add a tag to a photo");
				System.out.println("15. EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: DisplayFeed(esql); break;
					case 2: ViewUserPhotos(esql); break;
					case 3: UploadPhotos(esql); break;
					case 5: CommentPhoto(esql);	break;
					case 6: TagPhoto(esql);		break;
					case 15: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}
	// end readChoice

	public static void DisplayFeed(Instagram esql) {
		try {
			String query_display = "SELECT *\n FROM Photo\n ORDER BY likes DESC;";
			if(esql.executeQueryAndPrintResult(query_display) == 0) {
				System.out.println("Nothing on feed to display");
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
 	}

	public static void ViewUserPhotos(Instagram esql) {
		String username;
		String password;
		do {
			System.out.println("Username: ");
			try {
				username = in.readLine();
				if(username.length() > 64 || username.length() == 0)  {
					throw new ArithmeticException("Username cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);
		do {
			System.out.println("Password: ");
			try {
				password = in.readLine();
				if(password.length() > 64 || password.length() == 0)  {
					throw new ArithmeticException("Password cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		try {
			String query_user = "SELECT *\n FROM Users\n WHERE username = '" + username + "'and pwd = '" + password + "';";
			if (esql.executeQuery(query_user) == 0) {
				System.out.println("This user does not exist");
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
			
		try {
			String query_usr_photos = "SELECT *\n FROM Photo WHERE username = '" + username + "';";
			if(esql.executeQueryAndPrintResult(query_usr_photos) == 0) {
				System.out.println("Photos DNE");
				return;
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void UploadPhotos(Instagram esql) {
		String username;
		String password;
		String photo_title;
		
		do {
			System.out.println("Username: ");
			try {
				username = in.readLine();
				if(username.length() > 64 || username.length() == 0)  {
					throw new ArithmeticException("Username cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		do {
			System.out.println("Password: ");
			try {
				password = in.readLine();
				if(password.length() > 64 || password.length() == 0)  {
					throw new ArithmeticException("Password cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		try {
			String query_user = "SELECT *\n FROM Users\n WHERE username = '" + username + "'and pwd = '" + password + "';";
			if (esql.executeQuery(query_user) == 0) {
				System.out.println("This user does not exist");
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		do {
			System.out.println("Enter title for photo: ");
			try {
				photo_title = in.readLine();
				if(photo_title.length() > 128 || photo_title.length() == 0)  {
					throw new ArithmeticException("Photo title cannot be empty and has to be less 128 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		ZonedDateTime zone_date_time = ZonedDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ssx");
		String zdt = dtf.format(zone_date_time);

		List<List<String>> photo_id_list = new ArrayList<List<String>>();

		try {
			String photo_id_query = "SELECT max(pid) from Photo";

			photo_id_list = esql.executeQueryAndReturnResult(photo_id_query);

			if (photo_id_list.size() == 0) {
				System.out.println("This does not exist"); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		Integer photo_id = Integer.parseInt(photo_id_list.get(0).get(0)) + 1;
		System.out.println("Here is the your PID: " + photo_id);
		try {
			String insert_query = "INSERT INTO Photo (pid, username, title, likes, dislikes, pdate) VALUES ('" + photo_id + "', '" + username + "', '" + photo_title + "', '0', '0', '" + zdt + "');";
			esql.executeUpdate(insert_query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void CommentPhoto(Instagram esql) {
		String username;
		String password;
		String author;
		String photo_title;
		String comment;

		do {
			System.out.println("Username: ");
			try {
				username = in.readLine();
				if(username.length() > 64 || username.length() == 0)  {
					throw new ArithmeticException("Username cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		do {
			System.out.println("Password: ");
			try {
				password = in.readLine();
				if(password.length() > 64 || password.length() == 0)  {
					throw new ArithmeticException("Password cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		try {
			String query_user = "SELECT *\n FROM Users\n WHERE username = '" + username + "'and pwd = '" + password + "';";
			if (esql.executeQuery(query_user) == 0) {
				System.out.println("This user does not exist");
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		do {
			System.out.println("Enter the username who posted the photo: ");
			try {
				author = in.readLine();
				if(author.length() > 64 || author.length() == 0)  {
					throw new ArithmeticException("Author username cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		do {
			System.out.println("Enter the title of the photo you would like to comment on: ");
			try {
				photo_title = in.readLine();
				if(photo_title.length() > 128 || photo_title.length() == 0)  {
					throw new ArithmeticException("Username cannot be empty and has to be less 128 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		List<List<String>> pid_list = new ArrayList<List<String>>();
		try {
			String query_pid = "SELECT pid\n FROM Photo\n WHERE username = '" + author + "'and title = '" + photo_title + "';";
			pid_list = esql.executeQueryAndReturnResult(query_pid);
			if (pid_list.size() == 0) {
				System.out.println("The user does not have this photo, or this photo is not posted by this user");
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		Integer pid = Integer.parseInt(pid_list.get(0).get(0));

		List<List<String>> cid_list = new ArrayList<List<String>>();

		try {
			String cid_query = "SELECT max(cid) from PhotoComments";

			cid_list = esql.executeQueryAndReturnResult(cid_query);

			if (cid_list.size() == 0) {
				System.out.println("This does not exist"); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		Integer cid = Integer.parseInt(cid_list.get(0).get(0)) + 1;

		do {
			System.out.println("Enter your comment: ");
			try {
				comment = in.readLine();
				if(comment.length() > 128 || comment.length() == 0)  {
					throw new ArithmeticException("comment cannot be empty and has to be less 128 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Invalid input!");
				continue;
			}
		} while(true);

		try {
			String query = "INSERT INTO PhotoComments (cid, pid, commentor, comments) VALUES ('" + cid + "', '" + pid + "', '" + username
							 + "', '" + comment + "');";
			esql.executeUpdate(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void TagPhoto(Instagram esql) {

	}

}
