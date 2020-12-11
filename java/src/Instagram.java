
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

/*
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 * 
 */

public class Instagram{
	//define global variables
	public static String username;
	public static String password;

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
			
			CheckLogin(esql);

			while(keepon){
				System.out.println("\nMAIN MENU");
				System.out.println("---------");
				System.out.println("1. Display news feed");
				System.out.println("2. Search for user");
				System.out.println("3. Follow user");
				System.out.println("4. List popular users");
				System.out.println("5. Search for photo");
				System.out.println("6. View statistics of photo");
				System.out.println("7. Comment on photo");
				System.out.println("8. Add tag or tag user to photo");
				System.out.println("9. Upload photo");
				System.out.println("10. Download photo");
				System.out.println("11. List most popular photos");
				//System.out.println("12. View user photos");
				System.out.println("15. EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: DisplayFeed(esql); break;
					case 2: SearchForUser(esql); break;
					case 3: FollowUser(esql); break;
					case 4: ListPopularUsers(esql); break;
					case 5: SearchForPhoto(esql); break;
					case 6: ViewStatsOfPhoto(esql); break;
					case 7: CommentPhoto(esql);	break;
					case 8: TagPhoto(esql);		break;
					case 9:  UploadPhoto(esql); break;
					case 10: DownloadPhoto(esql); break;
					case 11: ListPopularPhotos(esql); break;
					//case 12: ViewUserPhotos(esql); break;
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

	/* =============================================================================
								Helper Functions
	============================================================================= */
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

	public static Integer FindPID(Instagram esql) {
		List<List<String>> pid_list = new ArrayList<List<String>>();
		String author;
		String photo_title;
		Integer pid;

		do {
			do {
				System.out.println("Enter the username who posted the photo: ");
				try {
					author = in.readLine();
					if(author.length() > 64 || author.length() == 0)  {
						System.out.println("Author username cannot be empty and has to be less 64 characters or less.");
						continue;
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
				System.out.println("Enter the title of the photo: ");
				try {
					photo_title = in.readLine();
					if(photo_title.length() > 128 || photo_title.length() == 0)  {
						System.out.println("Title cannot be empty and has to be less 128 characters or less.");
						continue;
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
				String query_pid = "SELECT pid FROM Photo WHERE username = '" + author + "'and title = '" + photo_title + "';";
				pid_list = esql.executeQueryAndReturnResult(query_pid);
				if (pid_list.size() == 0) {
					System.out.println("The user does not have this photo, or this photo is not posted by this user");
					continue;
				}
				else {
					pid = Integer.parseInt(pid_list.get(0).get(0));
					break;
				}
				
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		} while(true);

		return pid;
	}

	/* =============================================================================
									Login Function
	============================================================================= */

	public static void CheckLogin(Instagram esql) {
		String uname;
		String pwd;

		System.out.println("      R'Gram      ");
		System.out.println("------------------");
		System.out.println("Login");
		System.out.println("---------");

		do {
			do {
				System.out.println("Username: ");
				try {
					uname = in.readLine();
					if(uname.length() > 64 || uname.length() == 0)  {
						System.out.println("Username cannot be empty and has to be less 64 characters or less.");
						continue;
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
					pwd = in.readLine();
					if(pwd.length() > 64 || pwd.length() == 0)  {
						System.out.println("Password cannot be empty and has to be less 64 characters or less.");
						continue;
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
				String query_user = "SELECT * FROM Users WHERE username = '" + uname + "'and pwd = '" + pwd + "';";
				if (esql.executeQuery(query_user) == 0) {
					System.out.println("This user does not exist. Please try again.");
					continue;
				}
				else {
					Instagram.username = uname;
					Instagram.password = pwd;
					System.out.println("\nWelcome back " + uname + "!");
					break;
				}
				
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}

		} while(true);
	}

	/* =============================================================================
								BEGIN MENU FUNCTIONS
	   ============================================================================= */

	public static void DisplayFeed(Instagram esql) {	// 1
		try {
			String query_display = "SELECT * FROM Photo ORDER BY likes DESC;";
			if(esql.executeQueryAndPrintResult(query_display) == 0) {
				System.out.println("Nothing on feed to display");
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	 }
	 
	public static void SearchForUser(Instagram esql) {	// 2

	}

	public static void FollowUser(Instagram esql)  {	// 3
		List<List<String>> fid_list = new ArrayList<List<String>>();
		String following_usr;
		Integer follower_id;

		do{
			do {
				System.out.println("Enter the username of the user you would like to follow: "); 
				try {
						following_usr = in.readLine();
						if(following_usr.length() > 64 || following_usr.length() == 0)  {
							System.out.println("Username cannot be empty and has to be less 64 characters or less.");
							continue;
						}
						else {
							break;
						}
					} catch(Exception e) {
						System.out.println(e.getMessage());
						continue;
					}
			} while(true); 

			//check if the username to be followed exists in the database 	
			try {
				String query_user = "SELECT * FROM Users WHERE username = '" + following_usr + "';";
				if (esql.executeQuery(query_user) == 0) {
					System.out.println("This user does not exist");
					continue;
				}
				else{
					break; 
				}	
			} 
			catch(Exception e) {
				System.out.println(e.getMessage());
				continue; 
			}
		} while(true); 

		try {
			String fid_query = "SELECT max(fid) from Followers";

			fid_list = esql.executeQueryAndReturnResult(fid_query);

			if (fid_list.size() == 0) {
				System.out.println("This does not exist"); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		follower_id = Integer.parseInt(fid_list.get(0).get(0)) + 1;

		//add a new entry into the sql Following table 
		try{
			String insert_query = "INSERT INTO Followers (fid, username, following_usr) VALUES ('" + follower_id + "', '" + following_usr + "', '" + Instagram.username + "');";
			esql.executeUpdate(insert_query); 
		} 
		catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void ListPopularUsers(Instagram esql) {	// 4

	}

	public static void SearchForPhoto(Instagram  esql) {	// 5

	}

	public static void ViewStatsOfPhoto(Instagram esql) {	// 6
		String choice;
		Integer photo_id;

		photo_id = FindPID(esql);

		try {
			String photo_query = "SELECT P.username, P.title, P.likes, P.dislikes, COUNT(C.comments) AS NumberOfComments, P.pdate FROM Photo P LEFT JOIN PhotoComments C ON P.pid = C.pid WHERE P.pid = '" + photo_id + "' GROUP BY P.username, P.title, P.likes, P.dislikes, P.pdate;";
			esql.executeQueryAndPrintResult(photo_query);

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		do {
			System.out.println("Would you like to view comments for this photo? (Y/N)");
			try {
				choice = in.readLine();
				if(choice.equals("Y")) {
					String comment_query = "SELECT commentor, comments FROM PhotoComments WHERE pid = '" + photo_id + "';";
					esql.executeQueryAndPrintResult(comment_query);
					break;
				}
				else if(choice.equals("N")) {
					break;
				}
				else {
					System.out.println("Invalid choice, please try again.");
					continue;
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		} while(true);

	}

	public static void CommentPhoto(Instagram esql) {	// 7
		List<List<String>> cid_list = new ArrayList<List<String>>();
		String comment;
		Integer photo_id;
		Integer comment_id;

		photo_id = FindPID(esql);

		do {
			System.out.println("Enter your comment: ");
			try {
				comment = in.readLine();
				if(comment.length() > 128 || comment.length() == 0)  {
					System.out.println("Comment cannot be empty and has to be less 128 characters or less.");
					continue;
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
			String cid_query = "SELECT max(cid) from PhotoComments";

			cid_list = esql.executeQueryAndReturnResult(cid_query);

			if (cid_list.size() == 0) {
				System.out.println("This does not exist"); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		comment_id = Integer.parseInt(cid_list.get(0).get(0)) + 1;

		try {
			String query = "INSERT INTO PhotoComments (cid, pid, commentor, comments) VALUES ('" + comment_id + "', '" + photo_id + "', '" + username + "', '" + comment + "');";
			esql.executeUpdate(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void TagPhoto(Instagram esql) {	// 8
		List<List<String>> tid_list = new ArrayList<List<String>>();
		String tag;
		Integer photo_id;
		Integer tag_id;

		photo_id = FindPID(esql);

		do {
			System.out.println("Enter your tag: ");
			try {
				tag = in.readLine();
				if(tag.length() > 128 || tag.length() == 0)  {
					System.out.println("Tag cannot be empty and has to be less 128 characters or less.");
					continue;
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
			String tid_query = "SELECT max(tid) from Tags";

			tid_list = esql.executeQueryAndReturnResult(tid_query);

			if (tid_list.size() == 0) {
				System.out.println("This does not exist"); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		tag_id = Integer.parseInt(tid_list.get(0).get(0)) + 1;

		try {
			String query = "INSERT INTO Tags (tid, pid, tagging) VALUES ('" + tag_id + "', '" + photo_id + "', '" + tag + "');";
			esql.executeUpdate(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void UploadPhoto(Instagram esql) {	// 9
		List<List<String>> photo_id_list = new ArrayList<List<String>>();
		String photo_title;
		Integer photo_id;
		
		do {
			System.out.println("Enter title for photo: ");
			try {
				photo_title = in.readLine();
				if(photo_title.length() > 128 || photo_title.length() == 0)  {
					System.out.println("Photo title cannot be empty and has to be less 128 characters or less.");
					continue;
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

		photo_id = Integer.parseInt(photo_id_list.get(0).get(0)) + 1;

		try {
			String insert_query = "INSERT INTO Photo (pid, username, title, likes, dislikes, pdate) VALUES ('" + photo_id + "', '" + Instagram.username + "', '" + photo_title + "', '0', '0', '" + zdt + "');";
			esql.executeUpdate(insert_query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void DownloadPhoto(Instagram esql) {	// 10
		List<List<String>> photo_list = new ArrayList<List<String>>();
		String photo_title;
		Integer photo_id;

		photo_id = FindPID(esql);

		try {
			String photo_title_query = "SELECT title from Photo WHERE pid = '" + photo_id + "';";

			photo_list = esql.executeQueryAndReturnResult(photo_title_query);

			if (photo_list.size() == 0) {
				System.out.println("This does not exist"); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		photo_title = photo_list.get(0).get(0);
		System.out.println(photo_title);

		//insert hdfs function to download photo
		//title of photo is stored in photo_title

	}

	public static void ListPopularPhotos(Instagram esql) {	//11

	}

	// public static void ViewUserPhotos(Instagram esql) {
	// 	String username;
	// 	String password;
			
	// 	try {
	// 		String query_usr_photos = "SELECT *\n FROM Photo WHERE username = '" + Instagram.username + "';";
	// 		if(esql.executeQueryAndPrintResult(query_usr_photos) == 0) {
	// 			System.out.println("Photos DNE");
	// 			return;
	// 		}
	// 	} catch(Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}

	// }

}
