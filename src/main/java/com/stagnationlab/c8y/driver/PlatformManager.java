package com.stagnationlab.c8y.driver;

import c8y.Hardware;
import org.omg.CORBA.UNKNOWN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import c8y.lx.driver.HardwareProvider;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class PlatformManager {

    private static Logger log = LoggerFactory.getLogger(PlatformManager.class);

    public static Hardware resolveHardware() throws Exception {
        /*
        String command = "wmic csproduct get name,identifyingnumber /format:list";
        Process process = Runtime.getRuntime().exec(command);

        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] keyValuePair = line.trim().split("=");

                if ("Name".equals(keyValuePair[0])) {
                    hardware.setModel(keyValuePair[1]);
                }

                if ("IdentifyingNumber".equals(keyValuePair[0])) {
                    hardware.setSerialNumber(keyValuePair[1]);
                }
            }
        }
        */

        String mac = getMac();
        String hostname = getHostname();
        String osName = System.getProperty("os.name");

        Hardware hardware = new Hardware(
                hostname,
                mac,
                osName
        );

        log.info(
                "detected hardware model: " + hardware.getModel() +
                ", serial number: " + hardware.getSerialNumber() +
                ", version: " + hardware.getRevision()
        );

        return hardware;
    }

    private static String getMac() throws UnknownHostException, SocketException {
        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);

        byte[] mac = network.getHardwareAddress();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }

        return sb.toString();
    }

    private static String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

}
