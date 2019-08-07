package com.bondev.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>  {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase my_firebase_database;
    private DatabaseReference my_Database_reference;
    private ChildEventListener my_Child_listener;
    public  DealAdapter(){

        my_firebase_database = FirebaseUtil.my_firebase_database;
        my_Database_reference =FirebaseUtil.my_database_reference;
        deals=FirebaseUtil.deal;
        my_Child_listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal tv=dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal",tv.getTitle());
                tv.setId(dataSnapshot.getKey());
                deals.add(tv);
                notifyItemInserted(deals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        my_Database_reference.addChildEventListener(my_Child_listener);
    }
    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View itemView= LayoutInflater.from(context).inflate(R.layout.rv_row, parent,false);
        return new DealViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {

        TravelDeal deal=deals.get(position);
        holder.bind(deal);

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }
    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_title;
        TextView tv_Description;
        TextView tv_price;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title= itemView.findViewById(R.id.tv_title);
            tv_Description=itemView.findViewById(R.id.tv_description);
            tv_price=itemView.findViewById(R.id.tv_price);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal){
            tv_title.setText(deal.getTitle());
            tv_Description.setText(deal.getDescription());
            tv_price.setText(deal.getPrice());
        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            Log.d("click",String.valueOf(position));
            TravelDeal selected_deal=deals.get(position);
            Intent intent= new Intent(view.getContext(),DealActivity.class);
            intent.putExtra("Selected_deal",selected_deal);
            view.getContext().startActivity(intent);
        }
    }
}
