package com.rentme.rentme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rentme.rentme.R;
import com.rentme.rentme.interfaces.AdapterClickListner;
import com.rentme.rentme.interfaces.ProfileAdapterClickListner;
import com.rentme.rentme.models.Room;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.PostViewHolder> {
    private List<Room>rooms;
    Context context;
    ProfileAdapterClickListner listner;

    public MyPostAdapter(List<Room> rooms, Context context, ProfileAdapterClickListner listner) {
        this.rooms = rooms;
        this.context = context;
        this.listner = listner;
    }

    @NonNull
    @Override
    public MyPostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile_post_details,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.PostViewHolder holder, int position) {
    Room room = rooms.get(position);
    holder.roomTitle_tv.setText(room.getTitle());
    holder.roomDate_tv.setText(room.getCreated_at());
    holder.roomLocation_tv.setText(room.getLocation());
    holder.roomPrice_tv.setText(String.valueOf(room.getPrice()));

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView roomTitle_tv, roomPrice_tv,roomLocation_tv,roomDate_tv;
        ImageView moreBtn;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            roomTitle_tv = itemView.findViewById(R.id.profile_roomDetail_tv);
            moreBtn = itemView.findViewById(R.id.more_btn);
            roomDate_tv = itemView.findViewById(R.id.profile_days_tv);
            roomPrice_tv = itemView.findViewById(R.id.profile_price_tv);
            roomLocation_tv = itemView.findViewById(R.id.profile_locationName_tv);
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.moreBtnOnClick(getAdapterPosition(),v);
                }
            });
            roomTitle_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.titleOnClick(getAdapterPosition(),v);
                }
            });
        }
    }
}
