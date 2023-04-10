import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Cette classe traite les données saisies par l'utilisateur par le biais de l'interface graphique et
 * modifie les composantes de la scène en fonction des données modifiées dans la classe Client suite à la transmission
 * d'une requête.
 * <p>
 * La classe Controller correspond au Contrôleur dans le modèle Modèle-Vue-Contrôleur (MVC). Son premier rôle consiste à valider
 * les données saisies par l'utilisateur, nécessaires à la transmission des requêtes pour obtenir les cours offerts à
 * une session donnée ou d'inscription à un cours, par le client, au programme serveur. Son deuxième rôle consiste à faire
 * afficher le résultat de ces requêtes sur l'interface utilisateur.
 */
public class Controller {

    /**
     * L'expression régulière pour le format d'une entrée valide dans le champ "prénom".
     */
    public final static String PRENOM_REGEX_PATTERN =  "^[a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ._\s-]{2,60}$";

    /**
     * L'expression régulière pour le format d'une entrée valide dans le champ "nom".
     */
    public final static String NOM_REGEX_PATTERN =  "^[a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ._\s-]{2,60}$";

    /**
     * L'expression régulière pour le format d'une entrée valide dans le champ "email".
     */
    public final static String EMAIL_REGEX_PATTERN = "^(?=.{1,25}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    /**
     * L'expression régulière pour le format d'une entrée valide dans le champ "matricule".
     */
    public final static String MATRICULE_REGEX_PATTERN = "^\\d{8}$";

    /**
     * L'objet de la classe Client qui instancie le Modèle.
     */
    private Client client;

    /**
     * L'objet de la classe View qui instancie la Vue.
     */
    private Scene view;

    /**
     * L'objet de la classe TableView qui permet d'afficher la liste de cours pour une session donnée.
     */
    private TableView<Course> table;

    /**
     * L'objet de la classe Alert qui permet d'afficher un message d'erreur suite à la levée d'une exception.
     */
    private Alert alertException;

    /**
     * L'objet de la classe Alert qui permet d'afficher un message d'erreur de saisie de données par l'utilisateur.
     */
    private Alert alertInputValidation;

    /**
     * L'objet de la classe Alert qui permet d'afficher un message de confirmation suite au traitement d'une requête
     * par le serveur.
     */
    private Alert confirm;

    /**
     * La liste des erreurs de saisie de données par l'utilisateur.
     */
    private List<String> validationExceptionsList = new ArrayList<>();

    /**
     * Le constructeur de la classe Controlleur, qui initialise le Modèle et la Vue.
     * @param client correspond au Modèle de l'application.
     * @param view correspond à la Vue de l'application.
     */
    public Controller(Client client, Scene view) {
        this.client = client;
        this.view = view;
    }

    /**
     * Cette méthode traite l'événement du clic sur le bouton "charger".
     * <p>
     * Cette méthode permet d'obtenir la valeur de la session pour laquelle la liste de cours offerts est demandée par
     * l'utilisateur, afin de transmettre la requête au serveur (via le client) et d'afficher son résultat.
     *
     * @param choiceBox correspond à la session sélectionnée sur l'interface graphique.
     */

    public void getSelection(ChoiceBox<String> choiceBox){

        try {
            // Call charger(semester)
            this.client.charger(choiceBox.getValue());

            // Display courses list in tableview
            this.updateTable(client.getFilteredSortedList());

        } catch (Exception e) {
            displayAlertExeptionBox(e);
            System.exit(-1);
        }

    }

    /**
     * Cette méthode modifie le contenu affiché dans la table Liste de cours.
     *
     * @param filteredSortedList correspond à la liste de cours offerts pour une session donnée transmise par le serveur
     * (via le client).
     */
    private void updateTable(List<Course> filteredSortedList){

        if (this.table == null){
            List<TableView> lstTableViewElements = getNodesOfType((Pane)this.view.getRoot(), TableView.class);
;
            if (lstTableViewElements.isEmpty()){
                System.out.println("Le graphe des éléments de la vue (scene) ne contient pas un élément de type TableView.");
                System.exit(-1);
            }

            this.table = lstTableViewElements.get(0);
        }

        this.table.setItems(FXCollections.observableList(filteredSortedList));
    }

    /**
     * Cette méthode permet de rechercher dans le graphe de la scène une composante graphique spécifique (un noeud).
     *
     * @param parent correspond au noeud racine du graphe de la scène.
     * @param type correspond au type du noeud de la composante graphique recherchée dans le graphe de la scène.
     * @return la liste de tous les noeuds d'une composante graphique donnée.
     */
    private <T> List<T> getNodesOfType(Pane parent, Class<T> type) {
        List<T> elements = new ArrayList<>();
        for (Node node : parent.getChildren()) {
            if (node instanceof Pane) {
                elements.addAll(getNodesOfType((Pane) node, type));
            } else if (type.isAssignableFrom(node.getClass())) {
                elements.add((T) node);
            }
        }
        return Collections.unmodifiableList(elements);
    }

    /**
     * Cette méthode traite l'événement du clic sur le bouton "envoyer".
     * <p>
     * Cette méthode permet d'obtenir les valeurs pour le prénom, le nom, l'email, le matricule et le cours sélectionné
     * pour la création d'un RegistrationForm, afin de transmettre la requête au serveur (via le client) et d'afficher
     * son résultat.
     *
     * @param prenomInput correspond à la chaîne de caractères saisie par l'utilisateur.
     * @param nomInput correspond à la chaîne de caractères saisie par l'utilisateur.
     * @param emailInput correspond à la chaîne de caractères saisie par l'utilisateur.
     * @param matriculeInput correspond à la chaîne de caractères saisie par l'utilisateur.
     */

    public void getInputData(TextField prenomInput, TextField nomInput, TextField emailInput, TextField matriculeInput){

        RegistrationForm rf;

        try {
            rf = validateInputData(prenomInput, nomInput, emailInput, matriculeInput);

            if (!(rf == null)){

                // Call Inscrire(rf)
                this.client.inscrire(rf);

                // Display confirmationBox and clear all
                displayConfirmationBox();
                clearTextFields(prenomInput, nomInput, emailInput, matriculeInput);
                clearTable();
            }
        } catch (Exception e) {
            displayAlertExeptionBox(e);
            System.exit(-1);
        }

        // If no valid registration form is available, nothing else happens until user hits "envoyer" again with proper input data or exits app.
    }

    /**
     * Cette méthode valide toutes les entrées de données saisies par l'utilisateur.
     *
     * @param prenomInput correspond à la chaîne de caractères saisie par l'utilisateur.
     * @param nomInput correspond à la chaîne de caractères saisie par l'utilisateur.
     * @param emailInput correspond à la chaîne de caractères saisie par l'utilisateur.
     * @param matriculeInput correspond à la chaîne de caractères saisie par l'utilisateur.
     * @return un objet RegistrationForm, si les entrées saisies par l'utilisateur sont valides, sinon null.
     */
    private RegistrationForm validateInputData(TextField prenomInput, TextField nomInput, TextField emailInput, TextField matriculeInput){

        RegistrationForm rf;

        try {
            rf = new RegistrationForm(null, null, null, null, null);
            ObservableList<Course> selectedCourse;

            try {
                if(prenomInputIsValid(prenomInput))
                    rf.setPrenom(prenomInput.getText());
            } catch (InvalidRegexPatternException exc) {
                validationExceptionsList.add(exc.getMessage());
                prenomInput.clear();
            }

            try {
                if(nomInputIsValid(nomInput))
                    rf.setNom(nomInput.getText());
            } catch (InvalidRegexPatternException exc) {
                validationExceptionsList.add(exc.getMessage());
                nomInput.clear();
            }

            try {
                if(emailInputIsValid(emailInput))
                    rf.setEmail(emailInput.getText());
            } catch (InvalidRegexPatternException e) {
                validationExceptionsList.add(e.getMessage());
                emailInput.clear();
            }

            try {
                if(matriculeInputIsValid(matriculeInput))
                    rf.setMatricule(matriculeInput.getText());
            } catch (InvalidRegexPatternException e) {
                validationExceptionsList.add(e.getMessage());
                matriculeInput.clear();
            }

            try {
                selectedCourse = table.getSelectionModel().getSelectedItems();
                rf.setCourse(selectedCourse.get(0));
            } catch (Exception e) {
                validationExceptionsList.add("Il faut choisir un des cours offerts."); // e = IndexOutOfBoundException if selection missing
            }

            if(validationExceptionsList.isEmpty())
                return rf;
            else
                displayAlertInputValidationBox();

        } catch (RuntimeException e) {
            System.out.println("Exception autre qu'une erreur de saisie de données. ");
            e.printStackTrace();
        }
        validationExceptionsList.clear();
        return null;
    }

    /**
     * Cette méthode vide toutes les boîtes de saisie de données du formulaire d'inscription.
     *
     * @param prenomInput correspond au champ "prénom".
     * @param nomInput correspond au champ "nom".
     * @param emailInput correspond au champ "email".
     * @param matriculeInput correspond au champ "matricule".
     */

    private void clearTextFields (TextField prenomInput, TextField nomInput, TextField emailInput, TextField matriculeInput){
        prenomInput.clear();
        nomInput.clear();
        emailInput.clear();
        matriculeInput.clear();
    }

    /**
     * Cette méthode vide le contenu de la liste de cours.
     */
    private void clearTable(){
        table.getItems().clear();
    }

    /**
     * Cette méthode traite l'affichage d'un message d'erreur lié à la levée d'une exception via une boîte de dialogue.
     */

    private void displayAlertExeptionBox(Exception e){
        alertException = new Alert(Alert.AlertType.ERROR);
        alertException.setTitle("Message d'erreur");
        alertException.setHeaderText("Attention !");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Détails des traces d'appels (exception stacktrace):");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alertException.setContentText(e.toString());
        alertException.getDialogPane().setExpandableContent(expContent);
        alertException.showAndWait();
    }

    /**
     * Cette méthode traite l'affichage d'un message d'erreur lié au contrôle de saisie de données via une boîte de dialogue.
     */

    private void displayAlertInputValidationBox(){
        alertInputValidation = new Alert(Alert.AlertType.ERROR);
        alertInputValidation.setTitle("Message d'erreur");
        alertInputValidation.setHeaderText("Attention !");
        String msg = "Le formulaire est invalide: \n";
        for (String ex: validationExceptionsList)
            msg += "- " + ex + "\n";
        alertInputValidation.setContentText(msg);
        alertInputValidation.showAndWait();
    }

    /**
     * Cette méthode traite l'affichage d'une message de confirmation via une boîte de dialogue.
     */
    private void displayConfirmationBox(){
        confirm = new Alert(Alert.AlertType.INFORMATION);
        confirm.setTitle("Message de confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText(client.getConfirmMsgFromServer());
        confirm.showAndWait();
    }

    /**
     * Cette méthode valide le champ "prénom".
     *
     * @param prenomInput
     * @return true si la chaîne de caractères est valide.
     * @throws InvalidRegexPatternException
     */
    public boolean prenomInputIsValid(TextField prenomInput) throws InvalidRegexPatternException {

        if(nameMatches(prenomInput.getText(), PRENOM_REGEX_PATTERN))
            return true;
        else
            throw new InvalidRegexPatternException("Le prénom est absent ou ne respecte pas le format demandé");
    }

    /**
     * Cette méthode valide le champ "nom".
     *
     * @param nomInput
     * @return true si la chaîne de caractères est valide.
     * @throws InvalidRegexPatternException
     */
    public boolean nomInputIsValid(TextField nomInput) throws InvalidRegexPatternException {

        if(nameMatches(nomInput.getText(), NOM_REGEX_PATTERN))
            return true;
        else
            throw new InvalidRegexPatternException("Le nom est absent ou ne respecte pas le format demandé");
    }
    /**
     * Cette méthode valide le champ "email".
     *
     * @param emailInput
     * @return true si la chaîne de caractères est valide.
     * @throws InvalidRegexPatternException
     */
    public boolean emailInputIsValid(TextField emailInput) throws InvalidRegexPatternException {

        if(nameMatches(emailInput.getText(), EMAIL_REGEX_PATTERN))
            return true;
        else
            throw new InvalidRegexPatternException("Le email est absent ou ne respecte pas le format demandé");
    }

    /**
     * Cette méthode valide le champ "matricule".
     *
     * @param matriculeInput
     * @return true si la chaîne de caractères est valide.
     * @throws InvalidRegexPatternException
     */
    public boolean matriculeInputIsValid(TextField matriculeInput) throws InvalidRegexPatternException {

        if(nameMatches(matriculeInput.getText(), MATRICULE_REGEX_PATTERN))
            return true;
        else
            throw new InvalidRegexPatternException("Le matricule est absent ou ne respecte pas le format demandé");
    }


    /**
     * Cette méthode compare une chaîne de caractère à une expression régulière donnée en paramètre
     * @param name
     * @param regexPattern
     * @return
     */
    public boolean nameMatches(String name, String regexPattern){
        return Pattern.compile(regexPattern)
                .matcher(name)
                .matches();
    }

    /**
     * Cette méthode compare une chaîne de caractère à une expression régulière donnée en paramètre
     * @param email
     * @param regexPattern
     * @return
     */
    public boolean emailMatches(String email, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
    }

    /**
     * Cette méthode compare une chaîne de caractère à une expression régulière donnée en paramètre
     * @param matricule
     * @param regexPattern
     * @return
     */
    public boolean matriculeMatches(String matricule, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(matricule)
                .matches();
    }
}
