package nat.pink.base.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import nat.pink.base.model.ObjectMessenge;
import nat.pink.base.model.ObjectSpin;
import nat.pink.base.model.ObjectUser;
import nat.pink.base.model.ObjectsContentSpin;

@Database(entities = {ObjectUser.class, ObjectMessenge.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context,
                            AppDatabase.class,
                            "Spin-Database")
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }

    public abstract UserDao getUserDao();

    public abstract MessengeDao getMessengeDao();
}