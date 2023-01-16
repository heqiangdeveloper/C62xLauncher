package card.db;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import card.base.LauncherCard;
import card.service.R;
import launcher.base.async.AsyncSchedule;
import launcher.base.utils.EasyLog;

public class CardDataBaseService {
    private static final String TAG = "CardDataBaseService";
    private AppDataBase mDataBase;
    private Context mContext;
    private static final String TABLE_NAME = "LauncherCard";
    private static final String DB_NAME = "card_db";
    private final Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    public CardDataBaseService(Context context) {
        mContext = context;
        createDataBase(mContext);
    }

    private void createDataBase(Context context) {
        if (mDataBase == null) {
            mDataBase = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            EasyLog.i(TAG, "MIGRATION_1_2 migrate. " + Thread.currentThread().getName());
            try {
                moveTable(database);
            } catch (Exception e) {
                EasyLog.w(TAG, "MIGRATION_1_2 migrate exception, an empty table should be created soon.");
                e.printStackTrace();
                createNewTable(database);
            }
        }
    };

    private static void createNewTable(SupportSQLiteDatabase database) {
        EasyLog.d(TAG, "moveTable");
        String tempTableName = "temp_table";
        database.execSQL("drop table if exists " + TABLE_NAME);
        database.execSQL("drop table if exists " + tempTableName);
        database.execSQL("create table " + TABLE_NAME +
                " ( 'position' INTEGER NOT NULL, 'inHome' INTEGER NOT NULL, 'name' TEXT, 'type' INTEGER PRIMARY KEY NOT NULL, 'canExpand' INTEGER NOT NULL, " +
                "'mSelectBgResName' TEXT, 'mUnselectBgResName' TEXT)");
    }

    private static void moveTable(SupportSQLiteDatabase database) {
        EasyLog.d(TAG, "moveTable");
        String tempTableName = "temp_table";
        database.execSQL("create table " + tempTableName +
                " ( 'position' INTEGER NOT NULL, 'inHome' INTEGER NOT NULL, 'name' TEXT, 'type' INTEGER PRIMARY KEY NOT NULL, 'canExpand' INTEGER NOT NULL, " +
                "'mSelectBgResName' TEXT, 'mUnselectBgResName' TEXT)");
        // 此处发生过type唯一主键约束失败异常, 因此从insert 改为replace
        database.execSQL("replace into temp_table (position, inHome, name ,type ,canExpand ,mSelectBgResName, mUnselectBgResName)" +
                " select position, inHome, name ,type ,canExpand ,mSelectBgResName, mUnselectBgResName from " + TABLE_NAME);
        database.execSQL("drop table  if exists " + TABLE_NAME);
        database.execSQL("alter table " + tempTableName + " rename to " + TABLE_NAME);
    }


    private List<LauncherCard> getAllCardsSync() {
        EasyLog.d(TAG, "getAllCardsSync , mDataBase:" + mDataBase);
        if (mDataBase == null) {
            createDataBase(mContext);
        }
        try {
            CardDao dao = mDataBase.cardDao();
            List<LauncherCard> all = dao.getAll();
            EasyLog.d(TAG, "getAllCardsSync , finish :" + all);
            return all;
        } catch (Exception e) {
            EasyLog.w(TAG, "getAllCardsSync fail:");
            e.printStackTrace();
            // 发生过Migrate时,  insert 失败的异常: 唯一主键约束失败
            return new LinkedList<>();
        }
    }


    public void getAllCards(IQueryListener queryListener) {
        EasyLog.d(TAG, "getAllCards");
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                List<LauncherCard> allCardsSync = getAllCardsSync();
                if (allCardsSync == null) {
                    allCardsSync = new LinkedList<>();
                }
                List<LauncherCard> finalAllCardsSync = allCardsSync;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        queryListener.onSuccess(finalAllCardsSync);
                    }
                });
            }
        });
    }

    public void saveCards(List<LauncherCard> launcherCards) {
        if (mDataBase == null) {
            createDataBase(mContext);
        }
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                EasyLog.d(TAG, "saveCards :" + launcherCards);
                if (launcherCards == null || launcherCards.isEmpty()) {
                    return;
                }
                CardDao dao = mDataBase.cardDao();
                LauncherCard[] arr = new LauncherCard[launcherCards.size()];
                launcherCards.toArray(arr);
                dao.insertAll(arr);
            }
        });
    }

    public void updateCards(List<LauncherCard> launcherCards) {
        if (mDataBase == null) {
            createDataBase(mContext);
        }
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                EasyLog.d(TAG, "updateCards :" + launcherCards);
                if (launcherCards == null || launcherCards.isEmpty()) {
                    return;
                }
                CardDao dao = mDataBase.cardDao();
                LauncherCard[] arr = new LauncherCard[launcherCards.size()];
                launcherCards.toArray(arr);
                dao.update(arr);
            }
        });
    }

    public void updateCards(LauncherCard... cards) {
        if (mDataBase == null) {
            createDataBase(mContext);
        }
        AsyncSchedule.execute(new Runnable() {
            @Override
            public void run() {
                CardDao dao = mDataBase.cardDao();
                dao.update(cards);
            }
        });
    }

}
