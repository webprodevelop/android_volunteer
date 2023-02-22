//@formatter:off
package com.iot.volunteer.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iot.volunteer.App;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.AppConst;
import com.iot.volunteer.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ActivityUserData extends ActivityBase implements View.OnClickListener {
	private final String TAG = "ActivityUserData";

	private final static int            REQUEST_CODE_IMPORT_PHOTO		= 0;
	private final static int            REQUEST_CODE_IMPORT_ID_FRONT	= 1;
	private final static int            REQUEST_CODE_IMPORT_ID_BACK		= 2;
	private final static int            REQUEST_CODE_IMPORT_BUSINESS	= 3;

	private final static int            CAMERA_REQUEST_MODE     = 0;
	private final static int            GALLERY_REQUEST_MODE    = 1;

	private int							requestMode = -1;

	private ImageView				ivBack;

	private RelativeLayout			rlImportPhoto;
	private ImageView				ivUploadPhoto;
	private TextView				tvUploadPhoto;
	private ImageView				ivPhoto;

	private TextView				tvName;
	private LinearLayout			llWoman;
	private LinearLayout			llMan;
	private TextView				tvBirthday;
	private TextView				tvIDNumber;
	private TextView				tvIDAddress;

	private ImageView				ivIDFrontPhoto;
	private ImageView				ivIDBackPhoto;

	private TextView				tvPhone;

	private EditText				edtUserAddress;
	private EditText				edtWork;
	private Spinner					spnJob;
	private EditText				edtJobCert;
	private ImageView				ivCertPhoto;

	private TextView				tvConfirm;

	private Bitmap					photoBitmap = null;
	private boolean					photoBitmapChanged = false;
	private Bitmap					frontBitmap = null;
	private boolean					frontBitmapChanged = false;
	private Bitmap					backBitmap = null;
	private boolean					backBitmapChanged = false;
	private Bitmap					certBitmap = null;
	private boolean					certBitmapChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_data);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		loadData();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.CAMERA },
						AppConst.REQUEST_PERMISSION_CAMERA
				);
			}
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ivBack);
		rlImportPhoto = findViewById(R.id.rlImportPhoto);

		ivUploadPhoto = findViewById(R.id.ivUploadPhoto);
		tvUploadPhoto = findViewById(R.id.tvUploadPhoto);
		ivPhoto = findViewById(R.id.ivPhoto);

		tvName = findViewById(R.id.tvName);
		llWoman = findViewById(R.id.llWoman);
		llMan = findViewById(R.id.llMan);
		tvBirthday = findViewById(R.id.tvBirthday);
		tvIDNumber = findViewById(R.id.tvIDNumber);
		tvIDAddress = findViewById(R.id.tvIDAddress);
		ivIDFrontPhoto = findViewById(R.id.ivIDFrontPhoto);

		ivIDBackPhoto = findViewById(R.id.ivIDBackPhoto);

		tvPhone = findViewById(R.id.tvPhone);

		edtUserAddress = findViewById(R.id.edtUserAddress);
		edtWork = findViewById(R.id.edtWork);
		spnJob = findViewById(R.id.spnJob);
		edtJobCert = findViewById(R.id.edtJobCert);

		ivCertPhoto = findViewById(R.id.ivCertPhoto);

		tvConfirm = findViewById(R.id.tvConfirm);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);

		rlImportPhoto.setOnClickListener(this);

		tvName.setOnClickListener(this);
		llWoman.setOnClickListener(this);
		llMan.setOnClickListener(this);
		tvBirthday.setOnClickListener(this);
		tvIDNumber.setOnClickListener(this);
		tvIDAddress.setOnClickListener(this);
		ivIDFrontPhoto.setOnClickListener(this);

		ivIDBackPhoto.setOnClickListener(this);

		ivCertPhoto.setOnClickListener(this);

		tvConfirm.setOnClickListener(this);
	}

	private void loadData() {
		if (!Prefs.Instance().getUserPhoto().isEmpty()) {
			ivPhoto.setVisibility(View.VISIBLE);
			tvUploadPhoto.setVisibility(View.GONE);
			Target target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					photoBitmap = bitmap;
					ivPhoto.setImageBitmap(bitmap);
				}

				@Override
				public void onBitmapFailed(Exception e, Drawable errorDrawable) {

				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {

				}
			};
			ivPhoto.setTag(target);
			Picasso.get().load(Prefs.Instance().getUserPhoto())
					.placeholder(R.drawable.img_contact)
					.into(target);
		} else {
			ivPhoto.setVisibility(View.GONE);
			tvUploadPhoto.setVisibility(View.VISIBLE);
		}

		tvName.setText(Prefs.Instance().getUserName());
		if (Prefs.Instance().getSex()) {
			onSelectSex(llMan);
		} else {
			onSelectSex(llWoman);
		}
		if (!Prefs.Instance().getBirthday().isEmpty()) {
			tvBirthday.setText(Prefs.Instance().getBirthday());
		}
		tvIDAddress.setText(Prefs.Instance().getAddress());
		tvIDNumber.setText(Prefs.Instance().getIDCardNum());

		tvPhone.setText(Prefs.Instance().getUserPhone());

		edtUserAddress.setText(Prefs.Instance().getResidence());
		edtWork.setText(Prefs.Instance().getCompany());
		String job = Prefs.Instance().getJob();
		ArrayList<String> jobArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.job_array)));
		int jobIndex = jobArray.indexOf(job);
		if (jobIndex != -1) {
			spnJob.setSelection(jobIndex);
		}
		edtJobCert.setText(Prefs.Instance().getJobCert());

		if (!Prefs.Instance().getIDCardFront().isEmpty()) {
			Target target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					frontBitmap = bitmap;
					ivIDFrontPhoto.setImageBitmap(bitmap);
				}

				@Override
				public void onBitmapFailed(Exception e, Drawable errorDrawable) {

				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {

				}
			};
			ivIDFrontPhoto.setTag(target);
			Picasso.get().load(Prefs.Instance().getIDCardFront())
					.placeholder(R.drawable.img_contact)
					.into(target);
		}

		if (!Prefs.Instance().getIDCardBack().isEmpty()) {
			Target target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					backBitmap = bitmap;
					ivIDBackPhoto.setImageBitmap(bitmap);
				}

				@Override
				public void onBitmapFailed(Exception e, Drawable errorDrawable) {

				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {

				}
			};
			ivIDBackPhoto.setTag(target);
			Picasso.get().load(Prefs.Instance().getIDCardBack())
					.placeholder(R.drawable.img_contact)
					.into(target);
		}

		if (!Prefs.Instance().getCertPic().isEmpty()) {
			Target target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					certBitmap = bitmap;
					ivCertPhoto.setImageBitmap(bitmap);
				}

				@Override
				public void onBitmapFailed(Exception e, Drawable errorDrawable) {

				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {

				}
			};
			ivCertPhoto.setTag(target);
			Picasso.get().load(Prefs.Instance().getCertPic())
					.placeholder(R.drawable.img_contact)
					.into(target);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ivBack:
				finish();
				break;
			case R.id.rlImportPhoto:
				onImportPhoto(REQUEST_CODE_IMPORT_PHOTO);
				break;
			case R.id.llMan:
			case R.id.llWoman:
			case R.id.tvName:
			case R.id.tvBirthday:
			case R.id.tvIDAddress:
			case R.id.tvIDNumber:
			case R.id.ivIDFrontPhoto:
				onImportPhoto(REQUEST_CODE_IMPORT_ID_FRONT);
				break;
			case R.id.ivIDBackPhoto:
				onImportPhoto(REQUEST_CODE_IMPORT_ID_BACK);
				break;
			case R.id.ivCertPhoto:
				onImportPhoto(REQUEST_CODE_IMPORT_BUSINESS);
				break;
			case R.id.tvConfirm:
				onConfirm();
				break;
		}
	}

	private void onImportPhoto(final int requestCode) {
		final BottomSheetDialog dialog = new BottomSheetDialog(this);
		View parentView = getLayoutInflater().inflate(R.layout.dialog_select_picture_source, null);
		dialog.setContentView(parentView);
		((View)parentView.getParent()).setBackgroundColor(Color.TRANSPARENT);
		TextView tvTitle = parentView.findViewById(R.id.tvTitle);
		TextView tvPhoto = parentView.findViewById(R.id.ID_TXTVIEW_TAKE_PHOTO);
		TextView tvGallery = parentView.findViewById(R.id.ID_TXTVIEW_FROM_GALLERY);
		TextView tvCancel = parentView.findViewById(R.id.ID_TXTVIEW_CANCEL);

		if (requestCode == REQUEST_CODE_IMPORT_PHOTO) {
			tvTitle.setText(R.string.str_select_picture_source);
		} else if (requestCode == REQUEST_CODE_IMPORT_ID_FRONT) {
			tvTitle.setText(R.string.str_select_idcard_source);
		} else if (requestCode == REQUEST_CODE_IMPORT_ID_BACK) {
			tvTitle.setText(R.string.str_select_idcard_source);
		} else if (requestCode == REQUEST_CODE_IMPORT_BUSINESS) {
			tvTitle.setText(R.string.str_select_picture_source);
		}

		tvPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(
								ActivityUserData.this,
								new String[] { Manifest.permission.CAMERA },
								AppConst.REQUEST_PERMISSION_CAMERA
						);

						return;
					}
				}

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageOutputUri());
				requestMode = CAMERA_REQUEST_MODE;
				startActivityForResult(intent, requestCode);
			}
		});
		tvGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				requestMode = GALLERY_REQUEST_MODE;
				startActivityForResult(Intent.createChooser(intent, ""), requestCode);
			}
		});
		tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private Uri getCaptureImageOutputUri() {
		Uri outputFileUri = null;
		File getImage = getExternalCacheDir();
		if (getImage != null) {
			outputFileUri = FileProvider.getUriForFile(this, "com.iot.volunteer.provider",
					new File(getImage.getPath(), "idCard.png"));
		}
		return outputFileUri;
	}

	private void onSelectSex(View view) {
		llMan.setSelected(false);
		llWoman.setSelected(false);

		view.setSelected(true);
	}

	private void onConfirm() {
		if (frontBitmap == null) {
			Util.ShowDialogError(R.string.str_upload_id_front);
			return;
		}

		updateUserInfo();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode >= REQUEST_CODE_IMPORT_PHOTO && requestCode <= REQUEST_CODE_IMPORT_BUSINESS) {
//				Uri pathUri = data.getData();
				Uri pathUri = (requestMode == CAMERA_REQUEST_MODE) ? getCaptureImageOutputUri() : data.getData();
				String path = "";
				Bitmap bitmap = null;
				if (pathUri == null) {
					bitmap = (Bitmap) data.getExtras().get("data");
				} else {
					try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pathUri);
						bitmap = getRotatedBitmap(bitmap, pathUri);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (bitmap != null) {
					final int REQ_SIZE = 500;
					int rW, rH;
					if (bitmap.getWidth() < bitmap.getHeight()) {
						rW = REQ_SIZE;
						rH = (int) (REQ_SIZE * (bitmap.getHeight() / (float) bitmap.getWidth()));
					} else {
						rH = REQ_SIZE;
						rW = (int) (REQ_SIZE * (bitmap.getWidth() / (float) bitmap.getHeight()));
					}

					if (requestCode == REQUEST_CODE_IMPORT_PHOTO) {
						if (photoBitmap != null) {
							photoBitmap.recycle();
							photoBitmap = null;
						}
						photoBitmap = Util.resizeBitmap(bitmap,  rW, rH);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);
						ivPhoto.setVisibility(View.VISIBLE);
						tvUploadPhoto.setVisibility(View.GONE);
						photoBitmapChanged = true;
						ivPhoto.setImageBitmap(photoBitmap);
					} else if (requestCode == REQUEST_CODE_IMPORT_ID_FRONT) {

//						frontBitmap = Util.resizeBitmap(bitmap, rW, rH);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);

						getIdCardFrontInfo(true, Util.resizeBitmap(bitmap, rW, rH));
//						getIdCardFrontInfo(true, Util.resizeBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight()));
					} else if (requestCode == REQUEST_CODE_IMPORT_ID_BACK) {
						if (backBitmap != null) {
							backBitmap.recycle();
							backBitmap = null;
						}
						backBitmap = Util.resizeBitmap(bitmap, rW, rH);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);
						backBitmapChanged = true;
						ivIDBackPhoto.setImageBitmap(backBitmap);
					} else if (requestCode == REQUEST_CODE_IMPORT_BUSINESS) {
						if (certBitmap != null) {
							certBitmap.recycle();
							certBitmap = null;
						}
						certBitmap = Util.resizeBitmap(bitmap, rW, rH);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);
						certBitmapChanged = true;
						ivCertPhoto.setImageBitmap(certBitmap);
					}

					bitmap.recycle();
					bitmap = null;
				}
			}
		}
	}

	private void getIdCardFrontInfo(boolean showProgress, Bitmap bitmap) {
		if (showProgress) {
			m_dlgProgress.show();
		}
		String strFrontPhoto = bitmap != null ? Util.getEncoded64ImageStringFromBitmap(bitmap) : "";
		if (strFrontPhoto.isEmpty()){
			return;
		}

		HttpAPI.getIdCardFrontInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(),
				strFrontPhoto, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityUserData.this, sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strBirthday = dataObject.getString("birthday");
					String strCardNumber = dataObject.getString("id_card_num");
					String strAddress = dataObject.getString("address");
					String strSex = dataObject.optString("sex");
					String strName = dataObject.getString("name");

					tvName.setText(strName);
					tvBirthday.setText(strBirthday);
					tvIDNumber.setText(strCardNumber);
					tvIDAddress.setText(strAddress);
					if (dataObject.getString("sex").equals("1")) {
						onSelectSex(llMan);
					} else {
						onSelectSex(llWoman);
					}
					if (frontBitmap != null) {
						frontBitmap.recycle();
						frontBitmap = null;
					}
					frontBitmap = bitmap;
					frontBitmapChanged = true;
					ivIDFrontPhoto.setImageBitmap(frontBitmap);
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							ActivityUserData.this,
							getResources().getString(R.string.str_api_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						ActivityUserData.this,
						getResources().getString(R.string.str_api_failed)
				);
			}
		}, TAG);
	}

	private void updateUserInfo() {
		m_dlgProgress.show();
		String strName = tvName.getText().toString();
		int sex = llMan.isSelected() ? 1 : 0;
//		String birthday = Util.convertDateString(this, tvBirthday.getText().toString(), true);
		String birthday = tvBirthday.getText().toString();
		String address = edtUserAddress.getText().toString();
		String strJob = spnJob.getSelectedItem().toString();
		String strCompany = edtWork.getText().toString();

		String strPhoto = (photoBitmapChanged && photoBitmap != null) ? Util.getEncoded64ImageStringFromBitmap(photoBitmap) : "";
		String strFrontPhoto = (frontBitmapChanged && frontBitmap != null) ? Util.getEncoded64ImageStringFromBitmap(frontBitmap) : "";
		String strBackPhoto = (backBitmapChanged && backBitmap != null) ? Util.getEncoded64ImageStringFromBitmap(backBitmap) : "";
		String strCertPic = (certBitmapChanged && certBitmap != null) ? Util.getEncoded64ImageStringFromBitmap(certBitmap) : "";

		String strIdCardNum = tvIDNumber.getText().toString();
		String strAddress = tvIDAddress.getText().toString();
		String strJobCert = edtJobCert.getText().toString();

		HttpAPI.updateUserInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strName, sex, birthday, address, Prefs.Instance().getUserPhone(),
				strCompany, strJob, strIdCardNum, strAddress, strPhoto, strFrontPhoto, strBackPhoto, strCertPic, strJobCert, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityUserData.this, sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strBirthday = dataObject.getString("birthday");
					String strIDBack = dataObject.getString("id_card_back");
					String strAddress = dataObject.getString("address");
					String strCity = dataObject.getString("city");
					boolean bSex = dataObject.getInt("sex") == 1 ? true : false;
					String strIDFront = dataObject.getString("id_card_front");
					String strMobile = dataObject.getString("mobile");
					String strCertificate = dataObject.getString("certificate");
					String strPhoto = dataObject.getString("picture");
					String strToken = dataObject.getString("token");
					String strIDNum = dataObject.getString("id_card_num");
					String strCertPic = dataObject.getString("certificate_pic");
					String strProvince = dataObject.getString("province");
					String strDistrict = dataObject.getString("district");
					String strUserName = dataObject.getString("name");
					String strCompany = dataObject.getString("company");
					String strResidence = dataObject.getString("residence");
					String strJob = dataObject.getString("job");
					int iPoint = (int) dataObject.getDouble("point_level");
					int iMerit = (int) dataObject.getDouble("point");
					float fCash = (float) dataObject.getDouble("cash");
					String strBankName = dataObject.getString("bank_account_name");
					String strBankId = dataObject.getString("bank_account_id");
//					String strBankPassword = dataObject.getString("bank_account_pwd");
					String strAlipayName = dataObject.getString("alipay_account_name");
					String strAlipayId = dataObject.getString("alipay_account_id");
					String strWeixinName = dataObject.getString("weixin_account_name");
					String strWeixinId = dataObject.getString("weixin_account_id");
					String strStatus = dataObject.getString("status");

					if (strStatus.equals("DISABLED")) {
						Prefs.Instance().setTaskStatus(0);
					} else if (strStatus.equals("READY")) {
						Prefs.Instance().setTaskStatus(1);
					} else if (strStatus.equals("WORKING")) {
						Prefs.Instance().setTaskStatus(2);
					}
					int taskId = dataObject.getInt("task_id");
					Prefs.Instance().setTaskId(String.valueOf(taskId));

					Prefs.Instance().setBankName(strBankName);
					Prefs.Instance().setBankId(strBankId);
//					Prefs.Instance().setBankPassword(strBankPassword);
					Prefs.Instance().setAlipayName(strAlipayName);
					Prefs.Instance().setAlipayId(strAlipayId);
					Prefs.Instance().setWeixinName(strWeixinName);
					Prefs.Instance().setWeixinId(strWeixinId);
					Prefs.Instance().setMerit(iMerit);
					Prefs.Instance().setCash(fCash);
					Prefs.Instance().setPoint(iPoint);
					Prefs.Instance().setBirthday(strBirthday);
					Prefs.Instance().setIDCardBack(strIDBack);
					Prefs.Instance().setAddress(strAddress);
					Prefs.Instance().setCity(strCity);
					Prefs.Instance().setSex(bSex);
					Prefs.Instance().setIDCardFront(strIDFront);
					Prefs.Instance().setUserPhone(strMobile);
					Prefs.Instance().setCertificate(strCertificate);
					Prefs.Instance().setUserPhoto(strPhoto);
					Prefs.Instance().setUserToken(strToken);
					Prefs.Instance().setIDCardNum(strIDNum);
					Prefs.Instance().setCertPic(strCertPic);
					Prefs.Instance().setProvince(strProvince);
					Prefs.Instance().setDistrict(strDistrict);
					Prefs.Instance().setUserName(strUserName);
					Prefs.Instance().setCompany(strCompany);
					Prefs.Instance().setResidence(strResidence);
					Prefs.Instance().setJob(strJob);
					Prefs.Instance().setJobCert(strCertificate);

					Prefs.Instance().commit();

					finish();
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							ActivityUserData.this,
							getResources().getString(R.string.str_api_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(
						ActivityUserData.this,
						getResources().getString(R.string.str_api_failed)
				);
			}
		}, TAG);
	}

	private Bitmap getRotatedBitmap(Bitmap bitmap, Uri photoPath) {
		ExifInterface ei;
		Bitmap rotatedBitmap = bitmap;
		try {
			InputStream input = getContentResolver().openInputStream(photoPath);
			if (Build.VERSION.SDK_INT > 23)
				ei = new ExifInterface(input);
			else {
				ei = new ExifInterface(photoPath.getPath());
			}
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotatedBitmap = rotateImage(bitmap, 90);
					break;

				case ExifInterface.ORIENTATION_ROTATE_180:
					rotatedBitmap = rotateImage(bitmap, 180);
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					rotatedBitmap = rotateImage(bitmap, 270);
					break;

				case ExifInterface.ORIENTATION_NORMAL:
				default:
					rotatedBitmap = bitmap;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rotatedBitmap;
	}

	public Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
				matrix, true);
	}
}
