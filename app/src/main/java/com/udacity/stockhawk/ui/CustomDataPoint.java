package com.udacity.stockhawk.ui;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Date;


public class CustomDataPoint extends DataPoint {
    public CustomDataPoint(double x, double y) {
        super(x, y);
    }

    public CustomDataPoint(Date x, double y) {
        super(x, y);
    }

    public CustomDataPoint(long timestamp, float y) {
        super(timestamp, y);
    }
}
