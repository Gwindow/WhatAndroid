package what.util;

/**
 * Because we're using GSON we need a class to deserialize the Github releases
 * information into so that we can work with it easily.
 */
public class GitRelease {
	//The various urls associated with the release
	private String url;
	private String assets_url;
	private String html_url;
	//Release id
	private int id;
	//The release tag_name, these correspond to the release's versionName
	private String tag_name;
	//The version number of the release, determined from the tag_name
	private VersionNumber versionNumber;
	private String target_commitish;
	//Release's name
	private String name;
	//The release notes for the release
	private String body;
	//If the release is a draft or pre-release
	private boolean draft;
	private boolean prerelease;
	//Creation and publish dates for the release
	//TODO Should parse these into actual dates? or do we even care?
	private String created_at;
	private String published_at;

	public GitRelease(){
	}

	/**
	 * Get the url
	 * @return the api url
	 */
	public String getUrl(){
		return url;
	}

	/**
	 * Get the assets url
	 * @return the api url for the assets
	 */
	public String getAssetsUrl(){
		return assets_url;
	}

	/**
	 * Get the html url to view this release in the browser
	 * @return the web url to view the release
	 */
	public String getHtmlUrl(){
		return html_url;
	}

	/**
	 * Get the release id
	 * @return release id
	 */
	public int getId(){
		return id;
	}

	/**
	 * Get the release tag name
	 * @return release tag name
	 */
	public String getTagName(){
		return tag_name;
	}

	/**
	 * Get the release's version number
	 * @return the version number
	 */
	public VersionNumber getVersionNumber(){
		//The version number we make ourselves so if we haven't created it yet, do so now
		if (versionNumber == null)
			versionNumber = new VersionNumber(tag_name);
		return versionNumber;
	}

	/**
	 * Get the name of the release
	 * @return the release name
	 */
	public String getName(){
		return name;
	}

	/**
	 * Get the body text of the release, describing changes and etc.
	 * @return the release body text
	 */
	public String getBody(){
		return body;
	}

	/**
	 * Check if the release is a draft
	 * @return true if the release is a draft
	 */
	public boolean isDraft(){
		return draft;
	}

	/**
	 * Check if the release is a pre-release
	 * @return true if the release is a pre-release
	 */
	public boolean isPrerelease(){
		return prerelease;
	}
}
