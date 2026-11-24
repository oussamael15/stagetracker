package fr.stagetracker.model;

import java.time.LocalDate;


public class Relance {
    private final CanalRelance canal;
    private final LocalDate dateRelance;
    private final String commentaire;

    public Relance(CanalRelance canal, LocalDate dateRelance, String commentaire){
        if(canal == null){
            throw new IllegalArgumentException ("Canal obligatoire");
        }else if(dateRelance == null){
            throw new IllegalArgumentException ("Date obligatoire");
        }else if(commentaire == null || commentaire.isBlank()){
            throw new IllegalArgumentException ("Commentaire obligatoire");
        }
        this.canal= canal;
        this.dateRelance= dateRelance;
        this.commentaire= commentaire;
    }
    public CanalRelance getCanal(){
        return canal;
    }
    public LocalDate getDateRelance(){
        return dateRelance;
    }
    public String getCommentaire(){
        return commentaire;
    }
    public String toString(){
        return dateRelance + " via " + canal + " : " + commentaire;
    }
}
