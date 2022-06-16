package card.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Room;

import java.util.LinkedList;
import java.util.List;

import card.base.LauncherCard;
import card.service.R;
import launcher.base.async.AsyncSchedule;
import launcher.base.utils.EasyLog;

public class CardDataBaseService{
    private final String TAG = "CardDataBaseService";
    private AppDataBase mDataBase;
    private Context mContext;
    private final Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    public CardDataBaseService(Context context) {
        mContext = context;
        createDataBase(mContext);
    }

    private void createDataBase(Context context) {
        if (mDataBase == null) {
            mDataBase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDataBase.class, "card_db").build();
        }
    }

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
                EasyLog.d(TAG, "saveCards :"+launcherCards);
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
                EasyLog.d(TAG, "updateCards :"+launcherCards);
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
