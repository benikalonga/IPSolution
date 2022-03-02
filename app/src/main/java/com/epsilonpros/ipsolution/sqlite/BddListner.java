package com.epsilonpros.ipsolution.sqlite;

import android.content.ContentValues;

public  interface BddListner {

    void onOpen();
    void onClose();
    void onInsert(String tableName, long id, ContentValues values);
    void onUpdateLine(String tableName, long id, ContentValues values);
    void onCreated(String tableName);

    void onDeleteAll(String tableName);

    void onDelete(String tableName, long id);
    void onUpdateTable(String tableName);

}
