package com.asndeveloper.expensestracker.ExpensesActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.asndeveloper.expensestracker.Database.ExpenseDB;
import com.asndeveloper.expensestracker.Database.ExpensesEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class scansms extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        String format = bundle.getString("format");
        if (pdus == null) return;

        for (Object pdu : pdus) {

            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            String msgBody = sms.getMessageBody();

            // Filter only bank/UPI messages
            if (!(msgBody.contains("UPI") ||
                    msgBody.contains("debited") ||
                    msgBody.contains("credited") ||
                    msgBody.contains("₹") ||
                    msgBody.contains("Rs"))) {
                continue;
            }

            // Extract Amount
            String amount = (extractAmount(msgBody));
            if (amount.equals("0") || amount.trim().isEmpty()) return;


            // Save Transaction
            saveToDatabase(context, amount);
        }
    }

    private void saveToDatabase(Context context, String amount) {

        ExpensesEntity entity = new ExpensesEntity();

        entity.setTitle("UPI Auto Debit"+amount);     // clean title
        entity.setCategory("UPI");             // IMPORTANT for total & chart
        entity.setAmount(amount);              // save as string correctly
        entity.setTime(String.valueOf(System.currentTimeMillis()));  // correct time

        ExpenseDB.getInstance(context)
                .expensesDAO()
                .insert(entity);
    }

    // Extract formatted ₹1,500 | Rs 250 | Rs. 1,250.50 | 450.75
    private String extractAmount(String text) {

        Pattern p1 = Pattern.compile("(₹|Rs\\.?\\s?)([0-9,]+\\.?[0-9]*)",
                Pattern.CASE_INSENSITIVE);

        Matcher m1 = p1.matcher(text);
        if (m1.find()) {
            return m1.group(2).replace(",", "");
        }

        Pattern p2 = Pattern.compile("[0-9,]+\\.?[0-9]*");
        Matcher m2 = p2.matcher(text);

        if (m2.find()) {
            return m2.group(0).replace(",", "");
        }

        return "0";
    }
}
