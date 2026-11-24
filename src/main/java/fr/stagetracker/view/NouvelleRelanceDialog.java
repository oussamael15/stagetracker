package fr.stagetracker.view;

import fr.stagetracker.model.Candidature;
import fr.stagetracker.model.CanalRelance;
import fr.stagetracker.model.StageTracker;
import fr.stagetracker.model.StatutCandidature;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import fr.stagetracker.database.DatabaseManager;
import fr.stagetracker.model.Relance;
import java.sql.SQLException;
import java.util.List;

public class NouvelleRelanceDialog extends Stage {

    private final DatabaseManager database;

    public NouvelleRelanceDialog(StageTracker tracker, DatabaseManager database) {

        this.database=database;

        setTitle("Nouvelle relance");
        initModality(Modality.APPLICATION_MODAL);

        Label titre = new Label("Ajouter une relance");
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label labelCandidature = new Label("Candidature");

        ComboBox<Candidature> choixCandidature = new ComboBox<>(
                FXCollections.observableArrayList(
                        tracker.getCandidatures().stream()
                                .filter(c -> c.getStatut() != StatutCandidature.BROUILLON)
                                .toList()
                )
        );
        choixCandidature.setPromptText("Sélectionnez une candidature");
        choixCandidature.setMaxWidth(Double.MAX_VALUE);

        Label labelCanal = new Label("Canal");

        ComboBox<CanalRelance> choixCanal = new ComboBox<>(
                FXCollections.observableArrayList(CanalRelance.values())
        );
        choixCanal.setPromptText("Sélectionnez un canal");
        choixCanal.setMaxWidth(Double.MAX_VALUE);
        choixCanal.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(CanalRelance canal) {
                return canal == null ? "" : canal.getLibelle();
            }

            @Override
            public CanalRelance fromString(String texte) {
                return null;
            }
        });

        Label labelDate = new Label("Date de relance");
        DatePicker choixDate = new DatePicker(LocalDate.now());
        choixDate.setMaxWidth(Double.MAX_VALUE);

        Label labelCommentaire = new Label("Commentaire");
        TextArea champCommentaire = new TextArea();
        champCommentaire.setPromptText("Ex : Demander un retour après l'entretien");
        champCommentaire.setPrefRowCount(3);

        Button enregistrer = new Button("Enregistrer");
        Button annuler = new Button("Annuler");

        enregistrer.setDefaultButton(true);
        annuler.setCancelButton(true);

        enregistrer.setOnAction(event -> {

            Candidature candidature = choixCandidature.getValue();
            CanalRelance canal = choixCanal.getValue();
            LocalDate date = choixDate.getValue();
            String commentaire = champCommentaire.getText().trim();

            if (candidature == null || canal == null
                    || date == null || commentaire.isEmpty()) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Veuillez renseigner tous les champs."
                ).showAndWait();

                return;
            }

            boolean enregistree = tracker.getEtudiant().enregistrerRelance(
                    candidature,
                    date,
                    canal,
                    commentaire
            );

            if (enregistree) {

                try {
                    List<Relance> relances = candidature.getRelances();

                    Relance relance = relances.get(relances.size() - 1);

                    database.enregistrerRelance(relance, candidature);

                    close();

                } catch (SQLException e) {

                    new Alert(
                            Alert.AlertType.ERROR,
                            "Erreur lors de la sauvegarde de la relance : " + e.getMessage()
                    ).showAndWait();
                }

            } else {
                new Alert(
                        Alert.AlertType.ERROR,
                        "Impossible d'enregistrer cette relance."
                ).showAndWait();
            }
        });

        annuler.setOnAction(event -> close());

        VBox contenu = new VBox(
                12,
                titre,
                labelCandidature,
                choixCandidature,
                labelCanal,
                choixCanal,
                labelDate,
                choixDate,
                labelCommentaire,
                champCommentaire,
                enregistrer,
                annuler
        );

        contenu.setPadding(new Insets(25));
        contenu.setPrefWidth(420);

        setScene(new Scene(contenu));
    }
}
