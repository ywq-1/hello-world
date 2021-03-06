package com.example.agriculture;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.newland.nle_sdk.responseEntity.SensorDataPageDTO;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;
import retrofit2.Response;

public class History_Temperation extends AppCompatActivity {

    private LineChart lineChart;
    private NetWorkBusiness netWorkBusiness;
    private String deviceID = "307947";
    private String Pnum;
    private String Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_temperation);
        Bundle bundle = getIntent().getExtras();
        String accessToken = bundle.getString("accessToken");
        netWorkBusiness = new NetWorkBusiness(accessToken,"http://api.nlecloud.com:80/");

        lineChart = findViewById(R.id.line);
        showChart(lineChart);
        getTempData();
    }

    //设置图表显示样式
    private void showChart(LineChart lineChart) {
        lineChart.setDrawBorders(false);//是否在折线图上添加边框
        lineChart.setNoDataText("暂无数据"); //没有数据时显示
        lineChart.setDragEnabled(true); //设置可以拖拽
        lineChart.getAxisRight().setEnabled(false);//不显示y轴右边的值
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);//不显示x轴
        //设置x轴数据位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(11);
    }
    private void getTempData() {
        netWorkBusiness.getSensorData(deviceID, "temperature", "6", "30", "2021-09-10 00:00:00",
                "2021-10-13 00:00:00", "DESC", "20", "0", new NCallBack<BaseResponseEntity<SensorDataPageDTO>>() {
                    @Override
                    protected void onResponse(BaseResponseEntity<SensorDataPageDTO> response) {

                    }

                    public void onResponse(final Call<BaseResponseEntity<SensorDataPageDTO>> call, final Response<BaseResponseEntity<SensorDataPageDTO>> response) {
                        BaseResponseEntity baseResponseEntity = response.body();
                        if (baseResponseEntity != null) {
                            //有返回的数据
                            final Gson gson = new Gson();
                            try {
                                JSONObject jsonObject;
                                String msg = gson.toJson(baseResponseEntity);
                                jsonObject = new JSONObject(msg);   //解析数据.
                                JSONObject resultobj = jsonObject.getJSONObject("ResultObj");
                                int count = Integer.parseInt(resultobj.get("Count").toString());    //获取记录数20
                                JSONArray jsonArray = resultobj.getJSONArray("DataPoints");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("PointDTO");
                                List<HashMap<String, Object>> data = new ArrayList<>(100);
                               List<Entry> entries = new ArrayList<>();
                               List<String> list = new ArrayList<>();

                                for (int i = 0; i < count; i++) {
                                    JSONObject resultObj1 = jsonArray1.getJSONObject(i);
                                    HashMap<String, Object> item = new HashMap<>();
                                    Pnum = resultObj1.get("Value").toString();
                                    Time = resultObj1.get("RecordTime").toString();
                                    entries.add(new Entry(Float.parseFloat(Pnum),i));
                                    list.add(Time);
                                }
                                LineDataSet lineDataSet = new LineDataSet(entries,"历史温度");
                                LineData Pd = new LineData(list,lineDataSet);
                                lineChart.setData(Pd);
                                lineChart.setScaleEnabled(true);
                                lineChart.invalidate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
}