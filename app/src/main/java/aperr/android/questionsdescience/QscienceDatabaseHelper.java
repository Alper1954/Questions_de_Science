package aperr.android.questionsdescience;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Random;

/**
 * Created by perrault on 18/12/2017.
 */
public class QscienceDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "qhist";
    private static final int DB_VERSION = 1;
    private static Context mycontext;

    QscienceDatabaseHelper(Context context){

        super(context, DB_NAME, null, DB_VERSION);
        this.mycontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        //Log.i("alain", "QhistDatabaseHelper - onCreate");

        QhistCreateDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        //Log.i("alain", "Enter in onUpgrade " + "\n");

        db.execSQL("DROP TABLE QUESTIONS;");
        QhistCreateDatabase(db);
    }

    private static void QhistCreateDatabase(SQLiteDatabase db){

        //Log.i("alain", "Enter in QhistCreateDatabase " + "\n");


        //Log.i("alain", "First init after database creation "  + "\n");
        //Log.i("alain", "nbQuestionsTirage = "  + Integer.toString(Jeu.nbQuestionsTirage) + "\n");
        //Log.i("alain", "Tableau numQuestionsTirage: "  + "\n");
        //Log.i("alain", "Q1 = " + Integer.toString(Jeu.numQuestionsTirage[0]) + "\n");
        //Log.i("alain", "Q2 = " + Integer.toString(Jeu.numQuestionsTirage[1]) + "\n");
        //Log.i("alain", "Q3 = " + Integer.toString(Jeu.numQuestionsTirage[2]) + "\n");
        //Log.i("alain", "Q4 = " + Integer.toString(Jeu.numQuestionsTirage[3]) + "\n");
        //Log.i("alain", "Q5 = " + Integer.toString(Jeu.numQuestionsTirage[4]) + "\n");


        db.execSQL("CREATE TABLE QUESTIONS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "QUESTION TEXT, "
                + "EXPLICATION, "
                + "NB_ANSWERS TEXT, "
                + "NBOK TEXT, "
                + "ANSWER1 TEXT, "
                + "ANSWER2 TEXT, "
                + "ANSWER3 TEXT, "
                + "ANSWER4 TEXT);");

        String line;
        InputStream iStream;
        String question, explication, nbok;
        String answer1 = "?";
        String answer2 = "?";
        String answer3 = "?";
        String answer4 = "?";

        int nb_answers, length;

        int numline = 1;


        if (Locale.getDefault().getLanguage().equals("fr")) {
            iStream = mycontext.getResources().openRawResource(R.raw.science);
        } else {
            iStream = mycontext.getResources().openRawResource(R.raw.science_en);
        }

        BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream, Charset.forName("UTF8")));

        try {
            while ((line=bReader.readLine()) != null){

                String[] lineFields = line.split(";");
                length = lineFields.length;

                question = lineFields[1];
                explication = lineFields[2];

                nb_answers = 2;

                try{
                    nb_answers = Integer.parseInt(lineFields[3]);
                }catch (Exception e){
                    //Log.i("alain", "Exception, line: " + Integer.toString(numline) + "\n");
                    //Log.i("alain", line);
                }

                nbok = lineFields[4];


                if(length >= (nb_answers + 2)){
                    if(nb_answers == 4){
                        answer1 = lineFields[5];
                        answer2 = lineFields[6];
                        answer3 = lineFields[7];
                        answer4 = lineFields[8];
                    }else if(nb_answers == 3){
                        answer1 = lineFields[5];
                        answer2 = lineFields[6];
                        answer3 = lineFields[7];
                    }else{
                        answer1 = lineFields[5];
                        answer2 = lineFields[6];
                    }
                }else{
                    //Log.i("alain", line);
                }

                insertQuestion(db, question, explication, nb_answers, nbok, answer1, answer2, answer3, answer4);

                numline++;
            }

        }catch (IOException e){
            //Log.i("alain", "invalid file");
            Toast toast = Toast.makeText(mycontext, "Invalid file", Toast.LENGTH_SHORT);
            toast.show();
        }



    }

    private static void insertQuestion(SQLiteDatabase db,
                                       String question,
                                       String explication,
                                       int nb_answers,
                                       String nbok,
                                       String answer1,
                                       String answer2,
                                       String answer3,
                                       String answer4){


        ContentValues questionValues = new ContentValues();

        questionValues.put("QUESTION", question);
        questionValues.put("EXPLICATION", explication);
        questionValues.put("NB_ANSWERS", nb_answers);
        questionValues.put("NBOK", nbok);
        questionValues.put("ANSWER1", answer1);
        questionValues.put("ANSWER2", answer2);
        questionValues.put("ANSWER3", answer3);
        questionValues.put("ANSWER4", answer4);

        db.insert("QUESTIONS", null, questionValues);
    }
}
