package com.rentme.rentme.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.interfaces.AdapterClickListner;
import com.rentme.rentme.interfaces.VehicleAdapterClickListner;
import com.rentme.rentme.models.User;
import com.rentme.rentme.models.Vehicle;
import com.rentme.rentme.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    private List<Vehicle> vehicles;
    Context context;
    VehicleAdapterClickListner listner;

    public VehicleAdapter(List<Vehicle> vehicles, Context context, VehicleAdapterClickListner listner) {
        this.vehicles = vehicles;
        this.context = context;
        this.listner = listner;
    }

    @NonNull
    @Override
    public VehicleAdapter.VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_vehicle_details, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        SharedPreferences preferences = context.getSharedPreferences(Constants.sharePrefName, Context.MODE_PRIVATE);
        String userData = preferences.getString(Constants.userData, null);
        if (userData != null) {
            User user = new Gson().fromJson(userData,User.class);
            if (user.getId() == vehicle.getAdded_by()) {
                holder.deleteBtn.setVisibility(View.VISIBLE);
            } else {
                holder.deleteBtn.setVisibility(View.GONE);
            }
        }

        holder.details.setText(vehicle.getTitle());
        String price = "Price: " + vehicle.getPrice();
        String name = "Owner Name: " + vehicle.getOwner_name();
        String phone = "Contact: " + vehicle.getContact();
        String area = "Service Area: " + vehicle.getService_area();
        holder.name.setText(name);
        holder.price.setText(price);
        holder.phone.setText(phone);
        holder.service_location.setText(area);
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public class VehicleViewHolder extends RecyclerView.ViewHolder {
        private TextView details, name, price, phone, service_location;
        ImageView deleteBtn;
        Button btnCallVehicle;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.transport_details);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            service_location = itemView.findViewById(R.id.service_location);
            phone = itemView.findViewById(R.id.phone);
            deleteBtn = itemView.findViewById(R.id.v_btn_delete);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.dltBtnOnClick(getAdapterPosition(), v);
                }
            });

            btnCallVehicle = itemView.findViewById(R.id.btn_call_vehicle);
            btnCallVehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.callBtnOnClick(getAdapterPosition(), view);
                }
            });
        }

    }
}
