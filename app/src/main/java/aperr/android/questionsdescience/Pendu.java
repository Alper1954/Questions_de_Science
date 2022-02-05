package aperr.android.questionsdescience;


import android.app.Fragment;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Pendu extends Fragment {

    PenduView mPenduView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.i("alain", "Enter in Pendu onCeateView "  + "\n");


        mPenduView = new PenduView(getActivity());
        return mPenduView;
    }

    @Override
    public void onStart(){
        //Log.i("alain", "Enter in Pendu onStart "  + "\n");
        super.onStart();
    }

    @Override
    public void onPause(){
        //Log.i("alain", "Enter in Pendu onPause "  + "\n");
        super.onPause();
        mPenduView.pause();
    }

    @Override
    public void onResume(){
        //Log.i("alain", "Enter in Pendu onResume "  + "\n");
        super.onResume();
        mPenduView.resume();
    }

    @Override
    public void onStop(){
        //Log.i("alain", "Enter in Pendu onStop "  + "\n");
        super.onStop();
    }

    @Override
    public void onDestroy(){
        //Log.i("alain", "Enter in Pendu onDestroy "  + "\n");
        super.onDestroy();
    }

}
