class Service {
    int _ID;
    String _name;
    float _price;
    Boolean _active;

    public Service(int ID, String name, float price, int active) 
        throws InputException {
        String exceptionString = "";

        if(ID < 0 || ID > 999999) {
            exceptionString += "ID must be a six-digit integer.\n";
        }

        if(name.length() == 0 || name.length() > 20) {
            exceptionString += "Name must have 20 characters or fewer.\n";
        }

        if(price < 0 || price > 9999.99) {
            exceptionString += "Price must be a decimal between 0 and " +
                "9999.99.\n";
        }

        if(!(exceptionString.isEmpty())) {
            exceptionString = exceptionString.substring(0,
                    exceptionString.length() - 1);
            throw new InputException(exceptionString);
        }

        _ID = ID;
        _name = name;
        _price = price;
        
        if(active != 0) {
            _active = true;
        }

        else {
            _active = false;
        }

        return;
    }

    public Service(String name, float price, int active) throws InputException {
        this(0, name, price, active);
        return;
    }

    public int getID() {
        return _ID;
    }

    public String getName() {
        return _name;
    }

    public float getPrice() {
        return _price;
    }

    public Boolean getActive() {
        return _active;
    }

    @Override
    public String toString() {
        return "Service ID: " + _ID + "\n" +
               "Name: " + _name + "\n" +
               "Price: " + Float.toString(_price) + "\n" +
               "Status: " +
               ((_active)? "Active" : "Inactive");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if(!Service.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Service other = (Service) obj;

        // And we have to do this for every single string...

        if(!(compareStrings(_name, other.getName())) ||
           _price != other.getPrice()) {
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
        
