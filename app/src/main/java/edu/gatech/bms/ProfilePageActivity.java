package edu.gatech.bms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ProfilePageActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * text edit box where user can modify their email
     */
    private EditText userEmailET;
    /**
     * text edit box where user can modify their studentId
     */
    private EditText userStudentIdET;
    /**
     * text edit box where user can modify their first name
     */
    private EditText userFirstNameTv;
    /**
     * text edit box where user can modify their last name
     */
    private EditText userLastNameTv;

    /**
     * the user of the current logged-in user
     */
    private String major;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Backendless.initApp(this, BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);


        final BackendlessUser user = Backendless.UserService.CurrentUser();
        final String thisUsername = (String) user.getProperty("name");
        final String thisUserEmail = user.getEmail();
        final String thisUserStudentId = (String) user.getProperty("studentID");
        final String thisUserFirstName = (String) user.getProperty("firstName");
        final String thisUserLastName = (String) user.getProperty("lastName");
        final String thisUserMajor = (String) user.getProperty("major");


        final TextView userNameTv = (TextView) findViewById(R.id.displayUserName);
        userNameTv.setText(thisUsername);

        userEmailET = (EditText) findViewById(R.id.displayEmail);
        userEmailET.setText(thisUserEmail);

        userStudentIdET = (EditText) findViewById(R.id.displayStudentId);
        userStudentIdET.setText(thisUserStudentId);

        userFirstNameTv = (EditText) findViewById(R.id.displayFirstName);
        userFirstNameTv.setText(thisUserFirstName);

        userLastNameTv = (EditText) findViewById(R.id.displayLastName);
        userLastNameTv.setText(thisUserLastName);

        final Button saveButton = (Button)findViewById(R.id.saveButton);
        final Button cancelButton = (Button)findViewById(R.id.CancelButton);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        final Spinner dropDown = (Spinner) findViewById(R.id.profileMajor);
        final String[] dropDownOptions = {"MATH", "CS", "ECE", "CHEM", "CSE", "ISYE"};
        final ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dropDownOptions);
        dropDown.setAdapter(dropDownAdapter);
        final int currMajorPosition = dropDownAdapter.getPosition(thisUserMajor);
        dropDown.setSelection(currMajorPosition);

        final AdapterView.OnItemSelectedListener selectListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                major = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        };

        dropDown.setOnItemSelectedListener(selectListener);

    }

    /**this method handles the next view based on the buttons user might click
     * @param v the current view of the app
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CancelButton:
                onProfileCancel();
                break;
            case R.id.saveButton:
                onClickSave();
                break;
        }

    }

    /**
     * direct the application back to the main stage after logging in
     */
    public void onProfileCancel() {
        final Intent i = new Intent(this, ApplicationActivity.class);
        startActivity(i);
    }

    /**
     * save the modification that users have made and store them back to the Backendless database
     */
    public void onClickSave() {

        //ServerRequests<BackendlessUser> loginCallback = createLoginCallback();
        final BackendlessUser user = Backendless.UserService.CurrentUser();


        user.setProperty("firstName", userFirstNameTv.getText().toString());
        user.setProperty("lastName", userLastNameTv.getText().toString());
        user.setProperty("major", major);
        user.setProperty("studentID", userStudentIdET.getText().toString());
        user.setProperty("email", userEmailET.getText().toString());

        Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {

            public void handleResponse(BackendlessUser tempUser) {
//                System.out.println("success!");
            }

            public void handleFault(BackendlessFault fault) {
//                System.out.println("Failure: " + fault.getCode());
            }
        });

        final Intent i = new Intent(this, ApplicationActivity.class);
        startActivity(i);
    }



    /**
     * create a message when the app contacts from backend
     * @return a message attached together when user registers
     */
    public ServerRequests<BackendlessUser> createLoginCallback() {
        return new ServerRequests<BackendlessUser>( this, "Sending Save Request"){};
    }
}
