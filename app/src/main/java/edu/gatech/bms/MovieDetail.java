package edu.gatech.bms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Perform Detail page of movie
 */
public class MovieDetail extends AppCompatActivity {
    /**
     * API Key for connecting to RottenTomatoes API
     */
    private static final String API_KEY = "yedukp76ffytfuy24zsqk7f5";
    //private TextView SpId;
    //private ImageView Sp_image;
    /**
     * specific title of the movie
     */
    private TextView movieTitle;
    /**
     * specific year of the movie
     */
    private TextView movieYear;
    /**
     * specific genre of the movie
     */
    private TextView movieGenres;
    /**
     * specific runtime of the movie
     */
    private TextView movieRuntime;
    /**
     * specific synopsis of the movie
     */
    private TextView movieSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //specified_id = imported from upper cases
        final Bundle extras = getIntent().getExtras();
        //System.out.println(extras.getString("id"));
        final String movieId = extras.getString("id");
        setContentView(R.layout.activity_movie_detail);
        new RequestTask().execute("http://api.rottentomatoes.com/api/public/v1.0/movies/" + movieId + ".json?apikey=" + API_KEY);
        //Sp_image = (ImageView) findViewById(R.id.Sp_image);
        movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieYear = (TextView) findViewById(R.id.movieYear);
        movieGenres = (TextView) findViewById(R.id.movieGenres);
        movieRuntime = (TextView) findViewById(R.id.movieRuntime);
        movieSynopsis = (TextView) findViewById(R.id.movieSynopsis);
    }

    /**
     * Brings user to review/rating page when clicked
     * @param v current view
     */
    public void onViewReviewsClick(View v) {
        if (v.getId() == R.id.viewReviewB) {
            final Intent i = new Intent(this, MovieRating.class);
            i.putExtra("title", getIntent().getExtras().getString("title"));
            startActivity(i);
        }
    }

    private class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            final String urlString = params[0];
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
                    /*String url = jsonResponse.getString("detailed");
                    URL imgURL = new URL(url);
                    URLConnection conn = imgURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bm = BitmapFactory.decodeStream(bis);
                    Sp_image.setImageBitmap(bm);*/
                    final String title = jsonResponse.getString("title");
                    final String year = jsonResponse.getString("year");
                    final String runtime = jsonResponse.getString("runtime");
                    final String synopsis = jsonResponse.getString("synopsis");
                    movieTitle.setText("[ T I T L E ]\n" + title);
                    movieYear.setText("[ Y E A R ]\n" + year);
                    movieGenres.setText("[ G E N R E S ]\n" + jsonResponse.getString("genres"));
                    movieRuntime.setText("[ R U N T I M E ]\n" + runtime + " Min");
                    movieSynopsis.setText("[ S Y N O P S I S ]\n" + synopsis);

                } catch (JSONException e) {
                    Log.d("Test", "Failed to parse the JSON response!");
                } //catch (IOException e) {
                  //  Log.e("Test", "Error getting Bitmap", e);
                //}
            }
        }
    }


}
