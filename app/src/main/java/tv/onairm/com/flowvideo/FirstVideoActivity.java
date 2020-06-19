package tv.onairm.com.flowvideo;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class FirstVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private String TAG = "FirstVideoActivity";
    IjkMediaPlayer player;
    SurfaceView surface;
    SurfaceHolder surfaceHolder;
    String path = "http://1253332079.vod2.myqcloud.com/2b9deca2vodgzp1253332079/4e2c75e59031868222973415604/f0.mp4";
    Button btnSwitch;
    FrameLayout flLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first_video);
        initViews();
        initVideo();
        initListeners();
        initData();
    }

    private void initViews() {
        btnSwitch = (Button) findViewById(R.id.btnSwitch);
        surface = (SurfaceView) findViewById(R.id.surface);
        flLoading = (FrameLayout) findViewById(R.id.flLoading);
    }

    private void initData() {
        btnSwitch.requestFocus();

    }

    private void initListeners() {
        btnSwitch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btnSwitch.setBackgroundColor(Color.parseColor("#f9a825"));
                }
            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstVideoActivity.this, UsePlayerActivity.class));
            }
        });
        player.addOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        player.isBuffering = true;
                        flLoading.setVisibility(View.VISIBLE);
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        player.isBuffering = false;
                        flLoading.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                player.isPrepared = true;
            }
        });
    }

    private void initVideo() {
        // 初始化播放器
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        surfaceHolder = surface.getHolder();

        surfaceHolder.addCallback(this);
        openVideo();
    }

    public void openVideo() {
        try {
            player = MainApplication.getMediaPlayer();

            player.setDataSource(path);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setScreenOnWhilePlaying(true);
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(surface.getHolder());
        player.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
