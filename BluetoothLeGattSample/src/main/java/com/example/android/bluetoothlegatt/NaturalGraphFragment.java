package com.example.android.bluetoothlegatt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LineGraphView;

import java.util.Date;

/**
 * Created by noxa on 12/06/14.
 */
public class NaturalGraphFragment extends BasicGraphFragment {

    @Override
    public void resetData(SerializableGraphView[] arr) {
        super.resetData(arr);
        long timePeriod = 9 * 1000;
        long currentTime = System.currentTimeMillis();
        long pastTime = currentTime - (long) (timePeriod * 0.9);
        mGraphView.setViewPort((double) pastTime, (double) timePeriod);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GraphView graphView = (GraphView) super.onCreateView(inflater, container, savedInstanceState);
        graphView.getGraphViewStyle().setNumHorizontalLabels(10);
        graphView.setManualYAxis(true);
        graphView.setManualYAxisBounds((double) 1024, (double) 0);
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double v, boolean b) {
                if (b) {
                    return mTimeFormat.format(new Date((long)v));
                } else return ((long)v)+"";
            }
        });
        return graphView;
    }
}
