package aperr.android.questionsdescience;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class Quiz extends Activity {

    public static Config config;
    public static Config vue;
    public static Phase phase;
    public static boolean display;


    public static Boolean firstStart = true;
    public static Boolean stateValidation, stateSuite, timeoutQuestion, goodAnswer;
    public static int nbAnswered;
    public static String question,explication,nbAnswers,nbOk,answer1,answer2,answer3,answer4;;

    public static int numQuestion;
    public static int secs; // Temps restant pour la question
    public static int maNote;

    public static int numErreur = 0;
    public static int lgTrace[] = {0,0,0,0,0,0,0,0,0,0,0,0};
    public static int typTrace[] = {1,2,3,1,3,2,0,2,3,3,3,3};
    public static float coordPendu[] = {78F, 735F, 425F, 735F, 123F, 735F,
                               123F, 328F, 123F, 670F, 185F, 735F,
                               78F, 328F, 425F, 328F, 123F, 393F,
                               185F, 328F, 362F, 328F, 362F, 385F,
                               0F, 0F, 0F, 0F, 362F, 456F, 362F, 615F,
                               362F, 526F, 428F, 483F, 362F, 526F, 289F, 486F,
                               362F, 615F, 429F, 653F, 362F, 615F, 287F, 656F};

    public static int x_Appreciation;
    public static String t_Appreciation;

    public static Boolean ok;
    public static Boolean finQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i("alain", "Enter in Quiz onCreate "  + "\n");
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        phase = Phase.jeu;
        finQuiz = false;
        firstStart = true;
        stateValidation = true;
        stateSuite = false;
        nbAnswered = 0;
        numQuestion = 0;
        maNote = 0;
        numErreur = 0;
        for(int i=0; i<=11; i++) {
            lgTrace[i]=0;
        }


        setContentView(R.layout.activity_quiz);

        String sid = Integer.toString(Jeu.id);
        new lastquiz2().execute(sid);


        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Quiz.stateValidation = true;
        Quiz.stateSuite = false;
        Quiz.timeoutQuestion = false;


        Question questionFrag = new Question();
        ft.add(R.id.fragment_jeu, questionFrag);

        Pendu penduFrag = new Pendu();
        ft.add(R.id.fragment_pendu, penduFrag);

        //ft.addToBackStack(null);
        ft.commit();

    }

    private class lastquiz2 extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/lastquiz2.php?id=" + params[0];
            String url = "http://aperrault.atspace.cc/lastquiz2.php?id=" + params[0];
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();

                int connecttimeout = conn.getConnectTimeout();
                //Log.i("alain", "connecttimeout = "  + Integer.toString(connecttimeout) + "\n");

                int status = conn.getResponseCode();

                if(status == 200 || status == 204){
                    return true;

                }else{
                    //Log.i("alain", "Status HTTP = "  + Integer.toString(status) + "\n");
                    return false;

                }


            }catch(Exception e){
                //Log.i("alain", "Erreur HTTP = "  + e + "\n");
                return false;

            }finally {
                conn.disconnect();

            }

        }
        protected void onPostExecute(Boolean success){
            if(!success){
                if (Locale.getDefault().getLanguage().equals("fr")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Erreur réseau", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }




    @Override
    public void onResume(){
        //Log.i("alain", "Enter in Quiz onResume "  + "\n");
        super.onResume();

    }

    @Override
    public void onPause(){
        //Log.i("alain", "Enter in Quiz onPause "  + "\n");
        super.onPause();
    }

    @Override
    public void onStop(){
        //Log.i("alain", "Enter in Quiz onStop "  + "\n");
        super.onStop();
    }


    @Override
    protected void onDestroy(){
        //Log.i("alain", "Enter in Quiz onDestroy "  + "\n");
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
