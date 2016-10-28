import java.sql.*;

public class mainApp
{
    public static void main( String args[] ){
        try {
            Database dBase = new Database();
            dBase.addPatients("patients.csv");

            System.out.println("\nPrinting patients...\n");
            dBase.printAllPatients();


            System.out.println("Updating patient...");

            try {
                Patient updatePatient = new Patient("Michael Bottini", 
                        "4 Cross St", "Southborough", "MA", "91234", 1, 0);
                if(dBase.updatePatient(5912, updatePatient)) {
                    System.out.println("Patient updated.");
                }
                else {
                    System.out.println("ID not found.");
                }
            }
            catch(InputException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("\nRemoving ID = 1...");
            if (dBase.removePatient(1)) {
                System.out.println("Removed.");
            }

            else {
                System.out.println("Not found.");
            }

            System.out.println();
            dBase.printAllPatients();

        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }
    }
}
