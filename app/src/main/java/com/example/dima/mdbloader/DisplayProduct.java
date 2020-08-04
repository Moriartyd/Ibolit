package com.example.dima.mdbloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DisplayProduct extends Activity {
    private ListView listView;
    private EditText inputSearch; private DbOperations dbOperations; private  SQLiteDatabase db;private Cursor kursor;
    SimpleCursorAdapter cursorAdapter;
    String form,name;int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product);
       // BackGroundTask backGroundTask = new BackGroundTask(this);
       // backGroundTask.execute("get_info");
        listView = (ListView) findViewById(R.id.display_listview);
        inputSearch = (EditText) findViewById(R.id.txtsearch);

        dbOperations = new DbOperations(this);
        db = dbOperations.getReadableDatabase();

        try {
            dbOperations.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            dbOperations.openDataBase();
        }catch(SQLException sqle) {
            throw sqle;
        }

        dbOperations.onUpgrade(db, 1, 2);
        Cursor cursor = dbOperations.getInformations(db);
        while (cursor.moveToNext())
        {
            form = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.FORM));
            name = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.NAME));
            price = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.PRICE));
        }
        //Lekarstvo lekarstvo = new Lekarstvo(name,form,price);
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            dbOperations.openDataBase();
            kursor = dbOperations.myDatabase.rawQuery("select * from " + ProductContract.ProductEntry.TABLE_NAME, null);
            String[] headers = new String[]{ProductContract.ProductEntry.FORM, ProductContract.ProductEntry.NAME, ProductContract.ProductEntry.PRICE};
            cursorAdapter = new SimpleCursorAdapter(this, R.layout.display_product_row,
                    kursor, headers, new int[]{R.id.t_id, R.id.t_name, R.id.t_price}, 0);

            // если в текстовом поле есть текст, выполняем фильтрацию
            // данная проверка нужна при переходе от одной ориентации экрана к другой
            if(!inputSearch.getText().toString().isEmpty())
                cursorAdapter.getFilter().filter(inputSearch.getText().toString());

            // установка слушателя изменения текста
            inputSearch.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                // при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    cursorAdapter.getFilter().filter(s.toString());
                }
            });

            // устанавливаем провайдер фильтрации
            cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {

                    if (constraint == null || constraint.length() == 0) {

                        return dbOperations.myDatabase.rawQuery("select * from " + ProductContract.ProductEntry.TABLE_NAME, null);
                    }
                    else {
                        return dbOperations.myDatabase.rawQuery("select * from " + ProductContract.ProductEntry.TABLE_NAME + " where " +
                                ProductContract.ProductEntry.NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
                    }
                }
            });

            listView.setAdapter(cursorAdapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                    long id) {
                        Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                        String itemId = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.ID));

                        Intent intent;
                        intent = new Intent(DisplayProduct.this, InformationActivity.class);
                        intent.putExtra("ID", itemId);
                        startActivity(intent);
                    }
                });
        }
        catch (SQLException ex){}
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключения
        dbOperations.myDatabase.close();
        kursor.close();
    }

}




