package com.github.CSCE4444ElectronicRestrauntSystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class PayCredit extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_credit);

        // order number
        int orderNumber = getIntent().getExtras().getInt("OrderNumber");
        setTitle("Pay Credit - Order #" + (orderNumber + 1));

        // order amount
        TextView tvTotal = (TextView)findViewById(R.id.tvTotal);
        float total = getIntent().getExtras().getFloat("Total");
        String formattedTotal = String.format("$%.2f", total);
        tvTotal.setText(formattedTotal);
    }

    // call server event
    public void callServer(View view) {
        Intent intent = new Intent(this, CallServer.class);
        startActivity(intent);
    }

    // submit button event
    public void submit(View view) {
        // get order ID
        String orderID = getIntent().getExtras().getString("OrderID");

        // build query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
        query.whereEqualTo("objectId", orderID);

        // run query
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject order, ParseException e) {
                // set card name
                EditText etCardName = (EditText)findViewById(R.id.etCardName);
                String cardName = etCardName.getText().toString();
                order.put("CardName", cardName);

                // set card number
                EditText etCardNumber = (EditText)findViewById(R.id.etCardNumber);
                long cardNumber = Long.valueOf(etCardNumber.getText().toString());
                order.put("CardNumber", cardNumber);

                // set card zip
                EditText etCardZip = (EditText)findViewById(R.id.etCardZip);
                int cardZip = Integer.valueOf(etCardZip.getText().toString());
                order.put("CardZip", cardZip);

                // set gratuity
                EditText etGratuity = (EditText)findViewById(R.id.etGratuity);
                float gratuity = Float.valueOf(etGratuity.getText().toString());
                order.put("Gratuity", gratuity);

                // set paid
                order.put("Paid", true);

                // save the order
                try {
                    order.save();
                } catch (ParseException e2) {
                    // do nothing
                }

                // toast
                Toast.makeText(getApplicationContext(), "Payment Accepted", Toast.LENGTH_LONG).show();

                // survey
                new AlertDialog.Builder(PayCredit.this)
                    .setTitle("Survey")
                    .setMessage("Would you like to take a brief survey about your experience dining with us?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PayCredit.this, CustomerSurvey.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });
    }

}