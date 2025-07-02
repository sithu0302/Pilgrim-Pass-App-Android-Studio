package com.pilgrimpass; // ඔබගේ package name එකට මෙය වෙනස් කරන්න

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private GoogleMap gMap;

    // UI elements for address search
    private EditText addressInput;

    // UI elements for weather display
    private LinearLayout weatherDisplayContainer;
    private TextView tvWeatherLocation;
    private TextView tvTemperature;
    private TextView tvWeatherCondition;

    // SharedPreferences for reading settings
    private SharedPreferences settingsPrefs;
    private static final String APP_SETTINGS_PREFS_NAME = "AppSettings";
    private static final String KEY_SHOW_WEATHER_DETAILS = "showWeatherDetails";

    private final LatLng maligawaLatLng = new LatLng(7.2933, 80.6350); // Dalada Maligawa location

    // OpenWeatherMap API Key
    private static final String OPEN_WEATHER_MAP_API_KEY = "a04cace73a8fd8a6aec920d842d9008c"; // ඔබ ලබා දුන් API Key එක මෙතනට එක් කර ඇත.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login status
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_map); // Ensure this points to your activity_map.xml

        // Initialize UI elements for address search
        addressInput = findViewById(R.id.editTextAddress);
        Button showLocationButton = findViewById(R.id.buttonShowLocation);
        showLocationButton.setOnClickListener(this::onShowLocationClick); // Set listener here

        Button btnSelectMaligawa = findViewById(R.id.btnSelectMaligawa);

        // Initialize weather UI elements
        weatherDisplayContainer = findViewById(R.id.weatherDisplayContainer);
        tvWeatherLocation = findViewById(R.id.tvWeatherLocation);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherCondition = findViewById(R.id.tvWeatherCondition);

        // Initialize SharedPreferences to read settings
        settingsPrefs = getSharedPreferences(APP_SETTINGS_PREFS_NAME, MODE_PRIVATE);

        // Get the SupportMapFragment and request the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); // Changed ID to R.id.map to match your MainActivity2
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // This calls onMapReady when the map is ready
        } else {
            Toast.makeText(this, "Error: Map Fragment not found.", Toast.LENGTH_LONG).show();
            Log.e("MapActivity", "Map Fragment with ID R.id.map not found.");
        }

        btnSelectMaligawa.setOnClickListener(v -> {
            if (gMap != null) {
                gMap.clear();
                gMap.addMarker(new MarkerOptions()
                        .position(maligawaLatLng)
                        .title("Sri Dalada Maligawa"));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maligawaLatLng, 15));
                Toast.makeText(this, "Maligawa location centered!", Toast.LENGTH_SHORT).show();

                // If weather details are enabled in settings, fetch for Maligawa
                if (settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true)) {
                    fetchAndDisplayWeather(maligawaLatLng);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;

        // Set initial camera position to Maligawa and add a marker
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(maligawaLatLng, 15));
        gMap.addMarker(new MarkerOptions().position(maligawaLatLng).title("Sri Dalada Maligawa, Kandy"));

        // Check and request location permissions if needed for 'My Location' button
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Check settings and display weather if enabled
        boolean showWeather = settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true);
        if (showWeather) {
            weatherDisplayContainer.setVisibility(View.VISIBLE);
            // Fetch weather for the initial location (Maligawa in Kandy)
            fetchAndDisplayWeather(maligawaLatLng);
        } else {
            weatherDisplayContainer.setVisibility(View.GONE);
        }

        // Optional: Listen for map clicks to update weather for clicked location
        gMap.setOnMapClickListener(latLng -> {
            gMap.clear(); // Clear existing markers
            gMap.addMarker(new MarkerOptions().position(latLng).title("Selected Point"));
            Toast.makeText(MapActivity.this, "Map Clicked: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();

            // If weather details are enabled, fetch for the clicked location
            if (settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true)) {
                fetchAndDisplayWeather(latLng);
            }
        });
    }

    // New method for showLocationButton click, similar to MainActivity2's onClick
    private void onShowLocationClick(View view) {
        String location = addressInput.getText().toString();
        if (location.isEmpty()) {
            Toast.makeText(MapActivity.this, "Enter address", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(location, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(latLng).title(location));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                // Fetch weather for the searched location
                if (settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true)) {
                    fetchAndDisplayWeather(latLng);
                }

            } else {
                Toast.makeText(MapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MapActivity.this, "Error finding location", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to fetch and display weather details using AsyncTask for background operation
    @SuppressLint("SetTextI18n")
    private void fetchAndDisplayWeather(LatLng location) {
        if (location == null) {
            Toast.makeText(this, "Cannot fetch weather: location is null", Toast.LENGTH_SHORT).show();
            weatherDisplayContainer.setVisibility(View.GONE);
            return;
        }

        // Removed the "Please set your OpenWeatherMap API Key" Toast as the key is now set.
        // weatherDisplayContainer.setVisibility(View.GONE); // This line also removed as it would hide the container prematurely.

        // Show a loading state
        tvWeatherLocation.setText("Loading Weather...");
        tvTemperature.setText("--°C");
        tvWeatherCondition.setText("");
        weatherDisplayContainer.setVisibility(View.VISIBLE); // Ensure it's visible while loading

        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.latitude +
                "&lon=" + location.longitude + "&appid=" + OPEN_WEATHER_MAP_API_KEY + "&units=metric";

        new FetchWeatherTask().execute(weatherUrl);
    }

    // AsyncTask to perform network operation in the background
    @SuppressLint("StaticFieldLeak")
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String weatherJsonStr;

            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                    reader = new BufferedReader(inputStream);
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    weatherJsonStr = buffer.toString();
                } else {
                    Log.e("FetchWeatherTask", "Error response code: " + urlConnection.getResponseCode());
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    StringBuilder errorBuffer = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorBuffer.append(errorLine).append("\n");
                    }
                    Log.e("FetchWeatherTask", "Error Stream: " + errorBuffer);
                    return null;
                }
            } catch (Exception e) {
                Log.e("FetchWeatherTask", "Error fetching weather data", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final Exception e) {
                        Log.e("FetchWeatherTask", "Error closing stream", e);
                    }
                }
            }
            return weatherJsonStr;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String jsonResult) {
            if (jsonResult != null) {
                try {
                    JSONObject forecastJson = new JSONObject(jsonResult);
                    if (forecastJson.has("main") && forecastJson.has("weather") && forecastJson.has("name")) {
                        JSONObject main = forecastJson.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        String condition = forecastJson.getJSONArray("weather").getJSONObject(0).getString("description");
                        String cityName = forecastJson.getString("name");

                        DecimalFormat df = new DecimalFormat("#.#");
                        tvWeatherLocation.setText("Location: " + cityName);
                        tvTemperature.setText(df.format(temperature) + "°C");
                        tvWeatherCondition.setText(condition);
                        weatherDisplayContainer.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MapActivity.this, "Weather data not available for this location.", Toast.LENGTH_SHORT).show();
                        weatherDisplayContainer.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Log.e("FetchWeatherTask", "Error parsing weather JSON", e);
                    Toast.makeText(MapActivity.this, "Error parsing weather data.", Toast.LENGTH_SHORT).show();
                    weatherDisplayContainer.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(MapActivity.this, "Failed to fetch weather data. Check internet/API Key.", Toast.LENGTH_LONG).show();
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (gMap != null) {
                    try {
                        gMap.setMyLocationEnabled(true);
                        gMap.getUiSettings().setMyLocationButtonEnabled(true);
                        Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this, "Location permission denied. Cannot show current location.", Toast.LENGTH_SHORT).show();
                if (settingsPrefs.getBoolean(KEY_SHOW_WEATHER_DETAILS, true)) {
                    tvWeatherLocation.setText("Location N/A");
                    tvTemperature.setText("--°C");
                    tvWeatherCondition.setText("Permission denied");
                }
            }
        }
    }
}