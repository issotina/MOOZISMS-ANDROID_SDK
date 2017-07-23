package com.craftsman.moozisms;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


/**
 * Created by ALI SHADAÏ (Software Craftsman) on 02/07/2017.
 */

public class MOOZISMS {

   private String ACCOUNT_SID;
   private String targetURL = "http://api.moozisms.com";
   private String from = "MOOZISMS";
   private String AUTH_TOKEN ;
    private Context context;

    public MOOZISMS() {
    }

    /***
     * Get params to your dashboard on https://dashboard.moozisms.com
     * @param ACCOUNT_SID your account SID
     * @param AUTH_TOKEN your account token
     */
    public MOOZISMS(Context ctx,String ACCOUNT_SID,  String AUTH_TOKEN) {
        this.context = ctx;
        this.ACCOUNT_SID = ACCOUNT_SID;
        this.AUTH_TOKEN = AUTH_TOKEN;
    }


    /***
     *
     * @param to
     * @param from alphanumeric sender ID
     * @param msg message content
     * @param callback background task handler
     */
    public void  sendSms(final String to, final String from, final String msg, @Nullable final Callback callback){
        checkSafety();
        this.from = from;
        final String receiverId = cleanReceiverId(to);

        new AsyncTask<Void, Void, Boolean>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            protected Boolean doInBackground(Void... voids) {
                return  sendSMS(receiverId,msg);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
               callback.onFinish(aBoolean);
            }
        }.execute();


    }

    private void checkSafety() {
        if(ACCOUNT_SID.isEmpty()) try {
            throw new Exception("Your ACCOUNT SID is required. You can find it on moozisms dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(AUTH_TOKEN.isEmpty()) try {
            throw new Exception("Your AUTHENTICATION TOKEN SID is required. You can find it on moozisms dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!isGrantedInternetPermission()) try {
            throw new Exception("Check Internet permissions in your manifest");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String cleanReceiverId(String to) {
         if(to.startsWith("+")) to = to.substring(1);
        if(to.startsWith("00"))  to = to.substring(2);
        return to;
    }
    private  boolean sendSMS(String to, String msg) {

        int responseCode;

        to = to.replace("-", "");

        try {

            byte[] ptext = msg.getBytes("ISO-8859-1");
            String value = new String(ptext, Charset.forName("UTF-8"));

            String datatype = "json";  //String datatype = "xml";
            String urlParameters = "api_key="+ACCOUNT_SID+
                    "&api_secret="+AUTH_TOKEN+
                    "&to="+to+
                    "&from="+from+
                    "&text="+value+
                    "&datatype="+datatype;
            System.out.println("param "+urlParameters);
            //String params = "api_key=123456&api_secret=123456&to=22892520119&from=MOM&text=cc ici&datatype=json";
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;
            URL restServiceURL = new URL(targetURL);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setInstanceFollowRedirects(false);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConnection.setRequestProperty("charset", "utf-8");
            httpConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            httpConnection.setUseCaches(false);
            try  {
                DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
                wr.write(postData);
            }catch (Exception e){
                e.printStackTrace();
            }

             responseCode = httpConnection.getResponseCode();
            String responseMessage = httpConnection.getResponseMessage();

            System.out.println("Response code : " + responseCode + " Response message :" + responseMessage);
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            String buffLine;
            String output = "";
            System.out.println("Output from Server:  \n");

            while ((buffLine = responseBuffer.readLine()) != null) {
                //System.out.println(buffLine);
                output += buffLine;
            }
            System.out.println("Response du serveur : \n"+output);
            httpConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return responseCode == 200;

    }

    /***
     * Async task response handler
     */
    public interface Callback{
         void onFinish(boolean isSucces);
    }


    private boolean isGrantedInternetPermission() {

        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public Context getContext() {
        return context;
    }

}
