package com.easyplex.ui.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.easyplex.R;
import com.easyplex.databinding.ActivitySettingBinding;
import com.easyplex.ui.login.LoginViewModel;
import com.easyplex.ui.manager.AdsManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.mylist.MoviesListViewModel;
import com.easyplex.ui.payment.PaymentDetails;
import com.easyplex.ui.profile.EditProfileActivity;
import com.easyplex.ui.splash.SplashActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.Tools;
import com.google.firebase.messaging.FirebaseMessaging;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

import static com.easyplex.R.string.build_bersion;
import static com.easyplex.util.Constants.AUTO_PLAY;
import static com.easyplex.util.Constants.PAYMENT;
import static com.easyplex.util.Constants.PREMUIM;
import static com.easyplex.util.Constants.SUBS_SIZE;
import static com.easyplex.util.Constants.SWITCH_PUSH_NOTIFICATION;
import static com.easyplex.util.Constants.WIFI_CHECK;

public class SettingsActivity extends AppCompatActivity {


    ActivitySettingBinding binding;


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MoviesListViewModel moviesListViewModel;


    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TokenManager tokenManager;

    @Inject
    SettingsManager settingsManager;

    @Inject
    AdsManager adsManager;


    private static final int PAYPAL_REQUEST_CODE = 7777;


    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);


        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);

        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        onSetupPaypal();
        onLoadAppLogo();
        setButtonsUtilities();
        onLoadAppBar();
        onCheckAuth();
        onLogout();
        onLoadAboutUs();
        onLoadPrivacyPolicy();
        onClearCache();
        onLoadEditProfile();
        onClearRoomDatabase(moviesListViewModel);
    }

    private void onLoadEditProfile() {

        binding.btnEditProfile.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class)));
    }


    private void onClearRoomDatabase(MoviesListViewModel moviesListViewModel) {

        binding.ClearMyList.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_mylist);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {

                moviesListViewModel.deleteAllMovies();

                Toast.makeText(this, "My List has been cleared !", Toast.LENGTH_SHORT).show();

                dialog.dismiss();


            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });


    }

    public void onClearCache() {

        binding.linearLayoutCleaCache.setOnClickListener(v -> {


            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_cache);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {
                deleteCache(this);

                Toast.makeText(this, "The App cache has been cleared !", Toast.LENGTH_SHORT).show();


                dialog.dismiss();

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);


        });


    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteFile(dir);
        } catch (Exception e) {

            Timber.d("Error Deleting : %s", e.getMessage());
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String child : children) {
                    deletedAll = deleteFile(new File(file, child)) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }


    private void onLoadPrivacyPolicy() {


        binding.privacyPolicy.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_gdpr_basic);
            dialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            TextView reportMovieName = dialog.findViewById(R.id.tv_content);
            reportMovieName.setText(settingsManager.getSettings().getPrivacyPolicy());

            dialog.findViewById(R.id.bt_accept).setOnClickListener(v1 -> dialog.dismiss());

            dialog.findViewById(R.id.bt_decline).setOnClickListener(v12 -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);


        });


    }

    private void onLoadAboutUs() {


        // About Us - EasyPlex
        binding.aboutus.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_about);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            ImageView imageView = dialog.findViewById(R.id.logo_aboutus);

            Tools.loadMainLogo(this, imageView);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            TextView tvVersion = dialog.findViewById(R.id.tv_version);

            tvVersion.setText(String.format("%d%s", build_bersion, settingsManager.getSettings().getLatestVersion()));


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(v15 -> {
                if (settingsManager.getSettings().getAppUrlAndroid().isEmpty()) {


                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));

                } else {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));

                }

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(v14 -> dialog.dismiss());

            dialog.findViewById(R.id.app_url).setOnClickListener(v13 -> {

                if (settingsManager.getSettings().getAppUrlAndroid().isEmpty()) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));

                } else {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));

                }

            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });


    }

    private void onLogout() {

        binding.logout.setOnClickListener(v -> {

            tokenManager.deleteToken();
            settingsManager.deleteSettings();
            adsManager.deleteAds();
            moviesListViewModel.deleteAllMovies();
            startActivity(new Intent(this, SplashActivity.class));
            finish();

        });

    }

    private void onCheckAuth() {

        loginViewModel.getAuthDetails();
        loginViewModel.authDetailMutableLiveData.observe(this, auth -> {

            if (auth != null) {

                binding.authName.setText(auth.getName());
                binding.authEmail.setText(auth.getEmail());

                if (auth.getPremuim() == 1) {

                    binding.btnPremuim.setVisibility(View.VISIBLE);
                    binding.updatePlanText.setVisibility(View.GONE);
                    sharedPreferencesEditor.putInt(PREMUIM, 1).apply();

                } else {

                    sharedPreferencesEditor.putInt(PREMUIM, 0).apply();
                    binding.authFree.setVisibility(View.VISIBLE);
                    binding.updatePlanText.setVisibility(View.VISIBLE);

                }

            } else {


                Toast.makeText(this, "Errrrorr", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, SplashActivity.class));
                finish();

            }

        });


    }


    // Load AppBar
    private void onLoadAppBar() {

        Tools.loadAppBar(binding.scrollView, binding.toolbar);

    }


    // Load App Logo
    private void onLoadAppLogo() {

        Tools.loadMiniLogo(getApplicationContext(), binding.logoImageTop);

    }


    // Setup Paypal
    private void onSetupPaypal() {

        binding.updatePlanText.setOnClickListener(v -> {


            if (settingsManager.getSettings().getPaypalAmount() != null && settingsManager.getSettings().getPaypalClientId() != null) {


                PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                        .clientId(settingsManager.getSettings().getPaypalClientId());
                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(settingsManager.getSettings().getPaypalAmount()), "USD", settingsManager.getSettings().getAppName() + "VIP", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                mLauncher.launch(intent);


            } else {


                DialogHelper.showPaypalWarning(this);
            }


        });
    }

    private void setButtonsUtilities() {

        onLoadwifiSwitch();
        onLoadNotificationPushSwitch();
        setAutoPlaySwitch();


        //  Clear Application Cache - EasyPlex


        binding.currentSize.setText(String.format("current size : %s", sharedPreferences.getString(SUBS_SIZE, "16f")));

        binding.substitleSize.setOnClickListener(v -> {

            ArrayList<String> fontSize = new ArrayList<>();


            fontSize.add("10f");
            fontSize.add("12f");
            fontSize.add("14f");
            fontSize.add("16f");
            fontSize.add("20f");
            fontSize.add("24f");
            fontSize.add("30f");

            String[] charSequenceSubsSize = new String[fontSize.size()];
            for (int i = 0; i < fontSize.size(); i++) {
                charSequenceSubsSize[i] = String.valueOf(fontSize.get(i));

            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
            builder.setTitle("Font Size..");
            builder.setCancelable(true);
            builder.setItems(charSequenceSubsSize, (dialogInterface, wich) -> {

                sharedPreferencesEditor.putString(SUBS_SIZE, fontSize.get(wich)).apply();
                sharedPreferences.getString(SUBS_SIZE, "16f");
                binding.currentSize.setText(String.format("Current Size : %s", sharedPreferences.getString(SUBS_SIZE, "16f")));
                dialogInterface.dismiss();


            });

            builder.show();

        });


    }


    private void setAutoPlaySwitch() {

        // Detect AutoPlay ON/OFF Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(AUTO_PLAY, true)) {
            binding.autoplaySwitch.setChecked(false);
        }

        binding.autoplaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(AUTO_PLAY, true).apply();


            } else {

                sharedPreferencesEditor.putBoolean(AUTO_PLAY, false).apply();

            }

        });

    }

    private void onLoadNotificationPushSwitch() {

        // Detect Notification ON/OFF Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(SWITCH_PUSH_NOTIFICATION, false)) {

            binding.switchPushNotification.setChecked(false);
        }


        binding.switchPushNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(SWITCH_PUSH_NOTIFICATION, true).apply();

                FirebaseMessaging.getInstance().subscribeToTopic("/topics/all");

            } else {

                sharedPreferencesEditor.putBoolean(SWITCH_PUSH_NOTIFICATION, false).apply();

                FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/all");

            }

        });

    }

    private void onLoadwifiSwitch() {

        // Detect Wifi-Only Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(WIFI_CHECK, false)) {

            binding.wifiSwitch.setChecked(false);

        }

        binding.wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(WIFI_CHECK, true).apply();

            } else {

                sharedPreferencesEditor.putBoolean(WIFI_CHECK, false).apply();
            }

        });

    }


    // Create this as a variable in your Fragment class
    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == PAYPAL_REQUEST_CODE) {
                    //If the result is OK i.e. user has not canceled the payment
                    if (result.getResultCode() == Activity.RESULT_OK) {


                        //Getting the payment confirmation
                        PaymentConfirmation confirm = result.getData().getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                        Timber.tag("CONFIRM").d(String.valueOf(confirm));
                        //if confirmation is not null
                        if (confirm != null) {
                            try {
                                //Getting the payment details
                                String paymentDetails = confirm.toJSONObject().toString(4);
                                Timber.tag(PAYMENT).d(paymentDetails);
                                Timber.tag(PAYMENT).i(paymentDetails);
                                Timber.tag("Pay Confirm : ").d(String.valueOf(confirm.getPayment().toJSONObject()));
//                        Starting a new activity for the payment details and status to show

                                startActivity(new Intent(SettingsActivity.this, PaymentDetails.class)
                                        .putExtra("PaymentDetails", paymentDetails));

                            } catch (JSONException e) {
                                Timber.e(e, "an extremely unlikely failure occurred : ");
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Timber.i("The user canceled.");
                    } else if (result.getResultCode() == PaymentActivity.RESULT_EXTRAS_INVALID) {
                        Timber.i("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                    }
                }

            });


    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;

    }
}
