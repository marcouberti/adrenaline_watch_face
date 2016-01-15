package com.marcouberti.sonicboomwatchface.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marco on 05/06/15.
 */
public class WatchFaceWearableListenerService extends WearableListenerService {

    private static GoogleApiClient googleApiClient;

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final String LAST_KNOW_GPS_POSITION = "/gps_position";
    private String nodeId;

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d("WEAR", "Message received: " + messageEvent);
        nodeId = messageEvent.getSourceNodeId();

        //Toast.makeText(this, "Command received", Toast.LENGTH_LONG).show();
        if (messageEvent.getPath().contains(LAST_KNOW_GPS_POSITION)) {

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        LocationManager locationManager = (LocationManager) WatchFaceWearableListenerService.this.getSystemService(Context.LOCATION_SERVICE);
                        String locationProvider = LocationManager.NETWORK_PROVIDER;
                        // Or use LocationManager.GPS_PROVIDER
                        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                        if(lastKnownLocation != null) {
                            reply(LAST_KNOW_GPS_POSITION, lastKnownLocation.getLatitude() + "_" + lastKnownLocation.getLongitude());
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }.start();
        }
    }

    private void reply(final String path, String message) {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .build();

        if(message == null) message = "{}";

        Log.v("HANDSET SERVICE", "In reply()");
        Log.v("HANDSET SERVICE", "Path: " + path);

        if (googleApiClient != null && !(googleApiClient.isConnected() || googleApiClient.isConnecting()))
            googleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);

        Wearable.MessageApi.sendMessage(googleApiClient, nodeId, path, message.getBytes()).await();
        googleApiClient.disconnect();
    }

}
