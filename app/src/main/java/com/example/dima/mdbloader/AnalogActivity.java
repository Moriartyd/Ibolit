package com.example.dima.mdbloader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;

public class AnalogActivity extends Activity {
    private ListView listView;
    private EditText inputSearch; private DbOperations dbOperations; private SQLiteDatabase db;private Cursor kursor;
    SimpleCursorAdapter cursorAdapter;
    String form,name,vary;int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analog);


        Intent intent = getIntent();
        vary = intent.getStringExtra("ACT");

        listView = (ListView) findViewById(R.id.analogView);


        DbOperations dbOperations = new DbOperations(this);
        SQLiteDatabase db = dbOperations.getReadableDatabase();
        String selectQuery =  "select * from " + ProductContract.ProductEntry.TABLE_NAME + " where active_subst=" +"'" + vary+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        dbOperations.getInformations(db);
        while (cursor.moveToNext()) {
            form = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.FORM));
            name = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.NAME));
            price = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.PRICE));
        }
        //Lekarstvo lekarstvo = new Lekarstvo(name,form,price);
        dbOperations.openDataBase();
        kursor = dbOperations.myDatabase.rawQuery(selectQuery, null);
        String[] headers = new String[]{ProductContract.ProductEntry.FORM, ProductContract.ProductEntry.NAME, ProductContract.ProductEntry.PRICE};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.display_product_row,
                kursor, headers, new int[]{R.id.t_id, R.id.t_name, R.id.t_price}, 0);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                String itemId = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.ID));

                Intent intent;
                intent = new Intent(AnalogActivity.this, InformationActivity.class);
                intent.putExtra("ID", itemId);
                startActivity(intent);
            }
        });
    }

    public void onBackClick(View v){
        Intent intent = new Intent(AnalogActivity.this,DisplayProduct.class);
        startActivity(intent);
    }

}
