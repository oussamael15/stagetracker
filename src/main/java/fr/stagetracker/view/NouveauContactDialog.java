package fr.stagetracker.view;

import fr.stagetracker.model.Contact;
import fr.stagetracker.model.Entreprise;
import fr.stagetracker.model.StageTracker;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import fr.stagetracker.database.DatabaseManager;
import java.sql.SQLException;

public class NouveauContactDialog extends Stage {

    public NouveauContactDialog(StageTracker tracker, DatabaseManager database) {

        setTitle("Nouveau contact");
        initModality(Modality.APPLICATION_MODAL);

        Label titre = new Label("Ajouter un contact");
        titre.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label labelNom = new Label("Nom");
        TextField champNom = new TextField();
        champNom.setPromptText("Ex : Martin");

        Label labelPrenom = new Label("Prénom");
        TextField champPrenom = new TextField();
        champPrenom.setPromptText("Ex : Sophie");

        Label labelEmail = new Label("Email");
        TextField champEmail = new TextField();
        champEmail.setPromptText("Ex : sophie.martin@entreprise.com");

        Label labelTelephone = new Label("Téléphone");
        TextField champTelephone = new TextField();
        champTelephone.setPromptText("Ex : 06 12 34 56 78");

        Label labelPoste = new Label("Poste");
        TextField champPoste = new TextField();
        champPoste.setPromptText("Ex : Responsable RH");

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

            String nom = champNom.getText().trim();
            String prenom = champPrenom.getText().trim();
            String email = champEmail.getText().trim();
            String telephone = champTelephone.getText().trim();
            String poste = champPoste.getText().trim();

            Entreprise entreprise = choixEntreprise.getValue();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()
                    || entreprise == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Veuillez renseigner le nom, le prénom, l'email et l'entreprise."
                ).showAndWait();

                return;
            }

            try {
                Contact contact = new Contact(nom, prenom, email);

                contact.setTelephone(telephone);
                contact.setPostMetier(poste);

                entreprise.ajouterContact(contact);

                database.enregistrerContact(contact);

                tracker.getEtudiant().garderContact(contact);

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
                labelPrenom,
                champPrenom,
                labelEmail,
                champEmail,
                labelTelephone,
                champTelephone,
                labelPoste,
                champPoste,
                labelEntreprise,
                choixEntreprise,
                enregistrer,
                annuler
        );

        contenu.setPadding(new Insets(25));
        contenu.setPrefWidth(420);

        setScene(new Scene(contenu));
    }
}
