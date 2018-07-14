package com.sumon.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.binjar.prefsdroid.Preference;
import com.sumon.map.helper.SentInformation;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout mobileNumberTextInputLayout;
    EditText nameEditText;
    EditText mobileNumberEditText;
    Pattern mobileNumberPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mobileNumberTextInputLayout = (TextInputLayout) findViewById(R.id.til_mobile_number);
        nameEditText = (EditText) findViewById(R.id.et_name);
        mobileNumberEditText = (EditText) findViewById(R.id.et_mobile_number);
        mobileNumberPattern = Pattern.compile("^(?:\\+?88)?01[15-9]\\d{8}$");
        mobileNumberEditText.addTextChangedListener(mobileNumberTextWatcher);
    }

    TextWatcher mobileNumberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                mobileNumberTextInputLayout.setError(null);
                mobileNumberTextInputLayout.setErrorEnabled(false);
            } else {
                mobileNumberTextInputLayout.setError("মোবাইল নাম্বার খালি রাখা যাবে না");
                mobileNumberTextInputLayout.setErrorEnabled(true);
            }
        }
    };


    public void onCancelButtonCLick(View view) {
        finish();
    }

    public void onSaveButtonCLick(View view) {

        boolean isMobileNumberValid = false;
        String username = nameEditText.getText().toString();
        String mobileNumber = mobileNumberEditText.getText().toString();


        if (!mobileNumber.isEmpty()) {
            if (mobileNumberPattern.matcher(mobileNumber).matches()) {
                mobileNumberTextInputLayout.setError(null);
                mobileNumberTextInputLayout.setErrorEnabled(false);
                isMobileNumberValid = true;
            } else {
                mobileNumberTextInputLayout.setError("নাম্বার ভুল দিয়েছেন");
                mobileNumberTextInputLayout.setErrorEnabled(true);
                isMobileNumberValid = false;
            }
        } else {
            isMobileNumberValid = false;
            mobileNumberTextInputLayout.setError("মোবাইল নাম্বার খালি রাখা যাবে না");
            mobileNumberTextInputLayout.setErrorEnabled(true);
        }

        if (isMobileNumberValid) {
            Preference.putString(SentInformation.USERNAME, username);
            Preference.putString(SentInformation.MOBILE_NUMBER, mobileNumber);
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
