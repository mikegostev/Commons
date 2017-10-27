package com.pri.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Directory implements Serializable {

    private String name;
    private List<Directory> subDirs;
    private List<String> files;


    public Directory(String nm) {
        name = nm;
    }

    public String getName() {
        return name;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<Directory> getSubdirectories() {
        return subDirs;
    }

    private void addSubdirectory(Directory sd) {
        if (subDirs == null) {
            subDirs = new ArrayList<Directory>(5);
        }

        subDirs.add(sd);
    }

    private void addFile(String nm) {
        if (files == null) {
            files = new ArrayList<String>(5);
        }

        files.add(nm);
    }

    public static Directory createDirectory(File d) {
        if (!d.isDirectory()) {
            return null;
        }

        Directory dir = new Directory(d.getName());

        for (File f : d.listFiles()) {
            if (f.isDirectory()) {
                dir.addSubdirectory(createDirectory(f));
            } else {
                dir.addFile(f.getName());
            }
        }

        return dir;
    }

}
