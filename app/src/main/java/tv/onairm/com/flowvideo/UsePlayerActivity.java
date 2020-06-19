package tv.onairm.com.flowvideo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.onairm.com.flowvideo.widget.CustomSeekBar;
import tv.onairm.com.flowvideo.widget.ProgressTextView;

public class UsePlayerActivity extends Activity implements SurfaceHolder.Callback {
    protected ProgressTextView progressText;
    protected CustomSeekBar videoSeekBar;
    IjkMediaPlayer player;
    SurfaceView surface;
    SurfaceHolder surfaceHolder;
    private FrameLayout mLoadingLayout;
    // 进度条最大值
    private static int MAX_VIDEO_SEEK = 0;
    // 目标进度
    private int mTargetPosition = 0;
    private int mTargetProgress = 0;
    private int mSpaceTime = 1000;//单位毫秒(多长时间更新一下进度条)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_use_player);
        initView();
        initData();
        initVideo();
    }

    private void initData() {
        player = MainApplication.getMediaPlayer();
        surfaceHolder = surface.getHolder();
        surfaceHolder.addCallback(this);
        videoSeekBar.requestFocus();
    }

    public void initVideo() {
        mProgressHandler.sendMessage(Message.obtain());
        setMaxProgress();
        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                if (null != player) {
                    player.isPrepared = true;
                }
                mLoadingLayout.setVisibility(View.GONE);

                mProgressHandler.sendMessage(Message.obtain());
                setMaxProgress();

            }
        });

        player.addOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        player.isBuffering = true;
                        mLoadingLayout.setVisibility(View.VISIBLE);
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        player.isBuffering = false;
                        mLoadingLayout.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                //播放完成的处理
            }
        });

        player.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                //播放出错的处理
                return false;
            }
        });
        videoSeekBar.setOnSeekBarChangeListener(mSeekListener);
        videoSeekBar.setmOnKeySeekBarChangeListener(new CustomSeekBar.OnKeySeekBarChangeListener() {
            @Override
            public void onKeyStartTrackingTouch() {
                mProgressHandler.removeMessages(10);
            }

            @Override
            public void onKeyStopTrackingTouch() {
                player.seekTo(mTargetPosition);
                mProgressHandler.sendMessage(Message.obtain());
            }
        });
    }

    private void initView() {
        mLoadingLayout = (FrameLayout) findViewById(R.id.flLoading);
        surface = (SurfaceView) findViewById(R.id.surface);
        progressText = (ProgressTextView) findViewById(R.id.progressText);
        videoSeekBar = (CustomSeekBar) findViewById(R.id.videoSeekBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 可见时尝试继续播放
        if (player != null) {
            player.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(surfaceHolder);
        player.start();
        if (!player.isBuffering&&player.isPrepared) {
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private Handler mProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long pos = _setProgress();
            Message msg2 = Message.obtain();
            msg2.what = 10;
            sendMessageDelayed(msg2, mSpaceTime - (pos % mSpaceTime));
        }
    };

    /**
     * 更新进度条
     *
     * @return
     */
    private long _setProgress() {
        if (!player.isPlaying()) {
            return 0;
        }
        // 视频播放的当前进度
        long position = player.getCurrentPosition();
        // 视频总的时长
        long duration = player.getDuration();
        if (duration > 0) {
            // 转换为 Seek 显示的进度值
            int mProgress = (int) ((position / (float) duration) * MAX_VIDEO_SEEK);
            mTargetPosition = (mTargetPosition > player.getCurrentPosition()) ? mTargetPosition : (int) player.getCurrentPosition();
            if (mProgress >= mTargetProgress) {
                updateProgressTip(mProgress, mTargetPosition);
            } else {
                updateProgressTip(mTargetProgress, mTargetPosition);
            }
        }
        // 返回当前播放进度
        return position;
    }

    private void updateProgressTip(int progress, int position) {
        long duration = player.getDuration();
        progressText.setProgress(progress, formatDuring(position) + "/"
                + formatDuring(duration));
        videoSeekBar.setProgress(progress);

    }

    public static String formatDuring(long time) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = (time) / hh;
        long minute = (time - hour * hh) / mi;
        long second = (time - hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        if (hour > 99) {
            return ">99h";
        } else if (hour > 0) {
            return strHour + ":" + strMinute + ":" + strSecond;
        } else {
            return strMinute + ":" + strSecond;
        }

    }

    private void setMaxProgress() {
        long duration = player.getDuration();
        int seconds = (int) (duration / mSpaceTime);
        MAX_VIDEO_SEEK = seconds;
        videoSeekBar.setMax(MAX_VIDEO_SEEK);
        videoSeekBar.setKeyProgressIncrement(MAX_VIDEO_SEEK / 100);
        videoSeekBar.setMax(MAX_VIDEO_SEEK);
        progressText.initData(MAX_VIDEO_SEEK);
    }
    /**
     * SeekBar监听
     */
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar bar) {
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
            long duration = player.getDuration();
            if (fromUser) {
                mProgressHandler.removeMessages(10);
                // 计算目标位置
                mTargetPosition = (int) (progress / (float) MAX_VIDEO_SEEK * duration);
                mTargetProgress = progress;
                updateProgressTip(progress, mTargetPosition);
            }

        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
        }
    };
}
