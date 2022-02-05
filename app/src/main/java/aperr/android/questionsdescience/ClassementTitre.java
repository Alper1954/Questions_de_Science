package aperr.android.questionsdescience;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ClassementTitre extends Fragment {

    public ClassementTitre() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_classement_titre, container, false);

        ImageView arrow = (ImageView) view.findViewById(R.id.arrow);
        arrow.setImageResource(R.drawable.arrow);
        arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Log.i("alain", "Image clicked " + "\n");
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        TextView header_classement = (TextView) view.findViewById(R.id.header_classement);
        header_classement.setTypeface(Notes.police);
    }
}
