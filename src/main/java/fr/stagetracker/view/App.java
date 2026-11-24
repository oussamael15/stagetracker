package fr.stagetracker.view;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import fr.stagetracker.model.Etudiant;
import fr.stagetracker.model.StageTracker;
import fr.stagetracker.database.DatabaseManager;
import fr.stagetracker.model.Entreprise;
import java.sql.SQLException;
import fr.stagetracker.model.Contact;
import fr.stagetracker.model.Candidature;

public class App extends Application {

    private BorderPane root;
    private StageTracker tracker;
    private DatabaseManager database;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Etudiant etudiant = new Etudiant(
                "EL MOUSSAOUI",
                "Oussama",
                "oussama.elmoussaoui13@gmail.com"
        );

        tracker = new StageTracker(etudiant);

        try {
            database = new DatabaseManager();

            for (Entreprise entreprise : database.chargerEntreprises()) {
                etudiant.enregistrerEntreprise(entreprise);
            }

            for (Contact contact : database.chargerContacts(tracker.getEntreprises())) {
                etudiant.garderContact(contact);
            }
            for (Candidature candidature : database.chargerCandidatures(
                    tracker.getEntreprises(),
                    tracker.getContacts()
            )) {
                tracker.ajouterCandidature(candidature);
            }

            database.chargerRelances(tracker.getCandidatures());

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'accès à la base de données", e);
        }

        root = new BorderPane();

        Sidebar sidebar = new Sidebar(this::afficherPage);
        root.setLeft(sidebar);

        afficherPage("dashboard");

        Scene scene = new Scene(root, 1320, 900);
        stage.setTitle("StageTracker");
        stage.setScene(scene);
        stage.show();
    }

    private void afficherPage(String page) {
        String titre = switch (page) {
            case "dashboard" -> "Tableau de bord";
            case "candidatures" -> "Candidatures";
            case "entreprises" -> "Entreprises";
            case "contacts" -> "Contacts";
            case "relances" -> "Relances";
            default -> "Page inconnue";
        };

        if (page.equals("dashboard")) {
            root.setCenter(new DashboardView(tracker, database));
        } else if (page.equals("candidatures")) {
            root.setCenter(new CandidatureView(tracker, database));
        } else if (page.equals("entreprises")) {
            root.setCenter(new EntrepriseView(tracker, database));
        } else if (page.equals("contacts")) {
            root.setCenter(new ContactView(tracker, database));
        } else if (page.equals("relances")) {
            root.setCenter(new RelanceView(tracker, database));
        } else {
            Label label = new Label(titre);
            label.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

            VBox contenu = new VBox(label);
            contenu.setPadding(new Insets(30));

            root.setCenter(contenu);
        }
    }
    @Override
    public void stop() {
        if (database != null) {
            try {
                database.fermerConnexion();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
