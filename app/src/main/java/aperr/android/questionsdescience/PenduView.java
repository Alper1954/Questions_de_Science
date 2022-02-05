package aperr.android.questionsdescience;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by perrault on 01/09/2017.
 */
public class PenduView extends SurfaceView implements Runnable {
    Activity context;

    Thread mThread = null;
    SurfaceHolder mSurfaceHolder;
    volatile boolean running = false;

    Bitmap frameBuffer;
    Canvas bufferCanvas;

    AndroidInput input;

    Paint paint1, paint2, paint3, paint4, paint5, paint51, paint6, paint61, paint7;
    int posx;
    int x0_finquiz;
    int x1_finquiz;
    String finquiz, tnote;
    int x_note;

    private final static int FRAMES_PER_SECOND = 60;
    private final static int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;


    public PenduView(Activity context) {
        super(context);
        this.context = context;
        mSurfaceHolder = getHolder();

        frameBuffer = Bitmap.createBitmap(500, 850, Bitmap.Config.RGB_565);
        bufferCanvas = new Canvas(frameBuffer);

        //Log.i("alain", "PenduView framebuffer width: " + Integer.toString(frameBuffer.getWidth()) + "\n");
        //Log.i("alain", "PenduView framebuffer height: " + Integer.toString(frameBuffer.getHeight()) + "\n");

        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Log.i("alain", "PenduView screen width: " + Integer.toString(metrics.widthPixels) + "\n");
        //Log.i("alain", "PenduView screen height: " + Integer.toString(metrics.heightPixels) + "\n");


        float screenHeight = (float) metrics.heightPixels;;
        float screenWidth = (float) metrics.widthPixels;
        float fragWidth = screenWidth/3;

        float scaleX = (float) frameBuffer.getWidth() / fragWidth;
        float scaleY = (float) frameBuffer.getHeight() / screenHeight;

        input = new AndroidInput(this, scaleX, scaleY);


        if (Locale.getDefault().getLanguage().equals("fr")) {
            finquiz = "Fin du Quiz";
            tnote = "Note: ";
            x0_finquiz = -370;
            x1_finquiz = 80;
            x_note = 160;
        }else{
            finquiz = "End of the Quiz";
            tnote = "Mark: ";
            x0_finquiz = -370;
            x1_finquiz = 45;
            x_note = 160;
        }


        paint1 = new Paint();
        paint1.setTypeface(Jeu.police);
        paint1.setTextSize(60);
        paint1.setColor(Color.WHITE);

        paint2 = new Paint();
        paint2.setTypeface(Jeu.police);
        paint2.setTextSize(90);
        paint2.setColor(Color.WHITE);

        paint3 = new Paint();
        paint3.setTypeface(Jeu.police);
        paint3.setTextSize(75);
        paint3.setColor(Color.WHITE);

        paint4 = new Paint();
        paint4.setTypeface(Jeu.police);
        paint4.setTextSize(60);
        paint4.setColor(Color.YELLOW);

        paint5 = new Paint();
        paint5.setColor(Color.WHITE);
        paint5.setStrokeWidth(10);

        paint51 = new Paint();
        paint51.setColor(Color.rgb(128,128,128));
        paint51.setStrokeWidth(10);

        paint6 = new Paint();
        paint6.setColor(Color.WHITE);
        paint6.setStrokeWidth(10);
        paint6.setStyle(Paint.Style.STROKE);

        paint61 = new Paint();
        paint61.setColor(Color.rgb(128,128,128));
        paint61.setStrokeWidth(10);
        paint61.setStyle(Paint.Style.STROKE);

        paint7 = new Paint();
        //paint7.setTypeface(Jeu.police);
        paint7.setTextSize(40);
        paint7.setColor(Color.BLACK);

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inScaled = false;
        //cahier = BitmapFactory.decodeResource(getResources(), R.drawable.cahier, options);

    }

    public void resume(){
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

    private void update(){
            List<TouchEvent> touchEvents = input.getTouchEvents();
            int len = touchEvents.size();
            for(int  i=0; i<len; i++){
                TouchEvent event = touchEvents.get(i);
                if(event.type == TouchEvent.TOUCH_UP) {

                    if (Quiz.vue == Config.Explication){
                        //Log.i("alain", "x = " + Integer.toString(event.x) + "\n");
                        //Log.i("alain", "y = " + Integer.toString(event.y) + "\n");
                        if (event.x > 360 && event.y > 750) {
                            //Log.i("alain", "popBackStack " + "\n");

                            FragmentManager fm = context.getFragmentManager();
                            fm.popBackStack();
                        }
                    }
                    if(Quiz.config == Config.Question) {
                        if ((event.x > 240 && event.x < 330) && (event.y > 770 && event.y < 840)) {
                            if(Jeu.ttsInstalled){
                                if(Jeu.ttsEnabled){
                                    Question.initQueue("");
                                    Jeu.ttsEnabled = false;
                                } else {
                                    Jeu.ttsEnabled = true;
                                }
                            }
                        }
                        if(Quiz.phase == Phase.fin) {
                            //Log.i("alain", "x = " + Integer.toString(event.x) + "\n");
                            //Log.i("alain", "y = " + Integer.toString(event.y) + "\n");
                            if ((event.x > 370 && event.x < 490) && (event.y > 645 && event.y < 740)) {
                                context.finish();
                            }
                        }
                    }
                }
            }
    }


    private void doDraw(){

        if(Quiz.config == Config.Question) {

            if(Quiz.phase == Phase.jeu) {
                if(Jeu.ttsInstalled){
                    if(Jeu.ttsEnabled){
                        bufferCanvas.drawBitmap(Jeu.pendu_tableau_on, 0, 0, null);
                    }else{
                        bufferCanvas.drawBitmap(Jeu.pendu_tableau_off, 0, 0, null);
                    }
                }else{
                    bufferCanvas.drawBitmap(Jeu.pendu_tableau_disabled, 0, 0, null);
                }
                bufferCanvas.drawText(String.format("Q: %2d", Quiz.numQuestion), 60, 820, paint1);
                bufferCanvas.drawText(String.format("%2d", Quiz.secs), 430, 820, paint7);

                //bufferCanvas.drawLine(78,735,425,735,paint5);

                //for(int n=1; n<=1; n++){
                    //Log.i("alain", Integer.toString(n) + "\n");
                    //bufferCanvas.drawLine(78,735,425,735,paint5);
                //}

                if(Quiz.finQuiz) {
                    Quiz.phase = Phase.finQuiz;
                    posx = x0_finquiz;
                }else{
                    bufferCanvas.drawBitmap(Jeu.dessins, 0, 0, null);
                    if((Quiz.stateSuite)||(Quiz.timeoutQuestion)){
                        if(Quiz.goodAnswer){
                            bufferCanvas.drawBitmap(Jeu.ok, 0, 0, null);
                        }else{
                            bufferCanvas.drawBitmap(Jeu.nok, 0, 0, null);
                        }
                    }
                }

                print_pendu(paint5, paint6);



            }else if(Quiz.phase == Phase.finQuiz){
                if(Jeu.ttsInstalled){
                    if(Jeu.ttsEnabled){
                        bufferCanvas.drawBitmap(Jeu.pendu_tableau_on, 0, 0, null);
                    }else{
                        bufferCanvas.drawBitmap(Jeu.pendu_tableau_off, 0, 0, null);
                    }
                }else{
                    bufferCanvas.drawBitmap(Jeu.pendu_tableau_disabled, 0, 0, null);
                }
                bufferCanvas.drawText(String.format("Q: %2d", Quiz.numQuestion), 60, 820, paint1);
                bufferCanvas.drawText(String.format("%2d", Quiz.secs), 430, 820, paint7);
                posx = posx + 15;
                if(posx >= x1_finquiz){
                    posx = x1_finquiz;
                    Quiz.phase = Phase.fin;
                }
                if (Locale.getDefault().getLanguage().equals("fr")) {
                    bufferCanvas.drawText(finquiz, posx, 100, paint2);
                }else{
                    bufferCanvas.drawText(finquiz, posx, 100, paint3);
                }
                print_pendu(paint5, paint6);


            }else if(Quiz.phase == Phase.fin){
                if(Jeu.ttsInstalled){
                    if(Jeu.ttsEnabled){
                        bufferCanvas.drawBitmap(Jeu.pendu_tableau_on, 0, 0, null);
                    }else{
                        bufferCanvas.drawBitmap(Jeu.pendu_tableau_off, 0, 0, null);
                    }
                }else{
                    bufferCanvas.drawBitmap(Jeu.pendu_tableau_disabled, 0, 0, null);
                }
                bufferCanvas.drawText(String.format("Q: %2d", Quiz.numQuestion), 60, 820, paint1);
                bufferCanvas.drawText(String.format("%2d", Quiz.secs), 430, 820, paint7);

                if (Locale.getDefault().getLanguage().equals("fr")) {
                    bufferCanvas.drawText(finquiz, posx, 100, paint2);
                }else{
                    bufferCanvas.drawText(finquiz, posx, 100, paint3);
                }

                bufferCanvas.drawText(tnote + Quiz.maNote, x_note, 175, paint4);
                bufferCanvas.drawText(Quiz.t_Appreciation, Quiz.x_Appreciation, 250, paint4);

                print_pendu(paint51, paint61);

                bufferCanvas.drawBitmap(Jeu.arrow_fin, 0, 0, null);
            }

            Quiz.vue = Config.Question;
            //Log.i("alain", "Question "  + "\n");
        }else{

            //List<TouchEvent> touchEvents = input.getTouchEvents();
            //touchEvents.clear();

            bufferCanvas.drawBitmap(Jeu.einstein_explication, 0, 0, null);
            Quiz.vue = Config.Explication;
            Quiz.display = false;
            //Log.i("alain", "Explication "  + "\n");
        }
    }

    private void print_pendu(Paint paint1, Paint paint2){
        if(Quiz.numErreur>0){

            for(int i=1;i<=Quiz.numErreur;i++){
                for(int j=1;j<=i;j++){
                    if (j == 7) {
                        pendu_cercle(6, paint2);
                    }else{
                        pendu_trait(j-1, paint1);
                    }
                }
            }
        }
    }


    private void pendu_trait(int numE, Paint paint){
        int lgT = Quiz.lgTrace[numE];
        int typT = Quiz.typTrace[numE];
        float startX=Quiz.coordPendu[numE*4];
        float startY=Quiz.coordPendu[numE*4+1];
        float stopX=Quiz.coordPendu[numE*4+2];
        float stopY=Quiz.coordPendu[numE*4+3];
        if(lgT == 1000){
            bufferCanvas.drawLine(startX,startY,stopX,stopY,paint);
        }else{
            //if(numE==7){
                //Log.i("alain", "lgT before = " + Integer.toString(lgT) + "\n");
            //}
            lgT=lgT+64;
            //if(numE==7){
                //Log.i("alain", "lgT after = " + Integer.toString(lgT) + "\n");
            //}
            if(lgT>1000) lgT=1000;
            switch(typT){
                case 1: float stopX1=startX+(stopX-startX)*lgT/1000;
                    bufferCanvas.drawLine(startX,startY,stopX1,stopY,paint);
                    break;
                case 2: float stopY1=startY+(stopY-startY)*lgT/1000;
                    bufferCanvas.drawLine(startX,startY,stopX,stopY1,paint);
                    break;
                case 3: float stopXX1=startX+(stopX-startX)*lgT/1000;
                    float stopYY1=startY+(stopY-startY)*lgT/1000;
                    bufferCanvas.drawLine(startX,startY,stopXX1,stopYY1,paint);
                    break;
            }
            Quiz.lgTrace[numE]=lgT;
        }
    }

    private void pendu_cercle(int numE, Paint paint){
        RectF head=new RectF(322,385,404,456);
        int lgT = Quiz.lgTrace[numE];
        if(lgT == 360){
            bufferCanvas.drawArc(head, 270F, 360F, false, paint);
        }else {
            lgT = lgT + 12;
            if (lgT > 360) lgT = 360;
            float angle = 360*lgT/360;
            bufferCanvas.drawArc(head, 270F, angle, false, paint);
        }
        Quiz.lgTrace[numE] = lgT;
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

            if(Quiz.display){
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

}
