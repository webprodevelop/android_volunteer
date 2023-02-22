//@formatter:off
package com.iot.volunteer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iot.volunteer.App;
import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityMain;
import com.iot.volunteer.database.IOTDBHelper;
import com.iot.volunteer.model.ItemNotification;
import com.iot.volunteer.model.ItemWatchInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Util {
	public static ArrayList<ItemNotification> notificationList = new ArrayList<>();
	public static String storeAppVersion;
	public static String storeAppURL;

	public static void ShowDialogSuccess(Context a_context, String a_sSuccess) {
		AlertDialog.Builder		builder = new AlertDialog.Builder(a_context);
		builder.setMessage(a_sSuccess);
		builder.setPositiveButton(
			a_context.getResources().getString(R.string.str_ok),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}
		);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void ShowDialogError(Context a_context, String a_sError) {
		AlertDialog.Builder		builder = new AlertDialog.Builder(a_context);
		builder.setMessage(a_sError);
		builder.setPositiveButton(
			a_context.getResources().getString(R.string.str_ok),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}
		);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void ShowDialogError(int message) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		String str_message = context.getResources().getString(message);
		ShowDialogError(str_message);
	}

	public static void ShowDialogError(int message, final ResultProcess process) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		String str_message = context.getResources().getString(message);
		AlertDialog.Builder		builder = new AlertDialog.Builder(context);
		builder.setMessage(str_message);
		builder.setPositiveButton(context.getResources().getString(R.string.str_ok), (dialog, which) -> {
					dialog.dismiss();

				}
		);
		AlertDialog dialog = builder.create();
		dialog.setOnDismissListener(dialog1 -> process.process());
		dialog.show();
	}

	public static void ShowDialogError(String str_message) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		AlertDialog.Builder		builder = new AlertDialog.Builder(context);
		builder.setMessage(str_message);
		builder.setPositiveButton(context.getResources().getString(R.string.str_ok), (dialog, which) -> dialog.dismiss());
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void ShowDialogError(String a_sError, final ResultProcess process) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		AlertDialog.Builder		builder = new AlertDialog.Builder(context);
		builder.setMessage(a_sError);
		builder.setPositiveButton(context.getResources().getString(R.string.str_ok), (dialog, which) -> {
					dialog.dismiss();

				}
		);
		AlertDialog dialog = builder.create();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface dialog) {
				process.process();
			}
		});
		dialog.show();
	}

	public static void ShowDialogErrorAndExit(final Context a_context, String a_sError) {
		AlertDialog.Builder		builder = new AlertDialog.Builder(a_context);
		builder.setMessage(a_sError);
		builder.setPositiveButton(
				a_context.getResources().getString(R.string.str_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						((Activity)a_context).setResult(Activity.RESULT_CANCELED);
						((Activity)a_context).finish();
					}
				}
		);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void ShowDialogError(final Context a_context, String a_sError, final ResultProcess process) {
		AlertDialog.Builder		builder = new AlertDialog.Builder(a_context);
		builder.setMessage(a_sError);
		builder.setPositiveButton(
				a_context.getResources().getString(R.string.str_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}
		);
		AlertDialog dialog = builder.create();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface dialog) {
				process.process();
			}
		});
		dialog.show();
	}

	public static abstract class ResultProcess {
		abstract public void process();
	}

	public static void showToastMessage(Context context, int resId) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.custom_toast, null);

		TextView text = layout.findViewById(R.id.text);
		text.setText(resId);

		Toast toast = Toast.makeText(context,resId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.show();
	}

	public static void showToastMessage(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.custom_toast, null);

		TextView text = layout.findViewById(R.id.text);
		text.setText(msg);

		Toast toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.show();
	}

	public static long parseDateTimeSecFormatString(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date date = sdf.parse(strDate);
			return date.getTime();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	public static long parseDateFormatString(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date date = sdf.parse(strDate);
			return date.getTime();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	public static String getDateTimeSecFormatString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formateDate = sdf.format(date);

		return formateDate;
	}

	public static String getDateTimeFormatString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String formateDate = sdf.format(date);

		return formateDate;
	}

	public static String getTimeFormatStringIgnoreLocale(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String formateDate = sdf.format(date);

		return formateDate;
	}

	public static String getDateFormatStringIgnoreLocale(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formateDate = sdf.format(date);

		return formateDate;
	}

	public static String getDateFormatString(Context context, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.str_date_format));
		String formateDate = sdf.format(date);

		return formateDate;
	}

	public static String convertDateString(Context context, String strDate, boolean formatted) {
		String strConvert = "";
		SimpleDateFormat sdf1 = new SimpleDateFormat(context.getString(R.string.str_date_format));
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (formatted) {
				Date date = sdf1.parse(strDate);
				strConvert = sdf2.format(date);
			} else {
				Date date = sdf2.parse(strDate);
				strConvert = sdf1.format(date);
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return strConvert;
	}

	public static String extractTextFromHtmlContent(String strContent) {
		int sIndex = strContent.indexOf("<img");
		while (sIndex != -1) {
			int eIndex = strContent.indexOf("/>", sIndex);
			strContent = strContent.replace(strContent.substring(sIndex, eIndex + 2), "");
			sIndex = strContent.indexOf("<img");
		}

		return strContent;
	}

	public static void showBottomNumberPicker(Activity activity, int minValue, int maxValue, int value, NumberPicker.OnValueChangeListener valueChangeListener) {
		final BottomSheetDialog dialog = new BottomSheetDialog(activity);
		View parentView = activity.getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
		dialog.setContentView(parentView);
		((View)parentView.getParent()).setBackgroundColor(Color.TRANSPARENT);

		NumberPicker numberPicker = parentView.findViewById(R.id.ID_NUMPICK);
		numberPicker.setMaxValue(maxValue);
		numberPicker.setMinValue(minValue);
		numberPicker.setValue(value);
		numberPicker.setWrapSelectorWheel(false);
		numberPicker.setOnValueChangedListener(valueChangeListener);

		dialog.show();
	}

	public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		byte[] byteFormat = stream.toByteArray();
		// get the base 64 string
		String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

		return imgString;
	}

	public static int getResId(String resName, Class<?> c) {

		try {
			Field idField = c.getDeclaredField(resName);
			return idField.getInt(idField);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static Bitmap resizeBitmap(Bitmap bitmap,int newWidth,int newHeight) {
		//final int REQ_SIZE = 300;

		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

		float ratioX = newWidth / (float) bitmap.getWidth();
		float ratioY = newHeight / (float) bitmap.getHeight();
		float middleX = newWidth / 2.0f;
		float middleY = newHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}

	public static Bitmap getViewBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		view.measure(
			View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
			View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
		);
		view.layout(
			0,
			0,
			view.getMeasuredWidth(),
			view.getMeasuredHeight()
		);

		view.buildDrawingCache();
		Bitmap bmpCache = view.getDrawingCache();
		Bitmap bmp = Bitmap.createBitmap(bmpCache);

		return bmp;
	}


	public static void clearNotificationTable() {
		notificationList.clear();
	}

	public static void addNotification(ItemNotification entry) {
			for (int i = 0; i < notificationList.size(); i++) {
				ItemNotification itemNotification = notificationList.get(i);
//				if (itemNotification.taskId == entry.taskId) {
//					itemNotification.tas = 1;
//					break;
//				}
			}
		notificationList.add(entry);
	}

	public static ArrayList<ItemNotification> getAllNotifications(Context context) {
		ArrayList<ItemNotification> tempResult = new ArrayList<>(notificationList);

		IOTDBHelper iotdbHelper = new IOTDBHelper(context);
		tempResult.addAll(iotdbHelper.getAllNotificationEntries());

		ArrayList<ItemNotification> alarmList = new ArrayList<>();
		ArrayList<ItemNotification> noticeList = new ArrayList<>();
		for (int i = 0; i < tempResult.size(); i++) {
			if (tempResult.get(i).type.equals("task")) {
				alarmList.add(tempResult.get(i));
			}
			else {
				noticeList.add((tempResult.get(i)));
			}
		}

		Collections.sort(alarmList, (o1, o2) -> Long.compare(o2.time, o1.time));
		Collections.sort(noticeList, (o1, o2) -> Long.compare(o2.time, o1.time));

		alarmList.addAll(noticeList);

		return alarmList;
	}

	public static void deleteNotificationEntry(int id) {
		for (int i = 0; i < notificationList.size(); i++) {
			ItemNotification entry = notificationList.get(i);
			if (entry.id == id) {
				notificationList.remove(entry);
				return;
			}
		}
	}

	public static ItemNotification findNotificationEntry(int id) {
		for (int i = 0; i < notificationList.size(); i++) {
			ItemNotification entry = notificationList.get(i);
			if (entry.id == id)
				return entry;
		}
		return null;
	}

	public static void updateNotificationEntry(ItemNotification oldEntry, ItemNotification entry)  {
		for (int i = 0; i < notificationList.size(); i++) {
			ItemNotification itemNotification = notificationList.get(i);
			if (itemNotification.id == oldEntry.id) {
				notificationList.remove(i);
				notificationList.add(i, entry);
				return;
			}
		}
	}


}
