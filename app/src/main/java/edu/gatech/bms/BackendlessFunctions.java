package edu.gatech.bms;

import android.os.AsyncTask;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

/**
 * Created by Alex on 3/6/16.
 */
public final class BackendlessFunctions {
    /**
     * stores output whose value depends on which method is called
     */
    private static BackendlessUser output;

    /**
     * private empty constructor as this is a utility class
     */
    private BackendlessFunctions() {

    }
    /**
     * Returns BackendlessUser based on its name property or null if user not found.
     * WARNING: Method is asynchronous, may be unreliable if results are used soon.
     * @param user user's name property
     * @return BackendlessUser or null if user not found
     */
    public static BackendlessUser findUser (String user){
        final String whereClause = "name = '" + user + "'";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery(whereClause);
        Backendless.Persistence.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> usersBackendlessCollection) {
                final List<BackendlessUser> resultList = usersBackendlessCollection.getData();
                if (resultList.size() == 0) {
                    output = null;
                } else {
                    output = resultList.get(0);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("backendlessFault", backendlessFault.getMessage());
            }
        });
        return output;
    }

    /**
     * Synchronous version of findUser (uses AsyncTask)
     * @param user username to find
     * @return BackendlessUser, or null if user not found
     */
    public static BackendlessUser findUserSync (String user) {
        BackendlessUser foundUser = null;
        try {
            foundUser = new SearchUsers().execute(user).get();
        } catch (java.lang.InterruptedException e) {
            Log.e("Exception", e.getMessage(), e);
        } catch (java.util.concurrent.ExecutionException e) {
            Log.e("Exception", e.getMessage(), e);
        }
        return foundUser;
    }
    private static class SearchUsers extends AsyncTask<String, Void, BackendlessUser> {
        //<Params, Progress, Result>
        @Override
        protected BackendlessUser doInBackground(String... users) {
            //return BackendlessFunctions.findUserSync(users[0]);
            final String whereClause = "name = '" + users[0] + "'";
            final BackendlessDataQuery query = new BackendlessDataQuery(whereClause);
            final BackendlessCollection<BackendlessUser> result = Backendless.Persistence.of(BackendlessUser.class).find(query);
            final List<BackendlessUser> resultList = result.getData();
            if (resultList.size() == 0) {
                output = null;
            } else {
                output = resultList.get(0);
            }
            return output;
        }
    }
}
