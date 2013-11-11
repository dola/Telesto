package ch.ethz.syslab.telesto.profile;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.syslab.telesto.util.Log;

public final class SimpleProfiler {
    private static Log LOGGER = new Log(SimpleProfiler.class);

    private static SimpleProfiler instance = new SimpleProfiler();

    private Map<String, ProfileSection> sections = new HashMap<>();

    private SimpleProfiler() {

    }

    /**
     * Method to get Singleton SimpleProfiler Instance
     * 
     * @return singleton instance
     */
    public static SimpleProfiler get() {
        return instance;
    }

    public void startSection(String name) {
        long time = System.currentTimeMillis();

        ProfileSection s = new ProfileSection(name, time);
        if (sections.containsKey(name)) {
            LOGGER.severe("Sections Stack already contains an entry with name %s", name);
            return;
        }
        sections.put(name, s);
        LOGGER.fine("starting new section %s @ %s", name, time);
    }

    public long endSection(String name) {
        long time = System.currentTimeMillis();

        if (sections.containsKey(name)) {
            ProfileSection s = sections.remove(name);
            s.endTime = time;
            String out = String.format("ending section %s @ %s: total time was %d", name, time, s.getTotalTime());
            LOGGER.fine(out);
            System.out.println(out);
            return s.getTotalTime();
        }
        return 0;
    }

    private class ProfileSection {
        String name;
        long startTime;
        long endTime;

        public ProfileSection(String name, long timestamp) {
            this.name = name;
            startTime = timestamp;
            endTime = timestamp;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ProfileSection)) {
                return false;
            }
            ProfileSection other = (ProfileSection) obj;
            return name.equals(other.name);
        }

        public long getTotalTime() {
            return endTime - startTime;
        }
    }
}
