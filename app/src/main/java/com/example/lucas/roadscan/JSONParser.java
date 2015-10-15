package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */


        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.util.List;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.utils.URLEncodedUtils;
        import org.apache.http.conn.ConnectTimeoutException;
        import org.apache.http.entity.StringEntity;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicHeader;
        import org.apache.http.params.BasicHttpParams;
        import org.apache.http.params.HttpConnectionParams;
        import org.apache.http.params.HttpParams;
        import org.apache.http.protocol.HTTP;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.util.Log;
        import android.widget.Toast;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method, JSONArray jsonObj) {

        JSONObject jObj =  new JSONObject();
        //jObj.put("array", jsonArr);
        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                Log.d("JSONParser", "Metodo POST");
                // request method is POST

                DefaultHttpClient httpClient = new DefaultHttpClient();;
                HttpPost httpPost = new HttpPost(url);
                StringEntity se = new StringEntity(jsonObj.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setEntity(se);
                //httpPost.setEntity(new UrlEncodedFormEntity(params));


               /* HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
                HttpConnectionParams.setSoTimeout(httpParameters, 3000);*/

                //httpClient = new DefaultHttpClient(httpParameters);

                try{
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();


                    Log.d("JSONParser", "httpClent: "+ httpClient);
                    Log.d("JSONParser", "httpPost: "+ httpPost);
                    Log.d("JSONParser", "httpResponse: "+ httpResponse);
                    Log.d("JSONParser", "httpEntity: "+ httpEntity);
                    Log.d("JSONParser", "is: "+is);
                }catch (ConnectTimeoutException e) {
                    //Here Connection TimeOut excepion
                    Log.d("JSONParser", "timeout!");
                }
            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                //String paramString = URLEncodedUtils.format(params, "utf-8");
                //url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("JSONParser", "Server Response: " + json);

            jObj = new JSONObject(json);
            //Log.d("teste", "jObj: "+ jObj.getString("message"));
        } catch (Exception e) {
            Log.e("JSONParser", "Error converting result " + e.toString());
        }
        return jObj;

    }

}

