package aperr.android.questionsdescience;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class Jeu extends Activity {

    public static int nbNotes = 10;  //Nombre de notes composant une moyenne (doit être inférieur ou égal à 10)
    public static int nbQuestions =664 ;  //Nombre de questions contenues dans le fichier
    public static int lastQ = 20;  //Nombre de questions par questionnaire
    public static int timeQuestion = 30; // Temps max pour répondre à une question (en secondes)



    private JeuView mView = null;
    private int MY_DATA_CHECK_CODE = 0;
    InputStream in;

    public static Typeface police;
    public static Boolean connected;
    public static String nom, nom_N, nom_numQuestionsTirage;
    public static int id;
    public static String sid;
    public static int valueNotes[];
    public static String svalueNotes[];
    public static boolean validityNotes[];
    public static int somme;
    public static float moyenne;
    public static String sdatemoyenne;


    public static Bitmap einstein_explication, dessins, pendu_tableau_on, pendu_tableau_off, pendu_tableau_disabled, ok, nok, arrow_fin;
    public static Boolean ttsInstalled;
    public static Boolean ttsEnabled;
    public static int numQuestionsTirage[];  // Table des numéros de questions pour le tirage random courant
    public static int nbQuestionsTirage;  //Nombre de questions pour le tirage random courant
    public static int randomTirage; //flag tirage random pour la première série de questions puis tirage séquentiel suivant l'ordre du random obtenu
    public static int numTirageSeq; //Numéro du tirage séquentiel après le tirage random
    public static String qId; //Texte de la question sélectionnée



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Log.i("alain", "Jeu onCreate "  + "\n");

        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        police = Typeface.createFromAsset(getAssets(), "buxton-sketch.ttf");
        mView = new JeuView(this);
        setContentView(mView);

        valueNotes = new int[10];
        validityNotes = new boolean[10];
        svalueNotes = new String[10];
        //connected = false;
        //ttsInstalled = false;
        //ttsEnabled = false;


        createBitmap();

        Intent intent = getIntent();
        connected = intent.getBooleanExtra("connected", false);
        id = intent.getIntExtra("id", 0);
        nom = intent.getStringExtra("nom");
        nom_N = nom + "_N";
        nom_numQuestionsTirage = nom + "_numQuestionsTirage";

        record_installation();

        if(id != 0){
            //loadNotes();
            sid = Integer.toString(id);
            //Log.i("alain", "call read_data2 " + "\n");
            new read_data2().execute(sid);
            load_numQuestionsTirage();
        }

        if(!connected){
            ttsInstalled = false;
            ttsEnabled = false;
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ttsInstalled = true;
                ttsEnabled = true;

            }else{

                if (Locale.getDefault().getLanguage().equals("fr")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "La synthèse vocale n'est pas disponible", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "speech synthesis is not available", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }


    private void record_installation(){
        try {
            //Log.i("alain", "record_installation " + "\n");
            FileInputStream flux = openFileInput("test_installation");
            BufferedReader r = new BufferedReader(new InputStreamReader(flux));

            r.close();
            flux.close();

        }catch(Exception e){
            //Log.i("alain", "installation application " + "\n");

            new insert_installations_table().execute();

            try{
                FileOutputStream flux = openFileOutput("test_installation", Context.MODE_PRIVATE);
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(flux));
                w.write("appli installée" + "\n");

                w.close();
                flux.close();

            }catch(Exception e2){
                //Log.i("alain", "Erreur save test_installation = "  + e2 + "\n");
            }
        }
    }

    private class insert_installations_table extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/install2.php?application= science";
            String url = "http://aperrault.atspace.cc/install2.php?application= science";
            //Log.i("alain", "url = "  + url + "\n");
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();

                int timeout = conn.getConnectTimeout();
                //Log.i("alain", "timeout = "  + Integer.toString(timeout) + "\n");

                int status = conn.getResponseCode();
                //Log.i("alain", "status = "  + Integer.toString(status) + "\n");

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



    private void createBitmap(){
        AssetManager assets = getAssets();
        try {
            if (Locale.getDefault().getLanguage().equals("fr")) {

                in = assets.open("einstein_explication.png");
                einstein_explication = BitmapFactory.decodeStream(in);
            } else {

                in = assets.open("einstein_explication_en.png");
                einstein_explication = BitmapFactory.decodeStream(in);
            }

            in = assets.open("pendu_tableau_on.png");
            pendu_tableau_on = BitmapFactory.decodeStream(in);

            in = assets.open("pendu_tableau_off.png");
            pendu_tableau_off = BitmapFactory.decodeStream(in);

            in = assets.open("pendu_tableau_disabled.png");
            pendu_tableau_disabled = BitmapFactory.decodeStream(in);

            in = assets.open("dessins.png");
            dessins = BitmapFactory.decodeStream(in);

            in = assets.open("ok.png");
            ok = BitmapFactory.decodeStream(in);

            in = assets.open("nok.png");
            nok = BitmapFactory.decodeStream(in);

            in = assets.open("arrow_fin.png");
            arrow_fin = BitmapFactory.decodeStream(in);


        }catch (IOException e){}
    }

    private class read_data2 extends AsyncTask<String, Void, Boolean> {

        int erreur;
        String[] tab;
        //String vclassement;
        //String vntjoueurs;   //nombre de joueurs total
        //String vntjoueurs_c;  //nombre de joueurs classés  dernière moyenne datant de moins de 3 mois


        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/read_data2.php?id=" + sid;
            String url = "http://aperrault.atspace.cc/read_data2.php?id=" + sid;
            HttpURLConnection conn = null;
            InputStream is;

            try{
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int status = conn.getResponseCode();
                if(status == 200 || status == 204){
                    is = conn.getInputStream();

                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    tab = r.readLine().split(",");

                    //Log.i("alain", "dimension tab = " +Integer.toString(tab.length) + "\n");

                    //Log.i("alain", "tab[0]: " + tab[0]  + "\n");
                    //Log.i("alain", "tab[1]: " + tab[1]  + "\n");


                    for(int i=0; i<10; i++){
                        valueNotes[i]= Integer.parseInt(tab[i]);
                        if(valueNotes[i] == 9999){
                            validityNotes[i] = false;
                        }else{
                            validityNotes[i] = true;
                        }
                    }
                    if(tab.length == 12){
                        somme = Integer.parseInt(tab[10]);
                        moyenne = (float) somme/nbNotes;
                        sdatemoyenne = tab[11];

                        //Log.i("alain", "sdatemoyenne read from DB=  " + sdatemoyenne + "\n");
                    }


                    //Log.i("alain", "read_data2 OK = " + "\n");


                    return true;
                }else{

                    //Log.i("alain", "Status HTTP = "  + Integer.toString(status) + "\n");
                    erreur = 1;
                    return false;
                }

            }catch(Exception e){
                //Log.i("alain", "Erreur HTTP = "  + e + "\n");
                erreur = 2;
                return false;
            }finally {
                conn.disconnect();
            }
        }

        protected void onPostExecute(Boolean success){
            if(!success){
                //Log.i("alain", "Erreur = "  + Integer.toString(erreur) + "\n");

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



    private void load_numQuestionsTirage(){
        //Log.i("alain", "Enter in load_numQuestionsTirage " + "\n");
        try {
            FileInputStream flux = openFileInput(nom_numQuestionsTirage);
            BufferedReader r = new BufferedReader(new InputStreamReader(flux));

            numQuestionsTirage = new int[nbQuestions];
            

            for(int i=0; i<nbQuestions; i++){

                //Log.i("alain", "Line file: " + r.readLine()  + "\n");
                numQuestionsTirage[i] = Integer.parseInt(r.readLine());
            }
            nbQuestionsTirage = Integer.parseInt(r.readLine());
            randomTirage = Integer.parseInt(r.readLine());
            numTirageSeq = Integer.parseInt(r.readLine());


            r.close();
            flux.close();

        }catch(Exception e){
            //Log.i("alain", "Erreur load_nbQuestionsTirage = "  + e + "\n");

            numQuestionsTirage = new int[Jeu.nbQuestions];
            nbQuestionsTirage = Jeu.nbQuestions;
            randomTirage = 1;

            for(int i=0; i<Jeu.nbQuestionsTirage; i++){
                Jeu.numQuestionsTirage[i] = i+1;
            }

            //Log.i("alain", "Q1 = " + Integer.toString(Jeu.numQuestionsTirage[0]) + "\n");
            //Log.i("alain", "Q2 = " + Integer.toString(Jeu.numQuestionsTirage[1]) + "\n");
            //Log.i("alain", "Q3 = " + Integer.toString(Jeu.numQuestionsTirage[2]) + "\n");
            //Log.i("alain", "Q4 = " + Integer.toString(Jeu.numQuestionsTirage[3]) + "\n");
            //Log.i("alain", "Q5 = " + Integer.toString(Jeu.numQuestionsTirage[4]) + "\n");

            nbQuestionsTirage = Jeu.nbQuestions;
            randomTirage = 1;

        }
    }





    private void save_numQuestionsTirage(){
        if(numQuestionsTirage != null){
            try{
                //Log.i("alain", "Save file = "  + nom_numQuestionsTirage + "\n");

                FileOutputStream flux = openFileOutput(nom_numQuestionsTirage, Context.MODE_PRIVATE);
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(flux));

                for(int i=0; i<nbQuestions; i++){
                    w.write(Integer.toString(numQuestionsTirage[i]) + "\n");
                }
                w.write(Integer.toString(nbQuestionsTirage) + "\n");
                w.write(Integer.toString(randomTirage) + "\n");
                w.write(Integer.toString(numTirageSeq));

                w.close();
                flux.close();



            }catch(Exception e){
                //Log.i("alain", "Erreur save_nbQuestionsTirage = "  + e + "\n");
            }
        }
    }


    @Override
    protected void onResume(){

        //Log.i("alain", "Jeu onResume "  + "\n");

        super.onResume();
        mView.resume();

        if(id != 0){
            save_numQuestionsTirage();
        }

    }

    @Override
    protected void onPause(){

        //Log.i("alain", "Jeu onPause "  + "\n");

        super.onPause();
        mView.pause();

        if(isFinishing()){
            //Log.i("alain", "Jeu onPause/isFinishing "  + "\n");
            if(id != 0){
                save_numQuestionsTirage();
            }
        }
    }

}
