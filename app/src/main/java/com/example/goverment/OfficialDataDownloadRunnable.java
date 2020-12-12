package com.example.goverment;

import android.content.Context;
import android.location.Address;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OfficialDataDownloadRunnable implements Runnable {

    private static final String TAG = "OfficialDownloadDataRunnable";

    private MainActivity mainActivity;
    private String adminarea;
    private List<Official> s;



    private static final String CivicUrl = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    // STOCK_SYMBOL/quote?token=API_KEY"

    //////////////////////////////////////////////////////////////////////////////////
    // Sign up to get your API Key at:  https://home.openweathermap.org/users/sign_up
    private static final String yourAPIKey = "AIzaSyBQDwRg22Dcc-kIkw3gZvfZZNYFAv7Et38";
    //
    //////////////////////////////////////////////////////////////////////////////////

    OfficialDataDownloadRunnable(MainActivity mainActivity, String adminarea) {
        this.mainActivity = mainActivity;
        this.adminarea=adminarea;
    }


    @Override
    public void run() {

        String urlToUse=CivicUrl+yourAPIKey+"&address="+adminarea;

        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();

        try {

            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        parseJson(sb.toString());
        //saveStocks();





    }

    public void handleResults(final String jsonString) {

        System.out.println("null");
    }

    private void parseJson(String s) {


        try {
            JSONObject jObjMain = new JSONObject(s);

            JSONObject jNormalInput = jObjMain.getJSONObject("normalizedInput");

            String locationText = jNormalInput.getString("city") + ", " + jNormalInput.getString("state") + " " + jNormalInput.getString("zip");
            //mainActivity.setLocationText(locationText);
            JSONArray jArrayOffices = jObjMain.getJSONArray("offices");
            JSONArray jArrayOfficials = jObjMain.getJSONArray("officials");

            int length = jArrayOffices.length();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.clearOfficial();

                }
            });
            //mainActivity.clearOfficial();

            for (int i = 0; i < length; i++) {
                JSONObject jObj = jArrayOffices.getJSONObject(i);
                String officeName = jObj.getString("name");

                JSONArray indicesStr = jObj.getJSONArray("officialIndices");
                ArrayList<Integer> indices = new ArrayList<>();

                for (int j = 0; j < indicesStr.length(); j++) {
                    int pos = Integer.parseInt(indicesStr.getString(j));

                    JSONObject jOfficial = jArrayOfficials.getJSONObject(pos);
                    final Official official = new Official(officeName, jOfficial.getString("name"));

                    //official.setName(jOfficial.getString("name"));

                    JSONArray jAddresses = jOfficial.getJSONArray("address");
                    JSONObject jAddress = jAddresses.getJSONObject(0);

                    OfficalAddress address = new OfficalAddress();

                    if (jAddress.has("line1")) address.setLine1(jAddress.getString("line1"));
                    if (jAddress.has("city")) address.setCity(jAddress.getString("city"));
                    if (jAddress.has("state")) address.setState(jAddress.getString("state"));
                    if (jAddress.has("zip")) address.setZip(jAddress.getString("zip"));

                    official.setAddress(address);

                    if (jOfficial.has("party")) official.setParty(jOfficial.getString("party"));
                    if (jOfficial.has("phones"))
                        official.setPhone(jOfficial.getJSONArray("phones").getString(0));
                    if (jOfficial.has("urls"))
                        official.setUrl(jOfficial.getJSONArray("urls").getString(0));
                    if (jOfficial.has("emails"))
                        official.setEmail(jOfficial.getJSONArray("emails").getString(0));

                    if (jOfficial.has("channels")) {
                        SocialMediaChannel channel = new SocialMediaChannel();

                        JSONArray jChannels = jOfficial.getJSONArray("channels");
                        for (int k = 0; k < jChannels.length(); k++) {
                            JSONObject jChannel = jChannels.getJSONObject(k);
                            if (jChannel.getString("type").equals("Facebook"))
                                channel.setFacebookPageID(jChannel.getString("id"));
                            if (jChannel.getString("type").equals("Twitter"))
                                channel.setTwitterPageID(jChannel.getString("id"));
                            if (jChannel.getString("type").equals("YouTube"))
                                channel.setYoutubePageID(jChannel.getString("id"));
                        }
                        official.setSc(channel);
                    }

                    if (jOfficial.has("photoUrl"))
                        official.setPhotourl(jOfficial.getString("photoUrl"));
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.addOfficial(official);
                        }
                    });
                    //mainActivity.addOfficial(official);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            return;
        }
    }
    /*

    private List<Official> parseJSON(String s) {
        List<Official> st=new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i=0; i<jsonArray.length(); ++i) {

                JSONObject jObjMain = jsonArray.getJSONObject(i);
                JSONObject normalizedInput=jObjMain.getJSONObject("normalizedInput");
                JSONArray offices=jObjMain.getJSONArray("offices");
                JSONArray officials=jObjMain.getJSONArray("officials");
                for(int j=0; j<offices.length(); j++){
                    JSONObject jObj = offices.getJSONObject(j);
                    String title= jObj.getString("name");
                    JSONArray officialIndices= jObj.getJSONArray("officialIndices");
                    for(int k=0; k<officialIndices.length();k++){
                        int indice=officialIndices.getInt(k);
                        JSONObject detail=officials.getJSONObject(indice);
                        String name=detail.getString("name");
                        JSONObject address=detail.getJSONObject("address");
                        OfficalAddress ad=new OfficalAddress(detail.getString("line1"),detail.getString("city"),detail.getString("state"),detail.getString("zip"));
                        String party=detail.getString("party");
                        JSONArray phones=detail.getJSONArray("phones");
                        String phone = null;
                        if(phones!=null) {
                            phone = phones.getString(0);
                        }

                        JSONArray urls=detail.getJSONArray("urls");
                        String url = null;
                        if(urls!=null){
                            url=urls.getString(0);
                        }

                        JSONArray emails=detail.getJSONArray("emails");
                        String email = null;
                        if(emails!=null){
                            email=emails.getString(0);
                        }

                        String photourl=detail.getString("photoUrl");
                        JSONArray channels=detail.getJSONArray("channels");
                        SocialMediaChannel smc=new SocialMediaChannel();
                        if(channels!=null){
                            for(int h=0; h<channels.length();h++){
                                JSONObject obj=channels.getJSONObject(h);

                                if(obj.getString("type")=="Facebook"){
                                    smc.setFacebookPageID(obj.getString("id"));
                                }
                            }
                        }

                        Official of=new Official(title,name);
                        of.setAddress(ad);
                        of.setEmail(email);
                        of.setParty(party);
                        of.setPhone(phone);
                        of.setPhotourl(photourl);
                        of.setUrl(url);
                        of.setSc(smc);
                        st.add(of);

                    }

                }

            }

            return st;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void saveStocks() {

        Log.d(TAG, "saveOfficials: Saving JSON File");
        try {
            FileOutputStream fos = mainActivity.getApplicationContext().
                    openFileOutput("officials.json", Context.MODE_PRIVATE);


            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.setIndent("  ");
            writeMessagesArray(writer, s);
            writer.close();

        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    public void writeMessagesArray(JsonWriter writer, List<Official> sList) throws IOException {
        writer.beginArray();
        for (Official obj : sList) {
            writeMessage(writer, obj);
        }
        writer.endArray();
    }
    public void writeMessage(JsonWriter writer, Official o) throws IOException {
        writer.beginObject();
        Log.d(TAG, "writeMessage: "+ o.getTitle());
        writer.name("title").value(o.getTitle());
        writer.name("name").value(o.getName());
        writer.name("party").value(o.getParty());
        writer.name("email").value(o.getEmail());
        writer.name("phone").value(o.getPhone());
        writer.name("url").value(o.getUrl());
        writer.name("photourl").value(o.getPhotourl());
        writer.name("address").beginObject();
        OfficalAddress ad=o.getAddress();
        writer.name("line1").value(ad.getLine1());
        writer.name("city").value(ad.getCity());
        writer.name("state").value(ad.getState());
        writer.name("zip").value(ad.getZip());
        writer.endObject();
        writer.name("channels").beginArray();
        SocialMediaChannel sc=o.getSc();
        if(sc!=null){

            if(sc.getFacebookPageID()!=null){
                writer.beginObject();
                writer.name("type").value("facebook");
                writer.name("id").value(sc.getFacebookPageID());
                writer.endObject();
            }
            if(sc.getYoutubePageID()!=null){
                writer.beginObject();
                writer.name("type").value("youtube");
                writer.name("id").value(sc.getYoutubePageID());
                writer.endObject();
            }
            if(sc.getTwitterPageID()!=null){
                writer.beginObject();
                writer.name("type").value("twitter");
                writer.name("id").value(sc.getTwitterPageID());
                writer.endObject();
            }

        }
        writer.endArray();



        writer.endObject();
    }

*/
}
