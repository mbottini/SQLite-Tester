import java.sql.*;

public class mainApp
{
    public static void main( String args[] ){
        try {
            Database dBase = new Database();
            dBase.addPatients("patients.csv");

            System.out.println("\nPrinting patients...\n");
            dBase.printAllPatients();


            System.out.println("\nUpdating patient...\n");

            try {
            Patient updatePatient = new Patient("Michael Bottini", "4 Cross St",
                    "Southborough", "MA", "91234", 1, 0);
            dBase.updatePatient(0, updatePatient);
            }

            catch(InputException e) {
                System.out.println(e.getMessage());
            }

            System.out.println();
            dBase.printAllPatients();

        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }
    }
}
