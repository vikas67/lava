package com.apptech.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apptech.myapplication.R;
import com.apptech.myapplication.databinding.ActivityConfirmPasswordBinding;
import com.apptech.myapplication.other.NetworkCheck;
import com.apptech.myapplication.service.ApiClient;
import com.apptech.myapplication.service.LavaInterface;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPasswordActivity extends AppCompatActivity {



    ActivityConfirmPasswordBinding binding;
    LavaInterface lavaInterface;
    private static final String TAG = "ConfirmPasswordActivity";
    String mob = "" , otp ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        lavaInterface = ApiClient.getClient().create(LavaInterface.class);

        try {
            Intent intent = getIntent();
            if(intent != null){
                mob = intent.getStringExtra("mob");
                otp = intent.getStringExtra("otp");
                Toast.makeText(this, "" + otp, Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }


        binding.changePsw.setOnClickListener(v -> {
            if(new NetworkCheck().haveNetworkConnection(this)){
                if(validation()){
                    Changepassword();
                    return;
                }
                return;
            }
            Toast.makeText(ConfirmPasswordActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        });



        binding.OtpInputLayout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.OtpInputLayoutError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.passwordError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.Confirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ConfirmPasswordCheck(binding.password.getText().toString().trim() , binding.Confirmpassword.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void Changepassword() {
        binding.progressbar.setVisibility(View.VISIBLE);
        lavaInterface.FORGOT_PASS_OTP_SEND(mob , binding.OtpInputLayout.getText().toString().trim(),binding.password.getText().toString().trim()).enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                Log.e(TAG, "onResponse: " + response.body().toString() );
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String error = jsonObject.getString("error");
                    String message = jsonObject.getString("message");

                    if (error.equalsIgnoreCase("false")) {
                        startActivity(new Intent(ConfirmPasswordActivity.this , LoginActivity.class));
                        finish();
                        binding.progressbar.setVisibility(View.GONE);
                        return;
                    }

                    Toast.makeText(ConfirmPasswordActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                    binding.progressbar.setVisibility(View.GONE);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                binding.progressbar.setVisibility(View.GONE);
                Toast.makeText(ConfirmPasswordActivity.this, "" + getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );
                binding.progressbar.setVisibility(View.GONE);
                Toast.makeText(ConfirmPasswordActivity.this, "Time out", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean validation(){
        return  otpValidation(binding.OtpInputLayout.getText().toString().trim())
                && PasswordCheck(binding.password.getText().toString().trim())
                && ConfirmPasswordCheck(binding.password.getText().toString().trim() , binding.Confirmpassword.getText().toString().trim());
    }


    private boolean otpValidation(String otp) {
        if (otp.isEmpty()) {
            binding.OtpInputLayout.setError(getResources().getString(R.string.field_required));
            binding.OtpInputLayoutError.setVisibility(View.VISIBLE);
            binding.OtpInputLayoutError.setText(getResources().getString(R.string.field_required));
            return false;
        } else if (otp.length() < 4) {
            binding.OtpInputLayout.setError(getResources().getString(R.string.otp_four_digit));
            binding.OtpInputLayoutError.setVisibility(View.VISIBLE);
            binding.OtpInputLayoutError.setText(getResources().getString(R.string.otp_four_digit));
            return false;
        }
        binding.OtpInputLayout.setError(null);
        binding.OtpInputLayoutError.setVisibility(View.GONE);
        return true;
    }

    private boolean PasswordCheck(String psw) {
        if (psw.isEmpty()) {
            binding.password.setError(getResources().getString(R.string.field_required));
            binding.passwordError.setVisibility(View.VISIBLE);
            binding.passwordError.setText(getResources().getString(R.string.field_required));
            return false;
        } else if (psw.length() <= 6) {
            binding.password.setError(getResources().getString(R.string.psw_short));
            binding.passwordError.setVisibility(View.VISIBLE);
            binding.passwordError.setText(getResources().getString(R.string.psw_short));
            return false;
        }
        binding.password.setError(null);
        binding.passwordError.setVisibility(View.GONE);
        return true;
    }

    private boolean ConfirmPasswordCheck(String psw , String confirmpass) {
        if (confirmpass.isEmpty()) {
            binding.Confirmpassword.setError(getResources().getString(R.string.field_required));
            binding.ConfirmpasswordError.setVisibility(View.VISIBLE);
            binding.ConfirmpasswordError.setText(getResources().getString(R.string.field_required));
            return false;
        } else if (confirmpass.length() != psw.length()) {
            binding.Confirmpassword.setError(getResources().getString(R.string.psw_not_match));
            binding.ConfirmpasswordError.setVisibility(View.VISIBLE);
            binding.ConfirmpasswordError.setText(getResources().getString(R.string.psw_not_match));
            return false;
        } else if (!psw.equalsIgnoreCase(confirmpass)) {
            binding.Confirmpassword.setError(getResources().getString(R.string.psw_not_match));
            binding.ConfirmpasswordError.setVisibility(View.VISIBLE);
            binding.ConfirmpasswordError.setText(getResources().getString(R.string.psw_not_match));
            return false;
        }
        binding.Confirmpassword.setError(null);
        binding.ConfirmpasswordError.setVisibility(View.GONE);
        return true;
    }





}