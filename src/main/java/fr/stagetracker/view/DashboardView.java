package fr.stagetracker.view;

import fr.stagetracker.database.DatabaseManager;
import fr.stagetracker.model.StageTracker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import fr.stagetracker.model.StatutCandidature;
import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.time.format.DateTimeFormatter;
import fr.stagetracker.model.Candidature;
import java.time.LocalDate;

public class DashboardView extends VBox {

    private final StageTracker tracker;
    private final DatabaseManager database;

    public DashboardView(StageTracker tracker, DatabaseManager database) {
        this.tracker=tracker;
        this.database=database;
        setPadding(new Insets(70));
        setSpacing(25);
        setFillWidth(true);

        Label titre = new Label("Tableau de bord");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label sousTitre = new Label("Suivez l'évolution de vos candidatures.");
        sousTitre.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");

        Button nouvelleCandidature = new Button("+ Nouvelle candidature");
        nouvelleCandidature.setStyle(
                "-fx-background-color: #238636;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );
        nouvelleCandidature.setMinWidth(Region.USE_PREF_SIZE);

        nouvelleCandidature.setOnAction(event -> {
            NouvelleCandidatureDialog dialog = new NouvelleCandidatureDialog(tracker, database);
            dialog.showAndWait();
            afficherPageActualisee();
        });

        VBox titres = new VBox(7, titre, sousTitre);
        HBox entete = new HBox(titres, nouvelleCandidature);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setSpacing(20);
        entete.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titres, Priority.ALWAYS);

        long total = tracker.getCandidatures().size();

        long brouillons = tracker.getCandidatures().stream()
                .filter(c -> c.getStatut() == StatutCandidature.BROUILLON)
                .count();

        long envoyees = tracker.getCandidatures().stream()
                .filter(c -> c.getStatut() == StatutCandidature.ENVOYEE)
                .count();

        long acceptees = tracker.getCandidatures().stream()
                .filter(c -> c.getStatut() == StatutCandidature.ACCEPTEE)
                .count();

        long refusees = tracker.getCandidatures().stream()
                .filter(c -> c.getStatut() == StatutCandidature.REFUSEE)
                .count();
        long aRelancer = tracker.signalerCandidature(LocalDate.now()).size();

        HBox statistiques = new HBox(15,
                creerCarte("Total", String.valueOf(total), "#388BFD"),
                creerCarte("Brouillons", String.valueOf(brouillons), "#A371F7"),
                creerCarte("Envoyées", String.valueOf(envoyees), "#D29922"),
                creerCarte("Acceptées", String.valueOf(acceptees), "#238636"),
                creerCarte("Refusées", String.valueOf(refusees), "#F85149"),
                creerCarte("À relancer", String.valueOf(aRelancer), "#F0883E")
        );



        Label titreTableau = new Label("Candidatures récentes");
        titreTableau.setStyle("-fx-font-size: 19px; -fx-font-weight: bold;");

        TableView<Candidature> tableau = new TableView<>();

        TableColumn<Candidature, String> entreprise = new TableColumn<>("Entreprise");
        entreprise.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getEntreprise().getNomEntreprise()
                ));

        TableColumn<Candidature, String> poste = new TableColumn<>("Poste");
        poste.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getPoste()));

        TableColumn<Candidature, String> statut = new TableColumn<>("Statut");
        statut.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStatut().toString()));

        TableColumn<Candidature, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(cell -> {
            if (cell.getValue().getDateEnvoi() == null) {
                return new ReadOnlyStringWrapper("—");
            }

            return new ReadOnlyStringWrapper(
                    cell.getValue().getDateEnvoi()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        });

        tableau.getColumns().addAll(entreprise, poste, statut, date);
        tableau.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        var candidatures = tracker.getCandidatures();

        int debut = Math.max(0, candidatures.size() - 5);

        tableau.setItems(FXCollections.observableArrayList(
                candidatures.subList(debut, candidatures.size())
        ));

        VBox blocTableau = new VBox(15, titreTableau, tableau);
        blocTableau.setPadding(new Insets(20));
        blocTableau.setStyle(
                "-fx-background-color: #222D38;" +
                        "-fx-background-radius: 10;"
        );
        VBox.setVgrow(tableau, Priority.ALWAYS);

        getChildren().addAll(entete, statistiques, blocTableau);
        VBox.setVgrow(blocTableau, Priority.ALWAYS);
    }

    private VBox creerCarte(String titre, String valeur, String couleur) {
        Label nom = new Label(titre);
        nom.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");

        Label nombre = new Label(valeur);
        nombre.setStyle(
                "-fx-text-fill: " + couleur + ";" +
                        "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;"
        );

        VBox carte = new VBox(12, nom, nombre);
        carte.setPadding(new Insets(20));
        carte.setPrefHeight(110);
        carte.setStyle(
                "-fx-background-color: #222D38;" +
                        "-fx-background-radius: 10;"
        );

        HBox.setHgrow(carte, Priority.ALWAYS);
        carte.setMaxWidth(Double.MAX_VALUE);

        return carte;
    }
    private void afficherPageActualisee() {
        getChildren().clear();

        DashboardView nouvelleVue = new DashboardView(tracker, database);
        getChildren().setAll(nouvelleVue.getChildren());
    }
}
