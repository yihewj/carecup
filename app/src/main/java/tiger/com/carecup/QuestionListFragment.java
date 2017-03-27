package tiger.com.carecup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;



public class QuestionListFragment extends Fragment {
    private static final String TAG = "QuestionListFragment";

    int page = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.question_list, container, false);
        RecyclerView questionListView = (RecyclerView) v.findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        questionListView.setLayoutManager(layoutManager);

        final QuestionViewAdapter adapter = new QuestionViewAdapter(getContext(), questionListView);
        questionListView.setAdapter(adapter);
        questionListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        downloadPage(adapter, page++);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                adapter.onLoading();
                downloadPage(adapter, page++);
            }
        });



        return v;
    }




    void downloadPage(QuestionViewAdapter adapter, int pageNum) {
        Log.d(TAG, "pullPageData: ");

        CupJsonArrayRequest request = new CupJsonArrayRequest("https://careercup.com/page?n="+pageNum, new QuestionReqResponse(adapter, pageNum), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString()); //Timeout here. Need add something here.

            }
        });

        Volley.newRequestQueue(getContext()).add(request);
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
            /*boolean changed = questionCache.addNewQuestions(response);
            Log.d(TAG, "response with changed = " + changed);
            adapter.stopLoading();
            if (changed) {
                adapter.showPage(pageNum);
            }*/
            adapter.loadData(response, pageNum);
            adapter.notifyDataSetChanged();
        }
    }

}
