package com.arbelkilani.beautycompass;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.arbelkilani.beautycompass.global.AppUtils;
import com.arbelkilani.beautycompass.global.solar.SunriseSunsetCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static int REQUEST_CHECK_GOOGLE_SETTINGS = 0x99;
    public static final String BACKGROUND = "background";
    public static final String GREETING_MESSAGE = "greeting_message";
    public static final String LOCATION = "location";

    private View mMainView;
    private TextView mGreetingTextView;

    private TextView mPositionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
    }

    /**
     *
     */
    private void initializeView() {
        mMainView = findViewById(R.id.main_layout);
        mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));

        mGreetingTextView = findViewById(R.id.tv_greeting);
        mGreetingTextView.setText(R.string.standard_greeting);
        findViewById(R.id.iv_location).setOnClickListener(this);

        mPositionTextView = findViewById(R.id.tv_position);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(BACKGROUND) && bundle.containsKey(GREETING_MESSAGE) && bundle.containsKey(LOCATION)) {
            int background = bundle.getInt(BACKGROUND);
            int greetingMessage = bundle.getInt(GREETING_MESSAGE);

            if (background != -1 && greetingMessage != -1) {
                mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), background));
                mGreetingTextView.setText(greetingMessage);

                Location location = bundle.getParcelable(LOCATION);
                if (location != null) {
                    mPositionTextView.setVisibility(View.VISIBLE);
                    mPositionTextView.setText(AppUtils.convert(location.getLatitude(), location.getLongitude()));
                } else {
                    mPositionTextView.setVisibility(View.GONE);
                }
            } else {
                mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));
                mGreetingTextView.setText(R.string.standard_greeting);
            }
        } else {
            mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));
            mGreetingTextView.setText(R.string.standard_greeting);
        }
    }

    /**
     *
     */
    private void initialize() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(getApplicationContext());
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateDayState(location);
                            mPositionTextView.setVisibility(View.VISIBLE);
                            mPositionTextView.setText(AppUtils.convert(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });
    }

    /**
     * @param location
     */
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
            mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_night));
            mGreetingTextView.setText(R.string.night_greeting);

            if (current.after(sunrise)) {
                mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_morning));
                mGreetingTextView.setText(R.string.morning_greeting);

                if (current.after(noon)) {
                    mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_evening));
                    mGreetingTextView.setText(R.string.afternoon_greeting);

                    if (current.after(sunset)) {
                        mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_night));
                        mGreetingTextView.setText(R.string.night_greeting);
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_location:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askForLocationDialog();
                } else {
                    locationProvidedDialog();
                }
                break;

            default:
                break;
        }
    }

    /**
     *
     */
    private void askForLocationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage(R.string.allow_location_permission);
        builder.setPositiveButton(
                R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CHECK_GOOGLE_SETTINGS);
                    }
                }
        );
        builder.setNegativeButton(
                R.string.dont_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }

    /**
     *
     */
    private void locationProvidedDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_on_title);
        builder.setMessage(R.string.location_permission_allowed);
        builder.setPositiveButton(
                R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CHECK_GOOGLE_SETTINGS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                }
                break;
        }
    }
}
