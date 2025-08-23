package com.pilgrimpass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private GoogleMap gMap;

    // Weather UI (from your XML)
    private LinearLayout weatherDisplayContainer;
    private TextView tvWeatherLocation, tvTemperature, tvWeatherCondition;

    // Settings (optional toggle to show/hide weather)
    private SharedPreferences settingsPrefs;
    private static final String APP_SETTINGS_PREFS_NAME = "AppSettings";
    private static final String KEY_SHOW_WEATHER_DETAILS = "showWeatherDetails";

    // Fixed location: Sri Dalada Maligawa
    private final LatLng maligawaLatLng = new LatLng(7.2933, 80.6350);

    // OpenWeatherMap API Key (already provided by you)
    private static final String OPEN_WEATHER_MAP_API_KEY = "a04cace73a8fd8a6aec920d842d9008c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // (Optional) login check you had earlier
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_map); // your XML

        // Bind weather panel views defined in XML
        weatherDisplayContainer = findViewById(R.id.weatherDisplayContainer);
        tvWeatherLocation = findViewById(R.id.tvWeatherLocation);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherCondition = findViewById(R.id.tvWeatherCondition);

        // Settings
        settingsPrefs = getSharedPreferences(APP_SETTINGS_PREFS_NAME, MODE_PRIVATE);

        // Map fragment (id = map in your XML)
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error: Map Fragment not found.", Toast.LENGTH_LONG).show();
            Log.e("MapActivity", "Map Fragment with ID R.id.map not found.");
        }

        // Button to center on Maligawa
        Button btnSelectMaligawa = findViewById(R.id.btnSelectMaligawa);
        btnSelectMaligawa.setOnClickListener(v -> {
            if (gMap != null) {
                gMap.clear();
                gMap.addMarker(new MarkerOptions()
                        .position(maligawaLatLng)
                        .title("Sri Dalada Maligawa"));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maligawaLatLng, 15));
                Toast.makeText(this, "Maligawa location centered!", Toast.LENGTH_SHORT).show();

                if (settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true)) {
                    fetchAndDisplayWeather(maligawaLatLng);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;

        // Start at Maligawa
        gMap.addMarker(new MarkerOptions().position(maligawaLatLng).title("Sri Dalada Maligawa, Kandy"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maligawaLatLng, 15));

        // My Location button (if permitted)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            try {
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
            } catch (SecurityException ignore) {}
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }

        // Weather: show/hide by setting
        boolean showWeather = settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true);
        if (showWeather) {
            weatherDisplayContainer.setVisibility(View.VISIBLE);
            fetchAndDisplayWeather(maligawaLatLng);
        } else {
            weatherDisplayContainer.setVisibility(View.GONE);
        }

        // Tap anywhere on map to update weather there
        gMap.setOnMapClickListener(latLng -> {
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(latLng).title("Selected Point"));
            if (settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true)) {
                fetchAndDisplayWeather(latLng);
            }
        });
    }

    // Fetch weather for a given LatLng
    @SuppressLint("SetTextI18n")
    private void fetchAndDisplayWeather(LatLng location) {
        if (location == null) {
            Toast.makeText(this, "Cannot fetch weather: location is null", Toast.LENGTH_SHORT).show();
            weatherDisplayContainer.setVisibility(View.GONE);
            return;
        }

        tvWeatherLocation.setText("Loading Weather...");
        tvTemperature.setText("--°C");
        tvWeatherCondition.setText("");
        weatherDisplayContainer.setVisibility(View.VISIBLE);

        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat="
                + location.latitude + "&lon=" + location.longitude
                + "&appid=" + OPEN_WEATHER_MAP_API_KEY + "&units=metric";

        new FetchWeatherTask().execute(weatherUrl);
    }

    // AsyncTask -> network call
    @SuppressLint("StaticFieldLeak")
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                    return sb.toString();
                } else {
                    Log.e("FetchWeatherTask", "HTTP " + conn.getResponseCode());
                    return null;
                }
            } catch (Exception e) {
                Log.e("FetchWeatherTask", "Error fetching weather data", e);
                return null;
            } finally {
                if (conn != null) conn.disconnect();
                if (reader != null) {
                    try { reader.close(); } catch (Exception ignore) {}
                }
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String json) {
            if (json == null) {
                Toast.makeText(MapActivity.this, "Failed to fetch weather data.", Toast.LENGTH_LONG).show();
                weatherDisplayContainer.setVisibility(View.GONE);
                return;
            }
            try {
                JSONObject obj = new JSONObject(json);
                if (obj.has("main") && obj.has("weather") && obj.has("name")) {
                    double temp = obj.getJSONObject("main").getDouble("temp");
                    String condition = obj.getJSONArray("weather").getJSONObject(0).getString("description");
                    String city = obj.getString("name");

                    DecimalFormat df = new DecimalFormat("#.#");
                    tvWeatherLocation.setText("Location: " + city);
                    tvTemperature.setText(df.format(temp) + "°C");
                    tvWeatherCondition.setText(condition);
                    weatherDisplayContainer.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MapActivity.this, "Weather data not available for this location.", Toast.LENGTH_SHORT).show();
                    weatherDisplayContainer.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.e("FetchWeatherTask", "JSON parse error", e);
                Toast.makeText(MapActivity.this, "Error parsing weather data.", Toast.LENGTH_SHORT).show();
                weatherDisplayContainer.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (gMap != null) {
                    try {
                        gMap.setMyLocationEnabled(true);
                        gMap.getUiSettings().setMyLocationButtonEnabled(true);
                        Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show();
                    } catch (SecurityException ignore) {}
                }
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                if (settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true)) {
                    tvWeatherLocation.setText("Location N/A");
                    tvTemperature.setText("--°C");
                    tvWeatherCondition.setText("Permission denied");
                }
            }
        }
    }
}
