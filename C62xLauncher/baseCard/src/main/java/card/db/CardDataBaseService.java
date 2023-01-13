package card.db;

import android.content.Context;
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
    private final String TAG = "CardDataBaseService";
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
            database.execSQL("create table temp_table " +
                    "( 'position' INTEGER NOT NULL, 'inHome' INTEGER NOT NULL, 'name' TEXT, 'type' INTEGER PRIMARY KEY NOT NULL, 'canExpand' INTEGER NOT NULL, " +
                    "'mSelectBgResName' TEXT, 'mUnselectBgResName' TEXT)");
            database.execSQL("insert into temp_table (position, inHome, name ,type ,canExpand ,mSelectBgResName, mUnselectBgResName)" +
                    " select position, inHome, name ,type ,canExpand ,mSelectBgResName, mUnselectBgResName from " + TABLE_NAME);
            database.execSQL("drop table " + TABLE_NAME);
            database.execSQL("alter table temp_table rename to " + TABLE_NAME);
        }
    };

    private List<LauncherCard> getAllCardsSync() {
        if (mDataBase == null) {
            createDataBase(mContext);
        }
        CardDao dao = mDataBase.cardDao();
        return dao.getAll();
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
