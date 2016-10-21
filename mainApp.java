import java.sql.*;

public class mainApp
{
    public static void main( String args[] ){
        try {
            Database dBase = new Database();
            Patient newPatient = new Patient(1283, "Tom Bottini",
                    "24 Dartmouth Dr", "Framingham", "MA", "01701");
            System.out.println("Patient created.");

            if (dBase.addPatient(newPatient)) {
                System.out.println("Successful.");
            }

            dBase.printAllPatients();
        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }
        catch(InputException e) {
            System.out.println(e.getMessage());
        }
    }
}
