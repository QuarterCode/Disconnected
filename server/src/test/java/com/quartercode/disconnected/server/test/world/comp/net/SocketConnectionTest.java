/*
 * This file is part of Disconnected.
 * Copyright (c) 2013 QuarterCode <http://www.quartercode.com/>
 *
 * Disconnected is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disconnected is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disconnected. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.disconnected.server.test.world.comp.net;

import static org.junit.Assert.assertEquals;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Prioritized;
import com.quartercode.disconnected.server.util.ObjArray;
import com.quartercode.disconnected.server.world.comp.net.Address;
import com.quartercode.disconnected.server.world.comp.net.NetID;
import com.quartercode.disconnected.server.world.comp.net.NetworkModule;
import com.quartercode.disconnected.server.world.comp.net.Packet;
import com.quartercode.disconnected.server.world.comp.net.Socket;
import com.quartercode.disconnected.server.world.comp.net.Socket.PacketHandler;
import com.quartercode.disconnected.server.world.comp.net.Socket.SocketState;

public class SocketConnectionTest {

    /*
     * This field is changed by the test every run.
     */
    private static NetworkModuleSendHook netModuleSendHook;

    private static interface NetworkModuleSendHook {

        public void onSend(Socket socket, Object data);

    }

    @BeforeClass
    public static void installHooks() {

        NetworkModule.SEND.addExecutor("hook", NetworkModule.class, new FunctionExecutor<Void>() {

            @Override
            @Prioritized (Prioritized.LEVEL_9)
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                if (netModuleSendHook != null) {
                    netModuleSendHook.onSend((Socket) arguments[0], arguments[1]);
                }

                // Stop the invocation
                return null;
            }

        });

        // Change the sequence number generator of the Socket class in order to generate predictable numbers
        // Each socket always gets a sequence number which is equal to the socket's local port
        // Socket 1 always gets the sequence number 10 while socket 2 always gets the sequence number 20
        Socket.CURRENT_SEQ_NUMBER.addSetterExecutor("testGenerate", Socket.class, new FunctionExecutor<Void>() {

            @Override
            @Prioritized (Prioritized.LEVEL_9)
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                Socket holder = (Socket) invocation.getHolder();

                if ((int) arguments[0] < 0) {
                    int newSequenceNumber = holder.get(Socket.LOCAL_PORT).get();
                    holder.get(Socket.CURRENT_SEQ_NUMBER).set(newSequenceNumber);

                    // Cancel the invocation chain; the next generator should not be invoked
                    return null;
                } else {
                    return invocation.next(arguments);
                }
            }

        });
    }

    @AfterClass
    public static void uninstallHooks() {

        NetworkModule.SEND.removeExecutor("hook", NetworkModule.class);
        Socket.CURRENT_SEQ_NUMBER.removeSetterExecutor("testGenerate", Socket.class);
    }

    @Rule
    // @formatter:off
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};
    // @formatter:on

    private NetworkModule         netModule;
    @Mock
    private NetworkModuleSendHook mockNetModuleSendHook;

    private Address               socket1Address;
    private Address               socket2Address;
    private Socket                socket1;
    private Socket                socket2;

    @Before
    public void setUp() {

        netModule = new NetworkModule();

        // Create the test sockets
        socket1Address = createAddress(createNetId(0, 1), 10);
        socket2Address = createAddress(createNetId(0, 2), 20);
        socket1 = createSocket(netModule, socket1Address.get(Address.PORT).get(), socket2Address);
        socket2 = createSocket(netModule, socket2Address.get(Address.PORT).get(), socket1Address);

        // Apply a simple routing:
        // socket1 -> socket2
        // socket2 -> socket1
        netModuleSendHook = new NetworkModuleSendHook() {

            @Override
            public void onSend(Socket socket, Object data) {

                mockNetModuleSendHook.onSend(socket, data);

                Packet packet = new Packet();
                packet.get(Packet.DATA).set(data);

                if (socket == socket1) {
                    packet.get(Packet.SOURCE).set(socket1Address);
                    packet.get(Packet.DESTINATION).set(socket2Address);
                    socket2.get(Socket.HANDLE).invoke(packet);
                } else if (socket == socket2) {
                    packet.get(Packet.SOURCE).set(socket2Address);
                    packet.get(Packet.DESTINATION).set(socket1Address);
                    socket1.get(Socket.HANDLE).invoke(packet);
                }
            }

        };

    }

    @Test
    public void testSendAndHandle() {

        // Connect the two test sockets
        socket1.get(Socket.STATE).set(SocketState.CONNECTED);
        socket2.get(Socket.STATE).set(SocketState.CONNECTED);

        // Add some packet handlers for checking that the correct packets arrive
        final PacketHandler packetHandler1 = context.mock(PacketHandler.class, "packetHandler1");
        final PacketHandler packetHandler2 = context.mock(PacketHandler.class, "packetHandler2");

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence sequence = context.sequence("sequence");

            oneOf(mockNetModuleSendHook).onSend(socket1, "testdata1"); inSequence(sequence);
            oneOf(packetHandler2).handle("testdata1"); inSequence(sequence);
            oneOf(mockNetModuleSendHook).onSend(socket2, "testdata2"); inSequence(sequence);
            oneOf(packetHandler1).handle("testdata2"); inSequence(sequence);

        }});
        // @formatter:on

        socket1.get(Socket.PACKET_HANDLERS).add(packetHandler1);
        socket2.get(Socket.PACKET_HANDLERS).add(packetHandler2);

        // Send test packets
        socket1.get(Socket.SEND).invoke("testdata1");
        socket2.get(Socket.SEND).invoke("testdata2");
    }

    @Test
    public void testHandshake() {

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence handshake = context.sequence("handshake");

            // The sequence numbers are predictable because the test replaces the generator:
            // Socket 1: 10
            // Socket 2: 20
            oneOf(mockNetModuleSendHook).onSend(socket1, new ObjArray("$_handshake", "syn", 10)); inSequence(handshake);
            oneOf(mockNetModuleSendHook).onSend(socket2, new ObjArray( "$_handshake", "syn-ack", 20, 10 + 1)); inSequence(handshake);
            oneOf(mockNetModuleSendHook).onSend(socket1, new ObjArray( "$_handshake", "ack", 20 + 1)); inSequence(handshake);

        }});
        // @formatter:on

        // Connect the two sockets
        socket1.get(Socket.CONNECT).invoke();

        // Check the socket states
        assertEquals("State of socket 1 after the handshake", SocketState.CONNECTED, socket1.get(Socket.STATE).get());
        assertEquals("State of socket 2 after the handshake", SocketState.CONNECTED, socket2.get(Socket.STATE).get());

        // Send some packets in order to ensure that the connection functions
        // Note that this test is very basic and not very reliable

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence packetSending = context.sequence("packetSending");

            oneOf(mockNetModuleSendHook).onSend(socket1,  "testdata1"); inSequence(packetSending);
            oneOf(mockNetModuleSendHook).onSend(socket2,  "testdata2"); inSequence(packetSending);

        }});
        // @formatter:on

        socket1.get(Socket.SEND).invoke("testdata1");
        socket2.get(Socket.SEND).invoke("testdata2");
    }

    @Test
    public void testConnectionTimeout() {

        // Disconnect socket 2 so it can't send or receive any packets
        socket2.get(Socket.STATE).set(SocketState.DISCONNECTED);

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence packetSending = context.sequence("packetSending");

            oneOf(mockNetModuleSendHook).onSend(socket1, new ObjArray("$_handshake", "syn", 10)); inSequence(packetSending);
            oneOf(mockNetModuleSendHook).onSend(socket1, "$_teardown"); inSequence(packetSending);

        }});
        // @formatter:on

        // Connect the two sockets
        socket1.get(Socket.CONNECT).invoke();

        // Let socket1 time out
        for (int update = 0; update < Socket.CONNECTION_TIMEOUT; update++) {
            socket1.get(Socket.SCHEDULER).update("computerNetworkUpdate");
        }

        // Check the socket states
        assertEquals("State of socket 1 after the connection timeout", SocketState.DISCONNECTED, socket1.get(Socket.STATE).get());
        assertEquals("State of socket 2 after the connection timeout", SocketState.DISCONNECTED, socket2.get(Socket.STATE).get());
    }

    @Test
    public void testKeepalive() {

        // Connect the two test sockets
        socket1.get(Socket.STATE).set(SocketState.CONNECTED);
        socket2.get(Socket.STATE).set(SocketState.CONNECTED);

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence keepalive = context.sequence("keepalive");

            oneOf(mockNetModuleSendHook).onSend(socket1, new ObjArray("$_keepalive", "req")); inSequence(keepalive);
            oneOf(mockNetModuleSendHook).onSend(socket2, new ObjArray("$_keepalive", "rsp")); inSequence(keepalive);

        }});
        // @formatter:on

        // Let socket1 send a keepalive request and check whether a response has arrived
        for (int update = 0; update < Socket.KEEPALIVE_PERIOD + Socket.KEEPALIVE_REPONSE_TIMEOUT; update++) {
            socket1.get(Socket.SCHEDULER).update("computerNetworkUpdate");
        }

        // Check the socket states
        assertEquals("State of socket 1 after the successful keepalive", SocketState.CONNECTED, socket1.get(Socket.STATE).get());
        assertEquals("State of socket 2 after the successful keepalive", SocketState.CONNECTED, socket2.get(Socket.STATE).get());
    }

    @Test
    public void testKeepaliveNoResponse() {

        // Connect socket 1
        socket1.get(Socket.STATE).set(SocketState.CONNECTED);
        // Disconnect socket 2 so it can't send or receive any packets
        socket2.get(Socket.STATE).set(SocketState.DISCONNECTED);

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence packetSending = context.sequence("packetSending");

            oneOf(mockNetModuleSendHook).onSend(socket1, new ObjArray("$_keepalive", "req")); inSequence(packetSending);
            never(mockNetModuleSendHook).onSend(socket2, new ObjArray("$_keepalive", "rsp"));
            oneOf(mockNetModuleSendHook).onSend(socket1, "$_teardown"); inSequence(packetSending);

        }});
        // @formatter:on

        // Let socket1 send a keepalive request and check whether a response has arrived
        for (int update = 0; update < Socket.KEEPALIVE_PERIOD + Socket.KEEPALIVE_REPONSE_TIMEOUT; update++) {
            socket1.get(Socket.SCHEDULER).update("computerNetworkUpdate");
        }

        // Check the socket states
        assertEquals("State of socket 1 after the unsuccessful handshake", SocketState.DISCONNECTED, socket1.get(Socket.STATE).get());
        assertEquals("State of socket 2 after the unsuccessful handshake", SocketState.DISCONNECTED, socket2.get(Socket.STATE).get());
    }

    @Test
    public void testTeardown() {

        // Connect the two test sockets
        socket1.get(Socket.STATE).set(SocketState.CONNECTED);
        socket2.get(Socket.STATE).set(SocketState.CONNECTED);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(mockNetModuleSendHook).onSend(socket1, "$_teardown");

        }});
        // @formatter:on

        // Disconnect the two sockets
        socket1.get(Socket.DISCONNECT).invoke();

        // Check the socket states
        assertEquals("State of socket 1 after the handshake", SocketState.DISCONNECTED, socket1.get(Socket.STATE).get());
        assertEquals("State of socket 2 after the handshake", SocketState.DISCONNECTED, socket2.get(Socket.STATE).get());
    }

    private NetID createNetId(int subnet, int id) {

        NetID netId = new NetID();
        netId.get(NetID.SUBNET).set(subnet);
        netId.get(NetID.ID).set(id);

        return netId;
    }

    private Address createAddress(NetID netId, int port) {

        Address address = new Address();
        address.get(Address.NET_ID).set(netId);
        address.get(Address.PORT).set(port);

        return address;
    }

    private Socket createSocket(NetworkModule netModule, int localPort, Address destination) {

        Socket socket = new Socket();
        socket.setParent(netModule);
        socket.get(Socket.LOCAL_PORT).set(localPort);
        socket.get(Socket.DESTINATION).set(destination);

        return socket;
    }

}