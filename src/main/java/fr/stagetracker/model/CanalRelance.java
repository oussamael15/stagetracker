package fr.stagetracker.model;

public enum CanalRelance {
    EMAIL("Email"),
    TELEPHONE("téléphone"),
    LINKEDIN("LinkedIn");

    private final String libelle;

    private CanalRelance(String libelle){
        this.libelle = libelle;
    }
    public String getLibelle(){
        return libelle;
    }
}
