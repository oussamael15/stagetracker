package fr.stagetracker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Entreprise {
    private int id = -1;
    private final String nomEntreprise;
    private final String ville;
    private final String secteur;

    private final List<Contact> contacts= new ArrayList<>();

    public Entreprise(String nomEntreprise, String ville, String secteur){
        if (nomEntreprise==null || nomEntreprise.isBlank()){
            throw new IllegalArgumentException ("Nom d'entreprise obligatoire");
        }
        this.nomEntreprise= nomEntreprise;
        this.ville= ville;
        this.secteur= secteur;
    }

    public void ajouterContact(Contact c) {
        if (c==null){
            throw new IllegalArgumentException("Il faut un contact");
        }else if(!contacts.contains(c) && c.getEntreprise() != null && !c.getEntreprise().equals(this)) {
            throw new IllegalStateException("Ce contact travaille dans une autre entreprise");
        }else if (!contacts.contains(c)){
            contacts.add(c);
            c.setEntreprise(this);
        }
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNomEntreprise(){
        return nomEntreprise;
    }
    public String getVille(){
        return ville;
    }
    public String getSecteur(){
        return secteur;
    }

    public List<Contact> getContacts(){
        return Collections.unmodifiableList(contacts);
    }

    public String toString(){
        return nomEntreprise +" "+ "(" + ville + ")";
    }
}