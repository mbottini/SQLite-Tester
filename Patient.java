import java.util.HashSet;
import java.util.Arrays;

public class Patient extends Entity {
    Boolean _financialStanding;

    public Patient(int ID, String name, String address, String city, 
            String state, String zipcode, int active, int financialStanding) 
            throws InputException {
        super(ID, name, address, city, state, zipcode, active);

        if(financialStanding != 0) {
            _financialStanding = true;
        }

        else {
            _financialStanding = false;
        }
    }

    public Patient(String name, String address, String city, String state,
             String zipcode, int active, int financialStanding) 
             throws InputException {
        this(0, name, address, city, state, zipcode, active, 
             financialStanding);
    }

    @Override
    public String toString() {
        return "Patient ID: " + Integer.toString(_ID) + "\n" +
               "Name: " + _name + "\n" +
               "Address: " + _address + "\n" +
               "City: " + _city + "\n" +
               "State: " + _state + "\n" +
               "Zipcode: " + _zipcode + "\n" +
               "Active: " + ((_active)? "Yes\n" : "No\n") +
               "Financial Standing: " + 
               (_financialStanding? "Good" : "Suspended");
    }   
}

