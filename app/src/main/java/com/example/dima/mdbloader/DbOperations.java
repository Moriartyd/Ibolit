package com.example.dima.mdbloader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class DbOperations extends SQLiteOpenHelper {
    private Context myContext;
    public SQLiteDatabase myDatabase;

    private static String DB_PATH = null;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "lekarstvo.db3";
   // private static final String CREATE_QUERY = "create table " + ProductContract.ProductEntry.TABLE_NAME + "(" + ProductContract.ProductEntry.ID + " text," + ProductContract.ProductEntry.NAME + " text," + ProductContract.ProductEntry.PRICE + " integer);";

    DbOperations(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        this.myContext=ctx;
        this.DB_PATH = "/data/data/"+ctx.getPackageName()+"/"+"databases/";
        Log.e("PATH 1", DB_PATH);
        Log.d("Database Operations", "Database created!!!");
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist){

        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e){
                throw new Error("Error copying");
            }
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH+DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath,null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e){
        }
        if (checkDB!= null){
            checkDB.close();
        }
        return checkDB!=null ? true:false;
    }

    private void copyDataBase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH+DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length= myInput.read(buffer))>0){
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLiteException{
        String myPath = DB_PATH+DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
@Override
public synchronized void close(){
    if(myDatabase != null){
        myDatabase.close();
    }
    super.close();
}


    @Override
    public void onCreate(SQLiteDatabase db) {
    }



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME);
            if (newVersion>oldVersion){
                try {
                    copyDataBase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public Cursor query(String table,String[] columns, String selections, String[] selectionArgs, String groupBy, String having, String orderBy){
        return myDatabase.query(ProductContract.ProductEntry.TABLE_NAME, null, null, null, null, null, null, null);
    }


//Получение ИНФОРМАЦИИ!!!!!!
    public Cursor getInformations(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {ProductContract.ProductEntry.FORM,
                ProductContract.ProductEntry.NAME, ProductContract.ProductEntry.PRICE};
        cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projections, null, null, null, null, null);
        return cursor;
    }


    public Cursor  getSearch(String search) {
        DbOperations dbOperations = new DbOperations(myContext);
        SQLiteDatabase db = dbOperations.getReadableDatabase();
        String selectQuery =  "SELECT " +
                ProductContract.ProductEntry.FORM + "," +
                ProductContract.ProductEntry.NAME + "," +
                ProductContract.ProductEntry.PRICE + "," +
                " FROM " + ProductContract.ProductEntry.TABLE_NAME +
                " WHERE " + ProductContract.ProductEntry.ACTIVE_SUBST + " = " +search
                ;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;

    }



}
