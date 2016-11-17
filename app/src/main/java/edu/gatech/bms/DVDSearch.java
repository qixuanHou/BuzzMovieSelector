package edu.gatech.bms;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DVDSearch extends Activity {
    /**
     * API key to access the rottentomato database
     */
    private static final String API_KEY = "yedukp76ffytfuy24zsqk7f5";

    /**
     * The total number of movie displays in each page
     */
    private static final int MOVIE_PAGE_LIMIT = 10;

    /**
     * The total number of page per activity
     */
    private static final int PAGE_LIMIT = 1;

    /**
     * The map that will store all movie information get from rottentomato
     */
    private Map<String, String> movieMap = new HashMap<>(MOVIE_PAGE_LIMIT);

    /**
     * The array adapter that will take in all the input from the user
     */
    private ArrayAdapter<String> myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dvdsearch);

        final ListView listMovies = (ListView) findViewById(R.id.listMovies);
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listMovies.setAdapter(myAdapter);

        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Note: item may not be String if we change to more complex adapter
                //change ApplicationActivity.class to movie activity when implemented
                final Intent i = new Intent(view.getContext(), MovieRating.class);
                final String tempTitle = parent.getItemAtPosition(position).toString();
                final String tempId = movieMap.get(tempTitle);
                i.putExtra("title", tempTitle);
                i.putExtra("id", tempId);
                startActivity(i);
            }

        };
        listMovies.setOnItemClickListener(itemClickListener);
        new RequestTask().execute("http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/new_releases.json?"
                        + "q=" + "&page_limit=" + MOVIE_PAGE_LIMIT + "&page=" + PAGE_LIMIT + "&apikey=" + API_KEY
        );
    }



    private class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            @SuppressWarnings("deprecation")final HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                final StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (java.io.IOException e) {
                Log.d("IOException", e.getMessage());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    final JSONObject jsonResponse = new JSONObject(response);
                    final JSONArray movies = jsonResponse.getJSONArray("movies");
                    for (int i = 0; i < movies.length(); i++) {
                        final JSONObject movie = movies.getJSONObject(i);
                        final String tempTitle = movie.getString("title");
                        final String tempId = movie.getString("id");
                        myAdapter.add(tempTitle);
                        movieMap.put(tempTitle, tempId);
                    }
                } catch (JSONException e) {
                    Log.d("Test", "Failed to parse the JSON response!");
                }
            }
        }
    }
}