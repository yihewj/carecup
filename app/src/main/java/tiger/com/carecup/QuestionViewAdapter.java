package tiger.com.carecup;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tiger on 12/28/2016.
 */

public class QuestionViewAdapter extends RecyclerView.Adapter {
    ArrayList<CupcarrerQuestion> questions = new ArrayList<>();
    Context context;
    LinkedList<QuestionViewHolder> questionViewHolders = new LinkedList<>();
    HashSet<String> questionSet = new HashSet<>();
    QuestionCache cache = QuestionCache.getInstance();
    private final int VIEWPROG = 0;
    private final int VIEWQUESTION = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    int lastPage = 1;

    public QuestionViewAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    int firstVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (newState == 2 && firstVisiblePosition == 0) {
                        Log.i("Arslan", "Scroll top trying now");
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                 //   Log.i("Arslan", "Total count " + totalItemCount + " Last visible Item "+lastVisibleItem + "(dx, dy) = "+dx +"," +dy + " First position "+ firstVisiblePosition);
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            loading = true;
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    onLoadMoreListener.onLoadMore();
                                }
                            });

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
       // public TextView textView;
        public CupcarrerQuestion question;
        //public WebView webView;
        public TextView detailView;
        public TextView submitDateView;

        public QuestionViewHolder(View v) {
            super(v);
       //     textView = (TextView)v.findViewById(R.id.subject_card_textview);
            detailView = (TextView)v.findViewById(R.id.subject_card_textView);
          //  webView = (WebView) v.findViewById(R.id.subject_card_webView);
            submitDateView = (TextView) v.findViewById(R.id.submit_date);


           /* textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Onclick " + question.author , Toast.LENGTH_SHORT).show();
                }
            });*/

        }
    }

    public static class ProgressViewHolder extends  RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        }
    }

    public static final int HEAD = 0;
    public static final int TAIL = 1;


    public void addBatchQuestions(JSONArray newQuestions, int direction) {
        if (loading) {
            loading = false;
            questions.remove(questions.size() - 1);
        }

        try {
            for (int i = 0; i < newQuestions.length(); i++) {
                if (direction == HEAD) {
                    questions.add((CupcarrerQuestion) newQuestions.get(i));
                } else {
                    questions.add(i, (CupcarrerQuestion) newQuestions.get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void stopLoading() {
        if (loading) {
            loading = false;
            questions.remove(questions.size() - 1);
            notifyItemRemoved(questions.size());
        }
    }



    public void showPage(int pageNum) {

        if (lastPage == -1) {

            List<CupcarrerQuestion> cacheQuestions = cache.getQuestions(pageNum);

            if (cacheQuestions.size() == 0) {
                return;
            }

            for (CupcarrerQuestion question : cacheQuestions) {
                questions.add(question);
                questionSet.add(question.link);
            }
            notifyDataSetChanged();
            lastPage = pageNum;
            return;
        }

        if (pageNum == 1) {
            List<CupcarrerQuestion> cacheQuestions = cache.getQuestions(pageNum);
            if (cacheQuestions.size() == 0) {
                return;
            }
            boolean changed = false;
            for (int i = 0; i < cacheQuestions.size(); i++) {
                if (questionSet.contains(cacheQuestions.get(i).link)) {
                    break;
                }
                questions.add(i, cacheQuestions.get(i));
                changed = true;
            }
            if (changed) {
                notifyDataSetChanged();
            }
            return;
        }

        if (pageNum > 1 && pageNum >= lastPage) {
            List<CupcarrerQuestion> cacheQuestions = cache.getQuestions(pageNum);
            if (cacheQuestions.size() == 0) {
                return;
            }
            boolean changed = false;
            for (int i = 0; i < cacheQuestions.size(); i++) {
                if (questionSet.contains(cacheQuestions.get(i).link)) {
                    continue;
                }
                questions.add(cacheQuestions.get(i));
                changed = true;
            }
            if (changed) {
                notifyDataSetChanged();
            }
            lastPage = pageNum;
            return;
        }

    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof QuestionViewHolder) {
        //    ((QuestionViewHolder) holder).textView.setText(questions.get(position).detail);
         //   ((QuestionViewHolder) holder).textView.setVisibility(View.GONE);
/*            ((QuestionViewHolder) holder).webView.getSettings().setJavaScriptEnabled(true);
            ((QuestionViewHolder) holder).webView.getSettings().setDomStorageEnabled(true);
           ((QuestionViewHolder) holder).webView.loadData(questions.get(position).detail, "text/html", null);*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((QuestionViewHolder) holder).detailView.setText(Html.fromHtml(questions.get(position).detail, Html.FROM_HTML_MODE_COMPACT));
            } else {
                ((QuestionViewHolder) holder).detailView.setText(Html.fromHtml(questions.get(position).detail));
            }
            ((QuestionViewHolder) holder).question = questions.get(position);
            ((QuestionViewHolder) holder).submitDateView.setText(questions.get(position).shortDate);
        } else {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }



    @Override
    public int getItemCount() {
        return questions.size();
    }


}

