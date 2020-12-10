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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.*;

//hdfs dfs –mkdir /some_folder ,hdfs command to make a folder
//hdfs dfs -rm -r /some_folder ,hdfs command to remove an entire folder
//hdfs dfs -rm /some_folder/file_name , hdfs command to remove a file from a folder
//hdfs dfs -copyFromLocal C:\hadoop\pics\photo_name /username_photos, hdfs command to upload photos to hdfs 
//hdfs dfs -get /username_photos/photo_name C:\hadoop\cs179\photo_downloads, hdfs command to download photos from hdfs don't forget about the space
//hdfs dfs -ls / ,hdfs command to display all folders
//hdfs dfs -ls /some_folder ,hdfs command to display the contents of a folder



public class menu {
    static String username = "harry";  //this is for testing purposes, later on write a log in functions and store the actual user name into the username variable.
    static String sql_command = "INSERT INTO Users (uid, fname, lname, num_follows, num_followers) VALUES (9, 'James', 'Fred', 1000, 1000);"; //testing purposes only
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static void main (String[] args) {


            
            boolean keepon = true;
            while(keepon){
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Upload a photo");
                System.out.println("2. Download a photo");
                System.out.println("3. test the run_sql_command function");  //testing purposes, change later.
                System.out.println("4. EXIT");
                
                switch (readChoice()){
                    case 1: upload_photo(); break;
                    case 2: download_photo(); break;
                    case 3: run_sql_command(sql_command); break;
                    case 4: keepon = false; break;
                }
            }
        
    }


    public static int readChoice() {
        int input;
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


    public static void upload_photo(){
        
        //asks user to enter a path to photo they wish to upload
        Scanner sc= new Scanner(System.in);   
        System.out.print("Enter the path to the photo file: ");  
        String photo_path= sc.nextLine();  
        String upload_command;
        String ls_command;
        //hdfs dfs -copyFromLocal C:\hadoop\pics\photo_name /username_photos, hdfs command to upload photos to hdfs 
        upload_command = "hdfs dfs -copyFromLocal " + photo_path + " /" + username +"_photos";
 
        ProcessBuilder processBuilder = new ProcessBuilder();
        // Windows
        processBuilder.command("cmd.exe", "/c", upload_command);
        try {

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
       	    while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //verify the upload by displaying the ls command hdfs
        ls_command = "hdfs dfs -ls /" + username +"_photos";
        processBuilder.command("cmd.exe", "/c", ls_command);
        try {

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
       	    while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.print("\nPhoto has been uploaded\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void download_photo() {
        String username_download;
        String photo_name;
        String ls_command;
        String download_command;
        Scanner sc= new Scanner(System.in);  
        System.out.print("Enter the name of the user you wish to download a photo from: ");  
        username_download= sc.nextLine();  
        System.out.print("Here are the user's photos\n");
        ls_command = "hdfs dfs -ls /" + username_download +"_photos";
        ProcessBuilder processBuilder = new ProcessBuilder(); //show the photos of the username entered.
        processBuilder.command("cmd.exe", "/c", ls_command); 
        try {

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
       	    while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("\nEnter the name of the photo you wish to download: ");  //tell user to select a photo
        photo_name= sc.nextLine();
        //hdfs dfs -get /username_photos/photo_name C:\hadoop\cs179\photo_downloads, hdfs command to download photos from hdfs don't forget about the space
        download_command = "hdfs dfs -get /" + username_download + "_photos/" + photo_name + " C:\\hadoop\\cs179\\photo_downloads";  
        processBuilder.command("cmd.exe", "/c", download_command); //downloads the photo using the photo name and hdfs command
        try {

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
       	    while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.print("\nthe photo was downloaded to your photo_downloads folder\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static void run_sql_command(String sql_command){
        
        //sql_command = "INSERT INTO Users (uid, fname, lname, num_follows, num_followers) VALUES (8, 'Fred', 'Rodgers', 9999, 9999);";

        // auto close connection and preparedStatement
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "ahmad1987");
             PreparedStatement preparedStatement = conn.prepareStatement(sql_command)) {

            ResultSet resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("\n"); 

    }
}




