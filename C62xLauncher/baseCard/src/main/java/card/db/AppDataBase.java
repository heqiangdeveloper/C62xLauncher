package card.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import card.base.LauncherCard;

@Database(entities = {LauncherCard.class}, version = 2, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CardDao cardDao();
}
