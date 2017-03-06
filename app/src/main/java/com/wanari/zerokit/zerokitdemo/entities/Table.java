package com.wanari.zerokit.zerokitdemo.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

@IgnoreExtraProperties
public class Table implements Parcelable {

    @Exclude
    private String id;

    private String title;

    private String tresorId;

    public Table(String title, String tresorId) {
        this.title = title;
        this.tresorId = tresorId;
    }

    public Table(String key, Map<String, String> dataSnapshot) {
        this.id = key;
        this.title = dataSnapshot.get("title");
        this.tresorId = dataSnapshot.get("tresorId");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTresorId() {
        return tresorId;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.tresorId);
    }

    protected Table(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.tresorId = in.readString();
    }

    public static final Parcelable.Creator<Table> CREATOR = new Parcelable.Creator<Table>() {
        @Override
        public Table createFromParcel(Parcel source) {
            return new Table(source);
        }

        @Override
        public Table[] newArray(int size) {
            return new Table[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Table table = (Table) o;

        if (id != null ? !id.equals(table.id) : table.id != null) {
            return false;
        }
        if (title != null ? !title.equals(table.title) : table.title != null) {
            return false;
        }
        return tresorId != null ? tresorId.equals(table.tresorId) : table.tresorId == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (tresorId != null ? tresorId.hashCode() : 0);
        return result;
    }
}
