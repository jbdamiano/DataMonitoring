package com.jbdev.datamonitoring.utils;

public class CurrentStateAndLocation {
    private Double longitude;
    private Double latitude;
    private Float speed;
    private String provider;
    private String state;
    private String reason;
    private String subtype;
    private String operator;
    private String imsi;

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

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        if (operator == null) {
            this.operator = "";
        } else {
            this.operator = operator;
        }
    }

    private static CurrentStateAndLocation instance = null;

    private CurrentStateAndLocation() {

    }

    public static CurrentStateAndLocation getInstance() {
        if (instance == null) {
            instance = new CurrentStateAndLocation();
        }
        return instance;

    }
}
