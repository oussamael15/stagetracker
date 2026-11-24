package fr.stagetracker.view;

import fr.stagetracker.model.Candidature;
import fr.stagetracker.model.Relance;
import fr.stagetracker.model.StageTracker;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import fr.stagetracker.database.DatabaseManager;

public class RelanceView extends VBox {

    private final StageTracker tracker;
    private final DatabaseManager database;
    private final TableView<RelanceLigne> tableau = new TableView<>();

    public RelanceView(StageTracker tracker, DatabaseManager database) {
        this.tracker = tracker;
        this.database=database;

        setPadding(new Insets(70));
        setSpacing(25);
        setFillWidth(true);

        Label titre = new Label("Mes relances");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label sousTitre = new Label(
                "Suivez vos relances et ne manquez aucune échéance."
        );
        sousTitre.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");

        Button ajouterRelance = new Button("+ Ajouter une relance");
        ajouterRelance.setStyle(
                "-fx-background-color: #238636;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );

        VBox titres = new VBox(7, titre, sousTitre);

        HBox entete = new HBox(titres, ajouterRelance);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setSpacing(20);
        HBox.setHgrow(titres, Priority.ALWAYS);

        Label titreTableau = new Label("Toutes les relances");
        titreTableau.setStyle(
                "-fx-font-size: 19px; -fx-font-weight: bold;"
        );

        TableColumn<RelanceLigne, String> entreprise =
                new TableColumn<>("Entreprise");
        entreprise.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("entreprise")
        );

        TableColumn<RelanceLigne, String> contact =
                new TableColumn<>("Contact");
        contact.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("contact")
        );

        TableColumn<RelanceLigne, String> date =
                new TableColumn<>("Date prévue");
        date.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("date")
        );

        TableColumn<RelanceLigne, String> canal =
                new TableColumn<>("Canal");
        canal.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("canal")
        );

        TableColumn<RelanceLigne, String> commentaire =
                new TableColumn<>("Commentaire");
        commentaire.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("commentaire")
        );

        tableau.getColumns().addAll(
                entreprise, contact, date, canal, commentaire
        );

        tableau.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        actualiserTableau();

        VBox blocTableau = new VBox(15, titreTableau, tableau);
        blocTableau.setPadding(new Insets(20));
        blocTableau.setStyle(
                "-fx-background-color: #222D38;" +
                        "-fx-background-radius: 10;"
        );
        ajouterRelance.setOnAction(event -> {
            NouvelleRelanceDialog dialog = new NouvelleRelanceDialog(tracker, database);
            dialog.showAndWait();
            actualiserTableau();
        });

        VBox.setVgrow(tableau, Priority.ALWAYS);
        VBox.setVgrow(blocTableau, Priority.ALWAYS);

        getChildren().addAll(entete, blocTableau);
    }

    private void actualiserTableau() {
        List<RelanceLigne> lignes = new ArrayList<>();

        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Candidature candidature : tracker.getCandidatures()) {

            for (Relance relance : candidature.getRelances()) {

                String nomEntreprise =
                        candidature.getEntreprise().getNomEntreprise();

                String nomContact =
                        candidature.getContact() == null
                                ? "—"
                                : candidature.getContact().afficherNomComplet();

                lignes.add(new RelanceLigne(
                        nomEntreprise,
                        nomContact,
                        relance.getDateRelance().format(formatDate),
                        relance.getCanal().getLibelle(),
                        relance.getCommentaire()
                ));
            }
        }

        tableau.setItems(FXCollections.observableArrayList(lignes));
    }

    public static class RelanceLigne {

        private final String entreprise;
        private final String contact;
        private final String date;
        private final String canal;
        private final String commentaire;

        public RelanceLigne(
                String entreprise,
                String contact,
                String date,
                String canal,
                String commentaire
        ) {
            this.entreprise = entreprise;
            this.contact = contact;
            this.date = date;
            this.canal = canal;
            this.commentaire = commentaire;
        }

        public String getEntreprise() {
            return entreprise;
        }

        public String getContact() {
            return contact;
        }

        public String getDate() {
            return date;
        }

        public String getCanal() {
            return canal;
        }

        public String getCommentaire() {
            return commentaire;
        }
    }
}
