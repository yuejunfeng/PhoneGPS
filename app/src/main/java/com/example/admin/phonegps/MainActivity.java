package com.example.admin.phonegps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;

public class MainActivity extends AppCompatActivity {

    private MapView bmapView;
    private BaiduMap mBaiduMap;
    /** 清华大学*/
    protected LatLng qhPos = new LatLng(40.009424,116.332556);
    /** 北京坐标 */
    protected LatLng bjPos = new LatLng(39.997743,116.316176);
    /** 天安门坐标 */
    protected LatLng tamPos = new LatLng(39.915112,116.403963);
    //天安门经纬度
    private double mLat1= 39.915119;
    private double mLon1= 116.403963;
    //八维经纬度
    private double mLat2 = 40.047862;
    private double mLon2 = 116.306586;
    private LocationClient mLocClient;
    MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bmapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = bmapView.getMap();
        //设置地图中心点为清华大学
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(qhPos);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        //设置地图缩放为15
        mapStatusUpdate= MapStatusUpdateFactory.zoomTo(15);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onDestroy() {
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        bmapView.onDestroy();
        bmapView = null;
        bmapView.onDestroy();
        super.onDestroy();
    }
    //旋转
    public void xuanzhuan(View v){
        MapStatus mapStatus = mBaiduMap.getMapStatus();
        float rotate = mapStatus.rotate;
        //通过new 得到MapStatus.Builder对象
        MapStatus.Builder builder = new MapStatus.Builder();
        //通过MapStatus.Builder对象.rotate
        builder.rotate(rotate+30);
        //用MapStatus.Builder对象.build,得到MapStatus对象
        MapStatus build = builder.build();
        //用使用MapStatusUpdateFactory工厂调用newMapStatus,把MapStatus对象传入.得到MapStatusUpdate
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(build);
        //map对象.setMapStatus,传入MapStatusUpdate对象
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }
        //俯视
    public void fushi(View v){
        //获取当前地图的状态,map对象.getMapStatus,直接获取到MapStatus
        MapStatus mapStatus = mBaiduMap.getMapStatus();
        //然后MapStatus.overlook就可以拿到原来的俯仰度数.
        float overlook = mapStatus.overlook;
        //通过new MapStatus.Builder得到MapStatus.Builder对象
        MapStatus.Builder builder = new MapStatus.Builder();
        //用MapStatus.Builder对象.overlook(原来俯仰的度数-5);
        builder.overlook(overlook - 5);
        //用MapStatus.Builder对象.build,得到MapStatus对象
        MapStatus build = builder.build();
        //用使用MapStatusUpdateFactory工厂调用newMapStatus,把MapStatus对象传入.得到MapStatusUpdate
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(build);
        ////map对象.setMapStatus,传入MapStatusUpdate对象
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }
    //移动
    public void yidong(View v){
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(bjPos);

        mBaiduMap.animateMapStatus(mapStatusUpdate,20000);

    }
    //卫星地图
    public void weixing(View v){
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setTrafficEnabled(false);
    }
    //普通图层
    public void putong(View v){
       mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
       mBaiduMap.setTrafficEnabled(false);
    }
    //交通图
    public void jiaotong(View v){
        mBaiduMap.setTrafficEnabled(true);
    }
    //导航
    public void daohang(View v){
        LatLng pt1 = new LatLng(mLat2,mLon2);
        LatLng pt2 = new LatLng(mLat1,mLon1);

        NaviParaOption para = new NaviParaOption()
                .startPoint(pt1).endPoint(pt2)
                .startName("八维学院").endName("天安门");
        //百度地图导航
       BaiduMapNavigation.openBaiduMapNavi(para,this);
        //骑行导航
       // BaiduMapNavigation.openBaiduMapBikeNavi(para,this);
        //步行导航
      // BaiduMapNavigation.openBaiduMapWalkNavi(para,this);
        //AR导航
     //   BaiduMapNavigation.openBaiduMapWalkNaviAR(para,this);
    }
    //定位
    public void dingwei(View v){
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 创建定位所用到的类
        mLocClient = new LocationClient(this);
        //注册定位的监听器
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型,使用的地图加密算法
        option.setScanSpan(1000);//可选设置定位间隔时间,注意间隔需要大于等于1000ms
        mLocClient.setLocOption(option);//地图界面显示到定位位置
        mLocClient.start();//开启定位
    }
    //创建内部类监听
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || bmapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            boolean isFirstLoc = true;
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

}
