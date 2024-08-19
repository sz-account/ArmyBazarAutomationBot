package org.example;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SessionManager {

    private String cookies;
    private HttpClient httpclient;

    public SessionManager() {
        httpclient = new DefaultHttpClient();
    }

    public void logIn(String username, String password) throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.armybazar.eu/pl/zagloszenie/");

        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("login_meno", new StringBody(username));
        reqEntity.addPart("login_heslo", new StringBody(password));
        reqEntity.addPart("login_url", new StringBody("http://www.armybazar.eu/pl/"));
        reqEntity.addPart("login_login", new StringBody("Zalogować"));
        httppost.setEntity(reqEntity);

        HttpResponse response = httpclient.execute(httppost);

        Header[] headers = response.getAllHeaders();
        cookies = retrieveCookie(headers);
        EntityUtils.consume(response.getEntity());
    }

    public void uploadListings(List<Listing> listings) throws IOException {

        for (Listing listing : listings) {

            HttpPost httppost = new HttpPost("http://www.armybazar.eu/pl/dodac-ogloszenie/");
            uploadDescription(listing, httppost);
            uploadPhotos(listing, httppost);
        }
    }

    private void uploadDescription(Listing listing, HttpPost httppost) throws IOException {

        httppost.addHeader("Accept-Charset", "utf-8");
        httppost.addHeader("Cookie", cookies);

        MultipartEntity reqEntity = new MultipartEntity(null, null, StandardCharsets.UTF_8);
        reqEntity.addPart("nazov", new StringBody(listing.title, StandardCharsets.UTF_8));
        reqEntity.addPart("kategoria", new StringBody(listing.category));
        reqEntity.addPart("typ", new StringBody(listing.type));
        reqEntity.addPart("cena", new StringBody(listing.price));
        reqEntity.addPart("platnost", new StringBody("2"));
        reqEntity.addPart("popis", new StringBody(listing.description, StandardCharsets.UTF_8));
        reqEntity.addPart("submit", new StringBody("Kontynuować"));
        reqEntity.addPart("submit", new StringBody("1"));
        httppost.setEntity(reqEntity);

        HttpResponse response = httpclient.execute(httppost);
        EntityUtils.consume(response.getEntity());
    }

    private void uploadPhotos(Listing listing, HttpPost httppost) throws IOException {

        MultipartEntity reqEntity = new MultipartEntity();

        File[] photos = listing.photos;
        for (int i = photos.length - 1; i >= 0; i--) {
            File photo = photos[i];
            reqEntity.addPart("fotka[]", new FileBody(photo));
        }

        reqEntity.addPart("submit", new StringBody("true"));
        reqEntity.addPart("add", new StringBody("Zakończyć"));
        httppost.setEntity(reqEntity);

        System.out.println("Starting upload of " + listing.title);
        HttpResponse response = httpclient.execute(httppost);
        EntityUtils.consume(response.getEntity());
        System.out.println("Upload finished");
    }

    private String retrieveCookie(Header[] headers) {

        String cookie = formatString(headers[3].getValue()) + "warning-popup=hide; ";
        cookie += formatString(headers[12].getValue());
        cookie += formatString(headers[7].getValue());
        cookie += formatString(headers[8].getValue());
        cookie += formatString(headers[10].getValue());
        cookie += formatString(headers[11].getValue());
        return cookie;
    }

    private String formatString(String tmp)
    {
        int startIndex = tmp.indexOf("path");
        return tmp.substring(0, startIndex);
    }
}
