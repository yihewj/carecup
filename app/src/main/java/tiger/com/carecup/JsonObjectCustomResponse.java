package tiger.com.carecup;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by Tiger on 12/19/2016.
 */

public class JsonObjectCustomResponse extends JsonObjectRequest {
    public JsonObjectCustomResponse(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }
}
