import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Cette classe définit les composantes graphiques de l'interface utilisateur.
 * <p>
 * La classe View correspond à la Vue dans le modèle Modèle-Vue-Contrôleur (MVC). Dans cette version de l'application, le
 * modèle (classe Client) et le contrôleur (classe Controller) sont initialisés lors de l'appel du constructeur de
 * Controller dans la méthode start() de l'application JavaFx.
 */

public class View extends Application {

    /**
     * La fenêtre de l'interface utilisateur.
     */
    private Stage window;
    /**
     * La variable de classe qui permet de mettre en oeuvre la conception de l'application selon le modèle MVC.
     */
    private static Controller controller;

    /**
     * Cette méthode sert initialiser le contenu de la scène qui sera affichée dans la fenêtre.
     * <p>
     * Toutes les composantes graphiques de l'interface graphique sont définies dans cette méthode. La gestion des
     * événements se fait par le biais des méthodes de la classe Controller.
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        window.setTitle("Inscription UdeM");

        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane, 800,400);

        controller = new Controller(new Client(),scene);

        // LEFT PANE

        Text courseListTitle = new Text("Liste des cours");
        courseListTitle.setFont(Font.font("Helvetica",20));

        VBox layoutTable = new VBox(10);
        TableView<Course> table = new TableView<>();

        // code column
        TableColumn<Course, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setMinWidth(75);
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));// class property

        // name column
        TableColumn<Course, String> nameColumn = new TableColumn<>("Cours");
        nameColumn.setMinWidth(300);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));// class property

        table.getColumns().addAll(codeColumn,nameColumn);
        layoutTable.getChildren().add(table);

        HBox layoutCB = new HBox(10);

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("Hiver","Ete", "Automne");
        choiceBox.setValue("Hiver");

        Button chargerButton =new Button("charger");

        layoutCB.setSpacing(70);
        layoutCB.setAlignment(Pos.CENTER);
        layoutCB.getChildren().addAll(choiceBox, chargerButton);

        VBox leftPane = new VBox();
        leftPane.setPadding(new Insets(20,30,20,30));
        leftPane.setSpacing(15);
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.getChildren().addAll(courseListTitle,layoutTable,layoutCB);

        // RIGHT PANE

        Text registrationFormTitle = new Text("Formulaire d'inscription");
        registrationFormTitle.setFont(Font.font("Helvetica",20));

        Label prenomLabel = new Label("Prénom");
        Label nomLabel = new Label("Nom");
        Label emaiLabel = new Label("Email");
        Label matriculeLabel = new Label("Matricule");

        TextField prenomInput = new TextField();
        prenomInput.setPromptText("Dany");
        TextField nomInput = new TextField();
        nomInput.setPromptText("Michel");
        TextField emailInput = new TextField();
        emailInput.setPromptText("dany@umontreal.ca");
        TextField matriculeInput = new TextField();
        matriculeInput.setPromptText("12345678 (8 chiffres)");

        Button envoyerButton =new Button("envoyer");

        VBox labels = new VBox();
        labels.setSpacing(25);
        labels.getChildren().addAll(prenomLabel,nomLabel,emaiLabel,matriculeLabel);

        VBox inputFields = new VBox();
        inputFields.setSpacing(15);
        inputFields.setAlignment(Pos.CENTER);
        inputFields.getChildren().addAll(prenomInput,nomInput,emailInput,matriculeInput, envoyerButton);

        HBox dataSection = new HBox();
        dataSection.setSpacing(25);
        dataSection.getChildren().addAll(labels,inputFields);

        VBox rightPane = new VBox();
        rightPane.setPadding(new Insets(20,30,20,30));
        rightPane.setSpacing(15);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.getChildren().addAll(registrationFormTitle, dataSection);

        Separator verticalSep = new Separator(Orientation.VERTICAL);

        // MAIN LAYOUT
        gridPane.addColumn(0,leftPane);
        gridPane.addColumn(1,verticalSep);
        gridPane.addColumn(2,rightPane);
        gridPane.getColumnConstraints().add(new ColumnConstraints(390)); // column 0
        gridPane.getColumnConstraints().add(new ColumnConstraints(20)); // column 1
        gridPane.getColumnConstraints().add(new ColumnConstraints(390)); // column 2

        // EVENT HANDLERS

        chargerButton.setOnAction((action) -> controller.getSelection(choiceBox));
        envoyerButton.setOnAction((action) -> controller.getInputData(prenomInput, nomInput, emailInput, matriculeInput));

        window.setScene(scene);
        window.show();

    }

    public static void main(String[] args) {

        launch(args);
    }
}
