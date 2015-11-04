package com.github.CSCE4444ElectronicRestrauntSystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;

// the activity used for the main food menu
public class MenuMain extends AppCompatActivity {
    static final int REQUEST_ADD_ITEM = 0;
    static final int REQUEST_SUBMIT_ORDER = 1;

    View currentCategory;

    // activity creation event
    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_menu_main);
        currentCategory = findViewById(R.id.bAppetizers);
        switchCategory(currentCategory);
    }

    // switch category event
    public void switchCategory(View view) {

        // enable all the buttons
        currentCategory.setEnabled(true);
        currentCategory = view;

        // disable this button
        view.setEnabled(false);

        // create parse query of menu items
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MenuItem");
        query.whereEqualTo("Avalibility", true);

        // modify query based on which category button is used
        switch (view.getId()) {
            case R.id.bAppetizers:
                setTitle("Menu - Appetizers");
                query.whereEqualTo("Type", "Appatizer");
                break;
            case R.id.bFavorites:
                setTitle("Menu - Favorites");
                query.whereNotEqualTo("Type", "Drink");
                query.addDescendingOrder("Frequency");
                query.setLimit(3);
                break;
            case R.id.bEntrees:
                setTitle("Menu - Entrees");
                query.whereEqualTo("Type", "Entree");
                break;
            case R.id.bDesserts:
                setTitle("Menu - Desserts");
                query.whereEqualTo("Type", "Dessert");
                break;
            case R.id.bDrinks:
                setTitle("Menu - Drinks");
                query.whereEqualTo("Type", "Drink");
                break;
        }

        // run the query in the background, then create and set the adapter
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> menuItems, ParseException e) {
                MenuAdapter adapter = new MenuAdapter(menuItems);
                ListView lvMenu = (ListView)findViewById(R.id.lvMenu);
                lvMenu.setAdapter(adapter);
            }
        });
    }


    // add item event
    public void addItem(View view) {
        Intent intent = new Intent(this, AddItem.class);
        TextView tvItemName = (TextView)view.findViewById(R.id.tvItemName);
        String itemName = tvItemName.getText().toString();
        intent.putExtra("ItemName", itemName);
        startActivityForResult(intent, REQUEST_ADD_ITEM);
    }


    // call server event
    public void callServer(View view) {
        Intent intent = new Intent(this, CallServer.class);
        startActivity(intent);
    }


    // submit order activity
    public void submitOrder (View view) {
        Intent intent = new Intent(this, SubmitOrder.class);
        startActivity(intent);
    }


    // activity result event
    @Override public void onActivityResult(int id, int result, Intent intent) {
        if (id == REQUEST_ADD_ITEM && result == Activity.RESULT_OK) {
            String itemName = intent.getExtras().getString("ItemName");
            String request = intent.getExtras().getString("Request");
            float price = intent.getExtras().getFloat("Price");
            MainApplication application = (MainApplication)getApplicationContext();
            LinkedList<OrderItem> currentOrder = application.currentOrder;
            currentOrder.addLast(new OrderItem(itemName, request, price));
            String toast = itemName + " added to order.";
            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
            switchCategory(currentCategory);
        }
    }


    // nested class used for the menu adapter
    private class MenuAdapter extends ArrayAdapter<ParseObject> {

        // constructor
        public MenuAdapter(List<ParseObject> objects) { super(MenuMain.this, 0, objects); }

        // function called whenever the list is created or scrolled
        @Override public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.activity_menu_item, parent, false);
            }

            // get the current item
            ParseObject item = getItem(position);

            // get pictures from parse in background
            ImageView ivPicture = (ImageView)view.findViewById(R.id.ivPicture);
            ivPicture.setImageBitmap(null);
            item.getParseFile("Picture").getDataInBackground(new GetDataCallback() {
                private ImageView ivPicture;

                private GetDataCallback initialize(ImageView ivPicture) {
                    this.ivPicture = ivPicture;
                    return this;
                }

                @Override public void done(byte[] data, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ivPicture.setImageBitmap(bitmap);
                }
            }.initialize(ivPicture));

            // get item name
            TextView tvItemName = (TextView)view.findViewById(R.id.tvItemName);
            String itemName = item.getString("ItemName");
            tvItemName.setText(itemName);



            // get price
            TextView tvPrice = (TextView)view.findViewById(R.id.tvPrice);
            float price = item.getNumber("Price").floatValue();
            String formattedPrice = String.format("$%.2f", price);
            if (formattedPrice.equals("$0.00")) formattedPrice = "Free";
            tvPrice.setText(formattedPrice);

            // get description
            TextView tvDescription = (TextView)view.findViewById(R.id.tvDescription);
            String description = item.getString("Description");
            tvDescription.setText(description);

            // return the view
            return view;
        }
    }
}