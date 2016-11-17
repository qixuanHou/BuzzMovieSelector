package edu.gatech.bms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;

public class AdminFeatures extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_features);
    }

    /**
     * Brings user to screen listing users if user list button pressed
     * @param v current view
     */
    public void onBUserList(View v) {
        if (v.getId() == R.id.userListButton) {
            final Intent i = new Intent(this, UserList.class);
            startActivity(i);
        }
    }

    /**
     * Log out the admin screen and send them back to the main screen
     * @param v the current view from application
     */
    public void onBAdminLogout(View v) {
        Backendless.UserService.logout( new ServerRequests<Void>(this) {
            @Override
            public void handleResponse( Void response ) {
                super.handleResponse(response);
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            }

            @Override
            public void handleFault( BackendlessFault fault ) {
                if( ("3023").equals(fault.getCode()) ) { // Unable to logout: not logged in (session expired, etc.)
                    handleResponse(null);
                } else {
                    super.handleFault(fault);
                }
            }
        } );
    }
}
