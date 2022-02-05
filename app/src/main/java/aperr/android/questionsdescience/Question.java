package aperr.android.questionsdescience;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class Question extends Fragment implements TextToSpeech.OnInitListener {

    public static TextToSpeech myTTS_static;

    private TextToSpeech myTTS;
    private boolean running; //compteur de temps actif
    private View view;

    String ssomme;

    TextView textLoad, textQuestion, textAnswer1, textAnswer2, textAnswer3, textAnswer4;
    LinearLayout layout_valider, layout_suite;

    public Question() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.i("alain", "Question onCeateView "  + "\n");

        if(Jeu.ttsInstalled){
            if(myTTS == null){
                myTTS = new TextToSpeech(getActivity(), this);
                myTTS_static = myTTS;
                //Log.i("alain", "Create TTS "  + "\n");
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false);

    }

    @Override
    public void onStart(){
        //Log.i("alain", "Enter in Question onStart "  + "\n");
        super.onStart();
        view = getView();

        TextView header = (TextView) view.findViewById(R.id.header);
        if(header != null){
            header.setTypeface(Jeu.police);
        }


        //nquestion = (TextView) view.findViewById(R.id.nquestion);
        textLoad = (TextView) view.findViewById(R.id.textLoad);
        textQuestion = (TextView) view.findViewById(R.id.textQuestion);
        textAnswer1 = (TextView) view.findViewById(R.id.answer1);
        textAnswer2 = (TextView) view.findViewById(R.id.answer2);
        textAnswer3 = (TextView) view.findViewById(R.id.answer3);
        textAnswer4 = (TextView) view.findViewById(R.id.answer4);
        textQuestion.setTypeface(Jeu.police);
        textAnswer1.setTypeface(Jeu.police);
        textAnswer2.setTypeface(Jeu.police);
        textAnswer3.setTypeface(Jeu.police);
        textAnswer4.setTypeface(Jeu.police);

        layout_valider = (LinearLayout) view.findViewById(R.id.layout_valider);
        layout_suite = (LinearLayout) view.findViewById(R.id.layout_suite);

        ImageView buttonExplication = (ImageView) view.findViewById(R.id.buttonExplication);
        buttonExplication.setImageResource(R.drawable.why);
        buttonExplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQueue("");

                FragmentTransaction ft = getFragmentManager().beginTransaction();

                Explication explicationFrag = new Explication();
                ft.replace(R.id.fragment_jeu, explicationFrag);
                ft.addToBackStack(null);

                ft.commit();
            }
        });



        TextView valider = (TextView) view.findViewById(R.id.valider);
        final TextView suite = (TextView) view.findViewById(R.id.suite);

        valider.setTypeface(Jeu.police);
        suite.setTypeface(Jeu.police);



        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Quiz.nbAnswered == 0){
                    if (Locale.getDefault().getLanguage().equals("fr")) {
                        Toast toast = Toast.makeText(getActivity(), "Veuillez sélectionner une réponse", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(getActivity(), "Please, select an answer", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else {

                    layout_valider.setVisibility(View.GONE);
                    layout_suite.setVisibility(View.VISIBLE);

                    Quiz.stateSuite = true;
                    Quiz.stateValidation = false;
                    Quiz.timeoutQuestion = false;


                    running = false;

                    textAnswer1.setBackgroundColor(0xFFFFFFFF);
                    textAnswer2.setBackgroundColor(0xFFFFFFFF);
                    textAnswer3.setBackgroundColor(0xFFFFFFFF);
                    textAnswer4.setBackgroundColor(0xFFFFFFFF);

                    int inbok = Integer.parseInt(Quiz.nbOk);
                    if(inbok == 1){
                        textAnswer1.setBackgroundColor(0xff90ee90);
                    }else if(inbok == 2){
                        textAnswer2.setBackgroundColor(0xff90ee90);
                    }else if(inbok == 3) {
                        textAnswer3.setBackgroundColor(0xff90ee90);
                    }else {
                        textAnswer4.setBackgroundColor(0xff90ee90);
                    }

                    if (Quiz.nbAnswered != inbok) {
                        Quiz.goodAnswer = false;
                        if (Locale.getDefault().getLanguage().equals("fr")) {
                            initQueue("erreur");
                        } else {
                            initQueue("error");
                        }
                        Quiz.numErreur++;

                        if(Quiz.nbAnswered == 1){
                            textAnswer1.setBackgroundColor(0xffff7f50);
                        }else if(Quiz.nbAnswered == 2){
                            textAnswer2.setBackgroundColor(0xffff7f50);
                        }else if(Quiz.nbAnswered == 3) {
                            textAnswer3.setBackgroundColor(0xffff7f50);
                        }else {
                            textAnswer4.setBackgroundColor(0xffff7f50);
                        }
                    }else{
                        Quiz.goodAnswer = true;
                        if (Locale.getDefault().getLanguage().equals("fr")) {
                            initQueue("correct");
                        } else {
                            initQueue("correct");
                        }
                        Quiz.maNote++;
                    }

                    if((Quiz.numQuestion >= Jeu.lastQ)||(Quiz.numErreur == 12)) {
                        Quiz.finQuiz = true;

                        int typeNote = check_typeNote(Quiz.maNote);
                        find_textNote(typeNote);

                        Jeu.valueNotes[9] = Jeu.valueNotes[8];
                        Jeu.valueNotes[8] = Jeu.valueNotes[7];
                        Jeu.valueNotes[7] = Jeu.valueNotes[6];
                        Jeu.valueNotes[6] = Jeu.valueNotes[5];
                        Jeu.valueNotes[5] = Jeu.valueNotes[4];
                        Jeu.valueNotes[4] = Jeu.valueNotes[3];
                        Jeu.valueNotes[3] = Jeu.valueNotes[2];
                        Jeu.valueNotes[2] = Jeu.valueNotes[1];
                        Jeu.valueNotes[1] = Jeu.valueNotes[0];
                        Jeu.valueNotes[0] = Quiz.maNote;


                        Jeu.validityNotes[9] = Jeu.validityNotes[8];
                        Jeu.validityNotes[8] = Jeu.validityNotes[7];
                        Jeu.validityNotes[7] = Jeu.validityNotes[6];
                        Jeu.validityNotes[6] = Jeu.validityNotes[5];
                        Jeu.validityNotes[5] = Jeu.validityNotes[4];
                        Jeu.validityNotes[4] = Jeu.validityNotes[3];
                        Jeu.validityNotes[3] = Jeu.validityNotes[2];
                        Jeu.validityNotes[2] = Jeu.validityNotes[1];
                        Jeu.validityNotes[1] = Jeu.validityNotes[0];
                        Jeu.validityNotes[0] = true;


                        for(int i=0; i<10; i++){
                            Jeu.svalueNotes[i] = Integer.toString(Jeu.valueNotes[i]);
                        }


                        if (Jeu.validityNotes[Jeu.nbNotes-1]){
                            Jeu.somme = 0;
                            for(int i=0; i<Jeu.nbNotes; i++){
                                Jeu.somme = Jeu.somme + Jeu.valueNotes[i];
                            }
                            Jeu.moyenne = (float) Jeu.somme/Jeu.nbNotes;

                            Date today = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Jeu.sdatemoyenne = formatter.format(today);

                            ssomme = Integer.toString(Jeu.somme);

                            //Log.i("alain", "smoyenne =  "  + ssomme + "\n");
                            //Log.i("alain", "sdatemoyenne stored in DB=  " + Jeu.sdatemoyenne + "\n");

                            new update_somme2().execute();

                        }else{
                            new update_notes2().execute();
                        }

                        layout_suite.setVisibility(View.VISIBLE);
                        suite.setVisibility(View.INVISIBLE);


                    }else{
                        layout_suite.setVisibility(View.VISIBLE);
                        suite.setVisibility(View.VISIBLE);

                    }

                }
            }
        });



        suite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quiz.secs = Jeu.timeQuestion;
                running = true;

                Quiz.nbAnswered = 0;

                new ReadQuestion().execute();

                layout_valider.setVisibility(View.VISIBLE);
                layout_suite.setVisibility(View.GONE);
                Quiz.stateValidation = true;
                Quiz.stateSuite = false;
                Quiz.timeoutQuestion = false;

            }
        });



        textAnswer1 = (TextView)view.findViewById(R.id.answer1);
        textAnswer1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedAnswer(1);
            }
        });

        textAnswer2 = (TextView)view.findViewById(R.id.answer2);
        textAnswer2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedAnswer(2);
            }
        });

        textAnswer3 = (TextView)view.findViewById(R.id.answer3);
        textAnswer3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedAnswer(3);
            }
        });

        textAnswer4 = (TextView)view.findViewById(R.id.answer4);
        textAnswer4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedAnswer(4);
            }
        });


        if(Quiz.firstStart) {

            //Log.i("alain", "Quiz firstStart "  + "\n");


            Quiz.secs = Jeu.timeQuestion;
            running = true;
            runTimer();


            if(!Jeu.ttsInstalled){
                new ReadQuestion().execute();
            }
            Quiz.firstStart = false;

            layout_valider.setVisibility(View.VISIBLE);
            layout_suite.setVisibility(View.GONE);


        }else{

            textQuestion.setText(Quiz.question);

            textAnswer1.setBackgroundColor(0xFFFFFFFF);
            textAnswer2.setBackgroundColor(0xFFFFFFFF);
            textAnswer3.setBackgroundColor(0xFFFFFFFF);
            textAnswer4.setBackgroundColor(0xFFFFFFFF);

            if(Quiz.timeoutQuestion){
                //Log.i("alain", "Question onStart after timeout Question "  + "\n");
                layout_valider.setVisibility(View.GONE);
            }



            if(Quiz.stateValidation){
                //Log.i("alain", "Question onStart in stateValidation "  + "\n");

                Quiz.secs = Jeu.timeQuestion;
                running = true;

                layout_valider.setVisibility(View.VISIBLE);
                layout_suite.setVisibility(View.GONE);
                //Log.i("alain", "stateValidation = true "  + "\n");

                if(Quiz.nbAnswered == 1){
                    textAnswer1.setBackgroundColor(0xff77b5fe);
                }else if(Quiz.nbAnswered == 2){
                    textAnswer2.setBackgroundColor(0xff77b5fe);
                }else if(Quiz.nbAnswered == 3) {
                    textAnswer3.setBackgroundColor(0xff77b5fe);
                }else if(Quiz.nbAnswered == 3) {
                    textAnswer4.setBackgroundColor(0xff77b5fe);
                }

            }

            if(Quiz.stateSuite){
                //Log.i("alain", "Question onStart in stateSuite "  + "\n");

                layout_valider.setVisibility(View.GONE);
                layout_suite.setVisibility(View.VISIBLE);
                if(Quiz.finQuiz){
                    suite.setVisibility(View.INVISIBLE);

                }else{
                    suite.setVisibility(View.VISIBLE);

                }

                //Log.i("alain", "stateSuite = true "  + "\n");

                int inbok = Integer.parseInt(Quiz.nbOk);

                if(inbok == 1){
                    textAnswer1.setBackgroundColor(0xff90ee90);
                }else if(inbok == 2){
                    textAnswer2.setBackgroundColor(0xff90ee90);
                }else if(inbok == 3) {
                    textAnswer3.setBackgroundColor(0xff90ee90);
                }else {
                    textAnswer4.setBackgroundColor(0xff90ee90);
                }

                if (Quiz.nbAnswered != inbok) {
                    if(Quiz.nbAnswered == 1){
                        textAnswer1.setBackgroundColor(0xffff7f50);
                    }else if(Quiz.nbAnswered == 2){
                        textAnswer2.setBackgroundColor(0xffff7f50);
                    }else if(Quiz.nbAnswered == 3) {
                        textAnswer3.setBackgroundColor(0xffff7f50);
                    }else {
                        textAnswer4.setBackgroundColor(0xffff7f50);
                    }
                }

            }


            int nb = Integer.parseInt(Quiz.nbAnswers);
            if(nb == 4){
                textAnswer1.setText(Quiz.answer1);
                textAnswer1.setVisibility(View.VISIBLE);

                textAnswer2.setText(Quiz.answer2);
                textAnswer2.setVisibility(View.VISIBLE);

                textAnswer3.setText(Quiz.answer3);
                textAnswer3.setVisibility(View.VISIBLE);

                textAnswer4.setText(Quiz.answer4);
                textAnswer4.setVisibility(View.VISIBLE);

            }else if(nb == 3){
                textAnswer1.setText(Quiz.answer1);
                textAnswer1.setVisibility(View.VISIBLE);

                textAnswer2.setText(Quiz.answer2);
                textAnswer2.setVisibility(View.VISIBLE);

                textAnswer3.setText(Quiz.answer3);
                textAnswer3.setVisibility(View.VISIBLE);

                textAnswer4.setVisibility(View.GONE);

            }else {
                textAnswer1.setText(Quiz.answer1);
                textAnswer1.setVisibility(View.VISIBLE);

                textAnswer2.setText(Quiz.answer2);
                textAnswer2.setVisibility(View.VISIBLE);

                textAnswer3.setVisibility(View.GONE);

                textAnswer4.setVisibility(View.GONE);
            }
        }
    }

    private int check_typeNote(int note){
        if(note>18){
            return 1;
        }else if(note>16){
            return 2;
        }else if (note>12){
            return 3;
        }else if(note>8){
            return 4;
        }else{
            return 5;
        }
    }

    private void find_textNote(int type){

        if (Locale.getDefault().getLanguage().equals("fr")) {
            switch(type){
                case 1:
                    Quiz.t_Appreciation = "Excellent!";
                    Quiz.x_Appreciation = 150;
                    break;
                case 2:
                    Quiz.t_Appreciation = "Très bien!";
                    Quiz.x_Appreciation = 150;
                    break;
                case 3:
                    Quiz.t_Appreciation = "Bien!";
                    Quiz.x_Appreciation = 190;
                    break;
                case 4:
                    Quiz.t_Appreciation = "Assez bien!";
                    Quiz.x_Appreciation = 140;
                    break;
                case 5:
                    Quiz.t_Appreciation = "Doit progresser!";
                    Quiz.x_Appreciation = 60;
                    break;
            }
        }else{
            switch(type){
                case 1:
                    Quiz.t_Appreciation = "Excellent!";
                    Quiz.x_Appreciation = 150;
                    break;
                case 2:
                    Quiz.t_Appreciation = "Very good!";
                    Quiz.x_Appreciation = 140;
                    break;
                case 3:
                    Quiz.t_Appreciation = "Good!";
                    Quiz.x_Appreciation = 190;
                    break;
                case 4:
                    Quiz.t_Appreciation = "Pretty good!";
                    Quiz.x_Appreciation = 125;
                    break;
                case 5:
                    Quiz.t_Appreciation = "Should progress!";
                    Quiz.x_Appreciation = 60;
                    break;
            }

        }

    }


    private void selectedAnswer(Integer answer){

        if(Quiz.stateValidation){
            //Log.i("alain", "answer= "+ Integer.toString(answer)  + "\n");

            textAnswer1.setBackgroundColor(0xFFFFFFFF);
            textAnswer2.setBackgroundColor(0xFFFFFFFF);
            textAnswer3.setBackgroundColor(0xFFFFFFFF);
            textAnswer4.setBackgroundColor(0xFFFFFFFF);

            if(answer == 1){
                textAnswer1.setBackgroundColor(0xff77b5fe);
            }else if(answer == 2){
                textAnswer2.setBackgroundColor(0xff77b5fe);
            }else if(answer == 3) {
                textAnswer3.setBackgroundColor(0xff77b5fe);
            }else {
                textAnswer4.setBackgroundColor(0xff77b5fe);
            }

            Quiz.nbAnswered = answer;
        }

    }



    private void onBackgroundTaskDataObtained(String[] questionData) {

        textAnswer1.setBackgroundColor(0xFFFFFFFF);
        textAnswer2.setBackgroundColor(0xFFFFFFFF);
        textAnswer3.setBackgroundColor(0xFFFFFFFF);
        textAnswer4.setBackgroundColor(0xFFFFFFFF);

        Quiz.question = questionData[0];
        Quiz.explication = questionData[1].replaceAll("\\\\n", "\\\n");
        Quiz.nbAnswers = questionData[2];
        Quiz.nbOk = questionData[3];
        Quiz.answer1 = questionData[4];
        Quiz.answer2 = questionData[5];
        Quiz.answer3 = questionData[6];
        Quiz.answer4 = questionData[7];

        Quiz.nbAnswered = 0;

        initQueue(Quiz.question);

        int nb = Integer.parseInt(Quiz.nbAnswers);

        if(nb == 4){
            if (Locale.getDefault().getLanguage().equals("fr")) {
                addQueue("réponse une:");
                addQueue(Quiz.answer1);
                addQueue("réponse deux:");
                addQueue(Quiz.answer2);
                addQueue("réponse trois:");
                addQueue(Quiz.answer3);
                addQueue("réponse quatre:");
                addQueue(Quiz.answer4);
            } else {
                addQueue("answer one:");
                addQueue(Quiz.answer1);
                addQueue("answer two:");
                addQueue(Quiz.answer2);
                addQueue("answer three:");
                addQueue(Quiz.answer3);
                addQueue("answer four:");
                addQueue(Quiz.answer4);
            }
        }else if(nb == 3){
            if (Locale.getDefault().getLanguage().equals("fr")) {
                addQueue("réponse une:");
                addQueue(Quiz.answer1);
                addQueue("réponse deux:");
                addQueue(Quiz.answer2);
                addQueue("réponse trois:");
                addQueue(Quiz.answer3);
            } else {
                addQueue("answer one:");
                addQueue(Quiz.answer1);
                addQueue("answer two:");
                addQueue(Quiz.answer2);
                addQueue("answer three:");
                addQueue(Quiz.answer3);
            }

        }else{
            if (Locale.getDefault().getLanguage().equals("fr")) {
                addQueue("réponse une:");
                addQueue(Quiz.answer1);
                addQueue("réponse deux:");
                addQueue(Quiz.answer2);
            } else {
                addQueue("answer one:");
                addQueue(Quiz.answer1);
                addQueue("answer two:");
                addQueue(Quiz.answer2);
            }
        }


    }


    public void onInit(int initStatus){

        new ReadQuestion().execute();

        if(initStatus == TextToSpeech.SUCCESS){
            //Log.i("alain", "TTS init OK");

            if (Locale.getDefault().getLanguage().equals("fr")) {
                myTTS.setLanguage(Locale.FRANCE);
            } else {
                myTTS.setLanguage(Locale.US);
            }

        }else if (initStatus == TextToSpeech.ERROR){
            //Log.i("alain", "TTS init ERROR!!!!");
        }
    }

    public static void initQueue(String text){
        if(Jeu.ttsEnabled){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                //Log.i("alain", "Use TTS speak new version");
                myTTS_static.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }else{
                //Log.i("alain", "Use TTS speak deprecated version");
                myTTS_static.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }


    private void addQueue(String text){
        if(Jeu.ttsEnabled){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                //Log.i("alain", "Use TTS speak new version");
                myTTS.speak(text, TextToSpeech.QUEUE_ADD, null, null);
            }else{
                //Log.i("alain", "Use TTS speak deprecated version");
                myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }


    private class ReadQuestion extends AsyncTask<Void, Void, Boolean> {

        private String questionData[];
        private SQLiteDatabase db;
        private Cursor cursor;

        int questionId;


        protected void onPreExecute() {

            questionData = new String[10];

            textLoad.setVisibility(View.VISIBLE);

            //Log.i("alain", "Enter in onPreExecute "  + "\n");

        }


        protected Boolean doInBackground(Void ... params){
            //Log.i("alain", "Enter in doInBackground "  + "\n");

            //Log.i("alain", "nbQuestionsTirage = "  + Integer.toString(Jeu.nbQuestionsTirage) + "\n");
            //Log.i("alain", "Tableau numQuestionsTirage: "  + "\n");
            //Log.i("alain", "Q1 = " + Integer.toString(Jeu.numQuestionsTirage[0]) + "\n");
            //Log.i("alain", "Q2 = " + Integer.toString(Jeu.numQuestionsTirage[1]) + "\n");
            //Log.i("alain", "Q3 = " + Integer.toString(Jeu.numQuestionsTirage[2]) + "\n");
            //Log.i("alain", "Q4 = " + Integer.toString(Jeu.numQuestionsTirage[3]) + "\n");
            //Log.i("alain", "Q5 = " + Integer.toString(Jeu.numQuestionsTirage[4]) + "\n");

            if(Jeu.randomTirage == 1){

                //Log.i("alain", "nbQuestionsTirage = "  + Integer.toString(Jeu.nbQuestionsTirage) + "\n");

                Random r = new Random();
                int index = r.nextInt(Jeu.nbQuestionsTirage);
                questionId = Jeu.numQuestionsTirage[index];

                //Log.i("alain", "index = "  + Integer.toString(index) + "\n");
                //Log.i("alain", "questionId = "  + Integer.toString(questionId) + "\n");

                if(index <= Jeu.nbQuestions-2){
                    for(int i=index; i<=Jeu.nbQuestions-2; i++){
                        Jeu.numQuestionsTirage[i] = Jeu.numQuestionsTirage[i+1];
                    }
                }
                Jeu.numQuestionsTirage[Jeu.nbQuestions-1] = questionId;

                if(Jeu.nbQuestionsTirage == 1){
                    Jeu.randomTirage = 0;
                    Jeu.numTirageSeq = 0;
                }else{
                    Jeu.nbQuestionsTirage--;
                }

            }else{

                //Log.i("alain", "numTirageSeq = "  + Integer.toString(Jeu.numTirageSeq) + "\n");

                questionId = Jeu.numQuestionsTirage[Jeu.numTirageSeq];

                Jeu.numTirageSeq++;
                if(Jeu.numTirageSeq == Jeu.nbQuestions) Jeu.numTirageSeq = 0;
            }


            //Log.i("alain", "questionId = "  + Integer.toString(questionId) + "\n");

            Jeu.qId = Integer.toString(questionId);

            //Log.i("alain", "qId = " + Jeu.qId  + "\n");


            try {
                SQLiteOpenHelper qscienceDataBaseHelper = new QscienceDatabaseHelper(getActivity());
                db = qscienceDataBaseHelper.getReadableDatabase();

                //Log.i("alain", "qId = " + Jeu.qId  + "\n");

                cursor = db.query("QUESTIONS",
                        new String[] {"QUESTION", "EXPLICATION", "NB_ANSWERS","NBOK","ANSWER1","ANSWER2","ANSWER3","ANSWER4"},
                        "_id = ?",
                        new String[]{Jeu.qId},
                        null, null, null);
                if (cursor.moveToFirst()){

                    questionData[0] = cursor.getString(0);
                    questionData[1] = cursor.getString(1);
                    questionData[2] = cursor.getString(2);
                    questionData[3] = cursor.getString(3);
                    questionData[4] = cursor.getString(4);
                    questionData[5] = cursor.getString(5);
                    questionData[6] = cursor.getString(6);
                    questionData[7] = cursor.getString(7);

                }
                cursor.close();
                db.close();
                return true;
            }catch (SQLiteException e){
                //Log.i("alain", "erreur BD = "+ e.toString());
                return false;
            }
        }

        protected void onPostExecute(Boolean success){
            if (!success){

                //Log.i("alain", "Enter in onPostExecute error "  + "\n");

                Toast toast=Toast.makeText(getActivity(), "Database invalide", Toast.LENGTH_SHORT);
                toast.show();
            }else {


                //Log.i("alain", "Enter in onPostExecute success "  + "\n");

                textLoad.setVisibility(View.INVISIBLE);

                //Log.i("alain", "questionId = " + Integer.toString(questionId)  + "\n");

                //nquestion.setText(Jeu.qId);
                Quiz.numQuestion++;


                textQuestion.setText(questionData[0]);


                textAnswer1.setBackgroundColor(0xFFFFFFFF);
                textAnswer2.setBackgroundColor(0xFFFFFFFF);
                textAnswer3.setBackgroundColor(0xFFFFFFFF);
                textAnswer4.setBackgroundColor(0xFFFFFFFF);


                int nb = Integer.parseInt(questionData[2]);
                if(nb == 4){
                    textAnswer1.setText(questionData[4]);
                    textAnswer1.setVisibility(View.VISIBLE);

                    textAnswer2.setText(questionData[5]);
                    textAnswer2.setVisibility(View.VISIBLE);

                    textAnswer3.setText(questionData[6]);
                    textAnswer3.setVisibility(View.VISIBLE);

                    textAnswer4.setText(questionData[7]);
                    textAnswer4.setVisibility(View.VISIBLE);

                }else if(nb == 3){
                    textAnswer1.setText(questionData[4]);
                    textAnswer1.setVisibility(View.VISIBLE);

                    textAnswer2.setText(questionData[5]);
                    textAnswer2.setVisibility(View.VISIBLE);

                    textAnswer3.setText(questionData[6]);
                    textAnswer3.setVisibility(View.VISIBLE);

                    textAnswer4.setVisibility(View.GONE);

                }else {
                    textAnswer1.setText(questionData[4]);
                    textAnswer1.setVisibility(View.VISIBLE);

                    textAnswer2.setText(questionData[5]);
                    textAnswer2.setVisibility(View.VISIBLE);

                    textAnswer3.setVisibility(View.GONE);

                    textAnswer4.setVisibility(View.GONE);
                }


                onBackgroundTaskDataObtained(questionData);

            }

        }
    }


    private class update_somme2 extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/update_somme2.php?id=" + Jeu.sid + "&somme=" + ssomme + "&datesomme=" + Jeu.sdatemoyenne + "&note1=" + Jeu.svalueNotes[0]
            //        + "&note2=" + Jeu.svalueNotes[1] + "&note3=" + Jeu.svalueNotes[2]+ "&note4=" + Jeu.svalueNotes[3]+ "&note5=" + Jeu.svalueNotes[4]
              //      + "&note6=" + Jeu.svalueNotes[5] + "&note7=" + Jeu.svalueNotes[6]+ "&note8=" + Jeu.svalueNotes[7]+ "&note9=" + Jeu.svalueNotes[8]
              //      + "&note10=" + Jeu.svalueNotes[9];

            String url = "http://aperrault.atspace.cc/update_somme2.php?id=" + Jeu.sid + "&somme=" + ssomme + "&datesomme=" + Jeu.sdatemoyenne + "&note1=" + Jeu.svalueNotes[0]
                    + "&note2=" + Jeu.svalueNotes[1] + "&note3=" + Jeu.svalueNotes[2]+ "&note4=" + Jeu.svalueNotes[3]+ "&note5=" + Jeu.svalueNotes[4]
                    + "&note6=" + Jeu.svalueNotes[5] + "&note7=" + Jeu.svalueNotes[6]+ "&note8=" + Jeu.svalueNotes[7]+ "&note9=" + Jeu.svalueNotes[8]
                    + "&note10=" + Jeu.svalueNotes[9];



            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();

                int connecttimeout = conn.getConnectTimeout();
                //Log.i("alain", "connecttimeout = "  + Integer.toString(connecttimeout) + "\n");

                int status = conn.getResponseCode();

                if(status == 200 || status == 204){
                    return true;

                }else{
                    //Log.i("alain", "Status HTTP = "  + Integer.toString(status) + "\n");
                    return false;

                }


            }catch(Exception e){
                //Log.i("alain", "Erreur HTTP = "  + e + "\n");
                return false;

            }finally {
                conn.disconnect();

            }

        }
        protected void onPostExecute(Boolean success){
            if(!success){
                if (Locale.getDefault().getLanguage().equals("fr")) {
                    Toast toast = Toast.makeText(getActivity(), "Erreur réseau", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }else{
                Toast toast = Toast.makeText(getActivity(), "Update moyenne OK", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class update_notes2 extends AsyncTask<String, Void, Boolean> {


        protected Boolean doInBackground(String... params){

            //String url = "http://192.168.1.18/alain/update_notes2.php?id=" + Jeu.sid + "&note1=" + Jeu.svalueNotes[0]
              //      + "&note2=" + Jeu.svalueNotes[1] + "&note3=" + Jeu.svalueNotes[2]+ "&note4=" + Jeu.svalueNotes[3]+ "&note5=" + Jeu.svalueNotes[4]
              //      + "&note6=" + Jeu.svalueNotes[5] + "&note7=" + Jeu.svalueNotes[6]+ "&note8=" + Jeu.svalueNotes[7]+ "&note9=" + Jeu.svalueNotes[8]
              //      + "&note10=" + Jeu.svalueNotes[9];

            String url = "http://aperrault.atspace.cc/update_notes2.php?id=" + Jeu.sid + "&note1=" + Jeu.svalueNotes[0]
                  + "&note2=" + Jeu.svalueNotes[1] + "&note3=" + Jeu.svalueNotes[2]+ "&note4=" + Jeu.svalueNotes[3]+ "&note5=" + Jeu.svalueNotes[4]
                  + "&note6=" + Jeu.svalueNotes[5] + "&note7=" + Jeu.svalueNotes[6]+ "&note8=" + Jeu.svalueNotes[7]+ "&note9=" + Jeu.svalueNotes[8]
                  + "&note10=" + Jeu.svalueNotes[9];



            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();

                int connecttimeout = conn.getConnectTimeout();
                //Log.i("alain", "connecttimeout = "  + Integer.toString(connecttimeout) + "\n");

                int status = conn.getResponseCode();

                if(status == 200 || status == 204){
                    return true;

                }else{
                    //Log.i("alain", "Status HTTP = "  + Integer.toString(status) + "\n");
                    return false;

                }


            }catch(Exception e){
                //Log.i("alain", "Erreur HTTP = "  + e + "\n");
                return false;

            }finally {
                conn.disconnect();

            }

        }
    }


    private void runTimer(){
        final android.os.Handler handler = new android.os.Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if(running){
                    Quiz.secs--;
                    if(Quiz.secs <= 0){
                        Quiz.secs = 0;
                        running = false;

                        Quiz.timeoutQuestion = true;
                        Quiz.stateSuite = false;
                        Quiz.stateValidation = false;

                        Quiz.goodAnswer = false;
                        Quiz.numErreur++;

                        if (Locale.getDefault().getLanguage().equals("fr")) {
                            Toast toast = Toast.makeText(getActivity(), "Temps écoulé pour la réponse", Toast.LENGTH_SHORT);
                            toast.show();
                        }else{
                            Toast toast = Toast.makeText(getActivity(), "Timeout for answer", Toast.LENGTH_SHORT);
                            toast.show();
                        }


                        layout_valider.setVisibility(View.GONE);
                        layout_suite.setVisibility(View.VISIBLE);
                    }
                }
                handler.postDelayed(this,1000);
            }
        });
    }


    @Override
    public void onResume(){
        //Log.i("alain", "Enter in Question onResume "  + "\n");
        super.onResume();

        Quiz.display = true;
        Quiz.config = Config.Question;

    }

    @Override
    public void onPause(){
        //Log.i("alain", "Enter in Question onPause "  + "\n");
        running = false;
        super.onPause();
    }

    @Override
    public void onStop(){
        //Log.i("alain", "Enter in Question onStop "  + "\n");
        super.onStop();
    }


    @Override
    public void onDestroy(){
        //Log.i("alain", "Enter in Question onDestroy "  + "\n");
        //Log.i("alain", "Stop and shutdown TTS "  + "\n");
        if(myTTS != null){
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
    }

}
