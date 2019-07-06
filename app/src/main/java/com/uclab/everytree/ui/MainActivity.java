package com.uclab.everytree.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uclab.everytree.R;
import com.uclab.everytree.layouts.MapWrapperLayout;
import com.uclab.everytree.listeners.OnInfoWindowElemTouchListener;
import com.uclab.everytree.models.LoadingDialog;
import com.uclab.everytree.models.Map;
import com.uclab.everytree.models.serializers.Record;
import com.uclab.everytree.models.serializers.Tree;
import com.uclab.everytree.models.serializers.spinner.CommonName;
import com.uclab.everytree.models.serializers.spinner.ScientificName;
import com.uclab.everytree.models.serializers.spinner.SiteType;
import com.uclab.everytree.services.AppService;
import com.uclab.everytree.services.AuthService;
import com.uclab.everytree.services.NetworkService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener
{
	private ViewGroup infoWindow;
	private OnInfoWindowElemTouchListener infoButtonListener;
	private MapWrapperLayout mapWrapperLayout;
	private AuthService authService;
	private AppService appService;
	private Map map = null;
	private TextView cmnNameTxt, scNameTxt, addressTxt;
	private FloatingActionButton mapLayerBtn, addTreeBtn;
	private Button saveTreeBtn;
	private LoadingDialog progressDialog;

	//Отрисовка экрана
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progressDialog = new LoadingDialog(this);

		authService = new AuthService(this);
		appService = new AppService(this);
		appService.ClearAll();

		mapLayerBtn = findViewById(R.id.mapLayerBtn);
		addTreeBtn = findViewById(R.id.addTreeBtn);
		saveTreeBtn = findViewById(R.id.saveTreeBtn);

		mapLayerBtn.setOnClickListener(this);
		addTreeBtn.setOnClickListener(this);
		saveTreeBtn.setOnClickListener(this);

		fetchCommonNames();
		fetchScientificNames();
		fetchSiteTypes();

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (mapFragment != null) {
			mapFragment.getMapAsync(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.header_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.userPageBtn: {
				if (authService.isAuthorized()) {
					Intent intent = new Intent(getApplicationContext(), UserActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(intent);
				}
				return true;
			}
			case R.id.usersScoresBtn:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//Обработчик готовности карты
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = new Map(this, googleMap);

		//Инициализация infoWindow
		mapWrapperLayout = findViewById(R.id.map_relative_layout);

		// MapWrapperLayout initialization
		// 39 - default marker height
		// 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
		// last param is for offset between click at buttons
		mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39 + 20), getPixelsFromDp(this, 20));

		// We want to reuse the info window for all the markers,
		// so let's create only one class member instance
		infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.info_window, null);

		cmnNameTxt = infoWindow.findViewById(R.id.cmnNameTxt);
		scNameTxt = infoWindow.findViewById(R.id.scNameTxt);
		addressTxt = infoWindow.findViewById(R.id.addressTxt);

		Button infoButton1 = infoWindow.findViewById(R.id.btnEdit);
		Button infoButton2 = infoWindow.findViewById(R.id.btnMore);

		//Edit btn
		infoButtonListener = new OnInfoWindowElemTouchListener(infoButton1) {
			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				if (authService.isAuthorized() && appService.getRecord() != null) {
					Intent intent;
					intent = new Intent("android.intent.action.TreeRecordActivity.Add");
					startActivity(intent);
				}

				else
				{
					Toast.makeText(getApplicationContext(), "The record wasn't found!", Toast.LENGTH_LONG).show();
				}
			}
		};
		infoButton1.setOnTouchListener(infoButtonListener);

		//Show btn
		infoButtonListener = new OnInfoWindowElemTouchListener(infoButton2) {
			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				if (appService.getRecord() != null) {
					Intent intent;
					intent = new Intent("android.intent.action.TreeRecordActivity.Show");
					startActivity(intent);
				}

				else
				{
					Toast.makeText(getApplicationContext(), "The record wasn't found!", Toast.LENGTH_LONG).show();
				}
			}
		};
		infoButton2.setOnTouchListener(infoButtonListener);

		googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				//Отрисовать infoWindow
				infoButtonListener.setMarker(marker);
				Record record = appService.getRecord();

				if (record != null) {
					CommonName commonName = appService.getCommonNames().get(record.getCommonName() - 1);
					ScientificName scientificName = appService.getScientificNames().get(commonName.getScientificNameId() - 1);
					String commonText = setInfoPopup(getString(R.string.nav_item2), commonName.getName());
					String scientificText = setInfoPopup(getString(R.string.nav_item3), scientificName.getName());
					String nearestText = setInfoPopup(getString(R.string.nav_item13),record.getNearestAddress());

					cmnNameTxt.setText(commonText);
					scNameTxt.setText(scientificText);
					addressTxt.setText(nearestText);
				}

				else
				{
					cmnNameTxt.setText(setInfoPopup(null, null));
					scNameTxt.setText(setInfoPopup(null, null));
					addressTxt.setText(setInfoPopup(null, null));
				}

				// We must call this to set the current marker and infoWindow references
				// to the MapWrapperLayout
				mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
				return infoWindow;
			}
		});
	}

	private String setInfoPopup(String append, String value)
	{
		if (value == null)
		{
			return getString(R.string.notSet);
		}

		return append + ": " + value;
	}

	//Получение размеров
	private int getPixelsFromDp (Context context,float dp){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void toggleStateAddTreeBtn()
    {
        if (saveTreeBtn.getVisibility() == View.GONE)
        {
            saveTreeBtn.setVisibility(View.VISIBLE);
            map.setHandlerMarkerOnCenter(true);
            addTreeBtn.setImageResource(R.drawable.ic_cancel_black_24dp);
        }

        else
        {
            saveTreeBtn.setVisibility(View.GONE);
            map.setHandlerMarkerOnCenter(false);
            addTreeBtn.setImageResource(R.drawable.ic_location_on_black_24dp);
        }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.mapLayerBtn:
				map.changeLayer();
				break;
			case R.id.addTreeBtn:
                if (authService.isAuthorized()) {
                    toggleStateAddTreeBtn();
                }
				break;

            case R.id.saveTreeBtn:
                if (authService.isAuthorized() && map.getCenter() != null) {
                	Tree tree = new Tree();
                	tree.setLocation(map.getCenter());
					appService.setTree(tree);

                	Record record = new Record();
                    record.setNearestAddress(map.getAddress(map.getCenter()));
                    appService.setRecord(record);

                    Intent intent = new Intent("android.intent.action.TreeRecordActivity.Add");
                    startActivity(intent);

                    toggleStateAddTreeBtn();
                }
                break;
		}
	}

	private void fetchCommonNames()
	{
		if (appService.getCommonNames() != null)
		{
			return;
		}

		progressDialog.show();

		NetworkService.getInstance(this)
				.getEveryTreeAPI()
				.getCommonNames()
				.enqueue(new Callback<List<CommonName>>() {
					@Override
					public void onResponse(Call<List<CommonName>> call, Response<List<CommonName>> response) {
						if (response.body() != null)
						{
							appService.setCommonNames(response.body());
						}

						progressDialog.hide();
					}

					@Override
					public void onFailure(Call<List<CommonName>>  call, Throwable t) {
						System.out.println(t.getMessage());
						progressDialog.hide();
						Toast.makeText(getApplicationContext(), "We've got an error when was getting a common names list!", Toast.LENGTH_LONG).show();
					}});
	}

	private void fetchSiteTypes()
	{
		if (appService.getSiteTypes() != null)
		{
			return;
		}

		progressDialog.show();

		NetworkService.getInstance(this)
				.getEveryTreeAPI()
				.getSiteTypes()
				.enqueue(new Callback<List<SiteType>>() {
					@Override
					public void onResponse(Call<List<SiteType>> call, Response<List<SiteType>> response) {
						if (response.body() != null)
						{
							appService.setSiteTypes(response.body());
						}

						progressDialog.hide();
					}

					@Override
					public void onFailure(Call<List<SiteType>>  call, Throwable t) {
						System.out.println(t.getMessage());
						progressDialog.hide();
						Toast.makeText(getApplicationContext(), "We've got an error when was getting a site types list!", Toast.LENGTH_LONG).show();
					}});
	}

	private void fetchScientificNames()
	{
		if (appService.getScientificNames() != null)
		{
			return;
		}

		progressDialog.show();

		NetworkService.getInstance(this)
				.getEveryTreeAPI()
				.getScientificNames()
				.enqueue(new Callback<List<ScientificName>>() {
					@Override
					public void onResponse(Call<List<ScientificName>> call, Response<List<ScientificName>> response) {
						if (response.body() != null)
						{
							appService.setScientificNames(response.body());
						}

						progressDialog.hide();
					}

					@Override
					public void onFailure(Call<List<ScientificName>>  call, Throwable t) {
						System.out.println(t.getMessage());
						progressDialog.hide();
						Toast.makeText(getApplicationContext(), "We've got an error when was getting a scientific names list!", Toast.LENGTH_LONG).show();
					}});
	}
}