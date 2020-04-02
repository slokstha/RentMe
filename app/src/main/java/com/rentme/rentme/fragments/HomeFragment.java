package com.rentme.rentme.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.activities.RoomDetailActivity;
import com.rentme.rentme.adapters.RoomAdapter;
import com.rentme.rentme.adapters.SearchRoomAdapter;
import com.rentme.rentme.interfaces.AdapterClickListner;
import com.rentme.rentme.models.Room;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    View view;
    RecyclerView recyclerView,searchViewRecyclerView;
    RoomAdapter roomAdapter;
    Context context;
    String roomData;
    View searchLinerLayout,noConnectionLayout;
    Button retryBtn;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchRoomAdapter searchRoomAdapter;
    ArrayList<Room> rooms = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        initViews();
        if (CheckConnectivity.isNetworkAvailable(getContext())){
            noConnectionLayout.setVisibility(View.GONE);
        }
        else {
            noConnectionLayout.setVisibility(View.VISIBLE);
        }
        loadData();
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                },400);

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final ProgressDialog progress = new ProgressDialog(getContext());
                progress.setTitle("Loading..");
                progress.setMessage("Please wait");
                progress.setCancelable(false);
                progress.show();
                loadData();
                swipeRefreshLayout.setRefreshing(false);
                progress.dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) { //we can over write oncreate listner in fragment to customize as we like
        final MenuItem menuItem = menu.findItem(R.id.search);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchLinerLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search for e.g. room | kathmandu");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRoomAdapter.getFilter().filter(query, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
//                        Toast.makeText(mContext, "Total Item--> "+count, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRoomAdapter.getFilter().filter(newText, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
//                        Toast.makeText(mContext, "Total Item--> "+count, Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
//                    searchView.setIconified(false);
                    searchLinerLayout.setVisibility(View.GONE);
                    menuItem.collapseActionView();
                    searchView.setQuery("", false);

                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    private void loadData() {
        rooms.clear();
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle("Loading..");
        progress.setMessage("Please wait");
        progress.setCancelable(false);
        progress.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Api.getAllPost, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                noConnectionLayout.setVisibility(View.GONE);
                try {
                    JSONArray array = response.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Room room = new Gson().fromJson(object.toString(),Room.class);
//                        Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();
                        rooms.add(room);
                    }
                    roomAdapter.notifyDataSetChanged();
                    searchRoomAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                HandleNetworkError.handlerError(error,getContext());
            }
        });
        if (CheckConnectivity.isNetworkAvailable(getContext())){
            requestQueue.add(jsonObjectRequest);
        }
        else {
            progress.dismiss();
            noConnectionLayout.setVisibility(View.VISIBLE);
        }

    }


    private void initViews() {
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh_l);
        recyclerView = view.findViewById(R.id.room_recycler_view);
        noConnectionLayout=view.findViewById(R.id.no_connection_layout);
        retryBtn=noConnectionLayout.findViewById(R.id.btn_retry_connection);
        searchLinerLayout=view.findViewById(R.id.search_layout);
        searchViewRecyclerView=searchLinerLayout.findViewById(R.id.search_recycler_v);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomAdapter = new RoomAdapter(context, rooms, new AdapterClickListner() {
            @Override
            public void onClick(int position, View view) {
                Room room = rooms.get(position);
                roomData = new Gson().toJson(room);
                Intent intent = new Intent(getContext(), RoomDetailActivity.class);
                intent.putExtra("roomData",roomData);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(roomAdapter);
        searchRoomAdapter=new SearchRoomAdapter(rooms, getContext(), new AdapterClickListner() {
            @Override
            public void onClick(int position, View view) {
                Room room = rooms.get(position);
                roomData = new Gson().toJson(room);
                Intent intent = new Intent(getContext(), RoomDetailActivity.class);
                intent.putExtra("roomData",roomData);
                startActivity(intent);
            }
        });
        searchViewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchViewRecyclerView.setAdapter(searchRoomAdapter);

    }
}
