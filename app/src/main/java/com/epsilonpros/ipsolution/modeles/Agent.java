package com.epsilonpros.ipsolution.modeles;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Amunaso Tyty on 26/10/2018.
 */

public class Agent extends ModelAbstract<Agent> {

    //clefs
    public static String NUMERO_MAT = "NumeroMat",NOM = "Nom", POSTNOM = "Postnom", PRENOM = "Prenom", SEX = "Sex",
                         TACHE = "Tache", IMAGE = "image", NUMERO_CARD = "NumeroCard", FINGER_DATA = "fingerData";

    private String numeroMat, nom , postnom , prenom , sex , tache , image , numeroCard , fingerData ;
    private int id;

    public Agent() {
    }

    @Override
    public Agent fromCursor(Cursor cursor) {
        try {
            Agent agent = new Agent();

            agent.setNumeroMat(cursor.getString(cursor.getColumnIndex(NUMERO_MAT)));
            agent.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
            agent.setNom(cursor.getString(cursor.getColumnIndex(NOM)));
            agent.setPostnom(cursor.getString(cursor.getColumnIndex(POSTNOM)));
            agent.setPrenom(cursor.getString(cursor.getColumnIndex(PRENOM)));
            agent.setSex(cursor.getString(cursor.getColumnIndex(SEX)));
            agent.setTache(cursor.getString(cursor.getColumnIndex(TACHE)));
            agent.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
            agent.setNumeroCard(cursor.getString(cursor.getColumnIndex(NUMERO_CARD)));
            agent.setFingerData(cursor.getString(cursor.getColumnIndex(FINGER_DATA)));

            return agent;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Agent fromJSON(JSONObject jsonObject){
        try {
            Agent agent = new Agent();

            agent.setNumeroMat(jsonObject.getString(NUMERO_MAT));
            agent.setNom(jsonObject.getString(NOM));
            agent.setPostnom(jsonObject.getString(POSTNOM));
            agent.setPrenom(jsonObject.getString(PRENOM));
            agent.setSex(jsonObject.getString(SEX));
            agent.setTache(jsonObject.getString(TACHE));
            agent.setImage(jsonObject.getString(IMAGE));
            agent.setNumeroCard(jsonObject.getString(NUMERO_CARD));
            agent.setFingerData(jsonObject.getString(FINGER_DATA));

            return agent;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroMat() {
        return numeroMat;
    }

    public void setNumeroMat(String numeroMat) {
        this.numeroMat = numeroMat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPostnom() {
        return postnom;
    }

    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTache() {
        return tache;
    }

    public void setTache(String tache) {
        this.tache = tache;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumeroCard() {
        return numeroCard;
    }

    public void setNumeroCard(String numeroCard) {
        this.numeroCard = numeroCard;
    }

    public String getFingerData() {
        return fingerData;
    }

    public void setFingerData(String fingerData) {
        this.fingerData = fingerData;
    }

    public String getAbrev(){
        return (""+(getPrenom().charAt(0))+(getPostnom().charAt(0))).toUpperCase();
    }

    @Override
    public ArrayList<String[]> getKeysType() {
        ArrayList<String[]> keysType = new ArrayList<>();

        keysType.add(STRING_TO_TAB(_ID, TYPE_INTEGER+CLEF_PRIMAIRE+AUTOINCREMENT));
        keysType.add(STRING_TO_TAB(NUMERO_MAT, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(NOM, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(POSTNOM, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(PRENOM, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(SEX, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(TACHE, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(IMAGE, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(NUMERO_CARD, TYPE_TEXT));
        keysType.add(STRING_TO_TAB(FINGER_DATA, TYPE_TEXT));

        return keysType;
    }

    @Override
    public String TABLE_NAME() {
        return Agent.class.getSimpleName();
    }

    @Override
    public String BDD_NAME() {
        return Agent.class.getSimpleName();
    }

    @Override
    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(NUMERO_MAT, getNumeroMat() );
        contentValues.put(NOM, getNom() );
        contentValues.put(POSTNOM, getPostnom() );
        contentValues.put(PRENOM, getPrenom() );
        contentValues.put(SEX, getSex() );
        contentValues.put(TACHE, getTache() );
        contentValues.put(IMAGE, getImage() );
        contentValues.put(NUMERO_CARD, getNumeroCard() );
        contentValues.put(FINGER_DATA, getFingerData() );

        return contentValues;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{_ID,NUMERO_MAT,NOM, POSTNOM,PRENOM, PRENOM, SEX, TACHE, IMAGE, NUMERO_CARD, FINGER_DATA};
    }
}
