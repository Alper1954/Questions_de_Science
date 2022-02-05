package aperr.android.questionsdescience;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by perrault on 30/01/2019.
 */
public class JoueurAdapter extends ArrayAdapter<Joueur> {

    public JoueurAdapter(Context context, List<Joueur> joueurs){
        super(context, 0, joueurs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_classement,parent,false);
        }

        JoueurViewHolder viewHolder =(JoueurViewHolder)convertView.getTag();
        if(viewHolder == null){
            viewHolder = new JoueurViewHolder();
            viewHolder.valMoy = (TextView) convertView.findViewById(R.id.valMoy);
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nom);
            viewHolder.pays = (TextView) convertView.findViewById(R.id.pays);
            convertView.setTag(viewHolder);
        }

        Joueur joueur = getItem(position);

        viewHolder.valMoy.setText(joueur.getMoyenne());
        viewHolder.nom.setText(joueur.getNom());
        viewHolder.pays.setText(joueur.getPays());

        return convertView;
    }

    private class JoueurViewHolder{
        public TextView valMoy;
        public TextView nom;
        public TextView pays;
    }

}
