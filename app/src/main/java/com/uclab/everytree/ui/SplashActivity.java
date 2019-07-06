package com.uclab.everytree.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
	private String[] Permissions = {
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.INTERNET,
	};
	private final int PERMISSIONS_ALL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isPermissions()) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	//Проверка разрешений
	private boolean isPermissions()
	{
		if (hasPermissions(this, Permissions))
		{
			return true;
		} else {
			ActivityCompat.requestPermissions(this, Permissions,
					PERMISSIONS_ALL);
		}
		return false;
	}

	//Проверка полномочий
	public static boolean hasPermissions(Context context, String... permissions) {
		if (context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}

	//Получение ответа от пользователя на проверку разрешений
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSIONS_ALL) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED
					&& grantResults[1] == PackageManager.PERMISSION_GRANTED
					&& grantResults[2] == PackageManager.PERMISSION_GRANTED
					&& grantResults[3] == PackageManager.PERMISSION_GRANTED
					&& grantResults[4] == PackageManager.PERMISSION_GRANTED
					&& grantResults[5] == PackageManager.PERMISSION_GRANTED) {

				//Запуск главного экрана
				startActivity(new Intent(this, MainActivity.class));
				finish();
			} else {
				// permission denied
				Toast.makeText(this,
						"For further work app you need to restart the app and give him all permissions!", Toast.LENGTH_LONG).show();
			}
		}
	}
}