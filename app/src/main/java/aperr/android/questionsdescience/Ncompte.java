package aperr.android.questionsdescience;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


public class Ncompte extends Activity {

    private Boolean erreur;
    private String sname, scode, scode1, spays;
    private EditText edit_name, edit_code, edit_confirmer, edit_pays;
    public static Typeface police;
    TextView connexion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i("alain", "Ncompte onCreate " + "\n");
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        police = Typeface.createFromAsset(getAssets(), "buxton-sketch.ttf");

        setContentView(R.layout.activity_ncompte);

        TextView ncompte = (TextView) findViewById(R.id.ncompte);
        TextView nom = (TextView) findViewById(R.id.nom);
        TextView code = (TextView) findViewById(R.id.code);
        TextView confirmer = (TextView) findViewById(R.id.confirmer);
        TextView pays = (TextView) findViewById(R.id.pays);

        connexion = (TextView) findViewById(R.id.connexion);

        ncompte.setTypeface(police);
        nom.setTypeface(police);
        code.setTypeface(police);
        confirmer.setTypeface(police);
        pays.setTypeface(police);

        TextView ok_compte = (TextView) findViewById(R.id.ok_compte);
        ok_compte.setTypeface(Jeu.police);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_code = (EditText) findViewById(R.id.edit_code);
        edit_confirmer = (EditText) findViewById(R.id.edit_confirmer);
        edit_pays = (EditText) findViewById(R.id.edit_pays);

        ok_compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erreur = false;

                sname = edit_name.getText().toString();
                if(sname.isEmpty()){
                    erreur = true;
                }else{
                    edit_name.setTextColor(0xff000000);
                }
                scode = edit_code.getText().toString();
                if(scode.isEmpty()){
                    erreur = true;
                }else{
                    edit_code.setTextColor(0xff000000);
                }

                scode1 = edit_confirmer.getText().toString();
                if(scode1.isEmpty()){
                    erreur = true;
                }else{
                    edit_confirmer.setTextColor(0xff000000);
                }

                spays = edit_pays.getText().toString();
                if(spays.isEmpty()){
                    spays = "-";
                }

                if(!erreur) {
                    if(!scode.equals(scode1)){
                        erreur = true;
                        edit_code.setTextColor(0xffff0000);
                        edit_confirmer.setTextColor(0xffff0000);
                    }else{
                        edit_code.setTextColor(0xff000000);
                        edit_confirmer.setTextColor(0xff000000);
                    }
                }
                if (erreur){
                    if (Locale.getDefault().getLanguage().equals("fr")) {
                        Toast toast = Toast.makeText(Ncompte.this, "nom ou code invalide", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(Ncompte.this, "invalid name or code", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else{

                    new CreateAccount().execute(sname, scode, spays);
                    connexion.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    private class CreateAccount extends AsyncTask<String, Void, Boolean> {

        int erreur;
        int id;

        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/create_account.php?nom=" + params[0] + "&code=" + params[1] + "&pays=" + params[2];
            String url = "http://aperrault.atspace.cc/create_account.php?nom=" + params[0] + "&code=" + params[1] + "&pays=" + params[2];
            //Log.i("alain", "url = "  + url + "\n");
            HttpURLConnection conn = null;
            InputStream is = null;

            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int readtimeout = conn.getReadTimeout();
                //Log.i("alain", "readtimeout = "  + Integer.toString(readtimeout) + "\n");

                int timeout = conn.getConnectTimeout();
                //Log.i("alain", "timeout = "  + Integer.toString(timeout) + "\n");

                int status = conn.getResponseCode();
                //Log.i("alain", "status = "  + Integer.toString(status) + "\n");

                if(status == 200 || status == 204){
                    is = conn.getInputStream();

                    String string_id = readStream(is);
                    //Log.i("alain", "id = "  + string_id + "\n");

                    if(string_id.equals("0")){
                        erreur = 1;
                        return false;
                    }else{
                        id = Integer.parseInt(string_id);
                        return true;
                    }
                }else{
                    erreur = 2;
                    return false;
                }

            }catch(Exception e){
                //Log.i("alain", "Erreur HTTP = "  + e + "\n");
                erreur = 3;
                return false;
            }finally {
                conn.disconnect();
            }

        }

        protected void onPostExecute(Boolean success){

            connexion.setVisibility(View.INVISIBLE);

            if (success){

                edit_name.setTextColor(0xff000000);
                edit_code.setTextColor(0xff000000);
                edit_confirmer.setTextColor(0xff000000);

                if (Locale.getDefault().getLanguage().equals("fr")) {
                    Toast toast = Toast.makeText(Ncompte.this, "Le compte a été crée", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(Ncompte.this, "Account has been created", Toast.LENGTH_SHORT);
                    toast.show();
                }

                Intent intent = new Intent(Ncompte.this, Jeu.class);
                intent.putExtra("connected", true);
                intent.putExtra("id", id);
                intent.putExtra("nom", sname);
                startActivity(intent);
                finish();

            }else{
                //Log.i("alain", "Erreur = "  + Integer.toString(erreur) + "\n");
                if(erreur == 1){
                    edit_name.setTextColor(0xffff0000);

                    if (Locale.getDefault().getLanguage().equals("fr")) {
                        Toast toast = Toast.makeText(Ncompte.this, "Erreur: Nom déja existant", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(Ncompte.this, "Error: Name already exists", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }else{
                    edit_name.setTextColor(0xff000000);
                    edit_code.setTextColor(0xff000000);
                    edit_confirmer.setTextColor(0xff000000);

                    if (Locale.getDefault().getLanguage().equals("fr")) {
                        Toast toast = Toast.makeText(Ncompte.this, "Erreur réseau", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(Ncompte.this, "Network failure", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        }
        private String readStream(InputStream is) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null){
                response.append(line);
            }
            return response.toString();
        }
    }

}
