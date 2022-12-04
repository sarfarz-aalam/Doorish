package com.pentaware.doorish;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pentaware.doorish.model.Video;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterVideoListing extends RecyclerView.Adapter<AdapterVideoListing.YoutubeViewHolder> {

    private List<VideoListing> m_lstVideos;
    private static final String TAG = AdapterVideoListing.class.getSimpleName();
    private Context m_Context;

    IPlayVideo mPlayVideo;

    public AdapterVideoListing(List<VideoListing> lstVideos, Context ctx, IPlayVideo playVideo){
        this.m_lstVideos = lstVideos;
        m_Context = ctx;
        mPlayVideo = playVideo;
    }

    private String extractVid (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }
    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_video_listing, parent, false);
        return new YoutubeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeViewHolder holder, int position) {

        VideoListing listItem = m_lstVideos.get(position);
        final Video video = listItem.getVideo();

        holder.videoTitle.setText(video.title);

        holder.videoId = extractVid(video.video_url);

        /*  initialize the thumbnail image view , we need to pass Developer Key */
        holder.videoThumbnailImageView.initialize(CommonVariables.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                //when initialization is sucess, set the video id to thumbnail to load
                youTubeThumbnailLoader.setVideo(extractVid(video.video_url));

                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        //print or show error when thumbnail load failed
                        Log.e(TAG, "Youtube Thumbnail Error");
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //print or show error when initialization failed
                Log.e(TAG, "Youtube Initialization Failure");

            }
        });


    }

    @Override
    public int getItemCount() {
        return m_lstVideos.size();
    }

    public class YoutubeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public YouTubeThumbnailView videoThumbnailImageView;
        public TextView videoTitle, videoDuration;
        public String videoId;

        public YoutubeViewHolder(View itemView) {
            super(itemView);
            videoThumbnailImageView = itemView.findViewById(R.id.video_thumbnail_image_view);
            videoTitle = itemView.findViewById(R.id.video_title_label);
            //  videoDuration = itemView.findViewById(R.id.video_duration_label);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {

                VideoListing listItem = m_lstVideos.get(pos);
                Video video = listItem.getVideo();
                String videoId = extractVid(video.video_url);
                mPlayVideo.PlayVideo(videoId);

//                YoutubeVideoModel model = youtubeVideoModelArrayList.get(pos);
//                String videoId = model.getVideoId();
//                mPlayVideo.PlayVideo(videoId);
            }

        }
    }
}

