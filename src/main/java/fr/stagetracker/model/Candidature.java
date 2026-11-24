package fr.stagetracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Candidature {
    private int identifiant;
    private LocalDate dateEnvoi;
    private StatutCandidature statut;
    private final String poste;
    private final Entreprise entreprise;
    private Contact contact;
    private final List<Relance> relances = new ArrayList<>();

    public Candidature (String poste, Entreprise entreprise){
        if(entreprise==null){
            throw new IllegalArgumentException("Entreprise obligatoire");
        }else if(poste==null || poste.isBlank()){
            throw new IllegalArgumentException("Le poste est obligatoire");
        }
        this.poste = poste;
        this.entreprise = entreprise;
        this.identifiant = IdentifiantFactory.genererIdentifiant();
        this.dateEnvoi = null;
        this.statut = StatutCandidature.BROUILLON;
        this.contact =null;
    }
    public Candidature(
            int identifiant,
            String poste,
            Entreprise entreprise,
            LocalDate dateEnvoi,
            StatutCandidature statut,
            Contact contact
    ) {
        this(poste, entreprise);
        this.identifiant = identifiant;
        this.dateEnvoi = dateEnvoi;
        this.statut = statut;
        this.contact = contact;
    }
    public int getIdentifiant(){
        return identifiant;
    }
    public LocalDate getDateEnvoi(){
        return dateEnvoi;
    }
    public StatutCandidature getStatut(){
        return statut;
    }
    public String getPoste(){
        return poste;
    }

    public Contact getContact(){
        return contact;
    }

    public Entreprise getEntreprise(){
        return entreprise;
    }

    void setContact(Contact k){
        this.contact = k;
    }

    public List<Relance> getRelances(){
        return Collections.unmodifiableList(relances);
    }

    public String toString(){
        return "#"+identifiant + " " + poste + " chez " + entreprise + " " + "[" + statut + "]";
    }

    public boolean nePlusChanger() {
        return statut==StatutCandidature.ACCEPTEE|| statut==StatutCandidature.REFUSEE;

    }

    public boolean changerStatut(StatutCandidature nouveauStatut){
        if (nouveauStatut==null || nePlusChanger() || nouveauStatut==StatutCandidature.ENVOYEE){
            return false;
        }else{
            statut=nouveauStatut;
            return true;
        }
    }

    public Relance creerRelance(LocalDate dateRelance, CanalRelance canal, String commentaire){
        if(statut==StatutCandidature.BROUILLON){
            throw new IllegalStateException("Un brouillon ne peut pas etre relancé");
        }
            Relance relance = new Relance(canal, dateRelance, commentaire);
            relances.add(relance);
            return relance;
        }

    public void ajouterRelanceExistante(Relance relance) {
        if (relance == null) {
            throw new IllegalArgumentException("Relance obligatoire");
        }
        relances.add(relance);
    }

    public LocalDate obtenirDateDerniereAction(){
        if (relances.isEmpty()){
            return dateEnvoi;
        }else{
            return relances.get(relances.size()-1).getDateRelance();
        }
    }
    public boolean envoyer(LocalDate dateEnvoi) {
        if (statut==StatutCandidature.BROUILLON && dateEnvoi != null) {
            this.dateEnvoi=dateEnvoi;
            statut=StatutCandidature.ENVOYEE;
            return true;
        }else{
            return false;
        }
    }
}

