package com.pri.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public class Collections
{
 private static List<Object> EMPTY_LIST = new EmptyList();

 
 
 @SuppressWarnings("unchecked")
 public static final <T> List<T> emptyList()
 {
  return (List<T>) EMPTY_LIST;
 }

 /**
  * @serial include
  */
 private static class EmptyList implements List<Object>, RandomAccess, Serializable
 {
  private static final long serialVersionUID = 020110716L;

  public int size()
  {
   return 0;
  }

  public boolean contains(Object obj)
  {
   return false;
  }

  public Object get(int index)
  {
   throw new IndexOutOfBoundsException("Index: " + index);
  }

  @Override
  public boolean isEmpty()
  {
   return true;
  }

  @Override
  public Iterator<Object> iterator()
  {
   return EmptyIterator.getInstance();
  }

  @Override
  public Object[] toArray()
  {
   return new Object[0];
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] a)
  {
   return (T[])new Object[0];
  }

  @Override
  public boolean add(Object e)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o)
  {
   return false;
  }

  @Override
  public boolean containsAll(Collection< ? > c)
  {
   return false;
  }

  @Override
  public boolean addAll(Collection< ? extends Object> c)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int index, Collection< ? extends Object> c)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection< ? > c)
  {
   return false;
  }

  @Override
  public boolean retainAll(Collection< ? > c)
  {
   return false;
  }

  @Override
  public void clear()
  {
  }

  @Override
  public Object set(int index, Object element)
  {
   throw new IndexOutOfBoundsException("Index: " + index);
  }

  @Override
  public void add(int index, Object element)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public Object remove(int index)
  {
   throw new IndexOutOfBoundsException("Index: " + index);
  }

  @Override
  public int indexOf(Object o)
  {
   return -1;
  }

  @Override
  public int lastIndexOf(Object o)
  {
   return -1;
  }

  @Override
  public ListIterator<Object> listIterator()
  {
   return EmptyIterator.getInstance();
  }

  @Override
  public ListIterator<Object> listIterator(int index)
  {
   throw new IndexOutOfBoundsException("Index: " + index);
  }

  @Override
  public List<Object> subList(int fromIndex, int toIndex)
  {
   if( fromIndex != 0 || toIndex != 0 )
   throw new IndexOutOfBoundsException();
   
   return this;
  }

 }

}
