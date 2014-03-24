package what.whatandroid.updater;

/**
 * Stores information about an Alpha release, the version number and the
 * url to download the apk
 */
public class AlphaRelease {
	private String version;
	private VersionNumber versionNumber;
	private String url;

	public VersionNumber getVersionNumber(){
		if (versionNumber == null){
			versionNumber = new VersionNumber(version);
		}
		return versionNumber;
	}

	public String getUrl(){
		return url;
	}

	@Override
	public String toString(){
		return "AlphaRelease [versionNumber=" + getVersionNumber() + ", url=" + url + "]";
	}
}
