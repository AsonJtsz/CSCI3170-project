package net.codejava;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class NEW {
  public static void main(String[] args) {
    // TODO code application logic here
 //  The URL should be correct now
    String url = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group4";
    String username = "Group4";
    String password = "3170group4";
    

    try {
    	//Class.forName("com.mysql.jdbc.Driver");
    	Connection conn = DriverManager.getConnection(url, username, password);
    	main_menu(conn);
    } catch (SQLException e) {
     System.out.println(e);
    }
  }
	
	public static void main_menu(Connection conn) {
	    int input;
	    System.out.println("Welcome! Who are you?");
	    System.out.println("1. An administrator");
	    System.out.println("2. A passenger");
	    System.out.println("3. A driver");
	    System.out.println("4. A manager");
	    System.out.println("5. None of the above");
	    Scanner scan = new Scanner(System.in);
// 	    do {
// 	      System.out.print("Please enter [1-4]");
// 	      input = scan.nextInt();
// 	    } while (input < 1 || input > 5);
// 	    if (input == 1)
// 	      admin_operation(conn);
// 	    else if (input == 2)
// 	      passenger_operation(conn);
// 	    else if (input == 3)
// 	      driver_operation(conn);
// 	    else if (input == 4)
// 		  manager_operation(conn);
// 	    else if (input == 5) {
// 	      System.out.println("Good bye :)");
// 	    System.exit(1); }
// 	      else {
// 	     System.out.println("[ERROR] Invalid input");
// 	    }
		
   	 while (true) {
		Scanner scan = new Scanner(System.in);
	
	System.out.print("Please enter [1-4]");
		input = scan.nextInt();
	    if (input == 1) {
	    	System.out.println(1);}
	    else if (input == 2) {
	    	System.out.println(2);} 
	    else if (input == 3) {
	    	System.out.println(3); }
	    else if (input == 4) {
	    	System.out.println(4);;  
	    	} else if (input == 5) {
	      System.out.println("Good bye :)");
	    System.exit(1);
	    } else System.out.println("[ERROR] Invalid input");
	
		}		

	  }
	
	
	
	private static void admin_operation(Connection conn) {
		// TODO Auto-generated method stub
		
	}
	
	private static void passenger_operation(Connection conn) {
		// TODO Auto-generated method stub
		
	}

	private static void driver_operation(Connection conn) {
		// TODO Auto-generated method stub
			
	}
		
		
	private static void manager_operation(Connection conn) {
		
		
		String NewTable1 = "CREATE TABLE Manager (" 
	            + "Tid integer primary key,"  
	            + "Dname varchar(20)," 
	            + "Pname varchar(20)," 
	            + "start_location varchar(20),"
	            + "destination varchar(20),"
	            + "duration integer,"
	            + "distance integer)";
		
		
		String InsertManager = "Insert into Manager(Tid,Dname,Pname,start_location,destination,duration, distance)" 
	            + "Select Distinct T.ID,D.name,P.name, T.start_location, T.destination , (EXTRACT(MINUTE FROM (end_time - start_time)) + EXTRACT(hour FROM (end_time - start_time))*60), (ABS(TS.x - TE.x) + ABS(TS.y - TE.y))"  
	            + "from (((trip T Join driver D on T.driver_id = D.id) join passengers P on T.passenger_id = P.id)join taxi_stop TS on TS.name = T.start_location)join taxi_stop TE on TE.name = T.destination;";
		
		Scanner myObj = new Scanner(System.in);
		
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate("DROP TABLE IF EXISTS Manager;");
			statement.executeUpdate(NewTable1);
			statement.executeUpdate(InsertManager);
			
			System.out.println("Manager, what would you like to do?\r\n1. Find trips\r\n2. Go back\r\nPlease enter [1-2]");
				int option = myObj.nextInt();
				if(option == 1) {
					System.out.println("Please enter the minimum traveling distance.");
					int MinDis = myObj.nextInt();
					System.out.println("Please enter the maximum traveling distance.");
					int MaxDis = myObj.nextInt();
					//System.out.println("Data: " +MinDis +MaxDis);
					String ResultQuery = "select Tid,Dname,Pname,start_location,destination,duration,distance from Manager where distance > ? and distance < ?;";
					PreparedStatement preparedStatement = conn.prepareStatement(ResultQuery);
					preparedStatement.setInt(1, MinDis);
					preparedStatement.setInt(2, MaxDis);
					ResultSet result = preparedStatement.executeQuery();
					System.out.println("trip id, driver name, passenger name,start location, destination, duration");
					ResultSetMetaData rsmd = result.getMetaData();
					int columnsNumber = rsmd.getColumnCount();

					while (result.next()) {
					    for(int i = 1; i < columnsNumber; i++)
					        System.out.print(result.getString(i) + " ");
					    System.out.println();
					}
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	
	
}
