package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HtmlConverter implements Converter {

    public HtmlConverter() {
        for (int i = 0; i < 256; i++) {
            bitSequences[i] = new bitSequence();
        }
    }

    @Override
    public boolean Read(InputStream input, OutputStream output) throws IOException {
        if (input.read() != 0x81) {
            return false;
        }

        parseData(input);
        input.close();

        output.write(_data, 1, _data.length - 1);
        output.flush();
        output.close();
        return true;
    }

    private byte[] _data;

    private void parseData(InputStream input) throws IOException {
        _data = new byte[input.available()];
        input.read(_data);

        // first get every 2nd char, ad those are all 0x00 because of the Unicode encoding
        // as the file may contain non-latin chars, get the bytes that appears the most times
        // first 5 bytes = 0x81(not encoded) 0x81 {0xff 0xfe}=Unicode 0xc3='<'
        // start with the 5th byte, but 0th byte is already read so start with the 4th
        for (int i = 4; i < 260; i += 2) {
            Map<Byte, Integer> bytes = new HashMap<>();
            for (int i2 = i; i2 < _data.length; i2 += 256) {
                byte current = _data[i2];
                Integer count = bytes.get(current);
                if (count == null) {
                    count = 1;
                } else {
                    count = count + 1;
                }
                bytes.put(current, count);
            }
            byte mostcommon = 0;
            int count = 0;
            for (Map.Entry<Byte, Integer> e : bytes.entrySet()) {
                if (e.getValue() > count) {
                    mostcommon = e.getKey();
                    count = e.getValue();
                }
            }
            fixAll(i % 256, mostcommon);
        }

        //get the first 43 non-0x00 chars
        byte[] testbytes = (new String(new byte[]{(byte) 0xff}) + "<?xml version=\"1.0\" encoding=\"UTF-16\" ?>" + new String(new byte[]{(byte) 0x0d, 0x0a})).getBytes();

        for (int i = 0; i < testbytes.length; i++) {
            fixAll(1 + i * 2, (byte) (testbytes[i] ^ _data[1 + i * 2]));
        }

        //as we have the first 43 non 0x00 and all the 0x00 bytes, we already have the first 64 bytes (actually 86)
        //=> we know the 0-6th bits of every encoding byte
        byte[] allEnglish = new byte[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            '<', '>', ' ', '/', '"', '?', '=', '.', '-', '_',
            ',', 0x0d, 0x0a

        };
        for (int i = 87; i < 256; i += 2) {
            byte[] bytes = bitSequences[i].getPossibilities();

            if (bytes.length == 1) {
                fixAll(i, bytes[0]);
                continue;
            }

            byte[] bytes128 = bitSequences[(i + 128) % 256].getPossibilities();

            int[] counts = new int[bytes.length * bytes128.length];

            for (int i2 = 0; i2 < bytes.length * bytes128.length; i2++) {
                if ((bytes[i2 / bytes128.length] & 0x40) != (bytes128[i2 % bytes128.length] & 0x40)) {
                    continue;
                }

                for (int i3 = i; i3 < _data.length; i3 += 256) {
                    byte test = (byte) (_data[i3] ^ bytes[i2 / bytes128.length]);
                    for (byte eng : allEnglish) {
                        if (eng == test) {
                            counts[i2] += 1;
                            break;
                        }
                    }

                    if (i3 + 128 < _data.length) {
                        test = (byte) (_data[i3 + 128] ^ bytes128[i2 % bytes128.length]);
                        for (byte eng : allEnglish) {
                            if (eng == test) {
                                counts[i2] += 1;
                                break;
                            }
                        }
                    }
                }
            }
            byte bestbyte = 0;
            byte bestbyte128 = 0;
            int maxcount = 0;
            for (int i2 = 0; i2 < bytes.length * bytes128.length; i2++) {
                if (counts[i2] > maxcount) {
                    bestbyte = bytes[i2 / bytes128.length];
                    bestbyte128 = bytes128[i2 % bytes128.length];
                    maxcount = counts[i2];
                }
            }
            fixAll(i, bestbyte);
            fixAll((i + 128) % 256, bestbyte128);
        }

        for (int i = 0; i < _data.length; i++) {
            _data[i] = (byte) (_data[i] ^ bitSequences[i % 256].getPossibilities()[0]);
        }
    }

    private void fixAll(int first, byte b) {
        for (byte place = 0; place < 8; place++) {
            fixBits(first, place, (b & (1 << place)) != 0);
        }
    }

    private void fixBits(int first, byte place, boolean ONE) {
        int everyNth = 0;
        switch (place) {
            case 0:
            case 1:
                everyNth = 4;
                break;
            case 2:
                everyNth = 8;
                break;
            case 3:
                everyNth = 16;
                break;
            case 4:
                everyNth = 32;
                break;
            case 5:
                everyNth = 64;
                break;
            case 6:
                everyNth = 128;
                break;
            case 7:
                everyNth = 256;
                break;
        }

        for (int i = first; i < 256; i += everyNth) {
            bitSequences[i].fixBit(place, ONE);
        }
    }

    private final bitSequence[] bitSequences = new bitSequence[256];

    private class bitSequence {

        private final byte[] bits = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1};

        private int fixedcount = 0;

        public void fixBit(int place, boolean ONE) {
            if (bits[place] != -1) {
                return;
            }
            bits[place] = (byte) (ONE ? 1 : 0);
            fixedcount++;
        }

        public byte[] getPossibilities() {
            byte[] ret = new byte[1 << (8 - fixedcount)];
            for (int helpbyte = 0; helpbyte < ret.length; helpbyte++) {
                int current = 0;
                int elapsednonfix = 0;
                for (int i = 0; i < 8; i++) {
                    if (bits[i] == -1) {
                        current += ((helpbyte & (1 << elapsednonfix)) != 0 ? 1 : 0) << i;
                        elapsednonfix++;
                    } else {
                        current += bits[i] << i;
                    }
                }
                ret[helpbyte] = (byte) current;
            }
            return ret;

        }
    }
}
