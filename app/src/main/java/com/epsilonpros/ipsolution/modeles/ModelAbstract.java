package com.epsilonpros.ipsolution.modeles;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public abstract class ModelAbstract<T> {

    public static String TYPE_INTEGER = "INTEGER ";
    public static String TYPE_TEXT = "TEXT ";
    public static String TYPE_BOOLEAN = "BOOLEAN ";
    public static String CLEF_PRIMAIRE = "PRIMARY KEY ";
    public static String AUTOINCREMENT = "AUTOINCREMENT ";

    //COLUMN
    public static String _ID = "ID";

    public String CREATE_TABLE(){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE "+TABLE_NAME()+" ");
        stringBuilder.append("("+" ");

        ArrayList<String[]> keysValue = getKeysType();

        for (int i = 0; i<keysValue.size(); i++){
            String[] keyValue = keysValue.get(i);

            stringBuilder.append(keyValue[0]+" "+keyValue[1]);

            //break si on est au dernier
            if (i == keysValue.size()-1)break;
            stringBuilder.append(", ");
        }
        stringBuilder.append(")");

        return stringBuilder.toString();

    }
    public static String[] STRING_TO_TAB(String... strings){
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings){
            stringBuilder.append(s+ "_SEPARATOR_");
        }
        return stringBuilder.toString().split("_SEPARATOR_");
    }

    //methods abstraites

    public abstract T fromCursor(Cursor cursor);
    public abstract ArrayList<String[]> getKeysType();
    public abstract String TABLE_NAME();
    public abstract String BDD_NAME();
    public abstract ContentValues getContentValues();
    public abstract String[] getColumnNames();
    public abstract int getId();

}
