package com.example.android.googlebookslist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button findButton;
    private EditText searchBarEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findButton = (Button) findViewById(R.id.find_button);
        findButton.setOnClickListener(this);
    }

    public final boolean CheckInternetConn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        searchBarEditText = (EditText) findViewById(R.id.search_terms);

        if (CheckInternetConn(this)) {
            if (v.getId() == R.id.find_button) {

                String searchTermsString = formatSearchText(searchBarEditText.getText().toString());

                if (searchTermsString.equals("")) {
                    Toast.makeText(MainActivity.this, getString(R.string.search_terms_needed), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, BookActivity.class);
                    intent.putExtra("SEARCH_TERMS_STRING", searchTermsString);
                    startActivity(intent);
                }
            }
        }
    }

    private String formatSearchText(String string) {
        String trimmedString = string.trim();
        do {
            trimmedString = trimmedString.replace(" ", "+");
        } while (trimmedString.contains(" "));
        return trimmedString;
    }
}