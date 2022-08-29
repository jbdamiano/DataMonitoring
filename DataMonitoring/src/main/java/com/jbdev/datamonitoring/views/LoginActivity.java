//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jbdev.datamonitoring.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.jbdev.datamonitoring.R;
import com.jbdev.datamonitoring.utils.ServerInterface;
import java.io.IOException;
import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {
    public static final long serialVersionUID = -5343268913807453561L;
    public EditText email;
    public Button login;
    public EditText password;

    public LoginActivity() {

        super();
    }



    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.login_activity);
        this.email = (EditText)this.findViewById(R.id.mail);
        this.password = (EditText)this.findViewById(R.id.password);
        this.login = (Button)this.findViewById(R.id.login);
        this.login.setOnClickListener(new OnClickListener() {
            public static final long serialVersionUID = 3224504583917613180L;


            public void onClick(View view) {
            Builder builder;
            try {
                ServerInterface.connect(email.getText().toString(), password.getText().toString());
                //ServerInterface.connect("jbdamiano@gmail.com", "test");
                LoginActivity.this.finish();
            } catch (JSONException var3) {
                builder = new Builder(LoginActivity.this);
                builder.setTitle("Error");
                builder.setMessage("User or password are incorrect");
                builder.setCancelable(true);
                builder.create().show();
            } catch (IOException var4) {
                builder = new Builder(LoginActivity.this);
                builder.setTitle("Error");
                builder.setMessage("User or password are incorrect");
                builder.setCancelable(true);
                builder.create().show();
            }

            }
        });
    }
    }

