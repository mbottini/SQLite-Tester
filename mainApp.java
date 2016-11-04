import java.sql.*;
import java.util.Vector;

public class mainApp
{
    public static void main( String args[] ){
        try {
            Database dBase = new Database();
            dBase.addPatients("patients.csv");
            dBase.addProviders("providers.csv");
            dBase.addServices("services.csv");
            dBase.addTransactions("transactions.csv");

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

            System.out.println("Printing providers...\n");

            dBase.printAllProviders();

            System.out.println("Printing services...\n");

            dBase.printAllServices();

            System.out.println("Printing transactions...\n");

            dBase.printAllTransactions();


            System.out.println("Executing getPatientByID(0)...\n");

            Vector<Entity> vec = dBase.getPatientByID(0);

            for(Entity p : vec) {
                System.out.println(p);
            }

            System.out.println("\nExecuting getPatientsByName(\"Michael " +
                "Bottini\")...\n");

            vec = dBase.getPatientsByName("Michael Bottini");

            for(Entity p : vec) {
                System.out.println(p);
            }

            System.out.println("\nExecuting getPatientsByAddress(\"" +
                "123 Park Place\")...\n");

            vec = dBase.getPatientsByAddress("123 Park Place");

            for(Entity p : vec) {
                System.out.println(p);
            }

        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }
    }
}
