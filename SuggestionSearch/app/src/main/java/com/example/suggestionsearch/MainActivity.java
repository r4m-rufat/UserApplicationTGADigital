package com.example.suggestionsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private LineChart lineChart;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.lineChart);
        context = this;


        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setBackground(ContextCompat.getDrawable(context, R.color.white));
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 60));
        yValues.add(new Entry(1, 40));
        yValues.add(new Entry(2, 50));
        yValues.add(new Entry(3, 10));
        yValues.add(new Entry(4, 20));
        yValues.add(new Entry(5, 45));
        yValues.add(new Entry(6, 10));

        LineDataSet lineDataSet1 = new LineDataSet(yValues, "");


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.getData().setHighlightEnabled(false);


    }
}