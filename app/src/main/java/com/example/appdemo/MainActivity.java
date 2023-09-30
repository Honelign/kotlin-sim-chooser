package com.example.appdemo;



import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.appdemo.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Button pickSimButton;
    private static final int PERM_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSimSlotPicker();
            }
        });
    }

    private void launchSimSlotPicker() {
        // Get SubscriptionManager instance
        SubscriptionManager subMgr = getSystemService(SubscriptionManager.class);

        // Get list of all SIM info
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)  {


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERM_REQUEST_CODE);

        } else {
            List<SubscriptionInfo> subs = subMgr.getActiveSubscriptionInfoList();

// Convert to array of CharSequence
            CharSequence[] subNames = new CharSequence[subs.size()];
            for(int i=0; i < subs.size(); i++) {
                subNames[i] = subs.get(i).getDisplayName();
            }

// Now pass this array to setItems()
//            new AlertDialog.Builder(this)
//                    .setItems(subNames, new DialogInterface.OnClickListener() {
//                        // ...
//                    })
//                    .show();
           // List<SubscriptionInfo> subs = subMgr.getActiveSubscriptionInfoList();

            // Show dialog with list of SIMs
            new AlertDialog.Builder(this)
                    .setTitle("Pick SIM for calling")
                    .setItems(subNames, (dialog, which) -> {
                        // Get selected subId
                        int subId = subs.get(which).getSubscriptionId();
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                            makeCall(subId);  // Make call

                        }
                        else {

                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERM_REQUEST_CODE);

                        }
                        // Make direct call using selected SIM

                    })
                    .show();
        }
    }
    private void makeCall(int subId) {
        // Create intent to make call
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:1234567890")); // Number to call

        // Set SIM slot to use
        intent.putExtra(SubscriptionManager.EXTRA_SLOT_INDEX, subId);

        // Launch dialer
        startActivity(intent);
    }
}

