//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jbdev.datamonitoring.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog.Builder;
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



    public void onCreate(Bundle var1) {
            super.onCreate(var1);
            this.setContentView(R.layout.login_activity);
            this.email = (EditText)this.findViewById(R.id.mail);
            this.password = (EditText)this.findViewById(R.id.password);
            this.login = (Button)this.findViewById(R.id.login);
            this.login.setOnClickListener(new OnClickListener() {
                public static final long serialVersionUID = 3224504583917613180L;


                public void onClick(View var1) {
                        Builder var5;
                        try {
                            //ServerInterface.connect(LoginActivity.this.email.getText().toString(), LoginActivity.this.password.getText().toString());
                            ServerInterface.connect("jbdamiano@gmail.com", "test");
                            LoginActivity.this.finish();
                        } catch (JSONException var3) {
                            var5 = new Builder(LoginActivity.this);
                            var5.setTitle("Error");
                            var5.setMessage("User or password are incorrect");
                            var5.setCancelable(true);
                            var5.create().show();
                        } catch (IOException var4) {
                            var5 = new Builder(LoginActivity.this);
                            var5.setTitle("Error");
                            var5.setMessage("User or password are incorrect");
                            var5.setCancelable(true);
                            var5.create().show();
                        }

                }
            });
        }
    }

