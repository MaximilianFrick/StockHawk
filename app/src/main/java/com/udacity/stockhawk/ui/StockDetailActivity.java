package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_SYMBOL = "KEY_SYMBOL";
    @BindView(R.id.graph)
    GraphView graph;

    @BindView (R.id.title_stock)
    TextView titleStock;

    private String symbol;

    public static Intent newIntent(Activity activity, String symbol) {
        Intent intent = new Intent(activity, StockDetailActivity.class);
        intent.putExtra(KEY_SYMBOL, symbol);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);
        symbol = getIntent().getStringExtra(KEY_SYMBOL);
        titleStock.setText(symbol);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void initGraph(Cursor data) {
        LongSparseArray<Float> historyDataSet = createFormattedHistoryDataSet(data);
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < historyDataSet.size(); i++) {
            dataPoints.add(new CustomDataPoint(historyDataSet.keyAt(i), historyDataSet.valueAt(i)));
        }
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setHumanRounding(false);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
        graph.addSeries(series);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.uri, Contract.Quote.QUOTE_COLUMNS, null, null,
                Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        while (data.moveToNext()) {
            if (TextUtils.equals(data.getString(Contract.Quote.POSITION_SYMBOL), symbol)) {
                break;
            }
        }
        initGraph(data);
        data.close();
    }

    private LongSparseArray<Float> createFormattedHistoryDataSet(Cursor data) {
        LongSparseArray<Float> dataSet = new LongSparseArray<>();
        String historyUnformatted = data.getString(Contract.Quote.POSITION_HISTORY);
        String[] splittedData = historyUnformatted.split("\\n");
        for (String entry : splittedData) {
            String[] splittedEntry = entry.split(",");
            dataSet.append(Long.valueOf(splittedEntry[0]), Float.valueOf(splittedEntry[1].trim()));
        }
        return dataSet;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
