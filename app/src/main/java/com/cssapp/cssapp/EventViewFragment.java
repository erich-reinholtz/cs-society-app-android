package com.cssapp.cssapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EventViewFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static TextView  dateText;
    private View context;
    private HashMap<Integer, Integer> map;

    @SuppressLint("UseSparseArrays")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = inflater.inflate(R.layout.fragment_event_view, container, false);
        dateText = context.findViewById(R.id.dateText);
        map = new HashMap<>();
        return context;
    }

    public void setDate(String date) {
        dateText.setText(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createAdapter(HashMap<Integer, String> data){
        ArrayList<String> events = new ArrayList<>();
        map.clear();
        int x = 0;
        for(Integer i : data.keySet()){
            events.add(data.get(i));
            map.put(x,i);
            x++;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1,events);
        ListView lv = context.findViewById(R.id.events_list);
        lv.setAdapter(arrayAdapter);

    }
    public void listclicklistener(){
        final ListView listV = context.findViewById(R.id.events_list);
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
                //Get string from TextView
                String eId = Objects.requireNonNull(map.get(position)).toString();
                Intent intent = new Intent(getActivity(), EventDisplayActivity.class);
                //Send string to EventDisplayActivity
                intent.putExtra("EXTRA_MESSAGE", eId);
                startActivity(intent);
            }
        });
    }

}
