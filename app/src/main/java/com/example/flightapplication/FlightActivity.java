package com.example.flightapplication;

import com.example.flightapplication.FlightAdapter;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FlightActivity extends AppCompatActivity {
    private static final String LOG_TAG = FlightActivity.class.getName();
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;

    private Spinner spinnerFrom, spinnerTo;
    private EditText editDepartureDate, editReturnDate;
    private NumberPicker numberPicker;
    private Button buttonSearch;
    private RecyclerView recyclerView;
    private FlightAdapter flightAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        Log.d(LOG_TAG, "Authenticated user!");

        setupViews();
        setupSpinners();
        setupDatePickers();
        setupNumberPicker();
        setupRecyclerView();
        setupSearchButton();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flightAdapter = new FlightAdapter(new ArrayList<>());
        recyclerView.setAdapter(flightAdapter);
    }

    private void setupSearchButton() {
        buttonSearch.setOnClickListener(v -> {
            String from = spinnerFrom.getSelectedItem().toString();
            String to = spinnerTo.getSelectedItem().toString();
            String departureDate = editDepartureDate.getText().toString();
            int passengers = numberPicker.getValue();

            if (from.equals(to)) {
                Toast.makeText(this, "Az indulási és érkezési város nem lehet ugyanaz!", Toast.LENGTH_SHORT).show();
                return;
            }

            searchFlights(from, to, departureDate);
        });
    }

    private void searchFlights(String from, String to, String departureDate) {
        db.collection("flights")
                .whereEqualTo("from", from)
                .whereEqualTo("to", to)
                .whereEqualTo("departureDate", departureDate)
                .orderBy("price")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("FLIGHT_SEARCH", "Talált dokumentumok száma: " + queryDocumentSnapshots.size());
                    List<Flight> flights = new ArrayList<>();
                    for (var document : queryDocumentSnapshots) {
                        Flight flight = document.toObject(Flight.class);
                        flights.add(flight);
                    }
                    if (flights.isEmpty()) {
                        Toast.makeText(this, "Nincs találat a keresésre.", Toast.LENGTH_SHORT).show();
                        Log.d("QUERY", "from: '" + from + "', to: '" + to + "', date: '" + departureDate + "'");
                        Log.d("FLIGHT_SEARCH", "from=" + from + ", to=" + to + ", departureDate=" + departureDate);
                    }
                    flightAdapter.setFlights(flights);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupViews() {
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo = findViewById(R.id.spinner_to);
        editDepartureDate = findViewById(R.id.edit_departure_date);
        editReturnDate = findViewById(R.id.edit_return_date);
        numberPicker = findViewById(R.id.number_picker_passengers);
        buttonSearch = findViewById(R.id.button_search);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.city_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    private void setupDatePickers() {
        View.OnClickListener dateClickListener = view -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            EditText editText = (EditText) view;
            DatePickerDialog dpd = new DatePickerDialog(FlightActivity.this, (DatePicker datePicker, int y, int m, int d) -> {
                String date = y + "-" + String.format("%02d", m + 1) + "-" + String.format("%02d", d);
                editText.setText(date);
            }, year, month, day);
            dpd.show();
        };

        editDepartureDate.setOnClickListener(dateClickListener);
        editReturnDate.setOnClickListener(dateClickListener);
    }

    private void setupNumberPicker() {
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(true);
    }
    public FlightAdapter.FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightAdapter.FlightViewHolder(view);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadFlights();
    }

    private void loadFlights() {
        FirebaseFirestore.getInstance().collection("flights")
                .orderBy("date")
                .orderBy("time")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Flight> flights = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        flights.add(doc.toObject(Flight.class));
                    }
                    flightAdapter.setFlights(flights);
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba a járatok betöltésekor: " + e.getMessage()));
    }

    private void setSpinnerSelectionByCity(String city) {
        ArrayAdapter adapter = (ArrayAdapter) spinnerFrom.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(city)) {
                spinnerFrom.setSelection(i);
                break;
            }
        }
    }

}
