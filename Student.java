import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.ibatis.jdbc.ScriptRunner;

import oracle.jdbc.driver.*;

public class Student{
    static Connection con;
    static Statement stmt;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String argv[])
    {
        connectToDatabase();

        ScriptRunner sr = new ScriptRunner(con);

        while (true) {
            try {

                System.out.print("Enter the path to the sql file: ");

                String path = scanner.nextLine().trim();
                Reader reader = new BufferedReader(new FileReader(path));
                sr.runScript(reader);

                System.out.println("Successfully uploaded the sql file to the database");
                break;

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                System.out.println("Invalid path try again.");
            }
        }


        String choice = "0";
        while (!"6".equals(choice)) {
            System.out.println();
            System.out.println("Option 1. View table contents");
            System.out.println("Option 2. Search workshops");
            System.out.println("Option 3. Show registered students");
            System.out.println("Option 4. Register a new student");
            System.out.println("Option 5. Delete a registration");
            System.out.println("Option 6. Exit");
            System.out.print("Choose option number: ");

            choice = scanner.nextLine().trim();

            //calling each menu option
            switch (choice) {
                case "1": 
                    try {
                        viewTable();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                case "2": 
                {
                    try {
                        searchWorkshops();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } 
                    break;
                case "3": 
                    showRegisteredStudents(); 
                    break;
                case "4": 
                    registerStudent(); 
                    break;
                case "5": 
                    deleteRegistration(); 
                    break;
                case "6":
                    break;
                default:
                    System.out.println("Invalid option try again");
            }
        }


        //closing connection
        try {
            con.close();
            System.out.println("Connection closed Successfully");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // public static void queryTest(){
    //     String query = "SELECT * FROM Workshops";
    //     try {
    //         ResultSet rs = stmt.executeQuery(query);
    //         while (rs.next()) {
    //             System.out.println("Workshop ID is " + rs.getInt("WorkshopID") + ", title is " + rs.getString("Title"));
    //         }
    //         rs.close();
    //     } catch (SQLException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }

    public static void connectToDatabase()
    {
	String driverPrefixURL="jdbc:oracle:thin:@";
	String jdbc_url="artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu";
	

        System.out.print("Enter your Oracle username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter your Oracle password: ");
        String password = scanner.nextLine();
        // String username="mqazi4";
        // String password="icohoapt";

        
	
        try{
	    //Register Oracle driver
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (Exception e) {
            System.out.println("Failed to load JDBC/ODBC driver.");
            return;
        }

       try{
            System.out.println(driverPrefixURL+jdbc_url);
            con=DriverManager.getConnection(driverPrefixURL+jdbc_url, username, password);
            DatabaseMetaData dbmd=con.getMetaData();
            stmt=con.createStatement();

            System.out.println("Connected.");

            if(dbmd==null){
                System.out.println("No database meta data");
            }
            else {
                System.out.println("Database Product Name: "+dbmd.getDatabaseProductName());
                System.out.println("Database Product Version: "+dbmd.getDatabaseProductVersion());
                System.out.println("Database Driver Name: "+dbmd.getDriverName());
                System.out.println("Database Driver Version: "+dbmd.getDriverVersion());
            }
        }catch( Exception e) {e.printStackTrace();}

    }// End of connectToDatabase()



    //menu option 1
    public static void viewTable() throws SQLException {

        //input validation (case sensitive)
        String table = "";
        while (true) {
            System.out.print("Choose which table to display (Workshops/Registrations): ");
            table = scanner.nextLine().trim();

            if (table.equals("Workshops") || table.equals("Registrations")) {
                break;
            }

            System.out.println("Invalid input. Please enter 'Workshops' or 'Registrations'");
        }

        System.out.println("");
        String sql = null;

        if (table.equals("Workshops")) {
            sql = "SELECT WorkshopID, Title, Category, EventDate, Location, Capacity FROM Workshops ORDER BY WorkshopID";

        } else if (table.equals("Registrations")) {
            sql = "SELECT WorkshopID, StudentID, RegisteredOn FROM Registrations ORDER BY WorkshopID, StudentID";

        }


        //printing answer
        try (Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            printResults(rs);
        }
    }

  

    //option 2 
    public static void searchWorkshops() throws SQLException {

        //input validation
        String search = "";

        while (true) {
            System.out.print("Enter what to search Workshops by (WorkshopID/Title/Category): ");
            search = scanner.nextLine().trim();

            if (search.equals("WorkshopID") || search.equals("Title") || search.equals("Category")) {
                break;
            }

            System.out.println("Invalid input. options = 'WorkshopID', 'Title', or 'Category'");
        }

        System.out.println("");

        if (search.equals("WorkshopID")) {      //search by workshopid
            
            Integer id = null; 

            while (id == null) {

                System.out.print("Enter WorkshopID: ");
                String s = scanner.nextLine().trim();

                try {
                    id = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }

            String sql = "SELECT WorkshopID, Title, Category, EventDate, Location, Capacity FROM Workshops WHERE WorkshopID = ?";

            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        System.out.println("No results");
                    } else {
                        printResults(rs);
                    }
                }
            }
                
        } else if (search.equals("Title")) {                          //search by title or part of title (sae sensitive)
            
            System.out.print("Enter what Title should contain: ");
            String q = scanner.nextLine().trim();
            String sql = "SELECT WorkshopID, Title, Category, EventDate, Location, Capacity FROM Workshops WHERE Title LIKE ? ORDER BY WorkshopID";

            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, "%" + q + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        System.out.println("No results");
                    } else {
                        printResults(rs);
                    }
                }
            }
            
            
        } else {                                                                //search by category (case sensitive again)
            System.out.print("Enter what Category should contain: ");
            String q = scanner.nextLine().trim();
            String sql = "SELECT WorkshopID, Title, Category, EventDate, Location, Capacity FROM Workshops WHERE Category LIKE ? ORDER BY WorkshopID";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, "%" + q + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        System.out.println("No results");
                    }else {
                        printResults(rs);
                    }
                }
            }
        }
    }



    
    //menu option 3
    public static void showRegisteredStudents() {

        //input validation
        String searchby = "";

        while (true) {
            System.out.print("Search StudentID by (WorkshopID/Title): ");
            searchby = scanner.nextLine().trim();
            
            if (searchby.equals("WorkshopID") || searchby.equals("Title")) {
                break;
            }

            System.out.println("Invalid input. Please enter 'WorkshopID' or 'Title'.");
        }

        //the general query
        String sql;
        PreparedStatement ps = null;

        try {
            if (searchby.equals("WorkshopID")) { //WorkshopID

                sql = "SELECT r.StudentID FROM Workshops w JOIN Registrations r ON w.WorkshopID = r.WorkshopID WHERE w.WorkshopID = ? ORDER BY r.StudentID";

                ps = con.prepareStatement(sql);

                Integer wid = null;

                while (wid == null) {

                    System.out.print("Enter WorkshopID: ");
                    String s = scanner.nextLine().trim();

                    try {
                        wid = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer.");
                    }
                }
                ps.setInt(1, wid);

            } else {  //Title

                sql = "SELECT r.StudentID FROM Workshops w JOIN Registrations r ON w.WorkshopID = r.WorkshopID WHERE w.Title LIKE ? ORDER BY r.StudentID";

                ps = con.prepareStatement(sql);

                System.out.print("Enter what Title should contain: ");
                String title = scanner.nextLine().trim();
                ps.setString(1, "%" + title + "%");
            }


            //print and give total at the end
            try (ResultSet rs = ps.executeQuery()) {
                List<String> students = new ArrayList<>();
                while (rs.next()) {
                    students.add(rs.getString("StudentID"));
                }

                if (students.isEmpty()){
                    System.out.println("0 registered");
                } else {

                    for (String id : students) {
                        System.out.println(id);
                    }
                    System.out.println("Total: " + students.size());
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }






    //menu option 4
    public static void deleteRegistration() {

        //input validation
        Integer wid = null;

        while (wid == null) {
            System.out.print("Enter WorkshopID: ");
            String s = scanner.nextLine().trim();

            try {
                wid = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer");
            }
        }


        System.out.print("Enter StudentID: ");
        String sid = scanner.nextLine().trim();


        //confirm to delete
        String delete = "";

        while (true) {
            System.out.print("Confirm if you wanna delete (y/n): ");
            delete = scanner.nextLine().trim();

            if (delete.equals("y") || delete.equals("n")) {
                break;
            }
            System.out.println("Invalid input. Enter 'y' or 'n'");
        }

        if (delete.equals("n")) {
            System.out.println("Not Deleting anymore");
            return;
        }


        //deletion query
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM Registrations WHERE WorkshopID = ? AND StudentID = ?")) {
            ps.setInt(1, wid);
            ps.setString(2, sid);
            int n = ps.executeUpdate();

            if (n == 1) {
                System.out.println("Succesfully Deleted.");
            } else {
                System.out.println("No record found");
            }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }





    //menu option 5
    public static void registerStudent() {

        //input validation
        Integer wid = null;

        while (wid == null) {
            System.out.print("Enter WorkshopID: ");
            String s = scanner.nextLine().trim();

            try {
                wid = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }

        
        System.out.print("Enter StudentID: ");
        String sid = scanner.nextLine().trim();

        try {
            // checking capacity and if it exists
            Integer capacity = null;

            try (PreparedStatement ps = con.prepareStatement("SELECT Capacity FROM Workshops WHERE WorkshopID = ?")) {
                ps.setInt(1, wid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        capacity = rs.getInt(1);
                    }
                }
            }


            if (capacity == null) { 
                System.out.println("Workshop not found"); 
                return; 
            }

            //checking current count
            int count = 0;

            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM Registrations WHERE WorkshopID = ?")) {
                ps.setInt(1, wid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }

            if (count >= capacity) { 

                System.out.println("Workshop is full"); 
                return; 
            }

            //checking duplicate
            try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM Registrations WHERE WorkshopID = ? AND StudentID = ?")) {
                ps.setInt(1, wid);
                ps.setString(2, sid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { 
                        System.out.println("Duplicate registration found"); 
                        return; 
                    }
                }
            }

            //inserting
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO Registrations (WorkshopID, StudentID, RegisteredOn) VALUES (?, ?, ?)")) {

                ps.setInt(1, wid);
                ps.setString(2, sid);
                ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));   //learnt how to put in system date
                int n = ps.executeUpdate();
                if (n == 1) {
                System.out.println("Succesfully Registered");
                } else {
                    System.out.println("Registration failed");
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }





    //helper method to print in csv
    public static void printResults(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        //headers
        for (int i = 1; i <= columnCount; i++) {

            System.out.print(rsmd.getColumnName(i));
            if (i < columnCount) {
                System.out.print(", ");
            }
        }
        System.out.println();

        //row
        while (rs.next()) {

            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);

                if (value != null) {
                    System.out.print(value);
                } else {
                    System.out.print("NULL");
                }
                if (i < columnCount) {
                    System.out.print(", ");
                }
            }

            System.out.println();
        }
    }







    

}// End of class

