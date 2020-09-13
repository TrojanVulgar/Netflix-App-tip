package com.easyplex.data.model.serie;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodeStream implements Parcelable {



    public EpisodeStream(Integer id, String movieId, String server, String link, String lang, String createdAt, String updatedAt) {
        this.id = id;
        this.movieId = movieId;
        this.server = server;
        this.link = link;
        this.lang = lang;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("movie_id")

    @Expose
    private String movieId;
    @SerializedName("server")
    @Expose
    private String server;


    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("hd")
    @Expose
    private String hd;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;


    protected EpisodeStream(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        movieId = in.readString();
        server = in.readString();
        link = in.readString();
        lang = in.readString();
        hd = in.readString();
        status = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<EpisodeStream> CREATOR = new Creator<EpisodeStream>() {
        @Override
        public EpisodeStream createFromParcel(Parcel in) {
            return new EpisodeStream(in);
        }

        @Override
        public EpisodeStream[] newArray(int size) {
            return new EpisodeStream[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @NonNull
    @Override
    public String toString() {
        return server;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(movieId);
        dest.writeString(server);
        dest.writeString(link);
        dest.writeString(lang);
        dest.writeString(hd);
        dest.writeString(status);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }
}