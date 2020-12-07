package com.example.journal.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private int userId;
    private String userName;
    private String email;
    private String FIO;
    private int idGroup;
    private int idUniversity;
    private int idRole;
    private String password;

    public LoggedInUser(int userId, String FIO, int idGroup, int idRole, int idUniversity, String userName,String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.FIO = FIO;
        this.idGroup = idGroup;
        this.idUniversity = idUniversity;
        this.idRole = idRole;
        this.password=password;
    }

    public int getUserId() {
        return userId;
    }
    public int getidGroup() {
        return idGroup;
    }
    public int getidUniversity() {
        return idUniversity;
    }
    public int getidRole() {
        return idRole;
    }
    public String getDisplayName() {
        return userName;
    }
    public String getFIO() {
        return FIO;
    }
    public String getemail() {
        return email;
    }
}
