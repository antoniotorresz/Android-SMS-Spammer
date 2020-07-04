package com.ant.smsspammer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 1;
    private Button btn_chooose, btn_send;
    private TextView tv_times;
    private EditText et_number, et_msj, et_times;

    private String msg = "", number = "";
    private boolean sending = false;
    int veces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        askPermissions();
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS}, 1);
    }

    private void initComponents() {

        btn_chooose = (Button) findViewById(R.id.btn_chooose);
        btn_send = (Button) findViewById(R.id.btn_send);
        tv_times = (TextView) findViewById(R.id.tv_times);

        et_number = (EditText) findViewById(R.id.et_number);
        et_msj = (EditText) findViewById(R.id.et_msj);
        et_times = (EditText) findViewById(R.id.times);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = String.valueOf(et_msj.getText());
                veces = Integer.parseInt(String.valueOf(et_times.getText()));
                if (!msg.equals("null") && number != null && veces > 0) {
                    sending = true;
                    SmsManager sms = SmsManager.getDefault();
                    for (int i = 0; i < veces; i++) {
                        try {
                            sms.sendTextMessage(number, null, msg, null, null);
                            TimeUnit.SECONDS.sleep(1);
                            tv_times.setText(String.valueOf(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    tv_times.setText("");
                }
            }
        });

        btn_chooose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =
                                c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            // Toast.makeText(getApplicationContext(), cNumber, Toast.LENGTH_SHORT).show();
                            this.number = cNumber;
                            et_number.setText(String.valueOf(this.number));
                        }
                    }
                }
        }
    }
}