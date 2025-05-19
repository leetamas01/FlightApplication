package com.example.flightapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private EditText editFrom, editTo, editDate, editTime, editPrice;
    private Button buttonBook;

    public static final String EXTRA_FROM = "extra_from";
    public static final String EXTRA_TO = "extra_to";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_TIME = "extra_time";
    public static final String EXTRA_PRICE = "extra_price";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        editFrom = findViewById(R.id.edit_from);
        editTo = findViewById(R.id.edit_to);
        editDate = findViewById(R.id.edit_date);
        editTime = findViewById(R.id.edit_time);
        editPrice = findViewById(R.id.edit_price);
        buttonBook = findViewById(R.id.button_book);

        String fromIntent = getIntent().getStringExtra(EXTRA_FROM);
        String toIntent = getIntent().getStringExtra(EXTRA_TO);
        String dateIntent = getIntent().getStringExtra(EXTRA_DATE);
        String timeIntent = getIntent().getStringExtra(EXTRA_TIME);
        int priceIntent = getIntent().getIntExtra(EXTRA_PRICE, 0);

        editFrom.setText(fromIntent);
        editTo.setText(toIntent);
        editDate.setText(dateIntent);
        editTime.setText(timeIntent);
        editPrice.setText(String.valueOf(priceIntent));
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Foglalási Értesítések",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        buttonBook.setOnClickListener(v -> {
            saveBooking();
        });
    }

    private void saveBooking() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String from = editFrom.getText().toString().trim();
        String to = editTo.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();
        String priceText = editPrice.getText().toString().trim();

        if (from.isEmpty() || to.isEmpty() || date.isEmpty() || time.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Kérlek, tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Érvénytelen ár!", Toast.LENGTH_SHORT).show();
            return;
        }

        Date timestamp;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            timestamp = sdf.parse(date + " " + time);
        } catch (ParseException e) {
            Toast.makeText(this, "Érvénytelen dátum/idő formátum!", Toast.LENGTH_SHORT).show();
            return;
        }

        long millisTimestamp = timestamp.getTime();

        Booking booking = new Booking(uid, from, to, date, time, price, millisTimestamp, 1);

        BookingDao dao = new BookingDao();
        dao.addBooking(booking, new BookingDao.OnBookingSaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(BookingActivity.this, "Foglalás mentve Firebase-be!", Toast.LENGTH_SHORT).show();
                scheduleReminder(timestamp);
                finish();
                setContentView(R.layout.activity_booking_list);

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(BookingActivity.this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleReminder(Date flightDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(flightDateTime);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // előző nap
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}

