package com.pilgrimpass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.pilgrimpass.adapters.ImagePagerAdapter; // ඔබ මෙය පසුව සාදනු ඇත

import java.util.Arrays;
import java.util.List;

public class DaladaMaligawaInfoActivity extends AppCompatActivity {

    private TextView tvImageCounter;

    // දළදා මාළිගාව සඳහා පින්තූර
    private final List<Integer> daladaImages = Arrays.asList(
            R.drawable.dalada_maligawa_info_1, // උදාහරණ පින්තූරය 1
            R.drawable.dalada_maligawa_info_2, // උදාහරණ පින්තූරය 2
            R.drawable.dalada_maligawa_info_3,  // උදාහරණ පින්තූරය 3
            R.drawable.dalada_maligawa_info_4,
            R.drawable.dalada_maligawa_info_5
    );

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dalada_maligawa_info);

        // UI elements Initialize
        ViewPager2 viewPagerDaladaImages = findViewById(R.id.viewPagerDaladaImages);
        tvImageCounter = findViewById(R.id.tvImageCounter);
        MaterialButton btnViewOnMap = findViewById(R.id.btnViewOnMap);
        MaterialButton btnBookVisit = findViewById(R.id.btnBookVisit);

        //  ගැලරිය සඳහා ViewPager
        ImagePagerAdapter adapter = new ImagePagerAdapter(daladaImages);
        viewPagerDaladaImages.setAdapter(adapter);


        viewPagerDaladaImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvImageCounter.setText(String.format("%d of %d Images", position + 1, daladaImages.size()));
            }
        });

        if (!daladaImages.isEmpty()) {
            tvImageCounter.setText(String.format("1 of %d Images", daladaImages.size()));
        } else {
            tvImageCounter.setText("No images available");
        }

        // බොත්තම් සඳහා click listeners
        btnViewOnMap.setOnClickListener(v -> {
            // MapActivity වෙත යොමු
            Intent mapIntent = new Intent(DaladaMaligawaInfoActivity.this, MapActivity.class);
            // (අවශ්‍ය නම්: සිතියම දළදා මාළිගාවට කේන්ද්‍ර කිරීමට ස්ථාන දත්ත ලබා ගැනීම
            startActivity(mapIntent);
            Toast.makeText(this, "Opening Dalada Maligawa on Map", Toast.LENGTH_SHORT).show();
        });

        btnBookVisit.setOnClickListener(v -> {
            // BookingActivity වෙත යොමුව
            Intent bookingIntent = new Intent(DaladaMaligawaInfoActivity.this, BookingActivity.class);
            // (අවශ්‍ය නම්: වෙන්කර ගැනීමට ස්ථානය පිළිබඳ තොරතුරු ලබා ගැනීම  )
            // bookingIntent.putExtra("PLACE_TO_BOOK", "Dalada Maligawa");
            startActivity(bookingIntent);
            Toast.makeText(this, "Proceeding to Booking for Dalada Maligawa", Toast.LENGTH_SHORT).show();
        });
    }
}