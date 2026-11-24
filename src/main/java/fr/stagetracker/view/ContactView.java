package fr.stagetracker.view;

import fr.stagetracker.model.Contact;
import fr.stagetracker.model.StageTracker;
import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import fr.stagetracker.database.DatabaseManager;


public class ContactView extends VBox {

    private final StageTracker tracker;
    private final DatabaseManager database;
    private final TableView<Contact> tableau = new TableView<>();

    public ContactView(StageTracker tracker, DatabaseManager database) {
        this.tracker = tracker;
        this.database = database;

        setPadding(new Insets(70));
        setSpacing(25);
        setFillWidth(true);

        Label titre = new Label("Mes contacts");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label sousTitre = new Label(
                "Retrouvez et gérez vos contacts professionnels."
        );
        sousTitre.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");

        Button ajouterContact = new Button("+ Ajouter un contact");
        ajouterContact.setStyle(
                "-fx-background-color: #238636;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );

        VBox titres = new VBox(7, titre, sousTitre);

        HBox entete = new HBox(titres, ajouterContact);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setSpacing(20);
        HBox.setHgrow(titres, Priority.ALWAYS);

        Label titreTableau = new Label("Tous les contacts");
        titreTableau.setStyle(
                "-fx-font-size: 19px; -fx-font-weight: bold;"
        );

        TableColumn<Contact, String> nom = new TableColumn<>("Nom");
        nom.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getNom())
        );

        TableColumn<Contact, String> prenom = new TableColumn<>("Prénom");
        prenom.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getPrenom())
        );

        TableColumn<Contact, String> entreprise = new TableColumn<>("Entreprise");
        entreprise.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getEntreprise() == null
                                ? "—"
                                : cell.getValue().getEntreprise().getNomEntreprise()
                )
        );

        TableColumn<Contact, String> poste = new TableColumn<>("Poste");
        poste.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getPostMetier() == null
                                ? "—"
                                : cell.getValue().getPostMetier()
                )
        );

        TableColumn<Contact, String> email = new TableColumn<>("Email");
        email.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getEmail())
        );

        TableColumn<Contact, String> telephone = new TableColumn<>("Téléphone");
        telephone.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getTelephone() == null
                                ? "—"
                                : cell.getValue().getTelephone()
                )
        );

        tableau.getColumns().addAll(
                nom, prenom, entreprise, poste, email, telephone
        );

        tableau.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        actualiserTableau();

        ajouterContact.setOnAction(event -> {
            NouveauContactDialog dialog = new NouveauContactDialog(tracker, database);
            dialog.showAndWait();
            actualiserTableau();
        });

        VBox blocTableau = new VBox(15, titreTableau, tableau);
        blocTableau.setPadding(new Insets(20));
        blocTableau.setStyle(
                "-fx-background-color: #222D38;" +
                        "-fx-background-radius: 10;"
        );

        VBox.setVgrow(tableau, Priority.ALWAYS);
        VBox.setVgrow(blocTableau, Priority.ALWAYS);

        getChildren().addAll(entete, blocTableau);
    }

    private void actualiserTableau() {
        tableau.setItems(
                FXCollections.observableArrayList(tracker.getContacts())
        );
    }
}
