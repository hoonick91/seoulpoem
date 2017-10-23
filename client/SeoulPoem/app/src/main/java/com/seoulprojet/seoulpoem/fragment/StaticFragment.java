package com.seoulprojet.seoulpoem.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.activity.MainActivity;
import com.seoulprojet.seoulpoem.activity.TagActivity;

/**
 * Example about replacing fragments inside a ViewPager. I'm using
 * android-support-v7 to maximize the compatibility.
 * 
 * @author Dani Lao (@dani_lao)
 * 
 */
public class StaticFragment extends Fragment {

	//유저 정보
	private String userEmail = null;
	private int loginType = 0;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater
				.inflate(R.layout.static_fragment, container, false);


		//유저 정보 가져오기
//		Bundle extra = getArguments();
//		userEmail = extra.getString("userEmail");
//		loginType = extra.getInt("loginType");


		//클릭하면 메인으로
		Button btn = (Button) view.findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("test" ,"aa");
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("userEmail", "kjh338076@naver.com");
				intent.putExtra("loginType", 1);
				startActivity(intent);
			}
		});
		return view;
	}

}
