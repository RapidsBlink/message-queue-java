package io.openmessaging.utils;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChromiumBase64 {
    final private static byte[] e0 = {
            'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'C', 'C',
            'C', 'C', 'D', 'D', 'D', 'D', 'E', 'E', 'E', 'E',
            'F', 'F', 'F', 'F', 'G', 'G', 'G', 'G', 'H', 'H',
            'H', 'H', 'I', 'I', 'I', 'I', 'J', 'J', 'J', 'J',
            'K', 'K', 'K', 'K', 'L', 'L', 'L', 'L', 'M', 'M',
            'M', 'M', 'N', 'N', 'N', 'N', 'O', 'O', 'O', 'O',
            'P', 'P', 'P', 'P', 'Q', 'Q', 'Q', 'Q', 'R', 'R',
            'R', 'R', 'S', 'S', 'S', 'S', 'T', 'T', 'T', 'T',
            'U', 'U', 'U', 'U', 'V', 'V', 'V', 'V', 'W', 'W',
            'W', 'W', 'X', 'X', 'X', 'X', 'Y', 'Y', 'Y', 'Y',
            'Z', 'Z', 'Z', 'Z', 'a', 'a', 'a', 'a', 'b', 'b',
            'b', 'b', 'c', 'c', 'c', 'c', 'd', 'd', 'd', 'd',
            'e', 'e', 'e', 'e', 'f', 'f', 'f', 'f', 'g', 'g',
            'g', 'g', 'h', 'h', 'h', 'h', 'i', 'i', 'i', 'i',
            'j', 'j', 'j', 'j', 'k', 'k', 'k', 'k', 'l', 'l',
            'l', 'l', 'm', 'm', 'm', 'm', 'n', 'n', 'n', 'n',
            'o', 'o', 'o', 'o', 'p', 'p', 'p', 'p', 'q', 'q',
            'q', 'q', 'r', 'r', 'r', 'r', 's', 's', 's', 's',
            't', 't', 't', 't', 'u', 'u', 'u', 'u', 'v', 'v',
            'v', 'v', 'w', 'w', 'w', 'w', 'x', 'x', 'x', 'x',
            'y', 'y', 'y', 'y', 'z', 'z', 'z', 'z', '0', '0',
            '0', '0', '1', '1', '1', '1', '2', '2', '2', '2',
            '3', '3', '3', '3', '4', '4', '4', '4', '5', '5',
            '5', '5', '6', '6', '6', '6', '7', '7', '7', '7',
            '8', '8', '8', '8', '9', '9', '9', '9', '+', '+',
            '+', '+', '/', '/', '/', '/'
    };

    final private static byte[] e1 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '+', '/', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '+', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '+', '/'
    };

    final private static byte e2[] = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '+', '/', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '+', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '+', '/'
    };

    /* SPECIAL DECODE TABLES FOR LITTLE ENDIAN (INTEL) CPUS */
    final private static int[] d0 = {
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x000000f8, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x000000fc,
            0x000000d0, 0x000000d4, 0x000000d8, 0x000000dc, 0x000000e0, 0x000000e4,
            0x000000e8, 0x000000ec, 0x000000f0, 0x000000f4, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x00000000,
            0x00000004, 0x00000008, 0x0000000c, 0x00000010, 0x00000014, 0x00000018,
            0x0000001c, 0x00000020, 0x00000024, 0x00000028, 0x0000002c, 0x00000030,
            0x00000034, 0x00000038, 0x0000003c, 0x00000040, 0x00000044, 0x00000048,
            0x0000004c, 0x00000050, 0x00000054, 0x00000058, 0x0000005c, 0x00000060,
            0x00000064, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x00000068, 0x0000006c, 0x00000070, 0x00000074, 0x00000078,
            0x0000007c, 0x00000080, 0x00000084, 0x00000088, 0x0000008c, 0x00000090,
            0x00000094, 0x00000098, 0x0000009c, 0x000000a0, 0x000000a4, 0x000000a8,
            0x000000ac, 0x000000b0, 0x000000b4, 0x000000b8, 0x000000bc, 0x000000c0,
            0x000000c4, 0x000000c8, 0x000000cc, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff
    };

    final private static int[] d1 = {
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x0000e003, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x0000f003,
            0x00004003, 0x00005003, 0x00006003, 0x00007003, 0x00008003, 0x00009003,
            0x0000a003, 0x0000b003, 0x0000c003, 0x0000d003, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x00000000,
            0x00001000, 0x00002000, 0x00003000, 0x00004000, 0x00005000, 0x00006000,
            0x00007000, 0x00008000, 0x00009000, 0x0000a000, 0x0000b000, 0x0000c000,
            0x0000d000, 0x0000e000, 0x0000f000, 0x00000001, 0x00001001, 0x00002001,
            0x00003001, 0x00004001, 0x00005001, 0x00006001, 0x00007001, 0x00008001,
            0x00009001, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x0000a001, 0x0000b001, 0x0000c001, 0x0000d001, 0x0000e001,
            0x0000f001, 0x00000002, 0x00001002, 0x00002002, 0x00003002, 0x00004002,
            0x00005002, 0x00006002, 0x00007002, 0x00008002, 0x00009002, 0x0000a002,
            0x0000b002, 0x0000c002, 0x0000d002, 0x0000e002, 0x0000f002, 0x00000003,
            0x00001003, 0x00002003, 0x00003003, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff
    };


    final private static int[] d2 = {
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x00800f00, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x00c00f00,
            0x00000d00, 0x00400d00, 0x00800d00, 0x00c00d00, 0x00000e00, 0x00400e00,
            0x00800e00, 0x00c00e00, 0x00000f00, 0x00400f00, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x00000000,
            0x00400000, 0x00800000, 0x00c00000, 0x00000100, 0x00400100, 0x00800100,
            0x00c00100, 0x00000200, 0x00400200, 0x00800200, 0x00c00200, 0x00000300,
            0x00400300, 0x00800300, 0x00c00300, 0x00000400, 0x00400400, 0x00800400,
            0x00c00400, 0x00000500, 0x00400500, 0x00800500, 0x00c00500, 0x00000600,
            0x00400600, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x00800600, 0x00c00600, 0x00000700, 0x00400700, 0x00800700,
            0x00c00700, 0x00000800, 0x00400800, 0x00800800, 0x00c00800, 0x00000900,
            0x00400900, 0x00800900, 0x00c00900, 0x00000a00, 0x00400a00, 0x00800a00,
            0x00c00a00, 0x00000b00, 0x00400b00, 0x00800b00, 0x00c00b00, 0x00000c00,
            0x00400c00, 0x00800c00, 0x00c00c00, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff
    };


    final private static int[] d3 = {
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x003e0000, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x003f0000,
            0x00340000, 0x00350000, 0x00360000, 0x00370000, 0x00380000, 0x00390000,
            0x003a0000, 0x003b0000, 0x003c0000, 0x003d0000, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x00000000,
            0x00010000, 0x00020000, 0x00030000, 0x00040000, 0x00050000, 0x00060000,
            0x00070000, 0x00080000, 0x00090000, 0x000a0000, 0x000b0000, 0x000c0000,
            0x000d0000, 0x000e0000, 0x000f0000, 0x00100000, 0x00110000, 0x00120000,
            0x00130000, 0x00140000, 0x00150000, 0x00160000, 0x00170000, 0x00180000,
            0x00190000, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x001a0000, 0x001b0000, 0x001c0000, 0x001d0000, 0x001e0000,
            0x001f0000, 0x00200000, 0x00210000, 0x00220000, 0x00230000, 0x00240000,
            0x00250000, 0x00260000, 0x00270000, 0x00280000, 0x00290000, 0x002a0000,
            0x002b0000, 0x002c0000, 0x002d0000, 0x002e0000, 0x002f0000, 0x00300000,
            0x00310000, 0x00320000, 0x00330000, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff,
            0x01ffffff, 0x01ffffff, 0x01ffffff, 0x01ffffff
    };

    static int ChromiumBase64Encode(byte[] dest, byte[] str, int start_off, int len) {
        int size_p = 0;
        /* unsigned here is important! */ // for correct offset loopup
        byte t1, t2, t3;
        for (int i = 0; i < len - 2; i += 3) {
            t1 = str[start_off + i];
            t2 = str[start_off + i + 1];
            t3 = str[start_off + i + 2];
            dest[size_p] = e0[t1 & 0xff];
            dest[size_p + 1] = e1[(((t1 & 0x03) << 4) | ((t2 >> 4) & 0x0F)) & 0xff];
            dest[size_p + 2] = e1[(((t2 & 0x0F) << 2) | ((t3 >> 6) & 0x03)) & 0xff];
            dest[size_p + 3] = e2[t3 & 0xff];
            size_p += 4;
        }
        return size_p;
    }

    static void ChromiumBase64Decode(byte[] dest, byte[] src, int len) {
        int chunks = len / 4;
        int size_p = 0;
        int offset_y = 0;
        for (int i = 0; i < chunks; ++i, offset_y += 4) {
            int x = d0[src[offset_y]] | d1[src[1 + offset_y]] | d2[src[2 + offset_y]] | d3[src[3 + offset_y]];
            dest[size_p++] = (byte) ((x) & 0xff);  //x[0]
            dest[size_p++] = (byte) ((x >>> 8) & 0xff);  //x[1]
            dest[size_p++] = (byte) ((x >>> 16) & 0xff);   //x[2]
        }
    }

    static void ChromiumBase64DecodeOff(byte[] dest, int off, byte[] src, int len) {
        int chunks = len / 4;
        int size_p = off;
        int offset_y = 0;
        for (int i = 0; i < chunks; ++i, offset_y += 4) {
            int x = d0[src[offset_y]] | d1[src[1 + offset_y]] | d2[src[2 + offset_y]] | d3[src[3 + offset_y]];
            dest[size_p++] = (byte) ((x) & 0xff);  //x[0]
            dest[size_p++] = (byte) ((x >>> 8) & 0xff);  //x[1]
            dest[size_p++] = (byte) ((x >>> 16) & 0xff);   //x[2]
        }
    }

    // assume reusable bytebuffer and already flipped
    static int ChromiumBase64EncodeByteBuffer(byte[] dest, ByteBuffer srcByteBuffer, int len) {
        int size_p = 0;
        /* unsigned here is important! */ // for correct offset loopup
        byte t1, t2, t3;
        for (int i = 0; i < len - 2; i += 3) {
            t1 = srcByteBuffer.get(i);
            t2 = srcByteBuffer.get(i + 1);
            t3 = srcByteBuffer.get(i + 2);
            dest[size_p] = e0[t1 & 0xff];
            dest[size_p + 1] = e1[(((t1 & 0x03) << 4) | ((t2 >> 4) & 0x0F)) & 0xff];
            dest[size_p + 2] = e1[(((t2 & 0x0F) << 2) | ((t3 >> 6) & 0x03)) & 0xff];
            dest[size_p + 3] = e2[t3 & 0xff];
            size_p += 4;
        }
        return size_p;
    }

    // assume reuse DirectByteBuffer reusable
    static void ChromiumBase64DecodeByteBuffer(ByteBuffer dest, byte[] src, int len) {
        dest.clear(); // for writing

        int chunks = len / 4;
        int offset_y = 0;
        for (int i = 0; i < chunks; ++i, offset_y += 4) {
            int x = d0[src[offset_y]] | d1[src[1 + offset_y]] | d2[src[2 + offset_y]] | d3[src[3 + offset_y]];
            dest.put((byte) ((x >>> 0) & 0xff));
            dest.put((byte) ((x >>> 8) & 0xff));
            dest.put((byte) ((x >>> 16) & 0xff));
        }
    }

    private static void TestEndian() {
        int x = 1;
        if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
            System.out.println("Big-endian");
        } else {
            System.out.println("Little-endian");
        }
        for (int i = 0; i < 4; i++) {
            System.out.println("mem addr:[" + i + "]: " + (x >>> ((3 - i) * 8) & 0xff));
        }
    }

    private static void TestRawArr(byte[] chars) {
        // 1st: serialization
        byte[] serialized = new byte[chars.length / 4 * 3];
        ChromiumBase64Decode(serialized, chars, chars.length);

        System.out.println("serialized: " + serialized.length);
        for (byte aChar : serialized) {
            System.out.print(Integer.toString(aChar & 0xff, 16));
            System.out.print(",");
        }
        System.out.println();
        byte[] tmp = new byte[]{0x6a, (byte) 0xf7, 0x1d, 0x7b, 0x4d, 0x40, 0x73, 0x6d, (byte) 0xf8};
        System.out.println("ground truth of serialized: " + tmp.length);
        for (byte ch : tmp) {
            System.out.print(Integer.toString(ch & 0xff, 16) + ",");
        }
        System.out.println();

        // 2nd: deserialization
        byte[] deserialized = new byte[chars.length];
        ChromiumBase64Encode(deserialized, serialized, 0, serialized.length);
        System.out.println(new String(chars));
        System.out.println(new String(deserialized));
    }

    private static void TestByteBuffer(byte[] chars) {
        // 1st: serialization
        ByteBuffer serialized = ByteBuffer.allocateDirect(1024);
        ChromiumBase64DecodeByteBuffer(serialized, chars, chars.length);

        System.out.println();
        byte[] tmp = new byte[]{0x6a, (byte) 0xf7, 0x1d, 0x7b, 0x4d, 0x40, 0x73, 0x6d, (byte) 0xf8};
        System.out.println("ground truth of serialized: " + tmp.length);
        for (byte ch : tmp) {
            System.out.print(Integer.toString(ch & 0xff, 16) + ",");
        }
        System.out.println();

        // 2nd: deserialization
        byte[] deserialized = new byte[chars.length];
        serialized.flip();    // for later reading
        ChromiumBase64EncodeByteBuffer(deserialized, serialized, serialized.limit());
        System.out.println(new String(chars));
        System.out.println(new String(deserialized));
    }

    public static void main(String[] args) {
        byte[] chars = {'a', 'v', 'c', 'd', 'e', '0', '1', 'A', 'c', '2', '3', '4'};
        TestRawArr(chars);
        TestByteBuffer(chars);
    }
}
