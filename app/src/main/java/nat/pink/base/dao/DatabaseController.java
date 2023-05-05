package nat.pink.base.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import nat.pink.base.R;
import nat.pink.base.model.ObjectMessenge;
import nat.pink.base.model.ObjectSpin;
import nat.pink.base.model.ObjectUser;
import nat.pink.base.model.ObjectsContentSpin;
import nat.pink.base.utils.Const;

public class DatabaseController {

    private static DatabaseController databaseController;
    private AppDatabase appDatabase;
    private Context context;
    private List<ObjectsContentSpin> objectsContentSpins = new ArrayList<>();

    public static DatabaseController getInstance(Context context) {
        if (databaseController == null) {
            databaseController = new DatabaseController(context);
        }
        return databaseController;
    }

    public DatabaseController(Context context) {
        appDatabase = AppDatabase.getInstance(context);
        this.context = context;
    }

    public ArrayList<ObjectUser> getAllUser() {
        try{
            if (appDatabase != null) {
                return (ArrayList<ObjectUser>) appDatabase.getUserDao().getAllUser();
            }
        }catch (RuntimeException exception){
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public List<ObjectMessenge> getMessageById(int id) {
        if (appDatabase != null) {
            return appDatabase.getMessengeDao().getMessageByOwnId(id);
        }
        return new ArrayList<>();
    }

    public Long insertMessenge(ObjectMessenge objectMessenge) {
        if (appDatabase != null) {
            return appDatabase.getMessengeDao().insertMessenge(objectMessenge);
        }
        return 0L;
    }

    public Long insertUser(ObjectUser objectUser) {
        if (appDatabase != null) {
            return appDatabase.getUserDao().insertUser(objectUser);
        }
        return 0L;
    }

    public int updateUser(ObjectUser objectUser) {
        if (appDatabase != null)
            return appDatabase.getUserDao().updateUser(objectUser);
        return 0;
    }

    public void deleteUser(ObjectUser objectUser) {
        if (appDatabase != null) {
            appDatabase.getUserDao().deleteUser(objectUser);
        }
    }

    public void deleteMessenger(int id) {
        if (appDatabase != null) {
            appDatabase.getMessengeDao().deleteMessage(id);
        }
    }

}
