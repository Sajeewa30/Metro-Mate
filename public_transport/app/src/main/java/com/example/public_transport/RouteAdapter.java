package com.example.public_transport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class RouteAdapter extends BaseAdapter {

    private Context context;
    private List<String> destinations;
    private Map<String, String> routeTable;

    public RouteAdapter(Context context, List<String> destinations, Map<String, String> routeTable) {
        this.context = context;
        this.destinations = destinations;
        this.routeTable = routeTable;
    }

    @Override
    public int getCount() {
        return destinations.size();
    }

    @Override
    public Object getItem(int position) {
        return destinations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.route_item, parent, false);
        }

        TextView destinationView = convertView.findViewById(R.id.destinationName);
        TextView routeView = convertView.findViewById(R.id.routeNumber);

        String destination = destinations.get(position);
        String route = routeTable.get(destination);

        destinationView.setText(destination);
        routeView.setText(route);

        return convertView;
    }
}
