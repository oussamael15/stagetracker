package fr.stagetracker.view;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import java.util.function.Consumer;

public class Sidebar extends VBox {
    private Button boutonActif;
    private final Consumer<String> onNavigation;

    public Sidebar(Consumer<String> onNavigation) {
        this.onNavigation = onNavigation;
        Label titre = new Label("StageTracker");
        titre.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Button btnDashboard = new Button("Tableau de bord");
        btnDashboard.setMaxWidth(Double.MAX_VALUE);
        btnDashboard.setStyle(
                "-fx-background-color: #324449;" +
                        "-fx-text-fill: #DCE4E7;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 12 14;"
        );
        Button btnCandidatures = new Button("Candidatures");
        btnCandidatures.setMaxWidth(Double.MAX_VALUE);

        Button btnEntreprises = new Button("Entreprises");
        btnEntreprises.setMaxWidth(Double.MAX_VALUE);
        Button btnContacts = new Button("Contacts");
        btnContacts.setMaxWidth(Double.MAX_VALUE);
        Button btnRelances = new Button("Relances");
        btnRelances.setMaxWidth(Double.MAX_VALUE);
        setPrefWidth(200);
        setSpacing(12);
        setPadding(new Insets(22));
        setStyle("-fx-background-color: #1E2B33;");
        boutonActif = btnDashboard;
        configurerSurvol(btnCandidatures);
        configurerSurvol(btnEntreprises);
        configurerSurvol(btnContacts);
        configurerSurvol(btnRelances);
        btnDashboard.setOnAction(e -> {
            selectionnerBouton(btnDashboard);
            onNavigation.accept("dashboard");
        });

        btnCandidatures.setOnAction(e -> {
            selectionnerBouton(btnCandidatures);
            onNavigation.accept("candidatures");
        });

        btnEntreprises.setOnAction(e -> {
            selectionnerBouton(btnEntreprises);
            onNavigation.accept("entreprises");
        });

        btnContacts.setOnAction(e -> {
            selectionnerBouton(btnContacts);
            onNavigation.accept("contacts");
        });

        btnRelances.setOnAction(e -> {
            selectionnerBouton(btnRelances);
            onNavigation.accept("relances");
        });
        getChildren().addAll(titre, btnDashboard, btnCandidatures, btnEntreprises, btnContacts, btnRelances);
    }

    private void selectionnerBouton(Button nouveauBouton) {
        if (boutonActif != null) {
            boutonActif.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #DCE4E7;" +
                            "-fx-alignment: CENTER_LEFT;" +
                            "-fx-font-size: 15px;" +
                            "-fx-padding: 12 14;"
            );
        }
        boutonActif = nouveauBouton;
        nouveauBouton.setStyle(
                "-fx-background-color: #324449;" +
                        "-fx-text-fill: #DCE4E7;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 12 14;"
        );
    }

    private void configurerSurvol(Button button) {
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: #344650;" +
                            "-fx-text-fill: #DCE4E7;" +
                            "-fx-alignment: CENTER_LEFT;" +
                            "-fx-font-size: 15px;" +
                            "-fx-padding: 12 14;"
            );
        });

        button.setOnMouseExited(e -> {
            if (button != boutonActif) {
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #DCE4E7;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-font-size: 15px;" +
                                "-fx-padding: 12 14;"
                );
            }
        });
    }
}
