package com.example.farmconnect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "FarmConnect.db";
    public static String LOGGED_USER_ID = "";
    public static String LOGGED_USERNAME = "";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(uid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, password TEXT)");
        db.execSQL("CREATE TABLE messages(msgId INTEGER PRIMARY KEY AUTOINCREMENT, msgContent TEXT, msgType TEXT, timeStamp TEXT, senderId TEXT, user TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }

    public Boolean insertData(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = db.insert("users", null, contentValues);

        return result != -1;
    }

    public Boolean checkUsernameExists(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[] {username});
        return cursor.getCount() > 0;
    }

    public Boolean authorizeUser(String usernameEmail, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?", new String[] {usernameEmail, usernameEmail, password});
        if (cursor.getCount() > 0){
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?", new String[] {usernameEmail, usernameEmail, password});
            cursor.moveToFirst();
            LOGGED_USER_ID = cursor.getString(0);
            LOGGED_USERNAME = cursor.getString(1);
            return  true;
        } else {
            return false;
        }
    }

    public String getLoggedUserID(){
        return LOGGED_USER_ID;
    }
    public  String getLoggedUsername() {
        return LOGGED_USERNAME;
    }

    public Boolean insertMessage(String msg, String msgType, String timeStamp, String senderId, String user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("msgContent", msg);
        contentValues.put("msgType", msgType);
        contentValues.put("timeStamp", timeStamp);
        contentValues.put("senderId", senderId);
        contentValues.put("user", user);

        long result = db.insert("messages", null, contentValues);

        return result != -1;
    }

    Cursor readMessages(){
        String query = "SELECT * FROM messages";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    public void editMessage(String msgContent, String msgType, String timeStamp, String senderId, String newMsg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("msgContent", newMsg);
        db.update("messages", contentValues, "msgContent = ? AND msgType = ? AND timeStamp = ? AND senderId = ?", new String[] {msgContent, msgType,
                timeStamp, senderId});
    }

    public void deleteMessage(String msgContent, String msgType, String timeStamp, String senderId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("messages", "msgContent = ? AND msgType = ? AND timeStamp = ? AND senderId = ?", new String[] {msgContent, msgType, timeStamp, senderId});
    }

    Cursor searchMessages(String text){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM messages WHERE msgType = 'text' AND msgContent LIKE '%" + text + "%'";

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

}
