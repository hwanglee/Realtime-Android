package edu.umd.cs.realtime.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Omar on 4/26/17.
 */

public class Post implements Parcelable{
    private String title;
    private int category;
    private String content;
    private String summary;
    private long createdAt;
    private String id;
    private String end;
    private String start;
    private String location;
    private String pid;

    public Post() {

    }

    public Post(String title, int category, String content, String summary, long createdAt, String id, String end, String start, String location, String pid){
        this.title = title;
        this.category = category;
        this.content = content;
        this.summary = summary;
        this.createdAt = createdAt;
        this.id = id;
        this.end = end;
        this.start = start;
        this.location = location;
        this.pid =  pid;
    }

    //PARCELABLE IMPLEMENTATION

    public Post(Parcel in) {
        String[] data = new String[10];

        in.readStringArray(data);

        this.title = data[0];
        this.category = Integer.parseInt(data[1]);
        this.content = data[2];
        this.summary = data[3];
        this.createdAt = Long.parseLong(data[4]);
        this.id = data[5];
        this.end = data[6];
        this.start = data[7];
        this.location = data[8];
        this.pid =  data[9];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] postData = {
                this.title,
                String.valueOf(this.category),
                this.content,
                this.summary,
                String.valueOf(this.createdAt),
                this.id,
                this.end,
                this.start,
                this.location,
                this.pid
        };

        dest.writeStringArray(postData);

    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };


    //GETTERS AND SETTERS
    @Override
    public String toString() {
        return start;
    }

    public static String[] getCategories() {
        String[] categories = {
                "Arts & Humanities",
                "Beauty & Style",
                "Business & Finance",
                "Cars & Transportation",
                "Computers & Internet",
                "Consumer Electronics",
                "Dining Out",
                "Education & Reference",
                "Entertainment & Music",
                "Environment",
                "Family & Relationships",
                "Food & Drink",
                "Games & Recreation",
                "Health",
                "Home & Garden",
                "Local Businesses",
                "News & Events",
                "Pets",
                "Politics & Government",
                "Pregnancy & Parenting",
                "Science & Mathematics",
                "Social Science",
                "Society & Culture",
                "Sports",
                "Travel",
                "Yahoo Products"
        };

        return categories;
    }


    public String getTitle() {
        return title;
    }

    public int getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public String getSummary() {
        return summary;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public String getLocation() {
        return location;
    }

    public String getPid() {
        return pid;
    }


}
