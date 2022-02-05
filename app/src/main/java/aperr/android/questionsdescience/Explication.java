package aperr.android.questionsdescience;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Explication extends Fragment {
    TextView explication;

    public Explication() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.i("alain", "Enter in Explication onCeateView "  + "\n");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explication, container, false);
    }

    @Override
    public void onStart(){
        //Log.i("alain", "Enter in Explication onStart "  + "\n");
        super.onStart();
        View view = getView();
        explication = (TextView) view.findViewById(R.id.textExplication);
        explication.setTypeface(Jeu.police);
    }

    @Override
    public void onResume(){
        //Log.i("alain", "Enter in Explication onResume "  + "\n");
        super.onResume();

        explication.setText(Quiz.explication);

        Quiz.display = true;
        Quiz.config = Config.Explication;
    }
}
