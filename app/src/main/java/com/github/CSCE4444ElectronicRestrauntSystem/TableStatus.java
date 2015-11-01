package com.github.CSCE4444ElectronicRestrauntSystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class TableStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_status);

        Button bStatusSubmit = (Button) findViewById(R.id.bStatusSubmit);
        final Intent i = getIntent();
        //Submits Table status
        //Todo: Add cancel Button
        bStatusSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Finds selected button
                final RadioGroup status = (RadioGroup) findViewById(R.id.rbTables);
                int selectedStatus = status.getCheckedRadioButtonId();
                int tableNum = 0;
                final RadioButton newStatus = (RadioButton) findViewById(selectedStatus);

                //gets table number and finds in database
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Tables");
                query.whereEqualTo("Number", i.getIntExtra("Number", tableNum));
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> tables,
                                     ParseException e) {
                        //Todo: Submit nothing error
                        if (e == null) {
                            for (ParseObject table : tables) {
                                //Puts Status into database
                                Log.d("Tables", " " + newStatus.getText());
                                table.put("Status", newStatus.getText());
                                table.saveInBackground();
                            }
                        } else {
                            //Failed Query Log
                            Log.d("Tables", "Error: " + e.getMessage());
                        }
                    }
                });
                Intent i = new Intent(TableStatus.this, ServerMain.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}