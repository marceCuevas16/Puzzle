package com.example.marce.luckypuzzle.io.callback;

/**
 * Created by marce on 07/04/17.
 */

public interface FBCallback {
    void onFBUserAlreadyExists(String userName,String imageURL);
    void onNewFacebookUser(String email);
}
