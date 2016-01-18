package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;

public class XmlConverter implements Converter {

    private int ReadValuePackedS32(InputStream stream) throws IOException {
        int val = 0;
        for (int i = 0;; i++) {
            int buff = stream.read();
            val += (buff & 0x7f) << (i * 7);
            if ((buff & 0x80) == 0) {
                break;
            }
        }
        return val;
    }

    protected Map<Integer, byte[]> textmap = new HashMap<>();

    private void parseData(InputStream input) throws IOException {

        byte[] buffer = new byte[ReadValuePackedS32(input)];
        input.read(buffer);
        int start = 0;
        for (int i = 0; i < buffer.length; i += 2) {
            if (buffer[i] + (buffer[i + 1] << 8) == 0) {
                textmap.put(start / 2, Arrays.copyOfRange(buffer, start, i));
                start = i + 2;
            }
        }
    }

    private class BinaryXmlNode {

        public byte[] _name;
        public byte[] _value;
        public Map<byte[], byte[]> _attributes;
        public BinaryXmlNode[] _children;

        public synchronized void Read(InputStream input) throws IOException {
            _name = textmap.get(ReadValuePackedS32(input));

            int flags = input.read();

            // Value
            if ((flags & 1) != 0) {
                _value = textmap.get(ReadValuePackedS32(input));
                for (int i = 0; i < _value.length; i++) {
                    if (Objects.equals(BEGIN[0], _value[i])) {
                        byte[] naw = new byte[_value.length + 6];
                        System.arraycopy(_value, 0, naw, 0, i);
                        System.arraycopy(BEGIN2, 0, naw, i, 7);
                        System.arraycopy(_value, i + 1, naw, i + 7, (_value.length - i) - 2);
                        _value = naw;
                    } else if (Objects.equals(END[0], _value[i])) {
                        byte[] naw = new byte[_value.length + 6];
                        System.arraycopy(_value, 0, naw, 0, i);
                        System.arraycopy(END2, 0, naw, i, 7);
                        System.arraycopy(_value, i + 1, naw, i + 7, (_value.length - i) - 2);
                        _value = naw;
                    }
                }
            }

            // Attributes
            if ((flags & 2) != 0) {
                _attributes = new HashMap<>();
                int count = ReadValuePackedS32(input);
                for (int i = 0; i < count; i++) {
                    _attributes.put(textmap.get(ReadValuePackedS32(input)), textmap.get(ReadValuePackedS32(input)));
                }
            }

            // Children
            if ((flags & 4) != 0) {
                int count = ReadValuePackedS32(input);
                _children = new BinaryXmlNode[count];
                for (int i = 0; i < count; i++) {
                    BinaryXmlNode child = new BinaryXmlNode();
                    child.Read(input);
                    _children[i] = child;

                }
            }
        }
    }

    private BinaryXmlNode _root;

    @Override
    public boolean Read(InputStream input, OutputStream output) throws IOException {
        if (input.read() != 0x80) {
            return false;
        }
        parseData(input);
        _root = new BinaryXmlNode();
        _root.Read(input);
        input.close();

        XMLWriter writer = new XMLWriter(output);
        writer.writeHead();
        writer.writeNode(_root, 0);

        output.flush();
        output.close();

        return true;
    }

    static final byte[] SPACE = new byte[]{' ', 0};
    static final byte[] ENTER = new byte[]{'\n', 0};
    static final byte[] BEGIN = new byte[]{'<', 0};
    static final byte[] END = new byte[]{'>', 0};
    static final byte[] PER = new byte[]{'/', 0};
    static final byte[] QUOTATION = new byte[]{'"', 0};
    static final byte[] EQUAL = new byte[]{'=', 0};
    static final byte[] BEGIN2 = new byte[]{'&', 0, 'l', 0, 't', 0, ';'};
    static final byte[] END2 = new byte[]{'&', 0, 'g', 0, 't', 0, ';'};

    private class XMLWriter {

        private final OutputStream _output;

        public XMLWriter(OutputStream output) {
            _output = output;
        }

        public void writeHead() throws IOException {
            _output.write(0xff);
            _output.write(0xfe);
            _output.write(new byte[]{'<', 0,
                '?', 0,
                'x', 0,
                'm', 0,
                'l', 0,
                ' ', 0,
                'v', 0,
                'e', 0,
                'r', 0,
                's', 0,
                'i', 0,
                'o', 0,
                'n', 0,
                '=', 0,
                '"', 0,
                '1', 0,
                '.', 0,
                '0', 0,
                '"', 0,
                ' ', 0,
                'e', 0,
                'n', 0,
                'c', 0,
                'o', 0,
                'd', 0,
                'i', 0,
                'n', 0,
                'g', 0,
                '=', 0,
                '"', 0,
                'u', 0,
                't', 0,
                'f', 0,
                '-', 0,
                '1', 0,
                '6', 0,
                '"', 0,
                '?', 0,
                '>', 0,
                '\n', 0});
            _output.flush();
        }

        public void writeNode(BinaryXmlNode node, int depth) throws IOException {

            for (int i = 0; i < depth * 2; i++) {
                _output.write(SPACE);
            }

            _output.write(BEGIN);
            _output.write(node._name);

            if (node._attributes != null) {

                for (Map.Entry<byte[], byte[]> e : node._attributes.entrySet()) {

                    _output.write(SPACE);
                    _output.write(e.getKey());
                    _output.write(EQUAL);
                    _output.write(QUOTATION);
                    _output.write(e.getValue());
                    _output.write(QUOTATION);
                }
            }
            if (node._value == null && (node._children == null || node._children.length == 0)) {
                _output.write(PER);
                _output.write(END);
                _output.write(ENTER);
                return;
            }

            _output.write(END);

            if (node._value != null) {
                _output.write(node._value);
            }

            if (node._children != null && node._children.length > 0) {
                _output.write(ENTER);
                for (BinaryXmlNode child : node._children) {
                    writeNode(child, depth + 1);
                }
                for (int i = 0; i < depth * 2; i++) {
                    _output.write(SPACE);
                }
            }
            _output.write(BEGIN);
            _output.write(PER);
            _output.write(node._name);
            _output.write(END);
            _output.write(ENTER);
            _output.flush();
        }
    }
}
