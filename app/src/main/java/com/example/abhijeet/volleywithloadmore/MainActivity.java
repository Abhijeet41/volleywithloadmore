package com.example.abhijeet.volleywithloadmore;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener {
    private Context mContext;
    private Activity mActivity;
    private Button mButtonDo;
    private TextView mTextView;
    private String GetPurchaseDetails = "https://api.stackexchange.com/2.2/answers?page=1&pagesize=50&site=stackoverflow";

    String [] pdf_nameArray;

    private List<SuperHero> listSuperHeroes;
    private int requestCount = 1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    //Volley Request Queue
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        mContext = getApplicationContext();
        mActivity = MainActivity.this;


        listSuperHeroes = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(this);

        getData();


        recyclerView.setOnScrollChangeListener(this);

        //initializing our adapter
        adapter = new CardAdapter(listSuperHeroes, this);
        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

/*        recyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//Ifscrolled at last then
                if (isLastItemDisplaying(recyclerView)) {
//Calling the method getdata again
                    getData();
                }
            }
        });*/


    }
    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer


            requestQueue.add(getAllPurchaseList(requestCount));
            //Incrementing the request counter
            requestCount=+1;
        }



    private StringRequest getAllPurchaseList(int requestCount)
    {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        StringRequest request=new StringRequest(Request.Method.GET, "https://api.stackexchange.com/2.2/answers?page="+requestCount+"&pagesize=100&site=stackoverflow", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.d("response",response);
                    select(response);
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Please Check internet Connection", Toast.LENGTH_SHORT).show();
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map<String,String> params=new HashMap<>();
                params.put("page","1*");
                params.put("pagesize","50");
                params.put("site","stackoverflow");
                return params;
            }
        };
      return request;
    }

    private void select(String jsonresponse) throws JSONException {

        JSONObject jsonObj = new JSONObject(jsonresponse);
        JSONArray items = jsonObj.getJSONArray("items");

        for (int i=0; i<items.length();i++ )
        {
            JSONObject c = items.getJSONObject(i);

            String owner = c.getString("owner");
            Log.d("owner",owner);

            JSONObject object = new JSONObject(owner);
            String display_name = object.getString("display_name");
            Log.d("display_name",display_name);

            String user_id = object.getString("user_id");
            Log.d("user_id",user_id);

            String profileName = object.getString("profile_image");
            Log.d("profile_image",profileName);

           /* String reputation = c.getString("reputation");
            Log.d("reputation",reputation);*/


     /*      String reputation = c.getString("reputation");
           Log.d("name",reputation);*/

           String is_accepted = c.getString("question_id");
           Log.d("is_accepted", is_accepted);

        /*   String display_name = c.getString("is_accepted");
           Log.d("display_name",display_name);*/

            String profile_image = c.getString("answer_id");
            Log.d("profile_image",profile_image);

            SuperHero superHero = new SuperHero();
            superHero.setImageUrl(object.getString("profile_image"));
            superHero.setName(object.getString("display_name"));
            superHero.setPublisher(object.getString("user_id"));

            listSuperHeroes.add(superHero);
            Log.d("listSUperHero", String.valueOf(listSuperHeroes));

        }

        adapter.notifyDataSetChanged();
    }


    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    //Overriden method to detect scrolling
    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //Ifscrolled at last then
        if (isLastItemDisplaying(recyclerView)) {
            //Calling the method getdata again
            getData();
        }
    }

}
