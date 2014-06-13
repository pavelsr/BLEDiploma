package com.example.android.bluetoothlegatt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;

/**
 * Created by noxa on 12/06/14.
 */
public class DerivGraphFragment extends BasicGraphFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GraphView graphView = (GraphView)super.onCreateView(inflater, container, savedInstanceState);
        graphView.setManualYAxis(true);
        graphView.setManualYAxisBounds(30, -30);
        return graphView;
    }

    @Override
    public void resetData(SerializableGraphView[] arr) {
        super.resetData(arr);
        mGraphView.setViewPort(arr[arr.length-1].getX()-50,55);
    }
}
