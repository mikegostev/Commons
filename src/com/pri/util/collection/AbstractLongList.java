package com.pri.util.collection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;


public abstract class AbstractLongList  implements LongList,Iterable 

{
 protected AbstractLongList()
 {
 }

 public boolean add(long o)
 {
  add(size(), o);
  return true;
 }

 abstract public long get(int index);

 @SuppressWarnings("unused")
 public long set(int index, long element)
 {
  throw new UnsupportedOperationException();
 }

 @SuppressWarnings("unused")
 public void add(int index, long element)
 {
  throw new UnsupportedOperationException();
 }

 @SuppressWarnings("unused")
 public long removeAt(int index)
 {
  throw new UnsupportedOperationException();
 }

 public boolean isEmpty()
 {
  return size() == 0;
 }

 public int indexOf(long o)
 {
  LongListIterator e = listIterator();

  while(e.hasNext())
   if(o == e.next())
    return e.previousIndex();

  return -1;
 }

 public int lastIndexOf(long o)
 {
  LongListIterator e = listIterator(size());

  while(e.hasPrevious())
   if(o == e.previous())
    return e.nextIndex();

  return -1;
 }

 public void clear()
 {
  removeRange(0, size());
 }

 public long[] toArray()
 {
  long[] result = new long[size()];
  LongListIterator e = listIterator();
  for(int i = 0; e.hasNext(); i++)
   result[i] = e.next();
  return result;
 }

 public boolean contains(long o)
 {
  LongListIterator e = listIterator();

  while(e.hasNext())
   if(o == e.next())
    return true;
  return false;
 }

 public boolean addAll(int index, Collection<Long> c)
 {
  boolean modified = false;
  Iterator<Long> e = c.iterator();
  while(e.hasNext())
  {
   add(index++, e.next().intValue());
   modified = true;
  }
  return modified;
 }

 public Iterator<Long> iterator()
 {
  return new Itr();
 }

 public LongListIterator listIterator()
 {
  return listIterator(0);
 }

 public LongListIterator listIterator(final int index)
 {
  if(index < 0 || index > size())
   throw new IndexOutOfBoundsException("Index: " + index);

  return new ListItr(index);
 }

 private class Itr implements Iterator<Long>
 {
  int cursor           = 0;
  int lastRet          = -1;
  int expectedModCount = modCount;

  public boolean hasNext()
  {
   return cursor != size();
  }

  public Long next()
  {
   checkForComodification();
   try
   {
    Long next = get(cursor);
    lastRet = cursor++;
    return next;
   }
   catch(IndexOutOfBoundsException e)
   {
    checkForComodification();
    throw new NoSuchElementException();
   }
  }

  public void remove()
  {
   if(lastRet == -1)
    throw new IllegalStateException();
   checkForComodification();

   try
   {
    AbstractLongList.this.remove(lastRet);
    if(lastRet < cursor)
     cursor--;
    lastRet = -1;
    expectedModCount = modCount;
   }
   catch(IndexOutOfBoundsException e)
   {
    throw new ConcurrentModificationException();
   }
  }

  final void checkForComodification()
  {
   if(modCount != expectedModCount)
    throw new ConcurrentModificationException();
  }
 }

 private class ListItr implements LongListIterator
 {
  int cursor           = 0;
  int lastRet          = -1;
  int expectedModCount = modCount;

  ListItr(int index)
  {
   cursor = index;
  }

  public boolean hasPrevious()
  {
   return cursor != 0;
  }

  public long previous()
  {
   checkForComodification();
   try
   {
    int i = cursor - 1;
    long previous = get(i);
    lastRet = cursor = i;
    return previous;
   }
   catch(IndexOutOfBoundsException e)
   {
    checkForComodification();
    throw new NoSuchElementException();
   }
  }

  public int nextIndex()
  {
   return cursor;
  }

  public int previousIndex()
  {
   return cursor - 1;
  }

  public void set(long o)
  {
   if(lastRet == -1)
    throw new IllegalStateException();
   checkForComodification();

   try
   {
    AbstractLongList.this.set(lastRet, o);
    expectedModCount = modCount;
   }
   catch(IndexOutOfBoundsException e)
   {
    throw new ConcurrentModificationException();
   }
  }

  public void add(long o)
  {
   checkForComodification();

   try
   {
    AbstractLongList.this.add(cursor++, o);
    lastRet = -1;
    expectedModCount = modCount;
   }
   catch(IndexOutOfBoundsException e)
   {
    throw new ConcurrentModificationException();
   }
  }

  public boolean hasNext()
  {
   return cursor != size();
  }

  public long next()
  {
   checkForComodification();
   try
   {
    Long next = get(cursor);
    lastRet = cursor++;
    return next;
   }
   catch(IndexOutOfBoundsException e)
   {
    checkForComodification();
    throw new NoSuchElementException();
   }
  }

  public void remove()
  {
   if(lastRet == -1)
    throw new IllegalStateException();
   checkForComodification();

   try
   {
    AbstractLongList.this.remove(lastRet);
    if(lastRet < cursor)
     cursor--;
    lastRet = -1;
    expectedModCount = modCount;
   }
   catch(IndexOutOfBoundsException e)
   {
    throw new ConcurrentModificationException();
   }
  }

  final void checkForComodification()
  {
   if(modCount != expectedModCount)
    throw new ConcurrentModificationException();
  }
 }

 public LongList subList(int fromIndex, int toIndex)
 {
  return (this instanceof RandomAccess ? new LongRandomAccessSubList(this, fromIndex, toIndex) : new LongSubList(this,
    fromIndex, toIndex));
 }

 public boolean equals(Object o)
 {
  if(o == this)
   return true;
  if(!(o instanceof LongList))
   return false;

  LongListIterator e1 = listIterator();
  LongListIterator e2 = ((LongList) o).listIterator();
  while(e1.hasNext() && e2.hasNext())
  {
   if(e1.next() != e2.next())
    return false;
  }
  return !(e1.hasNext() || e2.hasNext());
 }

 public int hashCode()
 {
  long hashCode = 1;
  LongListIterator i = listIterator();

  while(i.hasNext())
  {
   long obj = i.next();
   hashCode = 31 * hashCode + obj;
  }
  return (int)hashCode;
 }

 public boolean remove(long o)
 {
  LongListIterator e = listIterator();

  while(e.hasNext())
  {
   if(o == e.next())
   {
    e.remove();
    return true;
   }
  }

  return false;
 }

 protected void removeRange(int fromIndex, int toIndex)
 {
  LongListIterator it = listIterator(fromIndex);
  for(int i = 0, n = toIndex - fromIndex; i < n; i++)
  {
   it.next();
   it.remove();
  }
 }

 protected transient int modCount = 0;
}

class LongSubList extends AbstractLongList {
 private AbstractLongList l;
 private int offset;
 private int size;
 private int expectedModCount;

 LongSubList(AbstractLongList list, int fromIndex, int toIndex) {
     if (fromIndex < 0)
         throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
     if (toIndex > list.size())
         throw new IndexOutOfBoundsException("toIndex = " + toIndex);
     if (fromIndex > toIndex)
         throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                            ") > toIndex(" + toIndex + ")");
     l = list;
     offset = fromIndex;
     size = toIndex - fromIndex;
     expectedModCount = l.modCount;
 }

 public long set(int index, long element) {
     rangeCheck(index);
     checkForComodification();
     return l.set(index+offset, element);
 }

 public long get(int index) {
     rangeCheck(index);
     checkForComodification();
     return l.get(index+offset);
 }

 public int size() {
     checkForComodification();
     return size;
 }

 public void add(int index, long element) {
     if (index<0 || index>size)
         throw new IndexOutOfBoundsException();
     checkForComodification();
     l.add(index+offset, element);
     expectedModCount = l.modCount;
     size++;
     modCount++;
 }

 public long removeAt(int index) {
     rangeCheck(index);
     checkForComodification();
     long result = l.removeAt(index+offset);
     expectedModCount = l.modCount;
     size--;
     modCount++;
     return result;
 }

 protected void removeRange(int fromIndex, int toIndex) {
     checkForComodification();
     l.removeRange(fromIndex+offset, toIndex+offset);
     expectedModCount = l.modCount;
     size -= (toIndex-fromIndex);
     modCount++;
 }

 public boolean addAll(Collection<Long> c) {
     return addAll(size, c);
 }

 public boolean addAll(int index, Collection<Long> c) {
     if (index<0 || index>size)
         throw new IndexOutOfBoundsException(
             "Index: "+index+", Size: "+size);
     int cSize = c.size();
     if (cSize==0)
         return false;

     checkForComodification();
     l.addAll(offset+index, c);
     expectedModCount = l.modCount;
     size += cSize;
     modCount++;
     return true;
 }

 public Iterator<Long> iterator() {
  checkForComodification();

  return new Iterator<Long>() {
      private LongListIterator i = l.listIterator(offset);

      public boolean hasNext() {
          return nextIndex() < size;
      }

      public Long next() {
          if (hasNext())
              return i.next();
          else
              throw new NoSuchElementException();
      }


      public int nextIndex() {
          return i.nextIndex() - offset;
      }

      public void remove() {
          i.remove();
          expectedModCount = l.modCount;
          size--;
          modCount++;
      }

  };
 }

 public LongListIterator listIterator(final int index) {
     checkForComodification();
     if (index<0 || index>size)
         throw new IndexOutOfBoundsException(
             "Index: "+index+", Size: "+size);

     return new LongListIterator() {
         private LongListIterator i = l.listIterator(index+offset);

         public boolean hasNext() {
             return nextIndex() < size;
         }

         public long next() {
             if (hasNext())
                 return i.next();
             else
                 throw new NoSuchElementException();
         }

         public boolean hasPrevious() {
             return previousIndex() >= 0;
         }

         public long previous() {
             if (hasPrevious())
                 return i.previous();
             else
                 throw new NoSuchElementException();
         }

         public int nextIndex() {
             return i.nextIndex() - offset;
         }

         public int previousIndex() {
             return i.previousIndex() - offset;
         }

         public void remove() {
             i.remove();
             expectedModCount = l.modCount;
             size--;
             modCount++;
         }

         public void set(long o) {
             i.set(o);
         }

         public void add(long o) {
             i.add(o);
             expectedModCount = l.modCount;
             size++;
             modCount++;
         }
     };
 }

 public LongList subList(int fromIndex, int toIndex) {
     return new LongSubList(this, fromIndex, toIndex);
 }

 private void rangeCheck(int index) {
     if (index<0 || index>=size)
         throw new IndexOutOfBoundsException("Index: "+index+
                                             ",Size: "+size);
 }

 private void checkForComodification() {
     if (l.modCount != expectedModCount)
         throw new ConcurrentModificationException();
 }
}

class LongRandomAccessSubList extends LongSubList implements RandomAccess {
 LongRandomAccessSubList(AbstractLongList list, int fromIndex, int toIndex) {
     super(list, fromIndex, toIndex);
 }

 public LongList subList(int fromIndex, int toIndex) {
     return new LongRandomAccessSubList(this, fromIndex, toIndex);
 }
}
