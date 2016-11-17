package edu.gatech.bms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class RecoverPassword extends AppCompatActivity implements View.OnClickListener {


    /**
     * The Edit Text view that require user to input their username
     */
    private EditText requireUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        Backendless.initApp(this, BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);

        //EditText requireEmail = (EditText) findViewById(R.id.requireEmail);
        requireUsername = (EditText) findViewById(R.id.requireUsername);
        final Button recoverButton = (Button) findViewById(R.id.recoverButton);
        recoverButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recoverButton:
                onrestorePassword();
                break;
        }
    }

    /**
     * This method will basically call backendless database to send to the user's email the new
     * password and handle any case that Backendless failed to answer this call.
     */
    public void onrestorePassword() {
        final String name = requireUsername.getText().toString();
        Backendless.UserService.restorePassword(name, new AsyncCallback() {
            @Override
            public void handleResponse(Object o) {
                Toast.makeText(getBaseContext(), "An email with a new password " +
                        "has been sent to the user's email", Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(getBaseContext(), "Recover Password Failed", Toast.LENGTH_LONG).show();
            }
        });

        final Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Return the user to the main screen
     * @param v the current view from application
     */
    public void onRCanelClick(View v) {
        if (v.getId() == R.id.cancel) {
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}
