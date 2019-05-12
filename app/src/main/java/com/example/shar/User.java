package com.example.shar;

public class User {

    private String mUID;
    private String mUsername;
    private String mEmail;


    public User(String uid, String username , String email){
        this.mUID = uid;
        this.mUsername = username;
        this.mEmail = email;
    }

    public String getmUID() {return mUID;}

    public String getmUsername() {return mUsername; }

    public void setmUID(String uid){ mUID = uid;}

    public void setmUsername(String username){ mUsername = username; }

    public void setmEmail(String email) { mEmail = email; }

    public String getmEmail() { return mEmail; }
}
