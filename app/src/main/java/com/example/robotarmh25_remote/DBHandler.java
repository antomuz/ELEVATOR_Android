package com.example.robotarmh25_remote;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.robotarmh25_remote.models.Action;
import com.example.robotarmh25_remote.models.Gamme;
import com.example.robotarmh25_remote.models.Scenario;
import com.example.robotarmh25_remote.models.SelectedAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    private static final String DB_NAME = "EV3data";
    private static final int DB_VERSION = 3;

    // -----------------------------------------------------------------
    // 1) Tables principales
    // -----------------------------------------------------------------
    // 1.1 Table Action
    public static final String TABLE_ACTION = "ActionTable";
    public static final String COL_ACTION_ID = "id_action";
    public static final String COL_ACTION_NAME = "name";

    // 1.2 Table Gamme
    public static final String TABLE_GAMME = "Gamme";
    public static final String COL_GAMME_ID = "id_gamme";
    public static final String COL_GAMME_NAME = "name";

    // 1.3 Table Scenario
    public static final String TABLE_SCENARIO = "Scenario";
    public static final String COL_SCENARIO_ID = "id_scenario";
    public static final String COL_SCENARIO_NAME = "name";
    public static final String COL_SCENARIO_DESC = "description";

    // -----------------------------------------------------------------
    // 2) Tables de liaison
    // -----------------------------------------------------------------
    // 2.1 Table Gamme_Action
    public static final String TABLE_GAMME_ACTION = "Gamme_Action";
    // - Colonnes
    public static final String COL_GA_GAMME_ID = "id_gamme";
    public static final String COL_GA_ACTION_ID = "id_action";
    public static final String COL_GA_PARAM = "parametre";           // valeur numérique (durée, vitesse...)
    public static final String COL_GA_ORDRE = "ordre_execution";     // pour l'ordre d'exécution

    // 2.2 Table Scenario_Gamme
    public static final String TABLE_SCENARIO_GAMME = "Scenario_Gamme";
    // - Colonnes
    public static final String COL_SG_SCENARIO_ID = "id_scenario";
    public static final String COL_SG_GAMME_ID = "id_gamme";
    public static final String COL_SG_ORDRE = "ordre_scenario";      // ordre de la gamme dans le scénario


    // -----------------------------------------------------------------
    // 3) Table utilisateurs
    // -----------------------------------------------------------------
    private static final String TABLE_USERS = "Users";
    private static final String USER_ID_COL = "user_id";
    private static final String USERNAME_COL = "username";
    private static final String PASSWORD_COL = "password";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // -----------------------------------------------------------------
        // Création des tables principales
        // -----------------------------------------------------------------

        // Table Action
        String createActionTable = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTION + " ("
                + COL_ACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ACTION_NAME + " TEXT NOT NULL"
                + ");";

        // Table Gamme
        String createGammeTable = "CREATE TABLE IF NOT EXISTS " + TABLE_GAMME + " ("
                + COL_GAMME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_GAMME_NAME + " TEXT"
                + ");";

        // Table Scenario
        String createScenarioTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SCENARIO + " ("
                + COL_SCENARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SCENARIO_NAME + " TEXT, "
                + COL_SCENARIO_DESC + " TEXT"
                + ");";

        // -----------------------------------------------------------------
        // Création des tables de liaison
        // -----------------------------------------------------------------

        // Table Gamme_Action (clé primaire composite : (id_gamme, id_action, ordre_execution))
        // On peut la gérer de cette façon (3 colonnes PK)
        String createGammeActionTable = "CREATE TABLE IF NOT EXISTS " + TABLE_GAMME_ACTION + " ("
                + COL_GA_GAMME_ID + " INTEGER NOT NULL, "
                + COL_GA_ACTION_ID + " INTEGER NOT NULL, "
                + COL_GA_ORDRE + " INTEGER NOT NULL, "
                + COL_GA_PARAM + " REAL, "
                + "PRIMARY KEY (" + COL_GA_GAMME_ID + ", " + COL_GA_ACTION_ID + ", " + COL_GA_ORDRE + "), "
                + "FOREIGN KEY (" + COL_GA_GAMME_ID + ") REFERENCES " + TABLE_GAMME + "(" + COL_GAMME_ID + "), "
                + "FOREIGN KEY (" + COL_GA_ACTION_ID + ") REFERENCES " + TABLE_ACTION + "(" + COL_ACTION_ID + ")"
                + ");";

        // Table Scenario_Gamme (clé primaire composite : (id_scenario, id_gamme, ordre_scenario))
        String createScenarioGammeTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SCENARIO_GAMME + " ("
                + COL_SG_SCENARIO_ID + " INTEGER NOT NULL, "
                + COL_SG_GAMME_ID + " INTEGER NOT NULL, "
                + COL_SG_ORDRE + " INTEGER NOT NULL, "
                + "PRIMARY KEY (" + COL_SG_SCENARIO_ID + ", " + COL_SG_GAMME_ID + ", " + COL_SG_ORDRE + "), "
                + "FOREIGN KEY (" + COL_SG_SCENARIO_ID + ") REFERENCES " + TABLE_SCENARIO + "(" + COL_SCENARIO_ID + "), "
                + "FOREIGN KEY (" + COL_SG_GAMME_ID + ") REFERENCES " + TABLE_GAMME + "(" + COL_GAMME_ID + ")"
                + ");";

        // -----------------------------------------------------------------
        // Création de la table utilisateurs
        // -----------------------------------------------------------------

        String createUsersTableQuery = "CREATE TABLE " + TABLE_USERS + " ("
                + USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " TEXT UNIQUE, "
                + PASSWORD_COL + " TEXT)";


        // Exécution des requêtes de création
        db.execSQL(createActionTable);
        db.execSQL(createGammeTable);
        db.execSQL(createScenarioTable);
        db.execSQL(createGammeActionTable);
        db.execSQL(createScenarioGammeTable);
        db.execSQL(createUsersTableQuery);

        // données par défaut
        insertDefaultActions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On supprime puis on recrée les tables si on change de version
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMME_ACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCENARIO_GAMME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCENARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }

    // -------------------------------------------------------------
    //    ACTION METHODS
    // -------------------------------------------------------------
    /**
     * Insert some default rows into the Action table.
     * Called from onCreate() and/or onUpgrade().
     */
    private void insertDefaultActions(SQLiteDatabase db) {
        // Example default actions
        insertAction(db, "NIVEAU");
        insertAction(db, "ROULER_HORAIRE");
        insertAction(db, "ROULER_ANTIHORAIRE");
        insertAction(db, "ATTENTE");
    }

    /**
     * Helper method to insert one action row.
     * We pass the SQLiteDatabase to keep everything within onCreate/onUpgrade flow.
     */
    private void insertAction(SQLiteDatabase db, String actionName) {
        ContentValues values = new ContentValues();
        values.put(COL_ACTION_NAME, actionName);
        db.insert(TABLE_ACTION, null, values);
    }

    public JSONObject construireJsonScenario(int scenarioId) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("type", "scenario");
        root.put("header", header);

        JSONArray body = new JSONArray();

        // Récupère les gammes du scénario dans l'ordre
        Cursor gammeCursor = db.rawQuery(
                "SELECT id_gamme FROM Scenario_Gamme WHERE id_scenario = ? ORDER BY ordre_scenario ASC",
                new String[]{String.valueOf(scenarioId)}
        );

        while (gammeCursor.moveToNext()) {
            int gammeId = gammeCursor.getInt(0);

            // Récupère les actions associées à la gamme
            Cursor actionCursor = db.rawQuery(
                    "SELECT id_action, parametre FROM Gamme_Action WHERE id_gamme = ? ORDER BY ordre_execution ASC",
                    new String[]{String.valueOf(gammeId)}
            );

            JSONArray gammeArray = new JSONArray();

            while (actionCursor.moveToNext()) {
                int actionId = actionCursor.getInt(0);
                int param = actionCursor.getInt(1);

                // Récupère le nom de l'action
                Cursor nameCursor = db.rawQuery(
                        "SELECT name FROM ActionTable WHERE id_action = ?",
                        new String[]{String.valueOf(actionId)}
                );
                String actionName = "";
                if (nameCursor.moveToFirst()) {
                    actionName = nameCursor.getString(0);
                }
                nameCursor.close();

                gammeArray.put(actionName + "_" + param);
            }

            actionCursor.close();
            body.put(gammeArray);
        }

        gammeCursor.close();
        root.put("body", body);

        return root;
    }

    /**
     * Get all actions from the 'Action' table.
     * @return A list of Action objects.
     */
    public List<Action> getAllActions() {
        List<Action> actions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Raw query to select all rows
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACTION, null);

        // If there is at least one result, moveToFirst() will be true
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_ACTION_ID);
            int nameIndex = cursor.getColumnIndex(COL_ACTION_NAME);

            // Iterate through all the rows
            do {
                int idAction = cursor.getInt(idIndex);
                String actionName = cursor.getString(nameIndex);

                // Create an Action object
                Action action =
                        new Action(idAction, actionName);

                // Add to the list
                actions.add(action);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return actions;
    }

    /**
     * Retrieve a single Action by its ID.
     * @param actionId The ID of the Action to find.
     * @return The Action object if found, or null otherwise.
     */
    public Action getActionById(int actionId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query (SELECT) for the row matching actionId
        Cursor cursor = db.query(
                TABLE_ACTION,
                new String[]{COL_ACTION_ID, COL_ACTION_NAME},           // columns to return
                COL_ACTION_ID + "=?",                                  // selection (WHERE)
                new String[]{String.valueOf(actionId)},                // selection args
                null,                                                  // groupBy
                null,                                                  // having
                null                                                   // orderBy
        );

        Action foundAction = null;

        // If cursor is not null and we can move to the first row
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_ACTION_ID);
            int nameIndex = cursor.getColumnIndex(COL_ACTION_NAME);

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);

            foundAction = new Action(id, name);

            cursor.close();
        }

        db.close();
        return foundAction;
    }

    public Action getActionByName(String actionName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_ACTION,
                new String[]{COL_ACTION_ID, COL_ACTION_NAME},
                COL_ACTION_NAME + "=?",
                new String[]{actionName},
                null, null, null
        );
        Action result = null;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_ACTION_ID);
            int nameIndex = cursor.getColumnIndex(COL_ACTION_NAME);
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            result = new Action(id, name);
        }
        if (cursor != null) cursor.close();
        db.close();
        return result;
    }

    // -------------------------------------------------------------
    //    GAMME METHODS
    // -------------------------------------------------------------

    /**
     * Insert a new Gamme into the database.
     * @param name The name/label of the new Gamme.
     * @return The row ID of the newly inserted Gamme, or -1 if an error occurred.
     */
    public long insertGamme(String name, ArrayList<SelectedAction> selectedActions) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long gammeId = -1;

        try {
            // Insert the Gamme itself (name only)
            ContentValues gammeValues = new ContentValues();
            gammeValues.put("name", name);
            gammeId = db.insertOrThrow("Gamme", null, gammeValues);

            // Insert each SelectedAction into Gamme_Action with its user-given parameter
            int ordreExecution = 1;
            for (SelectedAction selectedAction : selectedActions) {
                ContentValues liaisonValues = new ContentValues();
                liaisonValues.put("id_gamme", gammeId);
                liaisonValues.put("id_action", selectedAction.getAction().getId_action());
                liaisonValues.put("parametre", selectedAction.getParametre());  // Correctly using user input
                liaisonValues.put("ordre_execution", ordreExecution++);
                db.insertOrThrow("Gamme_Action", null, liaisonValues);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DBHandler", "Erreur lors de l'insertion de la gamme", e);
            gammeId = -1;
        } finally {
            db.endTransaction();
        }

        return gammeId;
    }


    /**
     * Retrieve all Gammes from the database.
     * @return A list of Gamme objects.
     */
    public List<Gamme> getAllGammes() {
        List<Gamme> gammeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GAMME, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_GAMME_ID);
            int nameIndex = cursor.getColumnIndex(COL_GAMME_NAME);

            do {
                int idGamme = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);

                Gamme gamme = new Gamme(idGamme, name);
                gammeList.add(gamme);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return gammeList;
    }

    /**
     * Retrieve a single Gamme by its ID.
     * @param gammeId The ID of the Gamme to find.
     * @return The Gamme object if found, or null otherwise.
     */
    public Gamme getGammeById(int gammeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query (SELECT) for the row matching gammeId
        Cursor cursor = db.query(
                TABLE_GAMME,
                new String[]{COL_GAMME_ID, COL_GAMME_NAME},
                COL_GAMME_ID + "=?",
                new String[]{String.valueOf(gammeId)},
                null, null, null);

        Gamme foundGamme = null;

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_GAMME_ID);
            int nameIndex = cursor.getColumnIndex(COL_GAMME_NAME);

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);

            foundGamme = new Gamme(id, name);
        }

        if (cursor != null) cursor.close();
        db.close();
        return foundGamme;
    }

    /**
     * Update the name of a Gamme by its ID.
     * @param gammeId ID of the Gamme to update.
     * @param name The new name for the Gamme.
     * @return The number of rows affected (should be 1 if success, 0 if not found).
     */
    public boolean updateGamme(int gammeId, String name, ArrayList<SelectedAction> selectedActions) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // Update gamme name
            ContentValues values = new ContentValues();
            values.put("name", name);
            db.update("Gamme", values, "id_gamme=?", new String[]{String.valueOf(gammeId)});

            // Delete previous actions
            db.delete("Gamme_Action", "id_gamme=?", new String[]{String.valueOf(gammeId)});

            // Re-insert actions
            int order = 1;
            for (SelectedAction action : selectedActions) {
                ContentValues liaison = new ContentValues();
                liaison.put("id_gamme", gammeId);
                liaison.put("id_action", action.getAction().getId_action());
                liaison.put("parametre", action.getParametre());
                liaison.put("ordre_execution", order++);
                db.insert("Gamme_Action", null, liaison);
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DBHandler", "Update Gamme Error", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Delete a Gamme by its ID.
     * @param gammeId The ID of the Gamme to delete.
     * @return The number of rows affected (should be 1 if success).
     */
    public int deleteGamme(int gammeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_GAMME,
                COL_GAMME_ID + "=?",
                new String[]{String.valueOf(gammeId)}
        );

        db.close();
        return rowsDeleted;
    }

    public long addGammeAction(int gammeId, int actionId, int ordreExecution, float paramValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GA_GAMME_ID, gammeId);
        values.put(COL_GA_ACTION_ID, actionId);
        values.put(COL_GA_ORDRE, ordreExecution);
        values.put(COL_GA_PARAM, paramValue);
        // Primary key is composite, so if (gammeId, actionId, ordreExecution) is a duplicate, insert fails
        long rowId = db.insert(TABLE_GAMME_ACTION, null, values);
        db.close();
        return rowId;
    }

    // Get gamme name by ID
    public String getGammeNameById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM Gamme WHERE id_gamme=?", new String[]{String.valueOf(id)});
        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    // Get selected actions of a gamme by ID
    public ArrayList<SelectedAction> getSelectedActionsByGammeId(int gammeId) {
        ArrayList<SelectedAction> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT GA.id_action, A.name, GA.parametre FROM Gamme_Action GA JOIN ActionTable A ON GA.id_action = A.id_action WHERE GA.id_gamme=? ORDER BY GA.ordre_execution", new String[]{String.valueOf(gammeId)});

        if (cursor.moveToFirst()) {
            do {
                int actionId = cursor.getInt(0);
                String actionName = cursor.getString(1);
                int parametre = cursor.getInt(2);
                list.add(new SelectedAction(new Action(actionId, actionName), parametre));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Gamme> getGammesByScenarioId(int scenarioId) {
        ArrayList<Gamme> gammes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT G.id_gamme, G.name " +
                "FROM Scenario_Gamme SG " +
                "JOIN Gamme G ON SG.id_gamme = G.id_gamme " +
                "WHERE SG.id_scenario = ? " +
                "ORDER BY SG.ordre_scenario ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(scenarioId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_gamme"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                gammes.add(new Gamme(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return gammes;
    }

    // -------------------------------------------------------------
    //    SCENARIO METHODS
    // -------------------------------------------------------------
    /**
     * Insert a new Scenario into the database.
     * @param name        The scenario's name
     * @param description Optional text describing the scenario
     * @return The row ID of the newly inserted scenario, or -1 if an error occurred.
     */
    public long addScenario(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_SCENARIO_NAME, name);
        values.put(COL_SCENARIO_DESC, description);

        long newRowId = db.insert(TABLE_SCENARIO, null, values);
        db.close();
        return newRowId;
    }

    /**
     * Retrieve all Scenarios from the database.
     * @return A list of Scenario objects.
     */
    public List<Scenario> getAllScenarios() {
        List<Scenario> scenarioList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCENARIO, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_SCENARIO_ID);
            int nameIndex = cursor.getColumnIndex(COL_SCENARIO_NAME);
            int descIndex = cursor.getColumnIndex(COL_SCENARIO_DESC);

            do {
                int idScenario = cursor.getInt(idIndex);
                String scenarioName = cursor.getString(nameIndex);
                String scenarioDesc = cursor.getString(descIndex);

                Scenario scenario = new Scenario(idScenario, scenarioName, scenarioDesc);
                scenarioList.add(scenario);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return scenarioList;
    }

    /**
     * Retrieve a single Scenario by its ID.
     * @param scenarioId The ID of the Scenario to find.
     * @return The Scenario object if found, or null otherwise.
     */
    public Scenario getScenarioById(int scenarioId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_SCENARIO,
                new String[]{COL_SCENARIO_ID, COL_SCENARIO_NAME, COL_SCENARIO_DESC},
                COL_SCENARIO_ID + "=?",
                new String[]{String.valueOf(scenarioId)},
                null,
                null,
                null
        );

        Scenario foundScenario = null;

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_SCENARIO_ID);
            int nameIndex = cursor.getColumnIndex(COL_SCENARIO_NAME);
            int descIndex = cursor.getColumnIndex(COL_SCENARIO_DESC);

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String desc = cursor.getString(descIndex);

            foundScenario = new Scenario(id, name, desc);
        }

        if (cursor != null) cursor.close();
        db.close();
        return foundScenario;
    }

    /**
     * Update the name and description of a Scenario by its ID.
     * @param scenarioId   The ID of the Scenario to update.
     * @param newName      New name
     * @param newDesc      New description (or null if none)
     * @return The number of rows affected (1 if success, 0 if not found, or >1 if multiple).
     */
    public int updateScenario(int scenarioId, String newName, String newDesc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SCENARIO_NAME, newName);
        values.put(COL_SCENARIO_DESC, newDesc);

        int rowsAffected = db.update(
                TABLE_SCENARIO,
                values,
                COL_SCENARIO_ID + "=?",
                new String[]{String.valueOf(scenarioId)}
        );

        db.close();
        return rowsAffected;
    }

    /**
     * Delete a Scenario by its ID.
     * @param scenarioId The ID of the Scenario to delete.
     * @return The number of rows deleted (1 if success, 0 if not found).
     */
    public int deleteScenario(int scenarioId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_SCENARIO,
                COL_SCENARIO_ID + "=?",
                new String[]{String.valueOf(scenarioId)}
        );

        db.close();
        return rowsDeleted;
    }

    public boolean updateScenarioWithGammes(int scenarioId, String name, String desc, List<Gamme> orderedGammes) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // 1. Update Scenario info
            ContentValues values = new ContentValues();
            values.put(COL_SCENARIO_NAME, name);
            values.put(COL_SCENARIO_DESC, desc);
            db.update(TABLE_SCENARIO, values, COL_SCENARIO_ID + "=?", new String[]{String.valueOf(scenarioId)});

            // 2. Delete all existing gammes from the scenario
            db.delete(TABLE_SCENARIO_GAMME, COL_SG_SCENARIO_ID + "=?", new String[]{String.valueOf(scenarioId)});

            // 3. Re-insert with new order
            int order = 1;
            for (Gamme gamme : orderedGammes) {
                ContentValues link = new ContentValues();
                link.put(COL_SG_SCENARIO_ID, scenarioId);
                link.put(COL_SG_GAMME_ID, gamme.getId_gamme());
                link.put(COL_SG_ORDRE, order++);
                db.insert(TABLE_SCENARIO_GAMME, null, link);
            }

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Log.e("DBHandler", "Erreur updateScenarioWithGammes", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Gamme> getGammesForScenario(int scenarioId) {
        ArrayList<Gamme> gammes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT G." + COL_GAMME_ID + ", G." + COL_GAMME_NAME +
                        " FROM " + TABLE_SCENARIO_GAMME + " SG " +
                        " JOIN " + TABLE_GAMME + " G ON SG." + COL_SG_GAMME_ID + " = G." + COL_GAMME_ID +
                        " WHERE SG." + COL_SG_SCENARIO_ID + " = ? " +
                        " ORDER BY SG." + COL_SG_ORDRE,
                new String[]{String.valueOf(scenarioId)}
        );

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_GAMME_ID);
            int nameIndex = cursor.getColumnIndex(COL_GAMME_NAME);

            do {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                gammes.add(new Gamme(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return gammes;
    }

    public boolean deleteScenarioCascade(int scenarioId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // Delete associated rows from Scenario_Gamme (liaison table)
            db.delete(TABLE_SCENARIO_GAMME, COL_SG_SCENARIO_ID + "=?", new String[]{String.valueOf(scenarioId)});

            // Delete the scenario itself
            db.delete(TABLE_SCENARIO, COL_SCENARIO_ID + "=?", new String[]{String.valueOf(scenarioId)});

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DBHandler", "Erreur suppression scénario (cascade)", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Ajoute une entrée dans la table de liaison Scenario_Gamme.
     * @param scenarioId ID du scénario
     * @param gammeId ID de la gamme
     * @param ordre Ordre d'exécution dans le scénario
     * @return ID de la ligne insérée, ou -1 en cas d'erreur
     */
    public long addScenarioGamme(int scenarioId, int gammeId, int ordre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SG_SCENARIO_ID, scenarioId);
        values.put(COL_SG_GAMME_ID, gammeId);
        values.put(COL_SG_ORDRE, ordre);

        long result = db.insert(TABLE_SCENARIO_GAMME, null, values);
        db.close();
        return result;
    }

    public void clearScenarioGammes(int scenarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCENARIO_GAMME, COL_SG_SCENARIO_ID + "=?", new String[]{String.valueOf(scenarioId)});
        db.close();
    }

    // -------------------------------------------------------------
    //    USER METHODS
    // -------------------------------------------------------------
    /**
     * Add a user to the Users table.
     * @param username The user's chosen username.
     * @param password The user's chosen password (plain text or hashed).
     * @return true if successfully inserted, false otherwise.
     */
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_COL, username);
        values.put(PASSWORD_COL, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        // if result == -1 => error
        return (result != -1);
    }

    /**
     * Check if a user exists in the DB with the given username + password.
     * @return true if match is found, false otherwise.
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query all matching rows
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{USER_ID_COL}, // we just need the ID
                USERNAME_COL + "=? AND " + PASSWORD_COL + "=?",
                new String[]{username, password},
                null, null, null);

        boolean userExists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return userExists;
    }
}