package edu.gatech.bms;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MovieSearch extends AppCompatActivity {
    //TODO: consider using choicemode to make list items selectable, expand item to show more details + navigate to movie details
    //TODO: consider using expandablelistview
    //TODO: consider allowing user to select number of results (remember to check if number selected is possible however) (max is 50/page)
    /**
     * adapter works with listview to display search results
     */
    private ArrayAdapter<String> myAdapter;
    /**
     * api key for rottentomatoes
     */
    private static final String API_KEY = "yedukp76ffytfuy24zsqk7f5";
    /**
     * works with adapter to display results in list
     */
    private ListView searchList;
    /**
     * results to be taken from rottentomatoes in a search
     */
    private static final int RESULTS_PER_PAGE = 10;
    /**
     * maps movie titles to their ids
     */
    private Map<String, String> movieMap = new HashMap<>(RESULTS_PER_PAGE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);
        final SearchView search = (SearchView) findViewById(R.id.searchView1);
        search.setSubmitButtonEnabled(true);

        final OnQueryTextListener queryListener = new OnQueryTextListener() {
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

        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent i = new Intent(view.getContext(), MovieDetail.class);
                final String itemTitle = parent.getItemAtPosition(position).toString();
                final String itemId = movieMap.get(itemTitle);
                i.putExtra("title", itemTitle);
                i.putExtra("id", itemId);
                startActivity(i);
                //Intent i = new Intent(view.getContext(), MovieRating.class);
                //i.putExtra("title", parent.getItemAtPosition(position).toString());
                //startActivity(i);
            }

        };
        search.setOnQueryTextListener(queryListener);

        searchList = (ListView) findViewById(R.id.listView);
        searchList.setOnItemClickListener(itemClickListener);
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        searchList.setAdapter(myAdapter);
    }

    /**
     * searches for movies based on query and adds them to adapter to be displayed
     * @param query movie title to be found
     */
    private void search(String query) {
        myAdapter.clear();
        searchList.clearChoices();
        try {
            final SearchURL urlString = new SearchURL(query);
            final URL url = new URL(urlString.toString());
            final JSONObject obj = new GetMovies().execute(url).get();
            if (("0").equals(obj.getString("total"))) {
                //Toast.makeText(getBaseContext(), "No results were found", Toast.LENGTH_SHORT).show();
                myAdapter.add("No results were found");
                return;
            }
            final JSONArray movies = obj.getJSONArray("movies");
            for (int i = 0; i < movies.length(); i++) {
                final JSONObject movie = movies.getJSONObject(i);
                final String title = movie.getString("title");
                final String id = movie.getString("id");

                //consider changing key to id and adding other properties (title, year, posters) in future as needed
                movieMap.put(title, id);
                myAdapter.add(title);
            }
        } catch (InterruptedException e) {
            Log.e("Interrupted Exception", e.getMessage(), e);

        } catch (java.net.MalformedURLException e) {
            Log.e("MalformedURLException", e.getMessage(), e);

        } catch (org.json.JSONException e) {
            Log.e("JSONException", e.getMessage(), e);

        } catch (java.util.concurrent.ExecutionException e) {
            Log.e("ExecutionException", e.getMessage(), e);

        }
    }


    private class SearchURL {
        /**
         * start of url to use when acquiring json from rottentomatoes
         */
        private static final String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?";
        /**
         * output url formatted for search query (title), pageLimit, and which page of results to return
         */
        private String output;

        /**
         * sets up default search url with user defined title, pageLimit defined by constant RESULTS_PER_PAGE, and page 1
         * @param title title to be searched for
         */
        public SearchURL(String title) {
            this(title, RESULTS_PER_PAGE, 1);
        }

        /**
         * sets up search url with defined title, number of results per page, and page of results to get
         * @param title title to be searched for
         * @param pageLimit number of results per page
         * @param page page of results to get
         */
        public SearchURL(String title, int pageLimit, int page) {
            //pageLimit = number of results per page
            //use "t=" for more exact match
            //use "s=" for more general search
            final String newTitle = convertSpaces(title);
            //consider adding options for exact match and search
            output = URL + "q=" + newTitle + "&page_limit=" + pageLimit + "&page=" + page +
                    "&apikey=" + API_KEY;
        }

        @Override
        public String toString() {
            return output;
        }

        /**
         * converts spaces in title to + signs so that the url returns a valid result
         * @param title title to be processed
         * @return processed title
         */
        private String convertSpaces(String title) {
            String newOutput = "";
            for (int i = 0; i < title.length(); i++) {
                final char character = title.charAt(i);
                if (character == ' ') {
                    newOutput += "+";
                } else {
                    newOutput += character;
                }
            }
            return newOutput;
        }

    }

    private class GetMovies extends AsyncTask<URL, Void, JSONObject> {
        //<Params, Progress, Result>

        /**
         * For each url (though only one url should ever be entered usually), results are obtained
         * from the json file found in the url. The method returns true if the result is valid
         * (Reponse is True) and false if it is invalid.
         *
         * @param urls urls to be processed
         * @return whether url yields correct result
         */
        @Override
        protected JSONObject doInBackground(URL... urls) {
            //Consider moving json processing into helper method, add results to a List
            //Also consider using java json api
            //Consider comparing "Response" (from original object) to array length to see if there are more pages
            for (final URL url : urls) {
                try {
                    final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    final String jsonString = readStream(in);
                    urlConnection.disconnect();
                    return new JSONObject(jsonString);
                } catch (java.io.IOException e) {
                    Log.d("Rottentomatoes", "Could not make a successful request.");
                    Toast.makeText(getBaseContext(), "Could not make a successful request.", Toast.LENGTH_LONG).show();
                    Log.e("Exception", e.getMessage(), e);

                } catch (org.json.JSONException e) {
                    Log.d("json", "Could not process json.");
                    Toast.makeText(getBaseContext(), "Could not process json.", Toast.LENGTH_LONG).show();
                    Log.e("Exception", e.getMessage(), e);

                }
            }
            return null;
        }

        /**
         * converts InputStream to String
         * @param input InputStream to be converted
         * @return String of InputStream
         */
        private String readStream(InputStream input) {
            try {
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int i = input.read();
                while (i != -1) {
                    stream.write(i);
                    i = input.read();
                }
                return stream.toString();
            } catch (IOException e) {
                return "";
            }
        }
    }
}
