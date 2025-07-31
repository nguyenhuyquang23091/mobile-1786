package com.example.coursework.data.local.implementation;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.coursework.data.local.repository.AuthRepository;
import com.example.coursework.data.local.util.AuthListener;
import com.example.coursework.data.local.util.ConnectivityCheck;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepositoryImplementation implements AuthRepository {
    private AuthFireBaseRepository fireBaseRepository;
    private ConnectivityCheck connectivityCheck;

    public AuthRepositoryImplementation(Application application) {
        this.fireBaseRepository = new AuthFireBaseRepository();
        this.connectivityCheck = new ConnectivityCheck(application);
        this.connectivityCheck.RegisterNetworkCallback();
    }
    public boolean isConnected(){
        return connectivityCheck.isConnected();
    }
    @Override
    public LiveData<FirebaseUser> getCurrentData() {
        return null;
    }



    @Override
    public void signUp(String email, String password, AuthListener listener) {
        if(isConnected()){
            fireBaseRepository.createUser(email, password, listener );
        } else  {
            if (listener != null ){
                listener.onFailure("No internet Connection");
            }
        }
    }

    @Override
    public void signIn(String email, String password, AuthListener listener) {
        if (isConnected()) {
            fireBaseRepository.signInWithEmailAndPassword(email, password, listener);
        } else {
            if (listener != null) {
                listener.onFailure("No internet Connection");
            }
        }
    }

    @Override
    public void signOut() {

    }

    @Override
    public void resetPassword(String email) {

    }
}
