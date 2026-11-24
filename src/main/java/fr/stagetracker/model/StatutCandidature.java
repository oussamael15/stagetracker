package fr.stagetracker.model;

public enum StatutCandidature {
    BROUILLON("Brouillon"),
    ENVOYEE("Envoyée"),
    ENTRETIEN("Entretien"),
    ACCEPTEE ("Acceptée"),
    REFUSEE("Refusée");

    private final String libelle;

    private StatutCandidature(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}


