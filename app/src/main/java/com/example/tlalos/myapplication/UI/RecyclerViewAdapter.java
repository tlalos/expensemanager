package com.example.tlalos.myapplication.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tlalos.myapplication.Activities.ExpenseDetailActivity;
import com.example.tlalos.myapplication.Model.ExpenseListItem;
import com.example.tlalos.myapplication.R;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<ExpenseListItem> listitems;


    public RecyclerViewAdapter(Context context,List listitem) {
        this.context=context;
        this.listitems=listitem;
    }




    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_recycler,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        ExpenseListItem item=listitems.get(position);

        holder.field1.setText(item.getField1());
        holder.field2.setText(item.getField2());
        holder.value.setText(item.getValue());

    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }


    public void updateList(List<ExpenseListItem> ulist){
        listitems.clear();
        listitems.addAll(ulist);
        notifyDataSetChanged();
    }

    public void updateItemToList(ExpenseListItem item){
        int position = listitems.indexOf(item);
        listitems.set(position,item);
        notifyItemChanged(position);
    }


    public void addItemToList(ExpenseListItem item, int position) {
        listitems.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItemFromList(ExpenseListItem item){
        int position = listitems.indexOf(item);
        listitems.remove(position);
        notifyItemRemoved(position);
    }





    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        private static final int DETAIL_ACTIVITY_REQUEST_CODE = 0;

        public TextView field1;
        public TextView field2;
        public TextView value;
        public Button cmdTestButton;

        public int id;


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            field1=(TextView) itemView.findViewById(R.id.tvField1);
            field2=(TextView) itemView.findViewById(R.id.tvField2);
            value=(TextView) itemView.findViewById(R.id.tvValue);

            cmdTestButton=(Button) itemView.findViewById(R.id.cmdRecyclerButtonTest);

            cmdTestButton.setOnClickListener(this);



            //row view click event - possible use to go to next activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position=getAdapterPosition();
                    Snackbar.make(v,"View Clicked "+listitems.get(position).getId(),Snackbar.LENGTH_LONG).show();


                    Intent intent = new Intent(context, ExpenseDetailActivity.class);
                    intent.putExtra("EditMode",1);
                    intent.putExtra("RecId",listitems.get(position).getId());
                    ((Activity)context).startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);


                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            switch (v.getId()) {

                case R.id.cmdRecyclerButtonTest:
                    Toast.makeText(context,"Button Clicked",Toast.LENGTH_LONG).show();
                    break;
            }

        }



    }



}
