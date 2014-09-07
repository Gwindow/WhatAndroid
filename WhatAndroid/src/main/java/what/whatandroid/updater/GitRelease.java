package what.whatandroid.updater;

/**
 * Because we're using GSON we need a class to deserialize the Github releases
 * information into so that we can work with it easily.
 */
public class GitRelease {
	/**
	 * The various urls associated with the release
	 */
	private String html_url;
	/**
	 * The release tag_name, these correspond to the release's versionName
	 */
	private String tag_name;
	/**
	 * The title for the release
	 */
	private String name;
	/**
	 * The version number of the release, determined from the tag_name
	 */
	private VersionNumber versionNumber;
	/**
	 * If the release is a draft or pre-release
	 * draft releases are always ignored, pre-releases
	 * can be enabled with an option. Draft means I haven't
	 * finished writing up the release notes :P
	 */
	private boolean draft, prerelease;

	public GitRelease(){
	}

	public String getHtmlUrl(){
		return html_url;
	}

	public String getTagName(){
		return tag_name;
	}

	public VersionNumber getVersionNumber(){
		if (versionNumber == null){
			versionNumber = new VersionNumber(tag_name);
		}
		return versionNumber;
	}

	public boolean isDraft(){
		return draft;
	}

	public boolean isPrerelease(){
		return prerelease;
	}

	@Override
	public String toString(){
		return "GitRelease: " + name + " version: " + getVersionNumber().toString();
	}
}
