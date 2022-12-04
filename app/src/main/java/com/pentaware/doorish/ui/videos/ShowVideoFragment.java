package com.pentaware.doorish.ui.videos;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pentaware.doorish.AdapterVideoListing;
import com.pentaware.doorish.IPlayVideo;
import com.pentaware.doorish.R;
import com.pentaware.doorish.VideoListing;
import com.pentaware.doorish.YoutubePlayerActivity;
import com.pentaware.doorish.model.Video;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowVideoFragment extends Fragment implements IPlayVideo {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    IPlayVideo playVideo;

    private List<VideoListing> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private View mView;
    private ShowVideoViewModel mViewModel;
    TextView txtNoItemToDisplay;
    GifImageView gifImageView;
    // public static ShowVideo instance = null;


    public static ShowVideoFragment newInstance() {
        return new ShowVideoFragment();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.show_video_fragment, container, false);
        playVideo = this;
        return  mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShowVideoViewModel.class);
        // TODO: Use the ViewModel

        txtNoItemToDisplay = (TextView)mView.findViewById(R.id.noItemTodisplay);
        gifImageView = (GifImageView)mView.findViewById(R.id.gifView);
        listItems = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.videoRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));

        LoadVideos();
    }

    private void LoadVideos(){

        db.collection("videos")
                //.whereEqualTo("Active", true)
                .get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Video video = document.toObject(Video.class);
                                if(video.available_for.equals("Sellers")){
                                    continue;
                                }
                                VideoListing videoListing = new VideoListing(video);
                                listItems.add(videoListing);
                                // Log.d("TAG", "getting documents: ", task.getException());
                            }
                            if(listItems.size() ==0){
                                txtNoItemToDisplay.setVisibility(View.VISIBLE);
                            }else{
                                txtNoItemToDisplay.setVisibility(View.GONE);
                            }
                            adapter = new AdapterVideoListing(listItems, mView.getContext(),  playVideo);
                            recyclerView.setAdapter(adapter);
                            gifImageView.setVisibility(View.GONE);
                        }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    @Override
    public void PlayVideo(String vidoeId) {

        startActivity(new Intent(mView.getContext(), YoutubePlayerActivity.class)
                .putExtra("video_id", vidoeId));

    }
}

