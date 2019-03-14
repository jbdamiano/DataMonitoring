package com.jbdev.datamonitoring.utils;

public class CurrentStateAndLocation {
  public Double longitude;
  public Double latitude;
  public String state;
  public String reason;
  public String subtype;
  public String operator;
  public String imsi;

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
