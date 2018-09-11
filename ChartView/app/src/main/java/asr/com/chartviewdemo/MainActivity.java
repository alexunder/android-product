package asr.com.chartviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircularFifoQueue<Integer> data = new CircularFifoQueue<Integer> (60);
        CircularFifoQueue<Integer> data2 = new CircularFifoQueue<Integer> (60);

        for (int i = 0; i < 60; i++) {
            data.add(new SecureRandom().nextInt(600));
        }

        for (int i = 0; i < 60; i++) {
            data2.add(0);
        }

        ChartView view = findViewById(R.id.chartview_demo);
        view.setXScale(0, 60);
        view.setYScale(0, 600);
        view.setDataSource(data, "Sucker", "kg", 0);
        view.setDataSource(data2, "Fucker", "kg", 1);
    }
}
