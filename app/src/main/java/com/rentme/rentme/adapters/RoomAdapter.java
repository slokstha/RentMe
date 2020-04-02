package com.rentme.rentme.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rentme.rentme.R;
import com.rentme.rentme.interfaces.AdapterClickListner;
import com.rentme.rentme.models.Room;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.PostViewHolder> {
    Context context;
    ArrayList<Room> rooms = new ArrayList<>();
    AdapterClickListner listner;

    public RoomAdapter(Context context, ArrayList<Room> rooms, AdapterClickListner listner) {
        this.context = context;
        this.rooms = rooms;
        this.listner = listner;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_room_details,parent,false);
        PostViewHolder holder = new PostViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Room room = rooms.get(position);
        if (room.getImages() != null) {
            Picasso.get().load(room.getImages().get(0)).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.room_placeholder);
        }
        if (room.getStatus()==1){
            holder.row_not_available_iv.setVisibility(View.VISIBLE);
        }
        else {
            holder.row_not_available_iv.setVisibility(View.GONE);
        }
        holder.name.setText(room.getTitle());
        String price = "Price: Npr, "+room.getPrice();
        holder.price.setText(price);
        String location = "Location:"+room.getLocation();
        holder.location.setText(location);
        holder.date.setText(room.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price, location, date;
        private ImageView imageView,row_not_available_iv;
        RelativeLayout linearLayout;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.row_img_view);
            name = itemView.findViewById(R.id.row_post_name);
            row_not_available_iv = itemView.findViewById(R.id.row_not_available_iv);
            price = itemView.findViewById(R.id.post_price);
            location = itemView.findViewById(R.id.location_name);
            date = itemView.findViewById(R.id.create_at_date);
            linearLayout = itemView.findViewById(R.id.relative_post);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onClick(getAdapterPosition(), v);
                }
            });
        }
    }

}
