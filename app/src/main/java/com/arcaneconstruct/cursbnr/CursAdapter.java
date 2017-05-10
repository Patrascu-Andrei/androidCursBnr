package com.arcaneconstruct.cursbnr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Archangel on 4/5/2016.
 */
public class CursAdapter extends ArrayAdapter<Curs> {
    Context context;
    List<Curs> listaCurs;
    public CursAdapter (Context context, int resource, List<Curs> objects) {
        super(context, resource, objects);
        this.context=context;
        listaCurs=objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (row==null){
            row = inflater.inflate(R.layout.layout_row, parent, false);
        }
        TextView currency = (TextView) row.findViewById(R.id.currency);
        TextView rate= (TextView) row.findViewById(R.id.rate);
        currency.setText(listaCurs.get(position).getCurrency());
        rate.setText((String) listaCurs.get(position).getRate());
        return row;
    }
}
