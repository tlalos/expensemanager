package com.example.tlalos.myapplication.Data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.tlalos.myapplication.R;

public class ExpenseTypeAdapter extends CursorAdapter {
    public ExpenseTypeAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.expense_type_list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
// Find fields to populate in inflated template
        TextView tvField1 = (TextView) view.findViewById(R.id.tvExpenseTypeListRow1);
        TextView tvField2= (TextView) view.findViewById(R.id.tvExpenseTypeListRow2);

        // Extract properties from cursor
        String descr = cursor.getString(cursor.getColumnIndexOrThrow("descr"));
        Integer codeId = cursor.getInt(cursor.getColumnIndexOrThrow("codeid"));
        // Populate fields with extracted properties
        tvField1.setText(descr);
        tvField2.setText("Code Id:"+Integer.toString(codeId));

    }
}
