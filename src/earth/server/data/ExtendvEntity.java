package earth.server.data;

import javax.persistence.*;

/**
 * Created by Frapo on 2017/1/24.
 */
@Entity
@Table(name = "extendv", schema = "", catalog = "earthbase")
public class ExtendvEntity {
    private long etid;
    private Integer facial;
    private String facialset;
    private String finger;
    private String print;
    private String authen;
    private long authentime;
    private String authenmeth;

    @Id
    @Column(name = "etid", nullable = false, insertable = true, updatable = true)
    public long getEtid() {
        return etid;
    }

    public void setEtid(long etid) {
        this.etid = etid;
    }

    @Basic
    @Column(name = "facial", nullable = true, insertable = true, updatable = true)
    public Integer getFacial() {
        return facial;
    }

    public void setFacial(Integer facial) {
        this.facial = facial;
    }

    @Basic
    @Column(name = "facialset", nullable = true, insertable = true, updatable = true, length = 100)
    public String getFacialset() {
        return facialset;
    }

    public void setFacialset(String facialset) {
        this.facialset = facialset;
    }

    @Basic
    @Column(name = "finger", nullable = true, insertable = true, updatable = true, length = 100)
    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    @Basic
    @Column(name = "print", nullable = true, insertable = true, updatable = true, length = 200)
    public String getPrint() {
        return print;
    }

    public void setPrint(String print) {
        this.print = print;
    }

    @Basic
    @Column(name = "authen", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAuthen() {
        return authen;
    }

    public void setAuthen(String authen) {
        this.authen = authen;
    }

    @Basic
    @Column(name = "authentime", nullable = false, insertable = true, updatable = true)
    public long getAuthentime() {
        return authentime;
    }

    public void setAuthentime(long authentime) {
        this.authentime = authentime;
    }

    @Basic
    @Column(name = "authenmeth", nullable = true, insertable = true, updatable = true, length = 60)
    public String getAuthenmeth() {
        return authenmeth;
    }

    public void setAuthenmeth(String authenmeth) {
        this.authenmeth = authenmeth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExtendvEntity that = (ExtendvEntity) o;

        if (etid != that.etid) return false;
        if (authentime != that.authentime) return false;
        if (facial != null ? !facial.equals(that.facial) : that.facial != null) return false;
        if (facialset != null ? !facialset.equals(that.facialset) : that.facialset != null) return false;
        if (finger != null ? !finger.equals(that.finger) : that.finger != null) return false;
        if (print != null ? !print.equals(that.print) : that.print != null) return false;
        if (authen != null ? !authen.equals(that.authen) : that.authen != null) return false;
        if (authenmeth != null ? !authenmeth.equals(that.authenmeth) : that.authenmeth != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (etid ^ (etid >>> 32));
        result = 31 * result + (facial != null ? facial.hashCode() : 0);
        result = 31 * result + (facialset != null ? facialset.hashCode() : 0);
        result = 31 * result + (finger != null ? finger.hashCode() : 0);
        result = 31 * result + (print != null ? print.hashCode() : 0);
        result = 31 * result + (authen != null ? authen.hashCode() : 0);
        result = 31 * result + (int) (authentime ^ (authentime >>> 32));
        result = 31 * result + (authenmeth != null ? authenmeth.hashCode() : 0);
        return result;
    }
}
