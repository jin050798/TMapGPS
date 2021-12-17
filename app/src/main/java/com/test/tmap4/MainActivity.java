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
import com.skt.Tmap.TMapPOIItem;
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

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private static final String API_Key = "l7xx5191db20e9d245d58e8dad551c3ae142";

    // T Map View
    double distance = 0;
    private TMapView tMapView = null;
    private Context mContext;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager gps = null;
    TextView text;
    Animation anim;
    TMapPoint tMapPointStart;

    double longitude=0;//경도
    double latitude=0; //위도

    double petlongi =127.34452717420048;
    double petlati = 36.36624725889481;
    @Override
    public void onLocationChange(Location location) {

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        if (m_bTrackingMode) {


            distance = getDistance(latitude,longitude,petlati,petlongi);

            tMapPointStart = new TMapPoint(latitude, longitude);
            tMapView.setLocationPoint(longitude, latitude);
        }
        Log.e("오류","실패");

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.point);
        tMapView.setIcon(bitmap);
        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_dot);



        if(distance<100){
            text.setText("반려동물이 100m 이내에 있습니다.");
            text.setTextColor(Color.GREEN);
            text.startAnimation(anim);
            TMapPoint tMapPointEnd = new TMapPoint(petlati, petlongi);

            TMapData tmapdata = new TMapData();
            tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                @Override
                public void onFindPathData(TMapPolyLine polyLine) {
                    tMapView.addTMapPath(polyLine);
                    polyLine.setLineColor(Color.GREEN);
                    polyLine.setLineWidth(8);
                }
            });

        }else if(distance>=100&&distance<500){
            text.setText("반려동물이 500m 이내에 있습니다.");

            TMapPoint tMapPointEnd = new TMapPoint(petlati, petlongi);

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
            TMapPoint tMapPointEnd = new TMapPoint(petlati, petlongi);

            TMapData tmapdata = new TMapData();
            tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH,tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                @Override
                public void onFindPathData(TMapPolyLine polyLine) {
                    tMapView.addTMapPath(polyLine);
                }
            });

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

        gps = new TMapGpsManager(MainActivity.this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(TMapGpsManager.GPS_PROVIDER);
        gps.OpenGps();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);
        tMapView.setCenterPoint(127.34447222396332, 36.36740562078646);


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

    public double getDistance(double latitude, double longitude, double platitude, double plongitude){

        Location PetLocation = new Location("pointA");
        PetLocation.setLatitude(platitude);
        PetLocation.setLongitude(plongitude);
        Location GPSLocation = new Location("pointB");
        GPSLocation.setLatitude(latitude);
        GPSLocation.setLongitude(longitude);

        double distance = GPSLocation.distanceTo(PetLocation);

        return distance;
    }


}

