package edu.gatech.bms;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

public class UserList extends AppCompatActivity {
    /**
     * Works with listview to store users found
     */
    private ArrayAdapter<String> myAdapter;
    /**
     * current page of results
     */
    private int currentPage = 1;
    /**
     * max page of results found, -1 if maxPage not yet reached/found
     */
    private int maxPage = -1;
    /**
     * stores literal "status"
     */
    private static final String STATUS = "status";
    /**
     * length of each status + spaces between username and status; used to trim username from listview
     */
    private static final int STATUS_LENGTH = 8;
    /**
     * results displayed per page
     */
    private static final int PAGE_SIZE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        final ListView userList = (ListView) findViewById(R.id.userList);
        final SearchView search = (SearchView) findViewById(R.id.userSearch);
        search.setSubmitButtonEnabled(true);
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        search.setOnQueryTextListener(queryListener);

        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String str = parent.getItemAtPosition(position).toString();

                final String user = str.substring(0, str.length() - STATUS_LENGTH);
                final BackendlessUser foundUser = BackendlessFunctions.findUserSync(user);
                toggleStatus(foundUser);
            }

        };
        userList.setOnItemClickListener(itemClickListener);

        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        userList.setAdapter(myAdapter);
        displayUsers();
    }

    /**
     * clears adapter and adds searched for user to adapter
     * @param query user to search for
     */
    private void search(String query) {
        myAdapter.clear();
        findUser(query);
        //TODO: add ability for partial match
        //TODO: disable next page and previous page buttons after search (weird things happen)
    }

    /**
     * finds and displays (adds to adapter) user based on query
     * @param user user to find
     */
    private void findUser(String user) {
        final BackendlessUser foundUser = BackendlessFunctions.findUserSync(user);
        if (foundUser == null) {
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
        } else {
            final String name = (String) foundUser.getProperty("name");
            final String status = (String) foundUser.getProperty(STATUS);

            myAdapter.add(name + ": " + status);
        }
    }

    /**
     * displays all users in system (limited by current page)
     */
    private void displayUsers() {
        final String whereClause = "type = 'user'";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery(whereClause);
        displayUsersHelper(dataQuery, PAGE_SIZE, currentPage);
    }

    /**
     * adds all users in system to adapter to be displayed
     * @param dataQuery Backendless data query to use to search database (should only be used in displayUsers())
     * @param pageSize number of results per page
     * @param page page of results to get
     */
    private void displayUsersHelper(BackendlessDataQuery dataQuery, final int pageSize, final int page) {
        //TODO: consider caching results for faster load time
        dataQuery.setPageSize(pageSize);
        dataQuery.setOffset((page - 1) * pageSize);
        Backendless.Persistence.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> usersBackendlessCollection) {
                final List<BackendlessUser> resultList = usersBackendlessCollection.getCurrentPage();
                if (resultList.size() == 0) {
                    if (currentPage == 1) {
                        maxPage = currentPage;
                    } else {
                        maxPage = currentPage - 1;
                        //cancels out currentPage increase from nextPage
                        currentPage -= 1;
                    }
                    Toast.makeText(getBaseContext(), "No users found", Toast.LENGTH_SHORT).show();
                } else {
                    myAdapter.clear();
                    for (final BackendlessUser user : resultList) {
                        final String name = (String) user.getProperty("name");
                        final String status = (String) user.getProperty(STATUS);
                        myAdapter.add(name + ": " + status);
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("backendlessFault", backendlessFault.getMessage());
            }
        });

    }

    /**
     * Displays next page of results on user list
     * @param v current view
     */
    public void userListNext(View v) {
        if (v.equals(findViewById(R.id.userListNextButton))) {
            if (maxPage == -1 || currentPage < maxPage) {
                //maxPage == -1 if maxPage not found yet
                currentPage += 1;
                displayUsers();
            } else {
                Toast.makeText(this, "You are currently on the last page of results", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Displays previous page of results on user list
     * @param v Current view
     */
    public void userListPrevious(View v) {
        if (v.equals(findViewById(R.id.userListPreviousButton))) {
            if (currentPage == 1) {
                Toast.makeText(this,
                        "You are currently on the first page of results", Toast.LENGTH_SHORT).show();
            } else {
                currentPage -= 1;
                displayUsers();
            }
        }
    }

    /**
     * Refreshes user list
     * @param v current view
     */
    public void userListRefresh(View v) {
        if (v.getId() == (R.id.refreshUserList)) {
            refreshList();
        }
    }

    /**
     * returns admin to first page of results
     */
    private void refreshList() {
        maxPage = -1;
        currentPage = 1;
        myAdapter.clear();
        displayUsers();
    }

    /**
     * refreshes current page of results
     */
    private void refreshPage() {
        myAdapter.clear();
        displayUsers();
    }

    /**
     * toggles status of specified user from banned/locked to active or vice versa
     * @param user user to be toggled
     */
    private void toggleStatus(BackendlessUser user) {
        final String status = (String) user.getProperty(STATUS);
        if (("active").equals(status)) {
            user.setProperty(STATUS, "banned");
        } else {
            //if locked or banned
            user.setProperty(STATUS, "active");
            LoginActivity.clearLoginAttempts((String) user.getProperty("name"));
        }
        new ToggleStatusHelper().execute(user);
    }
    private class ToggleStatusHelper extends AsyncTask<BackendlessUser, Void, Void> {
        //<Params, Progress, Result>
        @Override
        protected Void doInBackground(BackendlessUser... users) {
            for (final BackendlessUser user :users) {
                Backendless.UserService.update(user);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void a) {
            refreshPage();
        }

    }

}