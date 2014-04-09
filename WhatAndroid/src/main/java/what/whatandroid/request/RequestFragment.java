package what.whatandroid.request;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.cli.Utils;
import api.requests.Request;
import api.requests.Response;
import api.soup.MySoup;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.callbacks.ViewTorrentCallbacks;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;
import what.whatandroid.views.ImageDialog;

import java.util.Date;

/**
 * Display the details of a request, bounty, artists etc.
 */
public class RequestFragment extends Fragment implements OnLoggedInCallback, View.OnClickListener,
	LoaderManager.LoaderCallbacks<Request> {
	/**
	 * The request being viewed
	 */
	private Request request;
	/**
	 * Request id passed through when the fragment is created so we can defer loading
	 */
	private SetTitleCallback setTitle;
	private ViewUserCallbacks viewUser;
	private ViewTorrentCallbacks viewTorrent;
	/**
	 * Various views displaying the information about the request along with associated headers/text
	 * so that we can hide any unused views
	 */
	private ImageView image;
	private ProgressBar spinner;
	private TextView title, created, recordLabel, catalogueNumber, releaseType, filled, filledBy,
		acceptBitrates, acceptFormats, acceptMedia, votes, bounty, tags;
	private View recordLabelText, catalogueNumberText, releaseTypeText, filledText, filledByText,
		bitratesContainer, formatsContainer, mediaContainer, addVote;
	/**
	 * The list shows the artists & top contributors
	 */
	private ExpandableListView list;

	/**
	 * Use this factory method to create a request fragment displaying the request
	 *
	 * @param id request to load
	 * @return fragment displaying the request
	 */
	public static RequestFragment newInstance(int id){
		RequestFragment fragment = new RequestFragment();
		Bundle args = new Bundle();
		args.putInt(RequestActivity.REQUEST_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	public RequestFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			setTitle = (SetTitleCallback)activity;
			viewTorrent = (ViewTorrentCallbacks)activity;
			viewUser = (ViewUserCallbacks)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString()
				+ " must implement SetTitle, ViewUser and ViewTorrent callbacks");
		}
	}

	@Override
	public void onLoggedIn(){
		if (isAdded()){
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	/**
	 * Refresh the request, this is used to update the view after voting
	 */
	public void refresh(){
		getLoaderManager().restartLoader(0, getArguments(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.expandable_list_view, container, false);
		list = (ExpandableListView)view.findViewById(R.id.exp_list);
		View header = inflater.inflate(R.layout.header_request_info, null);
		list.addHeaderView(header, null, false);

		image = (ImageView)header.findViewById(R.id.image);
		spinner = (ProgressBar)header.findViewById(R.id.loading_indicator);
		title = (TextView)header.findViewById(R.id.title);
		created = (TextView)header.findViewById(R.id.created);
		recordLabelText = header.findViewById(R.id.record_label_text);
		recordLabel = (TextView)header.findViewById(R.id.record_label);
		catalogueNumberText = header.findViewById(R.id.catalogue_number_text);
		catalogueNumber = (TextView)header.findViewById(R.id.catalogue_number);
		releaseType = (TextView)header.findViewById(R.id.release_type);
		releaseTypeText = header.findViewById(R.id.release_type_text);
		filled = (TextView)header.findViewById(R.id.filled_torrent);
		filledText = header.findViewById(R.id.filled_torrent_text);
		filledBy = (TextView)header.findViewById(R.id.filled_user);
		filledByText = header.findViewById(R.id.filled_user_text);
		acceptBitrates = (TextView)header.findViewById(R.id.accept_bitrates);
		bitratesContainer = header.findViewById(R.id.accept_bitrates_container);
		acceptFormats = (TextView)header.findViewById(R.id.accept_formats);
		formatsContainer = header.findViewById(R.id.accept_formats_container);
		acceptMedia = (TextView)header.findViewById(R.id.accept_media);
		mediaContainer = header.findViewById(R.id.accept_media_container);
		votes = (TextView)header.findViewById(R.id.votes);
		bounty = (TextView)header.findViewById(R.id.bounty);
		tags = (TextView)header.findViewById(R.id.tags);
		addVote = header.findViewById(R.id.add_vote);
		addVote.setOnClickListener(this);
		image.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.add_vote){
			VoteDialog dialog = VoteDialog.newInstance(request);
			dialog.show(getChildFragmentManager(), "vote_dialog");
		}
		else if (v.getId() == R.id.image){
			ImageDialog dialog = ImageDialog.newInstance(request.getResponse().getImage());
			dialog.show(getChildFragmentManager(), "image_dialog");
		}
	}

	/**
	 * Update the request information being shown
	 */
	private void updateRequest(){
		Response response = request.getResponse();
		setTitle.setTitle(response.getTitle());
		title.setText(response.getTitle());
		votes.setText(response.getVoteCount().toString());
		bounty.setText(Utils.toHumanReadableSize(response.getTotalBounty().longValue()));
		Date createDate = MySoup.parseDate(response.getTimeAdded());
		created.setText(DateUtils.getRelativeTimeSpanString(createDate.getTime(),
			new Date().getTime(), DateUtils.WEEK_IN_MILLIS));

		RequestAdapter adapter = new RequestAdapter(getActivity(), response.getMusicInfo(), response.getTopContributors());
		list.setAdapter(adapter);
		list.setOnChildClickListener(adapter);

		//Requests may be missing any of these fields
		String imgUrl = response.getImage();
		if (SettingsActivity.imagesEnabled(getActivity()) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, image, new ImageLoadingListener(spinner));
		}
		else {
			image.setVisibility(View.GONE);
			spinner.setVisibility(View.GONE);
		}
		if (response.isFilled()){
			addVote.setVisibility(View.GONE);
			filled.setText("Yes");
			filled.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v){
					viewTorrent.viewTorrentGroup(request.getResponse().getTorrentId().intValue());
				}
			});
			filledBy.setText(response.getFillerName());
			filledBy.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v){
					viewUser.viewUser(request.getResponse().getFillerId().intValue());
				}
			});
		}
		else {
			filledText.setVisibility(View.GONE);
			filled.setVisibility(View.GONE);
			filledByText.setVisibility(View.GONE);
			filledBy.setVisibility(View.GONE);
		}
		if (response.getRecordLabel() != null && !response.getRecordLabel().isEmpty()){
			recordLabel.setText(response.getRecordLabel());
		}
		else {
			recordLabelText.setVisibility(View.GONE);
			recordLabel.setVisibility(View.GONE);
		}
		if (response.getCatalogueNumber() != null && !response.getCatalogueNumber().isEmpty()){
			catalogueNumber.setText(response.getCatalogueNumber());
		}
		else {
			catalogueNumberText.setVisibility(View.GONE);
			catalogueNumber.setVisibility(View.GONE);
		}
		if (response.getReleaseName() != null && !response.getReleaseName().isEmpty()){
			releaseType.setText(response.getReleaseName());
		}
		else {
			releaseTypeText.setVisibility(View.GONE);
			releaseType.setVisibility(View.GONE);
		}
		if (!response.getBitrateList().isEmpty()){
			String bitrates = response.getBitrateList().toString();
			bitrates = bitrates.substring(bitrates.indexOf('[') + 1, bitrates.lastIndexOf(']'));
			acceptBitrates.setText(bitrates);
		}
		else {
			bitratesContainer.setVisibility(View.GONE);
		}
		if (!response.getFormatList().isEmpty()){
			String formats = response.getFormatList().toString();
			formats = formats.substring(formats.indexOf('[') + 1, formats.lastIndexOf(']'));
			acceptFormats.setText(formats);
		}
		else {
			formatsContainer.setVisibility(View.GONE);
		}
		if (!response.getMediaList().isEmpty()){
			String media = response.getMediaList().toString();
			media = media.substring(media.indexOf('[') + 1, media.lastIndexOf(']'));
			acceptMedia.setText(media);
		}
		else {
			mediaContainer.setVisibility(View.GONE);
		}
		String tagString = response.getTags().toString();
		tagString = tagString.substring(tagString.indexOf('[') + 1, tagString.lastIndexOf(']'));
		tags.setText(tagString);
	}

	@Override
	public Loader<Request> onCreateLoader(int id, Bundle args){
		if (isAdded()){
			getActivity().setProgressBarIndeterminate(true);
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		return new RequestAsyncLoader(getActivity(), args);
	}

	@Override
	public void onLoadFinished(Loader<Request> loader, Request data){
		request = data;
		if (isAdded()){
			getActivity().setProgressBarIndeterminateVisibility(false);
			getActivity().setProgressBarIndeterminate(false);
			if (request != null && request.getStatus()){
				updateRequest();
			}
			else {
				Toast.makeText(getActivity(), "Could not load request", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Request> loader){
	}
}
