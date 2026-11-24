package fr.stagetracker.model;

public abstract class Personne {
    private final String nom;
    private final String prenom;
    private final String email;

    protected Personne(String nom, String prenom, String email){
        if (nom==null || nom.isBlank()){
            throw new IllegalArgumentException ("Le nom est obligatoire");
        }
        if (email==null || !email.contains("@")){
            throw new IllegalArgumentException ("Email invalide : " + email);
        }
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }
    public String afficherNomComplet(){
        return prenom + " " + nom.toUpperCase();
    }

    public abstract String afficherRole();

    public String getNom(){
        return nom;
    }
    public String getPrenom(){
        return prenom;
    }

    public String getEmail(){
        return email;
    }

    public String toString(){
        return afficherNomComplet() + " " + "(" + afficherRole() + ")";
    }
}
