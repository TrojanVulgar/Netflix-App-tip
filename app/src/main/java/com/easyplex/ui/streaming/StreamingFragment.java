package com.easyplex.ui.streaming;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.easyplex.R;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.databinding.FragmentStreamingBinding;
import com.easyplex.di.Injectable;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import javax.inject.Inject;



public class StreamingFragment extends Fragment implements Injectable {


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    AuthManager authManager;

    FragmentStreamingBinding binding;
    StreamingAdapter mStreamingAdapter;
    private boolean mStreamingLoaded;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_streaming, container, false);



        return binding.getRoot();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onLoadNestedToolbar();
        onLoadToolbar();


        setHasOptionsMenu(true);


        StreamingDetailViewModel streamingDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(StreamingDetailViewModel.class);
        streamingDetailViewModel.getStreaming();
        streamingDetailViewModel.latestStreamingMutableLiveData.observe(getViewLifecycleOwner(), this::onChanged);
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onLoadNestedToolbar() {

        Tools.loadAppBar(binding.scrollView,binding.toolbar);


    }


    private void onLoadToolbar() {

        Tools.loadToolbar(((AppCompatActivity)getActivity()),binding.toolbar,null);
        Tools.loadMiniLogo(getContext(),binding.logoImageTop);

    }

    private void onChanged(MovieResponse streaming) {

        mStreamingAdapter = new StreamingAdapter();
        binding.recyclerViewStreaming.setAdapter(mStreamingAdapter);
        binding.recyclerViewStreaming.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewStreaming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getActivity(), 0), true));
        binding.recyclerViewStreaming.setHasFixedSize(true);

        mStreamingAdapter.addStreaming(streaming.getStreaming(),authManager);


        mStreamingLoaded = true;
        checkAllDataLoaded();


        if (mStreamingAdapter.getItemCount() == 0)

            binding.noResults.setVisibility(View.VISIBLE);
        else {
            binding.noResults.setVisibility(View.GONE);
        }
    }




    private void checkAllDataLoaded() {
        if (mStreamingLoaded ) {
            binding.scrollView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerViewStreaming.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding =null;

    }
}
