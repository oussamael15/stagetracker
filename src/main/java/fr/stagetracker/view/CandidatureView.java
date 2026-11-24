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
import javafx.scene.layout.VBox;
import fr.stagetracker.model.Candidature;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import java.time.format.DateTimeFormatter;
import fr.stagetracker.model.StatutCandidature;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import fr.stagetracker.model.Contact;
import javafx.scene.control.ButtonType;
import java.sql.SQLException;

public class CandidatureView extends VBox {

    private final StageTracker tracker;
    private final DatabaseManager database;

    public CandidatureView(StageTracker tracker, DatabaseManager database) {
        this.tracker = tracker;
        this.database = database;

        setPadding(new Insets(70));
        setSpacing(25);
        setFillWidth(true);

        Label titre = new Label("Mes candidatures");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label sousTitre = new Label(
                "Consultez et gérez l'ensemble de vos candidatures."
        );
        sousTitre.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");

        Button nouvelleCandidature =
                new Button("+ Nouvelle candidature");
        Button modifierStatut = new Button("Modifier le statut");
        Button envoyerCandidature = new Button("Envoyer la candidature");
        Button associerContact = new Button("Associer un contact");
        Button voirDetails = new Button("Voir les détails");

        voirDetails.setStyle(
                "-fx-background-color: #6E7681;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );

        associerContact.setStyle(
                "-fx-background-color: #8957E5;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );

        envoyerCandidature.setStyle(
                "-fx-background-color: #238636;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );

        nouvelleCandidature.setStyle(
                "-fx-background-color: #238636;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );
        modifierStatut.setStyle(
                "-fx-background-color: #1F6FEB;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 11 16;" +
                        "-fx-font-weight: bold;"
        );

        VBox titres = new VBox(7, titre, sousTitre);

        HBox boutons = new HBox(
                10,
                nouvelleCandidature,
                modifierStatut,
                envoyerCandidature,
                associerContact,
                voirDetails
        );

        HBox entete = new HBox(titres, boutons);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setSpacing(20);
        HBox.setHgrow(titres, Priority.ALWAYS);

        Label titreTableau = new Label("Toutes les candidatures");
        titreTableau.setStyle(
                "-fx-font-size: 19px; -fx-font-weight: bold;"
        );

        TableView<Candidature> tableau = new TableView<>();

        TableColumn<Candidature, String> entreprise =
                new TableColumn<>("Entreprise");
        entreprise.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getEntreprise().getNomEntreprise()
                )
        );

        TableColumn<Candidature, String> poste =
                new TableColumn<>("Poste");
        poste.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getPoste()
                )
        );

        TableColumn<Candidature, String> statut =
                new TableColumn<>("Statut");
        statut.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getStatut().toString()
                )
        );

        TableColumn<Candidature, String> date =
                new TableColumn<>("Date de candidature");
        date.setCellValueFactory(cell -> {
            var dateEnvoi = cell.getValue().getDateEnvoi();

            String valeur = dateEnvoi == null
                    ? "—"
                    : dateEnvoi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            return new ReadOnlyStringWrapper(valeur);
        });
        TableColumn<Candidature, String> contact =
                new TableColumn<>("Contact");

        contact.setCellValueFactory(cell -> {
            Contact c = cell.getValue().getContact();

            String nom = c == null
                    ? "—"
                    : c.afficherNomComplet();

            return new ReadOnlyStringWrapper(nom);
        });

        tableau.getColumns().addAll(
                entreprise, poste, contact, statut, date
        );

        tableau.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        tableau.setItems(
                FXCollections.observableArrayList(tracker.getCandidatures())
        );

        nouvelleCandidature.setOnAction(event -> {
            NouvelleCandidatureDialog dialog =
                    new NouvelleCandidatureDialog(tracker, database);

            dialog.showAndWait();

            tableau.setItems(
                    FXCollections.observableArrayList(tracker.getCandidatures())
            );
        });
        modifierStatut.setOnAction(event -> {

            Candidature selection =
                    tableau.getSelectionModel().getSelectedItem();

            if (selection == null) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Veuillez sélectionner une candidature."
                ).showAndWait();
                return;
            }

            var statutsDisponibles = FXCollections.observableArrayList(
                    java.util.Arrays.stream(StatutCandidature.values())
                            .filter(s -> s != StatutCandidature.ENVOYEE)
                            .toList()
            );

            ComboBox<StatutCandidature> choixStatut =
                    new ComboBox<>(statutsDisponibles);

            choixStatut.setValue(selection.getStatut());

            Alert dialogue = new Alert(Alert.AlertType.CONFIRMATION);
            dialogue.setTitle("Modifier le statut");
            dialogue.setHeaderText("Choisissez le nouveau statut");
            dialogue.getDialogPane().setContent(choixStatut);

            dialogue.showAndWait().ifPresent(resultat -> {

                if (resultat == javafx.scene.control.ButtonType.OK
                        && choixStatut.getValue() != null) {

                    if (selection.nePlusChanger()) {
                        Alert alerte = new Alert(Alert.AlertType.WARNING);
                        alerte.setTitle("Modification impossible");
                        alerte.setHeaderText("Statut définitif");
                        alerte.setContentText(
                                "Une candidature acceptée ou refusée ne peut plus être modifiée."
                        );
                        alerte.showAndWait();

                        choixStatut.setValue(selection.getStatut());
                        return;
                    }

                    boolean modifie;

                    if (choixStatut.getValue() == StatutCandidature.ENVOYEE) {
                        modifie = selection.envoyer(LocalDate.now());
                    } else {
                        modifie = selection.changerStatut(choixStatut.getValue());
                    }

                    if (!modifie) {
                        new Alert(
                                Alert.AlertType.WARNING,
                                "Ce changement de statut n'est pas autorisé."
                        ).showAndWait();
                        return;
                    }
                    try {
                        database.modifierStatutCandidature(selection);
                    } catch (SQLException e) {
                        new Alert(
                                Alert.AlertType.ERROR,
                                "Erreur lors de la sauvegarde : " + e.getMessage()
                        ).showAndWait();
                    }

                    tableau.refresh();
                }
            });
        });

        envoyerCandidature.setOnAction(event -> {

            Candidature selection =
                    tableau.getSelectionModel().getSelectedItem();

            if (selection == null) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Veuillez sélectionner une candidature."
                ).showAndWait();
                return;
            }

            if (selection.getStatut() != StatutCandidature.BROUILLON) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Seuls les brouillons peuvent être envoyés."
                ).showAndWait();
                return;
            }

            DatePicker datePicker = new DatePicker(LocalDate.now());

            Alert dialogue = new Alert(Alert.AlertType.CONFIRMATION);
            dialogue.setTitle("Envoyer la candidature");
            dialogue.setHeaderText("Choisissez la date d'envoi");
            dialogue.getDialogPane().setContent(datePicker);

            dialogue.showAndWait().ifPresent(resultat -> {

                if (resultat == javafx.scene.control.ButtonType.OK) {
                    if (datePicker.getValue() == null) {
                        new Alert(
                                Alert.AlertType.WARNING,
                                "Veuillez choisir une date."
                        ).showAndWait();
                        return;
                    }

                    boolean envoyee = selection.envoyer(datePicker.getValue());

                    if (envoyee) {
                        try {
                            database.modifierStatutCandidature(selection);
                            new Alert(
                                    Alert.AlertType.INFORMATION,
                                    "Candidature envoyée avec succès."
                            ).showAndWait();
                        } catch (SQLException e) {
                            new Alert(
                                    Alert.AlertType.ERROR,
                                    "Erreur lors de la sauvegarde : " + e.getMessage()
                            ).showAndWait();
                        }
                    }

                    tableau.refresh();
                }
            });
        });

        associerContact.setOnAction(event -> {

            Candidature selection =
                    tableau.getSelectionModel().getSelectedItem();

            if (selection == null) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Veuillez sélectionner une candidature."
                ).showAndWait();
                return;
            }

            var contactsDisponibles = FXCollections.observableArrayList(
                    tracker.getContacts().stream()
                            .filter(c -> c.getEntreprise().equals(
                                    selection.getEntreprise()
                            ))
                            .toList()
            );

            if (contactsDisponibles.isEmpty()) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Aucun contact n'est enregistré pour cette entreprise."
                ).showAndWait();
                return;
            }

            ComboBox<Contact> choixContact =
                    new ComboBox<>(contactsDisponibles);

            choixContact.setPromptText("Choisir un contact");

            if (selection.getContact() != null) {
                choixContact.setValue(selection.getContact());
            }

            Alert dialogue = new Alert(Alert.AlertType.CONFIRMATION);
            dialogue.setTitle("Associer un contact");
            dialogue.setHeaderText("Choisissez le contact à associer");
            dialogue.getDialogPane().setContent(choixContact);

            dialogue.showAndWait().ifPresent(resultat -> {

                if (resultat == ButtonType.OK) {

                    Contact contactChoisi = choixContact.getValue();

                    if (contactChoisi == null) {
                        new Alert(
                                Alert.AlertType.WARNING,
                                "Veuillez choisir un contact."
                        ).showAndWait();
                        return;
                    }

                    contactChoisi.suivreCandidature(selection);

                    tableau.refresh();
                }
            });
        });

        voirDetails.setOnAction(event -> {

            Candidature selection =
                    tableau.getSelectionModel().getSelectedItem();

            if (selection == null) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Veuillez sélectionner une candidature."
                ).showAndWait();
                return;
            }

            StringBuilder details = new StringBuilder();

            details.append("Identifiant : ")
                    .append(selection.getIdentifiant()).append("\n");

            details.append("Entreprise : ")
                    .append(selection.getEntreprise().getNomEntreprise()).append("\n");

            details.append("Poste : ")
                    .append(selection.getPoste()).append("\n");

            details.append("Statut : ")
                    .append(selection.getStatut()).append("\n");

            details.append("Date d'envoi : ")
                    .append(selection.getDateEnvoi() == null
                            ? "Non envoyée"
                            : selection.getDateEnvoi().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ))
                    .append("\n");

            details.append("Contact : ")
                    .append(selection.getContact() == null
                            ? "Aucun contact associé"
                            : selection.getContact().afficherNomComplet())
                    .append("\n\n");

            details.append("Historique des relances :\n");

            if (selection.getRelances().isEmpty()) {
                details.append("Aucune relance enregistrée.");
            } else {
                for (var relance : selection.getRelances()) {

                    details.append("- ")
                            .append(relance.getDateRelance().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            ))
                            .append(" | ")
                            .append(relance.getCanal().getLibelle())
                            .append(" | ")
                            .append(relance.getCommentaire())
                            .append("\n");
                }
            }

            Alert dialogue = new Alert(Alert.AlertType.INFORMATION);
            dialogue.setTitle("Détails de la candidature");
            dialogue.setHeaderText("Candidature #" + selection.getIdentifiant());
            dialogue.setContentText(details.toString());
            dialogue.getDialogPane().setPrefWidth(500);

            dialogue.showAndWait();
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
