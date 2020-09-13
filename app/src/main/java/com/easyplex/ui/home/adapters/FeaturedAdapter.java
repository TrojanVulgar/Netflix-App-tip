package com.easyplex.ui.home.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.repository.MovieRepository;
import com.easyplex.databinding.RowItemFeaturedBinding;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.ui.trailer.TrailerPreviewActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
import com.easyplex.util.Tools;

import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.WIFI_CHECK;


/**
 * Adapter for Featured Movies.
 *
 * @author Yobex.
 */
public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder>{

    private List<Media> castList;
    private SharedPreferences preferences;
    private AuthManager authManager;
    private Context context;
    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public void addFeatured(List<Media> castList, Context context, SharedPreferences preferences, MovieRepository movieRepository,AuthManager authManager){
        this.castList = castList;
        this.context = context;
        this.preferences = preferences;
        this.movieRepository = movieRepository;
        this.authManager = authManager;
        notifyDataSetChanged();


    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowItemFeaturedBinding binding = RowItemFeaturedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);



        return new FeaturedViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {

        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {

            if(castList.size() < 10){

                return castList.size();
            }else {

                return 10;
            }

        } else {
            return 0;
        }
    }


    class FeaturedViewHolder extends RecyclerView.ViewHolder {


        private final RowItemFeaturedBinding binding;

        FeaturedViewHolder (@NonNull RowItemFeaturedBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        void onBind(final int position) {


            final Media media = castList.get(position);


            if (media.getSeasons() !=null) {

                binding.movietitle.setText(media.getName());

                for (Genre genre : media.getGenres()) {
                    binding.mgenres.setText(genre.getName());
                }


                binding.infoTrailer.setOnClickListener(v -> {


                    if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                        DialogHelper.showWifiWarning(context);

                    } else {


                        Intent intent = new Intent(context, TrailerPreviewActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);
                        Animatoo.animateFade(context);
                    }


                });


                binding.rootLayout.setOnClickListener(view -> {

                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

                });

                binding.PlayButtonIcon.setOnClickListener(view -> {

                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

                });


                if (media.getPremuim() == 1) {

                    binding.moviePremuim.setVisibility(View.VISIBLE);


                }else {

                    binding.moviePremuim.setVisibility(View.GONE);
                }


            }else {


                onLoadMovies(media);

            }



            binding.addToFavorite.setOnClickListener(view -> addFavorite(media));
            checkMovieFavorite(media);
            Tools.onLoadMediaCover(context,binding.itemMovieImage,(media).getPosterPath());



        }

        public void addFavorite(Media mediaDetail) {

            if (movieRepository.isFeaturedFavorite(Integer.parseInt(mediaDetail.getTmdbId()))) {

                Timber.i("Removed From Watchlist");
                compositeDisposable.add(Completable.fromAction(() -> movieRepository.removeFavorite(mediaDetail))
                        .subscribeOn(Schedulers.io())
                        .subscribe());

                binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

            }else {

                Timber.i("Added To Watchlist");
                compositeDisposable.add(Completable.fromAction(() -> movieRepository.addFavorite(mediaDetail))
                        .subscribeOn(Schedulers.io())
                        .subscribe());

                binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                Toast.makeText(context, "Added To Watchlist", Toast.LENGTH_SHORT).show();
            }


        }



        private void onLoadMovies(Media media) {

            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }

            binding.infoTrailer.setOnClickListener(v -> {


                if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                    DialogHelper.showWifiWarning(context);

                }else {

                    Intent intent = new Intent(context, TrailerPreviewActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);
                    Animatoo.animateFade(context);
                }

            });


            binding.movietitle.setText(media.getTitle());

            for (Genre genre : media.getGenres()) {
                binding.mgenres.setText(genre.getName());
            }

            binding.rootLayout.setOnClickListener(view -> {

                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                context.startActivity(intent);

            });

            binding.PlayButtonIcon.setOnClickListener(view -> onLoadFeaturedStream(media));

        }


        private void onLoadFeaturedStream(Media media) {

            if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                DialogHelper.showWifiWarning(context);

            }else {

                if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 0) {

                    DialogHelper.showPremuimWarning(context);



                }else {


                    if (media.getVideos() !=null && !media.getVideos().isEmpty()) {


                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.TUBI_MEDIA_KEY, MediaModel.media(media.getTmdbId(), media.getGenres().get(0).getName(), media.getVideos().get(0).getServer(), "0", media.getTitle(),
                                media.getVideos().get(0).getLink(), media.getBackdropPath(), null, null
                                , null, null,
                                null, null,
                                null,
                                null,
                                null,String.valueOf(media.getPremuim()),null));
                        context.startActivity(intent);
                        Animatoo.animateFade(context);


                    } else {

                        DialogHelper.showNoStreamAvailable(context);


                    }


                }

            }

        }


        private void checkMovieFavorite(Media featured) {

            if (movieRepository.isFeaturedFavorite(Integer.parseInt(featured.getTmdbId()))) {

                binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

            } else {

                binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

            }
        }


    }




    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        compositeDisposable.clear();
    }


}
