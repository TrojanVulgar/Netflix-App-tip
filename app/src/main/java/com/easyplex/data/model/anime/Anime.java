package com.easyplex.data.model.anime;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.easyplex.data.model.serie.Season;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.easyplex.data.model.genres.Genre;

import java.util.List;


public class Anime implements Parcelable {


    @NonNull
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("tmdb_id")
    @Expose
    private int tmdbId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("preview_path")
    @Expose
    private String previewPath;
    @SerializedName("vote_average")
    @Expose
    private String voteAverage;
    @SerializedName("vote_count")
    @Expose
    private String voteCount;
    @SerializedName("popularity")
    @Expose
    private String popularity;

    @SerializedName("status")
    @Expose
    private String status;


    @SerializedName("premuim")
    @Expose
    private int premuim;


    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;


    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("hd")
    @Expose
    private Integer hd;

    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;
    @SerializedName("seasons")
    @Expose
    private List<Season> seasons = null;


    protected Anime (Parcel in) {
        id = in.readString();
        tmdbId = in.readInt();
        name = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        previewPath = in.readString();
        voteAverage = in.readString();
        voteCount = in.readString();
        popularity = in.readString();
        status = in.readString();
        premuim = in.readInt();
        firstAirDate = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        if (in.readByte() == 0) {
            hd = null;
        } else {
            hd = in.readInt();
        }
        if (in.readByte() == 0) {
            views = null;
        } else {
            views = in.readInt();
        }
        genres = in.createTypedArrayList(Genre.CREATOR);
    }

    public static final Creator<Anime> CREATOR = new Creator<Anime>() {
        @Override
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        @Override
        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int  getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
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

    public Integer getHd() {
        return hd;
    }

    public void setHd(Integer hd) {
        this.hd = hd;
    }


    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public int getPremuim() {
        return premuim;
    }

    public void setPremuim(int premuim) {
        this.premuim = premuim;
    }


    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(tmdbId);
        dest.writeString(name);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(previewPath);
        dest.writeString(voteAverage);
        dest.writeString(voteCount);
        dest.writeString(popularity);
        dest.writeString(status);
        dest.writeInt(premuim);
        dest.writeString(firstAirDate);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        if (hd == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(hd);
        }
        if (views == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(views);
        }
        dest.writeTypedList(genres);
    }
}
