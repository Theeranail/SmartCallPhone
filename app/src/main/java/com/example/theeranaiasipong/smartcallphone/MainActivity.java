package com.example.theeranaiasipong.smartcallphone;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theeranaiasipong.smartcallphone.model.NFCconfig;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private NFCconfig nfCconfig;
    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.img_view_main);


        nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);

        nfCconfig = new NFCconfig(MainActivity.this, nfcAdapter, textView, imageView);

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
        } else {
            imageView.setColorFilter(this.getResources().getColor(R.color.colorRed));
            textView.setText("  NFC ถูกปิดอยู่ ");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {

            imageView.setColorFilter(this.getResources().getColor(R.color.colorPrimary));
            textView.setText(" เเตะที่อุปกรณ์ NCF ");
        } else {
            imageView.setColorFilter(this.getResources().getColor(R.color.colorRed));
            textView.setText(" NFC ถูกปิดอยู่ ");
        }
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfCconfig.startNFC();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfCconfig.stopNFC();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {


            Parcelable[] parcelablesMessageArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage ndefMessage = (NdefMessage) parcelablesMessageArray[0];
            if (parcelablesMessageArray != null && parcelablesMessageArray.length > 0) {
                nfCconfig.readTextFromMessgae(ndefMessage);
            } else {
                imageView.setColorFilter(this.getResources().getColor(R.color.colorRed));
                textView.setText(" ไม่พบข้อมูลใน NCF! ");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
