package earth.server;

import earth.server.Constant;
import earth.server.Monitor;
import earth.server.Nanjing.FileServer;
import earth.server.peking.Peking;
import earth.server.tianjin.Server.Tianjin;

import java.util.concurrent.TimeUnit;

/**
 * Created by Frapo on 2017/1/22.
 */
public class Main {

    //private static final SessionFactory ourSessionFactory;
    //private static final ServiceRegistry serviceRegistry;

    public static void main(String[] args){

        try {
            Constant.setUp();
        } catch (Exception e) {
            e.printStackTrace();
            Monitor.error("Hibernate Initialization have failed.");
            System.exit(0);
        }


        try {
            Tianjin.main();
            Peking.main();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileServer.newBuild().start();

        try {
            while (true) {
                // 监测其他进程是否正常
                if (!Constant.isSessionAlive()) {
                    // 告警
                    Monitor.error("sessionFactory creation unsuccessful / is Closed.");
                }
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            Constant.close();
        }
    }
}
