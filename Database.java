import java.sql.*;

public class Database {
    Connection conn = null;

    public Database() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:database.db");
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }
        System.out.println("Opened database successfully.");
        createPatientTable();
        createTransactionTable();
        createServiceTable();
        createProviderTable();
    }

    //            Database Creation Functions
    //---------------------------------------------------

    // Creates Patients table if it doesn't exist already.
    public void createPatientTable() throws SQLException {
        String commandString =
            "create table " +
            "Patients " + 
            "(" +
            "PATIENT_ID int NOT NULL, " +
            "NAME char(25) NOT NULL, " +
            "ADDRESS char(25) NOT NULL, " +
            "CITY char(14) NOT NULL, " +
            "STATE char(2) NOT NULL, " +
            "ZIPCODE char(5) NOT NULL, " +
            "PRIMARY KEY (PATIENT_ID)" +
            ")";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(commandString);
        }
        catch (SQLException e) {
            if(e.getMessage().equals("[SQLITE_ERROR] SQL error or "
                    + "missing database "
                    + "(table Patients already exists)")) {
                System.out.println("Patients table found.");
            }

            else {
                System.err.println(e.getClass().getName() 
                    + ": " + e.getMessage());
            }
        }
        finally {
            if(stmt != null) {
                stmt.close();
            }
        }
    }

    public void createTransactionTable() throws SQLException {
        String commandString =
            "create table " +
            "Transactions " + 
            "(" +
            "TRANSACTION_ID int NOT NULL, " +
            "DATE_TIME char(18) NOT NULL, " +
            "SERVICE_TIME char(10) NOT NULL, " +
            "PROVIDER_ID int NOT NULL, " +
            "PATIENT_ID int NOT NULL, " +
            "SERVICE_ID int NOT NULL, " +
            "COMMENT char(100), " +
            "PRIMARY KEY (TRANSACTION_ID)" +
            ")";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(commandString);
        }
        catch (SQLException e) {
            if(e.getMessage().equals("[SQLITE_ERROR] SQL error or "
                    + "missing database "
                    + "(table Transactions already exists)")) {
                System.out.println("Transactions table found.");
            }

            else {
                System.err.println(e.getClass().getName() 
                    + ": " + e.getMessage());
            }
        }
        finally {
            if(stmt != null) {
                stmt.close();
            }
        }
    }

    public void createServiceTable() throws SQLException {
        String commandString =
            "create table " +
            "Services " + 
            "(" +
            "SERVICE_ID int NOT NULL, " +
            "SERVICE_NAME char(20) NOT NULL, " +
            "SERVICE_PRICE float NOT NULL, " +
            "SERVICE_CATEGORY char(20), " +
            "PRIMARY KEY (SERVICE_ID)" +
            ")";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(commandString);
        }
        catch (SQLException e) {
            if(e.getMessage().equals("[SQLITE_ERROR] SQL error or "
                    + "missing database "
                    + "(table Services already exists)")) {
                System.out.println("Services table found.");
            }

            else {
                System.err.println(e.getClass().getName() 
                    + ": " + e.getMessage());
            }
        }
        finally {
            if(stmt != null) {
                stmt.close();
            }
        }
    }

    public void createProviderTable() throws SQLException {
        String commandString =
            "create table " +
            "Providers " + 
            "(" +
            "PROVIDER_ID int NOT NULL, " +
            "PROVIDER_NAME char(20) NOT NULL, " +
            "PROVIDER_CATEGORIES char(100), " +
            "PRIMARY KEY (PROVIDER_ID)" +
            ")";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(commandString);
        }
        catch (SQLException e) {
            if(e.getMessage().equals("[SQLITE_ERROR] SQL error or "
                    + "missing database "
                    + "(table Providers already exists)")) {
                System.out.println("Providers table found.");
            }

            else {
                System.err.println(e.getClass().getName() 
                    + ": " + e.getMessage());
            }
        }
        finally {
            if(stmt != null) {
                stmt.close();
            }
        }
    }

    public Boolean addPatient(int ID, String name, String address, String city, 
                    String state, String zipcode) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:database.db");
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }

        PreparedStatement pStatement = null;

        try {
            pStatement = conn.prepareStatement (
                "INSERT INTO Patients " +
                "VALUES (?, ?, ?, ?, ?, ?)"
            );

            pStatement.setInt(1, ID);
            pStatement.setString(2, name);
            pStatement.setString(3, address);
            pStatement.setString(4, city);
            pStatement.setString(5, state);
            pStatement.setString(6, zipcode);
            pStatement.executeUpdate();
        } catch(SQLException e) {
             System.err.println(e.getClass().getName() 
                                + ": " + e.getMessage());
             return false;
        }

        finally {
            if(pStatement != null) {
                pStatement.close();
            }
        }

        return true;
    }

    public void printPatients() throws SQLException{
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery (
                    "SELECT PATIENT_ID, NAME, ADDRESS, CITY, " + 
                    "STATE, ZIPCODE FROM Patients");
            while(rs.next()) {
                System.out.println(rs.getInt("PATIENT_ID"));
                System.out.println(rs.getString("ADDRESS"));
            }

            rs.close();

        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        finally {
            if(stmt != null) {
                stmt.close();
            }
        }
    }
}
