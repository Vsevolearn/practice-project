package com.uclab.everytree.models;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.uclab.everytree.models.serializers.Record;
import com.uclab.everytree.models.serializers.Tree;
import com.uclab.everytree.services.AppService;
import com.uclab.everytree.services.NetworkService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map {
	private static final String TAG = Map.class.getSimpleName();
	private GoogleMap gMap;
	private Context cxt;
	private LoadingDialog progressDialog;
	private LatLng centerMarker;
	private Marker treeMarker;
	private AppService appService;
	private final Integer levelMapZoom = 17;

	//return center coordinates
	public LatLng getCenter()
	{
		return this.centerMarker;
	}

	public Map (Context _cxt, GoogleMap _map)
	{
		cxt = _cxt;
		progressDialog = new LoadingDialog(cxt);
		gMap = _map;
		appService = new AppService(cxt);
		centerMarker = new LatLng(0,0);

		if (ContextCompat.checkSelfPermission( cxt, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission (cxt, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

			//Получение позиции
			gMap.setMyLocationEnabled(true);

			FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(cxt);

			mFusedLocationClient.getLastLocation()
					.addOnSuccessListener(
							new OnSuccessListener<Location>() {
								@Override
								public void onSuccess(Location location) {
									// Got last known location. In some rare situations this can be null.
									if (location != null) {
										centerMarker = new LatLng(location.getLatitude(), location.getLongitude());
										gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerMarker, levelMapZoom));
									}
								}
							});
		}

		setHandlerSmallInfo();
		setupLazyLoadMarkers();
	}

	public void changeLayer()
	{
		if (gMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID)
		{
			gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}

		else
		{
			gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		}
	}

	//Поместить маркер дерева по центру
	public void setHandlerMarkerOnCenter(boolean state)
	{
		if (state) {
			treeMarker = gMap.addMarker(new MarkerOptions().position(centerMarker));

			//Удержание маркера по центру
			gMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
				@Override
				public void onCameraMove() {
					centerMarker = gMap.getCameraPosition().target;
					treeMarker.setPosition(centerMarker);
				}
			});

			gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerMarker, levelMapZoom));
		}

		else
		{
			treeMarker.remove();
			gMap.setOnCameraMoveListener(null);
		}
	}

	private void setupLazyLoadMarkers()
	{
		gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
										 @Override
										 public void onCameraIdle() {
										 	getAllMarkers();
										 }
									 }

		);
	}


	//Обработчик на показ краткой информации о дереве
	private void setHandlerSmallInfo() {
		//Показ краткой информации о дереве
		gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				if (marker.getTag() == null)
				{
					return false;
				}

				progressDialog.show();

				final int tagMarker = (Integer)marker.getTag();
				final LatLng location = marker.getPosition();

				NetworkService.getInstance(cxt)
						.getEveryTreeAPI()
						.getRecord(tagMarker)
						.enqueue(new Callback<Record>() {
							@Override
							public void onResponse(Call<Record> call, Response<Record> response) {
								appService.setRecord(response.body());

								Tree tree = new Tree();
								tree.setId(tagMarker);
								tree.setLocation(location);

								appService.setTree(tree);

								marker.showInfoWindow();
								progressDialog.hide();
							}

							@Override
							public void onFailure(Call<Record> call, Throwable t) {
								System.out.println(t.getMessage());
								progressDialog.hide();
								Toast.makeText(cxt, "Error when getting record!", Toast.LENGTH_LONG).show();
							}
						});

				return true;
			}
		});
	}

	//Запросы
	private void getAllMarkers()
	{
		double lat = Math.round(gMap.getCameraPosition().target.latitude * 1000.) / 1000.;
		double lon = Math.round(gMap.getCameraPosition().target.longitude * 1000.) / 1000.;
		int zoom = Math.round(gMap.getCameraPosition().zoom);

		NetworkService.getInstance(cxt)
				.getEveryTreeAPI()
				.getMarkers(lat,lon, zoom)
				.enqueue(new Callback<List<Tree>>() {
					@Override
					public void onResponse(Call<List<Tree>> call, Response<List<Tree>> response) {
						if (response.body() != null) {
							for (Tree marker : response.body()) {
								LatLng position = marker.getLocation();
								Marker markerItem = gMap.addMarker(new MarkerOptions().position(position));
								markerItem.setTag(marker.getId());
							}
						}
					}

					@Override
					public void onFailure(Call<List<Tree>> call, Throwable t) {
						System.out.println(t.getMessage());
						Toast.makeText(cxt, "Error when getting all markers!", Toast.LENGTH_LONG).show();
					}
				});
	}

	public String getAddress(LatLng coords) {
		String fullAddress = null;
		Geocoder geocoder = new Geocoder(cxt, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(coords.latitude, coords.longitude, 1);

			if (addresses != null && addresses.size() > 0) {
				fullAddress = addresses.get(0).getAddressLine(0);
			}

		} catch (IOException e) {
			Log.e(TAG, "Unable connect to Geocoder!", e);
		}
		return fullAddress;
	}
}



