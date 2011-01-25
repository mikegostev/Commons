package uk.ac.ebi.mg.filedepot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileDepot
{
 private File rootDir;
 
 public FileDepot( File rt ) throws IOException
 {
  if( rt.exists() )
  {
   if( ! rt.isDirectory() )
    throw new IOException("Path is not directory");
   
   if( ! rt.canWrite() )
    throw new IOException("Root directory is not writable");
  }
  
  if( !  rt.mkdirs() )
   throw new IOException("Can't create root directory: "+rt.getAbsolutePath());
  
  rootDir = rt;
 }
 
 public File getFilePath( String fname, long timestamp )
 {
  char dgts[] = new char[4];
  int nmLen = fname.length();
  
  for( int i=1; i <= dgts.length; i++ )
  {
   if( i > nmLen )
    dgts[dgts.length-i]='0';
   else
   {
    char dg = fname.charAt( nmLen-i );
    
    if( ! Character.isDigit(dg) )
     dg = '0';
    
    dgts[dgts.length-i]=dg;
   }
  }
  
  
  File dir = new File(rootDir,"xx"+dgts[0]+dgts[1]+"xx/xx"+dgts[0]+dgts[1]+dgts[2]+dgts[3]+"/");
  dir.mkdirs();
  
  return new File(dir,fname+'.'+timestamp);
 }
 
 public List<File> listFiles()
 {
  List<File> list = new ArrayList<File>(10000);
  
  for( File l1d : rootDir.listFiles() )
   for( File l2d : l1d.listFiles() )
    for( File f : l2d.listFiles() )
     list.add(f);
  
  return list;
 }
}
