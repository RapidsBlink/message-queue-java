package io.openmessaging.utils;

import java.nio.ByteBuffer;

public class MessageB64Serialization {
    final static int FIXED_PART_LEN = 10;  // assume FIXED_PART_LEN >=2, otherwise program is not correct
    final static int BASE64_INFO_LEN = 2;
    final static int INDEX_LEN = 4;
    final static int VARYING_VERIFY_LEN = 4;
    final static int COPY_PART_LEN = FIXED_PART_LEN - INDEX_LEN;

    final static byte[] magicArr = {'B', 'L', 'I', 'N', 'K'};
    final static int MAX_FIVE_BITS_INT = 0x1f;      // 31, for varyingLen Serialization

    // mutable: message is not usable later
    public static byte[] SerializeBase64Decoding(byte[] message, int len) {
        int serialize_len = len - FIXED_PART_LEN;
        byte padding_chars = (byte) ((4 - serialize_len % 4) % 4);
        // 1st: fixed part
        int serialization_base64_output_length = 3 * (serialize_len / 4 + (serialize_len % 4 == 0 ? 0 : 1));
        byte[] serialized = new byte[serialization_base64_output_length + FIXED_PART_LEN + 1];

        System.arraycopy(message, serialize_len, serialized, serialization_base64_output_length, FIXED_PART_LEN);
        // 2nd: padding for mutable message
        System.arraycopy(magicArr, 0, message, serialize_len, padding_chars);
        // 3rd: lookup with chromium base64 decoding
        ChromiumBase64.ChromiumBase64Decode(serialized, message, serialize_len + padding_chars);
        // 4th: record padding chars
        serialized[serialization_base64_output_length + FIXED_PART_LEN] = padding_chars;      // indicate number of padding chars
        return serialized;
    }

    public static byte[] DeserializeBase64Encoding(byte[] serialized, int start_off, int total_serialized_len) {
        int serialize_len = total_serialized_len - FIXED_PART_LEN - 1;

        // 1st: must be the accurate size to make it correct later without extra copying
        byte[] deserialized = new byte[serialize_len / 3 * 4 + FIXED_PART_LEN - serialized[start_off + total_serialized_len - 1]];
        // 2nd: deserialization
        int length = ChromiumBase64.ChromiumBase64Encode(deserialized, serialized, start_off, serialize_len);
        // 3rd: copy the fixed part
        System.arraycopy(serialized, serialize_len + start_off, deserialized, length - serialized[start_off +
                total_serialized_len - 1], FIXED_PART_LEN);
        return deserialized;
    }

    public static void SerializeBase64DecodingSkipIndexOff(byte[] message, int len, byte[] serialized, int offset) {
        int serialize_len = len - FIXED_PART_LEN;
        // should be optimized to be always 0
        byte padding_chars = (byte) ((4 - serialize_len % 4) % 4);
        // 1st: fixed part
        int serialization_base64_output_length = 3 * (serialize_len / 4 + (serialize_len % 4 == 0 ? 0 : 1));

        System.arraycopy(message, serialize_len, serialized, offset + serialization_base64_output_length, BASE64_INFO_LEN);
        System.arraycopy(message, serialize_len + BASE64_INFO_LEN + INDEX_LEN, serialized,
                offset + serialization_base64_output_length + BASE64_INFO_LEN, VARYING_VERIFY_LEN);


        // 2nd: padding for mutable message
        System.arraycopy(magicArr, 0, message, serialize_len, padding_chars);
        // 3rd: lookup with chromium base64 decoding
        ChromiumBase64.ChromiumBase64DecodeOff(serialized, offset, message, serialize_len + padding_chars);
        // 4th: record padding chars
        serialized[serialization_base64_output_length + COPY_PART_LEN] = padding_chars;      // indicate number of padding chars
    }

    public static byte[] SerializeBase64DecodingSkipIndex(byte[] message, int len) {
        int serialize_len = len - FIXED_PART_LEN;
        // should be optimized to be always 0
        byte padding_chars = (byte) ((4 - serialize_len % 4) % 4);
        // 1st: fixed part
        int serialization_base64_output_length = 3 * (serialize_len / 4 + (serialize_len % 4 == 0 ? 0 : 1));
        byte[] serialized = new byte[serialization_base64_output_length + COPY_PART_LEN + 1];

        System.arraycopy(message, serialize_len, serialized, serialization_base64_output_length, BASE64_INFO_LEN);
        System.arraycopy(message, serialize_len + BASE64_INFO_LEN + INDEX_LEN, serialized,
                serialization_base64_output_length + BASE64_INFO_LEN, VARYING_VERIFY_LEN);


        // 2nd: padding for mutable message
        System.arraycopy(magicArr, 0, message, serialize_len, padding_chars);
        // 3rd: lookup with chromium base64 decoding
        ChromiumBase64.ChromiumBase64Decode(serialized, message, serialize_len + padding_chars);
        // 4th: record padding chars
        serialized[serialization_base64_output_length + COPY_PART_LEN] = padding_chars;      // indicate number of padding chars
        return serialized;
    }

    public static byte[] DeserializeBase64EncodingAddIndex(byte[] serialized, int start_off, int total_serialized_len, int index) {
        int serialize_len = total_serialized_len - COPY_PART_LEN - 1;

        // 1st: must be the accurate size to make it correct later without extra copying
        byte[] deserialized = new byte[serialize_len / 3 * 4 + FIXED_PART_LEN - serialized[start_off + total_serialized_len - 1]];
        // 2nd: deserialization
        int length = ChromiumBase64.ChromiumBase64Encode(deserialized, serialized, start_off, serialize_len);
        // 3rd: copy the fixed part
        int new_off = length - serialized[start_off + total_serialized_len - 1];
        //  1) remaining base64
        System.arraycopy(serialized, serialize_len + start_off, deserialized, new_off, BASE64_INFO_LEN);
        new_off += BASE64_INFO_LEN;
        //  2) index:
        deserialized[new_off] = (byte) (index & 0xff);
        deserialized[new_off + 1] = (byte) ((index >> 8) & 0xff);
        deserialized[new_off + 2] = (byte) ((index >> 16) & 0xff);
        new_off += INDEX_LEN;
        //  3) other verification parts
        System.arraycopy(serialized, serialize_len + start_off + BASE64_INFO_LEN, deserialized, new_off, VARYING_VERIFY_LEN);

        return deserialized;
    }


    // only for normal size message (58 raw bytes)
    public static void DeserializeBase64EncodingAddIndexNormalSize(byte[] serialized, int start_off, int total_serialized_len, int index, byte[] deserialized) {
        int serialize_len = total_serialized_len - COPY_PART_LEN - 1;

        // 1st: must be the accurate size to make it correct later without extra copying
        // 2nd: deserialization
        int length = ChromiumBase64.ChromiumBase64Encode(deserialized, serialized, start_off, serialize_len);
        // 3rd: copy the fixed part
        int new_off = length - serialized[start_off + total_serialized_len - 1];
        //  1) remaining base64
        System.arraycopy(serialized, serialize_len + start_off, deserialized, new_off, BASE64_INFO_LEN);
        new_off += BASE64_INFO_LEN;
        //  2) index:
        deserialized[new_off] = (byte) (index & 0xff);
        deserialized[new_off + 1] = (byte) ((index >> 8) & 0xff);
        deserialized[new_off + 2] = (byte) ((index >> 16) & 0xff);
        new_off += INDEX_LEN;
        //  3) other verification parts
        System.arraycopy(serialized, serialize_len + start_off + BASE64_INFO_LEN, deserialized, new_off, VARYING_VERIFY_LEN);
    }

    // mutable: message is not usable later
    static int SerializeBase64DecodingByteBuffer(byte[] message, int len, ByteBuffer serialized) {
        int serialize_len = len - FIXED_PART_LEN;
        byte padding_chars = (byte) ((4 - serialize_len % 4) % 4);

        // 1st: fixed part
        int serialization_base64_output_length = 3 * (serialize_len / 4 + (serialize_len % 4 == 0 ? 0 : 1));
        for (int i = 0; i < FIXED_PART_LEN; i++) {
            serialized.put(serialization_base64_output_length + i, message[serialize_len + i]);
        }
        // 2nd: padding for mutable message
        System.arraycopy(magicArr, 0, message, serialize_len, padding_chars);
        // 3rd: lookup with chromium base64 decoding
        ChromiumBase64.ChromiumBase64DecodeByteBuffer(serialized, message, serialize_len + padding_chars);
        // 4th: record padding chars
        serialized.position(serialization_base64_output_length + FIXED_PART_LEN);
        serialized.put(padding_chars);      // indicate number of padding chars

        serialized.flip();
        return serialization_base64_output_length + FIXED_PART_LEN + 1;
    }

    public static byte[] DeserializeBase64EncodingByteBuffer(ByteBuffer serialized, int total_serialized_len) {
        int serialize_len = total_serialized_len - FIXED_PART_LEN - 1;

        // 1st: must be the accurate size to make it correct later without extra copying
        byte padding_chars = serialized.get(total_serialized_len - 1);
        byte[] deserialized = new byte[serialize_len / 3 * 4 + FIXED_PART_LEN - padding_chars];
        // 2nd: deserialization
        int length = ChromiumBase64.ChromiumBase64EncodeByteBuffer(deserialized, serialized, serialize_len);
        // 3rd: copy the fixed part
        for (int i = 0; i < FIXED_PART_LEN; i++) {
            deserialized[i + length - padding_chars] = serialized.get(serialize_len + i);
        }
        return deserialized;
    }

    // attention: 1) assume serialized allocated the same size as message, worst case same length
    // 2) 2 extra bytes is for length of the raw message
    public static ByteBuffer SerializeVaryingLen(byte[] message, int messageLen) {
        // add the header to indicate raw message varying-length part size, similar to padding chars
        ByteBuffer serializedBuffer = ByteBuffer.allocate(messageLen + 2);    // assume clear at the allocation
        byte[] serialized = serializedBuffer.array();

        int serialize_len = messageLen - FIXED_PART_LEN;
        int headerLen;
        if (messageLen < 128) {
            serialized[0] = (byte) (messageLen - FIXED_PART_LEN);
            headerLen = 1;
        } else {
            int tmp = (messageLen - FIXED_PART_LEN);                 // tmp high bits are zeros
            serialized[0] = (byte) ((tmp >>> 7) | 0x80); // assume < 32767
            serialized[1] = (byte) (tmp & 0x7f);               // low 7 bits
            headerLen = 2;
        }
        int next_extra_3bits_idx = 5 * serialize_len;
        int next_5bits_idx = 0;

        // attention: message is not usable later
        for (int i = 0; i < serialize_len; i++) {
            message[i] = (byte) (message[i] >= 'a' ? message[i] - 'a' : message[i] - '0' + 26);
        }

        // 1) construct the compressed part
        for (int i = 0; i < serialize_len; i++) {
            int cur_uchar = message[i] & 0xff;
            int expand_uchar = cur_uchar < MAX_FIVE_BITS_INT ? (cur_uchar << 11) : (MAX_FIVE_BITS_INT << 11);

            int shift_bits = (next_5bits_idx & 0x7);
            expand_uchar >>= shift_bits;
            int idx = (next_5bits_idx >> 3) + headerLen;
            serialized[idx] |= (expand_uchar >> 8);
            serialized[idx + 1] |= (expand_uchar & 0xff);
            next_5bits_idx += 5;

            if (cur_uchar >= MAX_FIVE_BITS_INT) {
                // do extra bits operations
                expand_uchar = ((cur_uchar - MAX_FIVE_BITS_INT) << 13);
                shift_bits = (next_extra_3bits_idx & 0x7);
                expand_uchar >>= shift_bits;
                // assume little-endian
                idx = (next_extra_3bits_idx >> 3) + headerLen;
                serialized[idx] |= (expand_uchar >> 8);
                serialized[idx + 1] |= (expand_uchar & 0xff);
                next_extra_3bits_idx += 3;
            }
        }

        // 2) left FIXED_PART_LEN, should use memcpy
        int start_copy_byte_idx = headerLen + (next_extra_3bits_idx >> 3) + ((next_extra_3bits_idx & 0x7) != 0 ? 1 : 0);

        System.arraycopy(message, serialize_len, serialized, start_copy_byte_idx, FIXED_PART_LEN);
        serializedBuffer.position(start_copy_byte_idx + FIXED_PART_LEN);
        return serializedBuffer;
    }

    public static byte[] DeserializeVaryingLen(ByteBuffer serializedBuffer) {
        // get the length of varying part
        byte[] serialized = serializedBuffer.array();
        int varying_byte_len;
        int headerLen;
        if ((serialized[0] & 0x80) == 0) {
            varying_byte_len = serialized[0];
            headerLen = 1;
        } else {
            varying_byte_len = (((serialized[0] & 0x7f) << 0x7) + serialized[1]);
            headerLen = 2;
        }
        int next_extra_3bits_idx = 5 * varying_byte_len;
        int next_5bits_idx = 0;

        byte[] deserialized = new byte[varying_byte_len + FIXED_PART_LEN];
        // deserialize
        for (int i = 0; i < varying_byte_len; i++) {
            int idx = (next_5bits_idx >> 3) + headerLen;
            int value = ((serialized[idx] & 0xff) << 8) + (serialized[idx + 1] & 0xff);
            value = (value >> (11 - (next_5bits_idx & 0x7))) & MAX_FIVE_BITS_INT;
            if (value != MAX_FIVE_BITS_INT) {
                deserialized[i] = (byte) (value < 26 ? 'a' + value : value - 26 + '0');
            } else {
                idx = (next_extra_3bits_idx >> 3) + headerLen;
                value = ((serialized[idx] & 0xff) << 8) + (serialized[idx + 1] & 0xff);
                value = (value >> (13 - (next_extra_3bits_idx & 0x7))) & 0x7;
                deserialized[i] = (byte) (value + '5');
                next_extra_3bits_idx += 3;
            }
            next_5bits_idx += 5;
        }

        // 2) copy the fixed part
        System.arraycopy(serialized, headerLen +
                        (next_extra_3bits_idx >> 3) + ((next_extra_3bits_idx & 0x7) != 0 ? 1 : 0),
                deserialized, varying_byte_len, FIXED_PART_LEN);
        return deserialized;
    }
}
