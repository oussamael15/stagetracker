package fr.stagetracker.model;

import java.time.LocalDate;

public class Etudiant extends Personne {
    private String linkedin;
    private String telephone;
    private StageTracker tracker;


    public Etudiant (String nom, String prenom, String email){
        super(nom, prenom, email);
    }

    public StageTracker getTracker(){
        return tracker;
    }

    void setStageTracker(StageTracker tracker){
        this.tracker=tracker;
    }

    public String getLinkedin(){
        return linkedin;
    }
    public String getTelephone(){
        return telephone;
    }

    public void setLinkedin(String l){
        this.linkedin=l;
    }
    public void setTelephone(String t){
        this.telephone=t;
    }
    public String afficherRole(){
        return "Étudiant";
    }
    public void enregistrerEntreprise(Entreprise entreprise){
        if (tracker==null){
            throw new IllegalStateException("L'étudiant n'est rattaché à aucun tracker");
        }
        tracker.ajouterEntreprise(entreprise);
    }
    public void garderContact(Contact contact){
        if (tracker==null){
            throw new IllegalStateException("L'étudiant n'est rattaché à aucun tracker");
        }
        tracker.ajouterContact(contact);
    }
    public boolean enregistrerRelance(Candidature candidature, LocalDate date, CanalRelance canal, String commentaire){
        if (candidature==null){
            throw new IllegalArgumentException("Candidature obligatoire");
        }try{
            candidature.creerRelance(date, canal, commentaire);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }catch (IllegalStateException e) {
            return false;
        }
    }
    public void afficherStatut(Candidature candidature){
        if (candidature == null){
            throw new IllegalArgumentException ("Candidature obligatoire");
        }
        System.out.println("Candidature #" + candidature.getIdentifiant() +" : "+ candidature.getStatut());
    }
}
