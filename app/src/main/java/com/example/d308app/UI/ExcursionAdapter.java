package com.example.d308app.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308app.R;
import com.example.d308app.entities.Assignment;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    class ExcursionViewHolder extends RecyclerView.ViewHolder{
        private final TextView excursionItemView;
        private final TextView excursionDates;
        private ExcursionViewHolder(View itemView){
            super(itemView);
            excursionItemView=itemView.findViewById(R.id.textView20);
            excursionDates=itemView.findViewById(R.id.excursionViewDates);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    final Assignment current= mAssignments.get(position);
                    Intent intent=new Intent(context,ExcursionDetails.class);
                    intent.putExtra("id", current.getExcursionID());
                    intent.putExtra("name", current.getExcursionName());
                    intent.putExtra("date", current.getExcursionDate());
                    intent.putExtra("vacationID",current.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }
    private List<Assignment> mAssignments;
    private final Context context;
    private final LayoutInflater mInflater;

    public ExcursionAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }
    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.excursion_list_item,parent,false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if(mAssignments !=null){
            Assignment current= mAssignments.get(position);
            String name=current.getExcursionName();
            int prodID= current.getExcursionID();
            holder.excursionItemView.setText(name);
            holder.excursionDates.setText(current.getExcursionDate());
        }
        else{
            holder.excursionItemView.setText("No excursion name");
            holder.excursionDates.setText("");
        }
    }

    public void setExcursions(List<Assignment> assignments){
        mAssignments = assignments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mAssignments != null) {
            return mAssignments.size();
        }
        else {
            return 0;
        }
    }
}

