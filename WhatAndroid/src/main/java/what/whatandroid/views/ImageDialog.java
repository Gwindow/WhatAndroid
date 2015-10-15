package what.whatandroid.views;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import what.whatandroid.R;
import what.whatandroid.WhatApplication;

/**
 * Basic dialog to display a full image, similar to how the site does when you
 * click on some album art
 */
public class ImageDialog extends DialogFragment implements View.OnClickListener {
	public static final String IMAGE_URL = "what.whatandroid.IMAGE_URL";
	private String imageUrl;

	/**
	 * Use this factory method to create an image dialog displaying the image at some url
	 * In typical usage of the app this image will already be cached by UIL so we won't
	 * actually need to do any downloading
	 */
	public static ImageDialog newInstance(String imgUrl) {
		ImageDialog dialog = new ImageDialog();
		Bundle args = new Bundle();
		args.putString(IMAGE_URL, imgUrl);
		dialog.setArguments(args);
		return dialog;
	}

	public ImageDialog() {
		//Required empty ctor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageUrl = getArguments().getString(IMAGE_URL);
//		setStyle(STYLE_NORMAL, R.style.MY_DIALOG);
		setStyle(STYLE_NO_FRAME, R.style.ImageDialog);
//		setStyle(STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog d = getDialog();
		if (d != null) {
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			d.getWindow().setLayout(width, height);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_image, null);
		TouchImageView imageView = (TouchImageView) view.findViewById(R.id.image);
		final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.loading_indicator);

		WhatApplication.loadImage(getContext(), imageUrl, imageView, spinner, null, null);
		imageView.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		getDialog().dismiss();
	}
}