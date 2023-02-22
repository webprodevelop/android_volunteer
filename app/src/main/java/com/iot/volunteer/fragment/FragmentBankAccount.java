//@formatter:off
package com.iot.volunteer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;

public class FragmentBankAccount extends FragmentBase {
	private ImageView						ivBack;
	private TextView						tvTitle;
	private TextView						tvBank;
	private TextView						tvBankAccount;
	private TextView						tvAdd;
	private TextView						tvChange;
	private TextView						tvForgotPassword;

	static FragmentBankAccount fragment = null;

	public static FragmentBankAccount getInstance() {
		if (fragment == null) {
			fragment = new FragmentBankAccount();
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_bank_account, container, false);

		initControls(rootView);
		setEventListener();
		loadData();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void initControls(View layout) {
		super.initControls(layout);

		ivBack = layout.findViewById(R.id.ivBack);
		tvTitle = layout.findViewById(R.id.tvTitle);
		tvBank = layout.findViewById(R.id.tvBank);
		tvBankAccount = layout.findViewById(R.id.tvBankAccount);
		tvAdd = layout.findViewById(R.id.tvAdd);
		tvChange = layout.findViewById(R.id.tvChange);
		tvForgotPassword = layout.findViewById(R.id.tvForgotPassword);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.popChildFragment();
			}
		});
		tvAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.pushChildFragment(FragmentBankAccountAdd.getInstance(), FragmentBankAccountAdd.class.getSimpleName());
				FragmentBankAccountAdd.getInstance().setActivity(activityMain);
			}
		});
		tvChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentChangePassword fragmentChangePassword = new FragmentChangePassword();
				activityMain.pushChildFragment(fragmentChangePassword, FragmentChangePassword.class.getSimpleName());
				fragmentChangePassword.setActivity(activityMain);
				fragmentChangePassword.setIsBank(true);
			}
		});
		tvForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activityMain.pushChildFragment(FragmentBankAccountForgot.getInstance(), FragmentBankAccountForgot.class.getSimpleName());
				FragmentBankAccountForgot.getInstance().setActivity(activityMain);
			}
		});
	}

	private void loadData() {
		String bankName = Prefs.Instance().getBankName();
		if (bankName != null && !bankName.isEmpty()) {
			tvBank.setText(bankName);
		}
		String bankId = Prefs.Instance().getBankId();
		if (bankId != null && bankId.length() > 8) {
			bankId = bankId.substring(0, 4) + "**********" + bankId.substring(bankId.length() - 4);
			tvBankAccount.setText(bankId);
		}
	}
}