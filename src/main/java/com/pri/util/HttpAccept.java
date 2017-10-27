package com.pri.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HttpAccept {

    private static Comparator<Entry> cmpr = new Comparator<HttpAccept.Entry>() {
        @Override
        public int compare(Entry o1, Entry o2) {

            if (o1.q == o2.q) {
                return 0;
            }

            return (o1.q - o2.q) > 0 ? -1 : 1;
        }
    };

    private List<Entry> entries;

    private static class Entry {

        String major;
        String minor;
        double q;
    }

    public HttpAccept(String hdr) {
        List<String> pts = StringUtils.splitString(hdr, ',');

        entries = new ArrayList<HttpAccept.Entry>(pts.size());

        for (String s : pts) {
            List<String> prms = StringUtils.splitString(s, ';');

            List<String> ctp = StringUtils.splitString(prms.get(0), '/');

            Entry entry = new Entry();

            entry.major = ctp.get(0).trim();

            if (ctp.size() == 2) {
                entry.minor = ctp.get(1).trim();
            }

            entry.q = 1;

            for (int i = 1; i < prms.size(); i++) {
                List<String> nv = StringUtils.splitString(prms.get(i), '=');

                if ("q".equalsIgnoreCase(nv.get(0).trim()) && nv.size() == 2) {
                    try {
                        entry.q = Double.parseDouble(nv.get(1).trim());
                        break;
                    } catch (NumberFormatException e) {
                    }
                }

            }

            entries.add(entry);
        }

        Collections.sort(entries, cmpr);
    }

    public int bestMatch(List<String> strs) {
        int ind = -1;

        int match = entries.size();

        int i = -1;
        for (String s : strs) {
            i++;

            List<String> pts = StringUtils.splitString(s, '/');

            if (pts.size() != 2) {
                continue;
            }

            String mjr = pts.get(0).trim();
            String mnr = pts.get(1).trim();

            for (int j = 0; j < match; j++) {
                Entry e = entries.get(j);

                if ((mjr.equalsIgnoreCase(e.major) || e.major.equals("*")) && (e.minor == null || mnr
                        .equalsIgnoreCase(e.minor) || e.minor.equals("*"))) {
                    ind = i;
                    match = j;
                    break;
                }

            }

        }

        return ind;
    }

}
