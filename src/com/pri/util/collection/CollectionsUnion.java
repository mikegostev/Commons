package com.pri.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionsUnion<E> implements Collection<E>, Serializable
{
 private Collection<Collection<E>> collecs;
 private int size=0;
 
 public CollectionsUnion( Collection<E> ... cs )
 {
  collecs = new ArrayList<Collection<E>>( cs.length );
  
  for( Collection<E> c : cs )
  {
   collecs.add(c);
   size+=c.size();
  }
 }
 
 public CollectionsUnion( Collection<Collection<E>> cs )
 {
  collecs=cs;
  
  for( Collection<E> c : collecs )
   size += c.size();
 }
 
 
 @Override
 public boolean add(E e)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public boolean addAll(Collection< ? extends E> c)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void clear()
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public boolean contains(Object o)
 {
  for( Collection<E> c : collecs )
   if( c.contains(o) )
    return true;
  
  return false;
 }

 @Override
 public boolean containsAll(Collection< ? > c)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public boolean isEmpty()
 {
  return size()==0;
 }

 @Override
 public Iterator<E> iterator()
 {
  if( size == 0 )
   return new EmptyIterator<E>();
  
  return new Iterator<E>(){
   
   Iterator<Collection<E>> citer = collecs.iterator();
   Iterator<E> eiter=citer.next().iterator();
   
   
   @Override
   public boolean hasNext()
   {
    while( true )
    {
     if(eiter.hasNext())
      return true;

     if(!citer.hasNext())
      return false;
     
     eiter=citer.next().iterator();
    }
    
   }

   @Override
   public E next()
   {
    hasNext();
    return eiter.next();
   }

   @Override
   public void remove()
   {
    throw new UnsupportedOperationException();
   }};
 }

 @Override
 public boolean remove(Object o)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public boolean removeAll(Collection< ? > c)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public boolean retainAll(Collection< ? > c)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public int size()
 {
  return size;
 }

 @Override
 public Object[] toArray()
 {
  int i=0;
  
  Object[] res = new Object[size];
  
  for( Collection<E> c : collecs )
   for( E el : c )
    res[i++]=el;
  
  return res;
 }

 @Override
 public <T> T[] toArray(T[] a)
 {
  throw new UnsupportedOperationException();
 }

}
