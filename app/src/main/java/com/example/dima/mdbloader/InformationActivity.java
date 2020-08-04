package com.example.dima.mdbloader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InformationActivity extends Activity {
    private String ID;
    private String FORM;
    private String NAME ;
    private String PRICE;
    private String ACTIVE_SUBST;
    private int n;
    private float rat,r;

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        TextView textName=(TextView)findViewById(R.id.textName);
        TextView textPrice=(TextView)findViewById(R.id.textPrice);
        TextView textForm=(TextView)findViewById(R.id.textForm);
        TextView textSubst=(TextView)findViewById(R.id.textSubst);
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");

        final RatingBar rBar = (RatingBar) findViewById(R.id.rBar);
        rBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase = database.getReference();

                mDatabase.child(ID).child("n").addValueEventListener(new  ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        n = dataSnapshot.getValue(Integer.class);
                        //Log.d("TAG", "Value N is: " + n);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabase.child(ID).child("r").addValueEventListener(new  ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         r = dataSnapshot.getValue(Float.class);
                        //Log.d("TAG", "Value M is: " + r);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                n++;
                rating = rating+r/n;
                rBar.setRating(rating);
                rat=rating*n;
                mDatabase.child(ID).child("n").setValue(n);
                mDatabase.child(ID).child("r").setValue(rat);

            }
        });




        DbOperations dbOperations = new DbOperations(this);
        SQLiteDatabase db = dbOperations.getReadableDatabase();
        String selectQuery =  "select * from " + ProductContract.ProductEntry.TABLE_NAME + " where _id=" +"'"+ ID+"'";
        Cursor cursor =  db.rawQuery(selectQuery,null);
        //Cursor cursor1 = dbOperations.getInformations(db);
        //cursor1.moveToFirst();
        while (cursor.moveToNext()) {
            NAME = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.NAME));
            FORM = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.FORM));
            PRICE = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.PRICE));
            ACTIVE_SUBST = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.ACTIVE_SUBST));
        }
        textName.setText("Название: "+NAME);
        textForm.setText("Форма выпуска: "+FORM);
        textPrice.setText("Ориентировачная цена: "+PRICE);
        textSubst.setText("Действующее вещество: "+ACTIVE_SUBST);

    }

    public void AnalogClick(View view){
       Intent intent = new Intent(InformationActivity.this, AnalogActivity.class);
        intent.putExtra("ACT", ACTIVE_SUBST);
        startActivity(intent);
    }

}

