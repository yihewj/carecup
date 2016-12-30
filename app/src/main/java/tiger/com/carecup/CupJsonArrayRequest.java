package tiger.com.carecup;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;



import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;

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
            int nums =  doc.select("li.question").size();
            JSONArray result = new JSONArray();
            for (int i = 0; i < nums; i++) {
                CupcarrerQuestion question = new CupcarrerQuestion();
                String detail = doc.select("li.question").get(i).select("span.entry").get(0).select("a[href]").get(0).text();
                String questionurl = url + "\\" + doc.select("li.question").get(0).select("span.entry").get(0).select("a[href]").get(0).attr("href");
                int commentsCount = Integer.parseInt(doc.select("li.question").get(i).select("span.ratingAndFav").get(0).select("span.commentCount").get(0).text());
                String author = doc.select("li.question").get(i).select("span.entry").get(0).select("span.author").select("a[href]").get(0).text();
                question.setDetails(detail);
                question.setLink(questionurl);
                question.setCommentCount(commentsCount);
                question.setAuthor(author);
                result.put(question);
            }

            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }


}
