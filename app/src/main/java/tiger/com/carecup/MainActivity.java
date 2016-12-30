package tiger.com.carecup;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.helper.HttpConnection;

public class MainActivity extends AppCompatActivity {

    TextView txtView;
    RelativeLayout relativeLayout;
    QuestionViewAdapter adapter;
    RecyclerView.LayoutManager questionLayoutManager;
    RecyclerView questionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



/*        StringRequest stringRequest = new StringRequest("https://www.careercup.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Arslan", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Arslan", error.toString());

            }
        });*/
        //txtView = (TextView) findViewById(R.id.txt);


        questionLayoutManager = new LinearLayoutManager(getApplicationContext());
        questionView = (RecyclerView) findViewById(R.id.recyclerview1);
        relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
        adapter = new QuestionViewAdapter(getApplicationContext());
        questionView.setLayoutManager(questionLayoutManager);

        questionView.setAdapter(adapter);
        questionView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        pullPageData(adapter, 2);


    }

    void pullPageData(QuestionViewAdapter adapter, int pageNum) {
        CupJsonArrayRequest request = new CupJsonArrayRequest("https://careercup.com/page?n="+pageNum, new QuestionReqResponse(adapter), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Arslan", error.toString());

            }
        });

        // Volley.newRequestQueue(this).add(stringRequest);
        Volley.newRequestQueue(this).add(request);
    }

    class QuestionReqResponse implements Response.Listener<JSONArray> {
        QuestionViewAdapter adapter;
        QuestionReqResponse(QuestionViewAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onResponse(JSONArray response) {
            adapter.addBatchQuestions(response);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
