package what.whatandroid.testing;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Testing upper level fragment
 */
public class MasterContentFragment extends Fragment implements View.OnClickListener {
	private int position;
	private String content;
	private UpperTestingFragment.ViewDetail viewDetail;

	public static MasterContentFragment newInstance(String content, int position){
		MasterContentFragment f = new MasterContentFragment();
		f.content = content;
		return f;
	}

	public MasterContentFragment(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		TextView text = new TextView(getActivity());
		text.setGravity(Gravity.CENTER);
		text.setText(content);

		Button button = new Button(getActivity());
		button.setText("Clicky!");
		button.setOnClickListener(this);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT));
		layout.setGravity(Gravity.CENTER);
		layout.addView(text);
		layout.addView(button);
		return layout;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			viewDetail = (UpperTestingFragment.ViewDetail)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement ViewDetail");
		}
	}

	@Override
	public void onClick(View v){
		viewDetail.viewDetail(position);
	}
}
