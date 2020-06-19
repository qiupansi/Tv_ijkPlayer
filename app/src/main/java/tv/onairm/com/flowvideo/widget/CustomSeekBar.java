package tv.onairm.com.flowvideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class CustomSeekBar extends android.support.v7.widget.AppCompatSeekBar {
    private OnKeySeekBarChangeListener mOnKeySeekBarChangeListener;

    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setmOnKeySeekBarChangeListener(OnKeySeekBarChangeListener onKeySeekBarChangeListener) {
        this.mOnKeySeekBarChangeListener = onKeySeekBarChangeListener;
    }

    public interface OnKeySeekBarChangeListener {
        void onKeyStartTrackingTouch();

        void onKeyStopTrackingTouch();
    }

    void onKeyChange() {
        if (null != mOnKeySeekBarChangeListener) {
            mOnKeySeekBarChangeListener.onKeyStartTrackingTouch();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (null != mOnKeySeekBarChangeListener) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mOnKeySeekBarChangeListener.onKeyStopTrackingTouch();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
