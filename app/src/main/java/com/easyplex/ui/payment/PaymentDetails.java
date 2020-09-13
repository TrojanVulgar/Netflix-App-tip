package com.easyplex.ui.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.easyplex.R;
import com.easyplex.databinding.ActivityPaymentDetailsBinding;
import com.easyplex.ui.login.LoginViewModel;
import com.easyplex.ui.splash.SplashActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
import javax.inject.Inject;
import dagger.android.AndroidInjection;

/**
 * HoneyStream - Android Movie Portal App
 * @package     HoneyStream - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class PaymentDetails extends AppCompatActivity {



    ActivityPaymentDetailsBinding binding;

    private LoginViewModel loginViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_details);


        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);


        Intent intent = getIntent();


        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(intent.getStringExtra("PaymentDetails")));
            showDetails(jsonObject.getJSONObject("response"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        binding.btnStartWatching.setOnClickListener(view -> {

            startActivity(new Intent(PaymentDetails.this, SplashActivity.class));
            finish();

        });

    }

    private void showDetails(JSONObject response) throws JSONException {


            binding.paymentId.setText(response.getString("id"));
            binding.paymentStatus.setText(response.getString("state"));
            loginViewModel.getUpgrade();
            loginViewModel.authUpgradeMutableLiveData.observe(this, userUpgrade -> {


                        if (userUpgrade !=null) {


                            Toast.makeText(PaymentDetails.this, "Your Account Has been Upgraded !", Toast.LENGTH_SHORT).show();


                        }else {


                            Toast.makeText(PaymentDetails.this, "An error occurred , please contact us !", Toast.LENGTH_SHORT).show();


                        }


                    });


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, SplashActivity.class));
        finish();

    }
}