package com.pri.util.sql;

import com.pri.log.Log;
import com.pri.util.DateInterval;
import com.pri.util.FloatInterval;
import com.pri.util.Interval;
import com.pri.util.StringUtils;
import com.pri.util.collection.IntIterator;
import com.pri.util.collection.IntegerPool;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class SQLUtils {

    private SQLUtils() {
    }

    public static <T extends DataObject> List<T> select(Connection conn, DataObject<T> reqBean, Field srt, boolean asc)
            throws SQLException {
        Statement stmt = conn.createStatement();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT * FROM `").append(reqBean.getTypeName()).append("` WHERE TRUE");
        dataObjectToSQLCond(sb, reqBean, null);

        if (srt != null) {
            sb.append(" ORDER BY `").append(srt.getName()).append("` ").append(asc ? "ASC" : "DESC");
        }

        List<T> res = new ArrayList<T>();

        ResultSet rst = stmt.executeQuery(sb.toString());

        try {
            while (rst.next()) {
                T bn = reqBean.newInstance();

                for (Field f : reqBean.getFields()) {
                    bn.setFieldValue(f, rst.getObject(f.getName()));
                }

                res.add(bn);

            }
        } finally {
            if (rst != null) {
                rst.close();
            }
        }
        return res;
    }

    public static <T extends DataObject> void delete(Connection conn, DataObject<T> cond) throws SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM `").append(cond.getTypeName()).append("` WHERE TRUE");
        dataObjectToSQLCond(sb, cond, null);

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sb.toString());
    }


    public static <T extends DataObject> void update(Connection conn, DataObject<T> data, DataObject<T> cond)
            throws SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE `").append(data.getTypeName()).append("` SET");

        for (Field f : data.getFields()) {
            if (data.isSet(f)) {
                sb.append(" `").append(f.getName()).append("`=?,");
            }
        }

        sb.setCharAt(sb.length() - 1, ' ');
        sb.append("WHERE TRUE");

        dataObjectToSQLCond(sb, cond, null);

        PreparedStatement stmt = conn.prepareStatement(sb.toString());

        int i = 1;
        for (Field f : data.getFields()) {
            if (data.isSet(f)) {
                stmt.setObject(i++, data.getFieldValue(f));
            }
        }

        stmt.executeUpdate();
    }

    public static <T extends DataObject> int insert(Connection conn, DataObject<T> data) throws SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO `").append(data.getTypeName()).append("` (");

        int n = 0;
        for (Field f : data.getFields()) {
            if (data.isSet(f)) {
                sb.append("`").append(f.getName()).append("`,");
                n++;
            }
        }

        sb.setCharAt(sb.length() - 1, ')');
        sb.append(" VALUES (");

        for (int i = 0; i < n; i++) {
            sb.append("?,");
        }
        sb.setCharAt(sb.length() - 1, ')');

        PreparedStatement stmt = conn.prepareStatement(sb.toString());

        int i = 1;
        for (Field f : data.getFields()) {
            if (data.isSet(f)) {
                stmt.setObject(i++, data.getFieldValue(f));
            }
        }

        stmt.executeUpdate();

        ResultSet rst = stmt.getGeneratedKeys();

        try {
            if (rst.next()) {
                return rst.getInt(1);
            }
        } finally {
            if (rst != null) {
                rst.close();
            }
        }

        return -1;
    }

    public static <T> List<T> select(Connection conn, String tbl, Class<T> beanClass, Object reqBean)
            throws SQLException {
        Statement stmt = conn.createStatement();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT * FROM `").append(tbl).append("` WHERE TRUE");
        beanToSQLCond(sb, reqBean, null);

        List<T> res = new ArrayList<T>();

        ResultSet rst = stmt.executeQuery(sb.toString());

        try {

            Method[] mthds = beanClass.getMethods();

            while (rst.next()) {
                T bn = null;
                try {
                    bn = beanClass.newInstance();
                } catch (Throwable e) {
                    Log.warn("Can't instantain object class: " + beanClass.getName(), e);
                }

                for (int i = 0; i < mthds.length; i++) {
                    if (!mthds[i].getName().startsWith("set")) {
                        continue;
                    }

                    try {
                        mthds[i].invoke(bn, new Object[]{rst.getObject(mthds[i].getName().substring(3))});
                    } catch (Throwable e) {
                        Log.warn("Can't invoke method: " + mthds[i].getName() + " for class " + beanClass.getName(), e);
                    }

                    // Class< ? >[] prms = mthds[i].getParameterTypes();
                    //
                    // if(prms.length != 1)
                    // continue;
                    //
                    // try
                    // {
                    // if(prms[0] == String.class)
                    // mthds[i].invoke(bn, new Object[] {
                    // rst.getString(mthds[i].getName().substring(3)) });
                    // else if( prms[0] == int.class )
                    // mthds[i].invoke(bn, new Object[] {
                    // rst.getInt(mthds[i].getName().substring(3)) });
                    // else if( prms[0] == long.class )
                    // mthds[i].invoke(bn, new Object[] {
                    // rst.getLong(mthds[i].getName().substring(3)) });
                    // else if( prms[0] == float.class )
                    // mthds[i].invoke(bn, new Object[] {
                    // rst.getFloat(mthds[i].getName().substring(3)) });
                    // }
                    // catch(Exception e)
                    // {
                    // Log.warn("Can't invoke method: " + mthds[i].getName() + " for class " +
                    // beanClass.getName());

                    res.add(bn);
                }

            }
        } finally {
            if (rst != null) {
                rst.close();
            }
        }
        return res;
    }


    public static StringBuilder beanToSQLCond(StringBuilder sb, Object reqBean, String tbl) {

        Method[] mthds = reqBean.getClass().getMethods();

        for (int i = 0; i < mthds.length; i++) {
            String nm = mthds[i].getName();

            if (nm.startsWith("get")) {
                String fldName = nm.substring(3);

                Object value = null;
                try {
                    value = mthds[i].invoke(reqBean, (Object[]) null);
                } catch (Exception e) {
                    Log.warn("Can't invoke method '" + nm + "' of object of class " + reqBean.getClass()
                            .getCanonicalName());
                }

                if (value == null) {
                    continue;
                }

                if (value instanceof String) {
                    sb.append(" AND `");

                    if (tbl != null) {
                        sb.append(tbl).append("`.`");
                    }

                    sb.append(fldName);

                    if (((String) value).length() == 0) {
                        sb.append("` IS NULL");
                    } else {
                        sb.append("` REGEXP '");
                        StringUtils.appendBackslashed(sb, (String) value, '\'');
                        sb.append('\'');
                    }
                } else if (value instanceof Collection) {
                    Collection<?> coll = (Collection) value;

                    if (coll.size() == 0) {
                        continue;
                    }

                    int pos = sb.length();

                    sb.append(" AND `");

                    if (tbl != null) {
                        sb.append(tbl).append("`.`");
                    }

                    sb.append(fldName).append("` IN (");

                    boolean hasIvals = false;

                    for (Object ob : coll) {
                        if (ob instanceof Integer) {
                            sb.append(((Integer) ob).intValue()).append(',');
                        } else if (!hasIvals && ob instanceof Interval) {
                            hasIvals = true;
                        }
                    }

                    if (sb.charAt(sb.length() - 1) == ',') {
                        sb.setCharAt(sb.length() - 1, ')');
                    } else {
                        sb.setLength(pos);
                    }

                    if (hasIvals) {
                        for (Object ob : coll) {
                            if (ob instanceof Interval) {
                                sb.append(" AND `");

                                if (tbl != null) {
                                    sb.append(tbl).append("`.`");
                                }

                                sb.append(fldName).append("` BETWEEN ").append(((Interval) ob).getBegin())
                                        .append(" AND ").append(((Interval) ob).getEnd());
                            }
                        }
                    }

                } else if (value instanceof DateInterval) {
                    sb.append(" AND `");

                    if (tbl != null) {
                        sb.append(tbl).append("`.`");
                    }

                    sb.append(fldName).append("` BETWEEN ").append(((DateInterval) value).getBeginTimestamp())
                            .append(" AND ").append(((DateInterval) value).getEndTimestamp());

                }

            }
        }

        return sb;
    }

    public static <T extends DataObject> StringBuilder dataObjectToSQLCond(StringBuilder sb, DataObject<T> reqBean,
            String tbl) {

        if (reqBean == null) {
            return sb;
        }

        for (Field f : reqBean.getFields()) {
            if (!reqBean.isSet(f)) {
                continue;
            }

            Object value = reqBean.getFieldValue(f);

            if (value == null) {
                sb.append(" AND `");

                if (tbl != null) {
                    sb.append(tbl).append("`.`");
                }

                sb.append(f.getName()).append("` IS NULL");
            } else if (value instanceof Boolean) {
                sb.append(" AND `");

                if (tbl != null) {
                    sb.append(tbl).append("`.`");
                }

                sb.append(f.getName()).append("`=").append(((Boolean) value).booleanValue() ? 1 : 0);

            } else if (value instanceof Interval) {
                sb.append(" AND `");

                if (tbl != null) {
                    sb.append(tbl).append("`.`");
                }

                sb.append(f.getName());

                sb.append("` BETWEEN ").append(((Interval) value).getBegin()).append(" AND ")
                        .append(((Interval) value).getEnd());
            } else if (value instanceof Pattern) {
                sb.append(" AND `");

                if (tbl != null) {
                    sb.append(tbl).append("`.`");
                }

                sb.append(f.getName());

                sb.append("` REGEXP '");
                StringUtils.appendBackslashed(sb, ((Pattern) value).pattern(), '\'');
                sb.append('\'');
            } else if (value instanceof IntegerPool) {
                IntegerPool pool = (IntegerPool) value;

                if (pool.countSingles() != 0) {
                    sb.append(" AND `");

                    if (tbl != null) {
                        sb.append(tbl).append("`.`");
                    }

                    sb.append(f.getName()).append("` IN (");

                    IntIterator iitr = pool.getValues().listIterator();

                    while (iitr.hasNext()) {
                        sb.append(iitr.next()).append(',');
                    }

                    sb.setCharAt(sb.length() - 1, ')');
                }

                if (pool.countIntervals() != 0) {
                    for (Interval ob : pool.getIntervals()) {
                        sb.append(" AND `");

                        if (tbl != null) {
                            sb.append(tbl).append("`.`");
                        }

                        sb.append(f.getName()).append("` BETWEEN ").append(ob.getBegin()).append(" AND ")
                                .append(ob.getEnd());
                    }
                }

            } else if (value instanceof DateInterval) {
                sb.append(" AND `");

                if (tbl != null) {
                    sb.append(tbl).append("`.`");
                }

                sb.append(f.getName()).append("` BETWEEN ").append(((DateInterval) value).getBeginTimestamp())
                        .append(" AND ").append(((DateInterval) value).getEndTimestamp());

            } else if (value instanceof FloatInterval) {
                sb.append(" AND `");

                if (tbl != null) {
                    sb.append(tbl).append("`.`");
                }

                sb.append(f.getName()).append("` BETWEEN ").append(((FloatInterval) value).getBegin()).append(" AND ")
                        .append(((FloatInterval) value).getEnd());

            } else {
                sb.append(" AND `");

                if (tbl != null) {
                    sb.append(tbl).append("`.`");
                }

                sb.append(f.getName()).append("`='");
                StringUtils.appendBackslashed(sb, value.toString(), '\'').append('\'');
            }

        }

        return sb;
    }

}
