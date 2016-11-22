/*
    Copyright 2016 Arnaud Guyon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package fr.arnaudguyon.smartgl.tools;

import java.util.HashMap;

import android.os.SystemClock;
import android.util.Log;

/**
 * Helper to measure performance (duration) of some code
 */
public class Perf {

	private static final HashMap<String, Long> sMeasure = new HashMap<>();

	public static void perfStart(String perfName) {
		final long currentTime = SystemClock.uptimeMillis();
		sMeasure.put(perfName, currentTime);
	}

	public static long perfStop(String perfName, String optionalLogTag) {
		final long currentTime = SystemClock.uptimeMillis();
		final Long startTime = sMeasure.get(perfName);
		if (startTime != null) {
			final long duration = (currentTime - startTime);
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
