package de.raphaelmuesseler.financer.util.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PortAllocator {

    public static int nextFreePort() {
        return nextFreePort(1, 65535);
    }

    public static int nextFreePort(int from, int to) {
        Set<Integer> ports = new HashSet<>();
        int port;
        while (ports.size() <= (to - from)) {
            port = ThreadLocalRandom.current().nextInt(from, to + 1);
            ports.add(port);

            if (isLocalPortFree(port)) {
                return port;
            }
        }
        return -1;
    }

    private static boolean isLocalPortFree(int port) {
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
