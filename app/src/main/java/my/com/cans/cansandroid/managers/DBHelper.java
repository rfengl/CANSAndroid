package my.com.cans.cansandroid.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.com.cans.cansandroid.activities.BaseActivity;
import my.com.cans.cansandroid.objects.MyApp;
import my.com.cans.cansandroid.objects.dbo.DBOBase;
import my.com.cans.cansandroid.objects.dbo.T_User;
import my.com.cans.cansandroid.objects.interfaces.AutoIncrement;
import my.com.cans.cansandroid.objects.interfaces.PrimaryKey;

/**
 * Created by Rfeng on 04/04/2017.
 */
public class DBHelper extends SQLiteOpenHelper {
    protected Context mContext;

    public DBHelper(Context context) {
        super(initContext(context), "CarFixDB", null, 14);
        mContext = context;
    }

    private static Context initContext(Context context) {
        if (context == null)
            return MyApp.getContext();
        else
            return context;
    }

    public List<Class> getTableClasses() {
        List<Class> list = new ArrayList<Class>();
        list.add(T_User.class);
        return list;
    }

    private void createTable(SQLiteDatabase db, Class tableClass) {
        String tableName = tableClass.getSimpleName();
        List<String> columns = new ArrayList<String>();
        for (Field field : tableClass.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()))
                columns.add(field.getName() + " " + getDBTypeName(field));
        }
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " " +
                "(" + TextUtils.join(", ", columns) + ");");
    }

    private String getDBTypeName(Field field) {
        String fieldType, typeName = field.getType().getSimpleName();
        switch (typeName) {
            case "String":
                fieldType = "VARCHAR";
                break;
            default:
                fieldType = "VARCHAR";
                break;
            case "Integer":
            case "int":
                fieldType = "INTEGER";
                break;
            case "Double":
            case "double":
                fieldType = "DOUBLE";
                break;
            case "Float":
            case "float":
                fieldType = "FLOAT";
                break;
            case "Boolean":
            case "boolean":
                fieldType = "BOOLEAN";
                break;
            case "Date":
                fieldType = "DATE";
                break;
        }

        Annotation notNull = field.getAnnotation(NonNull.class);
        if (notNull != null ||
                typeName == "int" ||
                typeName == "float" ||
                typeName == "double" ||
                typeName == "boolean")
            fieldType += " NOT NULL";
        else
            fieldType += " NULL";

        Annotation primaryKey = field.getAnnotation(PrimaryKey.class);
        if (primaryKey != null)
            fieldType += " PRIMARY KEY";

        Annotation autoIncrement = field.getAnnotation(AutoIncrement.class);
        if (autoIncrement != null)
            fieldType += " AUTOINCREMENT";

        return fieldType;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<DBOBase> items = new ArrayList<DBOBase>();
        for (Class tableClass : getTableClasses()) {
            if (isTableExists(tableClass.getSimpleName(), db)) {
                for (Object item : this.select(db, tableClass, null, null)) {
                    items.add((DBOBase) item);
                }
                dropTable(db, tableClass);
            }
        }
        onCreate(db);

        for (DBOBase item : items)
            insert(db, item);
    }

    public void onCreate(SQLiteDatabase db) {
        for (Class tableClass : getTableClasses())
            createTable(db, tableClass);
    }

    private void dropTable(SQLiteDatabase db, Class tableClass) {
        String tableName = tableClass.getSimpleName();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public boolean insert(DBOBase item) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = insert(db, item);
        db.close();
        return result;
    }

    private boolean insert(SQLiteDatabase db, DBOBase item) {
        try {
            if (item instanceof DBOBase) {
                Class tableType = item.getClass();
                String name = tableType.getSimpleName();
                ContentValues contentValues = new ContentValues();
                Field[] fields = tableType.getFields();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers()))
                        continue;

                    Annotation autoIncrement = field.getAnnotation(AutoIncrement.class);
                    if (autoIncrement != null) continue;

                    String columnName = field.getName();
                    Object value = field.get(item);
                    if (value != null) {
                        AddToContentValues(field, contentValues, value);
                    }
                }
                db.insert(name, null, contentValues);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void AddToContentValues(Field field, ContentValues contentValues, Object value) {
        String fieldName = field.getName(), typeName = field.getType().getSimpleName();
        if (value == null)
            contentValues.putNull(fieldName);

        switch (typeName) {
            case "String":
                contentValues.put(fieldName, value.toString());
                break;
            case "Integer":
            case "int":
                contentValues.put(fieldName, (Integer) value);
                break;
            case "Double":
            case "double":
                contentValues.put(fieldName, (Double) value);
                break;
            case "Float":
            case "float":
                contentValues.put(fieldName, (Float) value);
                break;
            case "Boolean":
            case "boolean":
                contentValues.put(fieldName, (Boolean) value);
                break;
            case "Date":
                contentValues.put(fieldName, ((Date) value).getTime());
                break;
            default:
                contentValues.put(fieldName, value.toString());
                break;
        }
    }

    private boolean isTableExists(String tableName, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public <S> S selectExact(Class<S> tableClass, int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + tableClass.getSimpleName() + " where id=" + id + "", null);
        res.moveToFirst();
        S item = buildData(tableClass, res);

        res.close();
        db.close();

        return item;
    }

    private <S> S buildData(Class<S> tableClass, Cursor res) {
        try {
            S item = tableClass.newInstance();
            for (Field field : tableClass.getFields()) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;

                int index = res.getColumnIndex(field.getName());
                field.set(item, convertObject(field, res, index));
            }
            return item;
        } catch (Exception ex) {
            return null;
        }
    }

    private Object convertObject(Field field, Cursor cursor, int index) {
        String typeName = field.getType().getSimpleName();
        if (cursor.isNull(index))
            return null;

        switch (typeName) {
            case "String":
                return cursor.getString(index);
            default:
                return cursor.getString(index);
            case "int":
            case "Integer":
                return cursor.getInt(index);
            case "double":
            case "Double":
            case "float":
            case "Float":
                return cursor.getDouble(index);
            case "boolean":
            case "Boolean":
                Integer boolInt = cursor.getInt(index);
                if (boolInt == null)
                    return null;
                return boolInt == 1;
            case "Date":
                Long dateLong = cursor.getLong(index);
                return new Date(dateLong);
        }
    }

//    private String convertDBParam(Object item) {
//        if (item == (int) item ||
//                item == (double) item)
//            return item.toString();
//        else
//
//            return "'" + item.toString() + "'";
//    }

    public <S> int numberOfRows(Class<S> tableClass) {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, tableClass.getSimpleName());
        db.close();
        return numRows;
    }

    public boolean update(DBOBase item) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Class tableType = item.getClass();
            String name = tableType.getSimpleName();
            ContentValues contentValues = new ContentValues();
            Field[] fields = tableType.getFields();
            ArrayList<String> primaryKeys = new ArrayList<String>();
            ArrayList<String> primaryKeyValues = new ArrayList<String>();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;

                Annotation primaryKey = field.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    primaryKeys.add(field.getName() + " = ?");
                    primaryKeyValues.add(field.get(item).toString());
                    continue;
                }

                Object value = field.get(item);
                if (value != null)
                    AddToContentValues(field, contentValues, value);
            }

            if (primaryKeys.size() == 0)
                return false;
            db.update(name, contentValues, TextUtils.join(" and ", primaryKeys), primaryKeyValues.toArray(new String[primaryKeyValues.size()]));
        } catch (Exception ex) {
            return false;
        } finally {
            db.close();
        }

        return true;
    }

    public Integer delete(DBOBase item) {
        Class tableType = item.getClass();
        String tableName = tableType.getSimpleName();

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> primaryKeys = new ArrayList<String>();
        ArrayList<String> primaryKeyValues = new ArrayList<String>();
        try {
            for (Field field : tableType.getFields()) {
                Annotation primaryKey = field.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    primaryKeys.add(field.getName() + " = ?");
                    primaryKeyValues.add(field.get(item).toString());
                }
            }
            return db.delete(tableName,
                    TextUtils.join(" and ", primaryKeys),
                    primaryKeyValues.toArray(new String[primaryKeyValues.size()]));
        } catch (Exception ex) {
            message(ex.getMessage());
            return 0;
        } finally {
            db.close();
        }
    }

    private void message(String msg) {
        if (this.mContext instanceof BaseActivity)
            ((BaseActivity) this.mContext).message(msg);
    }

    public <S> S selectSingle(Class<S> tableClass) {
        return selectSingle(tableClass, "");
    }

    public <S> ArrayList<S> select(Class<S> tableClass) {
        return select(tableClass, "");
    }

    public <S> S selectSingle(Class<S> tableClass, String whereClause, Object... selectionArgs) {
        ArrayList<S> items = select(tableClass, whereClause, selectionArgs);
        if (items.size() > 0)
            return items.get(0);
        else
            return null;
    }

    public <S> ArrayList<S> select(Class<S> tableClass, String whereClause, Object... selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<S> list = select(db, tableClass, whereClause, selectionArgs);
        db.close();
        return list;
    }

    private <S> ArrayList<S> select(SQLiteDatabase db, Class<S> tableClass, String whereClause, Object[] selectionArgs) {
        String tableName = tableClass.getSimpleName();
        ArrayList<S> array_list = new ArrayList<S>();

        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();

        String where = "";
        if (whereClause != null && whereClause.isEmpty() == false) {
            where = " where " + whereClause;
        } else
            where = "";

        ArrayList<String> args = new ArrayList<String>();
        if (selectionArgs != null)
            for (Object arg : selectionArgs) {
                if (arg == null)
                    args.add("null");
                else if (arg instanceof Date) {
                    Long date = ((Date) arg).getTime();
                    args.add(date.toString());
                } else
                    args.add(arg.toString());
            }

        Cursor res = db.rawQuery("select * from " + tableName + where, args.toArray(new String[args.size()]));
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(buildData(tableClass, res));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {
                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}

