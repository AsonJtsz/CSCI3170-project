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
 //  not sure if below URL is correct
    String url = "jdbc:mysql://projg.cse.cuhk.edu.hk:2633/group4";
    String username = "Group4";
    String password = "3170group4";
    
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
	conn = DriverManager.getConnection(url, username, password);
    } catch (ClassNotFoundException e) {
      // handle any errors
      System.out.println("[ERROR] Java MySQL DBDriver not found!!");
      System.exit(0);
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
	    do {
	      System.out.print("Please enter [1-4]");
	      input = scan.nextInt();
	    } while (input < 1 || input > 5);
	    if (input == 1)
	      admin_operation(conn);
	    else if (input == 2)
	      passenger_operation(conn);
	    else if (input == 3)
	      driver_operation(conn);
	    else if (input == 4)
		  manager_operation(conn);
	    else if (input == 5) {
	      System.out.println("Good bye :)");
	    System.exit(1); }
	      else {
	     System.out.println("[ERROR] Invalid input");
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
		// TODO Auto-generated method stub
		
	}

	
	
	
	
}
