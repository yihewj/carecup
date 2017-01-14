package tiger.com.carecup;

import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView txtView;
    RelativeLayout relativeLayout;
    QuestionViewAdapter adapter;
    RecyclerView.LayoutManager questionLayoutManager;
    RecyclerView questionView;
    QuestionCache questionCache;
    int page = 1;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Arslan", "onStart");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        questionCache = QuestionCache.getInstance();
        Log.d("Arslan", "onCreate");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        questionView = (RecyclerView) findViewById(R.id.recyclerview1);
        questionLayoutManager = new LinearLayoutManager(getApplicationContext());
        questionView.setLayoutManager(questionLayoutManager);

        relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
        adapter = new QuestionViewAdapter(getApplicationContext(), questionView);
        LoadDiskTask loadCacheTask = new LoadDiskTask();
        loadCacheTask.execute(questionCache, adapter);


        questionView.setAdapter(adapter);
        questionView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        adapter.showPage(page);
        pullPageData(adapter, page++);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                adapter.onLoading();
                pullPageData(adapter, page++);

            }
        });
    }

    class LoadDiskTask extends AsyncTask<Object, Void, QuestionViewAdapter> {
        QuestionCache cache;


        @Override
        protected QuestionViewAdapter doInBackground(Object... params) {
            cache = (QuestionCache) params[0];

            cache.restoreFromDisk(getApplicationContext());
            return (QuestionViewAdapter) params[1];
        }

        @Override
        protected void onPostExecute(QuestionViewAdapter adapter) {
            adapter.showPage(page);
            super.onPostExecute(adapter);
        }
    }

    void pullPageData(QuestionViewAdapter adapter, int pageNum) {
        Log.d("Arslan", "Load page "+ pageNum);

        CupJsonArrayRequest request = new CupJsonArrayRequest("https://careercup.com/page?n="+pageNum, new QuestionReqResponse(adapter, pageNum), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Arslan", error.toString()); //Timeout here. Need add something here.

            }
        });

        Volley.newRequestQueue(this).add(request);
    }

    class QuestionReqResponse implements Response.Listener<JSONArray> {
        QuestionViewAdapter adapter;
        int pageNum;
        QuestionReqResponse(QuestionViewAdapter adapter, int pageNum) {
            this.adapter = adapter;
            this.pageNum = pageNum;
        }

        @Override
        public void onResponse(JSONArray response) {
            boolean changed = questionCache.addNewQuestions(response);
            Log.d("Arslan", "response with changed = " + changed);
            adapter.stopLoading();
            if (changed) {
                adapter.showPage(pageNum);
            }
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

    @Override
    protected void onPause() {
        Log.d("Arslan", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Arslan", "onStop");
        questionCache.syncToDisk(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("Arslan", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d("Arslan", "onResume");
        super.onRestart();
    }

    @Override
    protected void onRestart() {
        Log.d("Arslan", "onRestart");
        super.onRestart();
    }

}
