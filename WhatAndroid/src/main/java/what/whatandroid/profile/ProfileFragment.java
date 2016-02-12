package what.whatandroid.profile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Date;

import api.cli.Utils;
import api.index.Index;
import api.soup.MySoup;
import api.user.Profile;
import api.user.User;
import api.user.UserProfile;
import api.user.recent.UserRecents;
import what.whatandroid.R;
import what.whatandroid.WhatApplication;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.forums.thread.ReplyDialogFragment;
import what.whatandroid.settings.SettingsActivity;

/**
 * Fragment to display a user's profile
 */
public class ProfileFragment extends Fragment implements OnLoggedInCallback,
        LoaderManager.LoaderCallbacks<UserProfile>, ReplyDialogFragment.ReplyDialogListener {

    public static final String DEFER_LOADING = "what.whatandroid.DEFER_LOADING";
    /**
     * The user's profile information
     */
    private UserProfile userProfile;
    /**
     * The user id we want to view, passed earlier as a param since we defer loading until onCreate
     */
    private int userID;
    private boolean deferLoad;
    /**
     * Callbacks to the activity so we can go set the title
     */
    private SetTitleCallback setTitle;
    private LoadingListener<Index> indexLoadingListener;
    /**
     * Various content views displaying the user's information
     */
    private ImageView avatar;
    private ProgressBar spinner;
    private View artContainer;
    /**
     * The user's stats being shown
     */
    private TextView username, userClass, joined, invited, ratio, paranoia;
    /**
     * Text views saying what the various numbers in the profile mean, so we can hide those that are hidden
     * by the user's paranoia
     */
    private TextView invitedText, ratioText, paranoiaText;
    /**
     * View pagers & adapters for displaying the lists of recent snatches and uploads & headers for the views
     * headers are needed so we can hide the views if hidden by paranoia
     */
    private RecentTorrentPagerAdapter snatchesAdapter, uploadsAdapter;
    private View snatchesContainer, uploadsContainer, donor, warned, banned;
    /**
     * Draft of a message we're writing to the user
     */
    private String messageDraft = "", messageSubject = "";
    /**
     * Send message menu item, so we can hide it if we're viewing our own profile
     */
    private MenuItem sendMessage;
    /**
     * Text views to display user ranks
     */
    private TextView dataUploadedText, dataUploaded, dataDownloadedText, dataDownloaded, torrentsUploadedText, torrentsUploaded, requestsFilledText, requestsFilled, bountySpentText, bountySpent, postsMadeText, postsMade, artistsAddedText, artistsAdded, overallText, overall;
    /**
     * Text views to display user rank values
     */
    private TextView dataUploadedValue,dataDownloadedValue, torrentsUploadedValue, requestsFilledValue, postsMadeValue;

    /**
     * Button to show/collapse the sections
     */
    private ImageButton toggleRanks;
    private ImageButton toggleStats;
    private ImageButton toggleSnatches;
    private ImageButton toggleUploads;

    /**
     * Section view layouts
     */
    private RelativeLayout ranksView;
    private RelativeLayout statsView;
    private ViewPager snatchesView;
    private ViewPager uploadsView;


    /**
     * Use this factory method to create a new instance of the fragment displaying the
     * desired user's profile
     *
     * @param id        The user id to display the profile of
     * @param deferLoad True if the fragment should wait to load the profile until the user id is updated
     */
    public static ProfileFragment newInstance(int id, boolean deferLoad) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ProfileActivity.USER_ID, id);
        args.putBoolean(ProfileFragment.DEFER_LOADING, deferLoad);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    public void setUserID(int id) {
        if (deferLoad) {
            userID = id;
            getArguments().putInt(ProfileActivity.USER_ID, userID);
            //We now have the right id so we don't need to defer loading anymore
            getArguments().putBoolean(ProfileFragment.DEFER_LOADING, false);
            Bundle args = new Bundle();
            args.putInt(ProfileActivity.USER_ID, userID);
            getLoaderManager().initLoader(0, args, this);
        }
    }

    /**
     * Get the user id the fragment is currently viewing
     *
     * @return viewed user's id
     */
    public int getUserID() {
        return userID;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            setTitle = (SetTitleCallback) activity;
            indexLoadingListener = (LoadingListener<Index>) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ViewTorrent & SetTitle Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getArguments().getInt(ProfileActivity.USER_ID);
        deferLoad = getArguments().getBoolean(ProfileFragment.DEFER_LOADING);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            messageDraft = savedInstanceState.getString(ReplyDialogFragment.DRAFT);
            messageSubject = savedInstanceState.getString(ReplyDialogFragment.SUBJECT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        avatar = (ImageView) view.findViewById(R.id.avatar);
        spinner = (ProgressBar) view.findViewById(R.id.loading_indicator);
        artContainer = view.findViewById(R.id.art_container);
        username = (TextView) view.findViewById(R.id.username);
        userClass = (TextView) view.findViewById(R.id.user_class);
        joined = (TextView) view.findViewById(R.id.joined);
        invited = (TextView) view.findViewById(R.id.invited);
        invitedText = (TextView) view.findViewById(R.id.invited_text);
        ratio = (TextView) view.findViewById(R.id.ratio);
        ratioText = (TextView) view.findViewById(R.id.ratio_text);
        paranoia = (TextView) view.findViewById(R.id.paranoia);
        paranoiaText = (TextView) view.findViewById(R.id.paranoia_text);
        //Hide the paranoia text until we figure out what the user's paranoia settings are
        paranoiaText.setVisibility(View.GONE);
        ViewPager recentSnatches = (ViewPager) view.findViewById(R.id.recent_snatches);
        snatchesContainer = view.findViewById(R.id.snatches_container);
        ViewPager recentUploads = (ViewPager) view.findViewById(R.id.recent_uploads);
        uploadsContainer = view.findViewById(R.id.uploads_container);
        donor = view.findViewById(R.id.donor);
        warned = view.findViewById(R.id.warned);
        banned = view.findViewById(R.id.banned);

        donor.setVisibility(View.GONE);
        warned.setVisibility(View.GONE);
        banned.setVisibility(View.GONE);

        dataDownloadedText = (TextView) view.findViewById(R.id.data_downloaded_text);
        dataDownloaded = (TextView) view.findViewById(R.id.data_downloaded);
        dataUploadedText = (TextView) view.findViewById(R.id.data_uploaded_text);
        dataUploaded = (TextView) view.findViewById(R.id.data_uploaded);
        torrentsUploadedText = (TextView) view.findViewById(R.id.torrents_uploaded_text);
        torrentsUploaded = (TextView) view.findViewById(R.id.torrents_uploaded);
        requestsFilledText = (TextView) view.findViewById(R.id.requests_filled_text);
        requestsFilled = (TextView) view.findViewById(R.id.requests_filled);
        bountySpentText = (TextView) view.findViewById(R.id.bounty_spent_text);
        bountySpent = (TextView) view.findViewById(R.id.bounty_spent);
        postsMadeText = (TextView) view.findViewById(R.id.posts_made_text);
        postsMade = (TextView) view.findViewById(R.id.posts_made);
        artistsAddedText = (TextView) view.findViewById(R.id.artists_added_text);
        artistsAdded = (TextView) view.findViewById(R.id.artists_added);
        overallText = (TextView) view.findViewById(R.id.overall_text);
        overall = (TextView) view.findViewById(R.id.overall);

        dataDownloadedValue = (TextView) view.findViewById(R.id.data_downloaded_value);
        dataUploadedValue = (TextView) view.findViewById(R.id.data_uploaded_value);
        postsMadeValue = (TextView) view.findViewById(R.id.posts_made_value);
        requestsFilledValue = (TextView) view.findViewById(R.id.requests_filled_value);
        torrentsUploadedValue = (TextView) view.findViewById(R.id.torrents_uploaded_value);

        ranksView = (RelativeLayout) view.findViewById(R.id.user_ranks);
        statsView = (RelativeLayout) view.findViewById(R.id.user_stats);
        snatchesView = (ViewPager) view.findViewById(R.id.recent_snatches);
        uploadsView = (ViewPager) view.findViewById(R.id.recent_uploads);

        toggleStats = (ImageButton) view.findViewById(R.id.toggle_stats);
        toggleStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statsView.isShown()) {
                    statsView.setVisibility(View.GONE);
                    toggleStats.setImageResource(R.drawable.ic_action_add);
                } else if (!statsView.isShown()) {
                    statsView.setVisibility(View.VISIBLE);
                    toggleStats.setImageResource(R.drawable.ic_action_remove);
                }
            }
        });

        toggleRanks = (ImageButton) view.findViewById(R.id.toggle_ranks);
        toggleRanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ranksView.isShown()) {
                    ranksView.setVisibility(View.GONE);
                    toggleRanks.setImageResource(R.drawable.ic_action_add);
                } else if (!ranksView.isShown()) {
                    ranksView.setVisibility(View.VISIBLE);

                    toggleRanks.setImageResource(R.drawable.ic_action_remove);
                }
            }
        });

        toggleSnatches = (ImageButton) view.findViewById(R.id.toggle_snatches);
        toggleSnatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snatchesView.isShown()) {
                    snatchesView.setVisibility(View.GONE);
                    toggleSnatches.setImageResource(R.drawable.ic_action_add);
                } else {
                    snatchesView.setVisibility(View.VISIBLE);
                    toggleSnatches.setImageResource(R.drawable.ic_action_remove);
                }
            }
        });

        toggleUploads = (ImageButton) view.findViewById(R.id.toggle_uploads);
        toggleUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadsView.isShown()) {
                    uploadsView.setVisibility(View.GONE);
                    toggleUploads.setImageResource(R.drawable.ic_action_add);
                } else {
                    uploadsView.setVisibility(View.VISIBLE);
                    toggleUploads.setImageResource(R.drawable.ic_action_remove);
                }
            }
        });

        snatchesAdapter = new RecentTorrentPagerAdapter(getChildFragmentManager());
        uploadsAdapter = new RecentTorrentPagerAdapter(getChildFragmentManager());
        recentSnatches.setAdapter(snatchesAdapter);
        recentUploads.setAdapter(uploadsAdapter);

        if (MySoup.isLoggedIn() && !deferLoad) {
            //We could get -1 user id if we were logged out and trying to view our own profile, so update it
            if (userID == -1) {
                userID = MySoup.getUserId();
                getArguments().putInt(ProfileActivity.USER_ID, userID);
            }
            Bundle args = new Bundle();
            args.putInt(ProfileActivity.USER_ID, userID);
            getLoaderManager().initLoader(0, args, this);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ReplyDialogFragment.DRAFT, messageDraft);
        outState.putString(ReplyDialogFragment.SUBJECT, messageSubject);
    }

    @Override
    public void onLoggedIn() {
        if (isAdded() && !deferLoad) {
            //We could get -1 user id if we were logged out and trying to view our own profile, so update it
            if (userID == -1) {
                userID = MySoup.getUserId();
                getArguments().putInt(ProfileActivity.USER_ID, userID);
            }
            Bundle args = new Bundle();
            args.putInt(ProfileActivity.USER_ID, userID);
            getLoaderManager().initLoader(0, args, this);
        }
    }

    @Override
    public Loader<UserProfile> onCreateLoader(int id, Bundle args) {
        if (isAdded()) {
            getActivity().setProgressBarIndeterminate(true);
            getActivity().setProgressBarIndeterminateVisibility(true);
        }
        return new ProfileAsyncLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<UserProfile> loader, UserProfile data) {
        getActivity().setProgressBarIndeterminate(false);
        getActivity().setProgressBarIndeterminateVisibility(false);
        if (data == null || !data.getStatus()) {
            Toast.makeText(getActivity(), "Could not load profile", Toast.LENGTH_LONG).show();
        } else {
            userProfile = data;
            populateViews();
            if (indexLoadingListener != null) {
                indexLoadingListener.onLoadingComplete(MySoup.getIndex());
            }
            if (userID != MySoup.getUserId() && sendMessage != null) {
                sendMessage.setVisible(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
        sendMessage = menu.findItem(R.id.action_message);
        if (userProfile != null && userID != MySoup.getUserId()) {
            sendMessage.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            Bundle args = new Bundle();
            args.putInt(ProfileActivity.USER_ID, userID);
            getLoaderManager().restartLoader(0, args, this);
        }
        if (item.getItemId() == R.id.action_message) {
            showReplyDialog();
        }
        return false;
    }

    @Override
    public void post(String message, String subject) {
        messageDraft = "";
        messageSubject = "";
        new SendMessageTask().execute(subject, message);
    }

    @Override
    public void saveDraft(String message, String subject) {
        messageDraft = message;
        messageSubject = subject;
    }

    @Override
    public void discard() {
        messageDraft = "";
        messageSubject = "";
    }

    /**
     * Display the compose reply dialog so the user can write their response
     */
    private void showReplyDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ReplyDialogFragment reply = ReplyDialogFragment.newInstance(messageDraft, messageSubject);
        reply.setTargetFragment(this, 0);
        reply.show(ft, "dialog");
    }

    /**
     * Update the profile fields with the information we loaded. We need to do a ton of checks here to
     * properly handle user's various paranoia configurations, which could cause us to get a null for any of the
     * fields that can be hidden. We also hide the recent snatches/uploads if the user's paranoia is high (6+).
     * When viewing our own profile we'll get all the data back but will still see our paranoia value so we need to
     * ignore the paranoia if it's our own profile
     */
    private void populateViews() {
        Profile profile = userProfile.getUser().getProfile();
        setTitle.setTitle(profile.getUsername());
        username.setText(profile.getUsername());
        userClass.setText(profile.getPersonal().getUserClass());
        Date joinDate = MySoup.parseDate(profile.getStats().getJoinedDate());
        joined.setText("Joined " + DateUtils.getRelativeTimeSpanString(joinDate.getTime(),
                new Date().getTime(), DateUtils.WEEK_IN_MILLIS));

        //We need to check all the paranoia cases that may cause a field to be missing and hide the views for it
        String avatarUrl = profile.getAvatar();
        if (!SettingsActivity.imagesEnabled(getActivity())) {
            artContainer.setVisibility(View.GONE);
        } else {
            artContainer.setVisibility(View.VISIBLE);
            WhatApplication.loadImage(getActivity(), avatarUrl, avatar, spinner, null, null);
        }
        if (profile.getPersonal().getParanoia().intValue() > 0 && userID != MySoup.getUserId()) {
            paranoiaText.setVisibility(View.VISIBLE);
            paranoia.setText(profile.getPersonal().getParanoiaText());
        } else {
            paranoia.setVisibility(View.GONE);
        }
        if (profile.getCommunity().getInvited() != null) {
            invited.setText("" + profile.getCommunity().getInvited());
        } else {
            invitedText.setVisibility(View.GONE);
            invited.setVisibility(View.GONE);
        }
        if (profile.getStats().getRatio() != null && profile.getStats().getRequiredRatio() != null) {
            ratio.setText(String.format("%.2f", profile.getStats().getRatio().floatValue())
                    + " / " + String.format("%.2f", profile.getStats().getRequiredRatio().floatValue()));
        } else {
            ratioText.setVisibility(View.GONE);
            ratio.setVisibility(View.GONE);
        }
        //TODO: Keep an eye on this API endpoint and watch for when it starts respecting paranoia and we get null back
        UserRecents recentTorrents = userProfile.getUserRecents();
        if (profile.getPersonal().getParanoia().intValue() < 6 || userID == MySoup.getUserId()) {
            if (recentTorrents.getSnatches().size() > 0) {
                snatchesAdapter.onLoadingComplete(recentTorrents.getSnatches());
                snatchesAdapter.notifyDataSetChanged();
            } else {
                snatchesContainer.setVisibility(View.GONE);
            }
            if (recentTorrents.getUploads().size() > 0) {
                uploadsAdapter.onLoadingComplete(recentTorrents.getUploads());
                uploadsAdapter.notifyDataSetChanged();
            } else {
                uploadsContainer.setVisibility(View.GONE);
            }
        } else {
            snatchesContainer.setVisibility(View.GONE);
            uploadsContainer.setVisibility(View.GONE);
        }

        if (profile.getPersonal().isDonor()) {
            donor.setVisibility(View.VISIBLE);
        }
        if (profile.getPersonal().isWarned()) {
            warned.setVisibility(View.VISIBLE);
        }
        if (!profile.getPersonal().isEnabled()) {
            banned.setVisibility(View.VISIBLE);
        }


        if (profile.getRanks().getDownloaded() != null) {
            dataDownloadedText.setVisibility(View.VISIBLE);
            dataDownloaded.setText("" + profile.getRanks().getDownloaded().intValue() + "%");
            dataDownloadedValue.setVisibility(View.VISIBLE);
            dataDownloadedValue.setText("(" + Utils.toHumanReadableSize(profile.getStats().getDownloaded().longValue()) + ") ");
        } else {
            dataDownloaded.setVisibility(View.GONE);
            dataDownloadedText.setVisibility(View.GONE);
        }
        if (profile.getRanks().getUploaded() != null) {
            dataUploadedText.setVisibility(View.VISIBLE);
            dataUploaded.setText("" + profile.getRanks().getUploaded().intValue() + "%");
            dataUploadedValue.setVisibility(View.VISIBLE);
            dataUploadedValue.setText("(" + Utils.toHumanReadableSize(profile.getStats().getUploaded().longValue()) + ") ");
        } else {
            dataUploaded.setVisibility(View.GONE);
            dataUploadedText.setVisibility(View.GONE);
        }
        if (profile.getRanks().getUploads() != null) {
            torrentsUploadedText.setVisibility(View.VISIBLE);
            torrentsUploaded.setText("" + profile.getRanks().getUploads().intValue() + "%");
            torrentsUploadedValue.setVisibility(View.VISIBLE);
            torrentsUploadedValue.setText("(" + profile.getCommunity().getUploaded().intValue() + ") ");
        } else {
            torrentsUploaded.setVisibility(View.GONE);
            torrentsUploadedText.setVisibility(View.GONE);
        }
        if (profile.getRanks().getRequests() != null) {
            requestsFilledText.setVisibility(View.VISIBLE);
            requestsFilled.setText("" + profile.getRanks().getRequests().intValue() + "%");
            requestsFilledValue.setVisibility(View.VISIBLE);
            requestsFilledValue.setText("(" + profile.getCommunity().getRequestsFilled().intValue() + ") ");
        } else {
            requestsFilled.setVisibility(View.GONE);
            requestsFilledText.setVisibility(View.GONE);
        }
        if (profile.getRanks().getBounty() != null) {
            bountySpentText.setVisibility(View.VISIBLE);
            bountySpent.setText("" + profile.getRanks().getBounty().intValue() + "%");
        } else {
            bountySpent.setVisibility(View.GONE);
            bountySpentText.setVisibility(View.GONE);
        }
        if (profile.getRanks().getPosts() != null) {
            postsMadeText.setVisibility(View.VISIBLE);
            postsMade.setText("" + profile.getRanks().getPosts().intValue() + "%");
            postsMadeValue.setVisibility(View.VISIBLE);
            postsMadeValue.setText("(" + profile.getCommunity().getPosts().intValue() + ") ");
        } else {
            postsMade.setVisibility(View.GONE);
            postsMadeText.setVisibility(View.GONE);
        }
        if (profile.getRanks().getArtists() != null) {
            artistsAddedText.setVisibility(View.VISIBLE);
            artistsAdded.setText("" + profile.getRanks().getArtists().intValue() + "%");
        } else {
            artistsAdded.setVisibility(View.GONE);
            artistsAddedText.setVisibility(View.GONE);
        }
        if (profile.getRanks().getOverall() != null) {
            overallText.setVisibility(View.VISIBLE);
            overall.setText("" + profile.getRanks().getOverall().intValue() + "%");
        } else {
            overall.setVisibility(View.GONE);
            overallText.setVisibility(View.GONE);
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            return User.sendMessage(userID, params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (!status) {
                Toast.makeText(getActivity(), "Could not send message", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
