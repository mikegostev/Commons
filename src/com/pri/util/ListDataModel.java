/*
 * Created on Nov 28, 2005
 */
package com.pri.util;

import javax.swing.ListModel;

import com.pri.util.collection.ListListener;

public interface ListDataModel<T> extends ListModel{

	public void addListListener (ListListener<T> listener) ;
	public void removeListListener (ListListener<T> listener) ;
}
