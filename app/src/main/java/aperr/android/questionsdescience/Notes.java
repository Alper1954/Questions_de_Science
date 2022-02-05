package aperr.android.questionsdescience;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class Notes extends Activity {

    public static Typeface police;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        police = Typeface.createFromAsset(getAssets(), "buxton-sketch.ttf");

        setContentView(R.layout.activity_notes);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        VosNotes vosnotesFrag = new VosNotes();
        ft.add(R.id.fragment_vos_notes, vosnotesFrag);

        ClassementTitre classementtitreFrag = new ClassementTitre();
        ft.add(R.id.fragment_classement_titre, classementtitreFrag);

        VotreClassement votreclassementFrag = new VotreClassement();
        ft.add(R.id.fragment_votre_classement, votreclassementFrag);

        ft.commit();

    }

}
