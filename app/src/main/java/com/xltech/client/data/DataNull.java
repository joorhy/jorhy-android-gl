package com.xltech.client.data;

/**
 * Created by JooLiu on 2016/2/2.
 */
public class DataNull {
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
