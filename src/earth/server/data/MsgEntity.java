package earth.server.data;

import javax.persistence.*;

/**
 * Created by Frapo on 2017/1/31.
 * Version :23
 * Earth - Moudule earth.server.data
 */
@Entity
@Table(name = "msg", schema = "", catalog = "earthbase")
public class MsgEntity {
    private int target;
    private int sender;
    private byte stat;
    private String msg;

    @Id
    @Column(name = "target", nullable = false, insertable = true, updatable = true)
    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    @Basic
    @Column(name = "sender", nullable = false, insertable = true, updatable = true)
    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    @Basic
    @Column(name = "stat", nullable = false, insertable = true, updatable = true)
    public byte getStat() {
        return stat;
    }

    public void setStat(byte stat) {
        this.stat = stat;
    }

    @Basic
    @Column(name = "msg", nullable = false, insertable = true, updatable = true, length = 65535)
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MsgEntity msgEntity = (MsgEntity) o;

        if (target != msgEntity.target) return false;
        if (sender != msgEntity.sender) return false;
        if (stat != msgEntity.stat) return false;
        if (msg != null ? !msg.equals(msgEntity.msg) : msgEntity.msg != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = target;
        result = 31 * result + sender;
        result = 31 * result + (int) stat;
        result = 31 * result + (msg != null ? msg.hashCode() : 0);
        return result;
    }
}
