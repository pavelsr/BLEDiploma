package com.example.android.bluetoothlegatt;

import android.app.Activity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by noxa on 11/06/14.
 */
public class BasicGraphFragment extends Fragment {
    protected GraphViewSeries mADCseries;
    protected GraphView mGraphView;
    protected SimpleDateFormat mTimeFormat = new SimpleDateFormat("mm:ss");

    protected int mMode;

    public static final int NATURAL = 0;
    public static final int DERIV = 1;
    public static final int POW = 2;


    private String[] modesTitles = new String[]{"Натуральный поток","Производная", "Степень"};

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void resetData(SerializableGraphView[] arr) {
        mADCseries.resetData(arr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        GraphViewSeries.GraphViewSeriesStyle style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(200, 100, 0), 5);
        mADCseries = new GraphViewSeries("ADC raw", style, new SerializableGraphView[]{});

        mGraphView = new LineGraphView(getActivity(), modesTitles[mMode]);


        mGraphView.setScrollable(true);
        mGraphView.setScalable(true);

        mGraphView.addSeries(mADCseries);

//        mGraphView.setLayoutParams(new LinearLayout.LayoutParams(width - 50, height - 200));
        mGraphView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return mGraphView;
    }

}
