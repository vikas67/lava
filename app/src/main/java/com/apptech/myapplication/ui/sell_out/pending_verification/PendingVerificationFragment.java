package com.apptech.myapplication.ui.sell_out.pending_verification;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apptech.myapplication.R;
import com.apptech.myapplication.adapter.SellOutPendingVerificationAdapter;
import com.apptech.myapplication.databinding.PendingVerificationFragmentBinding;
import com.apptech.myapplication.databinding.PriceDropFragmentBinding;
import com.apptech.myapplication.fragment.price_drop.PriceDropViewModel;
import com.apptech.myapplication.modal.sellOutPendingVerification.List;
import com.apptech.myapplication.modal.sellOutPendingVerification.SellOutPendingVerificationList;
import com.apptech.myapplication.other.NetworkCheck;
import com.apptech.myapplication.other.SessionManage;
import com.apptech.myapplication.service.ApiClient;
import com.apptech.myapplication.service.LavaInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingVerificationFragment extends Fragment {

    private PendingVerificationViewModel mViewModel;
    PendingVerificationFragmentBinding binding;
    AlertDialog alertDialog;
    View view;
    LinearLayout fromDatetitle, toDatetitle;
    TextView fromTextView, toTextView;
    ImageView closeImg;
    DatePickerDialog picker;
    private static final String TAG = "SellOut_PendingVerifica";
    LavaInterface lavaInterface;
    SessionManage sessionManage;
    String USER_ID;
    String StartDate = null, EndDate = null;
    Button searchBtn;
    java.util.List<List> lists;
    SellOutPendingVerificationAdapter sellOutPendingVerificationAdapter;


    public static PendingVerificationFragment newInstance() {
        return new PendingVerificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = PendingVerificationFragmentBinding.inflate(inflater , container , false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PendingVerificationViewModel.class);
        // TODO: Use the ViewModel


        lavaInterface = ApiClient.getClient().create(LavaInterface.class);
        sessionManage = SessionManage.getInstance(requireContext());
        USER_ID = sessionManage.getUserDetails().get("ID");


        binding.datetimefilter.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
            view = LayoutInflater.from(requireContext()).inflate(R.layout.row_dialog_open, null);
            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();
            fromDatetitle = view.findViewById(R.id.fromDatetitle);
            toDatetitle = view.findViewById(R.id.toDatetitle);
            dilogclick();
        });


        StockList(getCurrentDate(), getCurrentDate());

        binding.validsipnner.setOnSpinnerItemSelectedListener((i, o, i1, t1) -> {
            Log.e(TAG, "onActivityCreated: " + t1.toString().trim());
            filtervalid(t1.toString());
        });



    }



    private void StockList(String startdate, String enddate) {
        Log.e(TAG, "onActivityCreated: " + USER_ID);

        lavaInterface.SellOut_Stock_List_DateFilter(USER_ID, startdate, enddate).enqueue(new Callback<SellOutPendingVerificationList>() {
            @Override
            public void onResponse(Call<SellOutPendingVerificationList> call, Response<SellOutPendingVerificationList> response) {

                Log.e(TAG, "onResponse: " + new Gson().toJson(response.body()));

                if (response.isSuccessful()) {
                    if (!response.body().getError()) {
                        if (response.body().getList().size() == 0) {
                            binding.noStock.setVisibility(View.VISIBLE);
                            binding.progressbar.setVisibility(View.GONE);
                            return;
                        }
                        lists = new ArrayList<>(response.body().getList());
                        sellOutPendingVerificationAdapter = new SellOutPendingVerificationAdapter(response.body().getList());
                        binding.ImeiRecyclerView.setAdapter(sellOutPendingVerificationAdapter);
                        binding.progressbar.setVisibility(View.GONE);
                        return;
                    }
                    binding.noStock.setVisibility(View.VISIBLE);
                }
                binding.progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<SellOutPendingVerificationList> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());

                binding.progressbar.setVisibility(View.GONE);
                binding.noStock.setVisibility(View.VISIBLE);

            }
        });

    }


    private void dilogclick() {


        fromTextView = view.findViewById(R.id.fromTextView);
        toTextView = view.findViewById(R.id.toTextView);
        closeImg = view.findViewById(R.id.closeImg);
        searchBtn = view.findViewById(R.id.searchBtn);

        closeImg.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        searchBtn.setOnClickListener(v -> {
            if (new NetworkCheck().haveNetworkConnection(requireActivity())) {
                if (fromTextView.getText().toString().trim().equalsIgnoreCase("From")) {
                    Snackbar snackbar = Snackbar
                            .make(binding.getRoot(), "from date", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                if (toTextView.getText().toString().trim().equalsIgnoreCase("To")) {
                    Snackbar snackbar = Snackbar
                            .make(binding.getRoot(), "to dater", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                dateFilter1(fromTextView.getText().toString().trim(), toTextView.getText().toString().trim());
                alertDialog.dismiss();
                return;
            }
            Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show();
        });


        fromDatetitle.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(requireContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
//                            eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        fromTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        Log.e(TAG, "onDateSet: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                        StartDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }, year, month, day);
            picker.show();
        });

        toDatetitle.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(requireContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        toTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        Log.e(TAG, "onDateSet: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                        EndDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }, year, month, day);
            picker.show();
        });

    }

    private void dateFilter1(String from, String to) {

        binding.progressbar.setVisibility(View.VISIBLE);
        binding.noStock.setVisibility(View.GONE);


        lavaInterface.SellOut_Stock_List_DateFilter(USER_ID, from, to).enqueue(new Callback<SellOutPendingVerificationList>() {
            @Override
            public void onResponse(Call<SellOutPendingVerificationList> call, Response<SellOutPendingVerificationList> response) {
                Log.e(TAG, "onResponse: " + new Gson().toJson(response.body()));

                if (response.isSuccessful()) {
                    if (!response.body().getError()) {
                        if (response.body().getList().size() > 0) {
                            lists = new ArrayList<>(response.body().getList());
                            binding.ImeiRecyclerView.setVisibility(View.VISIBLE);
                            sellOutPendingVerificationAdapter = new SellOutPendingVerificationAdapter(response.body().getList());
                            binding.ImeiRecyclerView.setAdapter(sellOutPendingVerificationAdapter);
                            sellOutPendingVerificationAdapter.notifyDataSetChanged();
                            binding.progressbar.setVisibility(View.GONE);
                            return;
                        }
                        binding.noStock.setVisibility(View.VISIBLE);
                        binding.progressbar.setVisibility(View.GONE);
                        binding.ImeiRecyclerView.setVisibility(View.GONE);
                        return;
                    }
                    binding.noStock.setVisibility(View.VISIBLE);
                }
                binding.progressbar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<SellOutPendingVerificationList> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());

                binding.progressbar.setVisibility(View.GONE);

            }
        });
    }

    private void filtervalid(String valid) {

        binding.progressbar.setVisibility(View.VISIBLE);
        java.util.List<List> list = new ArrayList<>();


        for (int i = 0; i < lists.size(); i++) {
            com.apptech.myapplication.modal.sellOutPendingVerification.List l = lists.get(i);

            Log.e(TAG, "filtervalid: " + valid );
            Log.e(TAG, "filtervalid: " + l.getValid() );

            if (valid.equalsIgnoreCase(l.getValid())) {
                list.add(l);
            }
        }

        Log.e(TAG, "filtervalid: " + list.size());
        Log.e(TAG, "filtervalid: " + list);

        if (list.size() == 0) {
            binding.noStock.setVisibility(View.VISIBLE);
        } else {
            binding.noStock.setVisibility(View.GONE);
        }
        sellOutPendingVerificationAdapter = new SellOutPendingVerificationAdapter(list);
        binding.ImeiRecyclerView.setAdapter(sellOutPendingVerificationAdapter);
        sellOutPendingVerificationAdapter.notifyDataSetChanged();
        binding.progressbar.setVisibility(View.GONE);

    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView title = getActivity().findViewById(R.id.Actiontitle);
        title.setText("Pending Verification");
    }



}