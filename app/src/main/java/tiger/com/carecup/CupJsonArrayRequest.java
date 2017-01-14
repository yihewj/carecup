package tiger.com.carecup;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;



import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tiger on 12/28/2016.
 */

public class CupJsonArrayRequest extends JsonArrayRequest {
    String url;

    public CupJsonArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
        this.url = url;

    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Document doc = Jsoup.parse(jsonString);
       //     String text = doc.select("li.detail").get(0).select("span.entry").get(0).select("a[href]").get(0).text();

            JSONArray result = new JSONArray();
            Elements questionElements = doc.select("li.question");
            int nums =  questionElements.size();
            for (int i = 0; i < nums; i++) {
                CupcarrerQuestion question = new CupcarrerQuestion();
                Elements entrys = questionElements.get(i).select("span.entry");
                if (entrys.size() == 0 ||  entrys.get(0).select("p").size() ==0) {
                    continue;
                }
                String detail = entrys.get(0).select("p").get(0).toString();
                String questionurl = url + "\\" + entrys.get(0).select("a[href]").get(0).attr("href");
                int commentsCount = Integer.parseInt(questionElements.get(i).select("span.ratingAndFav").get(0).select("span.commentCount").get(0).text());
                question.setDetails(detail);
                question.setLink(questionurl);
                question.setCommentCount(commentsCount);

                Elements authorsElements = entrys.get(0).select("span.author");
                if (authorsElements.size() > 0) {
                    String author = authorsElements.select("a[href]").get(0).text();
                    question.setAuthor(author);
                    String timeStr = authorsElements.select("abbr.timeago").attr("title");
                    String shortDate = authorsElements.select("abbr.timeago").text();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                    try {
                        Date date = format.parse(timeStr);
                        question.setSubmitDate(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    question.setShortDate(shortDate);
                }




                result.put(question);
            }

            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }


}
