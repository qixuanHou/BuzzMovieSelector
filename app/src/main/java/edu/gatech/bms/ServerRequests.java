package edu.gatech.bms;

import android.app.ProgressDialog;
import android.content.Context;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

/**
 * Created by Thanh on 2/14/2016.
 */
public class ServerRequests<T> implements AsyncCallback<T> {
    /**
     * context to be set
     */
    private Context context;
    /**
     * displays message for progress
     */
    private ProgressDialog progressDialog;
    /**
     * displays message for Backendless fault
     */
    private ProgressDialog dialogHelper;

    @Override
    public void handleResponse(T t) {
        progressDialog.dismiss();
    }

    @Override
    public void handleFault(BackendlessFault backendlessFault) {
        progressDialog.dismiss();
        dialogHelper.setMessage("BackendLess Fault!");
    }

    /**
     * create a dialog anytime when the application request information from the server
     * @param context current context
     * @param loadingMessage loading message to display
     */
    public ServerRequests(Context context, String loadingMessage) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(loadingMessage);
    }

    /**
     * create a dialog anytime when the application request information from the server with default message
     * @param context current context
     */
    public ServerRequests(Context context) {
        this.context = context;
        progressDialog = ProgressDialog.show( context, "", "Loading...", true );
    }

    /**
     * show dialog box
     */
    public void showLoading() {
        progressDialog.show();
    }

    /**
     * hide dialog box
     */
    public void hideLoading() {
        progressDialog.dismiss();
    }

}
