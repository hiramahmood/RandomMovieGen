package com.example.randommoviegen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class SecondFragment extends Fragment {
    String title;
    String rating;
    String summaryy_url;
    Elements image;
    Bitmap bitmap;
    Bitmap[] images = new Bitmap[251];
    String[] titles = new String[251];
    String[] ratings = new String[251];
    int num_on_list = 0;
    String summary;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View fragmentSecondLayout = inflater.inflate(R.layout.fragment_second, container, false);
        return fragmentSecondLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = "";
        String rating;
        downloadThread.start();
        try {
            downloadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //generating a random number to use
        Random rand = new Random();
        num_on_list = rand.nextInt(251);

        summaryThread.start();
        try {
            summaryThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //updating the textviews
        TextView movieTitle = view.getRootView().findViewById(R.id.MovieTitle);
        TextView movieRating = view.getRootView().findViewById(R.id.MovieRating);
        TextView movieSummary = view.getRootView().findViewById(R.id.summary);
        //ImageView poster = view.getRootView().findViewById(R.id.imageView);
        movieTitle.setText(titles[num_on_list]);
        movieRating.setText("Rating: " + ratings[num_on_list]);
        movieSummary.setText(summary);
        //poster.setImageBitmap(images[num_on_list]);

        view.findViewById(R.id.redo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    //thread that scrapes the web for IMDb top 250 movies
    Thread downloadThread = new Thread() {
        public void run() {
            try {
                Document document = Jsoup.connect("https://www.imdb.com/chart/top/").get();
                Document doc2;
                int i = 0;
                for (Element row : document.select("table.chart.full-width tr")) {
                    title = row.select(".titleColumn a").text();
                    rating = row.select(".imdbRating").text();

                    image = row.select(".posterColumn").select("img");
                    String imgSrc = image.attr("src");

                    titles[i] = title;
                    ratings[i] = rating;
                    //images[i] = bitmap;
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Thread summaryThread = new Thread() {
        public void run() {
            try {
                Document document = Jsoup.connect("https://www.imdb.com/chart/top/").get();
                Document doc2;
                int i = 0;
                for (Element row : document.select("table.chart.full-width tr")) {
                    if(i == num_on_list) {
                        summaryy_url = row.select(".titleColumn a").attr("href");
                        String fullUrl = "https://www.imdb.com" + summaryy_url;
                        doc2 = Jsoup.connect(fullUrl).get();
                        summary = doc2.select(".summary_text").text();
                    }
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}