package example.com.erp.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends OrmLiteSqliteOpenHelper {

    // Fields
    private static final String DB_NAME = "cart_manager.db";
    private static final int DB_VERSION = 1;

    // Public methods

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        getWritableDatabase();
    }

    public static HashMap<String, Object> where(String aVar, Object aValue) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(aVar, aValue);
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
        try {
            TableUtils.createTable(cs, CartItem.class);
        } catch (SQLException | java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, CartItem.class, true);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        onCreate(db, connectionSource);
    }

    public <T> List<T> getAll(Class<T> clazz) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
            return dao.queryForAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<T> getAllOrdered(Class<T> clazz, String orderBy, boolean ascending) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
            return dao.queryBuilder().orderBy(orderBy, ascending).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> void fillObject(Class<T> clazz, T aObj) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
            dao.createOrUpdate(aObj);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> void fillObjects(Class<T> clazz, Collection<T> aObjList) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
            for (T obj : aObjList) {
                dao.createOrUpdate(obj);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public <T> T getById(Class<T> clazz, Object aId) throws SQLException {
        Dao<T, Object> dao = null;
        try {
            dao = getDao(clazz);
            return dao.queryForId(aId);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T getBySelected(Class<T> clazz, String fieldName, Object aId) throws SQLException {
        Dao<T, Object> dao = null;
        try {
            dao = getDao(clazz);
            return (T) dao.queryBuilder().where()
                    .eq(fieldName, aId)
                    .queryForFirst();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<T> query(Class<T> clazz, Map<String, Object> aMap) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
            return dao.queryForFieldValues(aMap);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCartCount(String userId) {
        Dao<CartItem, ?> dao;
        try {
            dao = getDao(CartItem.class);
            return (int) dao.queryBuilder().where().eq("user_id", userId).countOf();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public <T> List<T> queryNot(Class<T> clazz, String columnName, int value) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
            return dao.queryBuilder().where().ne(columnName, value).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T queryFirst(Class<T> clazz, Map<String, Object> aMap) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = getDao(clazz);
            List<T> list = dao.queryForFieldValues(aMap);
            if (list.size() > 0)
                return list.get(0);
            else
                return null;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> Dao.CreateOrUpdateStatus createOrUpdate(T obj) throws SQLException {
        Dao<T, ?> dao = null;
        try {
            dao = (Dao<T, ?>) getDao(obj.getClass());
            return dao.createOrUpdate(obj);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public <T> int deleteById(Class<T> clazz, Object aId) throws SQLException {
        try {
            Dao<T, Object> dao = getDao(clazz);
            return dao.deleteById(aId);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public <T> int deleteObjects(Class<T> clazz, Collection<T> aObjList) throws SQLException {

        try {
            Dao<T, ?> dao = getDao(clazz);
            return dao.delete(aObjList);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public <T> void deleteAll(Class<T> clazz) throws SQLException {
        try {
            Dao<T, ?> dao = getDao(clazz);
            dao.deleteBuilder().delete();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}