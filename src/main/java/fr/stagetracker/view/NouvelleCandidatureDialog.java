package fr.stagetracker.view;

import fr.stagetracker.model.Candidature;
import fr.stagetracker.model.Entreprise;
import fr.stagetracker.model.StageTracker;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import fr.stagetracker.database.DatabaseManager;
import java.sql.SQLException;
import java.util.List;

public class NouvelleCandidatureDialog extends Stage {

    private final StageTracker tracker;
    private final DatabaseManager database;

    public NouvelleCandidatureDialog(StageTracker tracker, DatabaseManager database) {
        this.tracker = tracker;
        this.database=database;

        setTitle("Nouvelle candidature");
        initModality(Modality.APPLICATION_MODAL);

        Label titre = new Label("Ajouter une candidature");
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label labelPoste = new Label("Intitulé du poste");
        TextField champPoste = new TextField();
        champPoste.setPromptText("Ex : Développeur Java");

        Label labelEntreprise = new Label("Entreprise");
        ComboBox<Entreprise> choixEntreprise = new ComboBox<>(
                FXCollections.observableArrayList(tracker.getEntreprises())
        );
        choixEntreprise.setPromptText("Sélectionnez une entreprise");
        choixEntreprise.setMaxWidth(Double.MAX_VALUE);

        Button enregistrer = new Button("Enregistrer");
        Button annuler = new Button("Annuler");

        enregistrer.setDefaultButton(true);
        annuler.setCancelButton(true);

        enregistrer.setOnAction(event -> {
            String poste = champPoste.getText().trim();
            Entreprise entreprise = choixEntreprise.getValue();

            if (poste.isEmpty() || entreprise == null) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Veuillez renseigner le poste et sélectionner une entreprise."
                ).showAndWait();
                return;
            }

            try {
                Candidature candidature = new Candidature(poste, entreprise);
                database.enregistrerCandidature(candidature);
                tracker.ajouterCandidature(candidature);
                close();
            } catch (SQLException | IllegalArgumentException | IllegalStateException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });

        annuler.setOnAction(event -> close());

        VBox contenu = new VBox(
                12,
                titre,
                labelPoste,
                champPoste,
                labelEntreprise,
                choixEntreprise,
                enregistrer,
                annuler
        );

        contenu.setPadding(new Insets(25));
        contenu.setPrefWidth(400);

        setScene(new javafx.scene.Scene(contenu));
    }
}
