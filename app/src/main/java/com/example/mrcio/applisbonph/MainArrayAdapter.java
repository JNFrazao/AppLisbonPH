package com.example.mrcio.applisbonph;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by NB19194 on 02/05/2016.
 */
public class MainArrayAdapter extends ArrayAdapter<Integer> {
    private final Context context;
    private final Integer[] values;

    public MainArrayAdapter(Context context, Integer[] values) {
        super(context, R.layout.content_main, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.eventlist, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.eventLogo);
        imageView.setImageResource(values[position]);


        return rowView;
    }
}
