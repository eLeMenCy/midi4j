package com.elemency.Midi4J;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// https://www.nyu.edu/classes/bello/FMT_files/9_MIDI_code.pdf

public class MidiMessage implements Cloneable {

    private final Logger logger = LoggerFactory.getLogger(MidiMessage.class);
    private int midiDataSize = 0;
    private byte[] midiData;
    private double timeStamp = 0;


    /**
     * Creates a 3-byte short midi message.
     *
     * @param byte0
     * @param byte1
     * @param byte2
     * @param timeStamp
     */
    public MidiMessage(int byte0, int byte1, int byte2, double timeStamp) {
        midiDataSize = 3;
        this.timeStamp = timeStamp;

        if (byte0 >= 0xF0) {
            throw new MidiException("The Status of a 3 byte short message should be < 0xF0: 0x" + Integer.toHexString(byte0).toUpperCase());
        }

        midiData = new byte[midiDataSize];
        midiData[0] = (byte) byte0;
        midiData[1] = (byte) byte1;
        midiData[2] = (byte) byte2;
    }

    /**
     * Creates a 2-byte short midi message.
     *
     * @param byte0
     * @param byte1
     * @param timeStamp
     */
    public MidiMessage(int byte0, int byte1, double timeStamp) {
        midiDataSize = 2;
        this.timeStamp = timeStamp;

        if (byte0 >= 0xF0) {
            throw new MidiException("The Status of a 2 byte short message should be < 0xF0: 0x" + Integer.toHexString(byte0).toUpperCase());
        }

        midiData = new byte[this.midiDataSize];
        midiData[0] = (byte) byte0;
        midiData[1] = (byte) byte1;
    }

    /**
     * Creates a 1-byte short midi message.
     *
     * @param byte0
     * @param timeStamp
     */
    public MidiMessage(int byte0, double timeStamp) {
        midiDataSize = 1;
        this.timeStamp = timeStamp;

        if (byte0 >= 0xF0) {
            throw new MidiException("The Status of a 1 byte short message should be < 0xF0: 0x" + Integer.toHexString(byte0).toUpperCase());
        }

        midiData = new byte[this.midiDataSize];
        midiData[0] = (byte) byte0;
    }

    /**
     * Creates a midi message from a block of data.
     *
     * @param midiData
     * @param datasize
     * @param timeStamp
     */
    public MidiMessage(byte[] midiData, int datasize, double timeStamp) {
        midiDataSize = datasize;
        this.timeStamp = timeStamp;

//        this.midiDataSize = 0;
//        midiData = null;

        if (midiDataSize < 1) {
            throw new MidiException("A multibyte message size should be > 0");
        }

        this.midiData = new byte[this.midiDataSize];
        this.midiData = midiData;
    }

    /**
     * Creates a midi message from a native jna block of data.
     *
     * @param midiData
     * @param midiDataSize
     * @param timeStamp
     */
    public MidiMessage(@NotNull Pointer midiData,
                       @NotNull NativeSize midiDataSize,
                       double timeStamp) {
        this.midiDataSize = midiDataSize.intValue();
        this.timeStamp = timeStamp;

//        this.midiDataSize = 0;
//        midiData = null;

        if (midiData == null)
            throw new NullPointerException("A native Midi Message can't be null.");

        if (this.midiDataSize < 1)
            throw new MidiException("A native Midi Message size should be > 0.");

        // Byte array to receive the event from native pointer.
        this.midiData = new byte[this.midiDataSize];

        // Read native memory data into our data byte array.
        midiData.read(0, this.midiData, 0, this.midiDataSize);
    }

    /**
     * Returns the name of a midi note number.
     * E.g "C", "D#", etc.
     *
     * @param noteNumber          the midi note number, 0 to 127
     * @param useSharps           if true, sharpened notes are used, e.g. "C#", otherwise they'll be flattened, e.g. "Db"
     * @param includeOctaveNumber if true, the octave number will be appended to the string, e.g. "C#4"
     * @param octaveNumForMiddleC if an octave number is being appended, this indicates the number that will be used for middle C's octave
     * @link getMidiNoteInHertz
     */
    public static String getMidiNoteName(int noteNumber,
                                         boolean useSharps,
                                         boolean includeOctaveNumber,
                                         int octaveNumForMiddleC) {
        String[] sharpNoteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        String[] flatNoteNames = {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};

        if (noteNumber > 0 && noteNumber < 128) {
            String s = (useSharps ? sharpNoteNames[noteNumber % 12] : flatNoteNames[noteNumber % 12]);
            if (includeOctaveNumber) {
                s += (noteNumber / 12 + (octaveNumForMiddleC - 5));
            }

            return s;
        }

        return "--";
    }

    /**
     * Creates a system-exclusive message.
     * The data passed in is wrapped with header and tail bytes of 0xF0 and 0xF7.
     */
    public static MidiMessage createSysExMessage(byte[] sysexData, int dataSize, double timeStamp) {
        if (dataSize < 1) {
            throw new MidiException("A Sysex midi Message size should be > 0");
        }

        boolean headerExist = sysexData[0] == (byte) 0xF0;
        boolean tailExist = sysexData[dataSize - 1] == (byte) 0xF7;

        // Well formed sysex? Return new Sysex midi message.
        if (headerExist && tailExist) {
            return new MidiMessage(sysexData, dataSize, 0);

        }

        int sizeIncrease = 0;

        // Header or tail byte missing? Increase result array size by one...
        if (headerExist || tailExist) {
            sizeIncrease = 1;

            //... and by 2 if both are missing.
        } else {
            sizeIncrease = 2;
        }

        // Create our result array with correct size...
        byte[] result = new byte[dataSize + sizeIncrease];

        int destPos = 0;

        //... add the header if missing...
        if (!headerExist) {
            result[0] = (byte) 0xF0;
            destPos = 1;
        }
        //... add original data into result array...
        System.arraycopy(sysexData, 0, result, destPos, dataSize);

        //... add tail if missing.
        if (!tailExist) {
            result[dataSize + sizeIncrease - 1] = (byte) 0xF7;
        }

        //Time to return our freshly created Sysex midi message.
        return new MidiMessage(result, dataSize + sizeIncrease, 0);
    }

    /**
     * @param command
     * @param channel
     * @return
     */
    public static int createStatusByte(int command, int channel) {
        return ((command & 0xF0) | ((channel - 1) & 0x0F));
    }

    /**
     * Creates a key-down message (using an integer velocity).
     *
     * @param channel    the midi channel, in the range 1 to 16
     * @param noteNumber the key number, 0 to 127
     * @param velocity   in the range 0 to 127
     *                   see isNoteOn
     */
    public static MidiMessage noteOn(int channel, int noteNumber, int velocity, double timeStamp) {
        return new MidiMessage(createStatusByte(0x90, channel), (noteNumber & 127), velocity, timeStamp);
    }

    /**
     * Creates a key-down message (using a floating-point velocity).
     *
     * @param channel    the midi channel, in the range 1 to 16
     * @param noteNumber the key number, 0 to 127
     * @param velocity   in the range 0 to 1.0
     *                   see isNoteOn
     */
    public static MidiMessage noteOn(int channel, int noteNumber, float velocity, double timeStamp) {
        return noteOn(channel, noteNumber, (int) (127.0f * velocity), timeStamp);
    }

    /**
     * Creates a key-up message.
     *
     * @param channel    the midi channel, in the range 1 to 16
     * @param noteNumber the key number, 0 to 127
     * @param velocity   in the range 0 to 127
     * @link isNoteOff
     */
    public static MidiMessage noteOff(int channel, int noteNumber, int velocity, double timeStamp) {
        return new MidiMessage(createStatusByte(0x80, channel), noteNumber, velocity, timeStamp);
    }

    /**
     * Creates a key-up message.
     *
     * @param channel    the midi channel, in the range 1 to 16
     * @param noteNumber the key number, 0 to 127
     * @param velocity   in the range 0 to 1.0
     * @link isNoteOff
     */
    public static MidiMessage noteOff(int channel, int noteNumber, float velocity, double timeStamp) {
        return noteOff(channel, noteNumber, (int) (127.0f * velocity), timeStamp);
    }

    /**
     * Creates a key-up message.@param channel
     * the midi channel, in the range 1 to 16
     *
     * @param noteNumber the key number, 0 to 127
     * @link isNoteOff
     */
    public static MidiMessage noteOff(int channel, int noteNumber) {
        return noteOff(channel, noteNumber, 0, 0);
    }

    /**
     * Creates a program-change message.
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param programNumber the midi program number, 0 to 127
     * @link isProgramChange, getGMInstrumentName
     */
    static MidiMessage programChange(int channel, int programNumber) {
        return new MidiMessage(createStatusByte(0xC0, channel), programNumber & 0x7F);
    }

    /**
     * Creates a pitch-wheel move message.
     *
     * @param channel  the midi channel, in the range 1 to 16
     * @param position the wheel position, in the range 0 to 16383
     * @link isPitchWheel
     */
    static MidiMessage pitchWheel(int channel, int position) {
        return new MidiMessage(createStatusByte(0xE0, channel), position & 127, (position >> 7) & 127, 0);
    }

    /**
     * Creates a channel-pressure change event.
     *
     * @param channel  the midi channel: 1 to 16
     * @param pressure the pressure, 0 to 127
     * @link isChannelPressure
     */
    static MidiMessage channelPressureChange(int channel, int pressure) {
        return new MidiMessage(createStatusByte(0xD0, channel), pressure & 0x7F, 0);
    }

    /**
     * Creates an aftertouch message.
     *
     * @param channel          the midi channel, in the range 1 to 16
     * @param noteNumber       the key number, 0 to 127
     * @param aftertouchAmount the amount of aftertouch, 0 to 127
     * @link isAftertouch
     */
    static MidiMessage aftertouchChange(int channel, int noteNumber, int aftertouchAmount) {
        return new MidiMessage(createStatusByte(0xA0, channel), noteNumber & 0x7F, aftertouchAmount & 0x7F);
    }

    /**
     * Creates a controller message.
     *
     * @param channel        the midi channel, in the range 1 to 16
     * @param controllerType the type of controller
     * @param value          the controller value
     * @link isController
     */
    static MidiMessage controllerEvent(int channel, int controllerType, int value) {
        return new MidiMessage(createStatusByte(0xB0, channel), (controllerType & 127), (value & 127), 0);
    }

    /**
     * Creates an all-notes-off message.
     *
     * @param channel the midi channel, in the range 1 to 16
     * @link isAllNotesOff
     */
    public static MidiMessage allNotesOff(int channel) {
        return controllerEvent(channel, 123, 0);
    }

    /**
     * Creates an all-sound-off message.
     *
     * @param channel the midi channel, in the range 1 to 16
     * @link isAllSoundOff
     */
    public static MidiMessage allSoundOff(int channel) {
        return controllerEvent(channel, 120, 0);
    }

    /**
     * Creates an all-controllers-off message.
     *
     * @param channel the midi channel, in the range 1 to 16
     * @link isResetAllControllers
     */
    public static MidiMessage allControllersOff(int channel) {
        return controllerEvent(channel, 121, 0);
    }

    /**
     * Get the current midi data block
     *
     * @return byte[]
     */
    public byte[] getMidiData() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't return it.");
        }

        return midiData;
    }

    /**
     * Get the current midi data block size
     *
     * @return int
     */
    public int getMidiDataSize() {
        if (midiDataSize < 1) {
            throw new MidiException("midiDataSize must be > 0");
        }

        return midiDataSize;
    }

    /**
     * Returns a human-readable description of the midi message as a string
     *
     * @return "Note On C#3 Velocity 120 Channel 1"
     */
    public String getDescription() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't return its description");
        }

        if (isNoteOn(false)) {
//            return String.format("%02d:%02d:%02d:%03d - ",hours, minutes, seconds, millis);

            return String.format(
                    "Note ON  %-4s Velocity %03d Channel %02d",
                    getMidiNoteName(getNoteNumber(), true, true, 3),
                    getVelocity(),
                    getChannel()
            );
        }

        if (isNoteOff(true)) {
            return String.format(
                    "Note OFF %-4s Velocity %03d Channel %02d",
                    getMidiNoteName(getNoteNumber(), true, true, 3),
                    getVelocity(),
                    getChannel()
            );
        }

        if (isProgramChange()) {
            return String.format(
                    "Program change %03d Channel %02d",
                    getProgramChangeNumber(),
                    getChannel()
            );
        }

        if (isPitchWheel()) {
            return String.format(
                    "Pitchbend %05d Channel %02d",
                    getPitchWheelValue(),
                    getChannel()
            );
        }

        if (isPolyAftertouch()) {
            return String.format(
                    "Poly Aftertouch %-4s: %03d Channel %02d",
                    getMidiNoteName(getNoteNumber(), true, true, 3),
                    getPolyAftertouchValue(),
                    getChannel()
            );
        }

        if (isChannelPressure()) {
            return String.format(
                    "Channel Aftertouch %03d Channel %02d",
                    getChannelPressureValue(),
                    getChannel()
            );
        }

        if (isAllNotesOff()) {
            return String.format(
                    "All notes off Channel %02d",
                    getChannel()
            );
        }

        if (isAllSoundOff()) {
            return String.format(
                    "All sound off Channel %02d",
                    getChannel()
            );
        }

        if (isSysEx()) {
            return String.format(
                    "SysEx: %-4s",
                    midiDataToHexString()
            );
        }

        if (isMetaEvent()) {
            return "Meta event";
        }

        if (isController()) {
            String name = getControllerName(getControllerNumber());

            if (name.isEmpty())
                name = String.valueOf(getControllerNumber());

            return String.format(
                    "CC %s: %03d Channel %02d",
                    name.equals("--") ? getControllerNumber() : name,
                    getControllerValue(),
                    getChannel()
            );
        }

        return "Midi message description (HexString): " + midiDataToHexString();
    }

    /**
     * Returns raw midi data as a HexString.
     *
     * @return
     */
    public String midiDataToHexString() {
        String hexString = "No Midi data to process!";

        if (midiData == null || (midiData[0] & 0xFF) < 1 || midiDataSize < 1) {
            return hexString;
        }

        for (int i = 0; i < midiDataSize; i++) {

            if (i == 0)
                hexString = (midiData[0] & 0xFF) == 0xF0 ? "Header " : "Status ";

            else if (i == midiDataSize - 1 && isSysEx())
                hexString += (midiData[0] & 0xFF) == 0xF0 ? "Tail " : " ";

            hexString += String.format(
                    "0x%s(%02d)",
                    Integer.toHexString(midiData[i] & 0xFF).toUpperCase(),
                    midiData[i] & 0xFF
            );
            hexString += i < midiDataSize - 1 ? ", " : "";

//                hexString += "0x" + Integer.toHexString(midiData[i] & 0xFF) + "(" + (midiData[i] & 0xFF) + "), ";
        }
        return hexString;
    }

    /**
     * Returns this message's timestamp.
     *
     * @return
     */
    public double getTimeStamp() {
        return timeStamp;
    }

    /**
     * Changes the message's associated timestamp.
     *
     * @param newTimestamp
     */
    public void setTimeStamp(double newTimestamp) {
        timeStamp = newTimestamp;
    }

    /**
     * Adds a value to the message's timestamp.
     */
    public void addToTimeStamp(double delta) {
        timeStamp += delta;
    }

    /**
     * Return a copy of this message with a new timestamp.
     *
     * @param newTimestamp
     * @return
     */
    public MidiMessage withTimeStamp(double newTimestamp) throws CloneNotSupportedException {
        MidiMessage midiMessage = null;
        midiMessage = (MidiMessage) this.clone();

        if (midiMessage == null) {
            throw new MidiException("Attempt to clone current midiMessage and change its timestamp failed.");
        }

        midiMessage.timeStamp = newTimestamp;

        return midiMessage;
    }

    /**
     * Returns current message's midi channel.
     */
    public int getChannel() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get the Channel number.");
        }

        if ((midiData[0] & 0xF0) == 0xF0) {
            return 0;
        }

        return (midiData[0] & 0xF) + 1;
    }

    /**
     * Changes the message's midi channel.
     *
     * @param number
     */
    public void setChannel(int number) {
        if (number < 1 || number > 16) {
            throw new MidiException("A Midi voice channel can only be between 1 and 16");
        }

        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't set the Channel to " + number);
        }

        midiData[0] = (byte) ((midiData[0] & 0xF0) | ((number - 1) & 0xF));
    }

    /**
     * Returns true if the message applies to the given midi channel.
     *
     * @param number
     * @return
     */
    public boolean isForChannel(int number) {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if current message applies to channel " + number);
        }

        return (midiData[0] & 0xF) + 1 == (number & 0xF);
    }

    /**
     * Returns true if this is a system-exclusive message.
     *
     * @return
     */
    public boolean isSysEx() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a SysEx.");
        }

        return (midiData[0] & 0xF0) == 0xF0;

    }

    /**
     * Returns a byte array of sysex data inside the message.
     *
     * @return
     */
    public byte[] getSysExData() {
        return isSysEx() ? midiData : null;
    }

    /**
     * Returns the size of the sysex data.
     *
     * @return
     */
    public int getSysExDataSize() {
        if (midiDataSize < 1) {
            throw new MidiException("A SysEx message size must be > 0.");
        }

        return isSysEx() ? midiDataSize - 2 : 0;

    }

    /**
     * Returns true if this message is a 'key-down' event.
     *
     * @param returnTrueForVelocity0
     * @return
     */
    public boolean isNoteOn(boolean returnTrueForVelocity0) {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Note ON.");
        }

        return ((midiData[0] & 0xF0) == 0x90) && (returnTrueForVelocity0 || midiData[2] != 0);

    }

    /**
     * Returns true if this message is a 'key-up' event.
     *
     * @param returnTrueForNoteOnVelocity0
     * @return
     */
    public boolean isNoteOff(boolean returnTrueForNoteOnVelocity0) {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a NoteOFF.");
        }

        return ((midiData[0] & 0xF0) == 0x80)
                || (midiData.length == 3)
                && (returnTrueForNoteOnVelocity0
                && (midiData[2] == 0)
                && ((midiData[0] & 0xF0) == 0x90));

    }

    /**
     * Returns true if this message is a 'key-down' or 'key-up' event.
     *
     * @return
     */
    public boolean isNoteOnOrOff() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a NoteON or NoteOFF.");
        }

        return ((midiData[0] & 0xF0) == 0x90 || (midiData[0] & 0xF0) == 0x80);

    }

    /**
     * Returns the midi note number for note-on and note-off messages.
     *
     * @return
     */
    public int getNoteNumber() {
//        midiData = null;

        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get Note Number.");
        }

        return midiData[1];

    }

    /**
     * Changes the midi note number of a note-on or note-off message.
     *
     * @param newNoteNumber
     */
    public void setNoteNumber(byte newNoteNumber) {
        int result = -1;

        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't set a new note number.");
        }

        if (isNoteOnOrOff() || isPolyAftertouch() || isChannelPressure())
            midiData[1] = newNoteNumber;
    }

    /**
     * Returns the velocity of a note-on or note-off message.
     *
     * @return
     */
    public int getVelocity() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get Velocity.");
        }

        if (isNoteOnOrOff())
            return midiData[2];

        return 0;
    }

    /**
     * Changes the velocity of a note-on or note-off message.
     *
     * @param newVelocity
     */
    public void setVelocity(float newVelocity) {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't set a new velocity.");
        }

        if (isNoteOnOrOff())
            midiData[2] = (byte) (127.0f * newVelocity);
    }

    /**
     * Returns the velocity of a note-on or note-off message.
     *
     * @return
     */
    public float getFloatVelocity() {
        return getVelocity() * (1.0f / 127.0f);
    }

    /**
     * Multiplies the velocity of a note-on or note-off message by a given amount.
     *
     * @param scaleFactor
     */
    public void multiplyVelocity(float scaleFactor) {
        setVelocity(getFloatVelocity() * scaleFactor);
    }

    /**
     * Returns true if this message is a 'sustain pedal down' controller message.
     *
     * @return
     */
    public boolean isSustainPedalOn() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Sustain Pedal ON.");
        }

        return isControllerOfType(0x40) && midiData[2] >= 64;

    }

    /**
     * Returns true if this message is a 'sustain pedal up' controller message.
     *
     * @return
     */
    public boolean isSustainPedalOff() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Sustain Pedal OFF.");
        }

        return isControllerOfType(0x40) && midiData[2] < 64;

    }

    /**
     * Returns true if this message is a 'sostenuto pedal down' controller message.
     *
     * @return
     */
    public boolean isSostenutoPedalOn() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Sostenuto Pedal ON.");
        }

        return isControllerOfType(0x42) && midiData[2] >= 64;

    }

    /**
     * Returns true if this message is a 'sostenuto pedal up' controller message.
     *
     * @return
     */
    public boolean isSostenutoPedalOff() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Sostenuto Pedal OFF.");
        }

        return isControllerOfType(0x42) && midiData[2] < 64;

    }

    /**
     * Returns true if this message is a 'soft pedal down' controller message.
     *
     * @return
     */
    public boolean isSoftPedalOn() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Soft Pedal ON.");
        }

        return isControllerOfType(0x43) && midiData[2] >= 64;

    }

    /**
     * Returns true if this message is a 'soft pedal up' controller message.
     *
     * @return
     */
    public boolean isSoftPedalOff() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Soft Pedal OFF.");
        }

        return isControllerOfType(0x43) && midiData[2] < 64;

    }

    /**
     * Returns true if the message is a program (patch) change message.
     *
     * @return
     */
    public boolean isProgramChange() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Program Change.");
        }

        return (midiData[0] & 0xF0) == 0xC0;

    }

    /**
     * Returns the new program number of a program change message.
     *
     * @return
     */
    public int getProgramChangeNumber() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get a Program Change number.");
        }

        return midiData[1];

    }

    /**
     * Returns true if the message is a pitch-wheel move.
     *
     * @return
     */
    public boolean isPitchWheel() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Pitch Wheel");
        }

        return (midiData[0] & 0xF0) == 0xE0;

    }

    /**
     * Returns the pitch wheel position from a pitch-wheel move message.
     *
     * @return
     */
    public int getPitchWheelValue() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get a Pitch Wheel value.");
        }

        return midiData[1] | midiData[2] << 7;

    }

    /**
     * Returns true if the message is a channel-pressure change event.
     *
     * @return
     */
    public boolean isChannelPressure() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Channel Pressure.");
        }

        return ((midiData[0] & 0xF0) == 0xD0);

    }

    /**
     * Returns the pressure from a channel pressure change message.
     *
     * @return
     */
    public int getChannelPressureValue() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get a Channel Pressure value.");
        }

        if (isChannelPressure()) {
            return midiData[1];
        }

        return 0;
    }

    /**
     * Returns true if the message is a Polyphonic Aftertouch event.
     *
     * @return
     */
    public boolean isPolyAftertouch() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Poly After Touch.");
        }

        return ((midiData[0] & 0xF0) == 0xA0);

    }

    /**
     * Returns the amount of Poliphonic Aftertouch from an Aftertouch messages.
     *
     * @return
     */
    public int getPolyAftertouchValue() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get a Poly After Touch value.");
        }

        if (isPolyAftertouch()) {
            return midiData[2];
        }

        return 0;
    }

    /**
     * Returns true if this is a midi controller message.
     *
     * @return
     */
    public boolean isController() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Controller.");
        }

        return ((midiData[0] & 0xF0) == 0xB0);

    }

    /**
     * @param controllerNumber
     * @return
     */
    private String getControllerName(int controllerNumber) {
        String[] ctrlNames = {
                "Bank Select", "Modulation Wheel (coarse)", "Breath controller (coarse)",
                "--",
                "Foot Pedal (coarse)", "Portamento Time (coarse)", "Data Entry (coarse)",
                "Volume (coarse)", "Balance (coarse)",
                "--",
                "Pan position (coarse)", "Expression (coarse)", "Effect Control 1 (coarse)",
                "Effect Control 2 (coarse)",
                "--", "--",
                "General Purpose Slider 1", "General Purpose Slider 2",
                "General Purpose Slider 3", "General Purpose Slider 4",
                "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "Bank Select (fine)", "Modulation Wheel (fine)", "Breath controller (fine)",
                "--",
                "Foot Pedal (fine)", "Portamento Time (fine)", "Data Entry (fine)", "Volume (fine)",
                "Balance (fine)", "--", "Pan position (fine)", "Expression (fine)",
                "Effect Control 1 (fine)", "Effect Control 2 (fine)",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "Hold Pedal (on/off)", "Portamento (on/off)", "Sustenuto Pedal (on/off)", "Soft Pedal (on/off)",
                "Legato Pedal (on/off)", "Hold 2 Pedal (on/off)", "Sound Variation", "Sound Timbre",
                "Sound Release Time", "Sound Attack Time", "Sound Brightness", "Sound Control 6",
                "Sound Control 7", "Sound Control 8", "Sound Control 9", "Sound Control 10",
                "General Purpose Button 1 (on/off)", "General Purpose Button 2 (on/off)",
                "General Purpose Button 3 (on/off)", "General Purpose Button 4 (on/off)",
                "--", "--", "--", "--", "--", "--", "--",
                "Reverb Level", "Tremolo Level", "Chorus Level", "Celeste Level",
                "Phaser Level", "Data Button increment", "Data Button decrement", "Non-registered Parameter (fine)",
                "Non-registered Parameter (coarse)", "Registered Parameter (fine)", "Registered Parameter (coarse)",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "All Sound Off", "All Controllers Off", "Local Keyboard (on/off)", "All Notes Off",
                "Omni Mode Off", "Omni Mode On", "Mono Operation", "Poly Operation"
        };

        return ctrlNames[controllerNumber];
    }

    /**
     * Returns the controller number of a controller message.
     *
     * @return
     */
    public int getControllerNumber() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get a Controller number.");
        }

        return midiData[1];

    }

    /**
     * Returns the controller value from a controller message.
     *
     * @return
     */
    public int getControllerValue() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't get a Controller value.");
        }

        return midiData[2];

    }

    /**
     * Returns true if this message is a controller message and if it has the specified controller type.
     *
     * @param controllerType
     * @return
     */
    public boolean isControllerOfType(int controllerType) {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check type of Controller.");
        }

        return ((midiData[0] & 0xF0) == 0xB0) && (midiData[1] == controllerType);

    }

    /**
     * Checks whether this message is an all-notes-off message.
     *
     * @return
     */
    public boolean isAllNotesOff() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is an All Note OFF");
        }

        return ((midiData[0] & 0xF0) == 0xB0) && (midiData[1] == 123);
    }

    /**
     * Checks whether this message is an all-sound-off message.
     *
     * @return
     */
    public boolean isAllSoundOff() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is an All Sound OFF.");
        }

        return (midiData[1] == 120) && ((midiData[0] & 0xF0) == 0xB0);
    }

    /**
     * Checks whether this message is a reset all controllers message.
     *
     * @return
     */
    public boolean isResetAllControllers() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Reset All Controllers.");
        }

        return ((midiData[0] & 0xF0) == 0xB0) && (midiData[1] == 121);
    }

    /**
     * @return
     */
    public String timeStampAsTimecode() {
        return SmpteTimecode.getTimecode(timeStamp * 1000);
    }

    /**
     * Returns true if this event is a meta-event.
     *
     * @return
     */
    public boolean isMetaEvent() {
        if (midiData == null) {
            throw new MidiException("midiData is 'null' - can't check if it is a Meta Event.");
        }

        return (midiData[0] & 0xFF) == 0xFF;
    }

    //TODO: Implement these methods as well??
    /**
     * Returns true if this is an active-sense message.
     *
     * @return
     * /
    public boolean isActiveSense() {
    return (midiMessage[0] & 0xFE) == 0xFE;
    }

    /**
     * Returns a meta-event's type number.
     *
     * @return
     * /
    public int getMetaEventType() {
    return (midiMessage[0] & 0xFF) != 0xFF ? -1 : midiMessage[1];
    }

    /**
     * Returns a pointer to the data in a meta-event.
     * /
    public Pointer getMetaEventData() {
    return null;
    }

    /**
     * Returns the length of the data for a meta-event.
     *
     * @return
     * /
    public int getMetaEventLength() {
    return 0;
    }

    /**
     * Returns true if this is a 'track' meta-event.
     *
     * @return
     * /
    public boolean isTrackMetaEvent() {
    return false;
    }

    /** Creates an end-of-track meta-event.
     *  @link isEndOfTrackMetaEvent
     * /
    public static MidiMessage endOfTrack() {
    return null;
    }

    /**
     * Returns true if this is an 'end-of-track' meta-event.
     *
     * @return
     * /
    public boolean isEndOfTrackMetaEvent() {
    return false;
    }

    /**
     * Returns true if this is an 'track name' meta-event.
     *
     * @return
     * /
    public boolean isTrackNameEvent() {
    return false;
    }

    /**
     * Returns true if this is a 'text' meta-event.
     *
     * @return
     * /
    public boolean isTextMetaEvent() {
    return false;
    }

    /**
     * Returns the text from a text meta-event.
     *
     * @return
     * /
    public String getTextFromTextMetaEvent() {
    return "0";
    }

    /**
     * Returns true if this is a 'tempo' meta-event.
     *
     * @return
     * /
    public boolean isTempoMetaEvent() {
    return false;
    }

    /**
     * Returns the tick length from a tempo meta-event.
     *
     * @param timeFormat
     * @return
     * /
    public double getTempoMetaEventTickLength(short timeFormat) {
    return 0;
    }

    /**
     * Calculates the seconds-per-quarter-note from a tempo meta-event.
     *
     * @return
     * /
    public double getTempoSecondsPerQuarterNote() {
    return 0;
    }

    /**
     * Returns true if this is a 'time-signature' meta-event.
     *
     * @return
     * /
    public boolean isTimeSignatureMetaEvent() {
    return false;
    }

    /**
     * Returns the time-signature values from a time-signature meta-event.
     * /
    public void getTimeSignatureInfo(int numerator, int denominator) {

    }

    /**
     * Returns true if this is a 'key-signature' meta-event.
     *
     * @return
     * /
    public boolean isKeySignatureMetaEvent() {
    return false;
    }

    /**
     * Returns the key from a key-signature meta-event.
     *
     * @return
     * /
    public int getKeySignatureNumberOfSharpsOrFlats() {
    return 0;
    }

    /**
     * Returns true if this key-signature event is major, or false if it's minor.
     *
     * @return
     * /
    public boolean isKeySignatureMajorKey() {
    return false;
    }

    /**
     * Returns true if this is a 'channel' meta-event.
     *
     * @return
     * /
    public boolean isMidiChannelMetaEvent() {
    return false;
    }

    /**
     * Returns the channel number from a channel meta-event.
     *
     * @return
     * /
    public int getMidiChannelMetaEventChannel() {
    return 0;
    }

    /**
     * Returns true if this is a midi start event.
     *
     * @return
     * /
    public boolean isMidiStart() {
    return false;
    }

    /**
     * Returns true if this is a midi continue event.
     *
     * @return
     * /
    public boolean isMidiContinue() {
    return false;
    }

    /**
     * Returns true if this is a midi stop event.
     *
     * @return
     * /
    public boolean isMidiStop() {
    return false;
    }

    /**
     * Returns true if this is a midi clock event.
     *
     * @return
     * /
    public boolean isMidiClock() {
    return false;
    }

    /**
     * Returns true if this is a song-position-pointer message.
     *
     * @return
     * /
    public boolean isSongPositionPointer() {
    return false;
    }

    /**
     * Returns the midi beat-number of a song-position-pointer message.
     *
     * @return
     * /
    public int getSongPositionPointerMidiBeat() {
    return 0;
    }

    /**
     * Returns true if this is a quarter-frame midi timecode message.
     *
     * @return
     * /
    public boolean isQuarterFrame() {
    return false;
    }

    /**
     * Returns the sequence number of a quarter-frame midi timecode message.
     *
     * @return
     * /
    public int getQuarterFrameSequenceNumber() {
    return 0;
    }

    /**
     * Returns the value from a quarter-frame message.
     *
     * @return
     * /
    public int getQuarterFrameValue() {
    return 0;
    }

    /**
     * Returns true if this is a full-frame midi timecode message.
     *
     * @return
     * /
    public boolean isFullFrame() {
    return false;
    }

    /**
     * Extracts the timecode information from a full-frame midi timecode message.
     * /
    public void getFullFrameParameters(int hours, int minutes, int seconds, int frames, SmpteTimecodeType timecodeType) {

    }
     */

}
        
