package com.pri.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

public class Collections
{
 private static List<Object> EMPTY_COLLECTION = new EmptyCollection();
 private static Map<Object,Object> EMPTY_MAP = new EmptyMap();

 
 
 @SuppressWarnings("unchecked")
 public static final <T> List<T> emptyList()
 {
  return (List<T>) EMPTY_COLLECTION;
 }

 @SuppressWarnings("unchecked")
 public static final <T> Set<T> emptySet()
 {
  return (Set<T>) EMPTY_COLLECTION;
 }

 
 @SuppressWarnings("unchecked")
 public static final <K,V> Map<K,V> emptyMap()
 {
  return (Map<K,V>) EMPTY_MAP;
 }


 /**
  * @serial include
  */
 private static class EmptyCollection implements List<Object>, Set<Object>, RandomAccess, Serializable
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

 private static class EmptyMap implements Map<Object, Object>, Serializable
 {

  @Override
  public int size()
  {
   return 0;
  }

  @Override
  public boolean isEmpty()
  {
   return true;
  }

  @Override
  public boolean containsKey(Object key)
  {
   return false;
  }

  @Override
  public boolean containsValue(Object value)
  {
   return false;
  }

  @Override
  public Object get(Object key)
  {
   return null;
  }

  @Override
  public Object put(Object key, Object value)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public Object remove(Object key)
  {
   return null;
  }

  @Override
  public void putAll(Map< ? extends Object, ? extends Object> m)
  {
   throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
  }

  @Override
  public Set<Object> keySet()
  {
   return emptySet();
  }

  @Override
  public Collection<Object> values()
  {
   return emptySet();
  }

  @Override
  public Set<java.util.Map.Entry<Object, Object>> entrySet()
  {
   return emptySet();
  }
  
 }
 
}
