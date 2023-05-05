package nat.pink.base.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "message")
public class ObjectMessenge implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "datetime")
    private long dateTime;

    @ColumnInfo(name = "chatcontent")
    private String chatContent;

    @ColumnInfo(name = "issend")
    private boolean isSend;
    /*1: Text, 2: DateTime, 3: Emoji*/

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "userown")
    private long userOwn;

    @ColumnInfo(name = "emoji")
    private int emoji;
    /* 1: seeen - 2: Received - 3: Not seen - 4: Not received*/

    @ColumnInfo(name = "status")
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUserOwn() {
        return userOwn;
    }

    public void setUserOwn(long userOwn) {
        this.userOwn = userOwn;
    }

    public int getEmoji() {
        return emoji;
    }

    public void setEmoji(int emoji) {
        this.emoji = emoji;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
