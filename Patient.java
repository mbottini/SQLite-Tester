import java.util.HashSet;
import java.util.Arrays;


public class Patient {
    int _ID;
    String _name, _address, _city, _state, _zipcode;
    static HashSet<String> stateSet = new HashSet<String>(Arrays.asList("AK", "AL",
                "AR", "AZ", "CA", "CO", "CT", "DC", "DE", "FL", "GA", "HI",
                "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME",
                "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ",
                "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC", "SD",
                "TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY"));


    public Patient(int ID, String name, String address, String city,
                   String state, String zipcode) throws InputException {
        String inputExceptionString = "";

        if(ID < 0 || ID > 1000000000) {
            inputExceptionString += "ID must be a positive 9-digit number.\n";
        }

        if(isBlankOrTooManyChars(name, 25)) {
            inputExceptionString += "Name must not be blank and must " +
                    "contain fewer than 25 characters.\n";
        }

        if(isBlankOrTooManyChars(address, 25)) {
            inputExceptionString += "Address must not be blank and must " +
                    "contain fewer than 25 characters.\n";
        }

        if(isBlankOrTooManyChars(city, 14)) {
            inputExceptionString += "City must not be blank and must " +
                    "contain fewer than 14 characters.\n";
        }

        if(!(stateSet.contains(state))) {
            inputExceptionString += "Invalid state input.\n";
        }

        if(!(zipcode.matches("^\\d{5}$"))) {
            inputExceptionString += "Zipcode must be five numbers.\n";
        }

        if(inputExceptionString != "") {
            // Remove last '\n' from inputExceptionString.
            inputExceptionString = inputExceptionString.substring(0,
                                            inputExceptionString.length() - 1);
            throw new InputException(inputExceptionString);
        }

        _ID = ID;
        _name = name;
        _address = address;
        _city = city;
        _state = state;
        _zipcode = zipcode;
        return;
    }

    Boolean isBlankOrTooManyChars(String s, int limit) {
        return s.length() == 0 || s.length() > limit;
    }


    // Getter functions.

    public int ID() {
        return _ID;
    }

    public String name() {
        return _name;
    }

    public String address() {
        return _address;
    }

    public String city() {
        return _city;
    }

    public String state() {
        return _state;
    }

    public String zipcode() {
        return _zipcode;
    }

    // Printing a Patient value.

    @Override
    public String toString() {
        return "ID: " + Integer.toString(_ID) + "\n" +
               "Name: " + _name + "\n" +
               "Address: " + _address + "\n" +
               "City: " + _city + "\n" +
               "State: " + _state + "\n" +
               "Zipcode: " + _zipcode;
    }
}
