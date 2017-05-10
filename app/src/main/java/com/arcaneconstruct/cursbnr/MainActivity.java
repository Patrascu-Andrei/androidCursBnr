package com.arcaneconstruct.cursbnr;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String lastSync;
    Button btnUpdate;
    TextView txtUpdate;
    public static final String PREFS_NAME = "SHARED_INFO";
    private static final String TAG = "MainActivity";
    BnrAlarmReceiver alarm = new BnrAlarmReceiver();
    List<Curs> myListData = new ArrayList<Curs>();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnUpdate = (Button) findViewById(R.id.button_update);
        txtUpdate = (TextView) findViewById(R.id.text_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUpdate.setText("Ultima sincronizare " + lastSync);
                loadDataFromDataBase();
            }
        });
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        lastSync = settings.getString("lastSync", "");
        if (lastSync.length() > 0) {
            txtUpdate.setText("Ultima sincronizare " + lastSync);
        }
        list = (ListView) findViewById(R.id.mylist);
        XMLRetriever getXML = new XMLRetriever(this);
        getXML.execute();
        alarm.setAlarm(this);
        loadDataFromDataBase();
    }

    public void setMyListData(List<Curs> myListData) {
        this.myListData = myListData;
        CursAdapter adapter = new CursAdapter(this, R.layout.layout_row, myListData);
        list.setAdapter(adapter);
    }
    public void loadDataFromDataBase(){
        Log.d(TAG, "Incarcam din Baza de date");
        ExchangeDatabase database = new ExchangeDatabase(this);
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ExchangeDatabase.TABLE_NAME, null);
        List<Curs> data=new ArrayList<Curs>();
        if(cursor!=null&&cursor.moveToFirst()) {
            Curs temp = new Curs();
            do {
                temp=new Curs();
                temp.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                temp.setRate(cursor.getString(cursor.getColumnIndex(ExchangeDatabase.COLUMN_RATE)));
                temp.setCurrency(cursor.getString(cursor.getColumnIndex(ExchangeDatabase.COLUMN_CURRENCY)));
                temp.setDate(cursor.getString(cursor.getColumnIndex(ExchangeDatabase.COLUMN_DATE)));
                Log.d(TAG,temp.toString());
                data.add(temp);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        setMyListData(data);
    }
}
