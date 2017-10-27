package com.pri.adob;

import com.pri.log.Log;
import com.pri.util.stream.StreamPump;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;


public class FileADOBDepot implements ADOBDepot {

    private static final String LOCK_FILE_NAME = ".depot.lock";
    private static final String STATUS_FILE_NAME = ".depot.status";
    private static final String CONFIG_FILE_NAME = ".depot.config";

    private static final int LEVEL_WIDTH = 10;
    private static final int LEVELS = 2;

    private static final char SEP1 = '@';
    private static final char SEP2 = '_';
 
 
 /*-******************************************************************-*/

    private File root;

    private AtomicInteger storeCount = new AtomicInteger(0);

    private int fileCount;
    private long totalSize;
    private long dataSize;

    private long startTime = System.currentTimeMillis();
    private int levels = LEVELS;
    private int nodes = LEVEL_WIDTH;
    private boolean lazy;
    private File config;
    private File status;

    public FileADOBDepot(String rp, int levels, int nodes, boolean lazy) throws IOException {
        this(new File(rp), levels, nodes, lazy);
    }

    public FileADOBDepot(File rf, int levels, int nodes, boolean lazy) throws IOException {
        root = rf;

        if (!root.exists()) {
            if (!root.mkdir()) {
                throw new FileNotFoundException("Can't create depot root directory: " + root);
            }
        } else if (!root.isDirectory()) {
            throw new FileNotFoundException("Depot root is not directory: " + root);
        }

        if (!root.canWrite()) {
            throw new FileNotFoundException("Depot root directory isn't writable: " + root);
        }

        config = new File(root, CONFIG_FILE_NAME);
        status = new File(root, STATUS_FILE_NAME);

        this.lazy = lazy;
        this.nodes = nodes;
        this.levels = levels;

        if (config.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(config));
                DepotConfigV1 cfg = (DepotConfigV1) ois.readObject();
                ois.close();

                this.lazy = cfg.isLazy();
                this.levels = cfg.getLevels();
                this.nodes = cfg.getNodes();
            } catch (ClassNotFoundException e) {
                Log.error("Can't deserialize depot config. Root: {0}", e, rf.getAbsolutePath());
            } catch (IOException e) {
                Log.error("Can't read depot config. Root: {0}", e, rf.getAbsolutePath());
            }
        } else {
            DepotConfigV1 cfg = new DepotConfigV1(levels, nodes, lazy);
            FileOutputStream fst = new FileOutputStream(config);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(config));
            oos.writeObject(cfg);
            oos.close();
            fst.close();

            if (!this.lazy) {
                init(root, 0);
            }
        }

        loadStatus();

    }

    private void init(File lvl, int depth) throws FileNotFoundException {
        for (int i = 0; i < nodes; i++) {
            File subDir = new File(lvl, String.valueOf(i));

            if (!subDir.exists()) {
                subDir.mkdir();
            } else if (!subDir.isDirectory()) {
                Log.error("Depot ({0}) node is not directory: {1}", root, subDir);
                throw new FileNotFoundException("Depot node is not directory: " + subDir);
            }

            if (depth < levels - 1) {
                init(subDir, depth + 1);
            }
        }
    }


    private void loadStatus() throws IOException {
        File lock = new File(root, LOCK_FILE_NAME);

        if (!status.exists()) {
            rescanStatus();
            lock.createNewFile();
        } else {
            if (lock.createNewFile()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(status));
                    DepotStatusV1 stat = (DepotStatusV1) ois.readObject();
                    storeCount.set(stat.getLastCounter());
                    totalSize = stat.getTotalSize();
                    dataSize = stat.getDataSize();
                    fileCount = stat.getADOBCount();
                } catch (Exception e) {
                    Log.warn("Can't read depot status. Depot: {0}", e, root.getAbsolutePath());
                    rescanStatus();
                }
            } else {
                rescanStatus();
            }
        }
    }

    private void rescanStatus() {
        File[] subd = root.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });

        storeCount.set(0);
        totalSize = 0;
        dataSize = 0;
        fileCount = 0;

        for (int i = 0; i < subd.length; i++) {
            rescanStatus(subd[i]);
        }

        storeCount.incrementAndGet();
    }

    private void rescanStatus(File dir) {
        File[] f = dir.listFiles();

        for (int i = 0; i < f.length; i++) {
            if (f[i].isDirectory()) {
                rescanStatus(f[i]);
            } else {
                int colind = f[i].getName().indexOf(SEP1);
                if (colind <= 0) {
                    continue;
                }

                String nums = f[i].getName().substring(0, colind);
                int num = Integer.parseInt(nums);

                if (num > storeCount.get()) {
                    storeCount.set(num);
                }

                totalSize += f[i].length();

                if (!f[i].getName().endsWith(".meta")) {
                    fileCount++;
                    dataSize += f[i].length();
                }
            }
        }

    }

    private String generatePath(ADOB b) {
        int[] pt = new int[levels];

        int cnt = storeCount.getAndIncrement();
        int tc = cnt;

        if (lazy) {
            cnt /= nodes;
        }

        for (int i = levels - 1; i >= 0; i--) {
            pt[i] = cnt % nodes;
            cnt /= nodes;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < levels; i++) {
            sb.append(pt[i]).append('/');
        }

        sb.append(tc).append(SEP1).append(startTime).append(SEP1);

        String ctyp = b.getContentType();

        int pos = 0, ppos = 0;
        while ((pos = ctyp.indexOf('/', ppos)) != -1) {
            sb.append(ctyp.substring(ppos, pos));
            sb.append(SEP2);
            ppos = pos + 1;
        }

        if (ppos == 0) {
            sb.append(ctyp);
        } else {
            sb.append(ctyp.substring(ppos));
        }

        return sb.toString();
    }

    public File getRoot() {
        return root;
    }

    public String put(ADOB b) throws IOException {
        if (b instanceof FileADOB) {
            FileADOB fb = (FileADOB) b;
            if (fb.setTemporary(false)) {
                return storeTMPFB(fb);
            }
        }

        String path = generatePath(b);
        File f = new File(root, path);

        if (lazy) {
            File dir = new File(root, path.substring(0, path.lastIndexOf('/')));
            dir.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(f);
        long sz = StreamPump.doPump(b.getInputStream(), fos, true);

        fileCount++;
        dataSize += sz;
        totalSize += sz;

        Object meta = b.getMetaInfo();
        if (b != null && meta instanceof Serializable) {
            storeMeta(path, meta);
        }

        return path;
    }

    private String storeTMPFB(FileADOB fb) throws FileNotFoundException {
        if (!fb.getFile().exists()) {
            throw new FileNotFoundException("File not found: " + fb.getFile());
        }

        String path = generatePath(fb);

        if (lazy) {
            File dir = new File(root, path.substring(0, path.lastIndexOf('/')));
            dir.mkdirs();
        }

        File f = new File(root, path);
        fb.getFile().renameTo(f);
        fb.setFile(f);

        fileCount++;
        dataSize += f.length();
        totalSize += f.length();

        Object meta = fb.getMetaInfo();
        if (fb != null && meta instanceof Serializable) {
            storeMeta(path, meta);
        }

        return path;

    }

    private void storeMeta(String path, Object meta) {
        FileOutputStream fos;
        try {
            File metafile = new File(root, path + ".meta");
            fos = new FileOutputStream(metafile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(meta);
            oos.close();
            fos.close();

            totalSize += metafile.length();
        } catch (Exception e) {
            Log.warn("Meta info storing error. Depot: {0}", e, root.getAbsolutePath());
        }
    }

    private File findFile(Object key) {
        if (!(key instanceof String)) {
            return null;
        }

        String path = (String) key;
        if (path.charAt(0) == '/' || path.indexOf('.') != -1) {
            return null;
        }

        File f = new File(root, path);

        if (f.exists()) {
            return f;
        }

        return null;

    }

    public boolean containsADOB(Object key) {
        File f = findFile(key);

        return f != null;
    }

    public AbstractADOB get(Object key) {
        File f = findFile(key);

        if (f == null) {
            return null;
        }

        String path = (String) key;
        StringBuffer sb = new StringBuffer(path.substring(path.lastIndexOf(SEP1) + 1));

        int pos = 0;
        String sep2 = "" + SEP2;
        while ((pos = sb.indexOf(sep2, pos)) != -1) {
            sb.setCharAt(pos, '/');
            pos++;
        }

        FileADOB fb = new FileADOB(f, sb.toString());

        f = new File(root, path + ".meta");

        try {
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                fb.setMetaInfo(ois.readObject());
                ois.close();
            }
        } catch (Exception e) {
            Log.warn("Can't read ADOB meta info. ({0})", e, f.getAbsolutePath());
        }

        return fb;
    }

    public long getCreateTime(Object key) {
        File f = findFile(key);

        if (f == null) {
            return 0L;
        }

        return f.lastModified();
    }

    public boolean remove(Object key) {
        File f = findFile(key);

        if (f == null) {
            return false;
        }

        long sz = f.length();

        if (f.delete()) {
            totalSize -= sz;
            dataSize -= sz;
            fileCount--;
        } else {
            return false;
        }

        f = new File(f.getAbsolutePath() + ".meta");

        if (f.exists() && f.delete()) {
            totalSize -= f.length();
        }

        return true;
    }

    public void cleanup() throws IOException {
        File[] files = root.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                cleanup(files[i]);
            }
        }

        totalSize = 0;
        dataSize = 0;
        fileCount = 0;
        storeCount.set(0);

        saveStatus();

    }

    private void cleanup(File subd) {
        File[] files = subd.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                cleanup(files[i]);
            } else {
                files[i].delete();
            }
        }
    }

    public void saveStatus() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(status));
        oos.writeObject(new DepotStatusV1(totalSize, dataSize, fileCount, storeCount.get()));
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public long getADOBCount() {
        return fileCount;
    }


    public Iterator<Object> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

// private int pow(int base, int pow)
// {
//  int res = 1;
//  
//  while( pow-- > 0 )
//   res*=base;
//  
//  return res;
// }

    public void destroy() {
        if (root == null) {
            return;
        }

        try {
            saveStatus();
            new File(root, LOCK_FILE_NAME).delete();
        } catch (IOException e) {
            Log.warn("Can't store depot status. Depot: {0}", e, root.getAbsolutePath());
        }

        root = null;
    }

    public void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    private static class DepotRef {

        int count;
        FileADOBDepot depot;
        boolean delete;
    }

    private static Map<String, DepotRef> cache = new TreeMap<String, DepotRef>();

    public static synchronized FileADOBDepot getFileADOBDepot(File rf, int levels, int nodes, boolean lazy)
            throws IOException {
        DepotRef dpr = cache.get(rf.getAbsolutePath());

        if (dpr != null) {
            dpr.count++;
            return dpr.depot;
        }

        dpr = new DepotRef();
        dpr.depot = new FileADOBDepot(rf, levels, nodes, lazy);
        dpr.count = 1;
        dpr.delete = false;
        cache.put(rf.getAbsolutePath(), dpr);

        return dpr.depot;
    }

    public static synchronized void releaseFileADOBDepot(File rf) {
        releaseFileADOBDepot(rf.getAbsolutePath());
    }

    public static synchronized void releaseFileADOBDepot(FileADOBDepot fbd) {
        releaseFileADOBDepot(fbd.getRoot().getAbsolutePath());
    }

    public static synchronized void releaseFileADOBDepot(String rp) {
        DepotRef dpr = cache.get(rp);

        if (dpr == null) {
            return;
        }

        if (dpr.count == 1) {
            cache.remove(rp);

            if (dpr.delete) {
                removeDir(new File(rp));
            } else {
                dpr.depot.destroy();
            }
        } else {
            dpr.count--;
        }
    }

    public static synchronized boolean moveTo(FileADOBDepot dep, File newRoot) {
        File oldRoot = dep.root;

        boolean res = dep.getRoot().renameTo(newRoot);

        if (res) {
            dep.root = newRoot;

            DepotRef dpr = cache.remove(oldRoot.getAbsolutePath());

            if (dpr == null) {
                return true;
            }

            cache.put(newRoot.getAbsolutePath(), dpr);
        }

        return res;
    }

    public static synchronized void deleteFileADOBDepot(File depotFile) {
        DepotRef dpr = cache.get(depotFile.getAbsolutePath());

        if (dpr == null || dpr.count == 1) {
            removeDir(depotFile);
        } else {
            dpr.delete = true;
            dpr.count--;
        }
    }

    static void removeDir(File dir) {
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                removeDir(dir);
            } else {
                files[i].delete();
            }
        }

        dir.delete();
    }

    protected static class DepotConfigV1 implements Serializable {

        private static final long serialVersionUID = 1L;

        private int levels;
        private int nodes;
        private boolean lazy;


        public DepotConfigV1(int levels, int nodes, boolean lazy) {
            this.levels = levels;
            this.nodes = nodes;
            this.lazy = lazy;
        }


        public boolean isLazy() {
            return lazy;
        }


        public int getLevels() {
            return levels;
        }


        public int getNodes() {
            return nodes;
        }

    }

    protected static class DepotStatusV1 implements Serializable {

        private static final long serialVersionUID = 1L;

        private long totalSize;
        private long dataSize;
        private int blobCount;
        private int lastCounter;

        public DepotStatusV1(long totalSize, long dataSize, int blobCount, int lastCounter) {
            this.totalSize = totalSize;
            this.dataSize = dataSize;
            this.blobCount = blobCount;
            this.lastCounter = lastCounter;
        }

        public int getADOBCount() {
            return blobCount;
        }

        public long getDataSize() {
            return dataSize;
        }

        public int getLastCounter() {
            return lastCounter;
        }

        public long getTotalSize() {
            return totalSize;
        }


    }


}
