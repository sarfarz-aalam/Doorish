package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pentaware.doorish.model.Video;

public class VideoListing extends AppCompatActivity {

    private Video mVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_listing);

        //  YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube_player_view);
        //youTubePlayerVi
    }

    public VideoListing(Video video){
        this.mVideo = video;
    }

    public Video getVideo(){
        return this.mVideo;
    }
}

