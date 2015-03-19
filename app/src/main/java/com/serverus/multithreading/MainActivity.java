package com.serverus.multithreading;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private EditText editText;
    private ListView listView;
    private String[] listOfImages;
    private ProgressBar progressBar;
    private LinearLayout loadingSection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.downLoadUrl);
        listView = (ListView) findViewById(R.id.urlList);
        listView.setOnItemClickListener(this);
        listOfImages = getResources().getStringArray(R.array.imageUrls);
        progressBar = (ProgressBar) findViewById(R.id.downloadProgress);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void downLoadImage(View view){
//        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        String url = listOfImages[0];
//
//        Uri uri = Uri.parse(url);

        //L.s(this, file.getAbsolutePath());
        Thread myThread = new Thread(new DownloadImagesThread());
        myThread.start();
    }

    public boolean downloadImageUsingThreads(String url){

        boolean successful = false;
        URL downloadUrl = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;

        try{
            // makes the url recognize as URL in java
            downloadUrl = new URL(url);

            //L.m(downloadUrl.toString());

            connection = (HttpURLConnection) downloadUrl.openConnection();
            // getInputStream is like a pipe to url
            inputStream = connection.getInputStream();// read data

            //access the sd card phone storage file path
            file = new File( Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES).getAbsolutePath()

                    // get the last segment of the URL
                    +"/"+Uri.parse(url).getLastPathSegment());

            //L.m( file.getAbsolutePath());

            // fileOutputStream means writing to a file or directory
            // nag pasa tayo ng "file" which is the "pictures"
            // kung saan ilalagay ung dinawnload natin
            fileOutputStream = new FileOutputStream(file);

            int read = -1;
            byte[] buffer = new byte[1024];

            //eto ung mag hhandle ng bites received from the file na dinadownload mo
            // so every bite na ma rereceive is galing sa inputStream.read()
            // -1 kapag wala na narereceive
            while ((read =inputStream.read()) != -1){
                L.m("bytes recieved "+read);
                fileOutputStream.write(read);
            }

            successful = true;

        }catch (MalformedURLException e){
            L.m( "MALFORMED +++ "+e);

        } catch (IOException e) {
            L.m("IO EXCEPTION +++ "+e);
        }finally {

            if(connection != null){
                // to conserve resources
                //maintaining ports open takes resources
                // thats why we disconnect the connection after using it
                connection.disconnect();
            }

            if(inputStream !=null){
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {

                }
            }
        }

        return successful;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editText.setText(listOfImages[position]);
    }

    //NOTE: without this the app will crash because we cant perform network operation on the main thread
    // we need a background thread for this kind of process

    private class DownloadImagesThread implements Runnable{
        //NOTE: implementing Runnable  interface doesnt make your program multi threaded
        @Override
        public void run() {
             downloadImageUsingThreads(listOfImages[0]);
        }
    }
}
