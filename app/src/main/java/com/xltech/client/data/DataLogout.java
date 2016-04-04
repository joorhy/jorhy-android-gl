package com.xltech.client.data;

/**
 * Created by 123 on 2016/4/2.
 */
public class DataLogout {
    private byte command;
    private int sequence;

    public void setCommand(byte command) {
        this.command = command;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public byte getCommand() {
        return command;
    }

    public int getSequence() {
        return sequence;
    }
}
