package com.eroglu.sevk;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;




import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public String ArticelName;
    public String ArticelId;
    ProgressDialog mProgressDialog;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> taleplerimList;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Siparişler");





        getSupportActionBar().setLogo(R.drawable.photo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        new getTaleplerim().execute();

        taleplerimList = new ArrayList<>();
        lv = findViewById(R.id.lv);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                String Articel = ((TextView) view.findViewById(R.id.ArticelId)).getText().toString();
                String ArticelName = ((TextView) view.findViewById(R.id.ArticelName)).getText().toString();

                Intent intent = new Intent(view.getContext(), ArticelDetailActivity.class);
                intent.putExtra("ArticelId", Articel);
                intent.putExtra("ArticelName", ArticelName);
                startActivity(intent);
                overridePendingTransition(R.anim.sl, R.anim.sr);
            }

        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                mProgressDialog = new ProgressDialog(MainActivity.this, R.style.Theme_Design_BottomSheetDialog);

                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

                return true;
            }
        });


    }



    public void openCustomers(MenuItem item) {



        Intent t = new Intent(this, CorpList.class);
         startActivity(t);
        overridePendingTransition(R.anim.sl, R.anim.sr);
     }

    public void openWayBils(MenuItem item) {



        Intent t = new Intent(this, CorpArticelActivity.class);
        startActivity(t);
    }



    public void Gallery(MenuItem item) {
        Log.d(TAG, "Galeri Tıklandı");
    }



    public void Photo(MenuItem item) {
        Log.d(TAG, "Foto Tıklandı");
    }

    public void Share(MenuItem item) {
        Log.d(TAG, "Paylaş Tıklandı");
    }

    private class getTaleplerim extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this, R.style.Theme_Design_BottomSheetDialog);
            mProgressDialog.setTitle("Siparişler Getiriliyor");
            mProgressDialog.setMessage("Lütfen Bekleyin");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPConnection sh = new HTTPConnection();
            String jsonStr = String.valueOf(sh.makeServiceCall("Articels"));
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray taleplerim = jsonObject.getJSONArray("Result");
                    for (int i = 0; i < taleplerim.length(); i++) {
                        JSONObject t = taleplerim.getJSONObject(i);
                        String ArticelId = t.getString("id");
                        String ArticelName = t.getString("ArticelName");
                        String CustomerName = t.getString("CustomerName");
                        HashMap<String, String> talepler = new HashMap<>();
                        talepler.put("id", ArticelId);
                        talepler.put("Que", "AT - " + ArticelId);
                        talepler.put("ArticelName", ArticelName);
                        talepler.put("CustomerName", CustomerName);
                        taleplerimList.add(talepler);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            getApplicationContext().startActivity(intent);
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        getApplicationContext().startActivity(intent);
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new SimpleAdapter(MainActivity.this, taleplerimList,
                    R.layout.item_list, new String[]{"id", "Que", "ArticelName", "CustomerName"},
                    new int[]{R.id.ArticelId, R.id.Que, R.id.ArticelName, R.id.CustomerName});
            lv.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

}