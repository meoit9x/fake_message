package nat.pink.base.ui.home;

import android.content.Context;

import androidx.core.util.Consumer;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import nat.pink.base.base.BaseViewModel;
import nat.pink.base.dao.DatabaseController;
import nat.pink.base.model.ObjectMessenge;
import nat.pink.base.model.ObjectSpin;
import nat.pink.base.model.ObjectSpinDisplay;
import nat.pink.base.model.ObjectUser;
import nat.pink.base.model.ObjectsContentSpin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> reloadDataUser = new MutableLiveData<>();
    public List<ObjectUser> objectUsers = new ArrayList<>();
    public List<ObjectMessenge> objectMessenges = new ArrayList<>();
    public MutableLiveData<Boolean> reloadDataMessenger = new MutableLiveData<>();
    public MutableLiveData<ObjectUser> loadChatInfo = new MutableLiveData<>();

    public void getAllUser(Context context, Consumer consumer) {
        objectUsers = DatabaseController.getInstance(context).getAllUser();
        consumer.accept(new Object());
    }

    public boolean insertUser(Context context, ObjectUser objectUser) {
        if (DatabaseController.getInstance(context).insertUser(objectUser) != 0) {
            reloadDataUser.postValue(true);
            return true;
        }
        reloadDataUser.postValue(false);
        return false;
    }

    public boolean updateUser(Context context, ObjectUser objectUser) {
        if (DatabaseController.getInstance(context).updateUser(objectUser) != 0) {
            reloadDataUser.postValue(true);
            loadChatInfo.postValue(objectUser);
            return true;
        }
        loadChatInfo.postValue(null);
        reloadDataUser.postValue(false);
        return false;
    }

    public void getMessengerById(Context context, int userId, Consumer consumer) {
        objectMessenges = DatabaseController.getInstance(context).getMessageById(userId);
        consumer.accept(new Object());
    }

    public boolean inserMessenger(Context context, ObjectMessenge objectMessenge) {
        if (DatabaseController.getInstance(context).insertMessenge(objectMessenge) != 0) {
            reloadDataMessenger.postValue(true);
            return true;
        }
        return false;
    }

}
