package com.wanari.zerokit.zerokitdemo.entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.description;

@IgnoreExtraProperties
public class Table implements Parcelable {

    @Exclude
    private String id;

    private String title;

    public Table(String title) {
        this.title = title;
    }

    public Table(String key, Map<String, String> dataSnapshot) {
        this.id = key;
        this.title = dataSnapshot.get("title");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
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
    }

    protected Table(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
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

        if (!id.equals(table.id)) {
            return false;
        }
        return title.equals(table.title);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }
}
