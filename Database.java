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
                        rs.getFloat("PRICE"), rs.getInt("ACTIVE"));

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
                    "ACTIVE = ?, " +
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
                   "ACTIVE = 0 " +
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
                                             rs.getInt("ACTIVE"));
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

            if(!entryExists("Patients", newTransaction.getPatientID())) {
                return -1;
            }

            if(!entryExists("Providers", newTransaction.getProviderID())) {
                return -1;
            }

            if(!entryExists("Services", newTransaction.getServiceID())) {
                return -1;
            }

            // Otherwise, we're good!

            pStatement = conn.prepareStatement (
                "INSERT INTO Transactions " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );

            pStatement.setInt(1, transactionNum);
            pStatement.setString(2, newTransaction.getDateTime());
            pStatement.setString(3, newTransaction.getServiceDate());
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
            pStatement.setString(2, updateTransaction.getServiceDate());
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
                        rs.getString("SERVICE_DATE"),
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
}
