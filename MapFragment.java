package com.example.hp.myapplication;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import android.location.LocationListener;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location lastLocation;
    public Marker currentUserLocationMarker;
    private static final int Request_User_Location_code = 99;

    private static final Object TAG = "tag";
    Context context;
    MapView mapView;
    GoogleMap googleMap;
    Circle circle;

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    View view;
    int col;
    boolean onetime = true;

    ArrayList markerPoints = new ArrayList();

    MarkerOptions markerOptions = new MarkerOptions();

    public MapFragment() {
        //require empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapView);

        checkPermissions();

        mapView.onCreate(savedInstanceState);

        MapsInitializer.initialize(this.getActivity());

        mapView.getMapAsync(this);

        return view;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (checkPermission()) {
            buildGoogleApiClient();
             googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }


        circle  = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(11.503722, 77.276783))
                .radius(1000)
                .strokeWidth(0)
                .strokeColor(Color.WHITE)
                .fillColor(Color.argb(0, 255, 0, 5))
                  .clickable(true));
         
        circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(11.509347, 77.254602))
                .radius(1000)
                .strokeWidth(0)
                .strokeColor(Color.WHITE)
                .fillColor(Color.argb(0, 0, 255, 0))
                .clickable(true));

        circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(11.487108, 77.239665))
                .radius(1000)
                .strokeWidth(0)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(0, 0, 0, 250))
                .clickable(true));
                        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {

                            @Override
                            public void onCircleClick(Circle circle) {
                                int strokeColor = circle.getFillColor() ^ 0xffffffff;
                                circle.setFillColor(strokeColor);





        }
                                                           });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    googleMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                googleMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() == 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });



        /*LatLng coordinate1 = new LatLng(11.495916, 77.276485);
        googleMap.addMarker(new MarkerOptions().position(coordinate1).title("IT")).showInfoWindow();

        LatLng coordinate2 = new LatLng( 11.494961, 77.277013 );
        googleMap.addMarker(new MarkerOptions().position(coordinate2).title("pandal")).showInfoWindow();

        LatLng coordinate3 = new LatLng(11.495104, 77.276474);
        googleMap.addMarker(new MarkerOptions().position(coordinate3).title("LABS")).showInfoWindow();*/
    }

    private class DownloadTask extends AsyncTask {


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ParserTask parserTask = new ParserTask();


            parserTask.execute(o.toString());
        }

        @Override
        protected String doInBackground(Object[] objects) {
            String data = "";

            try {
                data = downloadUrl(objects[0].toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Log.d("onPostExecute: 111111", jObject.toString());
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = new PolylineOptions();

            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                Log.d("onPostExecute: 111111", "onPostExecute: here");
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {

                    HashMap point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat").toString());
                    double lng = Double.parseDouble(point.get("lng").toString());
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            //googleMap.addPolyline(lineOptions);
            if (points != null) googleMap.addPolyline(lineOptions);
            else {
                Log.d("onPostExecute: 111111", "onPostExecute: ");
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyASaSsdIcgBTsKnbgvspsupvov-3m-NAv4";

        Log.d("onPostExecute: 111111", url);
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }

        return true;
    }

    private boolean checkPermission() {

        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);


                    }
                }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {

                final Status status = result.getStatus();

                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:

                        Log.i(String.valueOf(TAG), "All location settings are satisfied.");

                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        Log.i((String) TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {

                            status.startResolutionForResult(getActivity(), 0x1);
                        } catch (IntentSender.SendIntentException e) {

                            Log.i((String) TAG, "PendingIntent unable to execute request.");
                        }

                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i((String) TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }


    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        //googleMap.clear();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("TEST_MACHINE");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        currentUserLocationMarker = googleMap.addMarker(markerOptions);
        currentUserLocationMarker.showInfoWindow();

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 11);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        googleMap.moveCamera(yourLocation);
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
        googleMap.animateCamera(zoom);

        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //  googleMap.animateCamera(CameraUpdateFactory.zoomBy(11));
        if (googleApiClient != null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}


