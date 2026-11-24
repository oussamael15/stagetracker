package fr.stagetracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StageTracker {
    private static final int DELAI_RELANCE=7;
    private final Etudiant etudiant;
    private final List<Candidature> candidatures = new ArrayList<>();
    private final List<Entreprise> entreprises = new ArrayList<>();
    private final List<Contact> contacts = new ArrayList<>();


    public int getDELAI_RELANCE(){
        return DELAI_RELANCE;
    }

    public List<Candidature> getCandidatures(){
        return Collections.unmodifiableList(candidatures);
    }
    public List<Entreprise> getEntreprises(){
        return Collections.unmodifiableList(entreprises);
    }
    public List<Contact> getContacts(){
        return Collections.unmodifiableList(contacts);
    }

    public Etudiant getEtudiant(){
        return etudiant;
    }
    public StageTracker(Etudiant etudiant){
        if (etudiant==null){
            throw new IllegalArgumentException("Etudiant est obligatoire");
        }
        this.etudiant=etudiant;
        etudiant.setStageTracker(this);
    }

    public List<Candidature> signalerCandidature(LocalDate dateReference) {
        if (dateReference == null) {
            throw new IllegalArgumentException("La date référence est obligatoire");
        }
        List<Candidature> candidaturesRelancer = new ArrayList<>();
        for (Candidature candidature : candidatures) {
            if (candidature.getStatut() == StatutCandidature.ENVOYEE) {
                LocalDate dateLimite = candidature.obtenirDateDerniereAction().plusDays(DELAI_RELANCE);
                if (!dateReference.isBefore(dateLimite)) {
                    candidaturesRelancer.add(candidature);
                }
            }
        }
        return candidaturesRelancer;
    }
    public void ajouterCandidature(Candidature c){
        if (c==null){
            throw new IllegalArgumentException("Candidature obligatoire");
        }else if (candidatures.contains(c)){
            throw new IllegalStateException("Cette candidature existe déjà dans la liste");
        }
            candidatures.add(c);
        }
    void ajouterEntreprise(Entreprise e){
        if (e==null){
            throw new IllegalArgumentException("Entreprise obligatoire");
        }else if (entreprises.contains(e)){
            throw new IllegalStateException("Cette entreprise existe déjà dans la liste");
        }
        entreprises.add(e);
    }
    void ajouterContact(Contact k) {
        if (k == null) {
            throw new IllegalArgumentException("Contact obligatoire");
        } else if (contacts.contains(k)) {
            throw new IllegalStateException("Ce contact existe déjà dans la liste");
        }
        contacts.add(k);
    }
}