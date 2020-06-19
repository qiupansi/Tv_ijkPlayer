package tv.onairm.com.flowvideo;

import android.app.Application;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Edison on 2017/6/25.
 */

public class MainApplication extends Application {
    private static IjkMediaPlayer ijkMediaPlayer;
    public static IjkMediaPlayer getMediaPlayer(){
        if(ijkMediaPlayer==null){
            ijkMediaPlayer = new IjkMediaPlayer();
        }
        return ijkMediaPlayer;
    }
}
