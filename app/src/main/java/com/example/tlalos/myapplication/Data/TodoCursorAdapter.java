package com.example.tlalos.myapplication.Data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.tlalos.myapplication.R;

public class TodoCursorAdapter extends CursorAdapter {
    public TodoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvField1 = (TextView) view.findViewById(R.id.tvField1);
        TextView tvField2= (TextView) view.findViewById(R.id.tvField2);
        TextView tvValue= (TextView) view.findViewById(R.id.tvValue);
        // Extract properties from cursor
        String expensedescr = cursor.getString(cursor.getColumnIndexOrThrow("expensedescr"));
        String comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"));
        String cdate = cursor.getString(cursor.getColumnIndexOrThrow("cdate"));
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String value = cursor.getString(cursor.getColumnIndexOrThrow("expensevalue"));
        // Populate fields with extracted properties
        tvField1.setText(expensedescr);
        tvField2.setText(cdate);
        tvValue.setText(value+"€");
    }
}
