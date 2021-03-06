package com.heaton.funnyvote.database;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "OPTION".
*/
public class OptionDao extends AbstractDao<Option, Long> {

    public static final String TABLENAME = "OPTION";

    /**
     * Properties of entity Option.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property VoteCode = new Property(1, String.class, "voteCode", false, "VOTE_CODE");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Count = new Property(3, Integer.class, "count", false, "COUNT");
        public final static Property Code = new Property(4, String.class, "code", false, "CODE");
        public final static Property IsUserChoiced = new Property(5, boolean.class, "isUserChoiced", false, "IS_USER_CHOICED");
    }

    private Query<Option> voteData_OptionsQuery;

    public OptionDao(DaoConfig config) {
        super(config);
    }
    
    public OptionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"OPTION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"VOTE_CODE\" TEXT," + // 1: voteCode
                "\"TITLE\" TEXT," + // 2: title
                "\"COUNT\" INTEGER," + // 3: count
                "\"CODE\" TEXT," + // 4: code
                "\"IS_USER_CHOICED\" INTEGER NOT NULL );"); // 5: isUserChoiced
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"OPTION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Option entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String voteCode = entity.getVoteCode();
        if (voteCode != null) {
            stmt.bindString(2, voteCode);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(4, count);
        }
 
        String code = entity.getCode();
        if (code != null) {
            stmt.bindString(5, code);
        }
        stmt.bindLong(6, entity.getIsUserChoiced() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Option entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String voteCode = entity.getVoteCode();
        if (voteCode != null) {
            stmt.bindString(2, voteCode);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(4, count);
        }
 
        String code = entity.getCode();
        if (code != null) {
            stmt.bindString(5, code);
        }
        stmt.bindLong(6, entity.getIsUserChoiced() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Option readEntity(Cursor cursor, int offset) {
        Option entity = new Option( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // voteCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // count
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // code
            cursor.getShort(offset + 5) != 0 // isUserChoiced
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Option entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setVoteCode(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCount(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsUserChoiced(cursor.getShort(offset + 5) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Option entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Option entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Option entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "options" to-many relationship of VoteData. */
    public List<Option> _queryVoteData_Options(String voteCode) {
        synchronized (this) {
            if (voteData_OptionsQuery == null) {
                QueryBuilder<Option> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.VoteCode.eq(null));
                queryBuilder.orderRaw("T.'_id' ASC");
                voteData_OptionsQuery = queryBuilder.build();
            }
        }
        Query<Option> query = voteData_OptionsQuery.forCurrentThread();
        query.setParameter(0, voteCode);
        return query.list();
    }

}
