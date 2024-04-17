package com.example.a5520_final_project;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

// A User class that represents a user in the app
// This is intended to be used with Firebase Authentication
// and it is very simple, but in practice you might include many more fields like name, profile picture, age, etc.
// the information can be collected at registration, and even updated in a settings feature.
public class User implements Parcelable {

    private String id;
    private String email;

    // Default constructor required for Firebase database to work properly
    public User() {
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    protected User(Parcel in) {
        id = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" + "id='" + id + '\'' + ", email='" + email + '\'' + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.email);
    }
}
