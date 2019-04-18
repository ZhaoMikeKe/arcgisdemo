package com.huachu.arcgis.arcgisdemo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.DrawableMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.util.ListenableList;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private GraphicsOverlay mGraphicsOverlay;
    private RxPermissions rxPermissions;
    private LocationDisplay mLocationDisplay;
    private Callout mCallout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        rxPermissions = new RxPermissions(this);
        TextView tvCompany = findViewById(R.id.tv_company);
        initPermissions();//权限申请
        tvCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                share_intent.setType("text/plain");//设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_SUBJECT, "标题");//添加分享内容标题
                share_intent.putExtra(Intent.EXTRA_TEXT, "内容");//添加分享内容
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "分享");
                MainActivity.this.startActivity(share_intent);*/


                /** * 分享图片 */
               /* Bitmap bgimg0 = ImageUtils.getBitmap(R.mipmap.ic_launcher);
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                share_intent.setType("image/*");  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, saveBitmap(bgimg0, "img"));
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "分享");
                MainActivity.this.startActivity(share_intent);*/
            }
        });

        setupMap();//地图设置
        createPointGraphics();//点
        createPolylineGraphics();//线
        createPolygonGraphics();//面
        //setupLocationDisplay();//定位
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupMap() {
        if (mMapView != null) {
            String url = getString(R.string.url);
            ArcGISTiledLayer mapImageLayer = new ArcGISTiledLayer(url);
            ArcGISMap map = new ArcGISMap(SpatialReference.create(3857));
            map.getOperationalLayers().add(mapImageLayer);
            Point centerPoint = new Point(114.71511, 38.09042);
            Point point = (Point) GeometryEngine.project(centerPoint, SpatialReference.create(4326));
            mGraphicsOverlay = new GraphicsOverlay();
            mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
            mMapView.setMap(map);
            mMapView.setViewpoint(new Viewpoint(point, 100000));
            mCallout = mMapView.getCallout();
            // 设定单击事件
            mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {

                @Override
                public boolean onSingleTapConfirmed(MotionEvent motionEvent) {


                    // get the point that was clicked and convert it to a point in map coordinates
                  /*  android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
                            Math.round(motionEvent.getY()));
                    // create a map point from screen point
                    Point mapPoint = mMapView.screenToLocation(screenPoint);
                    // convert to WGS84 for lat/lon format
                    Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
                    // create a textview for the callout
                    TextView calloutContent = new TextView(getApplicationContext());
                    calloutContent.setTextColor(Color.BLACK);
                    calloutContent.setSingleLine();
                    // format coordinates to 4 decimal places
                    calloutContent.setText("Lat: " + String.format("%.4f", wgs84Point.getY()) + ", Lon: " + String.format("%.4f", wgs84Point.getX()));

                    // get callout, set content and show
                    mCallout = mMapView.getCallout();
                    mCallout.setLocation(mapPoint);
                    mCallout.setContent(calloutContent);
                    mCallout.show();

                    // center on tapped point
                    mMapView.setViewpointCenterAsync(mapPoint);*/


                  /*  ListenableList<Graphic> graphicListenableList = mGraphicsOverlay.getGraphics();
                    double x = Math.round(motionEvent.getX());//点击坐标(像素)
                    double y = Math.round(motionEvent.getY());//点击坐标
                    for (int i = 0; i < graphicListenableList.size(); i++) {
                        Graphic gpVar = graphicListenableList.get(i);
                        if (gpVar != null) {
                            if (gpVar.getGeometry().toString().contains("Point")) {
                                Point pointVar = (Point) gpVar.getGeometry();
                                android.graphics.Point pointVar1 = mMapView.locationToScreen(pointVar);
                                double x1 = pointVar1.x;//marker坐标
                                double y1 = pointVar1.y;//marker坐标
                                double dd = Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
                                if (dd < 50) {
                                    gpVar.setSelected(true);
                                    break;
                                } else {

                                }

                            }
                        }
                    }*/
                    identifyGraphic(motionEvent);//判断点击的graphic
                    return true;
                }
            });


        }
    }

    private void identifyGraphic(MotionEvent motionEvent) {//判断点击的graphic
        // get the screen point
        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
                Math.round(motionEvent.getY()));
        // from the graphics overlay, get graphics near the tapped location
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyResultsFuture = mMapView
                .identifyGraphicsOverlayAsync(mGraphicsOverlay, screenPoint, 10, false);
        identifyResultsFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = identifyResultsFuture.get();
                    List<Graphic> graphics = identifyGraphicsOverlayResult.getGraphics();
                    // if a graphic has been identified
                    if (graphics.size() > 0) {
                        //get the first graphic identified
                        Graphic identifiedGraphic = graphics.get(0);
                        showCallout(identifiedGraphic);
                    } else {
                        // if no graphic identified
                        mCallout.dismiss();
                    }
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Identify error: " + e.getMessage());
                }
            }
        });
    }

    private void showCallout(final Graphic graphic) {
        // create a TextView for the Callout
       /* TextView calloutContent = new TextView(getApplicationContext());
        calloutContent.setTextColor(Color.BLACK);
        // set the text of the Callout to graphic's attributes
        calloutContent.setText(graphic.getAttributes().get("name").toString());*/
        // get Callout
        View calloutLayout = LayoutInflater.from(this).inflate(R.layout.related_features_callout, null);
        // create a text view and add park name
        TextView parkText = calloutLayout.findViewById(R.id.park_name);
        parkText.setText(graphic.getAttributes().get("name").toString());
        // set Callout options: animateCallout: true, recenterMap: false, animateRecenter: false
        mCallout.setShowOptions(new Callout.ShowOptions(true, false, false));
        mCallout.setContent(calloutLayout);
        // set the leader position and show the callout
        // set the leader position and show the callout
        Point calloutLocation = graphic.computeCalloutLocation(graphic.getGeometry().getExtent().getCenter(), mMapView);
        mCallout.setGeoElement(graphic, calloutLocation);
        mCallout.show();
    }

    private void setupLocationDisplay() {
        mLocationDisplay = mMapView.getLocationDisplay();
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

          /*  int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if (!(ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
            } else {
                String message = String.format("Error in DataSourceStatusChangedListener: %s",
                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }*/

        });
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                double x = locationChangedEvent.getLocation().getPosition().getX();
                double y = locationChangedEvent.getLocation().getPosition().getY();
            }
        });
        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        mLocationDisplay.startAsync();
    }

    private void createPolygonGraphics() {//面
        PointCollection polygonPoints = new PointCollection(SpatialReferences.getWgs84());
        polygonPoints.add(new Point(114.70372100524446, 38.03519536420519));
        polygonPoints.add(new Point(114.71766916267414, 38.03505116445459));
        polygonPoints.add(new Point(114.71923322580597, 38.04919407570509));
        polygonPoints.add(new Point(114.71631129436038, 38.04915962906471));
        polygonPoints.add(new Point(114.71526020370266, 38.059921300916244));
        polygonPoints.add(new Point(114.71153226844807, 38.06035488360282));
        polygonPoints.add(new Point(114.70803735010169, 38.05014385296186));
        polygonPoints.add(new Point(114.69877903513455, 38.045182336992816));
        polygonPoints.add(new Point(114.6979656552508, 38.040267760924316));
        polygonPoints.add(new Point(114.70259112469694, 38.038800278306674));
        polygonPoints.add(new Point(114.70372100524446, 38.03519536420519));
        Polygon polygon = new Polygon(polygonPoints);
        SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(60, 226, 119, 40),
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f));
        Map<String, Object> attr = new HashMap<>();
        attr.put("position", 2);
        attr.put("name", "R");
        Graphic polygonGraphic = new Graphic(polygon, attr, polygonSymbol);
        //Graphic polygonGraphic = new Graphic(polygon, polygonSymbol);
        mGraphicsOverlay.getGraphics().add(polygonGraphic);

    }

    private void createPolylineGraphics() {//线
        PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
        polylinePoints.add(new Point(114.71511, 38.09042));
        polylinePoints.add(new Point(114.72511, 38.08042));
        Polyline polyline = new Polyline(polylinePoints);
        SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3.0f);
        Map<String, Object> attr = new HashMap<>();
        attr.put("position", 1);
        attr.put("name", "line");
        Graphic polylineGraphic = new Graphic(polyline, attr, polylineSymbol);
        //Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
        //polylineGraphic.setSelected(true);
        mGraphicsOverlay.getGraphics().add(polylineGraphic);

    }

    private void createPointGraphics() {//点
        Point point = new Point(114.71511, 38.09042, SpatialReferences.getWgs84());
        //普通点
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f));
        //图片点
        PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol((BitmapDrawable) ImageUtils.bitmap2Drawable(ImageUtils.getBitmap(R.drawable.car)));
        Map<String, Object> attr = new HashMap<>();
        attr.put("position", 0);
        attr.put("name", "car");
        Graphic pointGraphic = new Graphic(point, attr, pictureMarkerSymbol);
        //Graphic pointGraphic = new Graphic(point, pictureMarkerSymbol);
        //pointGraphic.setSelected(true);
        //Point point1 = pointGraphic.computeCalloutLocation(point, mMapView);
        mGraphicsOverlay.getGraphics().add(pointGraphic);

    }


    @SuppressLint("CheckResult")
    protected void initPermissions() {
        //申请权限
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        // All requested permissions are granted

                    } else {
                        // At least one permission is denied

                    }

                });
    }
}