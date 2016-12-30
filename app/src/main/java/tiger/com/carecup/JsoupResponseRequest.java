package tiger.com.carecup;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Tiger on 12/19/2016.
 */

public class JsoupResponseRequest extends StringRequest {
    public JsoupResponseRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }
}
