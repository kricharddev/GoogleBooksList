package com.example.android.googlebookslist;

/**
 * Created by kyle on 12/26/16.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = BookActivity.class.getSimpleName();

    private String searchURL = "";
    private String searchText;
    private int numberOfResults = 0;
    private int index = 0;
    private static final int MAX_HITS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        Intent intent = getIntent();

        searchText = intent.getStringExtra("SEARCH_TERMS_STRING");

        searchURL = getString(R.string.VOLUME_URL) + searchText + getString(R.string.INDEX_URL) + String.valueOf(index) + getString(R.string.MAX_RESULTS_URL) + String.valueOf(MAX_HITS);

        BookSearchAsyncTask task = new BookSearchAsyncTask();
        task.execute();
    }

    private void updateUi(final ArrayList<BookStrings> books) {

        final BookAdapter bookAdapter = new BookAdapter(this, books);

        ListView booksListView = (ListView) findViewById(R.id.list);
        if (booksListView != null) {
            booksListView.setAdapter(bookAdapter);
        }

        index = index - MAX_HITS;
        searchURL = getString(R.string.VOLUME_URL) + searchText + getString(R.string.INDEX_URL) + String.valueOf(index) + getString(R.string.MAX_RESULTS_URL) + String.valueOf(MAX_HITS);
    }

    private class BookSearchAsyncTask extends AsyncTask<URL, Void, ArrayList> {

        ProgressDialog asyncDialog = new ProgressDialog(BookActivity.this);

        @Override
        protected void onPreExecute() {

            asyncDialog.setMessage(getString(R.string.searching));
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<BookStrings> doInBackground(URL... urls) {
            URL url = createUrl(searchURL);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException thrown, problem making Http Request", e);
            }

            return extractFeatureFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList books) {
            if (books == null || books.size() == 0) {
                asyncDialog.dismiss();
                searchResultsDialogShower(getString(R.string.no_results));
            } else {
                updateUi(books);
                asyncDialog.dismiss();
            }
        }

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error with HttpRequest.  Error response code: " + urlConnection.getResponseCode());

                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException thrown, issue retrieving Google Books JSON", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<BookStrings> extractFeatureFromJson(String booksJSON) {

            final ArrayList<BookStrings> books = new ArrayList<>();
            if (TextUtils.isEmpty(booksJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(booksJSON);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

                numberOfResults = baseJsonResponse.optInt("totalItems");

                if (itemsArray.length() > 0) {

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject bookItem = itemsArray.getJSONObject(i);
                        JSONObject volumeInfo = bookItem.getJSONObject("volumeInfo");
                        JSONArray authors = volumeInfo.optJSONArray("authors");

                        String author;
                        String title = volumeInfo.optString("title");
                        String rating = volumeInfo.optString("averageRating");
                        String date = volumeInfo.optString("publishedDate");

                        author = volumeInfo.optString("authors");

                        BookStrings bookStrings = new BookStrings(title, author, rating, date);
                        books.add(bookStrings);
                    }

                    return new ArrayList<>(books);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Parsing JSON resulted in Error", e);
            }
            return null;
        }

        public void searchResultsDialogShower(String noResults) {

            LayoutInflater noView = LayoutInflater.from(BookActivity.this);
            final View searchDialogView = noView.inflate(R.layout.no_results, null);

            final AlertDialog searchResults = new AlertDialog.Builder(BookActivity.this).create();
            searchResults.setView(searchDialogView);
            searchResults.show();

            Button retryButton = (Button) searchDialogView.findViewById(R.id.retry_button);

            TextView messageTextView = (TextView) searchDialogView.findViewById(R.id.no_results);
            messageTextView.setText(noResults);

            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchResults.dismiss();
                    finish();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
    }
}