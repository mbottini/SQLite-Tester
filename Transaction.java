import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;


class Transaction {
    int _ID;
    String _dateTime;
    String _serviceDate;
    int _providerID;
    int _patientID;
    int _serviceID;
    int _consultID;
    String _comment;

    public Transaction(int ID, String dateTime, String serviceDate, 
            int providerID, int patientID, int serviceID, 
            int consultID, String comment) throws InputException {
        String exceptionString = "";
        if(dateTime == "") {
            dateTime = getTodayDateLong();
        }

        if(ID < 0 || ID > 999999999) {
            exceptionString += "ID must be a 9-digit integer.\n";
        }

        if(!isValidLongDate(dateTime)) {
            exceptionString += "Date-Time must be formatted in form " +
                "MM-DD-YYYY HH-MM-SS.\n";
        }

        if(!isValidShortDate(serviceDate)) {
            exceptionString += "Service Date must be formatted in form " +
                "MM-DD-YYYY.\n";
        }

        if(providerID < 0 || providerID > 999999999) {
            exceptionString += "Provider ID must be a 9-digit integer.\n";
        }

        if(patientID < 0 || patientID > 999999999) {
            exceptionString += "Patient ID must be a 9-digit integer.\n";
        }
        
        if(serviceID < 0 || serviceID > 999999999) {
            exceptionString += "Service ID must be a 9-digit integer.\n";
        }

        if(consultID < 0 || consultID > 999999999) {
            exceptionString += "Consult ID must be a 9-digit integer.\n";
        }

        if(comment.length() > 100) {
            exceptionString += "Comment length must be less than 100.\n";
        }

        if(!exceptionString.isEmpty()) {
            exceptionString = exceptionString.substring(0,
                                  exceptionString.length() - 1);
            throw new InputException(exceptionString);
        }

        _ID = ID;
        _dateTime = dateTime;
        _serviceDate = serviceDate;
        _providerID = providerID;
        _patientID = patientID;
        _serviceID = serviceID;
        _consultID = consultID;
        _comment = comment;

        return;
    }

    public Transaction(String serviceDate, 
            int providerID, int patientID, int serviceID, 
            int consultID, String comment) throws InputException {
        this(0, "", serviceDate, providerID, patientID,
                serviceID, consultID, comment);
    }

    int getID() {
        return _ID;
    }

    String getDateTime() {
        return _dateTime;
    }

    String getServiceDate() {
        return _serviceDate;
    }

    int getProviderID() {
        return _providerID;
    }

    int getPatientID() {
        return _patientID;
    }

    int getServiceID() {
        return _serviceID;
    }

    int getConsultID() {
        return _consultID;
    }

    String getComment() {
        return _comment;
    }

    private String getTodayDateLong() {
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        return ft.format(now);
    }

    private Boolean isValidShortDate(String date) {
        if(date.length() != 10) {
            return false;
        }

        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");

        try {
            Date tryDate = ft.parse(date);
            return true;
        }

        catch(ParseException e) {
            return false;
        }
    }

    private Boolean isValidLongDate(String date) {
        if(date.length() != 19) {
            return false;
        }

        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        try {
            Date tryDate = ft.parse(date);
            return true;
        }

        catch(ParseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Transaction ID: " + _ID + "\n" +
            "Date-Time: " + _dateTime + "\n" +
            "Service Date: " + _serviceDate + "\n" +
            "Provider ID: " + Integer.toString(_providerID) + "\n" +
            "Patient ID: " + Integer.toString(_patientID) + "\n" +
            "Service ID: " + Integer.toString(_serviceID) + "\n" +
            "Consult ID: " + Integer.toString(_consultID) + "\n" +
            (!(_comment.isEmpty()) ? "Comment: " : "") +
            _comment;
    }
}



