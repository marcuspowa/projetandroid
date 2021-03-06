package com.example.remy.mmsongquizz.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.remy.mmsongquizz.R;

import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import utils.Logger;
import utils.SpotifyUtils;

public abstract class AbstractSpotifyActivity extends BaseActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {


    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be set to any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;
    private static String accessToken = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void authenticateSpotify(){
        if(accessToken == null){
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyUtils.CLIENT_ID, AuthenticationResponse.Type.TOKEN, SpotifyUtils.REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }
        else{
            onTokenReceived();
        }
    }

    public Player getmPlayer() {
        return mPlayer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                this.accessToken=response.getAccessToken();
                Logger.debug("AccessTokenSpotify : "+this.accessToken);
                onTokenReceived();
            } else if (response.getType() == AuthenticationResponse.Type.ERROR) {
                Logger.debug("AccessTokenSpotify LOGIN ERROR");

            }
            else{
                Logger.debug(response.getType().toString());
            }
        }
    }

    public void startPlayer(final String songId){
        Config playerConfig = new Config(this,this.accessToken, SpotifyUtils.CLIENT_ID);
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer.addConnectionStateCallback(AbstractSpotifyActivity.this);
                mPlayer.addPlayerNotificationCallback(AbstractSpotifyActivity.this);
                mPlayer.play(songId);
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.error("Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public abstract void onTokenReceived();

    @Override
    public void onLoggedIn() {
        Logger.debug( "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Logger.debug( "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Logger.debug( "Login failed "+error.getMessage());
    }

    @Override
    public void onTemporaryError() {
        Logger.debug("Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Logger.debug( "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Logger.debug( "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Logger.debug( "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}
