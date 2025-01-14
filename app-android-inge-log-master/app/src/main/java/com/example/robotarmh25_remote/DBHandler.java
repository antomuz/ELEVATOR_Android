package com.example.robotarmh25_remote;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.robotarmh25_remote.data.RepositoryScenario.Scenario;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "EV3data";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "Scenario";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our course name column
    private static final String ACTION5_COL = "action5";

    // below variable is for our course name column
    private static final String ACTION1_COL = "action1";
    // below variable is for our course name column
    private static final String ACTION2_COL = "action2";
    // below variable is for our course name column
    private static final String ACTION3_COL = "action3";
    // below variable is for our course name column
    private static final String ACTION4_COL = "action4";
    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ACTION1_COL + " TEXT,"
                + ACTION2_COL + " TEXT,"
                + ACTION3_COL + " TEXT,"
                + ACTION4_COL + " TEXT,"
                + ACTION5_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewScenario(Scenario actions) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        for (int i = 0; i<5; i++) {
            Log.e("Bluetooth", (i+1)>actions.nbTasks()?"non":"oiu");
            if ((i+1)>actions.nbTasks()) {
                values.put("action"+(i+1), "NULL");
                Log.e("Bluetooth", values.toString());
            }
            else {
                values.put("action"+(i+1), actions.getTask(i).toString());
                Log.e("Bluetooth", values.toString());
            }
        }
        Log.e("Bluetooth", values.toString());
/*        values.put(ACTION1_COL, actions.getTask(0).getTaskType().toString());
        values.put(ACTION2_COL, actions.getTask(1).getTaskType().toString());
        values.put(ACTION3_COL, actions.getTask(2).getTaskType().toString());
        values.put(ACTION4_COL, actions.getTask(3).getTaskType().toString());
        values.put(ACTION5_COL, actions.getTask(4).getTaskType().toString());*/
        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public HashMap<String, ArrayList<String>> getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();

        // Query to fetch all rows from the table
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            ArrayList<String> scenario = new ArrayList<String>();

            // Get column indices and check if valid
            int idIndex = res.getColumnIndex(ID_COL);
            int col1Index = res.getColumnIndex(ACTION1_COL);
            int col2Index = res.getColumnIndex(ACTION2_COL);
            int col3Index = res.getColumnIndex(ACTION3_COL);
            int col4Index = res.getColumnIndex(ACTION4_COL);
            int col5Index = res.getColumnIndex(ACTION5_COL);

            if (idIndex != -1) {
                String id = res.getString(idIndex);
                scenario.add(id);
            } else {
                Log.e("DBHandler", "Column " + ID_COL + " not found!");
            }

            if (col1Index != -1) {
                String col1 = res.getString(col1Index);
                scenario.add(col1);
            } else {
                Log.e("DBHandler", "Column " + ACTION1_COL + " not found!");
            }

            if (col2Index != -1) {
                String col2 = res.getString(col2Index);
                scenario.add(col2);
            } else {
                Log.e("DBHandler", "Column " + ACTION2_COL + " not found!");
            }

            if (col3Index != -1) {
                String col3 = res.getString(col3Index);
                scenario.add(col3);
            } else {
                Log.e("DBHandler", "Column " + ACTION3_COL + " not found!");
            }

            if (col4Index != -1) {
                String col4 = res.getString(col4Index);
                scenario.add(col4);
            } else {
                Log.e("DBHandler", "Column " + ACTION4_COL + " not found!");
            }

            if (col5Index != -1) {
                String col5 = res.getString(col5Index);
                scenario.add(col5);
            } else {
                Log.e("DBHandler", "Column " + ACTION5_COL + " not found!");
            }

            // Add to result map with ID as the key
            result.put(res.getString(idIndex), scenario);

            res.moveToNext();
        }

        res.close();
        return result;
    }
}