package com.example.pethospitaldb;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    Spinner spNameOfCity, spNameOfBusiness;
    SQLiteDatabase sqldb;
    String path = "/data/data/com.example.pethospitaldb/databases/petHDB.db";
    ArrayList<dataSet> arrayList = new ArrayList<>();
    ArrayList<String> arrayListSP1 = new ArrayList<>();
    Integer inte[] = {R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.tv7};
    TextView tv[] = new TextView[inte.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("데이터베이스 활용");
        bar.setIcon(R.drawable.ic_launcher_foreground);
        bar.setDisplayShowHomeEnabled(true);
        spNameOfCity = (Spinner) findViewById(R.id.spNameOfCity);
        spNameOfBusiness = (Spinner) findViewById(R.id.spNameOfBusiness);
        for (int i = 0; i < inte.length; i++) {
            tv[i] = (TextView) findViewById(inte[i]);
        }
        boolean bResult = isCheckDB(this);
        try {
            if (bResult == false) {
                Toast.makeText(getApplicationContext(), "복사중", Toast.LENGTH_SHORT).show();
                copyDB(this);
            }
        }
        catch (IOException ie)
        {}
        sqldb = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = sqldb.rawQuery("select * from petHTBL", null);

        while (cursor.moveToNext() != false) {
            dataSet d = new dataSet();
            for (int i = 0; i < d.Data.length; i++) {
                d.Data[i] = cursor.getString(i);
                if (i == 0) {
                    if (!arrayListSP1.contains(cursor.getString(i))) {
                        arrayListSP1.add(cursor.getString(i));
                    }
                }
            }
            arrayList.add(d);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayListSP1);
        spNameOfCity.setAdapter(adapter);
        spNameOfCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> arrayListSP2 = new ArrayList<>();
                ListIterator<dataSet> lit = arrayList.listIterator();
                while (lit.hasNext()) {
                    dataSet d = lit.next();
                    if (arrayListSP1.get(position).equals(d.Data[0])) {
                        arrayListSP2.add(d.Data[1]);
                    }
                }
                Collections.sort(arrayListSP2);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, arrayListSP2);
                spNameOfBusiness.setAdapter(adapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spNameOfBusiness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ListIterator<dataSet> lit = arrayList.listIterator();
                while (lit.hasNext()) {
                    dataSet d = lit.next();
                    if (d.Data[1].equals(spNameOfBusiness.getAdapter().getItem(position))) {
                        tv[0].setText("소재지=" + d.Data[0]);
                        tv[1].setText("병원이름=" + d.Data[1]);
                        tv[2].setText("개업일=" + d.Data[2]);
                        tv[3].setText("현재상태=" + d.Data[3]);
                        tv[4].setText("전화번호=" + d.Data[5]);
                        tv[5].setText("우편번호=" + d.Data[6]);
                        tv[6].setText("주소=" + d.Data[7]);


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean isCheckDB(Context context) {
        String filePath = "/data/data/com.example.pethospitaldb/databases/petHDB.db";
        File file = new File(filePath);
        long newdb_size = 0;
        long olddb_size = file.length();
        AssetManager manager = context.getAssets();
        try {
            InputStream is = manager.open("petHDB.db");
            newdb_size = is.available();
        } catch (IOException ie) {
        }
        return (newdb_size == 0) ? false : (newdb_size == olddb_size) ? true : false;
    }

    public void copyDB(Context context) throws IOException  {
        AssetManager manager = context.getAssets();
        String folderPath = "/data/data/com.example.pethospitaldb/databases";
        String filePath = folderPath + "/petHDB.db";
        File folder = new File(folderPath);
        File file = new File(filePath);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (folder.exists()) {
                if (file.exists()) {
                    file.delete();
                    file.createNewFile();
                }
            } else {
                folder.mkdir();
            }
            bis = new BufferedInputStream(manager.open("petHDB.db"));
            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            byte buffer[] = new byte[1024];
            int read = -1;
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }
        } catch (IOException ie) {
        }
        finally {
            bos.close();
            bis.close();
        }
    }

    class dataSet {
        String Data[] = new String[10];
    }
}
