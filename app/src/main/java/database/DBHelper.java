package database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AppDatabase.db";
    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_COLUMN_ID = "id";
    public static final String USERS_COLUMN_USERNAME = "username";
    public static final String USERS_COLUMN_EMAIL = "email";
    public static final String USERS_COLUMN_PASSWORD = "password";
    public static final String USERS_COLUMN_COMPLETE_NAME = "completeName";
    public static final String USERS_COLUMN_INVOICE_NAME = "invoiceName";
    public static final String USERS_COLUMN_CERTIFICATE_NAME = "certificateName";
    public static final String USERS_COLUMN_PHONE = "phone";
    public static final String USERS_COLUMN_CITIZEN_CARD = "citizenCard";
    public static final String USERS_COLUMN_WORK_BOOKLET = "workBooklet";
    public static final String USERS_COLUMN_NIF = "nif";
    public static final String USERS_COLUMN_ADDRESS = "address";
    public static final String USERS_COLUMN_POSTAL_CODE = "postalCode";


    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table users " +
                        "(id integer primary key, username text ,email text, password text, completeName text, invoiceName text, certificateName text, " +
                        " phone text, citizenCard text, workBooklet text, nif text, address text, postalCode text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }

    public boolean insertUser(String username, String email, String completeName, String invoiceName, String certificateName,
                              String phone, String citizenCard, String workBooklet, String nif, String address, String zipCode, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("completeName", completeName);
        contentValues.put("invoiceName", invoiceName);
        contentValues.put("certificateName", certificateName);
        contentValues.put("phone", phone);
        contentValues.put("citizenCard", citizenCard);
        contentValues.put("workBooklet", workBooklet);
        contentValues.put("nif", nif);
        contentValues.put("address", address);
        contentValues.put("postalCode", zipCode);
        contentValues.put("password", password);
        db.insert("users", null, contentValues);
        return true;
    }

    public Cursor getUserById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users where id="+id+"", null );
        return res;
    }

    public Cursor getUserByName(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users where username='"+username+"'", null );
        return res;
    }

    public int numberOfUsersRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USERS_TABLE_NAME);
        return numRows;
    }

    public boolean updateUser(Integer id, String username, String email, String completeName, String invoiceName, String certificateName,
                              String phone, String citizenCard, String workBooklet, String nif, String address, String zipCode, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("completeName", completeName);
        contentValues.put("invoiceName", invoiceName);
        contentValues.put("certificateName", certificateName);
        contentValues.put("phone", phone);
        contentValues.put("citizenCard", citizenCard);
        contentValues.put("workBooklet", workBooklet);
        contentValues.put("nif", nif);
        contentValues.put("address", address);
        contentValues.put("postalCode", zipCode);
        contentValues.put("password", password);
        db.update("users", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteUser(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("users",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllUsers()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from users", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(USERS_COLUMN_USERNAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
