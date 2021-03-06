/*
 * Copyright (C) 2020 - eLeMenCy, All Rights Reserved
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiSysApiMgr;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MidiDeviceTest {

    private MidiIn midi4jIn = null;
    private MidiOut midi4jOut = null;


    @BeforeEach
    void init()
    {
        this.midi4jOut = new MidiOut(RtMidiSysApiMgr.Api.UNIX_JACK.getIntValue(), "Midi4J");
        this.midi4jIn = new MidiIn(RtMidiSysApiMgr.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, false);
    }

    @AfterEach
    void clearup() {
        midi4jIn.close();
        midi4jOut.close();
    }

    @Test
    @Disabled
    void getRtMidiDevice() {

    }

    @Test
    void getDeviceClassName() {
        assertEquals("MidiOut", midi4jOut.getSourceDeviceClassName());
        assertEquals("MidiIn", midi4jIn.getSourceDeviceClassName());
    }

    @Test
    void getTargetDeviceType() {
        assertEquals("In", midi4jOut.getTargetDeviceType());
        assertEquals("Out", midi4jIn.getTargetDeviceType());
    }

    @Test
    void getDeviceType() {
        assertEquals("Out", midi4jOut.getSourceDeviceType());
        assertEquals("In", midi4jIn.getSourceDeviceType());
    }

    @Test
    @Disabled
    void error() {
    }

    @Test
    void getCurrentApiId() {
        assertEquals(3, midi4jOut.getCurrentApiId());
        assertEquals(2, midi4jIn.getCurrentApiId());
    }

    @Test
    void getCurrentApiName() {
        assertEquals("Jack", midi4jOut.getCurrentApiName());
        assertEquals("ALSA", midi4jIn.getCurrentApiName());
    }

    @Test
    void getTargetDeviceName() {
        assertEquals("Calf Studio Gear", midi4jOut.getTargetDeviceName(1));
        assertEquals("FCA1616", midi4jIn.getTargetDeviceName(1));
    }

    @Test
    void getDeviceName() {
        assertEquals("Midi4J", midi4jOut.getSourceDeviceName());
        assertEquals("Midi4J", midi4jIn.getSourceDeviceName());
    }

    @Test
    void setDeviceName() {
        if (midi4jOut.getCurrentApiName().equals("Jack") ||
                midi4jIn.getCurrentApiName().equals("Jack")) {
            return;
        }
        midi4jOut.setSourceDeviceName("DeviceOUT");
        midi4jIn.setSourceDeviceName("DeviceIN");
        assertEquals("DeviceOUT", midi4jOut.getSourceDeviceName());
        assertEquals("DeviceIN", midi4jIn.getSourceDeviceName());
    }

    @Test
    void getPortName() {
        midi4jOut.setSourcePortName("");
        midi4jIn.setSourcePortName("");
        assertEquals("OUT", midi4jOut.getSourcePortName());
        assertEquals("IN", midi4jIn.getSourcePortName());

        midi4jOut.setSourcePortName("PortOUT");
        midi4jIn.setSourcePortName("PortIN");
        assertEquals("PortOUT", midi4jOut.getSourcePortName());
        assertEquals("PortIN", midi4jIn.getSourcePortName());
    }

    @Test
    void setPortNameEmpty() {
        midi4jOut.setSourcePortName("");
        midi4jIn.setSourcePortName("");
        assertEquals("OUT", midi4jOut.getSourcePortName());
        assertEquals("IN", midi4jIn.getSourcePortName());
    }
    @Test
    void setPortName() {
        midi4jOut.setSourcePortName("PortOUT");
        midi4jIn.setSourcePortName("PortIN");
        assertEquals("PortOUT", midi4jOut.getSourcePortName());
        assertEquals("PortIN", midi4jIn.getSourcePortName());
    }

    @Test
    void isDeviceOpen() {
        assertFalse(midi4jOut.isSourceDeviceOpen());
        assertFalse(midi4jIn.isSourceDeviceOpen());
    }

    @Test
    @Disabled
    void free() {
    }

    @Test
    void connectWithAutoConnect() {
        assertTrue(midi4jOut.connect("OUT", 1, true));
        assertTrue(midi4jIn.connect("IN", 2, true));
    }

    @Test
    void connectWithoutAutoConnect() {
        assertTrue(midi4jOut.connect("OUT", 1, false));
        assertTrue(midi4jIn.connect("IN", 2, false));
    }

    @Test
    @Disabled
    void openVirtualDevice() {
    }

    @Test
    @Disabled
    void getDeviceCount() {

    }

    @Test
    @Disabled
    void getFullDeviceDetails() {
    }

    @Test
    void getTargetPortName() {
        assertEquals("Organ MIDI In", midi4jOut.getTargetPortName(1));
        assertEquals("FCA1616 MIDI 1", midi4jIn.getTargetPortName(1));
    }

    @Test
    @Disabled
    void listTargetDevices() {

    }

    @Test
    @Disabled
    void closeDevice() {
    }
}