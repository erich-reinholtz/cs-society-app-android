// ArrayAdapter for the Event Class.

package com.cssapp.cssapp;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends ArrayAdapter<Event>{
    private Context eContext;
    private List<Event> eventsList;

    EventsAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Event> list) {
        super(context, 0 , list);
        eContext = context;
        eventsList = list;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(eContext).inflate(R.layout.list_item,parent,false);

        Event currentEvent = eventsList.get(position);

        TextView name = listItem.findViewById(R.id.EventName_textView);
        name.setText(currentEvent.getName());

        TextView description = listItem.findViewById(R.id.EventDescription_textView);
        description.setText(currentEvent.getDescription());
        if(currentEvent.getDescription().isEmpty())
            description.setVisibility(View.GONE);

        TextView author = listItem.findViewById(R.id.Author_textView);
        author.setText(currentEvent.getAuthor());
        if(currentEvent.getAuthor().isEmpty())
            author.setVisibility(View.GONE);


        TextView id = listItem.findViewById(R.id.Id_textView);
        id.setText(currentEvent.getId());

        TextView hide = listItem.findViewById(R.id.Made_textView);
        if(currentEvent.getHide() == 1)
            hide.setVisibility(View.GONE);

        return listItem;
    }

    void clearAdapter()
    {
        eventsList.clear();
        notifyDataSetChanged();
    }
}
