import java.sql.*;

public class mainApp
{
    public static void main( String args[] ){
        try {
            Database dBase = new Database();
            dBase.printPatients();
            Patient newPatient = new Patient(1283, "Tom Bottini",
                    "24 Dartmouth Dr", "Framingham", "MAS", "01701");
            System.out.println("Successful.");
        }
        catch(SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
        }
        catch(InputException e) {
            System.out.println(e.getMessage());
        }
    }
}
