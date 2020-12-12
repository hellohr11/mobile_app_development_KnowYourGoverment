package com.example.goverment;

import java.io.Serializable;

public class SocialMediaChannel implements Serializable {

    private String twitterPageID;
    private String facebookPageID;
    private String youtubePageID;

    public SocialMediaChannel() {
    }

    public SocialMediaChannel(String twitterPageID, String facebookPageID, String youtubePageID) {
        this.twitterPageID = twitterPageID;
        this.facebookPageID = facebookPageID;
        this.youtubePageID = youtubePageID;
    }



    public String getTwitterPageID() {
        return twitterPageID;
    }

    public void setTwitterPageID(String twitterPageID) {
        this.twitterPageID = twitterPageID;
    }

    public String getFacebookPageID() {
        return facebookPageID;
    }

    public void setFacebookPageID(String facebookPageID) {
        this.facebookPageID = facebookPageID;
    }

    public String getYoutubePageID() {
        return youtubePageID;
    }

    public void setYoutubePageID(String youtubePageID) {
        this.youtubePageID = youtubePageID;
    }

    @Override
    public String toString() {
        return "Facebook "+ facebookPageID
                +"\n twitter "+ twitterPageID
                +"\n youtube "+ youtubePageID;

    }


}