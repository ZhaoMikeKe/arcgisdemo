package com.huachu.arcgis.arcgisdemo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.blankj.utilcode.util.ImageUtils;
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
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private GraphicsOverlay mGraphicsOverlay;
    private RxPermissions rxPermissions;

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
                Bitmap bgimg0 = ImageUtils.getBitmap(R.mipmap.ic_launcher);
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                share_intent.setType("image/*");  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, saveBitmap(bgimg0, "img"));
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "分享");
                MainActivity.this.startActivity(share_intent);
            }
        });

        setupMap();//地图设置
        createPointGraphics();//点
        createPolylineGraphics();//线
        createPolygonGraphics();//面
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
        }
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
        Graphic polygonGraphic = new Graphic(polygon, polygonSymbol);
        mGraphicsOverlay.getGraphics().add(polygonGraphic);
    }

    private void createPolylineGraphics() {//线
        PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
        polylinePoints.add(new Point(114.71511, 38.09042));
        polylinePoints.add(new Point(114.72511, 38.08042));
        Polyline polyline = new Polyline(polylinePoints);
        SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3.0f);
        Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
        mGraphicsOverlay.getGraphics().add(polylineGraphic);
    }

    private void createPointGraphics() {//点
        Point point = new Point(114.71511, 38.09042, SpatialReferences.getWgs84());
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f));
        Graphic pointGraphic = new Graphic(point, pointSymbol);
        mGraphicsOverlay.getGraphics().add(pointGraphic);
    }

    /**
     * 将图片存到本地
     */
    private static Uri saveBitmap(Bitmap bm, String picName) {
        try {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/renji/" + picName + ".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Uri uri = Uri.fromFile(f);
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从Assets中读取图片
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @SuppressLint("CheckResult")
    protected void initPermissions() {
        //申请权限
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        // All requested permissions are granted

                    } else {
                        // At least one permission is denied

                    }

                });
    }
}
