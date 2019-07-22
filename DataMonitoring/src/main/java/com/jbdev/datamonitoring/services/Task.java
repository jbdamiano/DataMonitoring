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

        Iterator iterator = MainActivity.getInstance().db.getAllStatesSinceId((long)id).iterator();

        while(iterator.hasNext()) {
            State state = (State)iterator.next();

            try {
                ServerInterface.sendData(state.getLatitude(), state.getLongitude(),
                        state.getState(), state.getOperator(), state.getImsi(), state.getTrace(),
                        state.getProvider(), state.getSpeed(), state.getTimestamp(),
                        state.getReason(), state.getSubtype(), state.getId(), currentId);
                if (state.getId() > id) {
                    currentId = currentId + 1L;
                    id = state.getId();
                    Editor editor = settings.edit();
                    editor.putInt("id", id);
                    editor.putLong("currentId", currentId);
                    editor.commit();
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
