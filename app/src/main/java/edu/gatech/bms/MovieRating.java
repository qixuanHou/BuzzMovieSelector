package edu.gatech.bms;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import android.view.View.OnClickListener;
import android.app.Activity;

import java.util.List;

public class MovieRating extends Activity {
    /**
     * rating bar
     */
    private RatingBar ratingBar;
    /**
     * comments
     */
    private EditText comments;
    /**
     * submit button
     */
    private Button submit;
    /**
     * average rating
     */
    private TextView averageRating;
    /**
     * star rating
     */
    private double starRating = 0;
    /**
     * my adapter
     */
    private ArrayAdapter<String> myAdapter;
    /**
     * title
     */
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_rating);

        title = getIntent().getExtras().getString("title");

        final TextView movieName = (TextView) findViewById(R.id.movieName);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        comments = (EditText) findViewById(R.id.userComment);
        submit = (Button) findViewById(R.id.submit);
        averageRating = (TextView) findViewById(R.id.averageRating);
        movieName.setText(title);
        Backendless.initApp(this, BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);
        getAverageRating();
        addListenerOnRatingBar();
        addListenerOnButton();

        final ListView listComments = (ListView) findViewById(R.id.commentsList);
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listComments.setAdapter(myAdapter);
        myAdapter.clear();
        getComments();
    }

    /**
     * add listener to submit button
     */
    public void addListenerOnButton() {
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comments.getText().toString().isEmpty()) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(MovieRating.this).create();
                    alertDialog.setMessage("Please enter your comments");
                    alertDialog.show();
                } else {
                    saveNewRating(starRating, comments.getText().toString());
                    findMovie();

                }
            }
        });
    }
    /**
     * add listener to rating bar button
     */
    public void addListenerOnRatingBar() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar pratingBar, float rating, boolean fromUser) {
                starRating = rating;
            }
        });
    }

    /**
     * save new rating
     * @param pstarRating star rating
     * @param pcomments comments
     */
    public void saveNewRating(double pstarRating, String pcomments) {
        final RatingsAndComments aRating = new RatingsAndComments();
        aRating.setStartRating(pstarRating);
        aRating.setComments(pcomments);
        aRating.setMovieName(title);
        aRating.setUser(Backendless.UserService.CurrentUser());

        Backendless.Persistence.save(aRating, new AsyncCallback<RatingsAndComments>() {
            public void handleResponse(RatingsAndComments response) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MovieRating.this).create();
                alertDialog.setMessage("Thanks for your comments");
                alertDialog.show();
            }

            public void handleFault(BackendlessFault fault) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MovieRating.this).create();
                alertDialog.setMessage("Please try again");
                alertDialog.show();
            }
        });
    }

    /**
     * save new movie
     */
    public void saveNewMovie() {
        final Movie aMovie = new Movie();

        final String major = Backendless.UserService.CurrentUser().getProperty("major").toString();

        aMovie.setMovieName(title);

        aMovie.setTotal(1);
        aMovie.setAverage(starRating);
        aMovie.setTotal(major, 1);
        aMovie.setAverage(major, starRating);
        Backendless.Persistence.save(aMovie, new AsyncCallback<Movie>() {
            @Override
            public void handleResponse(Movie movie) {
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
            }
        });
    }


    /**
     * update movie
     * @param aMovie movie name
     */
    public void updateMovie(Movie aMovie) {
        final String major = Backendless.UserService.CurrentUser().getProperty("major").toString();
        final double n = 100.0;
        aMovie.setTotal(aMovie.getTotal() + 1);
        final double rating = Math.round((starRating + aMovie.getAverageRating(major) * aMovie.getTotal(major)) / (aMovie.getTotal(major) + 1) * n) / n;
        aMovie.setAverage(major, rating);
        aMovie.setTotal(major, aMovie.getTotal(major) + 1);
        aMovie.setAverage(Math.round((starRating + aMovie.getTotal() * aMovie.getAverage()) / (aMovie.getTotal() + 1) * n) / n);
        Backendless.Persistence.save(aMovie, new AsyncCallback<Movie>() {
            @Override
            public void handleResponse(Movie movie) {
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
            }
        });
    }

    /**
     * find movie
     */
    public void findMovie() {
        final String whereClause = "movieName = '" + processTitle(title) + "'";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Movie.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Movie>>() {
            @Override
            public void handleResponse(BackendlessCollection<Movie> movieBackendlessCollection) {
                final List<Movie> resultList = movieBackendlessCollection.getCurrentPage();
                if (resultList.size() == 0) {
                    saveNewMovie();
                } else {
                    updateMovie(resultList.get(0));
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
            }
        });

    }

    /**
     * process title
     * @param ptitle title name
     * @return string of title
     */
    private String processTitle(String ptitle) {
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < ptitle.length(); i++) {
            final char c = ptitle.charAt(i);
            if (c == '\'') {
                //apostrophes in title must be replaced with \' to be valid whereclause
                output.append('\'');
                output.append('\'');
            } else {
                output.append(c);
            }
        }
        return output.toString();
    }

    /**
     * get comments
     */
    public void getComments() {
        final String whereClause = "movieName = '" + processTitle(title) + "'";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(RatingsAndComments.class).find(dataQuery,
                new AsyncCallback<BackendlessCollection<RatingsAndComments>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<RatingsAndComments> ratingsAndCommentsBackendlessCollection) {
                        final List<RatingsAndComments> resultList = ratingsAndCommentsBackendlessCollection.getCurrentPage();
                        if (resultList.size() == 0) {
                            myAdapter.add("No comments.");
                        }
                        for (final RatingsAndComments result : resultList) {
                            myAdapter.add(result.getComments());
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                    }
                });

    }
    /**
     * calculate average rating
     */
    public void getAverageRating() {
        final String whereClause = "movieName = '" + processTitle(title) + "'";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Movie.class).find(dataQuery,
                new AsyncCallback<BackendlessCollection<Movie>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Movie> movieBackendlessCollection) {
                        final List<Movie> resultList = movieBackendlessCollection.getCurrentPage();
                        if (resultList.isEmpty()) {
                            averageRating.setText("Movie not yet rated");
                        } else {
                            final double average = resultList.get(0).getAverage();
                            averageRating.setText(Double.toString(average));
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                    }
                });
    }

}
