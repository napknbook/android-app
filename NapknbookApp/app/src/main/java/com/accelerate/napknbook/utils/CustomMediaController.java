package com.accelerate.napknbook.utils;

import android.content.Context;
import android.widget.MediaController;

public class CustomMediaController extends MediaController {

    private MediaControllerListener listener;

    public CustomMediaController(Context context) {
        super(context);
    }

    public void setMediaControllerListener(MediaControllerListener listener) {
        this.listener = listener;
    }








    public interface MediaControllerListener {
        void onPause();
        void onPlay();
    }
}