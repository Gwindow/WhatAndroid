package what.gui;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PatchInputStream extends FilterInputStream {
	public PatchInputStream(InputStream in) {
		super(in);
	}

	@Override
	public long skip(long n) throws IOException {
		long m = 0L;
		while (m < n) {
			long _m = in.skip(n - m);
			if (_m == 0L)
				break;
			m += _m;
		}
		return m;
	}
}