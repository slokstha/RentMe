package com.rentme.rentme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rentme.rentme.R;
import com.rentme.rentme.interfaces.AdapterClickListner;
import com.rentme.rentme.models.Room;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchRoomAdapter extends RecyclerView.Adapter<SearchRoomAdapter.SearchRoomHolder> implements Filterable {
    ArrayList<Room> rooms;
    ArrayList<Room> roomsFull;
    Context context;
    AdapterClickListner listener;
    public SearchRoomAdapter(ArrayList<Room> rooms, Context context, AdapterClickListner listener) {
        this.rooms = rooms;
        this.context = context;
        this.listener = listener;
        this.roomsFull=rooms;
    }

    @NonNull
    @Override
    public SearchRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_search_view,parent,false);
        SearchRoomHolder holder = new SearchRoomHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRoomHolder holder, int position) {
        Room room = roomsFull.get(position);
        holder.titleTv.setText(room.getTitle());
        holder.cityTv.setText(room.getCity());
        holder.dateTv.setText(room.getCreated_at());
        if (room.getStatus()==1){
            String s = "Not Available";
            holder.markTv.setText(s);
        }
    }

    @Override
    public int getItemCount() {
        return roomsFull.size();
    }

    public class SearchRoomHolder extends RecyclerView.ViewHolder{

        TextView titleTv,dateTv,markTv,cityTv;
        RelativeLayout layout;
        public SearchRoomHolder(@NonNull View itemView) {
            super(itemView);
            titleTv= itemView.findViewById(R.id.search_title);
            cityTv=itemView.findViewById(R.id.search_city);
            layout=itemView.findViewById(R.id.row_search_layout);
            dateTv= itemView.findViewById(R.id.search_posted_date);
            markTv= itemView.findViewById(R.id.search_mark);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(getAdapterPosition(),v);
                }
            });
        }
    }
    @Override
    public Filter getFilter() {
        return filterList;
    }

    private Filter filterList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            if (charString.isEmpty()) {
                roomsFull=rooms;
            } else {
                ArrayList<Room> filteredList = new ArrayList<>();
                String filterPattern = constraint.toString().toLowerCase().trim();
                //trim and no case sensetive
                for (Room item : rooms) {
                    if (item.getProperty_type().toLowerCase().contains(filterPattern) || item.getCity().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                roomsFull=filteredList;
            }

            FilterResults results = new FilterResults();
            results.values = roomsFull;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            roomsFull=(ArrayList<Room>) results.values;
            notifyDataSetChanged();
        }
    };

}
