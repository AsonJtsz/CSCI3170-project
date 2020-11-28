package csci3170project;

import java.sql.*;
import java.util.Scanner;
import java.io.*;
import java.util.Calendar;


public class Main {
    
    public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group4";
    public static String dbUsername = "Group4";
    public static String dbPassword = "3170group4";
    
    public static Connection connectToDataBase()    {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
            System.out.println("Connect to DataBase Sucessfully!");
        } catch(ClassNotFoundException e)   {
            System.out.println("[Error]: Java MySQL DB Driver not found!!");
            System.exit(0);
        } catch (SQLException e)  {
            System.out.println(e);
            System.out.println("fail to connect the server");
        } 
        

        return conn;
    }
    
    public static void main(String[] args) {
       Connection conn = null;
       Scanner scan = new Scanner(System.in);
       outer:
       while(true)  {
            conn = connectToDataBase();
            System.out.println("Welcom! Who are you?");
            System.out.println("1. An administrator");
            System.out.println("2. A passenger");
            System.out.println("3. A driver");
            System.out.println("4. A manager");
            System.out.println("5. None of the above");
            System.out.println("Please enter[1-4]");
            int input = scan.nextInt();
        switch(input)    {
            case 1: 
                adminMenu(conn);
                break;
            case 2:
                passengerMenu(conn);
                break;
            case 5:
                System.out.println("ByeBye!");
                break outer;
            default:
                System.out.println("Invalid input, enter again");
                break;
        }
       }
    }   
    
    public static void adminMenu(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        outer:
        while(true) {
            System.out.println("Administrator, what would you like to do?");
            System.out.println("1. Create tables");
            System.out.println("2. Delete tables");
            System.out.println("3. Load Data");
            System.out.println("4. Check Data");
            System.out.println("5. Go Back");
            System.out.println("Please enter[1-5]");
            int input;
            input = scan.nextInt();
            switch(input)   {
                case 1:
                    createTable(conn);
                    break;
                case 2:
                    deleteTable(conn);
                    break;
                case 3:
                    loadData(conn);
                    break;
                case 4:
                    checkData(conn);
                    break;
                case 5:
                    break outer;
                default:
                    System.out.println("Invalid input, enter again");
                    break;
            }
        }
    }
    
    public static void createTable(Connection conn) {
        System.out.print("Processing...");
        StringBuilder vehicleSQL = new StringBuilder();
        StringBuilder driverSQL = new StringBuilder();
        StringBuilder PassengerSQL = new StringBuilder();
        StringBuilder Taxi_StopSQL = new StringBuilder();
        StringBuilder TripsSQL = new StringBuilder();
        StringBuilder RequestSQL = new StringBuilder();
        
        vehicleSQL.append("create table Vehicle(  VID VARCHAR(6) NOT NULL,  Model VARCHAR(30) NOT NULL, ");
        vehicleSQL.append("  Seats Integer NOT NULL,  PRIMARY KEY(VID));");
        
        driverSQL.append("create table Driver(   DID Integer NOT NULL, Name VARCHAR(30) NOT NULL, VID VARCHAR(6) NOT NULL, ");
        driverSQL.append("Driving_Years Integer NOT NULL,   PRIMARY KEY(DID),");
        driverSQL.append("FOREIGN KEY(VID) REFERENCES Vehicle(VID));");
        
        PassengerSQL.append("create table Passenger(   PID Integer NOT NULL,   Name VARCHAR(30) NOT NULL,");
        PassengerSQL.append("PRIMARY KEY(PID));");
        
        Taxi_StopSQL.append("create table Taxi_Stop(  Name VARCHAR(20) NOT NULL,");
        Taxi_StopSQL.append("X_Coordinate Integer NOT NULL,  Y_Coordinate Integer NOT NULL,");
        Taxi_StopSQL.append("PRIMARY KEY(Name));");
        
        TripsSQL.append("create table Trips(  TID Integer NOT NULL,  DID Integer NOT NULL,");
        TripsSQL.append("  PID Integer NOT NULL,  Start_Time DATETIME NOT NULL,  End_Time DATETIME,");
        TripsSQL.append("  Start_Location VARCHAR(20) NOT NULL,  Destination VARCHAR(20) NOT NULL,  Fee Integer,");
        TripsSQL.append("  PRIMARY KEY(TID),  FOREIGN KEY(DID) REFERENCES Driver(DID),");
        TripsSQL.append("  FOREIGN KEY(PID) REFERENCES Passenger(PID),  FOREIGN KEY(Start_Location) REFERENCES Taxi_Stop(Name),");
        TripsSQL.append("  FOREIGN KEY(Destination) REFERENCES Taxi_Stop(Name));");
        
        RequestSQL.append("create table Request(  RID Integer NOT NULL AUTO_INCREMENT, PID Integer NOT NULL,");
        RequestSQL.append("  Number_Of_Passengers Integer NOT NULL,");
        RequestSQL.append("  Start_Location VARCHAR(20) NOT NULL,  Destination VARCHAR(20) NOT NULL,");
        RequestSQL.append("  Partial_Model VARCHAR(30),  Minimum_Driving_Years Integer, Taken BOOLEAN DEFAULT false,");
        RequestSQL.append("  PRIMARY KEY(RID),  FOREIGN KEY(Start_Location) REFERENCES Taxi_Stop(Name),");
        RequestSQL.append("  FOREIGN KEY(Destination) REFERENCES Taxi_Stop(Name));");
        
        
        try {
            Statement stmt  = conn.createStatement();
            stmt.execute(vehicleSQL.toString());
            stmt.execute(driverSQL.toString());
            stmt.execute(PassengerSQL.toString());
            stmt.execute(Taxi_StopSQL.toString());
            stmt.execute(TripsSQL.toString());
            stmt.execute(RequestSQL.toString());
            System.out.println("Done! Tables are created!");
            stmt.close();
        } catch (SQLException e)   {
            System.out.println(e);
        }
        
    }
    
    public static void deleteTable(Connection conn) { 
        System.out.print("Processing...");
        String tables[] = {"Takes", "Drives", "Makes", "Request", "Trips", "Taxi_Stop", "Passenger", "Driver", "Vehicle"};
        for (int i = 0; i < tables.length; i++)   {
            StringBuilder dropStmt = new StringBuilder();
            dropStmt.append("drop table if exists ");
            //dropStmt.append("drop table ");          for testing
            dropStmt.append(tables[i]);
            try {
            PreparedStatement pstmt = conn.prepareStatement(dropStmt.toString());
            pstmt.executeUpdate();
            } catch (Exception e)   {
                System.out.println(e);
            }
        }
        System.out.println("Done! Tables are deleted");
    }
    
    public static void loadData(Connection conn)   {
        String vehicleSQL = "INSERT INTO Vehicle(VID, Model, Seats) VALUES (?,?,?)";
        String driverSQL = "INSERT INTO Driver(DID, Name, VID, Driving_Years) VALUES (?,?,?,?)";
        String passengerSQL = "INSERT INTO Passenger(PID, Name ) VALUES (?,?)";
        String taxi_stopSQL = "INSERT INTO Taxi_Stop(Name , X_Coordinate , Y_Coordinate) VALUES (?,?,?)";
        String tripsSQL = "INSERT INTO Trips(TID, DID, PID, Start_Time, End_Time, Start_Location, Destination, Fee) VALUES (?,?,?,?,?,?,?,?)";
        
        Scanner scan = new Scanner(System.in);
        String filePath;
        
        while(true) {
            System.out.println("Please enter the folder path");
            filePath = scan.nextLine();
            if((new File(filePath)).isDirectory()) 
                break;
            else
                System.out.println("The path does not exist!");
        }
        
        System.out.println("Processing...");
        
        System.out.println("Loading vehicles.csv");
        try {
            PreparedStatement stmt = conn.prepareStatement(vehicleSQL);
            BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/vehicles.csv"));
            String line;
            while ( (line = dataReader.readLine()) != null)    {
                String[] data = line.split(",");
                stmt.setString(1, data[0]);
                stmt.setString(2, data[1]);
                stmt.setInt(3, Integer.parseInt(data[2]));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch(Exception e)    {
            System.out.println(e);
        }
        System.out.println("Finish loading vehicles.csv!");
        
        System.out.println("Loading drivers.csv");
        try {
            PreparedStatement stmt = conn.prepareStatement(driverSQL);
            BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/drivers.csv"));
            String line;
            while ( (line = dataReader.readLine()) != null)    {
                String[] data = line.split(",");
                stmt.setInt(1, Integer.parseInt(data[0]));
                stmt.setString(2, data[1]);
                stmt.setString(3, data[2]);
                stmt.setInt(4, Integer.parseInt(data[3]));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch(Exception e)    {
            System.out.println(e);
        }
        System.out.println("Finish loading drivers.csv!");
        
        System.out.println("Loading passengers.csv");
        try {
            PreparedStatement stmt = conn.prepareStatement(passengerSQL);
            BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/passengers.csv"));
            String line;
            while ( (line = dataReader.readLine()) != null)    {
                String[] data = line.split(",");
                stmt.setInt(1, Integer.parseInt(data[0]));
                stmt.setString(2, data[1]);
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch(Exception e)    {
            System.out.println(e);
        }
        System.out.println("Finish loading passengers.csv!");
        
        System.out.println("Loading taxi_stops.csv");
        try {
            PreparedStatement stmt = conn.prepareStatement(taxi_stopSQL);
            BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/taxi_stops.csv"));
            String line;
            while ( (line = dataReader.readLine()) != null)    {
                String[] data = line.split(",");
                stmt.setString(1, data[0]);
                stmt.setInt(2, Integer.parseInt(data[1]));
                stmt.setInt(3, Integer.parseInt(data[2]));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch(Exception e)    {
            System.out.println(e);
        }
        System.out.println("Finish loading taxi_stops.csv!");
        
        System.out.println("Loading trips.csv");
        try {
            PreparedStatement stmt = conn.prepareStatement(tripsSQL);
            BufferedReader dataReader = new BufferedReader(new FileReader(filePath+"/trips.csv"));
            String line;
            while ( (line = dataReader.readLine()) != null)    {
                String[] data = line.split(",");
                stmt.setInt(1, Integer.parseInt(data[0]));
                stmt.setInt(2, Integer.parseInt(data[1]));
                stmt.setInt(3, Integer.parseInt(data[2]));
                stmt.setTimestamp(4, Timestamp.valueOf(data[3]));
                stmt.setTimestamp(5, Timestamp.valueOf(data[4]));
                stmt.setString(6, data[5]);
                stmt.setString(7, data[6]);
                stmt.setInt(8, Integer.parseInt(data[7]));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch(Exception e)    {
            System.out.println(e);
        }
        System.out.println("Finish loading trips.csv!");
        
    }
    
    public static void checkData(Connection conn)   {
        String tables[] = {"Vehicle", "Passenger", "Driver", "Trips", "Request",  "Taxi_Stop"};
        for (int i = 0; i < tables.length; i++) {
            try {
                Statement stmt  = conn.createStatement();
                StringBuilder sb = new StringBuilder();
                sb.append("select count(*) from ");
                sb.append(tables[i]);
                ResultSet rs = stmt.executeQuery(sb.toString());
                rs.next();
                System.out.println(tables[i]+": "+rs.getString(1));
                rs.close();
                stmt.close();
            } catch (Exception e)   {
                System.out.println(e);
            }
        }
        System.out.println("Numbers of records in each table:");
    }
    
    public static void passengerMenu(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        outer:
        while(true) {
            System.out.println("Passenger, what would you like to do?");
            System.out.println("1. Request a ride");
            System.out.println("2. Check trip records");
            System.out.println("3. Go back");;
            System.out.println("Please enter[1-3]");
            int input;
            input = scan.nextInt();
            switch(input)   {
                case 1:
                    requestRide(conn);
                    break;
                case 2:
                    
                    break;
                case 3:
                    break outer;
                default:
                    System.out.println("Invalid input, enter again");
                    break;
            }
        }
    }
    
    public static void requestRide(Connection conn) {
        Scanner scan = new Scanner(System.in);
        String checkID = "select * from Passenger where PID = ?";
        String checkLocation = "select * from Taxi_Stop where Name = ?";
        String checkModel = "select * from Vehicle where Model LIKE ?";
        String checkYears = "select * from Driver where Driving_Years >= ?";
        StringBuilder checkDriverNo = new StringBuilder();
        checkDriverNo.append("select count(*) from Driver, Vehicle where Driver.VID = Vehicle.VID and Seats >= ? and Driving_Years >= ? ");
        String requestSQL = "INSERT INTO Request(PID, Number_Of_Passengers, Start_Location, Destination, Partial_Model, Minimum_Driving_Years, Taken) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        int ID, PassengerNo, minimum_dirving_years;
        String start_location, destination, partial_model;
        while(true) {
            System.out.println("Please enter your ID.");
            String input = scan.nextLine();
            if (!input.matches("[0-9]+"))   {
                System.out.println("[Error]ID only contains digits");
                continue;
            }   else    {
                ID = Integer.parseInt(input);
            }
            try {
                PreparedStatement stmt = conn.prepareStatement(checkID);
                stmt.setInt(1, ID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())  {
                    break;
                } else  {
                    System.out.println("[Error] ID does not exist, enter again");
                }
            } catch (Exception e){
                System.out.println(e);
            }
        }
        
        while(true) {
            System.out.println("Please enter the number of passengers.");
            String input = scan.nextLine();
            if (!input.matches("[0-9]+"))   {
                System.out.println("[Error] Invalid input. Please enter digits only.");
                continue;
            }   else    {
                PassengerNo = Integer.parseInt(input);
            }
            
            if (PassengerNo > 8 || PassengerNo < 1)
                System.out.println("[Error] Invalid number of passengers.");
            else
                break;
        }
        
        while(true) {
            System.out.println("Please enter the start location.");
            start_location = scan.nextLine();
            try {
                PreparedStatement stmt = conn.prepareStatement(checkLocation);
                stmt.setString(1, start_location);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())  
                    break;
                else    {
                    System.out.println("[Error] Start Location Not found");
                }
            } catch(Exception e)    {
                System.out.println(e);
            }
        }
        
        while(true) {
            System.out.println("Please enter the destination.");
            destination = scan.nextLine();
            if (start_location.equals(destination)) {
                System.out.println("[Error] Destination and Start Location should be different.");
                continue;
            }
            try {
                PreparedStatement stmt = conn.prepareStatement(checkLocation);
                stmt.setString(1, destination);
                ResultSet rs = stmt.executeQuery();   
                if (rs.next())  
                    break;
                else    {
                    System.out.println("[Error] Dentination Not found");
                }
            } catch(Exception e)    {
                System.out.println(e);
            }
        }
        
        while(true) {
            System.out.println("Please enter the model. (Press enter to skip)");
            partial_model = scan.nextLine();
            if (partial_model.isEmpty())    {
                break;
            }
            try {
                PreparedStatement stmt = conn.prepareStatement(checkModel);
                String modelLike = "%" + partial_model + "%";
                stmt.setString(1, modelLike);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())  
                    break;
                else    {
                    System.out.println("[Error] Model Not Found.");
                }
            } catch(Exception e)    {
                System.out.println(e);
            }
        }
        
        while(true) {
            System.out.println("Please enter the minimum driving years of the driver. (Press enter to skip)");
            String minimum_years = scan.nextLine();
            if (minimum_years.isEmpty())    {
                minimum_dirving_years = -1;
                break;
            }
            try {
                PreparedStatement stmt = conn.prepareStatement(checkYears);
                minimum_dirving_years = Integer.parseInt(minimum_years);
                stmt.setInt(1, minimum_dirving_years);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())  
                    break;
                else    {
                    System.out.println("[Error] No Driver qualify the minimum years.");
                }
            } catch(Exception e)    {
                System.out.println(e);
            }
        }
        //select count(*) from Driver, Vehicle where Driver.VID = Vehicle.VID and Seats >= ? and Driving_Years >= ? and Vehicle.Model like BINARY "%?%" 
        try {
            if (!partial_model.isEmpty())   {
                checkDriverNo.append("and Model like BINARY ? ");
            }
            PreparedStatement stmt = conn.prepareStatement(checkDriverNo.toString());
            stmt.setInt(1, PassengerNo);
            if (minimum_dirving_years == -1)
                stmt.setInt(2, 0);
            else 
                stmt.setInt(2, minimum_dirving_years);
            if (!partial_model.isEmpty())
                stmt.setString(3, "%"+partial_model+"%");
            ResultSet rs = stmt.executeQuery();   
            if (!rs.next())
                System.out.println("No driver match all your requirement at the same time. Modify your requirement and try again.");
            else    {
                System.out.println("Your request is placed. "+ rs.getString(1)+" drivers are able to take the request.");
            }
        } catch (Exception e)   {
            System.out.println(e);
        }
        /*
        //  INSERT INTO Request(PID, Number_Of_Passengers, Start_Location, Destination, Partial_Model, Minimum_Driving_Years, Taken) VALUES (?, ?, ?, ?, ?, ?, ?)
                PreparedStatement requestStmt = conn.prepareStatement(requestSQL);
                requestStmt.setInt(1, ID);
                requestStmt.setInt(2, PassengerNo);
                requestStmt.setString(3, start_location);
                requestStmt.setString(4, destination);
                if (!partial_model.isEmpty())
                    requestStmt.setString(5, partial_model);
                requestStmt.setInt(6, minimum_dirving_years);
                requestStmt.setBoolean(7, false);
                requestStmt.execute();
                */
        try {
            PreparedStatement stmt = conn.prepareStatement(requestSQL);
            stmt.setInt(1, ID);
            stmt.setInt(2, PassengerNo);
            stmt.setString(3, start_location);
            stmt.setString(4, destination);
            if (!partial_model.isEmpty())
                stmt.setString(5, partial_model);
            else
                stmt.setString(5, null);
            if (minimum_dirving_years == -1)
                stmt.setInt(6, -1);
            else
                stmt.setInt(6, minimum_dirving_years);
            stmt.setBoolean(7, false);
            stmt.execute();
            //Test
            System.out.println("Finish inserting request!");
            
        } catch (Exception e)   {
            System.out.println(e);
        }
        
    }
    
}
