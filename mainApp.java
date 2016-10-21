import java.sql.*;

public class mainApp
{
    public static void main( String args[] ){
        try {
            Database dBase = new Database();
            dBase.addPatients("patients.csv");

            System.out.println("\nPrinting patients...\n");
            dBase.printAllPatients();
        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }
    }
}
