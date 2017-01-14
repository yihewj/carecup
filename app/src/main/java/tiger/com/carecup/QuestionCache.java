package tiger.com.carecup;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Tiger on 12/30/2016.
 */

public class QuestionCache {
    List<CupcarrerQuestion> caches = new ArrayList<>();
    HashSet<String> cacheSet = new HashSet<>();
    private static QuestionCache instance = new QuestionCache();
    int countsOfPage = 30;
    String cacheFileName = "question.cache";

    Gson gson = new Gson();
    private QuestionCache() {

    }

    public boolean isEmpty() {
        return caches.size() == 0;
    }

    public static QuestionCache getInstance() {
        return instance;
    }

    //Need other thread to sync
    void restoreFromDisk(Context context) {
        Type questionType = new TypeToken<List<CupcarrerQuestion>>() {}.getType();
        try {
            String filePath = context.getFilesDir().getPath().toString()+File.separator+cacheFileName;
            File cacheFile = new File(filePath);
            if (!cacheFile.exists()) {
                Log.d("Arslan", "Cache File "+filePath +" doesn't exist");
                return;
            }

            caches = gson.fromJson(new JsonReader(new FileReader(cacheFile)), questionType);
            for (CupcarrerQuestion question: caches) {
                cacheSet.add(question.link);
            }
            Log.d("Arslan", "Load cache questions "+caches.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    //Need background to sync
    void syncToDisk(final Context context) {
/*        try {
            if (caches.size() == 0){
                return;
            }
            String filePath = context.getFilesDir().getPath().toString()+File.separator+cacheFileName;
            File cacheFile = new File(filePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile, false));
            gson.toJson(caches, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (caches.size() == 0){
                        Log.d("Arslan", "Cache is empty, nothing to save to disk");
                        return;
                    }
                    String filePath = context.getFilesDir().getPath().toString()+File.separator+cacheFileName;
                    File cacheFile = new File(filePath);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile, false));
                    Type questionType = new TypeToken<List<CupcarrerQuestion>>() {}.getType();
                    String json = gson.toJson(caches, questionType);
                    writer.append(json);
                    writer.newLine();
                    writer.close();
                    //gson.toJson(caches, writer);
                    Log.d("Arslan", "Sync to file "+filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JsonIOException e) {
                    Log.d("Arslan", e.toString());
                }
            }
        });
    }

    ArrayList<CupcarrerQuestion> getQuestions(int page) {
        ArrayList<CupcarrerQuestion> result = new ArrayList<>();
        if (caches.size() == 0) {
            return result;
        }
        for (int i = (page-1)*countsOfPage; i < caches.size() && i < page*countsOfPage; i++) {
            result.add(caches.get(i));
        }
        return result;
    }
    //Questions always in sequence
    boolean addNewQuestions(JSONArray questions) {
        boolean changed = false;
        try {
            int start = -1, end = -1;
            for (int i = 0; i < questions.length(); i++) {
                CupcarrerQuestion question = (CupcarrerQuestion) questions.get(i);
                if (cacheSet.contains(question.link)) {
                    if (start != -1) {
                        end = i-1;
                        break;
                    }
                    continue;
                }

                if (start == -1) {
                    start = i;
                }

            }

            if (start != -1 && end == -1) {
                end = questions.length()-1;
            }
            if (start == -1 && end == -1) {
                return changed;
            }

            CupcarrerQuestion startQuestion = (CupcarrerQuestion) questions.get(start);
            CupcarrerQuestion endQuestion = (CupcarrerQuestion) questions.get(end);

            if (caches.size() == 0 || startQuestion.date.before(caches.get(caches.size()-1).date)) {
                for (int i = start; i <= end; i++){
                    caches.add( (CupcarrerQuestion) questions.get(i));
                }
                return true;
            }

            if (endQuestion.date.after(caches.get(0).date)) {
                for (int i = end; i >= start; i--) {
                    caches.add(0, (CupcarrerQuestion) questions.get(i));
                }
                return true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return changed;
    }

}
