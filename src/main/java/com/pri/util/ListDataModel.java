/*
 * Created on Nov 28, 2005
 */
package com.pri.util;

import com.pri.util.collection.ListListener;
import javax.swing.ListModel;

public interface ListDataModel<T> extends ListModel {

    public void addListListener(ListListener<T> listener);

    public void removeListListener(ListListener<T> listener);
}
