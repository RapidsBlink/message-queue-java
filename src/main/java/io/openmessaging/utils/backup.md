```java
    static int ChromiumBase64Decode(byte[] dest, byte[] src, int len) {
//        if (len == 0) return 0;
//
//        /*
//         * if padding is used, then the message must be at least
//         * 4 chars and be a multiple of 4
//         */
//        if (len < 4 || (len % 4 != 0)) {
//            return MODP_B64_ERROR; /* error */
//        }
//        /* there can be at most 2 pad chars at the end */
//        if (src[len - 1] == CHARPAD) {
//            len--;
//            if (src[len - 1] == CHARPAD) {
//                len--;
//            }
//        }

        int i;
//        int leftover = len % 4;
//        int chunks = (leftover == 0) ? len / 4 - 1 : len / 4;
        int chunks = len / 4;

        int x;
        int size_p = 0;

        int offset_y = 0;
        for (i = 0; i < chunks; ++i, offset_y += 4) {
            x = d0[src[offset_y]] | d1[src[1 + offset_y]] | d2[src[2 + offset_y]] | d3[src[3 + offset_y]];
//            if (x >= BADCHAR) return MODP_B64_ERROR;
            dest[size_p++] = (byte) ((x >>> 0) & 0xff);  //x[0]
            dest[size_p++] = (byte) ((x >>> 8) & 0xff);  //x[1]
            dest[size_p++] = (byte) ((x >>> 16) & 0xff);   //x[2]
        }

//        switch (leftover) {
//            case 0:
//                x = d0[src[offset_y]] | d1[src[offset_y + 1]] | d2[src[offset_y + 2]] | d3[src[offset_y + 3]];
//
//                if (x >= BADCHAR) return MODP_B64_ERROR;
//                dest[size_p++] = (byte) ((x >>> 0) & 0xff);  //x[0]
//                dest[size_p++] = (byte) ((x >>> 8) & 0xff);  //x[1]
//                dest[size_p] = (byte) ((x >>> 16) & 0xff);   //x[2]
//                return (chunks + 1) * 3;
//            case 1:  /* with padding this is an impossible case */
//                x = d0[src[offset_y]];
//                dest[size_p] = (byte) ((x >>> 0) & 0xff);  //x[0]// i.e. first char/byte in int
//                break;
//            case 2: // * case 2, 1  output byte */
//                x = d0[src[offset_y]] | d1[src[offset_y + 1]];
//                dest[size_p] = (byte) ((x >>> 0) & 0xff);  //x[0] // i.e. first char
//                break;
//            default: /* case 3, 2 output bytes */
//                x = d0[src[offset_y]] | d1[src[offset_y + 1]] | d2[src[offset_y + 2]];  /* 0x3c */
//                dest[size_p++] = (byte) ((x >>> 0) & 0xff);  //x[0]
//                dest[size_p] = (byte) ((x >>> 8) & 0xff);  //x[1]
//                break;
//        }
//        if (x >= BADCHAR) return MODP_B64_ERROR;
//        return 3 * chunks + (6 * leftover) / 8;
        return 3 * chunks;
    }
```

```java
    static int ChromiumBase64Encode(byte[] dest, byte[] str, int len) {
        int i = 0;
        int size_p = 0;

        /* unsigned here is important! */ // for correct offset loopup
        byte t1, t2, t3;

        if (len > 2) {
            for (; i < len - 2; i += 3) {
                t1 = str[i];
                t2 = str[i + 1];
                t3 = str[i + 2];
                dest[size_p++] = e0[t1 & 0xff];
                dest[size_p++] = e1[(((t1 & 0x03) << 4) | ((t2 >> 4) & 0x0F)) & 0xff];
                dest[size_p++] = e1[(((t2 & 0x0F) << 2) | ((t3 >> 6) & 0x03)) & 0xff];
                dest[size_p++] = e2[t3 & 0xff];
            }
        }

        switch (len - i) {
            case 0:
                break;
            case 1:
                t1 = str[i];
                dest[size_p++] = e0[t1 & 0xff];
                dest[size_p++] = e1[((t1 & 0x03) << 4) & 0xff];
                dest[size_p++] = CHARPAD;
                dest[size_p++] = CHARPAD;
                break;
            default: /* case 2 */
                t1 = str[i];
                t2 = str[i + 1];
                dest[size_p++] = e0[t1 & 0xff];
                dest[size_p++] = e1[(((t1 & 0x03) << 4) | ((t2 >> 4) & 0x0F)) & 0xff];
                dest[size_p++] = e2[((t2 & 0x0F) << 2) & 0xff];
                dest[size_p++] = CHARPAD;
        }
//        dest[size_p] = '\0';          // no need to use \0 in java
        return size_p;
    }
```

```java

    public static int ChromiumBase6EncodeLen(int A) {
        return ((A + 2) / 3 * 4 + 1);       // 2 for padding, 3 bytes -> 4 bytes, 1 is for '\0'
    }
```

```java
    static int MODP_B64_ERROR = ((Integer.MAX_VALUE) - 1);
    static byte CHAR62 = '+';
    static byte CHAR63 = '/';
        private static int BADCHAR = 0x01FFFFFF;


```

```java
switch (len - i) {
            case 0:
                break;
            case 1:
                t1 = str[i];
                dest[size_p++] = e0[t1 & 0xff];
                dest[size_p++] = e1[((t1 & 0x03) << 4) & 0xff];
                dest[size_p++] = CHARPAD;
                dest[size_p++] = CHARPAD;
                break;
            default: /* case 2 */
                t1 = str[i];
                t2 = str[i + 1];
                dest[size_p++] = e0[t1 & 0xff];
                dest[size_p++] = e1[(((t1 & 0x03) << 4) | ((t2 >> 4) & 0x0F)) & 0xff];
                dest[size_p++] = e2[((t2 & 0x0F) << 2) & 0xff];
                dest[size_p++] = CHARPAD;
        }
```

```java
    static int ChromiumBase64Encode(byte[] dest, byte[] str, int len) {
        int i = 0;
        int size_p = 0;
        /* unsigned here is important! */ // for correct offset loopup
        byte t1, t2, t3;
        if (len > 2) {
            for (; i < len - 2; i += 3) {
                t1 = str[i];
                t2 = str[i + 1];
                t3 = str[i + 2];
                dest[size_p++] = e0[t1 & 0xff];
                dest[size_p++] = e1[(((t1 & 0x03) << 4) | ((t2 >> 4) & 0x0F)) & 0xff];
                dest[size_p++] = e1[(((t2 & 0x0F) << 2) | ((t3 >> 6) & 0x03)) & 0xff];
                dest[size_p++] = e2[t3 & 0xff];
            }
        }
        return size_p;
    }
```