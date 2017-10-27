package uk.ac.ebi.mg.filedepot;

import com.pri.util.M2Pcodec;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileDepot implements Iterable<File> {

    private final File rootDir;
    private boolean useHash = false;

    public FileDepot(File rt) throws IOException {
        this(rt, false);
    }

    public FileDepot(File rt, boolean hsh) throws IOException {
        useHash = hsh;

        if (rt.exists()) {
            if (!rt.isDirectory()) {
                throw new IOException("Path is not directory");
            }

            if (!rt.canWrite()) {
                throw new IOException("Root directory is not writable");
            }
        } else if (!rt.mkdirs()) {
            throw new IOException("Can't create root directory: " + rt.getAbsolutePath());
        }

        rootDir = rt;
    }

    public File getRootDir() {
        return rootDir;
    }

    public File getFilePath(String fname) {
        return getFilePath(fname, -1L);
    }

    public String getRelativeFilePath(String fname, long timestamp) {
        fname = M2Pcodec.encode(fname);

        String pathSplit = null;

        char tail[] = new char[4];
        int nmLen;

        if (useHash) {
            pathSplit = Integer.toHexString(fname.hashCode());
            nmLen = pathSplit.length();
        } else {
            pathSplit = fname;

            int extPos = fname.lastIndexOf('.');

            if (extPos == -1) {
                nmLen = pathSplit.length();
            } else {
                nmLen = extPos;
            }
        }

        for (int i = 1; i <= tail.length; i++) {
            if (i > nmLen) {
                tail[tail.length - i] = '_';
            } else {
                char dg = pathSplit.charAt(nmLen - i);

                tail[tail.length - i] = dg;
            }
        }

        String relPath = "xx" + tail[0] + tail[1] + "xx/xx" + tail[0] + tail[1] + tail[2] + tail[3] + "/";

        if (timestamp == -1) {
            return relPath + fname;
        }

        return relPath + timestamp + "@" + fname;
    }

    public File getFilePath(String fname, long timestamp) {

        File file = new File(rootDir, getRelativeFilePath(fname, timestamp));
        file.getParentFile().mkdirs();

        return file;
    }

    public List<File> listFiles() {
        List<File> list = new ArrayList<File>(10000);

        for (File l1d : rootDir.listFiles()) {
            for (File l2d : l1d.listFiles()) {
                for (File f : l2d.listFiles()) {
                    list.add(f);
                }
            }
        }

        return list;
    }

    @Override
    public Iterator<File> iterator() {
        return new Iterator<File>() {
            private File next;

            File[] l1list = rootDir.listFiles();
            int l1ptr = 0;

            File[] l2list = new File[0];
            int l2ptr = 0;

            File[] l3list = l2list;
            int l3ptr = 0;

            private File getNextL3() {
                if (l3ptr < l3list.length) {
                    return l3list[l3ptr++];
                }

                l3list = getNextL2();

                if (l3list == null) {
                    return null;
                }

                l3ptr = 0;

                return l3list[l3ptr++];
            }

            private File[] getNextL2() {
                while (true) {
                    while (l2ptr < l2list.length) {
                        File n = l2list[l2ptr++];

                        if (n.isDirectory()) {
                            File[] fa = n.listFiles();

                            if (fa.length > 0) {
                                return fa;
                            }
                        }
                    }

                    l2list = getNextL1();

                    if (l2list == null) {
                        return null;
                    }

                    l2ptr = 0;
                }
            }

            private File[] getNextL1() {
                while (l1ptr < l1list.length) {
                    File n = l1list[l1ptr++];

                    if (n.isDirectory()) {
                        File[] fa = n.listFiles();

                        if (fa.length > 0) {
                            return fa;
                        }
                    }
                }

                return null;
            }


            @Override
            public boolean hasNext() {
                next = getNextL3();

                return next != null;
            }

            @Override
            public File next() {
                if (next != null) {
                    File f = next;
                    next = null;
                    return f;
                }

                hasNext();

                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void shutdown() {
    }
}
