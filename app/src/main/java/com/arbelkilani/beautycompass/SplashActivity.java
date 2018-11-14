package com.arbelkilani.beautycompass;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.arbelkilani.beautycompass.global.AppUtils;
import com.arbelkilani.beautycompass.global.solar.SunriseSunsetCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000;

    private Location mLocation = null;
    private int mBackground = -1;
    private int mGreetingMessage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.iv_logo);
        doBounceAnimation(logo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        }, SPLASH_DURATION);
    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, 100, 0);
        animator.setInterpolator(new BounceInterpolator());
        animator.setStartDelay(500);
        animator.setDuration(2500);
        animator.start();
    }

    private void navigate() {
        startActivity(
                new Intent(SplashActivity.this, MainActivity.class)
                        .putExtra(MainActivity.BACKGROUND, mBackground)
                        .putExtra(MainActivity.GREETING_MESSAGE, mGreetingMessage)
                        .putExtra(MainActivity.LOCATION, mLocation));
        finish();
    }

    private void initialize() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            navigate();
        }

        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getApplicationContext());
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                            updateDayState(location);
                        } else {
                            mBackground = R.drawable.bg_default;
                            mGreetingMessage = R.string.standard_greeting;
                            navigate();
                        }


                    }
                });
    }

    private void updateDayState(Location location) {

        com.arbelkilani.beautycompass.global.solar.Location location1 =
                new com.arbelkilani.beautycompass.global.solar.Location(location.getLatitude(), location.getLongitude());
        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(location1, Calendar.getInstance().getTimeZone());

        Date sunrise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(Calendar.getInstance()).getTime();
        Date sunset = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(Calendar.getInstance()).getTime();
        Date current = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        Date noon = calendar.getTime();


        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);

        Date midNight = calendar1.getTime();

        if (current.after(midNight)) {
            mBackground = R.drawable.bg_night;
            mGreetingMessage = R.string.night_greeting;

            if (current.after(sunrise)) {
                mBackground = R.drawable.bg_morning;
                mGreetingMessage = R.string.morning_greeting;


                if (current.after(noon)) {
                    mBackground = R.drawable.bg_evening;
                    mGreetingMessage = R.string.afternoon_greeting;

                    if (current.after(sunset)) {
                        mBackground = R.drawable.bg_night;
                        mGreetingMessage = R.string.night_greeting;

                    }
                }
            }
        }

        navigate();
    }
}
