package com.pri.util;

import java.io.File;
import java.io.FileNotFoundException;

import com.pri.adob.FileADOB;

public class JpegFileData extends FileADOB
{
 private static final String contentType = "image/jpeg";

 public JpegFileData(String fName) throws FileNotFoundException
 {
  this(new File(fName));
 }
 
 public JpegFileData(File f) throws FileNotFoundException
 {
  super(f, contentType);
  
  if( ! f.canRead() )
   throw new FileNotFoundException("File "+f+" not exists or unreadable");
 }

}
