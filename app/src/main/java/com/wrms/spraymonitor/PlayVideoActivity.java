package com.wrms.spraymonitor;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.wrms.spraymonitor.dataobject.VideoData;

import java.io.File;

/**
 * Created by WRMS on 31-10-2015.
 */
public class PlayVideoActivity  extends Activity {
    private VideoView video;
    private MediaController ctlr;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_video_play);

        VideoData data = getIntent().getParcelableExtra("data");

        File clip=new File(data.getVideo_path());

        if (clip.exists()) {

            video=(VideoView)findViewById(R.id.video);
            video.setVideoPath(clip.getAbsolutePath());

            ctlr=new MediaController(this);
            ctlr.setMediaPlayer(video);
            video.setMediaController(ctlr);
            video.requestFocus();
            video.start();
        }
    }
}