package com.sumon.map.helper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.binjar.prefsdroid.Preference;
import com.sumon.map.LoginActivity;
import com.sumon.map.MapsActivity;

/**
 * Created by hussa on 04-01-2018.
 */

public class CheckInfo extends AppCompatActivity{
    private String email;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        email = Preference.getString(SentInformation.USERNAME);
        phoneNumber = Preference.getString(SentInformation.MOBILE_NUMBER);
        if (email != null && phoneNumber != null) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}

