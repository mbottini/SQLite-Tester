import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Database {
    Connection conn = null;

    // Current ID numbers that will be assigned to the next entry that gets
    // added.
    int patientNum;
    int transactionNum;
    int serviceNum;
    int providerNum;
    int consultNum;

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
        consultNum = getHighestTransaction() + 1;
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
            "ENROLLMENT int NOT NULL," +
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
            "SERVICE_DATE char(10) NOT NULL, " +
            "PROVIDER_ID int NOT NULL, " +
            "PATIENT_ID int NOT NULL, " +
            "SERVICE_ID int NOT NULL, " +
            "CONSULT_ID int NOT NULL, " +
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
            "NAME varchar(20) NOT NULL, " +
            "PRICE float NOT NULL, " +
            "ENROLLMENT int NOT NULL," +
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
            "ENROLLMENT int NOT NULL," +
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
                        rs.getString("ZIPCODE"), rs.getInt("ENROLLMENT"),
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

            if(!(entryExists("Patients", ID))) {
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
                    "ENROLLMENT = ?, " +
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
        if (entryExists("Patients", ID)) {
            stmt = conn.createStatement();
            stmt.executeUpdate(
                   "UPDATE Patients " +
                   "SET " +
                   "ENROLLMENT = 0 " +
                   "WHERE PATIENT_ID = " +
                   Integer.toString(ID)
            );
            return true;
        }

        return false;
    }

    private Boolean entryExists(String tableName, int ID) throws SQLException {
        Statement stmt = conn.createStatement();
        String column = tableName.substring(0, tableName.length() - 1);
        column = column.toUpperCase();
        column += "_ID";

        ResultSet rs = stmt.executeQuery(
            "SELECT 1 FROM " + tableName + " WHERE " +
            column + " = " + Integer.toString(ID));

        if(rs.next()) {
            return true;
        }

        return false;
    }

    private Boolean entryExistsAndIsActive(String tableName, int ID) 
        throws SQLException {
        Statement stmt = conn.createStatement();
        String column = tableName.substring(0, tableName.length() - 1);
        column = column.toUpperCase();
        column += "_ID";

        ResultSet rs = stmt.executeQuery(
            "SELECT 1 FROM " + tableName + " WHERE " +
            column + " = " + Integer.toString(ID) +
            " AND ENROLLMENT = 1");

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
                                             rs.getInt("ENROLLMENT"),
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

    public int addProvider(Provider newProvider) throws SQLException {
        PreparedStatement pStatement = null;

        try {
            // See if the provider already exists.

            pStatement = conn.prepareStatement(
                "SELECT * FROM Providers WHERE NAME = ?"
            );

            pStatement.setString(1, newProvider.getName());

            ResultSet rs = null;
            rs = pStatement.executeQuery();
            Provider currentProvider = null;

            while(rs.next()) {
                currentProvider = new Provider(rs.getString("NAME"),
                        rs.getString("ADDRESS"), rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"), rs.getInt("ENROLLMENT"));

                if(currentProvider.equals(newProvider)) {
                    throw new AlreadyExistsException();
                }
            }

            // Otherwise, we're good!

            pStatement = conn.prepareStatement (
                "INSERT INTO Providers " +
                "VALUES (?, ?, ?, ?, ?, ?, 1)"
            );

            pStatement.setInt(1, providerNum);
            pStatement.setString(2, newProvider.getName());
            pStatement.setString(3, newProvider.getAddress());
            pStatement.setString(4, newProvider.getCity());
            pStatement.setString(5, newProvider.getState());
            pStatement.setString(6, newProvider.getZipcode());
            pStatement.executeUpdate();
            providerNum++;

        } catch(SQLException e) {
             System.err.println(e.getClass().getName() 
                                + ": " + e.getMessage());
             return -1;
        }

        catch(AlreadyExistsException e) {
            System.out.println("Provider already exists.");
            return -1;
        }

        catch(InputException e) {
            System.out.println("Somehow, an invalid provider is in the " +
                    "database.");
            return -1;
        }

        finally {
            if(pStatement != null) {
                pStatement.close();
            }
        }

        return providerNum - 1;
    }

    public void addProviders(String filename) {
        String line;
        Provider currentProvider;
        int currentProviderID;
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
                    currentProvider = new Provider(
                                            splitLine[0], // Name
                                            splitLine[1], // Address
                                            splitLine[2], // City
                                            splitLine[3], // State
                                            splitLine[4], // Zipcode
                                            1             // Enrollment Status
                                          );
                    currentProviderID = addProvider(currentProvider);

                    if(currentProviderID != -1) {
                        System.out.println("Added " + currentProvider.getName() +
                                           " to database. ID = " +
                                           currentProviderID);
                    }

                    else {
                        System.out.println("Tried to add " +
                            currentProvider.getName() + 
                            ", but provider already exists.");
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
    
    public Boolean updateProvider(int ID, Provider updateProvider) throws SQLException {
        PreparedStatement pStatement = null;

        try {
            // Check if the Patient is there.

            if(!(entryExists("Providers", ID))) {
                return false;
            }

            // If it exists, we update with the updatePatient object.

            pStatement = conn.prepareStatement(
                    "UPDATE Providers " +
                    "SET " +
                    "NAME = ?, " +
                    "ADDRESS = ?, " +
                    "CITY = ?, " +
                    "STATE = ?, " +
                    "ZIPCODE = ?, " +
                    "ENROLLMENT = ?, " +
                    "WHERE PROVIDER_ID = ?"
            );

            pStatement.setString(1, updateProvider.getName());
            pStatement.setString(2, updateProvider.getAddress());
            pStatement.setString(3, updateProvider.getCity());
            pStatement.setString(4, updateProvider.getState());
            pStatement.setString(5, updateProvider.getZipcode());
            pStatement.setInt(6, (updateProvider.getEnrollmentStatus())? 1 : 0);
            pStatement.setInt(7, ID);

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

    public Boolean removeProvider(int ID) throws SQLException {
        Statement stmt = null;
        if (entryExists("Providers", ID)) {
            stmt = conn.createStatement();
            stmt.executeUpdate(
                   "UPDATE Providers " +
                   "SET " +
                   "ENROLLMENT = 0 " +
                   "WHERE PROVIDER_ID = " +
                   Integer.toString(ID)
            );
            return true;
        }

        return false;
    }

    public void printAllProviders() throws SQLException {
        Statement stmt = null;
        Provider currentProvider;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery (
                    "SELECT * FROM Providers");
            while(rs.next()) {
                currentProvider = new Provider(rs.getInt("PROVIDER_ID"),
                                             rs.getString("NAME"),
                                             rs.getString("ADDRESS"),
                                             rs.getString("CITY"),
                                             rs.getString("STATE"),
                                             rs.getString("ZIPCODE"),
                                             rs.getInt("ENROLLMENT"));
                System.out.println(currentProvider + "\n");
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
    public int addService(Service newService) throws SQLException {
        PreparedStatement pStatement = null;

        try {
            // See if the provider already exists.

            pStatement = conn.prepareStatement(
                "SELECT * FROM Services WHERE NAME = ?"
            );

            pStatement.setString(1, newService.getName());

            ResultSet rs = null;
            rs = pStatement.executeQuery();
            Service currentService = null;

            while(rs.next()) {
                currentService = new Service(rs.getString("NAME"),
                        rs.getFloat("PRICE"), rs.getInt("ENROLLMENT"));

                if(currentService.equals(newService)) {
                    throw new AlreadyExistsException();
                }
            }

            // Otherwise, we're good!

            pStatement = conn.prepareStatement (
                "INSERT INTO Services " +
                "VALUES (?, ?, ?, 1)"
            );

            pStatement.setInt(1, serviceNum);
            pStatement.setString(2, newService.getName());
            pStatement.setFloat(3, newService.getPrice());

            pStatement.executeUpdate();
            serviceNum++;

        } catch(SQLException e) {
             System.err.println(e.getClass().getName() 
                                + ": " + e.getMessage());
             return -1;
        }

        catch(AlreadyExistsException e) {
            System.out.println("Service already exists.");
            return -1;
        }

        catch(InputException e) {
            System.out.println("Somehow, an invalid service is in the " +
                    "database.");
            return -1;
        }

        finally {
            if(pStatement != null) {
                pStatement.close();
            }
        }

        return serviceNum - 1;
    }

    public void addServices(String filename) {
        String line;
        Service currentService;
        int currentServiceID;
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
                    currentService = new Service(
                                        splitLine[0],                  // Name
                                        Float.parseFloat(splitLine[1]),// Price
                                        1                              // Active
                                     );
                    currentServiceID = addService(currentService);

                    if(currentServiceID != -1) {
                        System.out.println("Added " + currentService.getName() +
                                           " to database. ID = " +
                                           currentServiceID);
                    }

                    else {
                        System.out.println("Tried to add " +
                            currentService.getName() + 
                            ", but service already exists.");
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
    
    public Boolean updateService(int ID, Service updateService) throws SQLException {
        PreparedStatement pStatement = null;

        try {
            // Check if the Patient is there.

            if(!(entryExists("Services", ID))) {
                return false;
            }

            // If it exists, we update with the updatePatient object.

            pStatement = conn.prepareStatement(
                    "UPDATE Services " +
                    "SET " +
                    "NAME = ?, " +
                    "PRICE = ?, " +
                    "ENROLLMENT = ?, " +
                    "WHERE SERVICE_ID = ?"
            );

            pStatement.setString(1, updateService.getName());
            pStatement.setFloat(2, updateService.getPrice());
            pStatement.setInt(3, (updateService.getActive()? 1 : 0));

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

    public Boolean removeService(int ID) throws SQLException {
        Statement stmt = null;
        if (entryExists("Services", ID)) {
            stmt = conn.createStatement();
            stmt.executeUpdate(
                   "UPDATE Services " +
                   "SET " +
                   "ENROLLMENT = 0 " +
                   "WHERE SERVICE_ID = " +
                   Integer.toString(ID)
            );
            return true;
        }

        return false;
    }

    public void printAllServices() throws SQLException {
        Statement stmt = null;
        Service currentService;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery (
                    "SELECT * FROM Services");
            while(rs.next()) {
                currentService = new Service(rs.getInt("SERVICE_ID"),
                                             rs.getString("NAME"),
                                             rs.getFloat("PRICE"),
                                             rs.getInt("ENROLLMENT"));
                System.out.println(currentService + "\n");
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

    public int addTransaction(Transaction newTransaction) throws SQLException {
        PreparedStatement pStatement = null;
        Statement stmt = null;

        try {
            // We need to ensure that the Provider, Patient, and Service IDs
            // exist.

            if(!entryExistsAndIsActive("Patients", newTransaction.getPatientID())) {
                return -1;
            }

            if(!entryExistsAndIsActive("Providers", newTransaction.getProviderID())) {
                return -1;
            }

            if(!entryExistsAndIsActive("Services", newTransaction.getServiceID())) {
                return -1;
            }

            // Otherwise, we're good!

            pStatement = conn.prepareStatement (
                "INSERT INTO Transactions " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );

            pStatement.setInt(1, transactionNum);
            pStatement.setString(2, newTransaction.getDateTime());
            pStatement.setString(3, toSQLDate(newTransaction.getServiceDate()));
            pStatement.setInt(4, newTransaction.getProviderID());
            pStatement.setInt(5, newTransaction.getPatientID());
            pStatement.setInt(6, newTransaction.getServiceID());
            pStatement.setInt(7, newTransaction.getConsultID());
            pStatement.setString(8, newTransaction.getComment());

            pStatement.executeUpdate();
            transactionNum++;

        } catch(SQLException e) {
             System.err.println(e.getClass().getName() 
                                + ": " + e.getMessage());
             return -1;
        }

        finally {
            if(pStatement != null) {
                pStatement.close();
            }
        }

        return transactionNum - 1;
    }

    public void addTransactions(String filename) {
        String line;
        Transaction currentTransaction;
        int currentTransactionID;
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
                    currentTransaction = new Transaction(
                            splitLine[0],                      // Service Date
                            Integer.parseInt(splitLine[1]),    // Provider ID 
                            Integer.parseInt(splitLine[2]),    // Patient ID
                            Integer.parseInt(splitLine[3]),    // Service ID
                            Integer.parseInt(splitLine[4]),    // Consult ID
                            splitLine[5]                       // Comment
                                     );
                    currentTransactionID = addTransaction(currentTransaction);

                    if(currentTransactionID != -1) {
                        System.out.println("Added " +
                                currentTransaction.getProviderID() +
                                " -> " +
                                currentTransaction.getPatientID() + 
                                "to database. ID = " + currentTransactionID);
                    }
                }
                catch(InputException e) {
                    System.out.println("Error for " + 
                           splitLine[1] + " -> " + splitLine[2] + ": " +
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
    
    public Boolean updateTransaction(int ID, Transaction updateTransaction) throws SQLException {
        PreparedStatement pStatement = null;

        try {
            // Check if the Transaction is there.

            if(!(entryExists("Transactions", ID))) {
                return false;
            }

            // If it exists, we update with the updatePatient object.

            pStatement = conn.prepareStatement(
                    "UPDATE Transactions " +
                    "SET " +
                    "DATE_TIME = ?, " +
                    "SERVICE_DATE = ?, " +
                    "PROVIDER_ID = ?, " +
                    "PATIENT_ID = ?, " +
                    "SERVICE_ID = ?, " +
                    "CONSULT_ID = ?, " +
                    "COMMENT = ?, " +
                    "WHERE TRANSACTION_ID = ?"
            );

            pStatement.setString(1, updateTransaction.getDateTime());
            pStatement.setString(2, toSQLDate(updateTransaction.getServiceDate()));
            pStatement.setInt(3, updateTransaction.getProviderID());
            pStatement.setInt(4, updateTransaction.getPatientID());
            pStatement.setInt(5, updateTransaction.getServiceID());
            pStatement.setInt(6, updateTransaction.getConsultID());
            pStatement.setString(7, updateTransaction.getComment());

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

    public void printAllTransactions() throws SQLException {
        Statement stmt = null;
        Transaction currentTransaction;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery (
                    "SELECT * FROM Transactions");
            while(rs.next()) {
                currentTransaction = new Transaction(
                        rs.getInt("TRANSACTION_ID"),
                        rs.getString("DATE_TIME"),
                        toOutputDate(rs.getString("SERVICE_DATE")),
                        rs.getInt("PROVIDER_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getInt("SERVICE_ID"),
                        rs.getInt("CONSULT_ID"),
                        rs.getString("COMMENT"));
                System.out.println(currentTransaction + "\n");
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

    private int getHighestTransaction() throws SQLException {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                "SELECT count(*) FROM Transactions"
            );

            if(rs.getInt(1) == 0) {
                return -1;
            }

            rs = stmt.executeQuery(
                "SELECT MAX(CONSULT_ID) FROM Transactions"
            );

            return rs.getInt(1);
        }

        finally {
            if(stmt != null) {
                stmt.close();
            }
        }
    }       

    private Vector<Entity> getEntityByID(String table, int ID) throws SQLException {
        String IDColumn = table.toUpperCase();
        IDColumn = IDColumn.substring(0, IDColumn.length() - 1) + "_ID";
        System.out.println("Inside getEntityByID: " + IDColumn);

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + table + " WHERE " + IDColumn + " = " +
            Integer.toString(ID)
        );

        Vector<Entity> returnVec = new Vector<Entity>();

        // Because the function can build a vector of Patients or Providers, we
        // need to have different conditional blocks for whether we grabbed from
        // the Patient row or the Provider row.
        while(rs.next()) {
            try {
                if(IDColumn.matches("PATIENT_ID")) {
                    returnVec.add( new Patient(
                        rs.getInt(IDColumn),
                        rs.getString("NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"),
                        rs.getInt("ENROLLMENT"),
                        rs.getInt("STANDING")
                        )
                    );
                }

                if(IDColumn.matches("PROVIDER_ID")) {
                    returnVec.add( new Provider(
                        rs.getInt(IDColumn),
                        rs.getString("NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"),
                        rs.getInt("ENROLLMENT")
                        )
                    );
                }
            }
            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    private Vector<Entity> getEntityByString(String table, String column, 
            String criteria) throws SQLException {
        PreparedStatement pStatement = conn.prepareStatement(
            "SELECT * FROM " + table + " WHERE " + column + " = ?"
        );
        pStatement.setString(1, criteria);

        ResultSet rs = pStatement.executeQuery();

        Vector<Entity> returnVec = new Vector<Entity>();

        String IDColumn = table.toUpperCase();
        IDColumn = IDColumn.substring(0, IDColumn.length() - 1) +
            "_ID";

        // Because the function can build a vector of Patients or Providers, we
        // need to have different conditional blocks for whether we grabbed from
        // the Patient row or the Provider row.
        while(rs.next()) {
            try {
                if(IDColumn.matches("PATIENT_ID")) {
                    returnVec.add( new Patient(
                        rs.getInt(IDColumn),
                        rs.getString("NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"),
                        rs.getInt("ENROLLMENT"),
                        rs.getInt("STANDING")
                        )
                    );
                }

                if(IDColumn.matches("PROVIDER_ID")) {
                    returnVec.add( new Provider(
                        rs.getInt(IDColumn),
                        rs.getString("NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"),
                        rs.getInt("ENROLLMENT")
                        )
                    );
                }
            }
            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    // All of the database getter functions, which execute the private getter
    // functions listed above with various tables, columns, and criteria.

    public Vector<Entity> getPatientByID(int ID) throws SQLException {
        return getEntityByID("Patients", ID);
    }

    public Vector<Entity> getPatientsByName(String name) throws SQLException {
        return getEntityByString("Patients", "NAME", name);
    }

    public Vector<Entity> getPatientsByAddress(String address) 
        throws SQLException {
        return getEntityByString("Patients", "ADDRESS", address);
    }

    public Vector<Entity> getPatientsByCity(String city) throws SQLException {
        return getEntityByString("Patients", "CITY", city);
    }

    public Vector<Entity> getPatientsByState(String state) throws SQLException {
        return getEntityByString("Patients", "STATE", state);
    }

    public Vector<Entity> getPatientsByZipcode(String zipcode)
        throws SQLException {
        return getEntityByString("Patients", "ZIPCODE", zipcode);
    }

    public Vector<Entity> getProviderByID(int ID) throws SQLException {
        return getEntityByID("Providers", ID);
    }

    public Vector<Entity> getProvidersByName(String name) throws SQLException {
        return getEntityByString("Providers", "NAME", name);
    }

    public Vector<Entity> getProvidersByAddress(String address)
        throws SQLException {
        return getEntityByString("Providers", "ADDRESS", address);
    }

    public Vector<Entity> getProvidersByCity(String city) throws SQLException {
        return getEntityByString("Providers", "CITY", city);
    }

    public Vector<Entity> getProvidersByState(String state) throws SQLException {
        return getEntityByString("Providers", "STATE", state);
    }

    public Vector<Entity> getProvidersByZipcode(String zipcode)
        throws SQLException {
        return getEntityByString("Providers", "ZIPCODE", zipcode);
    }

    // Since Services and Transactions are not Entity objects, we have to
    // manually define them. Unfortunately.

    public Vector<Service> getServiceByID(int ID) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + "Services" + " WHERE " + "SERVICE_ID = " +
            Integer.toString(ID)
        );

        Vector<Service> returnVec = new Vector<Service>();

        while(rs.next()) {
            try {
                    returnVec.add( new Service(
                        rs.getInt("SERVICE_ID"),
                        rs.getString("NAME"),
                        rs.getFloat("PRICE"),
                        rs.getInt("ENROLLMENT")
                        )
                    );
                }
            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    public Vector<Service> getServiceByName(String name) throws SQLException {
        PreparedStatement pStatement = conn.prepareStatement(
            "SELECT * FROM Services WHERE NAME = ?"
        );

        pStatement.setString(1, name);
        ResultSet rs = pStatement.executeQuery();

        Vector<Service> returnVec = new Vector<Service>();

        while(rs.next()) {
            try {
                    returnVec.add( new Service(
                        rs.getInt("SERVICE_ID"),
                        rs.getString("NAME"),
                        rs.getFloat("PRICE"),
                        rs.getInt("ENROLLMENT")
                        )
                    );
                }
            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    public Vector<Service> getServiceByPrice(float price) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + "Services" + " WHERE " + "PRICE = " +
            Float.toString(price)
        );

        Vector<Service> returnVec = new Vector<Service>();

        while(rs.next()) {
            try {
                    returnVec.add( new Service(
                        rs.getInt("SERVICE_ID"),
                        rs.getString("NAME"),
                        rs.getFloat("PRICE"),
                        rs.getInt("ENROLLMENT")
                        )
                    );
                }
            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    // Since so many Transaction columns are ints, we can create a private
    // function that specifies the column that it will search by.

    private Vector<Transaction> getTransactionByInt(String column, 
            int criteria) throws SQLException {
        PreparedStatement pStatement = conn.prepareStatement(
            "SELECT * FROM " + "Transactions" + " WHERE " + column + " = ?"
        );
        pStatement.setInt(1, criteria);

        ResultSet rs = pStatement.executeQuery();

        Vector<Transaction> returnVec = new Vector<Transaction>();

        while(rs.next()) {
            try {
                returnVec.add( new Transaction(
                        rs.getInt("TRANSACTION_ID"),
                        rs.getString("DATE_TIME"),
                        toOutputDate(rs.getString("SERVICE_DATE")),
                        rs.getInt("PROVIDER_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getInt("SERVICE_ID"),
                        rs.getInt("CONSULT_ID"),
                        rs.getString("COMMENT")
                        )
                    );
            }

            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    // Same thing with DateTime and ServiceDate.
    private Vector<Transaction> getTransactionByString(String column, 
            String criteria) throws SQLException {
        PreparedStatement pStatement = conn.prepareStatement(
            "SELECT * FROM " + "Transactions" + " WHERE " + 
            column + " = ?"
        );
        pStatement.setString(1, criteria);

        ResultSet rs = pStatement.executeQuery();

        Vector<Transaction> returnVec = new Vector<Transaction>();

        while(rs.next()) {
            try {
                returnVec.add( new Transaction(
                        rs.getInt("TRANSACTION_ID"),
                        rs.getString("DATE_TIME"),
                        toOutputDate(rs.getString("SERVICE_DATE")),
                        rs.getInt("PROVIDER_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getInt("SERVICE_ID"),
                        rs.getInt("CONSULT_ID"),
                        rs.getString("COMMENT")
                        )
                    );
            }

            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    public Vector<Transaction> getTransactionByID(int ID) throws SQLException{
        return getTransactionByInt("TRANSACTION_ID", ID);
    }

    public Vector<Transaction> getTransactionsByDateTime(String dateTime) 
        throws SQLException{
        return getTransactionByString("DATE_TIME", dateTime);
    }

    // Note that the query transforms the specified date, which is in
    // MM-DD-YYYY, into YYYY-MM-DD.
    public Vector<Transaction> getTransactionsByServiceDate(String serviceDate) 
        throws SQLException{
        System.out.println("ServiceDate: " + toSQLDate(serviceDate));
        return getTransactionByString("SERVICE_DATE", toSQLDate(serviceDate));
    }

    public Vector<Transaction> getTransactionsByProviderID(int ID) throws SQLException{
        return getTransactionByInt("PROVIDER_ID", ID);
    }

    public Vector<Transaction> getTransactionsByPatientID(int ID) throws SQLException{
        return getTransactionByInt("PATIENT_ID", ID);
    }

    public Vector<Transaction> getTransactionsByServiceID(int ID) throws SQLException{
        return getTransactionByInt("SERVICE_ID", ID);
    }

    public Vector<Transaction> getTransactionsByConsultID(int ID) throws SQLException{
        return getTransactionByInt("CONSULT_ID", ID);
    }

    // Returns a full table of active patients or providers, meaning those with
    // an ENROLLMENT column equal to 1.

    private Vector<Entity> getAllActiveEntities(String table) throws SQLException {
        String IDColumn = table.toUpperCase();
        IDColumn = IDColumn.substring(0, IDColumn.length() - 1) + "_ID";

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + table + " WHERE ENROLLMENT = 1"
        );

        Vector<Entity> returnVec = new Vector<Entity>();

        while(rs.next()) {
            try {
                if(IDColumn.matches("PATIENT_ID")) {
                    returnVec.add( new Patient(
                        rs.getInt(IDColumn),
                        rs.getString("NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"),
                        rs.getInt("ENROLLMENT"),
                        rs.getInt("STANDING")
                        )
                    );
                }

                if(IDColumn.matches("PROVIDER_ID")) {
                    returnVec.add( new Provider(
                        rs.getInt(IDColumn),
                        rs.getString("NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("CITY"),
                        rs.getString("STATE"),
                        rs.getString("ZIPCODE"),
                        rs.getInt("ENROLLMENT")
                        )
                    );
                }
            }
            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    public Vector<Entity> getAllActivePatients() throws SQLException {
        return getAllActiveEntities("Patients");
    }

    public Vector<Entity> getAllActiveProviders() throws SQLException {
        return getAllActiveEntities("Providers");
    }

    public Vector<Service> getAllActiveServices() throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM SERVICES WHERE ENROLLMENT = 1"
        );

        Vector<Service> returnVec = new Vector<Service>();

        while(rs.next()) {
            try {
                    returnVec.add( new Service(
                        rs.getInt("SERVICE_ID"),
                        rs.getString("NAME"),
                        rs.getFloat("PRICE"),
                        rs.getInt("ENROLLMENT")
                        )
                    );
                }
            catch(InputException e) {
                System.out.println("Somehow, an invalid entry is in the DB.");
            }
        }

        return returnVec;
    }

    // Gets all transactions by either PATIENT_ID or PROVIDER_ID within one week
    // of the specified date.
    private Vector<Transaction> getWeekTransactions(String column,
            int ID, String date) throws SQLException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Vector<Transaction> returnVec = new Vector<Transaction>();

        try {
            Date currentDate = inputFormat.parse(date);
            Date pastDate = new Date();
            // pastDate = 7 days before currentDate.
            pastDate.setTime(currentDate.getTime() - 
                    (long)7 * 1000 * 60 * 60 * 24);


            PreparedStatement pStatement = conn.prepareStatement(
                "SELECT * FROM " + "Transactions" + " WHERE " + 
                column + " = ? AND " +
                "SERVICE_DATE >= ? AND SERVICE_DATE <= ?"
            );

            pStatement.setInt(1, ID);
            pStatement.setString(2, outputFormat.format(pastDate));
            pStatement.setString(3, outputFormat.format(currentDate));

            ResultSet rs = pStatement.executeQuery();

            while (rs.next()) {
                try {
                    returnVec.add( new Transaction(
                        rs.getInt("TRANSACTION_ID"),
                        rs.getString("DATE_TIME"),
                        toOutputDate(rs.getString("SERVICE_DATE")),
                        rs.getInt("PROVIDER_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getInt("SERVICE_ID"),
                        rs.getInt("CONSULT_ID"),
                        rs.getString("COMMENT")
                        )
                    );
                }

                catch(InputException e) {
                    System.out.println("Somehow, an invalid entry is in the DB.");
                }
            }

            return returnVec;
        }

        catch(ParseException e) {
            return returnVec;
        }
    }

    // Calls the private function defined above with the specified column.
    public Vector<Transaction> getWeekTransactionsByPatient(int ID, 
            String date) throws SQLException {
        return getWeekTransactions("PATIENT_ID", ID, date);
    }

    public Vector<Transaction> getWeekTransactionsByProvider(int ID,
            String date) throws SQLException {
        return getWeekTransactions("PROVIDER_ID", ID, date);
    }

    // Converts MM-DD-YYYY to YYYY-MM-DD.
    private String toSQLDate(String outputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date tryDate = inputFormat.parse(outputDate);
            return outputFormat.format(tryDate);
        }

        catch (ParseException e) {
            return "";
        }
    }      

    // Converts YYYY-MM-DD to MM-DD-YYYY.
    private String toOutputDate(String SQLDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

        try {
            Date tryDate = inputFormat.parse(SQLDate);
            return outputFormat.format(tryDate);
        }

        catch(ParseException e) {
            return "";
        }
    }
}
