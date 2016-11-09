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

            System.out.println("\nExecuting getPatientsByCity(\"" +
                "Hillsboro\")...\n");

            vec = dBase.getPatientsByCity("Hillsboro");

            for(Entity p : vec) {
                System.out.println(p);
            }
            System.out.println("\nExecuting getPatientsByState(\"" +
                "MA\")...\n");

            vec = dBase.getPatientsByState("MA");

            for(Entity p : vec) {
                System.out.println(p);
            }
            System.out.println("\nExecuting getPatientsByZipcode(\"" +
                "01701\")...\n");

            vec = dBase.getPatientsByZipcode("01701");

            for(Entity p : vec) {
                System.out.println(p);
            }

            System.out.println("Executing getProviderByID(0)...\n");

            vec = dBase.getProviderByID(0);

            for(Entity p : vec) {
                System.out.println(p);
            }

            System.out.println("\nExecuting getProvidersByName(\"" +
                "Acme\")...\n");

            vec = dBase.getProvidersByName("Acme");

            for(Entity p : vec) {
                System.out.println(p);
            }

            System.out.println("\nExecuting getProvidersByAddress(\"" +
                "4 Aleph St\")...\n");

            vec = dBase.getProvidersByAddress("4 Aleph St");

            for(Entity p : vec) {
                System.out.println(p);
            }

            System.out.println("\nExecuting getProvidersByCity(\"" +
                "Pigknuckle\")...\n");

            vec = dBase.getProvidersByCity("Pigknuckle");

            for(Entity p : vec) {
                System.out.println(p);
            }
            System.out.println("\nExecuting getProvidersByState(\"" +
                "AL\")...\n");

            vec = dBase.getProvidersByState("AL");

            for(Entity p : vec) {
                System.out.println(p);
            }
            System.out.println("\nExecuting getProvidersByZipcode(\"" +
                "87912\")...\n");

            vec = dBase.getProvidersByZipcode("87912");

            for(Entity p : vec) {
                System.out.println(p);
            }

            System.out.println("\nExecuting getServiceByID(0)...\n");

            Vector<Service> serviceVec = dBase.getServiceByID(0);

            for(Service s : serviceVec) {
                System.out.println(s);
            }

            System.out.println("\nExecuting getServiceByName(" +
                "\"Hypnosis\")...\n");

            serviceVec = dBase.getServiceByName("Hypnosis");

            for(Service s : serviceVec) {
                System.out.println(s);
            }

            System.out.println("\nExecuting getServiceByPrice(75.00F)...\n");

            serviceVec = dBase.getServiceByPrice(75.00F);

            for(Service s : serviceVec) {
                System.out.println(s);
            }
        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }
    }
}
