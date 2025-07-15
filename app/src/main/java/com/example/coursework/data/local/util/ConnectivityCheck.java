package com.example.coursework.data.local.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;


import java.util.logging.Level;

public class ConnectivityCheck {
        private Context context;
        private boolean isConnected = false;

    public ConnectivityCheck(Context context){
            this.context = context;
        }
    public boolean isConnected() {
        return isConnected;
    }
        public void RegisterNetworkCallback(){

            try{
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkRequest nr = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED).build();
                connectivityManager.registerNetworkCallback(nr, new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {

                        Log.d("ConnectivityCheck", "onAvailable");
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                            if(capabilities != null){
                                boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                                boolean hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                                boolean hasMobile = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                                isConnected = hasInternet;
                                Log.d("ConnectivityCheck", "Internet: " + hasInternet + ", WiFi: " + hasWifi + ", Cellular: " + "Mobile" + hasMobile
                                + "Connected " + isConnected
                                );
                            }
                    }
                    @Override
                    public void onLost(@NonNull Network network) {
                        Log.d("ConnectivityCheck", "onLost");
                        isConnected = false;
                    }
                });

            } catch (Exception e){
                Log.d(String.valueOf(Level.WARNING), e.getMessage(), e);
            }
        }

}
