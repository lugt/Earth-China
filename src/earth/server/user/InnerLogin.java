package earth.server.user;

import earth.server.Constant;
import earth.server.Monitor;
import earth.server.data.UserdaoEntity;
import earth.server.utils.Verifier;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Created by Frapo on 2017/1/25.
 * Version :10
 * Earth - Moudule earth.server.user
 */
public class InnerLogin {


    public long verify(String e, String s) {
        try {
            if(e == null || s == null || !Verifier.isValidB64(s)){
                return 0L;
            }
            Long target = Long.parseLong(e);
            Session session = Constant.getSession();
            Constant.getTransact(session);
            if(!session.isConnected() || !session.isOpen()){
                return 0L;
            }
            Query q = session.createQuery("from UserdaoEntity where sessid = :sess");
            q.setParameter("sess", s);
            List l = q.list();
            session.close();
            //UserdaoEntity udE = (UserdaoEntity) q.uniqueResult();
            if(l.get(0) instanceof UserdaoEntity) {
                UserdaoEntity udE = (UserdaoEntity) l.get(0);
                return (target == udE.getEtid()) ? udE.getEtid() : 0L;
            }else{
                return 0L;
            }
        } catch (Exception se) {
            se.printStackTrace();
            Monitor.logger("[Inner/Login] Query Fail"  + se.getMessage());
            return 0L;
        }
    }

    public long get(String s) {
        try {
            if(s == null){ return 0L; }
            if(!Verifier.isValidH64(s)) return 0;
            Session session = Constant.getSession();
            Constant.getTransact(session);
            if(!session.isConnected() || !session.isOpen()){
                return 0L;
            }
            Query q = session.createQuery("from UserdaoEntity where sessid = :sess");
            q.setParameter("sess", s);
            List l = q.list();
            session.close();
            //UserdaoEntity udE = (UserdaoEntity) q.uniqueResult();
            if (l.size() == 0){
                return 0L;
            }
            //udE == null || udE.getEtid() <= Constant.MINIMAL_ETID){
            if(l.get(0) instanceof UserdaoEntity) {
                UserdaoEntity udE = (UserdaoEntity) l.get(0);
                return udE.getEtid();
            }else{
                return 0L;
            }
        } catch (Exception se) {
            se.printStackTrace();
            Monitor.logger("[Inner/Login] Query Fail"  + se.getMessage());
            return 0;
        }
    }
}
