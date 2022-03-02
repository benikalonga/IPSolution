package com.epsilonpros.ipsolution.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.epsilonpros.ipsolution.modeles.ModelAbstract;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ravi on 15/03/18.
 */

public class BddManager<MODEL extends ModelAbstract> extends SQLiteOpenHelper implements Serializable {

    private MODEL model;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "apptendance_db";

    private BddListner listner;

    public BddManager(Context context) {
        this(context,null);
    }
    public BddManager(Context context, MODEL model) {
        super(context, DATABASE_NAME+model.TABLE_NAME(), null, DATABASE_VERSION);
        this.model = model;
    }

    public void setListner(BddListner listner) {
        this.listner = listner;
    }

    public void setModel(MODEL model) {
        this.model = model;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            // create notes table
            db.execSQL(model.CREATE_TABLE());
            if (listner!=null){
                listner.onCreated(model.TABLE_NAME());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + model.TABLE_NAME());

        // Create tables again
        onCreate(db);

        if (listner!=null){
            listner.onUpdateTable(model.TABLE_NAME());
        }
    }

    public synchronized long insertModel(MODEL model) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = model.getContentValues();

        long id = db.insert(model.TABLE_NAME(), null, values);

        // close db connection
        db.close();
        if (listner!=null){
            listner.onInsert(model.TABLE_NAME(),id,values);
        }

            // return newly inserted row id
        return id;
    }

    public MODEL getModel(String COLUMN, String value) {
        try {
            // get readable database as we are not inserting anything
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(model.TABLE_NAME(),model.getColumnNames(), COLUMN +"=?",new String[]{value},null,null,null);

            if (cursor != null)
                cursor.moveToFirst();

            // prepare note object
            MODEL modelCursor = (MODEL) model.fromCursor(cursor);

            // close the db connection
            cursor.close();

            return modelCursor;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public MODEL getModel(int id){
        return getModel(ModelAbstract._ID, String.valueOf(id));
    }

    public ArrayList<MODEL> getAllmodels() {

        ArrayList<MODEL> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + model.TABLE_NAME() + " ORDER BY " +
                ModelAbstract._ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MODEL model1 = (MODEL) model.fromCursor(cursor);
                notes.add(model1);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public ArrayList<MODEL> getAllmodels(String COLUMN, String VALUE) {

        ArrayList<MODEL> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + model.TABLE_NAME() +" WHERE "+COLUMN+" = '"+VALUE+ "' ORDER BY " +
                ModelAbstract._ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MODEL model1 = (MODEL) model.fromCursor(cursor);
                notes.add(model1);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }
    public ArrayList<MODEL> getAllmodels(String req) {

        ArrayList<MODEL> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + model.TABLE_NAME() +req;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MODEL model1 = (MODEL) model.fromCursor(cursor);
                notes.add(model1);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + model.TABLE_NAME();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int update(MODEL model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = model.getContentValues();

        // updating row
        int id = db.update(model.TABLE_NAME(), values, ModelAbstract._ID + " = ?",
                new String[]{String.valueOf(model.getId())});
        if (listner!=null){
            listner.onUpdateLine(model.TABLE_NAME(),id,values);
        }
        return id;
    }

    public void delete(MODEL model) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = db.delete(model.TABLE_NAME(), ModelAbstract._ID + " = ?",
                new String[]{String.valueOf(model.getId())});
        db.close();
        if (listner!=null){
            listner.onDelete(model.TABLE_NAME(),(long)id);
        }
    }
    public void deleteAll(){
        BddListner listner = this.listner;
        setListner(null);

        for (MODEL model : getAllmodels()){
            delete(model);
        }
        setListner(listner);
        if (listner!=null){
            listner.onDeleteAll(model.TABLE_NAME());
        }

    }
}
