package nat.pink.base.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import nat.pink.base.model.ObjectUser;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<ObjectUser> getAllUser();

    @Insert
    Long insertUser(ObjectUser user);

    @Update
    int updateUser(ObjectUser user);

    @Delete
    void deleteUser(ObjectUser user);

}
