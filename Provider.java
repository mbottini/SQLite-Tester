class Provider extends Entity {
    public Provider(int ID, String name, String address, String city,
            String state, String zipcode, int enrollmentStatus) 
            throws InputException {
        super(ID, name, address, city, state, zipcode, enrollmentStatus);
    }

    public Provider(String name, String address, String city, String state,
            String zipcode, int enrollmentStatus) throws InputException {
        super(name, address, city, state, zipcode, enrollmentStatus);
    }
    
    @Override
    public String toString() {
        return "Provider ID : " + Integer.toString(_ID) + "\n" +
               "Name: " + _name + "\n" +
               "Address: " + _address + "\n" +
               "City: " + _city + "\n" +
               "State: " + _state + "\n" +
               "Zipcode: " + _zipcode + "\n" +
               "Enrollment Status: " +
               ((_enrollmentStatus)? "Active" : "Inactive");
    }
}
