package com.example.theeranaiasipong.smartcallphone.model;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcF;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theeranaiasipong.smartcallphone.MainActivity;
import com.example.theeranaiasipong.smartcallphone.R;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by TheeranaiAsipong on 11/3/2560.
 */

public class NFCconfig {
    private Context context;
    private NfcAdapter nfcAdapter;
    private TextView textView;
    private ImageView imageView;

    public NFCconfig(Context context, NfcAdapter nfcAdapter, TextView textView, ImageView imageView) {
        this.context = context;
        this.nfcAdapter = nfcAdapter;
        this.textView = textView;
        this.imageView = imageView;
    }

    public NFCconfig(Context context, NfcAdapter nfcAdapter, TextView textView) {
        this.context = context;
        this.nfcAdapter = nfcAdapter;
        this.textView = textView;
    }

    public void startNFC() {

        Intent intent = new Intent(context, context.getClass()).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        String[][] techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        IntentFilter[] intentFilters = new IntentFilter[]{ndef};
        if (nfcAdapter == null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter((Activity) context);
        } else {
            nfcAdapter.enableForegroundDispatch((Activity) context, pendingIntent, intentFilters, techListsArray);
        }


    }

    public void stopNFC() {
        nfcAdapter.disableForegroundDispatch((Activity) context);
    }

    public void readTextFromMessgae(NdefMessage ndefMessage) {
        String tagContent = null;
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {

            NdefRecord ndefRecord = ndefRecords[0];
            try {
                tagContent = readText(ndefRecord);
                CallPhone.call(context, tagContent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            imageView.setColorFilter(context.getResources().getColor(R.color.colorYellow));
            textView.setText(" ไม่พบข้อมูลใน NCF! ");
        }
    }

    private String readText(NdefRecord ndefRecord) throws UnsupportedEncodingException {
        byte[] payload = ndefRecord.getPayload();
        String textEncoding = ((payload[0] & 128) == 0 ? "UTF-8" : "UTF-16");
        int languageCodeLength = payload[0] & 0063;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    public void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            if (tag == null) {
                textView.setText(" Tag Object cannot be null! ");
                return;
            }
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                formateTag(tag, ndefMessage);
            } else {

                ndef.connect();
                if (!ndef.isWritable()) {
                    textView.setText("ไม่สามารถเขียน tag ได้");
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                textView.setText(" เขียนข้อมูลเรียบร้อย ");

            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }

    }

    private void formateTag(Tag tag, NdefMessage ndefMessage) {

        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                textView.setText(" Tag is not ndef formatable ");
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            textView.setText(" เขียนข้อมูลเรียบร้อย ");

        } catch (Exception e) {
            Log.e("formateTag", e.getMessage());
        }

    }

    public NdefMessage createNdeMessage(String s) {
        NdefRecord ndefRecord = createTextRecord(s);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }

    public NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");
            final byte[] text = content.getBytes("UTF-8");
            final int laguagaeSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1 + laguagaeSize + textLength);
            outputStream.write((byte) (laguagaeSize & 0x1F));
            outputStream.write(language, 0, laguagaeSize);
            outputStream.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], outputStream.toByteArray());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
