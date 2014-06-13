package com.example.android.bluetoothlegatt;

import com.jjoe64.graphview.GraphView;

import java.io.Serializable;

/**
 * Created by noxa on 11/06/14.
 */
public class SerializableGraphView extends GraphView.GraphViewData implements Serializable {
    public SerializableGraphView(double valueX, double valueY) {
        super(valueX, valueY);
    }
}
