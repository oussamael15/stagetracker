package fr.stagetracker.model;

public class IdentifiantFactory {
    private static int nouvelIdentifiant=0;

    private IdentifiantFactory() {
    }

    public static int genererIdentifiant(){
        ++nouvelIdentifiant;
        return nouvelIdentifiant;
    }
}


