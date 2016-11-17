package edu.gatech.bms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by houqixuan on 2/3/16.
 */

public class LoginActivity extends Activity {
    /**
     * EditText box where user can type his user name
     */

    private EditText usernameTxt;
    /**
     * EditText box where user can enter his password
     */
    private EditText passwordTxt;

    /**
     * use user name as key and login attempts as value to keep track of the attempts user tries
     * to log in using different account
     */
    private static Map<String, Integer> userLogins = new HashMap<>();

    /**
     * Max number of trials user can make to attempt to log in before his account being banned
     */
    private static final int MAXATTEMPT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        usernameTxt = (EditText) findViewById(R.id.usernameTxt);
        passwordTxt = (EditText) findViewById(R.id.passwordTxt);

        final Button loginFacebookButton = (Button) findViewById(R.id.loginFacebookButton);
        loginFacebookButton.setOnClickListener(createLoginWithFacebookButtonListener());

        final Button loginTwitterButton = (Button) findViewById(R.id.loginTwitterButton );
        loginTwitterButton.setOnClickListener(createLoginWithTwitterButtonListener());
    }



    /**
     * The method will check if the user email and password match with
     * the information in the backend. IF it matches, the user can go to the main activity page
     * Otherwise, user will be kicked out.
     *
     * @param v the current view from application
     */
    public void onBLoginClick(View v) {
        final String tempUsername = usernameTxt.getText().toString();
        final AlertDialog.Builder alterInvalidUsernamePassword = new AlertDialog.Builder(this);
        final BackendlessUser user = BackendlessFunctions.findUserSync(tempUsername);
        if (user == null) {
            //TODO: consider changing to more general invalid user message with loading popup, as message is technically minor security risk
            //if user does not exist
            alterInvalidUsernamePassword.setMessage("User does not exist").create().show();
            return;
        }
        //if user exists
        final String status = (String) user.getProperty("status");
        if (("banned").equals(status)) {
            alterInvalidUsernamePassword.setMessage("Account is banned").create().show();
            return;
        } else if (("locked").equals(status)) {
            alterInvalidUsernamePassword.setMessage("Account is locked due to too many failed logins").create().show();
            return;
        }


        final ServerRequests<BackendlessUser> loginCallback = createLoginCallback();
        loginCallback.showLoading();
        Backendless.UserService.login(tempUsername, passwordTxt.getText().toString(), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                final String type = (String) backendlessUser.getProperty("type");
                if (("admin").equals(type)) {
                    final Intent i = new Intent(getBaseContext(), AdminFeatures.class);
                    startActivity(i);
                } else {
                    final Intent i = new Intent(getBaseContext(), ApplicationActivity.class);
                    i.putExtra("Username", tempUsername);
                    startActivity(i);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //code 3003 is invalid login or password
                //code 3036 is account is locked (if using backendless lock feature)
                //TODO: consider changing HashMap implementation to Backendless implementation (with new attempts column). Downside:slow. Upside: closing app doesn't reset login attempts
                final Integer loginAttempts = userLogins.get(tempUsername);
                if (loginAttempts == null) {
                    userLogins.put(tempUsername, 0);
                } else {
                    userLogins.put(tempUsername, loginAttempts + 1);
                    if (loginAttempts.equals(MAXATTEMPT)) {
                        alterInvalidUsernamePassword.setMessage("Account is locked due to too many failed logins").create().show();
                        final BackendlessUser user = BackendlessFunctions.findUserSync(tempUsername);
                        user.setProperty("status", "locked");
                        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {

                            public void handleResponse(BackendlessUser tempUser) {
                            }

                            public void handleFault(BackendlessFault fault) {
                            }
                        });
                        loginCallback.hideLoading();
                        return;
                    }
                }
                alterInvalidUsernamePassword.setMessage("Invalid Information").create().show();
                loginCallback.hideLoading();
            }
        });
    }





        /**
         * Clears login attempts for a specified user (used when admin unlocks user account)
         *
         * @param user user to clear login attempts of
         */
    public static void clearLoginAttempts(String user) {
        //user should always exist in HashMap as method should only be called in admin user list when it is locked
        userLogins.put(user, 0);
    }

    /**
     * Log the user into the application
     *
     * @param username      username of the account
     * @param password      password of the account
     * @param loginCallback create the text to display when user login
     */
    public void loginUser(String username, String password, AsyncCallback<BackendlessUser> loginCallback) {
        //Backendless.UserService.login(usernameTxt.getText().toString(), passwordTxt.getText().toString(), loginCallback);
    }

    /**
     * Return the user to the main screen
     *
     * @param v the current view from application
     */
    public void onBCancelClick(View v) {
        if (v.getId() == R.id.CancelB) {
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    /**
     * @param v the current view from application
     */
    public void onRegisterNew(View v) {
        if (v.getId() == R.id.registerNow) {
            final Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
    }

    /**
     * @param v the current view from application
     */
    public void onBforgotPassword(View v) {
        if (v.getId() == R.id.recoveryPassword) {
            final Intent i = new Intent(this, RecoverPassword.class);
            startActivity(i);
        }
    }

    /**
     * create a message when the app contacts from backend
     *
     * @return a message attached together when user registers
     */
    public ServerRequests<BackendlessUser> createLoginCallback() {
        return new ServerRequests<BackendlessUser>(this, "Sending Login Request") {
        };
    }


    /**
     * This method links our app to facebook login page, which allows users to log in with facebook
     * account
     * @param loginCallback a package consist of user information that will be packed into a Async
     *                      call and send it to the Facebook API
     */
    public void loginFacebookUser(AsyncCallback<BackendlessUser> loginCallback) {
        final Map<String, String> fieldsMappings = new HashMap<>();
        fieldsMappings.put("name", "name");
        Backendless.UserService.loginWithFacebook(this, null, fieldsMappings, Collections.<String>emptyList(), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                startActivity(new Intent(getBaseContext(), ApplicationActivity.class));
                finish();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                onSignUpFailed();
            }
        });
    }


    /**this method creates a view for facebook log in
     * @return a response consist of all user information and send it to Facebook API
     */
    public View.OnClickListener createLoginWithFacebookButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ServerRequests<BackendlessUser> loginCallback = createLoginCallback();
                loginCallback.showLoading();
                loginFacebookUser(loginCallback);
            }
        };
    }

    /**
     * this method creates a view for twitter log in
     * @return a response consist of all user information and send it to Twitter API
     */
    public View.OnClickListener createLoginWithTwitterButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ServerRequests<BackendlessUser> loginCallback = createLoginCallback();
                loginCallback.showLoading();
                loginTwitterUser(loginCallback);
            }
        };
    }

    /**
     * This method handles sign-up failure, which directs users back to the initial page when they
     * failt to sign up
     */
    public void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Account already Exists", Toast.LENGTH_LONG).show();
        //RegisterB.setEnabled(true);
        final Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * This method links our app to twitter login page, which allows users to log in with twitter
     * account
     * @param loginCallback a package consist of user information that will be packed into a Async
     *                      call and send it to the Twitter API
     */
    public void loginTwitterUser(AsyncCallback<BackendlessUser> loginCallback) {
        Map<String, String> twitterFieldsMapping = new HashMap<>();
        twitterFieldsMapping.put("name", "name");
        Backendless.UserService.loginWithTwitter(this, twitterFieldsMapping, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                startActivity(new Intent(getBaseContext(), ApplicationActivity.class));
                finish();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                onSignUpFailed();
            }
        });
    }

}

