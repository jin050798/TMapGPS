package com.test.tmap4;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    private static final String API_Key = "l7xx5191db20e9d245d58e8dad551c3ae142";

    // T Map View
    private TMapView tMapView = null;
    private Context mContext;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager gps = null;
    TextView text;
    Animation anim;
    boolean start;

    @Override
    public void onLocationChange(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        TMapPoint point = gps.getLocation();
        if (m_bTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.blink);
        // T Map View
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey(API_Key);
        mContext = this;

        //글자 깜빡거리기 위해
        anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(100);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);



        //초기 세팅
        tMapView.setZoomLevel(17);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        // T Map View Using Linear Layout
        linearLayoutTmap.addView(tMapView);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //GPS
        gps = new TMapGpsManager(MainActivity.this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();

        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);



        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.point);
        tMapView.setIcon(bitmap);
        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_dot);



        double distance;//거리 구하기
        double distance2;



        TMapPoint gpspoint = gps.getLocation();
        double gpsla = gpspoint.getLatitude();
        double gpslo = gpspoint.getLongitude();

        Location GPSLocation = new Location("pointC");
        GPSLocation.setLatitude(gpsla);
        GPSLocation.setLongitude(gpslo);

        Location CurrentLocation = new Location("pointA");
        CurrentLocation.setLatitude(36.36740562078646);
        CurrentLocation.setLongitude(127.34447222396332);

        double cula = CurrentLocation.getLatitude();
        double culo = CurrentLocation.getLongitude();

        TMapPoint CurrentPoint = new TMapPoint(cula, culo);


        Location PetLocation = new Location("pointB");
        PetLocation.setLatitude(36.36624725889481);
        PetLocation.setLongitude(127.34452717420048);


        double petla = PetLocation.getLatitude();
        double petlo = PetLocation.getLongitude();

        distance = CurrentLocation.distanceTo(PetLocation);

        distance2 = GPSLocation.distanceTo(PetLocation);
        if(distance<100){
            text.setText("반려동물이 100m 이내에 있습니다.");
            TMapMarkerItem markerItem1 = new TMapMarkerItem();

            text.setTextColor(Color.GREEN);
            text.startAnimation(anim);
            TMapPoint tMapPoint1 = new TMapPoint(petla, petlo); // SKT타워
            markerItem1.setIcon(bitmap1); // 마커 아이콘 지정
            markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
            markerItem1.setTMapPoint(tMapPoint1); // 마커의 좌표 지정
            markerItem1.setName("충남대학교"); // 마커의 타이틀 지정
            tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
        }else if(distance>=100&&distance<500){
            text.setText("반려동물이 500m 이내에 있습니다.");
            TMapPoint tMapPointStart = new TMapPoint(cula, culo);
            TMapPoint tMapPointEnd = new TMapPoint(petla, petlo);

            text.setTextColor(Color.YELLOW);
            text.startAnimation(anim);
            TMapData tmapdata = new TMapData();
            tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                @Override
                public void onFindPathData(TMapPolyLine polyLine) {
                    tMapView.addTMapPath(polyLine);
                    polyLine.setLineColor(Color.YELLOW);
                    polyLine.setLineWidth(8);
                }
            });


        }else{


            text.setText("반려동물이 500m 이상으로 떨어졌습니다.");
            text.setTextColor(Color.RED);
            text.startAnimation(anim);
            TMapPoint tMapPointStart = new TMapPoint(cula, culo);
            TMapPoint tMapPointEnd = new TMapPoint(petla, petlo);


            TMapData tmapdata = new TMapData();
            tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                @Override
                public void onFindPathData(TMapPolyLine polyLine) {
                    tMapView.addTMapPath(polyLine);
                }
            });

        }

        //TMapPoint CurrentLocation = new TMapPoint(36.36740562078646, 127.34447222396332);
        //TMapPoint petLocation1 = new TMapPoint(36.36624725889481, 127.34452717420048);//100m 이하
        //TMapPoint petLocation2 = new TMapPoint(36.36601746713756, 127.34585476891337);//100이상 500이하
        //TMapPoint petLocation3 = new TMapPoint(36.36601746713756, 127.34585476891337);//500 이상




        tMapView.setCenterPoint(127.34447222396332, 36.36740562078646);





        //ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
        //alTMapPoint.add( new TMapPoint(37.570841, 126.985302) ); // SKT타워
        //alTMapPoint.add( new TMapPoint(37.551135, 126.988205) ); // N서울타워
        //alTMapPoint.add( new TMapPoint(37.579600, 126.976998) ); // 경복궁

        //TMapPolyLine tMapPolyLine = new TMapPolyLine();
        //tMapPolyLine.setLineColor(Color.BLUE);
        //tMapPolyLine.setLineWidth(2);


        //for( int i=0; i<alTMapPoint.size(); i++ ) {
        //    tMapPolyLine.addLinePoint( alTMapPoint.get(i) );
        //}
        //tMapView.addTMapPolyLine("Line1", tMapPolyLine);

    }
    public void onClickZoomInBtn(View v) {
        mapZoomIn();
    }

    public void onClickZoomOutBtn(View v) {
        mapZoomOut();
    }

    public void mapZoomIn() {
        tMapView.MapZoomIn();
    }

    /**
     * mapZoomOut 지도를 한단계 축소한다.
     */
    public void mapZoomOut() {
        tMapView.MapZoomOut();
    }
}

