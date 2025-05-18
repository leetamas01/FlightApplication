package com.example.flightapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    private List<Flight> flights;

    public FlightAdapter(List<Flight> flights) {
        this.flights = flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.fromToText.setText(flight.getFrom() + " → " + flight.getTo());
        holder.dateText.setText("Dátum: " + flight.getDepartureDate() + " " + flight.getTime());
        holder.priceText.setText("Ár: " + flight.getPrice() + " Ft");

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra(BookingActivity.EXTRA_FROM, flight.getFrom());
            intent.putExtra(BookingActivity.EXTRA_TO, flight.getTo());
            intent.putExtra(BookingActivity.EXTRA_DATE, flight.getDepartureDate());
            intent.putExtra(BookingActivity.EXTRA_TIME, flight.getTime());
            intent.putExtra(BookingActivity.EXTRA_PRICE, flight.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return flights != null ? flights.size() : 0;
    }

    static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView fromToText, dateText, priceText;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            fromToText = itemView.findViewById(R.id.text_from_to);
            dateText = itemView.findViewById(R.id.text_date);
            priceText = itemView.findViewById(R.id.text_price);
        }
    }
}

