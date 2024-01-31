package com.example.a2024aex1322985441;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Tools {
    public static ArrayList<String> getContactList(boolean granted, ContentResolver cr, Context context) {// i dont use the return value because i dont need
        if(!granted){return null;}
        int count=0;
        String id,name;
        ArrayList<String>arr=new ArrayList<>();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                int cIndex = cur.getColumnIndex(ContactsContract.Contacts._ID);
                if (cIndex != -1) {
                    id = String.valueOf(cIndex);
                } else {
                    return null;
                }
                cIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                if (cIndex != -1) {
                    name = cur.getString(cIndex);
                } else {
                    return null;
                }
                cIndex = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                if (cur.getInt(cIndex) > 0 && cIndex > -1) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (name.indexOf('א') == 0) {
                       /* while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(cIndex);
                            Log.i("pttt", "Name: " + name);
                            Log.i("pttt", "Phone Number: " + phoneNo);*/
                            arr.add(name);
                        }
                    }
                   // pCur.close();
                }
            }

        if (cur != null) {
            cur.close();
        }
        return arr;

    }

}
