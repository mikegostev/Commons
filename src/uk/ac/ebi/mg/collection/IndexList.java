package uk.ac.ebi.mg.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class IndexList<NType, T extends Named<NType>> extends ArrayList<T>
{

 private static final long serialVersionUID = 1L;

 private Comparator<NType> comparator;
 
 private Comparator<T> elComparator;
 
 public IndexList( Comparator<NType> cmp )
 {
  comparator = cmp;
  
  elComparator = new Comparator<T>()
  {
   @Override
   public int compare(T o1, T o2)
   {
    return comparator.compare(o1.getId(), o2.getId());
   }
  };
  
 }
 
 public T get( NType key )
 {
  int low = 0;
  int high = size() - 1;

  while(low <= high)
  {
   int mid = (low + high) >>> 1;
   T midVal = get(mid);
   
   int cmp = comparator.compare(key, midVal.getId());

   if(cmp > 0)
    low = mid + 1;
   else if(cmp < 0)
    high = mid - 1;
   else
    return midVal; // key found
  }
  
  return null; // key not found
 }
 
 @Override
 public boolean add( T el )
 {
  boolean res = super.add(el);
  
  Collections.sort(this, elComparator);
  
  return res;
 }
 
 @Override
 public boolean addAll( Collection<? extends T> els )
 {
  boolean res = super.addAll(els);
  
  Collections.sort(this, elComparator);
  
  return res;
 }
 
 @Override
 public T set(int index, T element)
 {
  throw new UnsupportedOperationException();
 }
 
 @Override
 public void add( int ind, T el )
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public boolean addAll( int ind, Collection<? extends T> els )
 {
  throw new UnsupportedOperationException();
 }

}
