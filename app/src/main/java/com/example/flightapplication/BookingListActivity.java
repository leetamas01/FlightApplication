package com.example.flightapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingListAdapter adapter;
    private List<BookingWithId> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        recyclerView = findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        adapter = new BookingListAdapter(bookingList, this);
        recyclerView.setAdapter(adapter);

        loadBookings();
    }

    private void loadBookings() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(query -> {
                    bookingList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Booking booking = doc.toObject(Booking.class);
                        bookingList.add(new BookingWithId(doc.getId(), booking));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}

