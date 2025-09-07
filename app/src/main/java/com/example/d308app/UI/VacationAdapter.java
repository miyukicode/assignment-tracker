package com.example.d308app.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308app.R;
import com.example.d308app.entities.Course;

import java.util.ArrayList;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> implements Filterable {
    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;
        private final TextView datesText;
        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            datesText = itemView.findViewById(R.id.textViewDates);
            vacationItemView=itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    final Course current= mCourses.get(position);
                    Intent intent=new Intent(context,VacationDetails.class);
                    intent.putExtra("id", current.getVacationID());
                    intent.putExtra("name", current.getVacationName());
                    intent.putExtra("hotel", current.getHotel());
                    intent.putExtra("startdate", current.getStartDate());
                    intent.putExtra("enddate", current.getEndDate());
                    context.startActivity(intent);
                }
            });
        }
    }

    private List<Course> mCourses;
    private List<Course> mVacationsFull;
    private final Context context;
    private final LayoutInflater mInflater;

    public VacationAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }



    @NonNull
    @Override
    public VacationAdapter.VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.vacation_list_item,parent,false);
        return new VacationViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull VacationAdapter.VacationViewHolder holder, int position) {
        if(mCourses !=null){
            Course current= mCourses.get(position);
            String name=current.getVacationName();
            holder.vacationItemView.setText(name);
            holder.datesText.setText(current.getStartDate() + " - " + current.getEndDate());

        }
        else{
            holder.vacationItemView.setText("No class name");
            holder.datesText.setText("");
        }

    }

    @Override
    public int getItemCount() {
        if(mCourses != null) {
            return mCourses.size();
        }
        else {
            return 0;
        }
    }

    public void setVacations(List<Course> courses){
        mCourses = courses;
        mVacationsFull = new ArrayList<>(courses);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return vacationFilter;
    }

    private Filter vacationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Course> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mVacationsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Course v : mVacationsFull) {
                    if (v.getVacationName().toLowerCase().contains(filterPattern) ||
                            v.getHotel().toLowerCase().contains(filterPattern)) {
                        filteredList.add(v);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCourses.clear();
            mCourses.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}
