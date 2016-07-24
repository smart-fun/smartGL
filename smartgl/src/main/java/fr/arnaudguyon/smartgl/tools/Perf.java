package fr.arnaudguyon.smartgl.tools;

import java.util.HashMap;
import java.util.Map;

import android.os.SystemClock;
import android.util.Log;

public class Perf {

	private final static Map<String, Long> sMeasure = new HashMap<String, Long>();

	public final static void perfStart(String perfName) {
		final long currentTime = SystemClock.uptimeMillis();
		sMeasure.put(perfName, currentTime);
	}

	public final static long perfStop(String perfName, String optionalLogTag) {
		final long currentTime = SystemClock.uptimeMillis();
		final Long startTime = sMeasure.get(perfName);
		if (startTime != null) {
			final long duration = (currentTime - startTime.longValue());
			if (optionalLogTag != null) {
				Log.i(optionalLogTag, perfName + " took " + duration + " ms");
			}
			return duration;
		} else {
			if (optionalLogTag != null) {
				Log.i(optionalLogTag, perfName + " error: perfStart not called before");
			}
			return 0;
		}
	}

}
