package com.example.flightapplication;

import androidx.annotation.NonNull;
import com.example.flightapplication.Booking;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {

    public interface OnBookingSaveListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnBookingQueryListener {
        void onSuccess(List<Booking> bookings);
        void onFailure(Exception e);
    }

    public interface OnBookingUpdateListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnBookingDeleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void addBooking(Booking booking, OnBookingSaveListener listener) {
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void getBookingsForUser(String uid, OnBookingQueryListener listener) {
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Booking> bookings = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        bookings.add(doc.toObject(Booking.class));
                    }
                    listener.onSuccess(bookings);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void updateBookingDate(String bookingId, String newDate, OnBookingUpdateListener listener) {
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .document(bookingId)
                .update("date", newDate)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteBooking(String bookingId, OnBookingDeleteListener listener) {
        FirebaseFirestore.getInstance()
                .collection("bookings")
                .document(bookingId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }
}

