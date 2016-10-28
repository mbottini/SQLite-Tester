public abstract class Entity {
    int _ID;
    String _name, _address, _city, _state, _zipcode;

    Boolean _enrollmentStatus;

    public Entity(int ID, String name, String address, String city,
                   String state, String zipcode, int enrollmentStatus) 
                   throws InputException {
        String exceptionString = "";
        if(ID < 0 || ID > 1000000000) {
            exceptionString += "ID must be a positive 9-digit number.\n";
        }

        if(isBlankOrTooManyChars(name, 25)) {
            exceptionString += "Name must not be blank and must " +
                    "contain fewer than 25 characters.\n";
        }

        if(isBlankOrTooManyChars(address, 25)) {
            exceptionString += "Address must not be blank and must " +
                    "contain fewer than 25 characters.\n";
        }

        if(isBlankOrTooManyChars(city, 14)) {
            exceptionString += "City must not be blank and must " +
                    "contain fewer than 14 characters.\n";
        }

        if(!(state.matches("^[a-zA-Z]{2}$"))) {
            exceptionString += "State must be two characters.\n";
        }

        if(!(zipcode.matches("^\\d{5}$"))) {
            exceptionString += "Zipcode must be five numbers.\n";
        }

        if(!(exceptionString.isEmpty())) {
            exceptionString = exceptionString.substring(0,
                                  exceptionString.length() - 1); 
            throw new InputException(exceptionString);
        }

        _ID = ID;
        _name = name;
        _address = address;
        _city = city;
        _state = state;
        _zipcode = zipcode;

        if(enrollmentStatus != 0) {
            _enrollmentStatus = true;
        }

        else {
            _enrollmentStatus = false;
        }

        return;
    }

    Boolean isBlankOrTooManyChars(String s, int limit) {
        return s.length() == 0 || s.length() > limit;
    }

    public Entity(String name, String address, String city,
                   String state, String zipcode, int enrollmentStatus) 
                   throws InputException {
        this(0, name, address, city, state, zipcode, enrollmentStatus);
    }

    // Getter functions.

    public int getID() {
        return _ID;
    }

    public String getName() {
        return _name;
    }

    public String getAddress() {
        return _address;
    }

    public String getCity() {
        return _city;
    }

    public String getState() {
        return _state;
    }

    public String getZipcode() {
        return _zipcode;
    }

    public Boolean getEnrollmentStatus() {
        return _enrollmentStatus;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if(!Entity.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Entity other = (Entity) obj;

        // And we have to do this for every single string...

        if(!(compareStrings(_name, other.getName())) ||
           !(compareStrings(_address, other.getAddress())) ||
           !(compareStrings(_city, other.getCity())) ||
           !(compareStrings(_state, other.getState())) ||
           !(compareStrings(_zipcode, other.getZipcode()))) {
            return false;
        }

        return true;
    }

    private Boolean compareStrings(String s1, String s2) {
        if(s1 == null) {
            if(s2 != null) {
                return false;
            }
        }

        else {
            if(!(s1.equals(s2))) {
                return false;
            }
        }

        return true;
    }
}
