package fr.stagetracker.view;

import fr.stagetracker.model.Entreprise;
import fr.stagetracker.model.StageTracker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import fr.stagetracker.database.DatabaseManager;
import java.sql.SQLException;

public class NouvelleEntrepriseDialog extends Stage {

    public NouvelleEntrepriseDialog(StageTracker tracker, DatabaseManager database) {

        setTitle("Nouvelle entreprise");
        initModality(Modality.APPLICATION_MODAL);

        Label titre = new Label("Ajouter une entreprise");
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label labelNom = new Label("Nom de l'entreprise");
        TextField champNom = new TextField();
        champNom.setPromptText("Ex : Capgemini");

        Label labelVille = new Label("Ville");
        TextField champVille = new TextField();
        champVille.setPromptText("Ex : Toulouse");

        Label labelSecteur = new Label("Secteur d'activité");
        TextField champSecteur = new TextField();
        champSecteur.setPromptText("Ex : Informatique");

        Button enregistrer = new Button("Enregistrer");
        Button annuler = new Button("Annuler");

        enregistrer.setDefaultButton(true);
        annuler.setCancelButton(true);

        enregistrer.setOnAction(event -> {
            String nom = champNom.getText().trim();
            String ville = champVille.getText().trim();
            String secteur = champSecteur.getText().trim();

            if (nom.isEmpty()) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Le nom de l'entreprise est obligatoire."
                ).showAndWait();
                return;
            }

            try {
                Entreprise entreprise =
                        new Entreprise(nom, ville, secteur);

                database.enregistrerEntreprise(entreprise);

                tracker.getEtudiant().enregistrerEntreprise(entreprise);

                close();

            } catch (SQLException | IllegalArgumentException | IllegalStateException e) {
                new Alert(
                        Alert.AlertType.ERROR,
                        e.getMessage()
                ).showAndWait();
            }
        });

        annuler.setOnAction(event -> close());

        VBox contenu = new VBox(
                12,
                titre,
                labelNom,
                champNom,
                labelVille,
                champVille,
                labelSecteur,
                champSecteur,
                enregistrer,
                annuler
        );

        contenu.setPadding(new Insets(25));
        contenu.setPrefWidth(400);

        setScene(new Scene(contenu));
    }
}
