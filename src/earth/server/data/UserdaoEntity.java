package earth.server.data;

import javax.persistence.*;

/**
 * Created by Frapo on 2017/1/24.
 */
@Entity
@Table(name = "userdao", schema = "", catalog = "earthbase")
public class UserdaoEntity {
    private long etid = 0L;
    private String displayName = "Earth_CN_X";
    private String password = "xxxxxxxxxxxxxxxxxxxxxx";
    private String extendv = "no";
    private Long cellphone = 9998613800000000L;
    private byte status;
    private String identity;
    private String email;
    private String sessid;

    @Id
    @Column(name = "etid", nullable = false, insertable = true, updatable = true)
    public long getEtid() {
        return etid;
    }

    public void setEtid(long etid) {
        this.etid = etid;
    }

    @Basic
    @Column(name = "displayName", nullable = false, insertable = true, updatable = true, length = 100)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Basic
    @Column(name = "password", nullable = false, insertable = true, updatable = true, length = 64)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "extendv", nullable = false, insertable = true, updatable = true, length = 60)
    public String getExtendv() {
        return extendv;
    }

    public void setExtendv(String extendv) {
        this.extendv = extendv;
    }

    @Basic
    @Column(name = "cellphone", nullable = true, insertable = true, updatable = true)
    public Long getCellphone() {
        return cellphone;
    }

    public void setCellphone(Long cellphone) {
        this.cellphone = cellphone;
    }

    @Basic
    @Column(name = "status", nullable = false, insertable = true, updatable = true)
    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Basic
    @Column(name = "identity", nullable = true, insertable = true, updatable = true, length = 50)
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Basic
    @Column(name = "email", nullable = true, insertable = true, updatable = true, length = 70)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Basic
    @Column(name = "sessid", nullable = false, insertable = true, updatable = true, length = 32)
    public String getSessid() {
        return sessid;
    }

    public void setSessid(String sessid) {
        this.sessid = sessid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserdaoEntity that = (UserdaoEntity) o;

        if (etid != that.etid) return false;
        if (status != that.status) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (extendv != null ? !extendv.equals(that.extendv) : that.extendv != null) return false;
        if (cellphone != null ? !cellphone.equals(that.cellphone) : that.cellphone != null) return false;
        if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (sessid != null ? !sessid.equals(that.sessid) : that.sessid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (etid ^ (etid >>> 32));
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (extendv != null ? extendv.hashCode() : 0);
        result = 31 * result + (cellphone != null ? cellphone.hashCode() : 0);
        result = 31 * result + (int) status;
        result = 31 * result + (identity != null ? identity.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (sessid != null ? sessid.hashCode() : 0);
        return result;
    }
}
