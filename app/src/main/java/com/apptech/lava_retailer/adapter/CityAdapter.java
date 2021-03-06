package com.apptech.lava_retailer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptech.lava_retailer.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.Viewholder> implements Filterable {

    CityInterface cityInterface;
    List<String> citylist;
    List<String>ListAll = new ArrayList<>();


    public CityAdapter(CityInterface cityInterface, List<String> citylist) {
        this.cityInterface = cityInterface;
        this.citylist = citylist;
        ListAll.addAll(citylist);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_smart_select , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.textView.setText(citylist.get(position));
        holder.mainLayout.setOnClickListener(v -> cityInterface.OnItemClick( holder.textView.getText().toString()));
    }

    @Override
    public int getItemCount() {
        return citylist.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(ListAll);
            } else {
                for (String movie: ListAll) {
                    if (movie.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(movie);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            citylist.clear();
            citylist.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView textView;
        LinearLayout mainLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    public interface  CityInterface {
        void OnItemClick(String text);
    }

}


































