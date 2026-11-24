package fr.stagetracker.model;

public class Contact extends Personne {
    private String postMetier;
    private String telephone;
    private Entreprise entreprise;
    private int id = -1;

    public Contact (String nom, String prenom, String email){
        super(nom, prenom, email);
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPostMetier(){
        return postMetier;
    }
    public String getTelephone(){
        return telephone;
    }
    public void setTelephone(String tel) {
        this.telephone = tel;
    }
    public void setPostMetier(String p){
        this.postMetier = p;
    }
    public void suivreCandidature(Candidature candidature){
        if (candidature==null){
            throw new IllegalArgumentException ("Il faut une candidature");
        }
        candidature.setContact(this);
    }
    void setEntreprise(Entreprise e){
        this.entreprise = e;
    }
    public Entreprise getEntreprise(){
        return entreprise;
    }
    @Override
    public String afficherRole(){
        if(postMetier==null){
            return ("Contact");
        }else {
            return ("Contact - " + postMetier);
        }
    }
}

