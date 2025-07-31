package com.example.coursework.data.local.util;

import com.example.coursework.data.local.entities.User;

import java.util.List;

public interface OnUsersReceivedListener {
    void onUsersReceived(List<User> users);
    void onUsersFailure(String error);
}