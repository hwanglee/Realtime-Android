package edu.umd.cs.realtime.models;

/**
 * Created by Omar on 4/27/17.
 */

public class User {
    private String firstName;
    private String lastName;
    private String cellPhone;
    private String address;
    private String email;
    private String type;

    public  User(){

    }

    public User(String firstName, String lastName, String cellPhone, String address, String email, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cellPhone = cellPhone;
        this.address = address;
        this.email = email;
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }
}
