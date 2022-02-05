package aperr.android.questionsdescience;


import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class VosNotes extends Fragment {

    String ssomme;
    String smoyenne_c;
    String tab[];


    public VosNotes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vos_notes, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();

        TextView header_notes = (TextView) view.findViewById(R.id.header_notes);
        header_notes.setTypeface(Notes.police);
        TextView tmoyenne = (TextView) view.findViewById(R.id.tmoyenne);
        tmoyenne.setTypeface(Notes.police);
        TextView tclasse = (TextView) view.findViewById(R.id.tclasse);
        tclasse.setTypeface(Notes.police);
        TextView tclassement = (TextView) view.findViewById(R.id.tclassement);
        tclassement.setTypeface(Notes.police);
        TextView tntjoueurs = (TextView) view.findViewById(R.id.tntjoueurs);
        tntjoueurs.setTypeface(Notes.police);
        TextView tntjoueurs_c = (TextView) view.findViewById(R.id.tntjoueurs_c);
        tntjoueurs_c.setTypeface(Notes.police);

        if(Jeu.validityNotes[0]){
            TextView note1 = (TextView) view.findViewById(R.id.note1);
            note1.setText(Integer.toString(Jeu.valueNotes[0]));
            note1.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[1]){
            TextView note2 = (TextView) view.findViewById(R.id.note2);
            note2.setText(Integer.toString(Jeu.valueNotes[1]));
            note2.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[2]){
            TextView note3 = (TextView) view.findViewById(R.id.note3);
            note3.setText(Integer.toString(Jeu.valueNotes[2]));
            note3.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[3]){
            TextView note4 = (TextView) view.findViewById(R.id.note4);
            note4.setText(Integer.toString(Jeu.valueNotes[3]));
            note4.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[4]){
            TextView note5 = (TextView) view.findViewById(R.id.note5);
            note5.setText(Integer.toString(Jeu.valueNotes[4]));
            note5.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[5]){
            TextView note6 = (TextView) view.findViewById(R.id.note6);
            note6.setText(Integer.toString(Jeu.valueNotes[5]));
            note6.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[6]){
            TextView note7 = (TextView) view.findViewById(R.id.note7);
            note7.setText(Integer.toString(Jeu.valueNotes[6]));
            note7.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[7]){
            TextView note8 = (TextView) view.findViewById(R.id.note8);
            note8.setText(Integer.toString(Jeu.valueNotes[7]));
            note8.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[8]){
            TextView note9 = (TextView) view.findViewById(R.id.note9);
            note9.setText(Integer.toString(Jeu.valueNotes[8]));
            note9.setTextColor(Color.YELLOW);
        }
        if(Jeu.validityNotes[9]){
            TextView note10 = (TextView) view.findViewById(R.id.note10);
            note10.setText(Integer.toString(Jeu.valueNotes[9]));
            note10.setTextColor(Color.YELLOW);
        }

        if (Jeu.validityNotes[Jeu.nbNotes-1]){
            TextView moyenne = (TextView) view.findViewById(R.id.moyenne);

            String smoyenne = Float.toString(Jeu.moyenne);

            tab = new String[2];
            tab = smoyenne.split("\\.");
            if(tab[1].equals("0")){
                smoyenne_c = tab[0];
            }else{
                smoyenne_c = tab[0] + "." + tab[1];
            }
            moyenne.setText(smoyenne_c);

        }

        //print_calendar();

        //Log.i("alain", "moyenne = " + Float.toString(Jeu.moyenne) + "\n");
        //Log.i("alain", "somme = " + Integer.toString(Jeu.somme) + "\n");
        //Log.i("alain", "sdatemoyenne = " + Jeu.sdatemoyenne + "\n");

        ssomme = Integer.toString(Jeu.somme);
        new Read_ranking().execute(ssomme);

    }



    private class Read_ranking extends AsyncTask<String, Void, Boolean> {

        int erreur;
        String[] tab;
        String vclassement;
        String vntjoueurs;   //nombre de joueurs total
        String vntjoueurs_c;  //nombre de joueurs classés  dernière moyenne datant de moins de 3 mois


        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/read_ranking.php?somme=" + ssomme;
            String url = "http://aperrault.atspace.cc/read_ranking.php?somme=" + ssomme;
            //Log.i("alain", url + "\n");
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
                    //Log.i("alain", "tab[0]: " + tab[0]  + "\n");
                    //Log.i("alain", "tab[1]: " + tab[1]  + "\n");
                    vclassement = Integer.toString(Integer.parseInt(tab[0]) + 1);
                    vntjoueurs = tab[1];
                    vntjoueurs_c = tab[2];

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
            if(success){
                View view = getView();

                if(Jeu.validityNotes[Jeu.nbNotes-1]){

                    TextView moyenne = (TextView) view.findViewById(R.id.moyenne);
                    TextView tclasse = (TextView) view.findViewById(R.id.tclasse);
                    LinearLayout lclassement = (LinearLayout) view.findViewById(R.id.lclassement);


                    if(updated_moyenne()){
                        moyenne.setTextColor(Color.YELLOW);

                        tclasse.setVisibility(View.GONE);

                        lclassement.setVisibility(View.VISIBLE);
                        TextView classement = (TextView) view.findViewById(R.id.classement);
                        classement.setTextColor(Color.YELLOW);
                        classement.setText(vclassement);

                    }else{
                        moyenne.setTextColor(Color.WHITE);

                        lclassement.setVisibility(View.GONE);
                        tclasse.setVisibility(View.VISIBLE);
                    }
                }
                TextView ntjoueurs = (TextView) view.findViewById(R.id.ntjoueurs);
                ntjoueurs.setText(vntjoueurs);

                TextView ntjoueurs_c = (TextView) view.findViewById(R.id.ntjoueurs_c);
                ntjoueurs_c.setText(vntjoueurs_c);

            }else{

                //Log.i("alain", "Erreur = "  + Integer.toString(erreur) + "\n");

                if (Locale.getDefault().getLanguage().equals("fr")) {
                    Toast toast = Toast.makeText(getActivity(), "Erreur réseau", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }


    private void print_calendar(){

        if(Jeu.sdatemoyenne == null) return;

        Date datemoyenne = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            datemoyenne = formatter.parse(Jeu.sdatemoyenne);
        }catch(ParseException e){
            //Log.i("alain", "Erreur print_calendar = "  + e + "\n");
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(datemoyenne);

        Date today = new Date();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(today);
        calendar2.add(Calendar.DATE, -2);

        //Log.i("alain", "calendar1: " + formatter.format(calendar1.getTime())  + "\n");
        //Log.i("alain", "calendar2: " + formatter.format(calendar2.getTime())  + "\n");
    }


    private boolean updated_moyenne(){

        if(Jeu.sdatemoyenne == null){
            //Log.i("alain", "updated_moyenne:sdatemoyenne is null! " + "\n");
            return false;
        }

        Date datemoyenne = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            datemoyenne = formatter.parse(Jeu.sdatemoyenne);
        }catch(ParseException e){
            //Log.i("alain", "Erreur updated_moyenne = "  + e + "\n");
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(datemoyenne);

        Date today = new Date();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(today);
        calendar2.add(Calendar.DATE, -92);

        if(calendar1.compareTo(calendar2)<0){
            return false;
        }else{
            return true;
        }
    }


}
