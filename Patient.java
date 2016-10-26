import java.util.HashSet;
import java.util.Arrays;

public class Patient extends Entity {
    Boolean financialStanding;

    public Patient(int ID, String name, String address, String city, 
            String state, String zipcode) throws InputException {
        super(ID, name, address, city, state, zipcode);
    }

    public Patient(String name, String address, String city, String state,
                   String zipcode) throws InputException {
        super(name, address, city, state, zipcode);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}

