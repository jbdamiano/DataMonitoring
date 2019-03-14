package com.jbdev.datamonitoring.datas;

import com.jbdev.datamonitoring.database.model.State;

import java.util.ArrayList;
import java.util.List;

public class StatesCollection {
  static StatesCollection instamce = null;

  private List<State> statesList = new ArrayList<>();

  private StatesCollection() {

  }

  static public StatesCollection getInstamce() {
    if (instamce == null) {
      instamce = new StatesCollection();
    }
    return instamce;
  }

  public void addAll(List<State> data){
    statesList.addAll(data);
  }

  public List<State> getList() {
    return statesList;
  }

  public void clear() {
    statesList.clear();
  }

  public void add(int i, State n) {
    statesList.add(i, n);
    //send a refresh intent if needed

  }
}
