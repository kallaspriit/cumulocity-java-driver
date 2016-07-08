package com.stagnationlab.c8y.driver.actuators;

import c8y.Hardware;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryRelayActuator extends SimulatedRelayActuator {

    private static Logger log = LoggerFactory.getLogger(RaspberryRelayActuator.class);

    private boolean isRaspberryPi = false;
    private GpioPinDigitalOutput relayPin;
    private Pin pinName;

    public RaspberryRelayActuator(String id, Pin pinName) {
        super(id);

        this.pinName = pinName;
    }

    @Override
    public void initialize() throws Exception {
        String osName = System.getProperty("os.name");

        if (!osName.toLowerCase().contains("linux")) {
            log.info("not linux platform (" + osName + "), skipping initialization");

            throw new Exception("Skipping initialization on a non-linux platform");
        }

        log.info("initializing on " + osName);

        try {
            GpioController gpio = GpioFactory.getInstance();

            relayPin = gpio.provisionDigitalOutputPin(pinName, "Relay", PinState.LOW);
            relayPin.setShutdownOptions(true, PinState.HIGH);

            isRaspberryPi = true;
        } catch (Exception e) {
            throw new Exception("raspberry pi not found");
        }
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation parent) {
        if (!isRaspberryPi) {
            log.info("not a raspberry pi platform, not creating a child");

            return;
        }

        super.discoverChildren(parent);
    }

    @Override
    public void start() {
        if (!isRaspberryPi) {
            log.info("not a raspberry pi platform, not starting the driver");

            return;
        }

        super.start();
    }

    @Override
    protected Hardware getHardware() {
        return new Hardware(
                "Raspberry Relay Actuator",
                "876123688932234",
                "1.0.0"
        );
    }

    @Override
    protected void applyRelayState(boolean isRelayOn) {
        log.info("turning raspberry led " + (isRelayOn ? "on" : "off") + " on pin " + pinName.getName());

        relayPin.setState(isRelayOn);
    }

    /*
    private static void showSystemInfo() throws IOException, InterruptedException, ParseException {
        log.trace("----------------------------------------------------");
        log.trace("HARDWARE INFO");
        log.trace("----------------------------------------------------");
        log.trace("Serial Number     :  " + SystemInfo.getSerial());
        log.trace("CPU Revision      :  " + SystemInfo.getCpuRevision());
        log.trace("CPU Architecture  :  " + SystemInfo.getCpuArchitecture());
        log.trace("CPU Part          :  " + SystemInfo.getCpuPart());
        log.trace("CPU Temperature   :  " + SystemInfo.getCpuTemperature());
        log.trace("CPU Core Voltage  :  " + SystemInfo.getCpuVoltage());
        log.trace("CPU Model Name    :  " + SystemInfo.getModelName());
        log.trace("Processor         :  " + SystemInfo.getProcessor());
        log.trace("Hardware Revision :  " + SystemInfo.getRevision());
        log.trace("Is Hard Float ABI :  " + SystemInfo.isHardFloatAbi());
        log.trace("Board Type        :  " + SystemInfo.getBoardType().name());

        log.trace("----------------------------------------------------");
        log.trace("MEMORY INFO");
        log.trace("----------------------------------------------------");
        log.trace("Total Memory      :  " + SystemInfo.getMemoryTotal());
        log.trace("Used Memory       :  " + SystemInfo.getMemoryUsed());
        log.trace("Free Memory       :  " + SystemInfo.getMemoryFree());
        log.trace("Shared Memory     :  " + SystemInfo.getMemoryShared());
        log.trace("Memory Buffers    :  " + SystemInfo.getMemoryBuffers());
        log.trace("Cached Memory     :  " + SystemInfo.getMemoryCached());
        log.trace("SDRAM_C Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_C());
        log.trace("SDRAM_I Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_I());
        log.trace("SDRAM_P Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_P());

        log.trace("----------------------------------------------------");
        log.trace("OPERATING SYSTEM INFO");
        log.trace("----------------------------------------------------");
        log.trace("OS Name           :  " + SystemInfo.getOsName());
        log.trace("OS Version        :  " + SystemInfo.getOsVersion());
        log.trace("OS Architecture   :  " + SystemInfo.getOsArch());
        log.trace("OS Firmware Build :  " + SystemInfo.getOsFirmwareBuild());
        log.trace("OS Firmware Date  :  " + SystemInfo.getOsFirmwareDate());

        log.trace("----------------------------------------------------");
        log.trace("JAVA ENVIRONMENT INFO");
        log.trace("----------------------------------------------------");
        log.trace("Java Vendor       :  " + SystemInfo.getJavaVendor());
        log.trace("Java Vendor URL   :  " + SystemInfo.getJavaVendorUrl());
        log.trace("Java Version      :  " + SystemInfo.getJavaVersion());
        log.trace("Java VM           :  " + SystemInfo.getJavaVirtualMachine());
        log.trace("Java Runtime      :  " + SystemInfo.getJavaRuntime());

        log.trace("----------------------------------------------------");
        log.trace("NETWORK INFO");
        log.trace("----------------------------------------------------");

        log.trace("Hostname          :  " + NetworkInfo.getHostname());

        for (String ipAddress : NetworkInfo.getIPAddresses())
            log.trace("IP Addresses      :  " + ipAddress);
        for (String fqdn : NetworkInfo.getFQDNs())
            log.trace("FQDN              :  " + fqdn);
        for (String nameserver : NetworkInfo.getNameservers())
            log.trace("Nameserver        :  " + nameserver);

        log.trace("----------------------------------------------------");
        log.trace("CODEC INFO");
        log.trace("----------------------------------------------------");
        log.trace("H264 Codec Enabled:  " + SystemInfo.getCodecH264Enabled());
        log.trace("MPG2 Codec Enabled:  " + SystemInfo.getCodecMPG2Enabled());
        log.trace("WVC1 Codec Enabled:  " + SystemInfo.getCodecWVC1Enabled());

        log.trace("----------------------------------------------------");
        log.trace("CLOCK INFO");
        log.trace("----------------------------------------------------");
        log.trace("ARM Frequency     :  " + SystemInfo.getClockFrequencyArm());
        log.trace("CORE Frequency    :  " + SystemInfo.getClockFrequencyCore());
        log.trace("H264 Frequency    :  " + SystemInfo.getClockFrequencyH264());
        log.trace("ISP Frequency     :  " + SystemInfo.getClockFrequencyISP());
        log.trace("V3D Frequency     :  " + SystemInfo.getClockFrequencyV3D());
        log.trace("UART Frequency    :  " + SystemInfo.getClockFrequencyUART());
        log.trace("PWM Frequency     :  " + SystemInfo.getClockFrequencyPWM());
        log.trace("EMMC Frequency    :  " + SystemInfo.getClockFrequencyEMMC());
        log.trace("Pixel Frequency   :  " + SystemInfo.getClockFrequencyPixel());
        log.trace("VEC Frequency     :  " + SystemInfo.getClockFrequencyVEC());
        log.trace("HDMI Frequency    :  " + SystemInfo.getClockFrequencyHDMI());
        log.trace("DPI Frequency     :  " + SystemInfo.getClockFrequencyDPI());
    }
    */
}
