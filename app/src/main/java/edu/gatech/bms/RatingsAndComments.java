package edu.gatech.bms;

import com.backendless.BackendlessUser;

/**
 * Created by houqixuan on 2/26/16.
 */
public class RatingsAndComments {
    /**
     * comments
     */
    private String comments;
    /**
     * star rating
     */
    private double startRating;
    /**
     * movie name
     */
    private String movieName;
    /**
     * object id
     */
    private String objectId;

    /**
     * backendless user
     */
    private BackendlessUser user;

    /**
     * Moive Name setter
     * @param pmovieName a movie name
     */
    public void setMovieName(String pmovieName) {
        this.movieName = pmovieName;
    }
    /**
     * Moive Name getter
     * @return movieName a movie name
     */
    public String getMovieName() {
        return movieName;
    }
    /**
     * comments setter
     * @param pcomments the comments
     */
    public void setComments(String pcomments) {
        this.comments = pcomments;
    }
    /**
     * star rating setter
     * @param pstartRating a start rating
     */
    public void setStartRating(double pstartRating) {
        this.startRating = pstartRating;
    }
    /**
     * comments getter
     * @return comments comments
     */
    public String getComments() {
        return comments;
    }
    /**
     * star rating getter
     * @return startRating a star rating
     */
    public double getStartRating() {
        return startRating;
    }
    /**
     * user getter
     * @return user a user
     */
    public BackendlessUser getUser() {
        return user;
    }
    /**
     * user setter
     * @param puser a user
     */
    public void setUser(BackendlessUser puser) {
        this.user = puser;
    }

    /**
     * objectId getter
     * @return ObjectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * objectId getter
     * @param pobjectId objectId
     */
    public void setObjectId(String pobjectId) {
        this.objectId = pobjectId;
    }
}


