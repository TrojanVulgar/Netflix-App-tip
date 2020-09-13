package com.easyplex.data.local.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.bumptech.glide.Glide;
import com.easyplex.data.model.serie.Season;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.model.substitles.MediaSubstitle;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.easyplex.data.model.genres.Genre;

import java.util.List;


/**
 * @author Yobex.
 */

@Entity(primaryKeys = "tmdbId",
        tableName = "favorite",
        indices = {@Index(value = {"tmdbId"}, unique = true)}
)


public class Media implements Parcelable {


    @NonNull
    @SerializedName("id")
    @Expose
    private String id;

    @NonNull
    @SerializedName("tmdb_id")
    @Expose
    private String tmdbId;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    private String title;

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



    @SerializedName("premuim")
    @Expose
    private int premuim;


    @SerializedName("is_anime")
    @Expose
    private int isAnime;

    @SerializedName("popularity")
    @Expose
    private String popularity;


    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("substitles")
    @Expose
    private List<MediaSubstitle> substitles = null;


    @SerializedName("seasons")
    @Expose
    private List<Season> seasons = null;

    @SerializedName("runtime")
    @Expose
    private String runtime;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("genre")
    @Expose
    private String genre;

    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;

    @SerializedName("trailer_id")
    @Expose
    private String trailerId;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Media(@NonNull String id, @NonNull String tmdbId, String title, String name) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.title = title;
        this.name = name;
    }

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;




    @SerializedName("hd")
    @Expose
    private Integer hd;


    @SerializedName("videos")
    @Expose
    private List<MediaStream> videos;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres;


    protected Media(Parcel in) {
        id = in.readString();
        tmdbId = in.readString();
        title = in.readString();
        name = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        previewPath = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readString();
        }
        if (in.readByte() == 0) {
            voteCount = null;
        } else {
            voteCount = in.readString();
        }
        if (in.readByte() == 0) {
            premuim = 0;
        } else {
            premuim = in.readInt();
        }
        if (in.readByte() == 0) {
            popularity = null;
        } else {
            popularity = in.readString();
        }
        if (in.readByte() == 0) {
            views = null;
        } else {
            views = in.readInt();
        }
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readString();
        }
        runtime = in.readString();
        releaseDate = in.readString();
        genre = in.readString();
        firstAirDate = in.readString();
        trailerId = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        if (in.readByte() == 0) {
            hd = null;
        } else {
            hd = in.readInt();
        }
        genres = in.createTypedArrayList(Genre.CREATOR);
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(String tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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


    public List<MediaSubstitle> getSubstitles() {
        return substitles;
    }

    public void setSubstitles(List<MediaSubstitle> substitles) {
        this.substitles = substitles;
    }



    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public List<MediaStream> getVideos() {
        return videos;
    }

    public void setVideos(List<MediaStream> videos) {
        this.videos = videos;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }


    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }


    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }



    public int getIsAnime() {
        return isAnime;
    }

    public void setIsAnime(int isAnime) {
        this.isAnime = isAnime;
    }


    @BindingAdapter("android:imageUrl")
    public static void loadImage (ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(tmdbId);
        parcel.writeString(title);
        parcel.writeString(name);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(previewPath);
        if (voteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeString(voteAverage);
        }
        if (voteCount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeString(voteCount);
        }
        if (premuim == 0) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(premuim);
        }
        if (popularity == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeString(popularity);
        }
        if (views == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(views);
        }
        if (status == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeString(status);
        }
        parcel.writeString(runtime);
        parcel.writeString(releaseDate);
        parcel.writeString(genre);
        parcel.writeString(firstAirDate);
        parcel.writeString(trailerId);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
        if (hd == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(hd);
        }
        parcel.writeTypedList(genres);
    }
}
