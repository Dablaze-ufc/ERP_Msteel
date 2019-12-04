package example.com.erp.activity;
/*
Create by user on 14-02-2019 at 05:37 PM for ERP
*/

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import example.com.erp.R;
import example.com.erp.model.Datum;
import example.com.erp.model.DayGraphModel;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphActivity extends AppCompatActivity {
    ArrayList<Entry> yVals;
    String[] xValue;
    private Toolbar toolbar;
    private TabLayout tabs;
    private LineChart chart;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            if (getIntent().getStringExtra("id") != null)
                id = getIntent().getStringExtra("id");
        }
        setContentView(R.layout.frag_graph);
        assignViews();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    getGraphByType("day");
                } else if (tab.getPosition() == 1) {
                    getGraphByType("week");
                } else if (tab.getPosition() == 2) {
                    getGraphByType("month");
                } else if (tab.getPosition() == 3) {
                    getGraphByType("year");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void assignViews() {
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        chart = findViewById(R.id.line_chart);

        tabs.addTab(tabs.newTab().setText("Day"));
        tabs.addTab(tabs.newTab().setText("Week"));
        tabs.addTab(tabs.newTab().setText("Month"));
        tabs.addTab(tabs.newTab().setText("Year"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Graph View");
        }

        final float textSize = 8f;
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        XAxis xAxis;
        xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextSize(textSize);
        getGraphByType("day");
    }

    private void getGraphByType(String type) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DayGraphModel> call = apiInterface.getGraph(id, type);
        call.enqueue(new Callback<DayGraphModel>() {
            @Override
            public void onResponse(@NonNull Call<DayGraphModel> call, @NonNull Response<DayGraphModel> response) {
                if (response.isSuccessful()) {
                    DayGraphModel message = response.body();
                    if (message != null && (message.getSuccess() == 6 || message.getSuccess() == 5 || message.getSuccess() == 4 || message.getSuccess() == 3)) {
                        List<Datum> arrayList = message.getData();
                        xValue = new String[arrayList.size()];
                        yVals = new ArrayList<>();
                        for (int i = 0; i < arrayList.size(); i++) {
                            xValue[i] = arrayList.get(i).getCreatedAt();
                            yVals.add(new Entry(i, Float.parseFloat(arrayList.get(i).getRate())));
                        }
                        displayChart();
                    }

                } else {
                    Toast.makeText(GraphActivity.this, "Error :" + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DayGraphModel> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(GraphActivity.this, "Check network connection..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GraphActivity.this, "data cannot parse..", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                }
            }
        });
    }

    private void displayChart() {
        LineDataSet dataSet = new LineDataSet(yVals, "Graph Result");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    //2019-04-25 17:18:15
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                    Date newDate = format.parse(xValue[(int) value]);
                    return new SimpleDateFormat("dd-MMM", Locale.getDefault()).format(newDate);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return "";
            }
        };
        xAxis.setGranularity(1.5f);
        xAxis.setValueFormatter(formatter);
        //xAxis.setValueFormatter( new DayAxisValueFormatter( chart ) );

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.animateX(1500);
        chart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            performBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void performBack() {
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        performBack();
        super.onBackPressed();
    }

    public class DayAxisValueFormatter extends IndexAxisValueFormatter {
        protected String[] mMonths = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
        };

        private BarLineChartBase<?> chart;

        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int days = (int) value;

            int year = determineYear(days);

            int month = determineMonth(days);
            String monthName = mMonths[month % mMonths.length];
            String yearName = String.valueOf(year);

            if (chart.getVisibleXRange() > 30 * axis.getLabelCount()) {

                return monthName + " " + yearName;
            } else {

                int dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016));

                String appendix = "th";

                switch (dayOfMonth) {
                    case 1:
                        appendix = "st";
                        break;
                    case 2:
                        appendix = "nd";
                        break;
                    case 3:
                        appendix = "rd";
                        break;
                    case 21:
                        appendix = "st";
                        break;
                    case 22:
                        appendix = "nd";
                        break;
                    case 23:
                        appendix = "rd";
                        break;
                    case 31:
                        appendix = "st";
                        break;
                }

                return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
            }
         /*try {
            //2019-04-25 17:18:15
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss", Locale.getDefault() );
            Date newDate = format.parse( xValue[(int) value] );
            return new SimpleDateFormat( "MMM", Locale.getDefault() ).format( newDate );
         } catch (Exception ex) {
            ex.printStackTrace();
         }return "";*/

        }

        private int getDaysForMonth(int month, int year) {

            if (month == 1) {

                if (year == 2016 || year == 2020)
                    return 29;
                else
                    return 28;
            }

            if (month == 3 || month == 5 || month == 8 || month == 10)
                return 30;
            else
                return 31;
        }

        private int determineMonth(int dayOfYear) {

            int month = -1;
            int days = 0;

            while (days < dayOfYear) {
                month = month + 1;

                if (month >= 12)
                    month = 0;

                int year = determineYear(days);
                days += getDaysForMonth(month, year);
            }

            return Math.max(month, 0);
        }

        private int determineDayOfMonth(int dayOfYear, int month) {

            int count = 0;
            int days = 0;

            while (count < month) {

                int year = determineYear(days);
                days += getDaysForMonth(count % 12, year);
                count++;
            }

            return dayOfYear - days;
        }

        private int determineYear(int days) {

            if (days <= 366)
                return 2016;
            else if (days <= 730)
                return 2017;
            else if (days <= 1094)
                return 2018;
            else if (days <= 1458)
                return 2019;
            else
                return 2020;

        }
    }
}
