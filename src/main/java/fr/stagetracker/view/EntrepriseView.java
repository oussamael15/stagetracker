package fr.stagetracker.view;

import fr.stagetracker.model.Entreprise;
import fr.stagetracker.model.StageTracker;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import fr.stagetracker.database.DatabaseManager;

public class EntrepriseView extends VBox {

    private final StageTracker tracker;
    private final DatabaseManager database;

    public EntrepriseView(StageTracker tracker, DatabaseManager database) {
        this.tracker = tracker;
        this.database = database;

        setPadding(new Insets(70));
        setSpacing(25);
        setFillWidth(true);

        Label titre = new Label("Mes entreprises");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label sousTitre = new Label(
                "Consultez et gérez les entreprises qui vous intéressent."
        );
        sousTitre.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");

        Button ajouterEntreprise = new Button("+ Ajouter une entreprise");
        ajouterEntreprise.setStyle(
                "-fx-background-color: #238636;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );

        VBox titres = new VBox(7, titre, sousTitre);

        HBox entete = new HBox(titres, ajouterEntreprise);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setSpacing(20);
        HBox.setHgrow(titres, Priority.ALWAYS);

        Label titreTableau = new Label("Toutes les entreprises");
        titreTableau.setStyle(
                "-fx-font-size: 19px; -fx-font-weight: bold;"
        );

        TableView<Entreprise> tableau = new TableView<>();

        TableColumn<Entreprise, String> nom =
                new TableColumn<>("Entreprise");
        nom.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getNomEntreprise()
                )
        );

        TableColumn<Entreprise, String> secteur =
                new TableColumn<>("Secteur d'activité");
        secteur.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getSecteur()
                )
        );

        TableColumn<Entreprise, String> ville =
                new TableColumn<>("Ville");
        ville.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getVille()
                )
        );

        tableau.getColumns().addAll(nom, secteur, ville);

        tableau.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        tableau.setItems(
                FXCollections.observableArrayList(tracker.getEntreprises())
        );

        ajouterEntreprise.setOnAction(event -> {
            NouvelleEntrepriseDialog dialog =
                    new NouvelleEntrepriseDialog(tracker, database);

            dialog.showAndWait();

            tableau.setItems(
                    FXCollections.observableArrayList(tracker.getEntreprises())
            );
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
}
