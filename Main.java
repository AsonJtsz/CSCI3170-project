package proj;

import java.sql.*;
import java.util.Scanner;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {
    
    public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group4";
    public static String dbUsername = "Group4";
    public static String dbPassword = "3170group4";
    
    public static Connection connectToDataBase()    {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
            //System.out.println("Connect to DataBase Sucessfully!");
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
            case 3:
                driverMenu(conn);
                break;
            case 4:
                managerMenu(conn);
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
        
        TripsSQL.append("create table Trips(  TID Integer NOT NULL AUTO_INCREMENT,  DID Integer NOT NULL,");
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
                    checkRecord(conn);
                    break;
                case 3:
                    break outer;
                default:
                    System.out.println("Invalid input, enter again");
                    break;
            }
        }
    }
    //INSERT INTO Request(RID, PID, Number_Of_Passengers, Start_Location, Destination, Partial_Model, Minimum_Driving_Years, Taken) VALUES (NULL, 1, 1, "Mong Kok", "Lam Tin", "Honda", -1, false);
    public static void requestRide(Connection conn) {
        Scanner scan = new Scanner(System.in);
        String checkID = "select * from Passenger where PID = ?";
        String checkLocation = "select * from Taxi_Stop where Name = ?";
        String checkModel = "select * from Vehicle where Model LIKE binary ?";
        String checkYears = "select * from Driver where Driving_Years >= ?";
        String requestSQL = "INSERT INTO Request(RID, PID, Number_Of_Passengers, Start_Location, Destination, Partial_Model, Minimum_Driving_Years, Taken) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
        StringBuilder checkDriverNo = new StringBuilder();
        checkDriverNo.append("select count(*) from Driver, Vehicle where Driver.VID = Vehicle.VID and Seats >= ? and Driving_Years >= ? ");
        Boolean insert = false;
        
        
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
            if (!rs.next()) {
                System.out.println("No driver match all your requirement at the same time. Modify your requirement and try again.");
            }
            else    {
                System.out.println("Your request is placed. "+ rs.getString(1)+" drivers are able to take the request.");
                insert = true;
           }
        } catch (Exception e)   {
            System.out.println(e);
        }
        if (insert) {
            try {
                PreparedStatement stmt = conn.prepareStatement(requestSQL);
                stmt.setInt(1, ID);
                stmt.setInt(2, PassengerNo);
                stmt.setString(3, start_location);
                stmt.setString(4, destination);
                if (!partial_model.isEmpty())   {
                    stmt.setString(5, partial_model);
                }
                else    {
                    stmt.setString(5, null);
                }
                if (minimum_dirving_years == -1)    {
                    stmt.setInt(6, -1);
                }
                else    {
                    stmt.setInt(6, minimum_dirving_years);
                 }
                stmt.setBoolean(7, false);
                stmt.executeUpdate();
            } catch (Exception e)   {
                System.out.println(e);
            }
        }
        
        
    }
    
    public static void checkRecord(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        
        String checkID = "select * from Passenger where PID = ?";
        String checkLocation = "select * from Trips where Destination = ?";
        StringBuilder checkRecordSQL = new StringBuilder();
        checkRecordSQL.append("select TID, Driver.Name, Driver.VID, Model, Start_Time, End_Time, Fee, Start_Location, Destination from Trips, Driver, Vehicle ");
        checkRecordSQL.append("where Driver.VID = Vehicle.VID AND Trips.DID = Driver.DID AND PID = ? AND Start_Time >= ? AND End_Time <= ? AND Destination = ? order by Start_Time desc");
        
        int ID;
        String start_date, end_date, destination;
        
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
        
        while (true)   {
            System.out.println("Please enter the start date.");
            String input = scan.nextLine();
            if (!input.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                System.out.println("[Error]Incompatible date format");
                continue;
            } else  {
                start_date = input;
                break;
            }
        }
        
        while (true)   {
            System.out.println("Please enter the end date.");
            String input = scan.nextLine();
            if (!input.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                System.out.println("[Error]Incompatible date format");
                continue;
            } else  {
                end_date = input;
                break;
            }
        }
        
        while(true) {
            System.out.println("Please enter the destination.");
            destination = scan.nextLine();
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
        
        try {
            PreparedStatement stmt1 = conn.prepareStatement(checkRecordSQL.toString());
            stmt1.setInt(1, ID);
            stmt1.setString(2, start_date);
            stmt1.setString(3, end_date);
            stmt1.setString(4, destination);
            ResultSet rs = stmt1.executeQuery();  
            
            if (!rs.next())  {
                System.out.println("No Record Found.");
            } else  {
                System.out.println("Trip_id, Driver Name, Vehicle ID, Vehicle Model, Start, End, Fee, Start Location, Destination");
                do {
                    StringBuilder temp = new StringBuilder();
                    for (int i = 1; i <= 9; i++)    {
                        temp.append(rs.getString(i));
                        if (i != 9)
                            temp.append(", ");
                    }
                    System.out.println(temp.toString());
                
                } while (rs.next());
            }
        } catch (Exception e)   {
            System.out.println(e);
        }
    }
    
    public static void driverMenu(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        outer:
        while(true) {
            System.out.println("Driver, what would you like to do?");
            System.out.println("1. Search requests");
            System.out.println("2. Take a request");
            System.out.println("3. Finish a trip");
            System.out.println("4. Go back");
            System.out.println("Please enter[1-4]");
            int input;
            input = scan.nextInt();
            switch(input)   {
                case 1:
                    searchRequest(conn);
                    break;
                case 2:
                    takeRequest(conn);
                    break;
                case 3:
                    finishTrip(conn);
                    break;
                case 4:
                    break outer;
                default:
                    System.out.println("Invalid input, enter again");
                    break;
            }
        }
    }
    
    public static void searchRequest(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        String checkDID = "select * from Driver where DID = ?";
        String getDriverInfo = "select Driving_Years, Model, Seats from Driver, Vehicle where Driver.VID = Vehicle.VID AND DID = ?";
        StringBuilder getRequestSQL = new StringBuilder();
        getRequestSQL.append("select RID, Passenger.Name, Number_Of_Passengers, Start_Location, Destination from Request, Taxi_Stop, Passenger where ");
        getRequestSQL.append("Request.Start_Location = Taxi_Stop.Name and Request.PID = Passenger.PID and (ABS(? - Taxi_Stop.X_Coordinate) + ABS(? - Taxi_Stop.Y_Coordinate) <= ? ) ");
        getRequestSQL.append("and (? like binary CONCAT('%',Request.Partial_Model,'%') or Request.Partial_Model is null) and Number_Of_Passengers <= ? and Taken = false and Minimum_Driving_Years <= ? order by RID ");
        int DID, x_coordinate, y_coordinate, maximum_distance, driving_years = 0, Seats = 0;
        String Model = null;
        
        while(true) {
            System.out.println("Please enter your ID.");
            String input = scan.nextLine();
            if (!input.matches("[0-9]+"))   {
                System.out.println("[Error] Invalid input. Please enter digits only.");
                continue;
            }   else    {
                DID = Integer.parseInt(input);
            }
            try {
                PreparedStatement stmt = conn.prepareStatement(checkDID);
                stmt.setInt(1, DID);
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
            System.out.println("Please enter the coordinates of your location.");
            String input = scan.nextLine();
            if (!input.matches("[0-9 ]+"))   {
                System.out.println("[Error] Invalid input. Please enter digits or space only.");
                continue;
            }   else    {
                String[] temp = input.split(" ");
                x_coordinate = Integer.parseInt(temp[0]);
                y_coordinate = Integer.parseInt(temp[1]);
                break;
            }
        }
        
        while(true) {
            System.out.println("Please enter the maximum distance from you to passenger.");
            String input = scan.nextLine();
            if (!input.matches("[0-9]+"))   {
                System.out.println("[Error] Invalid input. Please enter digits only.");
                continue;
            }   else    {
                maximum_distance = Integer.parseInt(input);
                break;
            }
        }
        
        try {
            PreparedStatement stmt = conn.prepareStatement(getDriverInfo);
            stmt.setInt(1, DID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            driving_years = Integer.parseInt(rs.getString(1));
            Model = rs.getString(2);
            Seats = Integer.parseInt(rs.getString(3));      
        } catch (Exception e)   {
            System.out.println(e);
        }
        
        try {
            PreparedStatement stmt = conn.prepareStatement(getRequestSQL.toString());
            stmt.setInt(1, x_coordinate);
            stmt.setInt(2, y_coordinate);
            stmt.setInt(3, maximum_distance);
            stmt.setString(4, Model);
            stmt.setInt(5, Seats);
            stmt.setInt(6, driving_years);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())  {
                System.out.println("request ID, passenger name, num of passengers, start location, destination");
                do {
                    StringBuilder temp = new StringBuilder();
                    for (int i = 1; i <= 5; i++)    {
                        temp.append(rs.getString(i));
                        if (i != 5)
                            temp.append(", ");
                    }
                    System.out.println(temp.toString());
                } while (rs.next());
                
            } else  {
                System.out.println("No Request Available");
            }
            
        } catch (Exception e)   {
            System.out.println(e);
        }
    }
    //insert into Trips(TID, DID, PID, Start_Time, End_Time, Start_Location, Destination, Fee) values (null, 1, 1, "2018-1-1 00:00:01", NULL, "Mong Kok", "Sham Shui Po", 100);
    
    //select RID from Request, Passenger where Request.PID = Passenger.PID
    //and ("Honda" like binary CONCAT('%',Request.Partial_Model,'%') or Request.Partial_Model is null) and Number_Of_Passengers <= 8 and Taken = false and Minimum_Driving_Years <= 5 order by RID;
     
    
    public static void takeRequest(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        String checkDID = "select * from Driver where DID = ? ";
        String checkUnfinished = "select * from Trips where DID = ? and End_Time is null";
        String getDriverInfo = "select Driving_Years, Model, Seats from Driver, Vehicle where Driver.VID = Vehicle.VID AND DID = ?";
        StringBuilder getRequest = new StringBuilder();
        getRequest.append("select RID from Request, Passenger where Request.PID = Passenger.PID ");
        getRequest.append("and (? like binary CONCAT('%',Request.Partial_Model,'%') or Request.Partial_Model is null) ");
        getRequest.append("and Number_Of_Passengers <= ? and Taken = false and Minimum_Driving_Years <= ? order by RID ;");
        String requestInfo = "select Request.PID, Start_Location, Destination, Name from Request, Passenger where Passenger.PID = Request.PID and RID = ? ;";
        String insertTrip = "insert into Trips(TID, DID, PID, Start_Time, End_Time, Start_Location, Destination, Fee) values (null, ?, ?, ?, CAST(NULL AS DATETIME), ?, ?, NULL);";
        
        boolean takeRequest = false;
        boolean anyAvailableRequest = false;
        boolean insertTrips = false;
        int DID, driving_years = 0, Seats = 0, RID = 0, PID = 0;
        String Model = null, start_time, start_location = null, destination = null, passengerName = null;
        List<Integer> requestAvailable = new ArrayList<>();
        
        while(true) {
            System.out.println("Please enter your ID.");
            String input = scan.nextLine();
            if (!input.matches("[0-9]+"))   {
                System.out.println("[Error] Invalid input. Please enter digits only.");
                continue;
            }   else    {
                DID = Integer.parseInt(input);
            }
            try {
                PreparedStatement stmt = conn.prepareStatement(checkDID);
                stmt.setInt(1, DID);
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
        
        try {
            PreparedStatement stmt = conn.prepareStatement(checkUnfinished);
            stmt.setInt(1, DID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())  {
                System.out.println("[Error] you have unfinished trips.");
            } else  {
                //System.out.println("you can take a request");
                takeRequest = true;
            }
        } catch (Exception e)   {
            System.out.println(e);
        }
        
        if (takeRequest)    {
            try {
                PreparedStatement stmt = conn.prepareStatement(getDriverInfo);
                stmt.setInt(1, DID);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                driving_years = Integer.parseInt(rs.getString(1));
                Model = rs.getString(2);
                Seats = Integer.parseInt(rs.getString(3));      
            } catch (Exception e)   {
                System.out.println(e);
            }
        }
        
        if (takeRequest)    {
            try {
                PreparedStatement stmt = conn.prepareStatement(getRequest.toString());
                stmt.setString(1, Model);
                stmt.setInt(2, Seats);
                stmt.setInt(3, driving_years);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())  {
                    do {
                        requestAvailable.add(Integer.parseInt(rs.getString(1)));
                    } while (rs.next());
                    anyAvailableRequest = true;
                } else  {
                    System.out.println("No Request Available");
                }
                
            } catch(Exception e)    {
                System.out.println(e);
            }
            
        }
        
        if (anyAvailableRequest)    {
            while(true) {
                System.out.println("Please enter the request ID.");
                String input = scan.nextLine();
                if (!input.matches("[0-9]+"))   {
                    System.out.println("[Error] Invalid input. Please enter digits only.");
                    continue;
                }   else    {
                    RID = Integer.parseInt(input);
                    if (requestAvailable.contains(RID)) {
                        insertTrips = true;
                        break;
                    } 
                }
            }
        }
        
        if (insertTrips)  {
            try {
                PreparedStatement stmt = conn.prepareStatement(requestInfo);
                stmt.setInt(1, RID);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                PID = Integer.parseInt(rs.getString(1));
                start_location = rs.getString(2);
                destination = rs.getString(3);
                passengerName = rs.getString(4);
                
            } catch (Exception e)   {
                System.out.println(e);
            }
        }
        
        if (insertTrips)    {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                start_time = sdf.format(timestamp).toString();
                PreparedStatement stmt = conn.prepareStatement(insertTrip, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, DID);
                stmt.setInt(2, PID);
                stmt.setTimestamp(3,  Timestamp.valueOf(start_time));
                stmt.setString(4, start_location);
                stmt.setString(5, destination);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                rs.next();
                int TID = rs.getInt(1);
                
                System.out.println("Trip ID, Passenger name, Start");
                StringBuilder temp = new StringBuilder();
                temp.append(TID);
                temp.append(", ");
                temp.append(passengerName);
                temp.append(", ");
                temp.append(start_time);
                System.out.println(temp.toString());
            } catch (Exception e)   {
                System.out.println(e);
            }
        }  
    }
    
    public static void finishTrip(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        String checkDID = "select * from Driver where DID = ? ";
        String getTrip = "Select TID, Trips.PID, Start_Time, Name from Trips, Passenger where Trips.PID = Passenger.PID and DID = ? and End_Time is null ";
        String updateTrip = "update Trips set Fee = ? and End_Time = ? where TID = ? ; ";
        int DID, TID = 0;
        String start_time = null, end_time, passengerName = null;
        
        while(true) {
            System.out.println("Please enter your ID.");
            String input = scan.nextLine();
            if (!input.matches("[0-9]+"))   {
                System.out.println("[Error]ID only contains digits");
                continue;
            }   else    {
                DID = Integer.parseInt(input);
            }
            try {
                PreparedStatement stmt = conn.prepareStatement(checkDID);
                stmt.setInt(1, DID);
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
        boolean getOut = true;
        try {
            PreparedStatement stmt = conn.prepareStatement(getTrip);
            stmt.setInt(1, DID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())  {
                StringBuilder temp = new StringBuilder();
                for (int i = 1; i <= 3; i++)    {
                    temp.append(rs.getString(i));
                    if (i != 3)
                        temp.append(", ");
                }
                TID = Integer.parseInt(rs.getString(1));
                start_time = rs.getString(3);
                passengerName = rs.getString(4);
                System.out.println("Trip ID, Passenger ID, Start");
                System.out.println(temp.toString());
            } else {
                System.out.println("[Error] No unfinished trip found");
                getOut = false;
                
            }
        } catch(Exception e)    {
            System.out.println(e);
        }
        
        
        boolean finish = false;
        if (getOut) {
            while(true) {
                System.out.println("Do you wish to finish the trip? [y/n]");
                String input = scan.nextLine();
                if (input.equals("y")||input.equals("n"))   {
                    if (input.equals("y"))
                        finish = true;
                    break;
                }   else    {
                    System.out.println("[Error]Input must be y/n");
                }
            } 
        }

        if (finish) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                end_time = sdf.format(timestamp).toString();
                java.util.Date startTime = sdf.parse(start_time);
                java.util.Date finishTime = sdf.parse(end_time);
                double duration = finishTime.getTime() - startTime.getTime();
                int fee = (int)(duration/1000/60);
                PreparedStatement stmt = conn.prepareStatement(updateTrip);
                stmt.setInt(1, fee);
                stmt.setTimestamp(2,  Timestamp.valueOf(end_time));
                stmt.setInt(3, TID);
                stmt.executeUpdate();
                
                System.out.println("Trip ID , Passenger name, Start, End, Fee");
                StringBuilder temp = new StringBuilder();
                temp.append(TID);
                temp.append(", ");
                temp.append(passengerName);
                temp.append(", ");
                temp.append(start_time);
                temp.append(", ");
                temp.append(end_time);
                temp.append(", ");
                temp.append(fee);
                System.out.println(temp.toString());
            } catch (Exception e)   {
                System.out.println(e);
            }
        }
    }
    
    public static void managerMenu(Connection conn) {
        Scanner scan = new Scanner(System.in);
        
        outer:
        while(true) {
            System.out.println("Manager, what would you like to do?");
            System.out.println("1. Find trips");
            System.out.println("2. Go back");
            System.out.println("Please enter [1-2]");
            int input = scan.nextInt();
            switch(input)   {
                case 1:
                    findTrips(conn);
                    break;
                case 2:
                    break outer;
            }
        }
    }
    //select TID, Driver.Name, Passenger.Name, Start_Location, Destination, Start_Time, End_Time
    //from Trips, Driver, Passenger, Taxi_Stop t1, Taxi_Stop t2 
    //where Trips.DID = Driver.DID and Trips.PID = Passenger.PID and End_Time is not null 
    //Trips.Start_Location = t1.Name and Trips.Destination = t2.Name 
    //and ABS(t1.X_Coordinate-t2.X_Coordinate) + ABS(t1.Y_Coordinate-t2.Y_Coordinate) between 100 and 120;
    public static void findTrips(Connection conn)   {
        Scanner scan = new Scanner(System.in);
        
        StringBuilder finishedTrips = new StringBuilder();
        finishedTrips.append("select TID, Driver.Name, Passenger.Name, Start_Location, Destination, Start_Time, End_Time ");
        finishedTrips.append("from Trips, Driver, Passenger, Taxi_Stop t1, Taxi_Stop t2 where Trips.DID = Driver.DID ");
        finishedTrips.append("and End_Time is not null ");
        finishedTrips.append("and Trips.PID = Passenger.PID and Trips.Start_Location = t1.Name and Trips.Destination = t2.Name ");
        finishedTrips.append("and ABS(t1.X_Coordinate-t2.X_Coordinate) + ABS(t1.Y_Coordinate-t2.Y_Coordinate) between ? and ? ;");
        
        int minDist = 0, maxDist = 0;
        while(true) {
            System.out.println("Please enter the minimum traveling distance.");
            String temp = scan.nextLine();
            if (!temp.matches("[0-9]+"))    {
                System.out.println("Please enter digits only");
                continue;
            } else  {
                minDist = Integer.parseInt(temp);
                break;
            }
        }
        
        while(true) {
            System.out.println("Please enter the maximum traveling distance.");
            String temp = scan.nextLine();
            if (!temp.matches("[0-9]+"))    {
                System.out.println("Please enter digits only");
                continue;
            } else  {
                maxDist = Integer.parseInt(temp);
                break;
            }
        }

        try{
            PreparedStatement stmt = conn.prepareStatement(finishedTrips.toString());
            stmt.setInt(1, minDist);
            stmt.setInt(2, maxDist);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())  {
                System.out.println("trip id, driver name, passenger name, start location, destination, duration");
                do {
                    String start_time = rs.getString(6);
                    String end_time = rs.getString(7);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                    java.util.Date startTime = sdf.parse(start_time);
                    java.util.Date finishTime = sdf.parse(end_time);
                    double timeDiff = finishTime.getTime() - startTime.getTime();
                    int duration = (int)(timeDiff/1000/60);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= 5; i++)    {
                        sb.append(rs.getString(i));
                        sb.append(", ");
                    }
                    sb.append(duration);
                    System.out.println(sb.toString());
                    
                } while (rs.next());
                
            } else  {
                System.out.println("No finished trips satisfy your requirement.");
            }
            
            
        } catch (Exception e)   {
            System.out.println(e);
        }
        
    }
}
