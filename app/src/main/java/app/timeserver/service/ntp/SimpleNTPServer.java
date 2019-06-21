package app.timeserver.service.ntp;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;

import org.apache.commons.net.ntp.NtpUtils;
import org.apache.commons.net.ntp.NtpV3Impl;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeStamp;
import app.timeserver.repository.time.TimeStorageConsumer;
import app.timeserver.service.ntp.logging.ServerLogDataPoint;
import app.timeserver.service.ntp.logging.ServerLogDataPointGrouper;
import com.google.common.util.concurrent.RateLimiter;


import java.lang.Runnable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * The SimpleNTPServer class is a UDP implementation of a server for the
 * Network Time Protocol (NTP) version 3 as described in RFC 1305.
 * It is a minimal NTP server that doesn't actually adjust the time but
 * only responds to NTP datagram requests with response sent back to
 * originating host with info filled out using the current clock time.
 * To be used for debugging or testing.
 * <p>
 * To prevent this from interfering with the actual NTP service it can be
 * run from any local port.
 */
public class SimpleNTPServer implements Runnable {
    private Context sContext;
    private int port;
    private int maxPackets;
    private String stratum = "1";

    private volatile boolean running;
    private boolean started;

    private DatagramSocket socket;

    private TimeStorageConsumer timeStorageConsumer;
    private Intent serverIntent;
    private RateLimiter rateLimiter;
    byte buffer[] = new byte[48];
    public DatagramPacket request = new DatagramPacket(buffer, buffer.length);
    Handler restartHandle = new Handler();



    /**
     * Create SimpleNTPServer listening on default NTP port.
     */
    public SimpleNTPServer() {
        this(NtpV3Packet.NTP_PORT);
    }

    /**
     * Create SimpleNTPServer.
     *
     * @param port the local port the server socket is bound to, or
     *             <code>zero</code> for a system selected free port.
     * @throws IllegalArgumentException if port number less than 0
     */
    public SimpleNTPServer(int port) {
        if (port < 0) {
            throw new IllegalArgumentException();
        }
        this.port = port;
        this.timeStorageConsumer = new TimeStorageConsumer();
    }

    public int getPort() {
        return port;
    }

    public String getStratumNumber(){
       return stratum;
    }

    public void setStratumNumber(String value) {
        stratum = value;
    }

    public int getPacketSize(){
      return maxPackets;
    }

    public void setPacketSize(int value){
      maxPackets = value;
      rateLimiter.create(value);
    }



    /**
     * Return state of whether time service is running.
     *
     * @return true if time service is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Return state of whether time service is running.
     *
     * @return true if time service is running
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Connect to server socket and listen for client connections.
     *
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    public void connect() throws IOException {
        if (socket == null) {
            socket = new DatagramSocket(port);
            // port = 0 is bound to available free port
            if (port == 0) {
                port = socket.getLocalPort();
            }
            System.out.println("Running NTP service on port " + port + "/UDP");
        }
    }

    /**
     * Start time service and provide time to client connections.
     *
     * @throws java.io.IOException if an I/O error occurs when creating the socket.
     */
    public void start() throws IOException {
        if (socket == null) {
            connect();
        }
        if (!started) {
            started = true;
            new Thread(this).start();
            //restartHandle.post(restart);
        }
    }

    /**
     * main thread to service client connections.
     */
    @Override
    public void run() {
       running = true;

       do {
           try {
               if(socket == null){
                 connect();
                 request = new DatagramPacket(buffer, buffer.length);
               }else{
                 socket.receive(request);
                 final long rcvTime = timeStorageConsumer.getTime();
                 handlePacket(request, rcvTime);
               }

           } catch (IOException e) {
               Log.e("NTP", e.getMessage(), e);
               if (running) {
                   e.printStackTrace();
               }
               // otherwise socket thrown exception during shutdown
           } catch (Exception e) {
               // Don't fail for malformed packets
               Log.e("NTP", e.getMessage(), e);
               Log.i("NTP", "Sent by: " + request.getAddress() + " on port " + request.getPort());
           }
       } while (running);
   }

    /**
     * Handle incoming packet. If NTP packet is client-mode then respond
     * to that host with a NTP response packet otherwise ignore.
     *
     * Zac: This function has been altered to log requests and deliver satellite time, rather than system time.
     *
     * @param request incoming DatagramPacket
     * @param rcvTime time packet received
     * @throws IOException if an I/O error occurs.
     */
    protected void handlePacket(DatagramPacket request, long rcvTime) throws IOException {
        final RateLimiter rateLimiter = RateLimiter.create(5000.0);
        ServerLogDataPointGrouper.addPacket(new ServerLogDataPoint(timeStorageConsumer.getTime(), request, true));
        NtpV3Packet message = new NtpV3Impl();
        message.setDatagramPacket(request);
        System.out.printf("NTP packet from %s mode=%s%n", request.getAddress().getHostAddress(),
                NtpUtils.getModeName(message.getMode()));
        if (message.getMode() == NtpV3Packet.MODE_CLIENT) {
            NtpV3Packet response = new NtpV3Impl();

            response.setStratum(1);
            response.setMode(NtpV3Packet.MODE_SERVER);
            response.setVersion(NtpV3Packet.VERSION_3);
            response.setPrecision(-20);
            response.setPoll(0);
            response.setRootDelay(62);
            response.setRootDispersion((int) (16.51 * 65.536));

            // originate time as defined in RFC-1305 (t1)
            response.setOriginateTimeStamp(message.getTransmitTimeStamp());
            // Receive Time is time request received by server (t2)
            response.setReceiveTimeStamp(TimeStamp.getNtpTime(rcvTime));
            response.setReferenceTime(response.getReceiveTimeStamp());
            response.setReferenceId(0x4C434C00); // LCL (Undisciplined Local Clock)

            // Transmit time is time reply sent by server (t3)
            long appTime = timeStorageConsumer.getTime();
            response.setTransmitTime(TimeStamp.getNtpTime(appTime));

            DatagramPacket dp = response.getDatagramPacket();
            dp.setPort(request.getPort());
            dp.setAddress(request.getAddress());
            socket.send(dp);
            ServerLogDataPointGrouper.addPacket(new ServerLogDataPoint(appTime, dp, false));

        }

        // otherwise if received packet is other than CLIENT mode then ignore it

    }

    public Runnable restart = new Runnable() {
      @Override
      public void run() {
        // Do something here on the main thread
        if(running){
          if (socket != null) {
              socket.close();  // force closing of the socket
              socket = null;
              try {
                  connect();
                }
                catch(IOException e) {
                  Log.e("NTP", "restarting error " + e.getMessage(), e);
                }
          }


        }
        // Repeat this the same runnable code block again another 2 seconds
        restartHandle.postDelayed(restart, 300000);
      }
  };

    /**
     * Close server socket and stop listening.
     */
    public void stop() {
        running = false;
        if (socket != null) {
            socket.close();  // force closing of the socket
            socket = null;
        }
        started = false;
        //restartHandle.removeCallbacks(restart);
    }
}
