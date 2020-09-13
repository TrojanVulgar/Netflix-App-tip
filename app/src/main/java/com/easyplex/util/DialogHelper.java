package com.easyplex.util;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.easyplex.R;
import com.easyplex.ui.settings.SettingsActivity;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class DialogHelper {



    private DialogHelper(){


    }



    public static void update(@NonNull Context context,String linkUpdate,String version,String updateMessage){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_update_msg);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;


        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.bt_getcode).setOnClickListener(view -> {

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(linkUpdate));
            context.startActivity(i);

        });

        TextView message = dialog.findViewById(R.id.app_update_message);
        TextView appVersion = dialog.findViewById(R.id.app_version);


        ImageView imageView = dialog.findViewById(R.id.app_logo);

        message.setText(updateMessage);
        appVersion.setText(version);

        Tools.loadMiniLogo(context,imageView);

        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }



    public static void erroLogin(@NonNull Context context){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_error_login);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;
        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }



    public static void erroRegister(@NonNull Context context){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_error_register);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;
        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }


    // Show  AlertDialog Warning if no stream
    public static void showNoStreamAvailable(@NonNull Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_about_no_stream);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;


        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    public static void showNoTrailerAvailable(@NonNull Context context, String trailerId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_no_trailer);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;



        dialog.findViewById(R.id.bt_getcode).setOnClickListener(view -> {

            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_SEARCH_BASE_URL + trailerId));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Constants.YOUTUBE_WATCH_BASE_URL + trailerId));
            try {
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }



        });




        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }




    public static void  showWifiWarning(@NonNull Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about_wifi);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;


        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> {
            context.startActivity(new Intent(context, SettingsActivity.class));
            dialog.dismiss();

        });

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    public static void  showPaypalWarning(@NonNull Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_paypal_warning);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;


        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    public static void  showPremuimWarning(@NonNull Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_premuim);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;


        dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

         dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


}
