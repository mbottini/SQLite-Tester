public abstract class Entity {
    int _ID;
    String _name, _address, _city, _state, _zipcode;

    public Entity(int ID, String name, String address, String city,
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

    public Entity(String name, String address, String city,
                   String state, String zipcode) throws InputException {
        this(0, name, address, city, state, zipcode);
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

        if(!(compareStrings(_name, other.name())) ||
           !(compareStrings(_address, other.address())) ||
           !(compareStrings(_city, other.city())) ||
           !(compareStrings(_state, other.state())) ||
           !(compareStrings(_zipcode, other.zipcode()))) {
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
