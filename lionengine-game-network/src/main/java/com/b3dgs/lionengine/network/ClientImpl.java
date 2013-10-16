/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Client implementation.
 */
final class ClientImpl
        extends NetworkModel<ConnectionListener>
        implements Client
{
    /** Ping timer. */
    private final Timing pingTimer;
    /** Ping request timer. */
    private final Timing pingRequestTimer;
    /** Average bandwidth. */
    private final Timing bandwidthTimer;
    /** Socket. */
    private Socket socket;
    /** Output stream. */
    private ObjectOutputStream out;
    /** Input stream. */
    private ObjectInputStream in;
    /** Client id. */
    private byte clientId;
    /** Client name. */
    private String name;
    /** Disconnect flag. */
    private boolean connected;
    /** Ping. */
    private int ping;
    /** Bandwidth size. */
    private int bandwidth;
    /** Bandwidth per second. */
    private int bandwidthPerSecond;

    /**
     * Constructor.
     * 
     * @param decoder The message decoder.
     * @throws LionEngineException if cannot connect to the server.
     */
    ClientImpl(NetworkMessageDecoder decoder) throws LionEngineException
    {
        super(decoder);
        pingTimer = new Timing();
        pingRequestTimer = new Timing();
        bandwidthTimer = new Timing();
        connected = false;
        clientId = -1;
        name = null;
        bandwidth = 0;
    }

    /**
     * Terminate connection.
     */
    private void kick()
    {
        if (!connected)
        {
            return;
        }
        messagesIn.clear();
        messagesOut.clear();
        try
        {
            out.close();
        }
        catch (final IOException exception)
        {
            Verbose.warning(Client.class, "kick", "Error on closing output: ", exception.getMessage());
        }
        try
        {
            in.close();
        }
        catch (final IOException exception)
        {
            Verbose.warning(Client.class, "kick", "Error on closing input: ", exception.getMessage());
        }
        try
        {
            socket.close();
        }
        catch (final IOException exception)
        {
            Verbose.warning(Client.class, "kick", "Error on closing socket: ", exception.getMessage());
        }
        for (final ConnectionListener listener : listeners)
        {
            listener.notifyConnectionTerminated(Byte.valueOf(getId()));
        }
        listeners.clear();
        connected = false;
        Verbose.info("Disconnected from the server !");
    }

    /**
     * Get the name value read from the stream.
     * 
     * @param in The input stream.
     * @return The name string.
     * @throws IOException In case of error.
     */
    private String readString(ObjectInputStream in) throws IOException
    {
        final int size = this.in.readByte();
        if (size > 0)
        {
            final byte[] name = new byte[size];
            this.in.read(name);
            return new String(name, NetworkMessage.CHARSET);
        }
        return null;
    }

    /*
     * Client
     */

    @Override
    public void connect(String ip, int port)
    {
        try
        {
            socket = new Socket(InetAddress.getByName(ip), port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            clientId = -1;
            pingRequestTimer.start();
            bandwidthTimer.start();
        }
        catch (final IOException
                     | SecurityException
                     | IllegalArgumentException
                     | NullPointerException exception)
        {
            throw new LionEngineException(exception, "Cannot connect to the server !");
        }
    }

    @Override
    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
        if (!connected)
        {
            return;
        }
        try
        {
            out.write(NetworkMessageSystemId.OTHER_CLIENT_RENAMED);
            out.write(clientId);
            final byte[] data = this.name.getBytes(NetworkMessage.CHARSET);
            out.writeByte(data.length);
            out.write(data);
            out.flush();
        }
        catch (final IOException exception)
        {
            Verbose.warning(Client.class, "setName", "Unable to set a new client name !");
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getPing()
    {
        return ping;
    }

    @Override
    public int getBandwidth()
    {
        return bandwidthPerSecond;
    }

    @Override
    public byte getId()
    {
        return clientId;
    }

    /*
     * Network
     */

    @Override
    public void disconnect()
    {
        try
        {
            out.write(NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED);
            out.write(clientId);
            out.flush();
            kick();
        }
        catch (final SocketException exception)
        {
            connected = false;
        }
        catch (final IOException exception)
        {
            Verbose.exception(ClientImpl.class, "disconnect", exception);
        }
        connected = false;
    }

    @Override
    public void sendMessages()
    {
        if (!connected)
        {
            return;
        }
        // Ping
        if (pingRequestTimer.elapsed(1000L))
        {
            try
            {
                out.writeByte(NetworkMessageSystemId.PING);
                out.writeByte(clientId);
                out.flush();
                pingTimer.stop();
                pingTimer.start();
                pingRequestTimer.stop();
                pingRequestTimer.start();
                bandwidth += 2;
            }
            catch (final IOException exception)
            {
                Verbose.warning(Client.class, "sendMessage", "Unable to send the messages for client: ",
                        String.valueOf(clientId));
            }
        }
        // Send messages
        for (final NetworkMessage message : messagesOut)
        {
            try (ByteArrayOutputStream encode = message.encode();)
            {
                final byte[] encoded = encode.toByteArray();
                // Message header
                out.writeByte(NetworkMessageSystemId.USER_MESSAGE);
                out.writeByte(message.getClientId());
                out.writeByte(message.getClientDestId());
                out.writeByte(message.getType());
                // Message content
                out.writeInt(encoded.length);
                out.write(encoded);
                out.flush();

                bandwidth += 8 + encoded.length;
            }
            catch (final IOException exception)
            {
                Verbose.warning(Client.class, "sendMessage", "Unable to send the messages for client: ",
                        String.valueOf(clientId));
            }
        }
        if (bandwidthTimer.elapsed(1000L))
        {
            bandwidthPerSecond = bandwidth;
            bandwidth = 0;
            bandwidthTimer.stop();
            bandwidthTimer.start();
        }
        messagesOut.clear();
    }

    @Override
    public void receiveMessages()
    {
        if (!connected)
        {
            return;
        }
        messagesIn.clear();
        try
        {
            if (in.available() == 0)
            {
                return;
            }
            final byte messageSystemId = in.readByte();
            switch (messageSystemId)
            {
                case NetworkMessageSystemId.CONNECTING:
                {
                    if (clientId == -1)
                    {
                        // Receive id
                        clientId = in.readByte();
                        // Send the name
                        out.writeByte(NetworkMessageSystemId.CONNECTING);
                        out.writeByte(clientId);
                        final byte[] data = name.getBytes(NetworkMessage.CHARSET);
                        out.writeByte(data.length);
                        out.write(data);
                        out.flush();
                        Verbose.info("Client: Performing connection to the server...");
                    }
                    break;
                }
                case NetworkMessageSystemId.CONNECTED:
                {
                    byte cid = in.readByte();
                    // Ensure the client id is the same
                    if (cid != clientId)
                    {
                        break;
                    }
                    for (final ConnectionListener listener : listeners)
                    {
                        listener.notifyConnectionEstablished(Byte.valueOf(clientId), name);
                    }
                    // Read the client list
                    final int clientsNumber = in.readByte();
                    for (int i = 0; i < clientsNumber; i++)
                    {
                        cid = in.readByte();
                        final String cname = readString(in);
                        for (final ConnectionListener listener : listeners)
                        {
                            listener.notifyClientConnected(Byte.valueOf(cid), cname);
                        }
                    }
                    // Message of the day if has
                    if (in.available() > 0)
                    {
                        final String motd = readString(in);
                        for (final ConnectionListener listener : listeners)
                        {
                            listener.notifyMessageOfTheDay(motd);
                        }
                    }
                    // Send the last answer
                    out.write(NetworkMessageSystemId.CONNECTED);
                    out.write(clientId);
                    out.flush();
                    Verbose.info("Client: Connected to the server !");
                    break;
                }
                case NetworkMessageSystemId.PING:
                {
                    ping = (int) pingTimer.elapsed();
                    break;
                }
                case NetworkMessageSystemId.KICKED:
                {
                    kick();
                    break;
                }
                case NetworkMessageSystemId.OTHER_CLIENT_CONNECTED:
                {
                    final byte cid = in.readByte();
                    final String cname = readString(in);
                    for (final ConnectionListener listener : listeners)
                    {
                        listener.notifyClientConnected(Byte.valueOf(cid), cname);
                    }
                    break;
                }
                case NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED:
                {
                    final byte cid = in.readByte();
                    final String cname = readString(in);
                    for (final ConnectionListener listener : listeners)
                    {
                        listener.notifyClientDisconnected(Byte.valueOf(cid), cname);
                    }
                    break;
                }
                case NetworkMessageSystemId.OTHER_CLIENT_RENAMED:
                {
                    final byte cid = in.readByte();
                    final String cname = readString(in);
                    for (final ConnectionListener listener : listeners)
                    {
                        listener.notifyClientNameChanged(Byte.valueOf(cid), cname);
                    }
                    break;
                }
                case NetworkMessageSystemId.USER_MESSAGE:
                {
                    final byte from = in.readByte();
                    final byte dest = in.readByte();
                    final byte type = in.readByte();
                    final int size = in.readInt();
                    if (size > 0)
                    {
                        final byte[] data = new byte[size];
                        in.read(data);
                        try (DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(data));)
                        {
                            decodeMessage(type, from, dest, buffer);
                        }
                    }
                    bandwidth += 4 + size;
                    break;
                }
                default:
                    break;
            }
        }
        catch (final IOException exception)
        {
            Verbose.warning(Client.class, "receiveMessages", "Unable to receive the messages for client: ",
                    String.valueOf(clientId));
        }
    }
}
