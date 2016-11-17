package edu.gatech.bms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.StringTokenizer;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Minimum username length
     */
    private static final int USERNAME_LENGTH = 5;
    /**
     * A string that is requried to use in Backendless
     */
    private static final String TAG = "RegisterActivity";

    /**
     * The Edit Text require user to input their Georgia Tech StudentID
     */
    private EditText inputStudentID;

    /**
     * The Edit Text require user to input their email address
     */
    private EditText inputEmail;

    /**
     * The Edit Text require user to input their account desire Username
     */
    private EditText inputUserName;

    /**
     * The Edit Text require user to input their account password
     */
    private EditText inputPassword;

    /**
     * Button alow user to push and register new account
     */
    private Button registerB;

    /**
     * Button allow user to login instead of registering if they are already registered
     */
    private Button alreadyRegisterB;
    //private Button RegisterCancelB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Backendless.initApp(this, BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);

        inputStudentID = (EditText) findViewById(R.id.inputStudentID);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputUserName = (EditText) findViewById(R.id.inputUserName);
        inputPassword = (EditText) findViewById(R.id.inputPassWord);
        registerB = (Button) findViewById(R.id.registerB);

        registerB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerB:
                signUp();
                break;
        }
    }

    /**
     * CHeck if the information input is valided.
     * Create a Signup dialog when user log in.
     */
    public void signUp() {
        Log.d(TAG, "Signup");
        if (!validate()) {
            onSignUpFailed();
            return;
        }
        registerB.setEnabled(false);

        final String name = inputUserName.getText().toString();
        final String email = inputEmail.getText().toString();
        final String studentID = inputStudentID.getText().toString();
        final String password = inputPassword.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_AppBarOverlay);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account For You...");
        progressDialog.show();

        final ServerRequests<BackendlessUser> registrationCallback = createRegistrationCallback();
        registrationCallback.showLoading();
        registerUser(email, studentID, name, password, registrationCallback);

       /* new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        OnSignUpSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
        */
    }

    /**
     * create a message when the app contacts from backend
     *
     * @return a message attached together when user registers
     */
    private ServerRequests<BackendlessUser> createRegistrationCallback() {
        return new ServerRequests<BackendlessUser>(this, "Sending Registration Request");
    }

    /**
     * What the application will do when the registration success
     */
    public void onSignUpSuccess() {
        registerB.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    /**
     * What the application will do when the registration fails.
     */
    public void onSignUpFailed() {
        Toast.makeText(getBaseContext(), "Sign Up Failed", Toast.LENGTH_LONG).show();
        registerB.setEnabled(true);
    }

    /**
     * Check if the information user enters is valid or not
     *
     * @return a boolean if all the information is entered correctly
     */
    public boolean validate() {
        boolean valid = true;

        final String name = inputUserName.getText().toString();
        final String email = inputEmail.getText().toString();
        //final String studentID = inputStudentID.getText().toString();
        final String password = inputPassword.getText().toString();

        if (name.isEmpty() || name.length() < USERNAME_LENGTH) {
            inputUserName.setError("Please enter at least 5 character");
            valid = false;
        } else {
            inputUserName.setError(null);
        }
        //email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (email.isEmpty() || !validateEmailAddress(email)) {
            inputEmail.setError("Please enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty()) {
            inputPassword.setError("Please enter your password");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }

    /**
     * Check if the email address given by the user is valid or not
     * @param email the email address that we need to check
     * @return return true if the email address satisfy all the requirement, else return false
     */
    public static boolean validateEmailAddress(String email) {
        if (email == null) {
            return false;
        }

        if (!email.contains("@")) {
            return false;
        }

        if (!email.contains(".")) {
            return false;
        }
        if ((email.charAt(email.length() - 1)) == '.'
                || email.contains(" ") || email.contains(",")) {
            return false;
        }

        if (!testDomain(email)) {
            return false;
        }
        return true;
    }

    /**
     * check if the domain of the mail address is valid or not.
     * @param email the email address that we need to check
     * @return true if the domain is valid, else return false
     */
    private static boolean testDomain(String email) {
        final StringTokenizer st = new StringTokenizer(email, ".");
        String lastOrder = null;

        while(st.hasMoreTokens()) {
            lastOrder = st.nextToken();
        }

        return lastOrder.length() >= 2;
    }
    /**
     * register user into the backend
     *
     * @param email                email of the user
     * @param username             username of the user
     * @param password             password of the user
     * @param registrationCallback the text displays when the user login
     * @param studentID            the student ID of user
     */
    public void registerUser(String email, String studentID, String username, String password, AsyncCallback<BackendlessUser> registrationCallback) {
        final BackendlessUser user = new BackendlessUser();
        user.setEmail(email);
        user.setProperty("name", username);
        user.setPassword(password);
        user.setProperty("studentID", studentID);


        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                onSignUpSuccess();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                onSignUpFailed();
            }
        });
    }

    /**
     * Cancel the registration and send the user to the main screen
     *
     * @param v the current view from application
     */
    public void onBRegisterCancel(View v) {
        if (v.getId() == R.id.registerCancelB) {
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    /**
     * Send the user to the login screen if user has already registered.
     *
     * @param v the current view from application
     */
    public void onBLoginRegister(View v) {
        if (v.getId() == R.id.alreadyRegisterB) {
            final Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

}

