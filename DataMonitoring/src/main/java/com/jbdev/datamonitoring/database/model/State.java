package com.jbdev.datamonitoring.database.model;

public class State {
    public static final String TABLE_NAME = "states";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_NETWORKTYPE = "networktype";
    public static final String COLUMN_OPERATOR = "operator";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_REASON = "reason";
    public static final String COLUMN_TRACE = "trace";
    public static final String COLUMN_PROVIDER = "provider";
    public static final String COLUMN_SPEED = "speed";
    private int id;
    private String state;
    private String subtype;
    private String timestamp;
    private Double longitude;
    private Double latitude;
    private String operator;
    private String reason;
    private String provider;
    private Float speed;
    private int trace;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_STATE + " TEXT,"
                    + COLUMN_REASON + " TEXT,"
                    + COLUMN_NETWORKTYPE + " TEXT,"
                    + COLUMN_OPERATOR + " TEXT,"
                    + COLUMN_LATITUDE + " REAL,"
                    + COLUMN_LONGITUDE + " REAL,"
                    + COLUMN_PROVIDER + " TEXT,"
                    + COLUMN_SPEED + " REAL,"
                    + COLUMN_TRACE + " INTEGER,"
                    + COLUMN_TIMESTAMP + " TEXT DEFAULT (strftime('%Y-%m-%dT%H:%M','now', 'localtime'))"
                    + ")";

    public State() {
    }

    public State(int id, String state, String reason, String subtype, String timestamp, String operator, Double latitude, Double longitude, int trace, String provider, Float speed) {
        this.id = id;
        this.state = state;
        this.reason = reason;
        this.subtype = subtype;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.operator = operator;
        this.trace = trace;
        this.provider = provider;
        this.speed = speed;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public String getProvider() {
        if (provider == null) {
            return "NDF";
        }

        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getTrace() {
        return trace;
    }

    public void setTrace(int trace) {
        this.trace = trace;
    }

    public int getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
