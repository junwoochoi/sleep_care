package com.example.dell.sleepcare.LocalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dell.sleepcare.Model.UserEnv;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private Context context; public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createEnvDb = "CREATE TABLE ENV_TB(" +
                "USER_EMAIL VARCHAR(40)," +
                "ENV_TIME DATETIME," +
                "ENV_HUMID VARCHAR(20)," +
                "ENV_TEMP VARCHAR(20)," +
                "ENV_LIGHT VARCHAR(20)," +
                "ENV_LOUD VARCHAR(20))";
        db.execSQL(createEnvDb);
    }

    public List<UserEnv> getAll(){
        List<UserEnv> envList = new ArrayList<UserEnv>();

        String selectAll = "Select * from ENV_TB";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);

        if(cursor.moveToFirst()){
            do{
                UserEnv userEnv = new UserEnv();
                userEnv.setUserEmail(cursor.getString(0));
                userEnv.setEnvTime(cursor.getString(1));
                userEnv.setEnvHumid(cursor.getString(2));
                userEnv.setEnvTemp(cursor.getString(3));
                userEnv.setEnvLight(cursor.getString(4));
                userEnv.setEnvLoud(cursor.getString(5));
                envList.add(userEnv);
            } while (cursor.moveToNext());
        }
        return envList;
    }

    public void add(UserEnv env){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("USER_EMAIL", env.getUserEmail());
        values.put("ENV_TIME", env.getEnvTime());
        values.put("ENV_HUMID", env.getEnvHumid());
        values.put("ENV_TEMP", env.getEnvTemp());
        values.put("ENV_LIGHT", env.getEnvLight());
        values.put("ENV_LOUD", env.getEnvLoud());

        db.insert("ENV_TB", null, values);
        db.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void testDB() {
        SQLiteDatabase db = getReadableDatabase();
    }
}