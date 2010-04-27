/*
 * Created on 01.04.2006
 */
package com.pri.util.collection;

import java.util.List;

import com.pri.util.ListDataModel;

public interface DataList<T> extends List<T>, ListDataModel<T>{
	public T getByID (int id) ;

}
