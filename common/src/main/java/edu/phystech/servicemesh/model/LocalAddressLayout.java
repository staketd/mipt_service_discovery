package edu.phystech.servicemesh.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import edu.phystech.servicemesh.exception.WrongParameterException;
import org.springframework.data.annotation.Transient;

public class LocalAddressLayout extends IntResourceAllocator {
    @Transient
    private static final String minAvailableAddress = "127.0.0.20";
    @Transient
    private static final String maxAvailableAddress = "127.255.255.255";

    @Transient
    private static final int minAvailableAddressInt;
    @Transient
    private static final int maxAvailableAddressInt;

    static {
        try {
            minAvailableAddressInt = convertAddressToInt(InetAddress.getByName(minAvailableAddress));
            maxAvailableAddressInt = convertAddressToInt(InetAddress.getByName(maxAvailableAddress));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public LocalAddressLayout() {
        super(minAvailableAddressInt, maxAvailableAddressInt);
    }

    public String allocateAddress() {
        return convertIntToAddress(allocate());
    }

    public void deallocateAddress(String address) {
        try {
            deallocate(convertAddressToInt(InetAddress.getByName(address)));
        } catch (UnknownHostException e) {
            throw new WrongParameterException("Address doesn't exist");
        }
    }

    public static int convertAddressToInt(InetAddress address) {
        int result = 0;
        for (byte b: address.getAddress()) {
            result = result << 8 | (b & 0xFF);
        }
        return result;
    }

    public static String convertIntToAddress(int address) {
        byte[] result = ByteBuffer.allocate(4).putInt(address).array();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; ++i) {
            builder.append(Byte.toUnsignedInt(result[i]));
            builder.append('.');
        }
        return builder.append(Byte.toUnsignedInt(result[3])).toString();
    }

}
