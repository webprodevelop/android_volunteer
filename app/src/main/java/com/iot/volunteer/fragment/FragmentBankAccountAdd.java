//@formatter:off
package com.iot.volunteer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.http.HttpAPI;
import com.iot.volunteer.http.HttpAPIConst;
import com.iot.volunteer.http.VolleyCallback;
import com.iot.volunteer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FragmentBankAccountAdd extends FragmentBase {
	public final static int            REQUEST_CODE_IMPORT_PHOTO		= 100;

	private ImageView						ivBack;
	private TextView						tvTitle;
	private ImageView						ivPhoto;
	private TextView						tvTakePhoto;
	private TextView						tvManualAdd;

	private Bitmap							bankBitmap = null;
	private String							strBankName = "";
	private String							strBankCard = "";

	static FragmentBankAccountAdd fragment = null;

	public static FragmentBankAccountAdd getInstance() {
		if (fragment == null) {
			fragment = new FragmentBankAccountAdd();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_bank_account_delete, container, false);

		initControls(rootView);
		setEventListener();

		return rootView;
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		ivPhoto = layout.findViewById(R.id.ivPhoto);
		tvTakePhoto = layout.findViewById(R.id.tvTakePhoto);
		tvManualAdd = layout.findViewById(R.id.tvManualAdd);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
		ivPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		tvTakePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onImportPhoto();
			}
		});
		tvManualAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBankAccountSetting(true);
			}
		});
	}

	public void onBankAccountSetting(boolean manual) {
		FragmentBankAccountSetting fragmentBankAccountSetting = new FragmentBankAccountSetting();
		activityMain.pushChildFragment(fragmentBankAccountSetting, FragmentBankAccountSetting.class.getSimpleName());
		fragmentBankAccountSetting.setActivity(activityMain);
		fragmentBankAccountSetting.setBankSettings(manual, strBankName, strBankCard);
	}

	private void onImportPhoto() {
		final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
		View parentView = getLayoutInflater().inflate(R.layout.dialog_select_picture_source, null);
		dialog.setContentView(parentView);
		((View)parentView.getParent()).setBackgroundColor(Color.TRANSPARENT);

		TextView tvPhoto = parentView.findViewById(R.id.ID_TXTVIEW_TAKE_PHOTO);
		TextView tvGallery = parentView.findViewById(R.id.ID_TXTVIEW_FROM_GALLERY);
		TextView tvCancel = parentView.findViewById(R.id.ID_TXTVIEW_CANCEL);

		tvPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				getActivity().startActivityForResult(intent, REQUEST_CODE_IMPORT_PHOTO);
			}
		});
		tvGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				getActivity().startActivityForResult(Intent.createChooser(intent, ""), REQUEST_CODE_IMPORT_PHOTO);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_IMPORT_PHOTO) {
				Uri pathUri = data.getData();
				Bitmap bitmap = null;
				if (pathUri == null) {
					bitmap = (Bitmap) data.getExtras().get("data");
				} else {
					try {
						bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pathUri);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (bitmap != null) {
					final int REQ_SIZE = 300;
					int rW, rH;
					if (bitmap.getWidth() < bitmap.getHeight()) {
						rW = REQ_SIZE;
						rH = (int) (REQ_SIZE * (bitmap.getHeight() / (float)bitmap.getWidth()));
					} else {
						rH = REQ_SIZE;
						rW = (int) (REQ_SIZE * (bitmap.getWidth() / (float)bitmap.getHeight()));
					}

					if (bankBitmap != null) {
						bankBitmap.recycle();
						bankBitmap = null;
					}
					bankBitmap = Util.resizeBitmap(bitmap, rW, rH);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);

					getBankCardInfo();

					bitmap.recycle();
					bitmap = null;
				}
			}
		}
	}

	private void getBankCardInfo() {
		m_dlgProgress.show();
		String strFrontPhoto = bankBitmap != null ? Util.getEncoded64ImageStringFromBitmap(bankBitmap) : "";

		HttpAPI.getIdCardFrontInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strFrontPhoto, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(getActivity(), sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					strBankName = dataObject.getString("bank_name");
					strBankCard = dataObject.getString("bank_card");

					onBankAccountSetting(false);
				}
				catch (JSONException e) {
					Util.ShowDialogError(
							getActivity(),
							getResources().getString(R.string.str_api_failed)
					);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();

				Util.ShowDialogError(
						getActivity(),
						getResources().getString(R.string.str_api_failed)
				);
			}
		}, TAG);
	}
}