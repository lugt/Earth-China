package earth.server.user;

import earth.server.Constant;
import earth.server.Monitor;
import earth.server.data.UserdaoEntity;
import earth.server.utils.Verifier;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpRequest;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.nio.charset.Charset;

/**
 * Created by Frapo on 2017/1/22.
 */
public class UserInfo {

    public String basic(ByteBuf content) {
        String m = content.toString(Charset.forName("UTF-8"));
        String[] x = m.split(","); //Cell,
        if (x.length != 2) {
            return "Bas,fail,param";
        }
        if (!Verifier.isValidH64(x[0])) {
            return "Bas,fail,usign";
        }
        UserdaoEntity udE = getUserOnSSid(x[0]);
        if(udE == null || !Verifier.isValidEtid(udE.getEtid())) return "Bas,fail,login";
        StringBuilder id = new StringBuilder();
        id.append(udE.getDisplayName()).append(",")
            .append(udE.getEmail()).append(",")
            .append(udE.getExtendv()).append(",")
            .append(udE.getCellphone()-9998600000000000L).append(",")
            .append(udE.getEtid()).append(",")
            .append(udE.getStatus());
        return "Bas,ok,"+id;
    }

    public String getIdentity(ByteBuf content) {
        String m = content.toString(Charset.forName("UTF-8"));
        String[] x = m.split(","); //Cell,
        if (x.length != 2) {
            return "Id,fail,param";
        }
        if (!Verifier.isValidH64(x[0])) {
            return "Id,fail,usign";
        }
        UserdaoEntity udE = getUserOnSSid(x[0]);
        if(udE == null || !Verifier.isValidEtid(udE.getEtid())) return "Id,fail,login";
        String id = udE.getIdentity();
        return "Id,ok,"+id;
    }

    public UserdaoEntity getUserOnSSid(String ssid) {
        try {
            Session session = Constant.getSession();
            Query q = session.createQuery("from UserdaoEntity where sessid = :sess");
            q.setParameter("sess", ssid);
            UserdaoEntity udE = (UserdaoEntity) q.uniqueResult();
            if (udE == null) return null;
            return udE;
        } catch (Exception es) {
            Monitor.logger("[GetEtid]" + es.getMessage());
            es.printStackTrace();
            return null;
        }
    }

    public String verify(ByteBuf buf) {
        String y = buf.toString(Charset.forName("utf-8"));
        return "V,ok";
    }

    public String getPublicEtid(ByteBuf content) {
        String m = content.toString(Charset.forName("UTF-8"));
        String[] x = m.split(","); //Cell,
        if (x.length != 2) {
            return "Ppet,fail,param";
        }
        Long et = Long.valueOf(x[0]);
        if (!Verifier.isValidEtid(et)) {
            return "Ppet,fail,etid";
        }
        UserdaoEntity udE = getUserOnEtid(et);
        if(udE == null || !Verifier.isValidEtid(udE.getEtid())) return "Ppet,fail,search";
        String id = udE.getEtid() + "," + udE.getDisplayName();
        return "Ppet,ok,"+id;
    }

    public String getPublicCell(ByteBuf content) {
        String m = content.toString(Charset.forName("UTF-8"));
        String[] x = m.split(","); //Cell,
        if (x.length != 2) {
            return "Pcel,fail,param";
        }
        if (!Verifier.isMobile(x[0])) {
            return "Pcel,fail,mobile";
        }
        Long et = Long.valueOf(x[0]);
        et = 9998600000000000L + et;
        UserdaoEntity udE = getUserOnCell(et);
        if(udE == null || !Verifier.isValidEtid(udE.getEtid())) return "Pcel,fail,search";
        String id = udE.getEtid() + "," + udE.getDisplayName();
        return "Pcel,ok,"+id;
    }

    private UserdaoEntity getUserOnCell(Long et) {
        try {
            Session session = Constant.getSession();
            Query q = session.createQuery("from UserdaoEntity where cellphone = :cell");
            q.setParameter("cell", et);
            UserdaoEntity udE = (UserdaoEntity) q.uniqueResult();
            if (udE == null) return null;
            return udE;
        } catch (Exception es) {
            Monitor.logger("[GetEtid]" + es.getMessage());
            es.printStackTrace();
            return null;
        }
    }

    private UserdaoEntity getUserOnEtid(long et) {
        try {
            Session session = Constant.getSession();
            Query q = session.createQuery("from UserdaoEntity where etid = :eid");
            q.setParameter("eid", et);
            UserdaoEntity udE = (UserdaoEntity) q.uniqueResult();
            if (udE == null) return null;
            return udE;
        } catch (Exception es) {
            Monitor.logger("[GetEtid]" + es.getMessage());
            es.printStackTrace();
            return null;
        }
    }
}
