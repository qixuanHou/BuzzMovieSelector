package edu.gatech.bms;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import java.util.List;



public class RecommendationOverall extends AppCompatActivity {
    /**
     * adapter works with listview to display search results
     */
    private ArrayAdapter<String> myAdapter;

    /**
     * the minimum average rating to recommend
     */
    private static final int THRESHOLD = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_overall);

        Backendless.initApp(this, BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);

        final Spinner dropDown = (Spinner) findViewById(R.id.filterMajor);
        final String[] dropDownOptions = {"All", "MATH", "CS", "ECE", "CHEM", "CSE", "ISYE"};
        final ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dropDownOptions);
        dropDown.setAdapter(dropDownAdapter);

        final AdapterView.OnItemSelectedListener selectListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String major = parent.getItemAtPosition(position).toString();
                myAdapter.clear();
                if (("All").equals(major)) {
                    getRatingByMovie();
                } else {
                    getRatingByMovie(major);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        dropDown.setOnItemSelectedListener(selectListener);

        final ListView recommendMovies = (ListView) findViewById(R.id.recommendmoviesoverall);
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        recommendMovies.setAdapter(myAdapter);
        myAdapter.clear();
        getRatingByMovie();

    }

    /**This method displays movies with overall rating greater than 4.0 in a descending order as
     * recommendation
     *
     */
    private void getRatingByMovie() {
        final String columnName = "average";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        final QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption(columnName + " DESC");
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Persistence.of(Movie.class).find(dataQuery,
                new AsyncCallback<BackendlessCollection<Movie>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Movie> movieBackendlessCollection) {
                        final List<Movie> resultList = movieBackendlessCollection.getCurrentPage();
                        for (final Movie result : resultList) {
                            final double rating = result.getAverage();
                            if (rating >= THRESHOLD) {
                                myAdapter.add(result.getMovieName() + ",   " + result.getAverage());
                            }
                        }
                        if (myAdapter.isEmpty()) {
                            //no movies with ratings meeting requirements
                            myAdapter.add("No movies found");
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                    }
                });
    }

    /**This method request top average rating movies from Backendless database and displays them in
     * descending oder as recommendation by major
     * @param major the major users select from the drop-down menu, and we give recommendation based
     *              on that
     */
    private void getRatingByMovie(final String major) {
        final String columnName = major + "_average";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        final QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption(columnName + " DESC");
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Persistence.of(Movie.class).find(dataQuery,
                new AsyncCallback<BackendlessCollection<Movie>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Movie> movieBackendlessCollection) {
                        final List<Movie> resultList = movieBackendlessCollection.getCurrentPage();
                        for (final Movie result : resultList) {
                            final double rating = result.getAverageRating(major);
                            if (rating >= THRESHOLD) {
                                myAdapter.add(result.getMovieName() + ",   " + result.getAverageRating(major));
                            }
                        }
                        if (myAdapter.isEmpty()) {
                            //no movies with ratings meeting requirements
                            myAdapter.add("No movies found");
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                    }
                });
    }

}
