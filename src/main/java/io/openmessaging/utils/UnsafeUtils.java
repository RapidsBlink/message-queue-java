package io.openmessaging.utils;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

// Not to be used in our project
public class UnsafeUtils {
    private static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
        }
        return unsafe;
    }

    public static void main(String[] args) {
        Unsafe unsafe = getUnsafe();
        long address = unsafe.allocateMemory(2);
        short number = 1;
        unsafe.putShort(address, number);
        if (unsafe.getByte(address) == 0)
            System.out.println("Big Endian");
        else
            System.out.println("Little Endian");
        unsafe.freeMemory(address);
    }

    //===================start of the unsafe hack========================
    //https://stackoverflow.com/questions/15409727/missing-some-absolute-methods-on-bytebuffer
    private static final sun.misc.Unsafe UNSAFE;

    public static void unmap(MappedByteBuffer buffer) {
        if(buffer != null) {
            sun.misc.Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
            cleaner.clean();
        }
    }

    static {
        Object result = null;
        try {
            Class<?> klass = Class.forName("sun.misc.Unsafe");
            for (Field field : klass.getDeclaredFields()) {
                if (field.getType() == klass &&
                        (field.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) ==
                                (Modifier.FINAL | Modifier.STATIC)) {
                    field.setAccessible(true);
                    result = field.get(null);
                    break;
                }
            }
        } catch (Throwable ignored) {
        }
        UNSAFE = result == null ? null : (sun.misc.Unsafe) result;
    }

    private static final Field ADDRESS_FIELD;

    static {
        Field f;
        try {
            f = Buffer.class.getDeclaredField("address");
            f.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            f = null;
        }
        ADDRESS_FIELD = f;
    }


    public static void absolutePut(ByteBuffer dstBuffer, int dstPosition, ByteBuffer srcBuffer) {
        if (!srcBuffer.isDirect()) {
            absolutePut(dstBuffer, dstPosition,
                    srcBuffer.array(), srcBuffer.arrayOffset() + srcBuffer.position(),
                    srcBuffer.remaining());
            return;
        }

        if (UNSAFE != null && ADDRESS_FIELD != null && dstBuffer.isDirect()) {
            try {
                long dstAddress = (long) ADDRESS_FIELD.get(dstBuffer) + dstPosition;
                long srcAddress = (long) ADDRESS_FIELD.get(srcBuffer) + srcBuffer.position();
                UNSAFE.copyMemory(srcAddress, dstAddress, srcBuffer.remaining());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            // fallback to basic loop
            for (int i = srcBuffer.position(); i < srcBuffer.limit(); i++) {
                dstBuffer.put(dstPosition + i, srcBuffer.get(i));
            }
        }
    }

    public static void absolutePut(ByteBuffer dstBuffer, int dstPosition, byte[] src, int srcOffset, int length) {
        if (UNSAFE != null && ADDRESS_FIELD != null && dstBuffer.isDirect()) {
            try {
                long dstAddress = (long) ADDRESS_FIELD.get(dstBuffer) + dstPosition;
                UNSAFE.copyMemory(
                        src, UNSAFE.arrayBaseOffset(byte[].class) + srcOffset,
                        null, dstAddress,
                        length);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            // fallback to System.arraycopy
            System.arraycopy(
                    src, srcOffset,
                    dstBuffer.array(), dstBuffer.arrayOffset() + dstPosition,
                    length);
        }
    }
    //===================end of the unsafe hack========================
}