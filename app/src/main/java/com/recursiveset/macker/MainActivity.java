package com.recursiveset.macker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.text.TextWatcher;
import android.text.Editable;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String POST_URL = "https://api.mlab.com/api/1/databases/macker_data/collections/transactions?apiKey=OSilFcuakv2pYsKuHBXmlvL_LMhIp2zj";
        final RequestQueue reqQueue = Volley.newRequestQueue(this);

        final EditText nameText = findViewById(R.id.name);
        final EditText amountText = findViewById(R.id.amount);
        final Spinner datetimeSpinner = findViewById(R.id.datetime_spinner);
        final Spinner tagSpinner = findViewById(R.id.tag_spinner);
        final Spinner daySpinner = findViewById(R.id.day);
        final Spinner monthSpinner = findViewById(R.id.month);
        final EditText timeInput = findViewById(R.id.time);

        datetimeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0){
                    daySpinner.setVisibility(View.INVISIBLE);
                    monthSpinner.setVisibility(View.INVISIBLE);
                    timeInput.setVisibility(View.INVISIBLE);
                }else if(position == 1){
                    daySpinner.setVisibility(View.VISIBLE);
                    monthSpinner.setVisibility(View.VISIBLE);
                    timeInput.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });


        final Button pushButton = findViewById(R.id.pushButton);
        pushButton.setBackgroundColor(0xFFDDDDDD);
        pushButton.setClickable(false);
        pushButton.setEnabled(false);

        final TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!nameText.getText().toString().equals("") && !amountText.getText().toString().equals("")){
                    pushButton.setEnabled(true);
                    pushButton.setClickable(true);
                }else{
                    pushButton.setClickable(false);
                    pushButton.setEnabled(false);
                }
            }
            public void afterTextChanged(Editable s) {}
        };


        nameText.addTextChangedListener(watcher);
        amountText.addTextChangedListener(watcher);

        pushButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("tag", tagSpinner.getSelectedItem().toString());
                    if(datetimeSpinner.getSelectedItem().toString().equals("Current")){
                        jsonBody.put("datetime", Calendar.getInstance().getTime().toString());
                    }else{
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(daySpinner.getSelectedItem().toString()));
                        cal.set(Calendar.MONTH, monthSpinner.getSelectedItemPosition());
                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeInput.getText().toString().substring(0, 2)));
                        cal.set(Calendar.MINUTE, Integer.parseInt(timeInput.getText().toString().substring(3, 5)));
                        Date date = cal.getTime();
                        jsonBody.put("datetime", date.toString());
                    }
                    jsonBody.put("name", nameText.getText().toString());
                    jsonBody.put("amount", amountText.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, POST_URL, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Rest Response", response.toString());
                        nameText.setText("");
                        amountText.setText("");
                        pushButton.setText(R.string.push_transaction);
                        pushButton.setBackgroundColor(0xFFDDDDDD);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Rest error", error.toString());
                                pushButton.setText(R.string.push_transaction);
                                pushButton.setClickable(true);
                                pushButton.setEnabled(true);
                                pushButton.setBackgroundColor(0xFFDDDDDD);
                            }
                        }

                );

                pushButton.setText(R.string.pushing_transaction);
                pushButton.setClickable(false);
                pushButton.setEnabled(false);

                pushButton.setBackgroundColor(0xFFFFFFFF);

                reqQueue.add(req);
            }

        });


    }


}