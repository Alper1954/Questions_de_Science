package aperr.android.questionsdescience;


import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotreClassement extends Fragment {

    private ListView mListView;


    public VotreClassement() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_votre_classement, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Read_sommes().execute();
    }



    private class Read_sommes extends AsyncTask<String, Void, Boolean> {

        int erreur;

        String tab[];
        String somme;
        float moyenne;
        String smoyenne;
        String smoyenne_c;
        String nom;
        String pays;
        int nbJoueurs;

        List<Joueur> joueurs;

        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/read_sommes.php";
            String url = "http://aperrault.atspace.cc/read_sommes.php";
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
                    JSONArray jJoueurArray = new JSONArray(readStream(is));

                    nbJoueurs = jJoueurArray.length();
                    //Log.i("alain", "nbJoueurs = "  + Integer.toString(nbJoueurs) + "\n");

                    tab = new String[2];

                    joueurs = new ArrayList<Joueur>();

                    for (int i=0; i<nbJoueurs; i++){
                        JSONObject jJoueurObject = jJoueurArray.getJSONObject(i);
                        somme = jJoueurObject.getString("somme");
                        moyenne = ((float) Integer.parseInt(somme))/Jeu.nbNotes;
                        smoyenne = Float.toString(moyenne);
                        tab = smoyenne.split("\\.");

                        if(tab[1].equals("0")){
                            smoyenne_c = tab[0];
                        }else{
                            smoyenne_c = tab[0] + "." + tab[1];
                        }

                        nom = jJoueurObject.getString("nom");
                        pays = jJoueurObject.getString("pays");

                        joueurs.add(new Joueur(smoyenne, nom, pays));

                    }
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

                JoueurAdapter adapter = new JoueurAdapter(getActivity(), joueurs);
                mListView.setAdapter(adapter);

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

        private String readStream(InputStream is) throws IOException {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null){
                response.append(line).append('\n');
            }
            return response.toString();
        }

    }


}
