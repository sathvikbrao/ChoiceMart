package com.example.choicemart1;

public class UserData {
    private static UserData instance;
    private String userId;
    private String userName;
    private String userImageUri;

    private UserData() {
        // Private constructor to prevent instantiation
    }

    public static synchronized UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUri() {
        return userImageUri;
    }

    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }
}
