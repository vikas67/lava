package com.apptech.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptech.myapplication.databinding.RowBrandsBinding;
import com.apptech.myapplication.other.SessionManage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.Viewholder> {


    RowBrandsBinding binding;
    List<com.apptech.myapplication.modal.brand.List> brandLists;
    BrandInterfaces brandInterfaces;
    SessionManage sessionManage;
    Context context;


    public BrandsAdapter(List<com.apptech.myapplication.modal.brand.List> list, BrandInterfaces brandInterfaces) {
        this.brandLists = list;
        this.brandInterfaces = brandInterfaces;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManage = SessionManage.getInstance(context);
        binding = RowBrandsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.setItemclick(brandInterfaces);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {
        com.apptech.myapplication.modal.brand.List list = brandLists.get(position);
        binding.setLang(sessionManage.getUserDetails().get("LANGUAGE"));
        binding.setList(list);
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return brandLists.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(@NonNull @NotNull RowBrandsBinding itemView) {
            super(itemView.getRoot());
        }
    }

    public interface BrandInterfaces {
        void brandItemClick(com.apptech.myapplication.modal.brand.List list);
    }


}
































