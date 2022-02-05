package aperr.android.questionsdescience;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Created by perrault on 12/07/2017.
 */
public class AndroidInput {
    KeyboardHandler keyHandler;
    TouchHandler touchHandler;

    public AndroidInput(View view, float scaleX, float scaleY) {
        keyHandler = new KeyboardHandler(view);
        touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
    }

    public boolean isKeyPressed(int keyCode){
        return keyHandler.isKeyPressed(keyCode);
    }

    public boolean isTouchDown(int pointer){
        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer){
        return touchHandler.getTouchX(pointer);
    }

    public int getTouchY(int pointer){
        return touchHandler.getTouchY(pointer);
    }

    public List<TouchEvent> getTouchEvents(){
        return touchHandler.getTouchEvents();
    }

    public List<KeyEvent> getKeyEvents(){
        return keyHandler.getKeyEvents();
    }
}
