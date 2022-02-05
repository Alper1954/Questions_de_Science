package aperr.android.questionsdescience;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by perrault on 25/11/2017.
 */
public class JeuView extends SurfaceView implements Runnable {
    Activity context;
    Vue vue;
    SurfaceHolder mSurfaceHolder;
    Thread mThread = null;

    Bitmap frameBuffer;
    Canvas bufferCanvas;
    Paint paint, paint1;

    InputStream in;
    AndroidInput input;

    Bitmap jeu_menu, jeu_info, klaxon_on, klaxon_off, klaxon_disabled, questions, connexion, info;

    volatile boolean running = false;


    private final static int FRAMES_PER_SECOND = 60;
    private final static int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

    enum Vue {
        welcome, notes, info
    }
    enum ConfigVue {
        welcome, notes, info
    }
    public static ConfigVue configVue;

    boolean display;


    public JeuView(Activity context) {
        super(context);
        this.context = context;
        mSurfaceHolder = getHolder();

        frameBuffer = Bitmap.createBitmap(1500, 850, Bitmap.Config.RGB_565);
        bufferCanvas = new Canvas(frameBuffer);

        configVue = ConfigVue.welcome;
        display = true;

        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float screenHeight = (float) metrics.heightPixels;;
        float screenWidth = (float) metrics.widthPixels;

        float scaleX = (float) frameBuffer.getWidth() / screenWidth;
        float scaleY = (float) frameBuffer.getHeight() / screenHeight;

        input = new AndroidInput(this, scaleX, scaleY);



        AssetManager assets = context.getAssets();
        try {

            if (Locale.getDefault().getLanguage().equals("fr")) {
                in = assets.open("jeu_menu.png");
                jeu_menu = BitmapFactory.decodeStream(in);

                in = assets.open("jeu_info.png");
                jeu_info = BitmapFactory.decodeStream(in);

                in = assets.open("connexion.png");
                connexion = BitmapFactory.decodeStream(in);
            } else {
                in = assets.open("jeu_menu_en.png");
                jeu_menu = BitmapFactory.decodeStream(in);

                in = assets.open("jeu_info_en.png");
                jeu_info = BitmapFactory.decodeStream(in);

                in = assets.open("connexion_en.png");
                connexion = BitmapFactory.decodeStream(in);
            }

            in = assets.open("info.png");
            info = BitmapFactory.decodeStream(in);

            in = assets.open("questions.png");
            questions = BitmapFactory.decodeStream(in);

            in = assets.open("klaxon_on.png");
            klaxon_on = BitmapFactory.decodeStream(in);

            in = assets.open("klaxon_off.png");
            klaxon_off = BitmapFactory.decodeStream(in);

            in = assets.open("klaxon_disabled.png");
            klaxon_disabled = BitmapFactory.decodeStream(in);

            paint = new Paint();
            paint.setTypeface(Jeu.police);
            paint.setTextSize(60);
            paint.setColor(Color.YELLOW);

            paint1 = new Paint();
            paint1.setTypeface(Jeu.police);
            paint1.setTextSize(50);
            paint1.setColor(Color.YELLOW);

        }catch (IOException e){}

    }






    private void update() {

        List<TouchEvent> touchEvents = input.getTouchEvents();
        int len = touchEvents.size();
        for(int  i=0; i<len; i++){
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if ((event.x > 1400 && event.x < 1500) && (event.y > 0 && event.y < 90)) {
                    if(vue == Vue.welcome){
                        context.finish();
                    }
                }
                if ((event.x > 1190 && event.x < 1480) && (event.y > 170 && event.y < 255)) {
                    //Log.i("alain", "JeuView x = " + Integer.toString(event.x) + "\n");
                    //Log.i("alain", "JeuView y = " + Integer.toString(event.y) + "\n");
                    if(vue == Vue.welcome){
                        if(Jeu.connected){
                            Intent intent = new Intent(context, Notes.class);
                            context.startActivity(intent);
                        }
                    }
                }
                if (event.x > 1350 && event.y > 740){
                    if(vue == Vue.info){
                        configVue = ConfigVue.welcome;
                        display = true;
                    }
                }
                if ((event.x > 1260 && event.x < 1330) && (event.y > 320 && event.y < 420)) {
                    if(vue == Vue.welcome){
                        if(Jeu.connected){
                            configVue = ConfigVue.info;
                            display = true;
                        }
                    }
                }
                if ((event.x > 550 && event.x < 1050) && (event.y > 650 && event.y < 760)) {
                    //Log.i("alain", "JeuView x = " + Integer.toString(event.x) + "\n");
                    //Log.i("alain", "JeuView y = " + Integer.toString(event.y) + "\n");
                    if(vue == Vue.welcome){
                        if(Jeu.connected){
                            Intent intent = new Intent(context, Quiz.class);
                            context.startActivity(intent);
                        }
                    }
                }
                if ((event.x > 440 && event.x < 680) && (event.y > 650 && event.y < 770)) {
                    //Log.i("alain", "JeuView x = " + Integer.toString(event.x) + "\n");
                    //Log.i("alain", "JeuView y = " + Integer.toString(event.y) + "\n");
                    if(vue == Vue.welcome){
                        if(!Jeu.connected){
                            Intent intent = new Intent(context, Ncompte.class);
                            context.startActivity(intent);
                            context.finish();
                        }
                    }
                }
                if ((event.x > 920 && event.x < 1380) && (event.y > 650 && event.y < 770)) {
                    //Log.i("alain", "JeuView x = " + Integer.toString(event.x) + "\n");
                    //Log.i("alain", "JeuView y = " + Integer.toString(event.y) + "\n");
                    if(vue == Vue.welcome){
                        if(!Jeu.connected){
                            Intent intent = new Intent(context, Identification.class);
                            context.startActivity(intent);
                            context.finish();
                        }
                    }
                }
                if ((event.x > 1260 && event.x < 1335) && (event.y > 490 && event.y < 556)) {
                    if(vue == Vue.welcome){
                        if(Jeu.ttsInstalled){
                            if(Jeu.ttsEnabled){
                                Jeu.ttsEnabled = false;
                            } else {
                                Jeu.ttsEnabled = true;
                            }
                            display = true;
                        }
                    }
                }
            }
        }
    }


    private void doDraw(){
        if(configVue == ConfigVue.welcome) {

            bufferCanvas.drawBitmap(jeu_menu, 0, 0, null);

            if(Jeu.connected){

                bufferCanvas.drawBitmap(info, 0, 0, null);

                if(Jeu.ttsInstalled){
                    if(Jeu.ttsEnabled){
                        bufferCanvas.drawBitmap(klaxon_on, 1260, 490, null);
                    }else{
                        bufferCanvas.drawBitmap(klaxon_off, 1260, 490, null);
                    }
                }else{
                    bufferCanvas.drawBitmap(klaxon_disabled, 1260, 490, null);
                }

                bufferCanvas.drawText(Jeu.nom, 720, 585, paint);

                bufferCanvas.drawBitmap(questions, 0, 0, null);

                if (Locale.getDefault().getLanguage().equals("fr")) {
                    bufferCanvas.drawText("Notes/", 1240, 210, paint1);
                    bufferCanvas.drawText("Classement", 1190, 250, paint1);
                } else {
                    bufferCanvas.drawText("Marks/", 1240, 210, paint1);
                    bufferCanvas.drawText("Ranking", 1230, 251, paint1);
                }

            }else{

                bufferCanvas.drawBitmap(connexion, 0, 0, null);
            }



            vue = Vue.welcome;
            display = false;
        }
        if(configVue == ConfigVue.info) {

            bufferCanvas.drawBitmap(jeu_info, 0, 0, null);

            vue = Vue.info;
            display = false;
        }
    }

    public void run(){
        Canvas canvas;
        long startTime;
        long sleepTime;
        Rect dstRect = new Rect();

        while (running) {
            if(!mSurfaceHolder.getSurface().isValid()) continue;

            startTime = System.currentTimeMillis();

            update();

            if(display){
                canvas = mSurfaceHolder.lockCanvas();
                canvas.getClipBounds(dstRect);
                doDraw();
                canvas.drawBitmap(frameBuffer, null, dstRect, null);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }

            sleepTime = SKIP_TICKS - (System.currentTimeMillis() - startTime);

            try {
                if (sleepTime >= 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
            }
        }

    }

    public void resume(){
        //List<TouchEvent> touchEvents = input.getTouchEvents();
        //touchEvents.clear();

        display = true;
        running = true;
        mThread = new Thread(this);
        mThread.start();
    }

    public void pause() {
        running = false;
        boolean retry = true;
        while(retry){
            try{
                mThread.join();
                retry = false;
            }catch (InterruptedException e){}
        }
    }

}
