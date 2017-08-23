package earth.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Created by Frapo on 2017/1/22.
 */
public class Constant {
    public static final String ServerV = "1005";
    public static final String ClientV = "1005";
    public static final String ProtoV = "1001";
    public static final String MinimunProto = "1001";
    public static final String SecureEnforce = "ssl";
    public static final long MINIMAL_ETID = 999L;
    public static final Long MAX_ETID = 6999L;
    public static final String REDIS_SERVER = "localhost";

    private static SessionFactory sessionFactory;
    private static Log log = LogFactory.getLog(Constant.class);
    public static final int File_SERVER_Port = 8500;
    public static String fileUploadDir = "H:\\IdeaProjects\\matrice\\temp\\";

    public static void setUp() throws HibernateException {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml") // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            e.printStackTrace();
            log.error("Hibernate Exception: "+e.getMessage());
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static Session getSession() throws HibernateException{
        Session session;
        if (sessionFactory == null) {
            setUp();
            if(sessionFactory == null) {
                log.fatal("EESession: sessionFactory is dead");
                throw new HibernateException("Could not initiate sessionFactory");
            }
        }
        if(sessionFactory.isClosed()){
            setUp();
            session = sessionFactory.openSession();
        }
        try {
            session = sessionFactory.getCurrentSession();
        }catch (Exception e){
            session =  sessionFactory.openSession();
        }
        return session;
    }

    public static boolean isSessionAlive() {
        return !sessionFactory.isClosed();
    }

    public static void close() {
        if (null != sessionFactory.getCurrentSession()) sessionFactory.getCurrentSession().close();
        if (sessionFactory != null) {
            sessionFactory.close();
        }

    }

    public static void getTransact(Session session) throws Exception{

        if(session == null || !session.isOpen()){
            Monitor.logger("Session is not Connected in getTransact");
            throw new HibernateException("Session not connected");
        }

        if(session.getTransaction() == null){
            session.beginTransaction().setTimeout(3);
        }else {
            if(!session.getTransaction().isActive()){
                session.beginTransaction().setTimeout(3);
            }
        }
    }
}
