package com.example.nik7.celebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    String html;
    List<String> urlList;
    List<String> nameList;
    ImageView image;
    Button b1,b2,b3,b4;
    String correct;
    int len;

    public class DownloadText extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                int c = reader.read();
                String result = "";

                while (c != -1) {
                    result += (char) c;
                    c = reader.read();
                }
                return result;
            }
            catch(Exception e){
                e.printStackTrace();
                return "";
            }
        }
    }



    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            DownloadText task = new DownloadText();
            html = task.execute("http://www.posh24.se/kandisar").get();
            String[] afterSplit = html.split("<div class=\"listedArticles\">");
            nameList = new ArrayList<String>();
            urlList = new ArrayList<String>();
            image = (ImageView) findViewById(R.id.image);
            b1 = (Button) findViewById(R.id.b1);
            b2 = (Button) findViewById(R.id.b2);
            b3 = (Button) findViewById(R.id.b3);
            b4 = (Button) findViewById(R.id.b4);


            //For urls of the images

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(afterSplit[0]);

            while (m.find()) {
                urlList.add(m.group(1));
            }


            //For names of the images

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(afterSplit[0]);

            while (m.find()) {
                nameList.add(m.group(1));
            }

            len = nameList.size();
            Display();
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }


    public void Display(){
        Random r = new Random();
        int ind = r.nextInt(len);
        correct = nameList.get(ind);

        try {

            //Setting the image
            DownloadImage task = new DownloadImage();
            String url = urlList.get(ind);
            Bitmap b = task.execute(url).get();
            image.setImageBitmap(b);


            //Setting the options
            List<Integer> chosenIndices = new ArrayList<Integer>();
            chosenIndices.add(ind);
            int n = 1;

            while (n < 4) {
                ind = r.nextInt(len);
                if (chosenIndices.contains(ind)) {
                    continue;
                } else {
                    chosenIndices.add(ind);
                    n++;
                }
            }

            Collections.shuffle(chosenIndices);
            b1.setText(nameList.get(chosenIndices.get(0)));
            b2.setText(nameList.get(chosenIndices.get(1)));
            b3.setText(nameList.get(chosenIndices.get(2)));
            b4.setText(nameList.get(chosenIndices.get(3)));
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void checkAnswer(View view){
        Button b = (Button) view;
        String chosen = b.getText().toString();

        if(chosen.equals(correct)){
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        }

        else{
            Toast.makeText(this, "Wrong! It was " + correct , Toast.LENGTH_SHORT).show();
        }

        Display();
    }

}
