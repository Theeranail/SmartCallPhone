package com.example.theeranaiasipong.smartcallphone;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.theeranaiasipong.smartcallphone.model.NFCconfig;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private NFCconfig nfCconfig;
    private TextView textview_status;
    private EditText editTextNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextNumber = (EditText) findViewById(R.id.edittext_numberphone);
        textview_status = (TextView) findViewById(R.id.textview_status);

        nfcAdapter = NfcAdapter.getDefaultAdapter(SettingActivity.this);

        nfCconfig = new NFCconfig(SettingActivity.this, nfcAdapter, textview_status);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            textview_status.setText("  ");
        } else {
            textview_status.setText(" NFC ถูกปิดอยู่ ");
        }

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
        textview_status.setText(" ");
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {

            if (editTextNumber.length() < 10 || editTextNumber.length() > 10) {
                textview_status.setText(" ใส่ข้อมูลให้เท่ากับ 10 ตัว");
            } else {

                textview_status.setText(" กำลังเขียนข้อมูล ");
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = nfCconfig.createNdeMessage(editTextNumber.getText().toString());
                nfCconfig.writeNdefMessage(tag, ndefMessage);
            }
        }
    }


}
