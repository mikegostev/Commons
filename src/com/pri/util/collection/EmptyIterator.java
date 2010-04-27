/*
 * Created on 29.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util.collection;

import java.util.Iterator;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class EmptyIterator<T> implements Iterator<T>
{

 public void remove()
 {
  // TODO Auto-generated method stub

 }

 /* (non-Javadoc)
  * @see java.util.Iterator#hasNext()
  */
 public boolean hasNext()
 {
  return false;
 }

 /* (non-Javadoc)
  * @see java.util.Iterator#next()
  */
 public T next()
 {
  return null;
 }

}
