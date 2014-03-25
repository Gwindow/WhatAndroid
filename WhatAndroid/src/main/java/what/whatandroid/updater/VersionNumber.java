package what.whatandroid.updater;

/**
 * Store information about the current version, Major.Minor.Patch
 */
public class VersionNumber {
	private int major = 0, minor = 0, patch = 0;
	private boolean beta = false, alpha = false;

	/**
	 * Create the version number, the string should contain the version information
	 * such as "1.2.1"
	 * Will also handle cases of incomplete version numbers, ie. 1.2 will be read to 1.2.0
	 * A version number with an 'a' at the end indicates an alpha buld: 2.0.0.a -> 2.0.0 alpha release
	 * A version number with a 'b' at the end indicates a beta build: 1.2.1.b -> 1.2.1 beta release
	 *
	 * @param s string containg the version information
	 */
	public VersionNumber(String s){
		String[] vals = s.split("\\.");
		if (vals.length > 0){
			major = Integer.parseInt(vals[0]);
		}
		if (vals.length > 1){
			minor = Integer.parseInt(vals[1]);
		}
		if (vals.length > 2){
			patch = Integer.parseInt(vals[2]);
		}
		if (s.charAt(s.length() - 1) == 'b'){
			beta = true;
		}
		else if (s.charAt(s.length() - 1) == 'a'){
			alpha = true;
		}
	}

	/**
	 * Check if this version number is higher than some other version
	 *
	 * @param other the version number to compare with
	 * @return true if this version is a higher version
	 */
	public boolean isHigher(VersionNumber other){
		if (major < other.major || minor < other.minor || patch < other.patch
			|| (alpha && !other.alpha) || (beta && !(other.alpha || other.beta))){
			return false;
		}
		//If the version numbers are equal but we aren't a preview and the other build is, we're higher
		return !(major == other.major && minor == other.minor && patch == other.patch) || (beta && other.alpha)
			|| (!beta && !alpha && (other.alpha || other.beta));
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

	public boolean isBeta(){
		return beta;
	}

	public boolean isAlpha(){
		return alpha;
	}

	@Override
	public String toString(){
		return major + "." + minor + "." + patch + (alpha ? " Alpa" : beta ? " Beta" : "");
	}
}