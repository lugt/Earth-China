package earth.server.tianjin.Server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Frapo on 2017/1/22.
 */
public class NettyChannelMap {
    private static Map<Long, Channel> map = new ConcurrentHashMap<Long, Channel>();

    public static void add(long etId, Channel socketChannel) {
        map.put(etId, socketChannel);
    }

    public static Channel get(long clientId) {
        return map.get(clientId);
    }

    public static boolean contains(Channel sC) {
        return map.containsValue(sC);
    }

    public static void remove(Channel socketChannel) {
        // 这个遍历需要巨大的时间消耗（程序死亡）
        for (Map.Entry entry : map.entrySet()) {
            if (((Channel)entry.getValue()).id().equals(socketChannel.id())) {
                map.remove(entry.getKey());
            }
        }
    }

}