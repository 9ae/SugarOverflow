package me.valour.sugaroverflow.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static com.android.volley.Response.*;

/**
 * Created by alice on 3/29/16.
 */
public class StackExchangeAPI {

    public static final String STARTER = "STARTER";
    public static final String RECURRING = "RECURRING";

    private final String baseURL = "https://api.stackexchange.com";

    private static StackExchangeAPI instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private int requestsCount = 0; //TODO: remove this var

    private StackExchangeAPI(Context ctx) {
        this.ctx = ctx;
        requestQueue = getRequestQueue();

    }

    /**
     * Get single isntance of RequestQueue
     * @return the RequestQueue
     */
    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            requestQueue.start();
        }
        return requestQueue;
    }

    /**
     * Get single instance of this class
     * @param ctx
     * @return
     */
    public static synchronized StackExchangeAPI getInstance(Context ctx){
        if(instance == null){
            instance = new StackExchangeAPI(ctx);
        }
        return instance;
    }

    /**
     * Add request to queue
     * @param req
     * @param <T>
     */
    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    private void queueJsonRequest(String path,
                                 int method,
                                 JSONObject body,
                                 Response.Listener<JSONObject> responseListener, String tag){

        JsonObjectRequest request = new JsonObjectRequest(method,
                baseURL+path, body, responseListener, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String message = error.getMessage();
                if(message==null){
                    message = "Unknown Error";
                }
                Log.e("VolleyError", message);

            }

        });
        if(tag !=null){
            request.setTag(tag);
        }

        requestsCount++;
        Log.i("^@^", requestsCount+" requests made in "+tag);

        addToRequestQueue(request);

    }

    /**
     * Makes paginated list of android questions
     * sorted by creation date, from oldest to newest
     * and adds it to the request queue
     * @param pageSize entries to return
     * @param page page position to retrieve
     * @param responseListener Response Handler  for this request
     */
    public void listAndroidQuestions(int pageSize, int page, Response.Listener<JSONObject> responseListener) {
        queueJsonRequest("/2.2/questions?" +
                        "pagesize="+pageSize+
                        "&page="+page+
                        "&order=asc"+
                        "&sort=creation"+
                        "&tagged=android"+
                        "&site=stackoverflow",
                Request.Method.GET, null, responseListener, STARTER);
    }

    /**
     * Makes request to get all entries created after provided datetime
     * * and adds it to the request queue
     * @param datetime Datetime (in ms) to fetch entries
     * @param responseListener Response handler for this request
     */
    public void getAndroidQuestionsAfter(long datetime, Response.Listener<JSONObject> responseListener) {
        long after = datetime + 1;
        queueJsonRequest("/2.2/questions?" +
                        "from_date=" + after +
                        "&order=asc" +
                        "&sort=creation" +
                        "&tagged=android" +
                        "&site=stackoverflow",
                Request.Method.GET, null, responseListener, RECURRING);
    }

    /**
     * Clear request queue
     * @param tag All requests with this tag name will be cleared from the queue
     */
    public void clearQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }

}
