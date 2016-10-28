import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Database {
    Connection conn = null;

    int patientNum;
    int transactionNum;
    int serviceNum;
    int providerNum;

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

        // Get the number of rows from each table.
        patientNum = getNumberOfRows("Patients");
        transactionNum = getNumberOfRows("Transactions");
        serviceNum = getNumberOfRows("Services");
        providerNum = getNumberOfRows("Providers");
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
            "NAME varchar(25) NOT NULL, " +
            "ADDRESS varchar(25) NOT NULL, " +
            "CITY varchar(14) NOT NULL, " +
            "STATE char(2) NOT NULL, " +
            "ZIPCODE char(5) NOT NULL, " +
            "ACTIVE int NOT NULL," +
            "STANDING int NOT NULL," +
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
            "COMMENT varchar(100), " +
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
            "SERVICE_NAME varchar(20) NOT NULL, " +
            "SERVICE_PRICE float NOT NULL, " +
            "ACTIVE int NOT NULL," +
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
            "NAME varchar(25) NOT NULL, " +
            "ADDRESS varchar(25) NOT NULL, " +
            "CITY varchar(14) NOT NULL, " +
            "STATE char(2) NOT NULL, " +
            "ZIPCODE char(5) NOT NULL, " +
            "ACTIVE int NOT NULL," +
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

    private int getNumberOfRows(String tableName) throws SQLException {
        int numberOfRows = -1;
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM " 
                                             + tableName);
            numberOfRows = rs.getInt("total");
        }

        catch (SQLException e) {
                System.err.println(e.getClass().getName() 
                    + ": " + e.getMessage());
        }

        finally {
            if(stmt != null) {
                stmt.close();
            }
        }

        return numberOfRows;
    }

    // addPatient functions from Patient object and CSV.

    public int addPatient(Patient newPatient) throws SQLException {
        PreparedStatement pStatement = null;

        try {
            // See if the patient already exists.

            pStatement = conn.prepareStatement(
                "SELECT * FROM Patients WHERE NAME = ?"
            );

            pStatement.setString(1, newPatient.getName());

            ResultSet rs = null;
            rs = pStatement.executeQuery();
            Patient currentPatient = null;

            while(rs.next()) {
                currentPatient = new Patient(rs.getString("NAME"),
                        rs.getString("ADDRESS"), rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"), rs.getInt("ACTIVE"),
                        rs.getInt("STANDING"));

                if(currentPatient.equals(newPatient)) {
                    throw new AlreadyExistsException();
                }
            }

            // Otherwise, we're good!

            pStatement = conn.prepareStatement (
                "INSERT INTO Patients " +
                "VALUES (?, ?, ?, ?, ?, ?, 1, 1)"
            );

            pStatement.setInt(1, patientNum);
            pStatement.setString(2, newPatient.getName());
            pStatement.setString(3, newPatient.getAddress());
            pStatement.setString(4, newPatient.getCity());
            pStatement.setString(5, newPatient.getState());
            pStatement.setString(6, newPatient.getZipcode());
            pStatement.executeUpdate();
            patientNum++;

        } catch(SQLException e) {
             System.err.println(e.getClass().getName() 
                                + ": " + e.getMessage());
             return -1;
        }

        catch(AlreadyExistsException e) {
            System.out.println("Patient already exists.");
            return -1;
        }

        catch(InputException e) {
            System.out.println("Somehow, an invalid patient is in the " +
                    "database.");
            return -1;
        }

        finally {
            if(pStatement != null) {
                pStatement.close();
            }
        }

        return patientNum - 1;
    }

    public void addPatients(String filename) {
        String line;
        Patient currentPatient;
        int currentPatientID;
        int lineNumber = 1;

        // Fatal exception try.
        try {
            BufferedReader reader = new BufferedReader(
                                        new FileReader(filename)
                                    );

            while((line = reader.readLine()) != null) {
                String [] splitLine = line.split(",");
                // Individual line exception try.
                try {
                    currentPatient = new Patient(
                                            splitLine[0], // Name
                                            splitLine[1], // Address
                                            splitLine[2], // City
                                            splitLine[3], // State
                                            splitLine[4], // Zipcode
                                            1,            // Enrollment Status
                                            1             // Financial Standing
                                          );
                    currentPatientID = addPatient(currentPatient);

                    if(currentPatientID != -1) {
                        System.out.println("Added " + currentPatient.getName() +
                                           " to database. ID = " +
                                           currentPatientID);
                    }

                    else {
                        System.out.println("Tried to add " +
                            currentPatient.getName() + 
                            ", but patient already exists.");
                    }
                }
                catch(InputException e) {
                    System.out.println("Error for " + splitLine[0] + ": " +
                                       e.getMessage());
                }
                catch(ArrayIndexOutOfBoundsException e) {
                    System.out.println("ArrayIndexOutOfBounds exception on line " +
                            Integer.toString(lineNumber));
                }
                catch(NumberFormatException e) {
                    System.out.println("NumberFormatException on line " +
                            Integer.toString(lineNumber));
                }
                

                lineNumber++;
            }
            
        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
            return;
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        return;
    }

    public Boolean updatePatient(int ID, Patient updatePatient) throws SQLException {
        PreparedStatement pStatement = null;

        try {
            // Check if the Patient is there.

            if(!(patientExists(ID))) {
                return false;
            }

            // If it exists, we update with the updatePatient object.

            pStatement = conn.prepareStatement(
                    "UPDATE Patients " +
                    "SET " +
                    "NAME = ?, " +
                    "ADDRESS = ?, " +
                    "CITY = ?, " +
                    "STATE = ?, " +
                    "ZIPCODE = ?, " +
                    "ACTIVE = ?, " +
                    "STANDING = ? " +
                    "WHERE PATIENT_ID = ?"
            );

            pStatement.setString(1, updatePatient.getName());
            pStatement.setString(2, updatePatient.getAddress());
            pStatement.setString(3, updatePatient.getCity());
            pStatement.setString(4, updatePatient.getState());
            pStatement.setString(5, updatePatient.getZipcode());
            pStatement.setInt(6, (updatePatient.getEnrollmentStatus())? 1 : 0);
            pStatement.setInt(7, (updatePatient.getFinancialStanding())? 1 : 0 );
            pStatement.setInt(8, ID);

            pStatement.executeUpdate();
        }

        catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
            return false;
        }

        finally {
            if(pStatement != null) {
                pStatement.close();
            }
        }

        return true;
    }

    public Boolean removePatient(int ID) throws SQLException {
        Statement stmt = null;
        if (patientExists(ID)) {
            stmt = conn.createStatement();
            stmt.executeUpdate(
                   "UPDATE Patients " +
                   "SET " +
                   "ACTIVE = 0 " +
                   "WHERE PATIENT_ID = " +
                   Integer.toString(ID)
            );
            return true;
        }

        return false;
    }

    Boolean patientExists(int ID) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery (
                "SELECT * FROM Patients WHERE PATIENT_ID = " +
                Integer.toString(ID)
                );

        if(rs.next()) {
            return true;
        }

        return false;
    }

    public void printAllPatients() throws SQLException {
        Statement stmt = null;
        Patient currentPatient;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery (
                    "SELECT * FROM Patients");
            while(rs.next()) {
                currentPatient = new Patient(rs.getInt("PATIENT_ID"),
                                             rs.getString("NAME"),
                                             rs.getString("ADDRESS"),
                                             rs.getString("CITY"),
                                             rs.getString("STATE"),
                                             rs.getString("ZIPCODE"),
                                             rs.getInt("ACTIVE"),
                                             rs.getInt("STANDING"));
                System.out.println(currentPatient + "\n");
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
