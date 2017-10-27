/*
 * Created on 01.04.2006
 */
package com.pri.util.collection;

import com.pri.util.ListDataModel;
import java.util.List;

public interface DataList<T> extends List<T>, ListDataModel<T> {

    public T getByID(int id);

}
