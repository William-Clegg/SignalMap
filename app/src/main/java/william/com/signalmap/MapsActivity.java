package william.com.signalmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton beginDraw;
    private ImageButton finishDraw;
    private ImageButton readjustCamera;
    private ImageButton searchAddress;
    private EditText addressField;
    private boolean drawMode = false;
    private LatLng tempPos;

    public int width;
    public  int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        beginDraw = findViewById(R.id.beginDraw);
        finishDraw = findViewById(R.id.finishDraw);
        readjustCamera = findViewById(R.id.readjustCamera);
        searchAddress = findViewById(R.id.searchAddress);
        addressField = findViewById(R.id.addressField);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        final Geocoder geoCoder = new Geocoder(this, Locale.getDefault());

        searchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String address = addressField.getText().toString();
                    //String address = "3696 Thompson Mill Rd Buford, GA 30519";
                    List<Address> addressMain = geoCoder.getFromLocationName(address, 1);

                    if (addressMain.size() > 0) {
                        Double lat = (double) (addressMain.get(0).getLatitude());
                        Double lon = (double) (addressMain.get(0).getLongitude());

                        final LatLng user = new LatLng(lat, lon);
                        /*used marker for show the location */

                        // Move the camera instantly to hamburg with a zoom of 15.
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));

                        // Zoom in, animating the camera.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        beginDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.getUiSettings().setAllGesturesEnabled(false);
                beginDraw.setVisibility(View.GONE);
                finishDraw.setVisibility(View.VISIBLE);
                readjustCamera.setVisibility(View.VISIBLE);
                drawMode = true;
            }
        });

        readjustCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.getUiSettings().setAllGesturesEnabled(true);
                beginDraw.setVisibility(View.VISIBLE);
                readjustCamera.setVisibility(View.GONE);
                finishDraw.setVisibility(View.GONE);
                drawMode = false;
            }
        });

        finishDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (drawMode) {
                    if (tempPos == null) {
                        tempPos = latLng;
                    } else {
                        mMap.addPolyline((new PolylineOptions()
                                .add(tempPos, latLng)
                                .width(5)
                                .color(Color.BLUE)));
                        tempPos = latLng;
                    }
                }
            }
        });
    }
}


