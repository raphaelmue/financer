package de.raphaelmuesseler.financer.util.network;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

@Tag("unit")
public class PortAllocatorTest {

    @Test
    public void testAllocatePort() throws IOException {
        final int startPort = 50000;
        ServerSocket socket1 = new ServerSocket(startPort);
        ServerSocket socket2 = new ServerSocket(startPort + 1);

        int port = PortAllocator.nextFreePort(startPort, startPort + 2);

        Assertions.assertNotEquals(startPort, port);
        Assertions.assertNotEquals(startPort + 1, port);
        Assertions.assertEquals(startPort + 2, port);
        socket1.close();
        socket2.close();
    }

    @Test
    public void testAllocatePortIfNoPortIsFree() throws IOException {
        final int startPort = 50005;
        ServerSocket socket1 = new ServerSocket(startPort);
        ServerSocket socket2 = new ServerSocket(startPort + 1);
        ServerSocket socket3 = new ServerSocket(startPort + 2);

        int port = PortAllocator.nextFreePort(startPort, startPort + 2);

        Assertions.assertNotEquals(startPort, port);
        Assertions.assertNotEquals(startPort + 1, port);
        Assertions.assertEquals(-1, port);

        socket1.close();
        socket2.close();
        socket3.close();
    }

}
