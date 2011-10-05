package uk.ac.ebi.mg.rwarbiter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RWArbiter
{
 private ReentrantLock lock = new ReentrantLock();
 
 private Condition writeReleased = lock.newCondition();
 private Condition readReleased = lock.newCondition();
 
 private int readReqs=0;
 private ReadWriteToken writeToken=null;
 
 public Object getReadLock()
 {
  try
  {
   lock.lock();
   
   while( writeToken != null )
    writeReleased.awaitUninterruptibly();

   readReqs++;
   
   return new ReadWriteToken();
  }
  finally
  {
   lock.unlock(); 
  }
 }
 
 public Object getWriteLock() throws InterruptedException
 {
  try
  {
   lock.lock();
   
   while( writeToken != null )
    writeReleased.awaitUninterruptibly();

   writeToken=new ReadWriteToken();
   
   if( readReqs > 0 )
    readReleased.awaitUninterruptibly();
   
   return new ReadWriteToken();
  }
  finally
  {
   lock.unlock(); 
  }
 }

 public boolean checkTokenValid( Object tobj )
 {
  return  tobj instanceof ReadWriteToken && ((ReadWriteToken)tobj).isActive();
 }
 
 public void releaseLock( Object tobj ) throws InvalidTokenException
 {
  if( ! checkTokenValid(tobj) )
   throw new InvalidTokenException();
  
  try
  {
   lock.lock();
   
   if( writeToken == tobj )
   {
    writeToken.setActive(false);
    writeToken = null;
    
    writeReleased.signalAll();
   }
   else
   {
    ((ReadWriteToken)tobj).setActive(false);
    
    readReqs--;
    
    if( readReqs == 0 )
     readReleased.signal();
   }
  }
  finally
  {
   lock.unlock(); 
  }
  
 }
 
 private class ReadWriteToken
 {
  boolean active = true;

  public boolean isActive()
  {
   return active;
  }

  public void setActive(boolean active)
  {
   this.active = active;
  }
 }
}
