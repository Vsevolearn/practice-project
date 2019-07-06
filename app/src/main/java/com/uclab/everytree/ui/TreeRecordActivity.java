package com.uclab.everytree.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.uclab.everytree.R;
import com.uclab.everytree.adapters.RecordTabAdapter;
import com.uclab.everytree.models.LoadingDialog;
import com.uclab.everytree.models.serializers.Photo;
import com.uclab.everytree.models.serializers.Record;
import com.uclab.everytree.models.serializers.Tree;
import com.uclab.everytree.services.AppService;
import com.uclab.everytree.services.AuthService;
import com.uclab.everytree.services.NetworkService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TreeRecordActivity extends AppCompatActivity implements View.OnClickListener {
	private static final String TAG = TreeRecordActivity.class.getSimpleName();
	private TextView treeKeeperTxt;
	private ProgressBar progressBar;
	private AuthService authService;
	private AppService appService;
	private int progressFill = 0;
	private LoadingDialog progressDialog = null;
	private Button saveBtn, cancelBtn;
	private int[] tabIcons = {
			R.drawable.ic_info_black_24dp,
			R.drawable.ic_date_range_black_24dp,
			R.drawable.ic_location_on_black_24dp,
			R.drawable.ic_photo_camera_black_24dp
	};
	private int[] tabHeaders = {
			R.string.infoHeader,
			R.string.dateHeader,
			R.string.siteHeader,
			R.string.photoHeader
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tree_record);

		authService = new AuthService(this);
		appService = new AppService(this);

		saveBtn = findViewById(R.id.saveBtn);
		cancelBtn = findViewById(R.id.cancelBtn);
		treeKeeperTxt = findViewById(R.id.treeKeeperTxt);
		progressBar = findViewById(R.id.progressBar);

		cancelBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);

		progressDialog = new LoadingDialog(this);

		if (getIntent() != null && getIntent().getAction() != null) {
			switchToMode(getIntent().getAction());
		}
	}

	private void switchToMode(String intentMode)
	{
	    setProgressBarOnStart();

		if (intentMode .equals("android.intent.action.TreeRecordActivity.Show")) {
			saveBtn.setText(R.string.editBtn);
			treeKeeperTxt.setText(appService.getRecord().getUser());
			appService.setAddMode(false);
		}

		else if (intentMode.equals("android.intent.action.TreeRecordActivity.Add")) {
			saveBtn.setText(R.string.saveBtn);
			appService.setAddMode(true);
			treeKeeperTxt.setText(authService.getUser().getUsername());
		}

        setTabs();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//replaces the default 'Back' button action
		if(keyCode== KeyEvent.KEYCODE_BACK)
		{
			exitFromActivity();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case (R.id.cancelBtn):
				exitFromActivity();
				break;
			case R.id.saveBtn:
				redirectToActivity();
				break;
		}
	}

	private void redirectToActivity()
	{
		if (authService.isAuthorized())
		{
			if (appService.isAddMode()) {
				postTree();
			}

			else
			{
					Intent intent;
					intent = new Intent("android.intent.action.TreeRecordActivity.Add");
					startActivity(intent);
					finish();
			}
		}

		else
		{
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void setTabs()
	{
		ViewPager pager = findViewById(R.id.pager);
		RecordTabAdapter tabAdapter = new RecordTabAdapter(getSupportFragmentManager());
		pager.setAdapter(tabAdapter);

		TabLayout tabLayout = findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(pager);

		for (int i = 0; i < tabAdapter.getCount(); i++) {
			TabLayout.Tab tab = tabLayout.getTabAt(i);

			if (tab != null) {
				tab.setText(tabHeaders[i]);
				tab.setIcon(tabIcons[i]);
			}
		}
	}

	private void postTree()
	{
		Tree tree = appService.getTree();

		if (tree == null)
		{
			Toast.makeText(getApplicationContext(), "We've got an error when preparing post tree!", Toast.LENGTH_LONG).show();
			return;
		}

		progressDialog.show();
		NetworkService.getInstance(this)
				.getEveryTreeAPI()
				.postTree(tree)
				.enqueue(new Callback<Tree>() {
					@Override
					public void onResponse(Call<Tree> call, Response<Tree> response) {
						progressDialog.hide();

						if (response.body() != null) {
							appService.setTree(response.body());
							postRecord();
						}
					}

					@Override
					public void onFailure(Call<Tree> call, Throwable t) {
						System.out.println(t.getMessage());
						progressDialog.hide();
						Toast.makeText(getApplicationContext(), "We've got an error when posting tree!", Toast.LENGTH_LONG).show();
					}
				});
	}

	private void postRecord() {
		progressDialog.show();

		AppService.getEditableRecord().setTreeId(appService.getTree().getId());

		NetworkService.getInstance(this)
				.getEveryTreeAPI()
				.postRecord(AppService.getEditableRecord())
				.enqueue(new Callback<Record>() {
					@Override
					public void onResponse(Call<Record> call, Response<Record> response) {
						progressDialog.hide();

						if (response.body() != null) {
							appService.setRecord(response.body());
						}

						postRecordPhotos();
					}

					@Override
					public void onFailure(Call<Record> call, Throwable t) {
						System.out.println(t.getMessage());
						progressDialog.hide();
						Toast.makeText(getApplicationContext(), "We've got an error when posting record!", Toast.LENGTH_LONG).show();
					}
				});
	}

	private void postRecordPhotos()
	{
		List<MultipartBody.Part> list = new ArrayList<>();

		if (appService.getPhotosToUpload() == null)
		{
			showDoneDialog();
			return;
		}

		progressDialog.show();

		for (Photo photo : appService.getPhotosToUpload())
		{
			File file = new File(photo.getUrl());

			if(file.exists()) {
				RequestBody requestFile =
						RequestBody.create(
								MediaType.parse("image/jpg"),
								file);

				list.add(MultipartBody.Part.createFormData("url[]", file.getName(), requestFile));
			}
			else {
				Toast.makeText(getApplicationContext(), "We've got an error when posting photos!", Toast.LENGTH_LONG).show();
				return;
			}
		}

		NetworkService.getInstance(this)
				.getEveryTreeAPI()
				.postRecordPhotos(appService.getTree().getId(), list)
				.enqueue(new Callback<Void> () {
					@Override
					public void onResponse(Call<Void>  call, Response<Void>  response) {
						progressDialog.hide();
						showDoneDialog();
					}

					@Override
					public void onFailure(Call<Void>  call, Throwable t) {
						System.out.println(t.getMessage());
						progressDialog.hide();
						Toast.makeText(getApplicationContext(), "We've got an error when posting photos!", Toast.LENGTH_LONG).show();
					}});
	}

	private void setProgressBarOnStart() {
	    if (appService.getRecord() == null)
        {
            return;
        }

		if (appService.getRecord().getSkeletalBranchesNumber() != 0)
		{
			progressFill += 10;
		}

		if (appService.getRecord().getHeight() != 0)
		{
			progressFill += 10;
		}

		if (appService.getRecord().getTrunkDiameter() != 0)
		{
			progressFill += 10;
		}

		if (appService.getRecord().getCondition() != null)
		{
			progressFill += 10;
		}

		if (appService.getRecord().getDateRemoved() != null)
		{
			progressFill += 10;
		}

		if (appService.getRecord().getDatePlanted() != null)
		{
			progressFill += 10;
		}

		progressBar.setProgress(progressFill);
	}

	private void exitFromActivity()
	{
		if (appService.isAddMode()) {
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.confirmExit))
					.setMessage(getString(R.string.confirmExitMsg))
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
					.show();
		}
		else
		{
			finish();
		}
	}

	private void showDoneDialog() {
		if (!isFinishing()) {
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.doneMsg))
					.setCancelable(false)
					.show();
		}

		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Thread(new Runnable() {
					public void run() {
						appService.ClearAfterWrite();
						Intent intent;
						intent = new Intent("android.intent.action.TreeRecordActivity.Show");
						startActivity(intent);
						finish();
					}
				}));
			}
		}, 1500); // End of your timer code.
	}
}