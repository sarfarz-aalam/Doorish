package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity {

    private static final String TAG = YoutubePlayerActivity.class.getSimpleName();
    private String videoID;
    private YouTubePlayerView youTubePlayerView;
    // private String DEVELOPER_KEY = "AIzaSyDTayO4E-LF1dGCigpiCkDxKNd8ScNV-ZE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        //get the video id
        videoID = getIntent().getStringExtra("video_id");
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        initializeYoutubePlayer();
    }

    private void initializeYoutubePlayer() {
        youTubePlayerView.initialize(CommonVariables.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,
                                                boolean wasRestored) {

                //if initialization success then load the video id to youtube player
                if (!wasRestored) {
                    //set the player style here: like CHROMELESS, MINIMAL, DEFAULT
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    //load the video
                    youTubePlayer.loadVideo(videoID);

                    //OR

                    //cue the video
                    //youTubePlayer.cueVideo(videoID);

                    //if you want when activity start it should be in full screen uncomment below comment
                    //  youTubePlayer.setFullscreen(true);

                    //If you want the video should play automatically then uncomment below comment
                    //  youTubePlayer.play();

                    //If you want to control the full screen event you can uncomment the below code
                    //Tell the player you want to control the fullscreen change
                   /*player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                    //Tell the player how to control the change
                    player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean arg0) {
                            // do full screen stuff here, or don't.
                            Log.e(TAG,"Full screen mode");
                        }
                    });*/

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

            //@Override
//            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
//                //print or show error if initialization failed
//                Log.e(TAG, "Youtube Player View initialization failed");
//            }
        });
    }
}

