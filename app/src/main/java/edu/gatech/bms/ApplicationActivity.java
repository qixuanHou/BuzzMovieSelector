package edu.gatech.bms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessFault;

/**
 * Created by ${houqixuan} on ${2/3/16}.
 */

public class ApplicationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application);
        final String username = getIntent().getStringExtra("Username");
        final TextView logUser = (TextView)findViewById(R.id.Username);
        logUser.setText(username);

    }

    /**
     * Send the user to the profile screen
     * @param v the current view from application
     */
    public void onProfileClick(View v) {
        if (v.getId() == R.id.ProfileButton) {
            final Intent i = new Intent(this, ProfilePageActivity.class);
            final String username = getIntent().getStringExtra("Username");
            i.putExtra("Username", username);
            startActivity(i);

        }
    }

    /**
     * Log out the user screen and send them back to the main screen
     * @param v the current view from application
     */
    public void onBLogoutClick(View v) {
        Backendless.UserService.logout( new ServerRequests<Void>(this)
        {
            @Override
            public void handleResponse( Void response ) {
                super.handleResponse( response );
                startActivity(new Intent(ApplicationActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void handleFault( BackendlessFault fault ) {
                if( "3023".equals(fault.getCode()) ) {// Unable to logout: not logged in (session expired, etc.)
                    handleResponse(null);
                } else {
                    super.handleFault(fault);
                }
            }
        } );
    }
    /**
     * Click on DVD
     * @param v the DVD view
     */
    public void onDVDClick(View v) {
        if (v.getId() == R.id.SearchDVDButton) {
            final Intent i = new Intent(this,DVDSearch.class);
            startActivity(i);
        }
    }

    /**
     * Click on recent movies
     * @param v the recent movies view
     */
    public void onRecentMoviesClick(View v) {
        if (v.getId() == R.id.RecentMoviesButton) {
            final Intent i = new Intent(this,RecentMovies.class);
            startActivity(i);
        }
    }

    /**
     * Click on movie search
     * @param v the movie search view
     */
    public void onMovieSearchClick(View v) {
        if (v.getId() == R.id.MovieSearchButton) {
            final Intent i = new Intent(this, MovieSearch.class);
            startActivity(i);
        }
    }

    /**
     * Click on recommendation
     * @param v the recommendation view
     */
    public void onRecommendationClick(View v) {
        if (v.getId() == R.id.RecommendationButton) {
            final Intent i = new Intent(this, RecommendationOverall.class);
            startActivity(i);
        }
    }
}


