//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jbdev.datamonitoring.services;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.jbdev.datamonitoring.database.model.State;
import com.jbdev.datamonitoring.utils.ServerInterface;
import com.jbdev.datamonitoring.views.MainActivity;
import java.io.IOException;
import java.util.Iterator;
import java.util.TimerTask;
import org.json.JSONException;

public class Task extends TimerTask {
    public static Long currentId;
    public static Integer id;
    public static final long serialVersionUID = 6772898086054904522L;
    public static SharedPreferences settings;

    public Task() {

        super();

    }


    public void run() {

        Iterator var6 = MainActivity.getInstance().db.getAllStatesSinceId((long)id).iterator();

        while(var6.hasNext()) {
            State var2 = (State)var6.next();

            try {
                ServerInterface.sendData(var2.getLatitude(), var2.getLongitude(), var2.getState(), var2.getOperator(), var2.getImsi(), var2.getTrace(), var2.getProvider(), var2.getSpeed(), var2.getTimestamp(), var2.getReason(), var2.getSubtype(), var2.getId(), currentId);
                if (var2.getId() > id) {
                    Long var3 = currentId;
                    currentId = currentId + 1L;
                    id = var2.getId();
                    Editor var7 = settings.edit();
                    var7.putInt("id", id);
                    var7.putLong("currentId", currentId);
                    var7.commit();
                }
            } catch (JSONException var4) {
                var4.printStackTrace();
                break;
            } catch (IOException var5) {
                var5.printStackTrace();
                break;
            }
        }

    }
}
