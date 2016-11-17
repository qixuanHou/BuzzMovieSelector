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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RecentMovies extends Activity {
    /**
     * API Key for connecting to RottenTomatoes API
     */
    private static final String API_KEY = "yedukp76ffytfuy24zsqk7f5";
    /**
     * list of view limited on one page.
     */
    private static final int MOVIE_PAGE_LIMIT = 10;
    /**
     * make a map of title - ID matching.
     */
    private Map<String, String> movieIDs = new HashMap<String, String>();

    //private EditText sBox;
    //private Button sButton;

    /**
     * make an Adapter
     */
    private ArrayAdapter<String> myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView recent;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recentmovies);
        recent = (ListView) findViewById(R.id.recent);
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        recent.setAdapter(myAdapter);
        new RequestTask().execute("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/opening.json?apikey="
                + API_KEY + "&q=" + "&limit=" + MOVIE_PAGE_LIMIT);

        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Note: item may not be String if we change to more complex adapter
                //change ApplicationActivity.class to movie activity when implemented
                final Intent i = new Intent(view.getContext(), MovieDetail.class);
                //findID(parent.getItemAtPosition(position).toString());
                final String itemTitle = parent.getItemAtPosition(position).toString();
                final String mid = findID(itemTitle);
                i.putExtra("id", mid);
                i.putExtra("title", itemTitle);
                startActivity(i);
            }

        };
        recent.setOnItemClickListener(itemClickListener);

    }

    /**
     * Find movie's ID on RottenTomatoes database
     * @param title movie's title.
     * @return return ID
     */
    private String findID (String title) {
        //System.out.println(movieIDs.get(title)); //it works
        return movieIDs.get(title);
    }
    private class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            final String urlString = params[0];
            //StringBuilder sb = new StringBuilder();
            //String responseString = null;
            try {
                final URL url = new URL(urlString);
                final HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                final int statusCode = con.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String sb = "";
                    String line = reader.readLine();
                    while (line != null) {
                        sb = sb + line + "\n";
                        line = reader.readLine();
                    }
                    reader.close();
                    return sb;
                    //JSONObject obj = new JSONObject(String.valueOf(sb));
                    //responseString = obj.getString("Title");
                }
            } catch (IOException e) {
                Log.d("Test", "Couldn't make a successful request!");
            }
            return null;
        }


        // if the request above completed successfully, this method will 
        // automatically run so you can do something with the response
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    // convert the String response to a JSON object,
                    // because JSON is the response format Rotten Tomatoes uses
                    final JSONObject jsonResponse = new JSONObject(response);

                    // fetch the array of movies in the response
                    final JSONArray movies = jsonResponse.getJSONArray("movies");

                    // add each movie's title to listview
                    for (int i = 0; i < movies.length(); i++) {
                        final JSONObject movie = movies.getJSONObject(i);
                        final String tempTitle = movie.getString("title");
                        final String tempId = movie.getString("id");
                        myAdapter.add(tempTitle);
                        movieIDs.put(tempTitle, tempId);
                    }
                } catch (JSONException e) {
                    Log.d("Test", "Failed to parse the JSON response!");
                }
            }
        }
    }
}