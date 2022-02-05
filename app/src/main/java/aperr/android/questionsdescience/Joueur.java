package aperr.android.questionsdescience;

/**
 * Created by perrault on 29/01/2019.
 */
public class Joueur {
    private String moyenne;
    private String nom;
    private String pays;

    public Joueur(String moyenne, String nom, String pays){
        this.moyenne = moyenne;
        this.nom = nom;
        this.pays = pays;
    }

    public String getMoyenne(){
        return moyenne;
    }
    public String getNom(){
        return nom;
    }
    public String getPays(){
        return pays;
    }

    public void setMoyenne(String moyenne){
        this.moyenne = moyenne;
    }
    public void setNom(String nom){
        this.nom = nom;
    }
    public void setPays(String pays){
        this.pays = pays;
    }

}
