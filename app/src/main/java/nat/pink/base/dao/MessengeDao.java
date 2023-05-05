package nat.pink.base.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import nat.pink.base.model.ObjectMessenge;

@Dao
public interface MessengeDao {

    @Insert
    Long insertMessenge(ObjectMessenge objectMessenge);

    @Query("SELECT * FROM message WHERE userOwn IN (:userOwn)")
    List<ObjectMessenge> getMessageByOwnId(int userOwn);

    @Query("DELETE FROM message WHERE id IN (:id)")
    void deleteMessage(int id);
}
