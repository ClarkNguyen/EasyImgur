package sg.vinova.easy_imgur.fragment.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.models.MUser;
import sg.vinova.easy_imgur.networking.ImgurAPI;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.utilities.TokenUtility;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

public class UploadImageFragment extends BaseFragment implements
		OnClickListener {

	// tag
	public static final String TAG = "UploadImageFragment";

	// request code
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_IMAGE_CROP = 2;

	// UI component
	private Button btnUpload;
	private Button btnCamera;
	private Button btnCrop;
	private ImageView ivThumb;

	// image data base64
	private String imageData;

	// picture uri
	private Uri imageUri;

	// picture bitmap
	Bitmap imageBitmap;

	// temporary image file
	File tempFile;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.upload_image_fragment, container,
				false);

		findViews(view);
		return view;
	}

	private void findViews(View view) {
		// connect to UI component
		ivThumb = (ImageView) view.findViewById(R.id.ivThumb);
		btnUpload = (Button) view.findViewById(R.id.btn_upload);
		btnCamera = (Button) view.findViewById(R.id.btn_camera);
		btnCrop = (Button) view.findViewById(R.id.btn_crop);

		btnUpload.setOnClickListener(this);
		btnCamera.setOnClickListener(this);
		btnCrop.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btnUpload) {
			uploadImage(imageBitmap);

		} else if (v == btnCamera) {
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			if (takePictureIntent.resolveActivity(getActivity()
					.getPackageManager()) != null) {

				try {
					tempFile = File.createTempFile("crop", "png",
							Environment.getExternalStorageDirectory());
					imageUri = Uri.fromFile(tempFile);
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							imageUri);

					startActivityForResult(takePictureIntent,
							REQUEST_IMAGE_CAPTURE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (v == btnCrop) {
			cropImage();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE
				&& resultCode == Activity.RESULT_OK) {
			try {
				imageBitmap = MediaStore.Images.Media.getBitmap(getActivity()
						.getContentResolver(), imageUri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// uploadImage(imageData);
			ivThumb.setImageBitmap(imageBitmap);

			// LogUtility.e(TAG, "Encoded: " + imageData);
			LogUtility.e(TAG, "Capture: " + imageBitmap.getWidth() + " --- "
					+ imageBitmap.getHeight());

		} else if (requestCode == REQUEST_IMAGE_CROP
				&& resultCode == Activity.RESULT_OK && data != null) {
			// get the returned data
			Bundle extras = data.getExtras();
			// get the cropped bitmap
			imageBitmap = extras.getParcelable("data");

			ivThumb.setImageBitmap(imageBitmap);
		}
	}

	private void cropImage() {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		// indicate image type and Uri
		cropIntent.setDataAndType(imageUri, "image/*");
		// set crop properties
		cropIntent.putExtra("crop", "true");
		// cropIntent.putExtra("output", imageUri);
		cropIntent.putExtra("return-data", true);
		// start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
	}

	private void uploadImage(final Bitmap image) {
		// File extStore = Environment.getExternalStorageDirectory();
		// File imageFile = new File(extStore + "/Download/images.jpeg");
		// LogUtility.e(TAG, "size: " + imageFile.getAbsolutePath());

		if (image == null) {
			return;
		}

		// convert image to base64 for upload
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		imageBitmap.compress(Bitmap.CompressFormat.PNG, 100,
				byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String encodedData = Base64.encodeToString(byteArray, Base64.DEFAULT);

		AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {

				// get new token when current token is expires.
				if (status.getCode() == 403) {
					// handle refresh token
					ImgurAPI.getClient().getNewToken(mContext,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject json) {
									LogUtility.e(TAG,
											"RefreshToken: " + json.toString());
									if (json != null) {
										try {
											MUser mUser = TokenUtility
													.getUser(mContext);
											mUser.setAccessToken(json
													.getString("access_token"));
											mUser.setExpires(json
													.getInt("expires_in"));
											mUser.setRefreshToken(json
													.getString("refresh_token"));
											mUser.setUserName(json
													.getString("account_username"));
											mUser.setTokenType(json
													.getString("token_type"));

											TokenUtility.saveUser(mContext,
													new Gson().toJson(mUser));

											uploadImage(image);
										} catch (Exception e) {
											LogUtility.e(TAG,
													"Parse token error");
										}
									}
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError err) {
									LogUtility.e(TAG, "RefreshTokenError: "
											+ err.networkResponse.statusCode);
								}
							});
				} else {
					Gson gson = new Gson();
					MGallery mGallery = gson.fromJson(json.toString(),
							MGallery.class);
					LogUtility.e(TAG, "Return data: " + json.toString());
					Toast.makeText(mContext, "Upload success",
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		MUser mUser = TokenUtility.getUser(mContext);
		if (mUser != null) {
			callback.header("Authorization", "Bearer " + mUser.getAccessToken());
		} else {
			callback.header("Authorization", "Client-ID " + Constant.CLIENT_ID);
		}

		ImgurAPI.getClient().uploadImage(mContext, encodedData, null, null,
				"sample", "sample image upload", callback);
	}
}
