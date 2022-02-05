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
import android.widget.LinearLayout;
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

public class Identification extends Activity {

    private Boolean erreur;
    private String sname, scode;
    private EditText edit_name1, edit_code1;
    public static Typeface police;
    TextView connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        police = Typeface.createFromAsset(getAssets(), "buxton-sketch.ttf");

        setContentView(R.layout.activity_identification);

        TextView compte = (TextView) findViewById(R.id.compte);
        TextView identification = (TextView) findViewById(R.id.identification);
        TextView nom = (TextView) findViewById(R.id.nom1);
        TextView code = (TextView) findViewById(R.id.code1);

        connexion = (TextView) findViewById(R.id.connexion);



        compte.setTypeface(police);
        identification.setTypeface(police);
        nom.setTypeface(police);
        code.setTypeface(police);

        TextView ok_connexion = (TextView) findViewById(R.id.ok_connexion);
        ok_connexion.setTypeface(Jeu.police);
        edit_name1 = (EditText) findViewById(R.id.edit_name1);
        edit_code1 = (EditText) findViewById(R.id.edit_code1);


        ok_connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erreur = false;

                sname = edit_name1.getText().toString();
                if(sname.isEmpty()){
                    erreur = true;
                }else{
                    edit_name1.setTextColor(0xff000000);
                }
                scode = edit_code1.getText().toString();
                if(scode.isEmpty()){
                    erreur = true;
                }else{
                    edit_code1.setTextColor(0xff000000);
                }

                if (erreur){
                    if (Locale.getDefault().getLanguage().equals("fr")) {
                        Toast toast = Toast.makeText(Identification.this, "nom ou code invalide", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(Identification.this, "invalid name or code", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else{
                    new Check_identification().execute(sname, scode);
                    connexion.setVisibility(View.VISIBLE);

                }

            }
        });
    }


    private class Check_identification extends AsyncTask<String, Void, Boolean> {

        int erreur;
        int id;

        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/check_identification.php?nom=" + params[0] + "&code=" + params[1];
            String url = "http://aperrault.atspace.cc/check_identification.php?nom=" + params[0] + "&code=" + params[1];
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

                int connecttimeout = conn.getConnectTimeout();
                //Log.i("alain", "connecttimeout = "  + Integer.toString(connecttimeout) + "\n");

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
                    //Log.i("alain", "Status HTTP = "  + Integer.toString(status) + "\n");
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
                edit_name1.setTextColor(0xff000000);
                edit_code1.setTextColor(0xff000000);

                if (Locale.getDefault().getLanguage().equals("fr")) {
                    Toast toast = Toast.makeText(Identification.this, "Identification OK", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(Identification.this, "Identification OK", Toast.LENGTH_SHORT);
                    toast.show();
                }
                //Log.i("alain", "Identification connected = true " + "\n");

                Intent intent = new Intent(Identification.this, Jeu.class);
                intent.putExtra("connected", true);
                intent.putExtra("id", id);
                intent.putExtra("nom", sname);
                startActivity(intent);
                finish();

            }else{
                //Log.i("alain", "Erreur = "  + Integer.toString(erreur) + "\n");
                if(erreur == 1){
                    edit_name1.setTextColor(0xffff0000);
                    edit_code1.setTextColor(0xffff0000);

                    if (Locale.getDefault().getLanguage().equals("fr")) {
                        Toast toast = Toast.makeText(Identification.this, "Erreur d'identification", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(Identification.this, "Identification error", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else{
                    edit_name1.setTextColor(0xff000000);
                    edit_code1.setTextColor(0xff000000);

                    if (Locale.getDefault().getLanguage().equals("fr")) {
                        Toast toast = Toast.makeText(Identification.this, "Erreur réseau", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(Identification.this, "Network error", Toast.LENGTH_SHORT);
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
