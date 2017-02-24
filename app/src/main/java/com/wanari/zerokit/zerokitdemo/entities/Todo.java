package com.wanari.zerokit.zerokitdemo.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

@IgnoreExtraProperties
public class Todo implements Parcelable {

    private String id;

    private String title;

    private String description;

    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Todo(Map<String, String> dataSnapshot) {
        this.id = dataSnapshot.get("id");
        this.title = dataSnapshot.get("title");
        this.description = dataSnapshot.get("description");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("description", description);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
    }

    protected Todo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Todo> CREATOR = new Parcelable.Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel source) {
            return new Todo(source);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };
}
