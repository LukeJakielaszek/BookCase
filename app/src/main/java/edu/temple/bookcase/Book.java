package edu.temple.bookcase;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private int id;
    private String title;
    private String author;
    private int published;
    private String coverURL;
    private int duration;

    public Book(int id, String title, String author, int published, String coverURL, int duration){
        this.id = id;
        this.title = title;
        this.author = author;
        this.published = published;
        this.coverURL = coverURL;
        this.duration = duration;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        published = in.readInt();
        coverURL = in.readString();
        duration = in.readInt();
    }

    public int getDuration(){
        return duration;
    }

    public int getPublished() {
        return published;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public String getTitle() {
        return title;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeInt(published);
        parcel.writeString(coverURL);
        parcel.writeInt(duration);
    }
}
