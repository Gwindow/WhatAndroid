package what.util;

/**
 * Store information about the current version, Major.Minor.Patch
 */
public class VersionNumber {
	private int major = 0, minor = 0, patch = 0;

	/**
	 * Create the version number, the string should contain the version information
	 * such as "1.2.1"
	 * Will also handle cases of incomplete version numbers, ie. 1.2 will be read to 1.2.0
	 * @param s string containg the version information
	 */
	public VersionNumber(String s){
		String[] vals = s.split("\\.");
		if (vals.length > 0)
			major = Integer.parseInt(vals[0]);
		if (vals.length > 1)
			minor = Integer.parseInt(vals[1]);
		if (vals.length > 2)
			patch = Integer.parseInt(vals[2]);
	}

	/**
	 * Check if this version number is higher than some other version
	 * @param other the version number to compare with
	 * @return true if this version is a higher version
	 */
	public boolean isHigher(VersionNumber other){
		if (major > other.major)
			return true;
		if (major == other.major && minor > other.minor)
			return true;
		return (major == other.major && minor == other.minor && patch > other.patch);
	}

	public int getMajor(){
		return major;
	}

	public int getMinor(){
		return minor;
	}

	public int getPatch(){
		return patch;
	}

	@Override
	public String toString(){
		return major + "." + minor + "." + patch;
	}
}
