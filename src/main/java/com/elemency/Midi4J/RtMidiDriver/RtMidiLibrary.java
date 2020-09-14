package com.elemency.Midi4J.RtMidiDriver;

import com.elemency.Midi4J.MidiIn;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


/**
 * JNA Wrapper (bridge) to the <b><a href="https://www.music.mcgill.ca/~gary/rtmidi/">RtMidiDevice</a></b> native midi library<br>
 * This file was autogenerated by the great <a href="https://github.com/nativelibs4java/JNAerator">JNAerator</a> tool.<br>
 * It was then, rearranged into convenient categories.
 *
 * @author eLeMenCy
 */
public interface RtMidiLibrary extends Library {
    String JNA_LIBRARY_NAME = "RtMidiLibrary_4xx";
    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(RtMidiLibrary.JNA_LIBRARY_NAME);
    RtMidiLibrary INSTANCE = Native.load(RtMidiLibrary.JNA_LIBRARY_NAME, RtMidiLibrary.class);

    /* *********************************************************************************************************************
     * 											           RtMidiDevice API
     **********************************************************************************************************************/

    /**
     * Determine the available compiled MIDI APIs.<br>
     * <p>
     * If the given `apis` parameter is null, returns the number of available APIs.<br>
     * Otherwise, fill the given apis array with the RtMidi::Api values.<br>
     *
     * @param apis      An array or a null value.<br>
     * @param apis_size Number of elements pointed to by apis<br>
     * @return          number of items needed for apis array if apis==NULL, or<br>
     *                  number of items written to apis array otherwise.  A negative<br>
     *                  return value indicates an error.<br>
     *                  <p>
     *                  See ref: RtMidi::getCompiledApi().<br>
     *                  Original signature : <code>int rtmidi_get_compiled_api(RtMidiApi*, unsigned int)</code><br>
     *                  <i>native declaration : RtMidi/rtmidi_c.h:114</i>
     */
    int rtmidi_get_compiled_api(IntBuffer apis, int apis_size);

    /**
     * Return the name of a specified compiled MIDI API.<br>
     * See ref: RtMidi::getApiName().<br>
     * Original signature : <code>char* rtmidi_api_name(RtMidiApi)</code><br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/RtMidi/rtmidi_c.h:118</i>
     * @param api   id of the desired midi api
     * @return      String
     */
    String rtmidi_api_name(int api);

    /**
     * Return the display name of a specified compiled MIDI API.<br>
     * See ref: RtMidi::getApiDisplayName().<br>
     * Original signature : <code>char* rtmidi_api_display_name(RtMidiApi)</code><br>
     * <i>native declaration : RtMidi/rtmidi_c.h:122</i>
     * @param api   api id of the desired midi api
     * @return      String
     */
    String rtmidi_api_display_name(int api);

    /**
     * Return the compiled MIDI API having the given name.<br>
     * See ref :RtMidi::getCompiledApiByName().<br>
     * Original signature : <code>RtMidiApi rtmidi_compiled_api_by_name(const char*)</code><br>
     * <i>native declaration : RtMidi/rtmidi_c.h:126</i>
     * @param name  name of the desired Midi Api
     * @return  int
     */
    int rtmidi_compiled_api_by_name(String name);

    /**
     * Internal - Report an error.<br>
     * Original signature : <code>void rtmidi_error(RtMidiErrorType, const char*)</code><br>
     * <i>native declaration : RtMidi/rtmidi_c.h:129</i>
     *
     * @param type          error type to pass on driver
     * @param errorString   error string to pass on driver
     */
    void rtmidi_error(int type, String errorString);

    /* *********************************************************************************************************************
     * 											           RtMidiDevice Port API
     **********************************************************************************************************************/

    /**
     * Open a MIDI port.<br>
     *
     * @param device        Must be a valid device<br>
     * @param portNumber    Must be greater than 0<br>
     * @param portName      Name for the application port<br>
     * @param autoConnect   auto connect or not devices together<br>
     *                      See RtMidi::openPort().<br>
     *                      Original signature : <code>void rtmidi_open_port(RtMidiPtr, unsigned int, const char*)</code><br>
     *                      <i>native declaration : RtMidi/rtmidi_c.h:138</i>
     */
    void rtmidi_open_port(RtMidiDevice device, int portNumber, String portName, boolean autoConnect);

    /**
     * Creates a virtual MIDI port to which other software applications can<br>
     * connect.<br>
     *
     * @param device   Must be a valid device<br>
     * @param portName Name for the application port.<br>
     *                 See RtMidi::openVirtualPort().<br>
     *                 Original signature : <code>void rtmidi_open_virtual_port(RtMidiPtr, const char*)</code><br>
     *                 <i>native declaration : RtMidi/rtmidi_c.h:147</i>
     */
    void rtmidi_open_virtual_port(RtMidiDevice device, String portName);

    /**
     * Close a MIDI connection.<br>
     *
     * @param device Must be a valid device<br>
     *               See RtMidi::closePort().<br>
     *               Original signature : <code>void rtmidi_close_port(RtMidiPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:152</i>
     */
    void rtmidi_close_port(RtMidiDevice device);

    /**
     * Check a MIDI port connexion <br>
     *
     * @param device Must be a valid device<br>
     *               See RtMidi::isPortOpen().<br>
     *               Original signature : <code>void rtmidi_close_port(RtMidiPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:152</i>
     * @return       boolean
     */
    boolean rtmidi_is_port_open(RtMidiDevice device);

    /**
     * Return the number of available MIDI ports.<br>
     *
     * @param device Must be a valid device<br>
     *               See RtMidi::getPortCount().<br>
     *               Original signature : <code>int rtmidi_get_port_count(RtMidiPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:157</i>
     * @return       int
     */
    int rtmidi_get_port_count(RtMidiDevice device);

    /**
     * Return a string identifier for the specified MIDI input port number.<br>
     *
     * @param device     Must be a valid device<br>
     * @param portNumber Must be greater then 0<br>
     *                   See RtMidi::getPortName().<br>
     *                   Original signature : <code>char* rtmidi_get_port_name(RtMidiPtr, unsigned int)</code><br>
     *                   <i>native declaration : RtMidi/rtmidi_c.h:162</i>
     * @return           String
     */
    String rtmidi_get_port_name(RtMidiDevice device, int portNumber);

    /**
     * Return a string identifier for the specified MIDI input port number.<br>
     *
     * @param device   Must be a valid device<br>
     * @param portName Must be greater then 0<br>
     *                 See RtMidi::getPortName().<br>
     *                 Original signature : <code>char* rtmidi_get_port_name(RtMidiPtr, unsigned int)</code><br>
     *                 <i>native declaration : RtMidi/rtmidi_c.h:162</i>
     */
    void rtmidi_set_port_name(RtMidiDevice device, String portName);

    /**
     * Return a string identifier for the specified MIDI input port number.<br>
     *
     * @param device     Must be a valid device<br>
     * @param clientName Must be greater then 0<br>
     *                   See RtMidi::getPortName().<br>
     *                   Original signature : <code>char* rtmidi_get_port_name(RtMidiPtr, unsigned int)</code><br>
     *                   <i>native declaration : RtMidi/rtmidi_c.h:162</i>
     */
    void rtmidi_set_client_name(RtMidiDevice device, String clientName);


    /* *********************************************************************************************************************
     * 											           RtMidiDevice IN API
     **********************************************************************************************************************/

    /**
     * Create a default RtMidiInPtr value, with no initialization.<br>
     * Original signature : <code>RtMidiInPtr rtmidi_in_create_default()</code><br>
     * <i>native declaration : RtMidi/rtmidi_c.h:167</i>
     * @return               RtMidiDevice
     */
    RtMidiDevice rtmidi_in_create_default();

    /**
     * Create a  RtMidiInPtr value, with given api, clientName and queueSizeLimit.<br>
     *
     * @param api            An optional API id can be specified.<br>
     * @param clientName     An optional client name can be specified. This<br>
     *                       will be used to group the ports that are created<br>
     *                       by the application.<br>
     * @param queueSizeLimit An optional size of the MIDI input queue can be<br>
     *                       specified.<br>
     *                       See RtMidiIn::RtMidiIn().<br>
     *                       Original signature : <code>RtMidiInPtr rtmidi_in_create(RtMidiApi, const char*, unsigned int)</code><br>
     *                       <i>native declaration : RtMidi/rtmidi_c.h:180</i>
     * @return               RtMidiDevice
     */
    RtMidiDevice rtmidi_in_create(int api, String clientName, int queueSizeLimit);

    /**
     * Free the given RtMidiInPtr.<br>
     *
     * @param device Must be a valid device<br>
     *               Original signature : <code>void rtmidi_in_free(RtMidiInPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:183</i>
     */
    void rtmidi_in_free(RtMidiDevice device);

    /**
     * Returns the MIDI API specifier for the given instance of RtMidiIn.<br>
     *
     * @param device Must be a valid device.<br>
     *               See ref: RtMidiIn::getCurrentApi().<br>
     *               Original signature : <code>RtMidiApi rtmidi_in_get_current_api(RtMidiPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:187</i>
     * @return       int
     */
    int rtmidi_in_get_current_api(RtMidiDevice device);

    /**
     * Set a callback function to be invoked for incoming MIDI messages.<br>
     *
     * @param device   Must be a valid device.<br>
     * @param callback Name of the callback function.<br>
     * @param userData Supplementary data sent by user. <br>
     *                 See ref: RtMidiIn::setCallback().<br>
     *                 Original signature : <code>void rtmidi_in_set_callback(RtMidiInPtr, RtMidiCCallback, void*)</code><br>
     *                 <i>native declaration : RtMidi/rtmidi_c.h:191</i>
     */
    void rtmidi_in_set_callback(RtMidiDevice device, MidiIn.MidiInCallback callback, Pointer userData);

    /**
     * Cancel use of the current callback function (if one exists).<br>
     *
     * @param device Must be a valid device<br>
     *               See ref: RtMidiIn::cancelCallback().<br>
     *               Original signature : <code>void rtmidi_in_cancel_callback(RtMidiInPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:195</i>
     */
    void rtmidi_in_cancel_callback(RtMidiDevice device);

    /**
     * Specify whether certain MIDI message types should be queued or ignored during input.<br>
     *
     * @param device    Must be a valid device<br>
     * @param midiSysex Midi System exclusive byte<br>
     * @param midiTime  Midi event time byte<br>
     * @param midiSense Midi sensing byte<br>
     *                  See ref: RtMidiIn::ignoreTypes().<br>
     *                  Original signature : <code>void rtmidi_in_ignore_types(RtMidiInPtr, bool, bool, bool)</code><br>
     *                  <i>native declaration : RtMidi/rtmidi_c.h:199</i>
     */
    void rtmidi_in_ignore_types(RtMidiDevice device, byte midiSysex, byte midiTime, byte midiSense);

    /**
     * Fill the user-provided array with the data bytes for the next available<br>
     * MIDI message in the input queue and return the event delta-time in seconds.<br>
     *
     * @param device  Must be a valid device<br>
     * @param message Must point to a char* that is already allocated.<br>
     *                SYSEX messages maximum size being 1024, a statically<br>
     *                allocated array could<br>
     *                be sufficient.<br>
     * @param size    Is used to return the size of the message obtained.<br>
     *                Must be set to the size of \ref message when calling.<br>
     *                See RtMidiIn::getMessage().<br>
     *                Original signature : <code>double rtmidi_in_get_message(RtMidiInPtr, unsigned char*, size_t*)</code><br>
     *                <i>native declaration : RtMidi/rtmidi_c.h:213</i>
     * @return        double
     */
    double rtmidi_in_get_message(RtMidiDevice device, ByteBuffer message, NativeSizeByReference size);

    /* *********************************************************************************************************************
     * 											           RtMidiDevice OUT API
     **********************************************************************************************************************/

    /**
     * Create a default RtMidiInPtr value, with no initialization.<br>
     * Original signature : <code>RtMidiOutPtr rtmidi_out_create_default()</code><br>
     * <i>native declaration : RtMidi/rtmidi_c.h:218</i>
     * @return RtMidiDevice
     */
    RtMidiDevice rtmidi_out_create_default();

    /**
     * Create a RtMidiOutPtr value, with given and clientName.<br>
     *
     * @param api        An optional API id can be specified.<br>
     * @param clientName An optional client name can be specified. This<br>
     *                   will be used to group the ports that are created<br>
     *                   by the application.<br>
     *                   See RtMidiOut::RtMidiOut().<br>
     *                   Original signature : <code>RtMidiOutPtr rtmidi_out_create(RtMidiApi, const char*)</code><br>
     *                   <i>native declaration : RtMidi/rtmidi_c.h:229</i>
     * @return           RtMidiDevice
     */
    RtMidiDevice rtmidi_out_create(int api, String clientName);

    /**
     * Free the given RtMidiOutPtr.<br>
     *
     * @param device Must be a valid device<br>
     *               Original signature : <code>void rtmidi_out_free(RtMidiOutPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:232</i>
     */
    void rtmidi_out_free(RtMidiDevice device);

    /**
     * Returns the MIDI API specifier for the given instance of RtMidiOut.<br>
     *
     * @param device Must be a valid device<br>
     *               See ref: RtMidiOut::getCurrentApi().<br>
     *               Original signature : <code>RtMidiApi rtmidi_out_get_current_api(RtMidiPtr)</code><br>
     *               <i>native declaration : RtMidi/rtmidi_c.h:236</i>
     * @return        int
     */
    int rtmidi_out_get_current_api(RtMidiDevice device);

    /**
     * Immediately send a single message out an open MIDI output port.<br>
     *
     * @param device  Must be a valid device<br>
     * @param message Message to send<br>
     * @param length  Message length<br>
     *                See ref: RtMidiOut::sendMessage().<br>
     *                Original signature : <code>int rtmidi_out_send_message(RtMidiOutPtr, const unsigned char*, int)</code><br>
     *                <i>native declaration : RtMidi/rtmidi_c.h:240</i>
     * @return        int
     */
    int rtmidi_out_send_message(RtMidiDevice device, byte[] message, int length);
}
