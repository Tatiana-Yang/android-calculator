package fr.ensicaen.ecole.calculatrice2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CalculatriceActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button buttonVirgule;
    private Button buttonPlus;
    private Button buttonMoins;
    private Button buttonMulti;
    private Button buttonDivis;
    private Button buttonEgale;
    private Button buttonPourcentage;
    private Button buttonCarre;
    private Button buttonPuissance;
    private Button buttonC;
    private Button buttonPi;
    private boolean again = false;
    private TextView viewCalcul;

    private ArrayList<Button> listButton;
    private ArrayList<String> listButtonOpe;

    private void instanciationEtListe(){
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        buttonVirgule = findViewById(R.id.buttonVirgule);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonMoins = findViewById(R.id.buttonMoins);
        buttonMulti = findViewById(R.id.buttonMultiplication);
        buttonDivis = findViewById(R.id.buttonDiviser);
        buttonEgale = findViewById(R.id.buttonEgale);
        buttonPourcentage = findViewById(R.id.buttonPourcentage);
        buttonC = findViewById(R.id.buttonC);
        buttonPi = findViewById(R.id.buttonPi);
        buttonCarre = findViewById(R.id.buttonCarre);
        buttonPuissance = findViewById(R.id.buttonPuissance);

        listButton = new ArrayList<>();
        listButton.add(button0);
        listButton.add(button1);
        listButton.add(button2);
        listButton.add(button3);
        listButton.add(button4);
        listButton.add(button5);
        listButton.add(button6);
        listButton.add(button7);
        listButton.add(button8);
        listButton.add(button9);
        listButton.add(buttonPlus);
        listButton.add(buttonMoins);
        listButton.add(buttonDivis);
        listButton.add(buttonMulti);
        listButton.add(buttonVirgule);
        listButton.add(buttonEgale);
        listButton.add(buttonPourcentage);
        listButton.add(buttonC);
        listButton.add(buttonPi);
        listButton.add(buttonCarre);
        listButton.add(buttonPuissance);

        listButtonOpe = new ArrayList<>();
        listButtonOpe.add("+");
        listButtonOpe.add("-");
        listButtonOpe.add("/");
        listButtonOpe.add("*");
        listButtonOpe.add("%");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculatrice_view);

        instanciationEtListe();

        viewCalcul = findViewById(R.id.viewCalcul);
        viewCalcul.setText("");

        for(Button button : this.listButton){
            button.setOnClickListener(this);
        }
    }

    public void onClick(View v){
        if(again){
           viewCalcul.setText("");
           again = false;
        }
        for(Button button : this.listButton){
            // On affiche les nombres ainsi que les opérateurs mais pas le signe egale ainsi que le C pour l'effacement
            if(v == button && button != buttonEgale && button != buttonC){
                String text = (String) viewCalcul.getText();

                if(button == buttonPi){
                    String pi = "\u03c0";
                    text += pi;
                }
                else if(button == buttonCarre){
                    text += "^2";
                }
                else {
                    String buttonText = (String) button.getText();

                    //Permet de ne pas pouvoir mettre des opérateurs les uns à la suite des autres
                    if (isOperateur(buttonText) && !text.isEmpty()) {
                        String last = "" + text.charAt(text.length() - 1);
                        if (!isOperateur(last)) {
                            text += buttonText;
                        }
                    } else {
                        text += buttonText;
                    }
                }

                viewCalcul.setText(text);
            }
            if(v == button && button == buttonC){
                viewCalcul.setText("");
            }
            // Dans le cas ou nous appuyons sur "=" pour obtenir le résultat de notre calcul
            if(v == button && button == buttonEgale){
                String affichage = (String) viewCalcul.getText();

                String [] termes = extractTermes(affichage, false);

                try {
                    String resultat = calcul(termes[0], termes[1], termes[2]);
                    again = true;
                    viewCalcul.setText(resultat);
                }catch(Exception e){
                    e.printStackTrace();
                    viewCalcul.setText("");

                    //Affichage d'une pop-up en cas d'erreur
                    Context context = getApplicationContext();
                    CharSequence text = "Erreur de syntaxe !";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                }
            }
        }
    }

    /**
     * Ce parametre permet de faire le calcul de notre premier terme et le second en fonction de notre opérateur
     * @param terme1 le premier terme
     * @param terme2 le second terme
     * @param ope notre opérateur
     * @return String le calcul qui a été réaliser que nous avons converti en chaine de caractère
     */
    private String calcul(String terme1, String terme2, String ope){
        double resultat = termePresent(terme1, ope);
        switch (ope) {
            case "+":
                resultat += termePresent(terme2, ope);
                break;
            case "-":
                resultat -= termePresent(terme2, ope);
                break;
            case "*":
            case "":
                resultat *= termePresent(terme2, ope);
                break;
            case "/":
                resultat /= termePresent(terme2, ope);
                break;
            case "%":
                resultat %= termePresent(terme2, ope);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + ope);
        }
        return String.valueOf(resultat);
    }
    /**
     * Permet de savoir si le caractère que nous voulons ajouter est un opérateur ou non
     * @param str le chaine de caractère que nous voulons vérifier
     * @return boolean
     */
    private boolean isOperateur(String str){
        return listButtonOpe.contains(str);
    }

    /**
     * Methode qui permet d'extraire les deux termes et l'opérateur du TextView
     * Il permet également d'extraire les deux parties de la puissance
     * dans le cas ou le paramètre isPow est égale à true
     * @param affichage le calcul à réaliser
     * @param presenceSpecial boolean qui permet de connaitre l'extraction des deux parties du terme ayant une puissance ou PI
     * @return une liste de String avec les deux termes et l'opérateur
     */
    private String[] extractTermes(String affichage, boolean presenceSpecial){

        StringBuilder terme1 = new StringBuilder();
        StringBuilder terme2 = new StringBuilder();
        StringBuilder ope = new StringBuilder();
        boolean special = false;
        // on met dans une variable terme1 le premier terme, l'operateur dans ope et le second terme dans terme2
        for(int i = 0; i < affichage.length(); i++){
            String caractere = "" + affichage.charAt(i);
            String conv = "" + affichage.charAt(i);
            if(presenceSpecial){
                if(caractere.equals("^") || caractere.equals("\u03c0")){
                    special = true;
                }
            }

            if(listButtonOpe.contains(conv) && !presenceSpecial) {
                for(String str : listButtonOpe) {
                    if (conv.equals(str)) {
                        ope.append(conv);
                    }
                }
            }
            else {
                if(ope.toString().equals("") &&/* !caractere.equals("\u03c0") && */!special){
                    terme1.append(caractere);
                }
                else if(/*caractere.equals("\u03c0") ||*/ (special && !caractere.equals("^")) || !presenceSpecial){
                    terme2.append(caractere);
                }
            }
        }
        String [] termes = new String[3];
        termes[0] = terme1.toString();
        termes[1] = terme2.toString();
        termes[2] = ope.toString();
        return termes;
    }

    /**
     * Cette méthode perme de savoir si notre terme contient pi ou non
     * @param str le terme à vérifier
     * @return boolean
     */
    private boolean containsPi(String str){
        return str.contains("\u03c0");
    }

    /**
     * Cette méthode perme de savoir si notre terme est pi ou non
     * @param str le terme à vérifier
     * @return boolean true si c'est pi, false sinon
     */
    private boolean isPi(String str){
        return str.equals("\u03c0");
    }

    /**
     * Permet de savoir si notre terme est une puissance
     * @param str le terme à vérifier
     * @return true s'il y a une puissance, false sinon
     */
    private boolean containsPuissance(String str){
        return str.contains("^");
    }

    /**
     * Permet de récupérer le nombre sous forme de double
     * Pi = 3.1415 etc
     * les termes contenant "^" est calculer avec Math.pow
     * @param str la chaine de caractère à vérifier
     * @return un nombre double après avoir fait les calcul necessaire
     */
    private double getNombre(String str){
        double resultat =0;
        if(containsPi(str)){
            String[] termes = extractTermes(str, true);
            if(isPi(termes[0])){
                resultat = Math.PI;
            }
            else{
                resultat += Double.parseDouble(termes[0]);
            }

            if(isPi(termes[1])){
                resultat *= Math.PI;
            }
            else{
                resultat *= Double.parseDouble(termes[1]);
            }
        }
        else if(containsPuissance(str)){
            String[] termes = extractTermes(str, true);
            double terme1 = Double.parseDouble(termes[0]);
            double terme2 = Double.parseDouble(termes[1]);

            resultat = Math.pow(terme1, terme2);
        }
        else {
            resultat = Double.parseDouble(str);
        }
        return resultat;
    }

    /**
     * Permet de savoir si notre terme est vide ou non
     * Si oui nous retournons 1 dans le cas d'une multiplication ou division et 0 sinon
     * Si non nous faisons appel à la méthode getNombre()
     * @param str la chaine de caractère à vérifier
     * @param ope l'opérateur présent pour le calcul
     * @return une double
     */
    private double termePresent(String str, String ope){
        if(str.isEmpty()){
            if(ope.equals("*") || ope.equals("/") || ope.equals("")){
                return 1;
            }
            return 0;
        }
        return getNombre(str);
    }

}
