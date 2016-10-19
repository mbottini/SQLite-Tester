public class Patient {
    int _ID;
    String _name, _address, _city, _state, _zipcode;

    public Patient(int ID, String name, String address, String city,
                   String state, String zipcode) throws InputException {
        if(ID < 0 || ID > 1000000000) {
            throw new InputException("ID must be a positive 9-digit number.");
        }

        if(isBlankOrTooManyChars(name, 25)) {
            throw new InputException("Name must not be blank and must " +
                    "contain fewer than 25 characters.");
        }

        if(isBlankOrTooManyChars(address, 25)) {
            throw new InputException("Address must not be blank and must " +
                    "contain fewer than 25 characters.");
        }

        if(isBlankOrTooManyChars(city, 14)) {
            throw new InputException("Address must not be blank and must " +
                    "contain fewer than 25 characters.");
        }

        if(!(state.matches("^[a-zA-Z]{2}$"))) {
            throw new InputException("State must be two characters.");
        }

        if(!(zipcode.matches("^\\d{5}$"))) {
            throw new InputException("Zipcode must be five numbers.");
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
}