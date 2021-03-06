/*
 * Copyright (C) 2017 Angel Garcia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xengar.android.movieguide.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xengar.android.movieguide.utils.Constants.LOG;

/**
 * JSONLoader helper class.
 */
public class JSONLoader {

    private static final String MOVIE_BASE_URI = "http://api.themoviedb.org/3";
    private static final String TAG = JSONLoader.class.getSimpleName();

    public static JSONObject load(String relativeUri, String apiKey, String language) {
        return load(relativeUri,  apiKey, language, null);
    }

    public static JSONObject load(String relativeUri, String apiKey, String language,
                                  String appendToResponse) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr;

        try {
            Uri.Builder uriBuilder = Uri.parse(MOVIE_BASE_URI + relativeUri).buildUpon()
                    .appendQueryParameter("api_key", apiKey)
                    .appendQueryParameter("language", language);
            if(appendToResponse!=null) {
                uriBuilder.appendQueryParameter("append_to_response", appendToResponse);
            }
            Uri builtUri = uriBuilder.build();
            URL url = new URL(builtUri.toString());
            if (LOG) {
                Log.v(TAG, "Built URI " + builtUri.toString());
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                if (LOG) {
                    Log.v(TAG, "buffer - " + buffer.toString());
                }
            }

            if (buffer.length() == 0) {
                // Stream was empty.
                return null;
            }

            movieJsonStr = buffer.toString();
            JSONObject jObj = new JSONObject(movieJsonStr);
            if (LOG) {
                Log.v(TAG, "Movie JSON String: " + movieJsonStr);
                Log.v(TAG, "Movie JSON obj: " + jObj);
            }
            return jObj;

        } catch (IOException | JSONException e) {
            if (LOG) {
                Log.e(TAG, "Error ", e);
            }
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    if (LOG) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }
    }
}
