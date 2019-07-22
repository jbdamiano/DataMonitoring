package com.jbdev.datamonitoring.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.jbdev.datamonitoring.database.model.State;

/**
 * Created by ravi on 15/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

  // Database Version
  private static final int DATABASE_VERSION = 6;

  // Database Name
  private static final String DATABASE_NAME = "notes_db";


  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {

    // create notes table
    db.execSQL(State.CREATE_TABLE);
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + State.TABLE_NAME);

    // Create tables again
    onCreate(db);
  }

  public long insertState(String state, String reason, String subtype, String operator, String imsi, Double latitude, Double longitude, int trace, String provider, Float speed) {
    // get writable database as we want to write data
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    // `id` and `timestamp` will be inserted automatically.
    // no need to add them
    values.put(State.COLUMN_STATE, state);
    values.put(State.COLUMN_REASON, reason);
    values.put(State.COLUMN_NETWORKTYPE, subtype);
    values.put(State.COLUMN_OPERATOR, operator);
    values.put(State.COLUMN_IMSI, imsi);
    values.put(State.COLUMN_LATITUDE, latitude);
    values.put(State.COLUMN_LONGITUDE, longitude);
    values.put(State.COLUMN_TRACE, trace);
    values.put(State.COLUMN_PROVIDER, provider);
    values.put(State.COLUMN_SPEED, speed);

    // insert row
    long id = db.insert(State.TABLE_NAME, null, values);

    // close db connection
    db.close();

    // return newly inserted row id
    return id;
  }


  public State getState(long id) {
    // get readable database as we are not inserting anything
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(State.TABLE_NAME,
        new String[]{State.COLUMN_ID, State.COLUMN_STATE, State.COLUMN_REASON,
                State.COLUMN_NETWORKTYPE, State.COLUMN_TIMESTAMP, State.COLUMN_OPERATOR,
                State.COLUMN_IMSI, State.COLUMN_LONGITUDE, State.COLUMN_LATITUDE,
                State.COLUMN_TRACE, State.COLUMN_PROVIDER, State.COLUMN_SPEED},
        State.COLUMN_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);

    if (cursor != null)
      cursor.moveToFirst();

    // prepare note object
    State note = new State(
        cursor.getInt(cursor.getColumnIndex(State.COLUMN_ID)),
        cursor.getString(cursor.getColumnIndex(State.COLUMN_STATE)),
        cursor.getString(cursor.getColumnIndex(State.COLUMN_REASON)),
        cursor.getString(cursor.getColumnIndex(State.COLUMN_NETWORKTYPE)),
        cursor.getString(cursor.getColumnIndex(State.COLUMN_TIMESTAMP)),
        cursor.getString(cursor.getColumnIndex(State.COLUMN_OPERATOR)),
        cursor.getString(cursor.getColumnIndex(State.COLUMN_IMSI)),
        cursor.getDouble(cursor.getColumnIndex(State.COLUMN_LATITUDE)),
        cursor.getDouble(cursor.getColumnIndex(State.COLUMN_LONGITUDE)),
        cursor.getInt(cursor.getColumnIndex(State.COLUMN_TRACE)),
        cursor.getString(cursor.getColumnIndex(State.COLUMN_PROVIDER)),
        cursor.getFloat(cursor.getColumnIndex(State.COLUMN_SPEED))
            );

    // close the db connection
    cursor.close();

    return note;
  }

  public List<State> getAllStates() {
    List<State> states = new ArrayList<>();

    // Select All Query
    String selectQuery = "SELECT  * FROM " + State.TABLE_NAME + " ORDER BY " +
        State.COLUMN_ID + " DESC";

    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
      do {
        State state = new State();
        state.setId(cursor.getInt(cursor.getColumnIndex(State.COLUMN_ID)));
        state.setState(cursor.getString(cursor.getColumnIndex(State.COLUMN_STATE)));
        state.setReason(cursor.getString(cursor.getColumnIndex(State.COLUMN_REASON)));
        state.setSubtype(cursor.getString(cursor.getColumnIndex(State.COLUMN_NETWORKTYPE)));
        state.setTimestamp(cursor.getString(cursor.getColumnIndex(State.COLUMN_TIMESTAMP)));
        state.setLatitude(cursor.getDouble(cursor.getColumnIndex(State.COLUMN_LATITUDE)));
        state.setLongitude(cursor.getDouble(cursor.getColumnIndex(State.COLUMN_LONGITUDE)));
        state.setOperator(cursor.getString(cursor.getColumnIndex(State.COLUMN_OPERATOR)));
        state.setImsi(cursor.getString(cursor.getColumnIndex(State.COLUMN_IMSI)));
        state.setTrace(cursor.getInt(cursor.getColumnIndex(State.COLUMN_TRACE)));
        state.setProvider(cursor.getString(cursor.getColumnIndex(State.COLUMN_PROVIDER)));
        state.setSpeed(cursor.getFloat(cursor.getColumnIndex(State.COLUMN_SPEED)));
        states.add(state);
      } while (cursor.moveToNext());
    }

    // close db connection
    db.close();

    // return notes list
    return states;
  }

  public List<State> getAllStatesSinceId(long id) {
      List<State> states = new ArrayList<>();
      StringBuilder selectQueryBuild = new StringBuilder();
      selectQueryBuild.append("SELECT  * FROM states WHERE id > ");
      selectQueryBuild.append(id);
      selectQueryBuild.append(" ORDER BY ");
      selectQueryBuild.append("id");
      String selectQuery = selectQueryBuild.toString();
      SQLiteDatabase db = this.getWritableDatabase();
      Cursor cursor = db.rawQuery(selectQuery, (String[])null);
      if (cursor.moveToFirst()) {
        do {
          State state = new State();
          state.setId(cursor.getInt(cursor.getColumnIndex("id")));
          state.setState(cursor.getString(cursor.getColumnIndex("state")));
          state.setReason(cursor.getString(cursor.getColumnIndex("reason")));
          state.setSubtype(cursor.getString(cursor.getColumnIndex("networktype")));
          state.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
          state.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
          state.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
          state.setOperator(cursor.getString(cursor.getColumnIndex("operator")));
          state.setImsi(cursor.getString(cursor.getColumnIndex("imsi")));
          state.setTrace(cursor.getInt(cursor.getColumnIndex("trace")));
          state.setProvider(cursor.getString(cursor.getColumnIndex("provider")));
          state.setSpeed(cursor.getFloat(cursor.getColumnIndex("speed")));
          states.add(state);
        } while(cursor.moveToNext());
      }

      db.close();
      return states;
  }

  public int getStateCount() {
    String countQuery = "SELECT  * FROM " + State.TABLE_NAME;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(countQuery, null);

    int count = cursor.getCount();
    cursor.close();


    // return count
    return count;
  }

  public void deleteAllState() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(State.TABLE_NAME, null, null);
    db.close();
  }

}