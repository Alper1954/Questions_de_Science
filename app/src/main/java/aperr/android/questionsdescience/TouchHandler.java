package aperr.android.questionsdescience;

import android.view.View;

import java.util.List;

/**
 * Created by perrault on 05/07/2017.
 */
public interface TouchHandler extends View.OnTouchListener {
    public boolean isTouchDown(int pointer);
    public int getTouchX(int pointer);
    public int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
}
