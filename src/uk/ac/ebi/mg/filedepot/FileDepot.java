package uk.ac.ebi.mg.filedepot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileDepot
{
 private File rootDir;
 private boolean useHash = false;

 public FileDepot( File rt ) throws IOException
 {
  this(rt,false);
 }
 
 public FileDepot( File rt, boolean hsh ) throws IOException
 {
  useHash = hsh;
  
  if( rt.exists() )
  {
   if( ! rt.isDirectory() )
    throw new IOException("Path is not directory");
   
   if( ! rt.canWrite() )
    throw new IOException("Root directory is not writable");
  }
  else if( !  rt.mkdirs() )
   throw new IOException("Can't create root directory: "+rt.getAbsolutePath());
  
  rootDir = rt;
 }

 public File getFilePath( String fname )
 {
  return getFilePath(fname, -1L);
 }

 
 public File getFilePath( String fname, long timestamp )
 {
  String name = fname;
  
  char tail[] = new char[4];
  int nmLen;

  if( useHash )
  {
   name = Integer.toHexString(fname.hashCode() );
   nmLen = name.length();
  }
  else
  {
   name = fname;

   int extPos = fname.lastIndexOf('.');
   
   
   if( extPos == -1 )
    nmLen = fname.length();
   else
    nmLen = extPos;
  }
   
  
  for( int i=1; i <= tail.length; i++ )
  {
   if( i > nmLen )
    tail[tail.length-i]='_';
   else
   {
    char dg = name.charAt( nmLen-i );
    
//    dg = Character.toLowerCase(dg);
//    
//    if( ! Character.isDigit(dg) )
//     dg = '0';
    
    tail[tail.length-i]=dg;
   }
  }
  
  
  File dir = new File(rootDir,"xx"+tail[0]+tail[1]+"xx/xx"+tail[0]+tail[1]+tail[2]+tail[3]+"/");
  dir.mkdirs();
  
  if( timestamp == -1 )
   return new File(dir,fname);
  
  return new File(dir,fname+'@'+timestamp);
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

 public void shutdown()
 {
 }
}
