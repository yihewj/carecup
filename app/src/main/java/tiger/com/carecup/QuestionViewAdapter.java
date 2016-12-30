package tiger.com.carecup;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Tiger on 12/28/2016.
 */

public class QuestionViewAdapter extends RecyclerView.Adapter {
    ArrayList<CupcarrerQuestion> questions = new ArrayList<>();
    Context context;


    private final int VIEWPROG = 0;
    private final int VIEWQUESTION = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public QuestionViewAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        }
    }



    @Override
    public int getItemViewType(int position) {
        if (questions.get(position) == null) {
            return VIEWPROG;
        } else {
            return VIEWQUESTION;
        }
    }

    public void onLoading(){
        loading = true;
        questions.add(null);
        notifyItemInserted(questions.size()-1);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEWQUESTION) {
            View v = LayoutInflater.from(context).inflate(R.layout.question_cards_item, parent, false);
            viewHolder = new QuestionViewHolder(v);
            return viewHolder;
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.progress_item, parent,false);
            viewHolder = new ProgressViewHolder(v);
            return viewHolder;
        }
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CupcarrerQuestion question;

        public QuestionViewHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.subject_card_textview);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Onclick " + question.author , Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public static class ProgressViewHolder extends  RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        }
    }


    public void addBatchQuestions(JSONArray newQuestions) {
        if (loading) {
            loading = false;
            questions.remove(questions.size() - 1);
        }

        try {
            for (int i = 0; i < newQuestions.length(); i++) {
                questions.add((CupcarrerQuestion) newQuestions.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof QuestionViewHolder) {
            ((QuestionViewHolder) holder).textView.setText(questions.get(position).detail);
            ((QuestionViewHolder) holder).question = questions.get(position);
        } else {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }



    @Override
    public int getItemCount() {
        return questions.size();
    }


}

