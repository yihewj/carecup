package tiger.com.carecup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    View view;
    ViewHolder viewHolder;

    public QuestionViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.quesiton_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CupcarrerQuestion question;

        public ViewHolder (View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.subject_textview);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Onclick " + question.author , Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void addNewQuestion(CupcarrerQuestion question) {
        questions.add(question);
        notifyDataSetChanged();
    }

    public void addBatchQuestions(JSONArray newQuestions) {
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
        ((ViewHolder)holder).textView.setText(questions.get(position).detail);
        ((ViewHolder)holder).question = questions.get(position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }


}
