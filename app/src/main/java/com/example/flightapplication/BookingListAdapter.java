package com.example.flightapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.flightapplication.BookingWithId;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.net.CookieStore;
import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder> {

    private List<BookingWithId> bookingList;
    private Context context;

    public BookingListAdapter(List<BookingWithId> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingWithId bookingWithId = bookingList.get(position);
        Booking booking = bookingWithId.booking;

        Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        holder.itemView.startAnimation(slideIn);

        holder.textFrom.setText("Honnan: " + booking.from);
        holder.textTo.setText("Hova: " + booking.to);
        holder.textDate.setText("Dátum: " + booking.date);
        holder.textTime.setText("Idő: " + booking.time);
        holder.textPrice.setText("Ár: " + booking.price + " Ft");

        holder.buttonDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("bookings")
                    .document(bookingWithId.id)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        bookingList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(holder.itemView.getContext(), "Foglalás törölve", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        holder.buttonEdit.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra(BookingActivity.EXTRA_FROM, booking.from);
            intent.putExtra(BookingActivity.EXTRA_TO, booking.to);
            intent.putExtra(BookingActivity.EXTRA_DATE, booking.date);
            intent.putExtra(BookingActivity.EXTRA_TIME, booking.time);
            intent.putExtra(BookingActivity.EXTRA_PRICE, booking.price);
            intent.putExtra("BOOKING_ID", bookingWithId.id);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView textFrom, textTo, textDate, textTime, textPrice;
        Button buttonEdit, buttonDelete;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textFrom = itemView.findViewById(R.id.textFrom);
            textTo = itemView.findViewById(R.id.textTo);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            textPrice = itemView.findViewById(R.id.textPrice);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}

