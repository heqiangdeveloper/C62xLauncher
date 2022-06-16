package card.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import card.base.LauncherCard;

@Dao
public interface CardDao {
    @Query("SELECT * FROM LauncherCard")
    List<LauncherCard> getAll();

    @Insert
    void insertAll(LauncherCard... cards);

    @Update
    void update(LauncherCard... launcherCard);

}
