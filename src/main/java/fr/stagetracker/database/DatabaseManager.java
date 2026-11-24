package fr.stagetracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import fr.stagetracker.model.Entreprise;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import fr.stagetracker.model.Contact;
import fr.stagetracker.model.Candidature;
import fr.stagetracker.model.StatutCandidature;
import java.time.LocalDate;
import fr.stagetracker.model.Relance;
import fr.stagetracker.model.CanalRelance;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:stageTracker.db");

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        initialiserDatabase();
    }

    private void initialiserDatabase() throws SQLException {

        try (Statement statement = connection.createStatement()) {

            statement.execute("""
                CREATE TABLE IF NOT EXISTS entreprises (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    ville TEXT,
                    secteur TEXT
                )
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS contacts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    prenom TEXT,
                    email TEXT NOT NULL,
                    poste_metier TEXT,
                    telephone TEXT,
                    entreprise_id INTEGER NOT NULL,
                    FOREIGN KEY (entreprise_id) REFERENCES entreprises(id)
                )
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS candidatures (
                    id INTEGER PRIMARY KEY,
                    poste TEXT NOT NULL,
                    statut TEXT NOT NULL,
                    date_envoi TEXT,
                    entreprise_id INTEGER NOT NULL,
                    contact_id INTEGER,
                    FOREIGN KEY (entreprise_id) REFERENCES entreprises(id),
                    FOREIGN KEY (contact_id) REFERENCES contacts(id)
                )
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS relances (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date_relance TEXT NOT NULL,
                    canal TEXT NOT NULL,
                    commentaire TEXT NOT NULL,
                    candidature_id INTEGER NOT NULL,
                    FOREIGN KEY (candidature_id) REFERENCES candidatures(id)
                )
            """);
        }
    }
    public void enregistrerEntreprise(Entreprise entreprise) throws SQLException {

        String sql = """
        INSERT INTO entreprises (nom, ville, secteur)
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entreprise.getNomEntreprise());
            statement.setString(2, entreprise.getVille());
            statement.setString(3, entreprise.getSecteur());

            statement.executeUpdate();
        }

        // Récupération de l'identifiant généré par SQLite
        try (Statement statement = connection.createStatement();
             ResultSet resultat = statement.executeQuery("SELECT last_insert_rowid()")) {

            if (resultat.next()) {
                entreprise.setId(resultat.getInt(1));
            }
        }
    }
    public List<Entreprise> chargerEntreprises() throws SQLException {

        List<Entreprise> entreprises = new ArrayList<>();

        String sql = "SELECT id, nom, ville, secteur FROM entreprises";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultat = statement.executeQuery()) {

            while (resultat.next()) {

                Entreprise entreprise = new Entreprise(
                        resultat.getString("nom"),
                        resultat.getString("ville"),
                        resultat.getString("secteur")
                );

                entreprise.setId(resultat.getInt("id"));

                entreprises.add(entreprise);
            }
        }

        return entreprises;
    }
    public void enregistrerContact(Contact contact) throws SQLException {

        if (contact.getEntreprise() == null || contact.getEntreprise().getId() == -1) {
            throw new IllegalArgumentException("L'entreprise doit être enregistrée en base.");
        }

        String sql = """
        INSERT INTO contacts (nom, prenom, email, poste_metier, telephone, entreprise_id)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, contact.getNom());
            statement.setString(2, contact.getPrenom());
            statement.setString(3, contact.getEmail());
            statement.setString(4, contact.getPostMetier());
            statement.setString(5, contact.getTelephone());
            statement.setInt(6, contact.getEntreprise().getId());

            statement.executeUpdate();
        }
        try (Statement statement = connection.createStatement();
             ResultSet resultat = statement.executeQuery("SELECT last_insert_rowid()")) {

            if (resultat.next()) {
                contact.setId(resultat.getInt(1));
            }
        }
    }
    public List<Contact> chargerContacts(List<Entreprise> entreprises) throws SQLException {

        List<Contact> contacts = new ArrayList<>();

        String sql = "SELECT * FROM contacts";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultat = statement.executeQuery()) {

            while (resultat.next()) {

                int entrepriseId = resultat.getInt("entreprise_id");

                Entreprise entreprise = entreprises.stream()
                        .filter(e -> e.getId() == entrepriseId)
                        .findFirst()
                        .orElse(null);

                if (entreprise != null) {
                    Contact contact = new Contact(
                            resultat.getString("nom"),
                            resultat.getString("prenom"),
                            resultat.getString("email")
                    );

                    contact.setId(resultat.getInt("id"));

                    contact.setPostMetier(resultat.getString("poste_metier"));
                    contact.setTelephone(resultat.getString("telephone"));

                    entreprise.ajouterContact(contact);
                    contacts.add(contact);
                }
            }
        }

        return contacts;
    }

    public void enregistrerCandidature(Candidature candidature) throws SQLException {

        String sql = """
        INSERT INTO candidatures (id, poste, statut, date_envoi, entreprise_id, contact_id)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, candidature.getIdentifiant());
            statement.setString(2, candidature.getPoste());
            statement.setString(3, candidature.getStatut().name());

            if (candidature.getDateEnvoi() == null) {
                statement.setNull(4, java.sql.Types.VARCHAR);
            } else {
                statement.setString(4, candidature.getDateEnvoi().toString());
            }

            statement.setInt(5, candidature.getEntreprise().getId());
            if (candidature.getContact() == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, candidature.getContact().getId());
            }

            statement.executeUpdate();
        }
    }

    public void modifierStatutCandidature(Candidature candidature) throws SQLException {

        String sql = "UPDATE candidatures SET statut = ?, date_envoi = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, candidature.getStatut().name());

            if (candidature.getDateEnvoi() == null) {
                statement.setNull(2, java.sql.Types.VARCHAR);
            } else {
                statement.setString(2, candidature.getDateEnvoi().toString());
            }

            statement.setInt(3, candidature.getIdentifiant());

            statement.executeUpdate();
        }
    }

    public List<Candidature> chargerCandidatures(
            List<Entreprise> entreprises,
            List<Contact> contacts
    ) throws SQLException {

        List<Candidature> candidatures = new ArrayList<>();

        String sql = "SELECT * FROM candidatures";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultat = statement.executeQuery()) {

            while (resultat.next()) {

                int entrepriseId = resultat.getInt("entreprise_id");
                int contactId = resultat.getInt("contact_id");
                boolean contactEstNull = resultat.wasNull();

                Entreprise entreprise = entreprises.stream()
                        .filter(e -> e.getId() == entrepriseId)
                        .findFirst()
                        .orElse(null);

                Contact contact = contactEstNull ? null :
                        contacts.stream()
                                .filter(c -> c.getId() == contactId)
                                .findFirst()
                                .orElse(null);

                if (entreprise != null) {

                    String dateTexte = resultat.getString("date_envoi");

                    LocalDate date = dateTexte == null
                            ? null
                            : LocalDate.parse(dateTexte);

                    Candidature candidature = new Candidature(
                            resultat.getInt("id"),
                            resultat.getString("poste"),
                            entreprise,
                            date,
                            StatutCandidature.valueOf(resultat.getString("statut")),
                            contact
                    );

                    candidatures.add(candidature);
                }
            }
        }

        return candidatures;
    }

    public void enregistrerRelance(Relance relance, Candidature candidature) throws SQLException {

        String sql = """
        INSERT INTO relances (date_relance, canal, commentaire, candidature_id)
        VALUES (?, ?, ?, ?)
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, relance.getDateRelance().toString());
            statement.setString(2, relance.getCanal().name());
            statement.setString(3, relance.getCommentaire());
            statement.setInt(4, candidature.getIdentifiant());

            statement.executeUpdate();
        }
    }

    public void chargerRelances(List<Candidature> candidatures) throws SQLException {

        // Tri par date : la dernière relance de la liste doit être la plus récente
        String sql = "SELECT * FROM relances ORDER BY date_relance, id";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultat = statement.executeQuery()) {

            while (resultat.next()) {

                int candidatureId = resultat.getInt("candidature_id");

                Candidature candidature = candidatures.stream()
                        .filter(c -> c.getIdentifiant() == candidatureId)
                        .findFirst()
                        .orElse(null);

                if (candidature != null) {

                    Relance relance = new Relance(
                            CanalRelance.valueOf(resultat.getString("canal")),
                            LocalDate.parse(resultat.getString("date_relance")),
                            resultat.getString("commentaire")
                    );

                    candidature.ajouterRelanceExistante(relance);
                }
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void fermerConnexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}