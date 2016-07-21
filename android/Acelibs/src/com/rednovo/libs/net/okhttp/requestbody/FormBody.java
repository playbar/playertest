package com.rednovo.libs.net.okhttp.requestbody;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okio.Buffer;
import okio.BufferedSink;

public final class FormBody extends RequestBody {
    static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    private final List<String> encodedNames;
    private final List<String> encodedValues;

    private FormBody(List<String> encodedNames, List<String> encodedValues) {
        this.encodedNames = Util.immutableList(encodedNames);
        this.encodedValues = Util.immutableList(encodedValues);
    }

    /**
     * The number of key-value pairs in this form-encoded body.
     */
    public int size() {
        return encodedNames.size();
    }

    public String encodedName(int index) {
        return encodedNames.get(index);
    }

    public String name(int index) {
        return percentDecode(encodedName(index), true);
    }

    public String encodedValue(int index) {
        return encodedValues.get(index);
    }

    public String value(int index) {
        return percentDecode(encodedValue(index), true);
    }

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    @Override
    public long contentLength() {
        return writeOrCountBytes(null, true);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        writeOrCountBytes(sink, false);
    }

    /**
     * Either writes this request to {@code sink} or measures its content length. We have one method
     * do double-duty to make sure the counting and content are consistent, particularly when it comes
     * to awkward operations like measuring the encoded length of header strings, or the
     * length-in-digits of an encoded integer.
     */
    private long writeOrCountBytes(BufferedSink sink, boolean countBytes) {
        long byteCount = 0L;

        Buffer buffer;
        if (countBytes) {
            buffer = new Buffer();
        } else {
            buffer = sink.buffer();
        }

        for (int i = 0, size = encodedNames.size(); i < size; i++) {
            if (i > 0) buffer.writeByte('&');
            buffer.writeUtf8(encodedNames.get(i));
            buffer.writeByte('=');
            buffer.writeUtf8(encodedValues.get(i));
        }

        if (countBytes) {
            byteCount = buffer.size();
            buffer.clear();
        }

        return byteCount;
    }

    public static final class Builder {
        private final List<String> names = new ArrayList<>();
        private final List<String> values = new ArrayList<>();

        public Builder add(String name, String value) {
//            names.add(HttpUrl.canonicalize(name, FORM_ENCODE_SET, false, false, true, true));
//            values.add(HttpUrl.canonicalize(value, FORM_ENCODE_SET, false, false, true, true));
            names.add(canonicalize(name, FORM_ENCODE_SET, false, false, true, true));
            values.add(canonicalize(value, FORM_ENCODE_SET, false, false, true, true));
            return this;
        }

        public Builder addEncoded(String name, String value) {
//            names.add(HttpUrl.canonicalize(name, FORM_ENCODE_SET, true, false, true, true));
//            values.add(HttpUrl.canonicalize(value, FORM_ENCODE_SET, true, false, true, true));
            names.add(canonicalize(name, FORM_ENCODE_SET, true, false, true, true));
            values.add(canonicalize(value, FORM_ENCODE_SET, true, false, true, true));
            return this;
        }

        public FormBody build() {
            return new FormBody(names, values);
        }
    }

    static String percentDecode(String encoded, boolean plusIsSpace) {
        return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
    }

    static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
        for (int i = pos; i < limit; i++) {
            char c = encoded.charAt(i);
            if (c == '%' || (c == '+' && plusIsSpace)) {
                // Slow path: the character at i requires decoding!
                Buffer out = new Buffer();
                out.writeUtf8(encoded, pos, i);
                percentDecode(out, encoded, i, limit, plusIsSpace);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required decoding.
        return encoded.substring(pos, limit);
    }

    static void percentDecode(Buffer out, String encoded, int pos, int limit, boolean plusIsSpace) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = encoded.codePointAt(i);
            if (codePoint == '%' && i + 2 < limit) {
                int d1 = decodeHexDigit(encoded.charAt(i + 1));
                int d2 = decodeHexDigit(encoded.charAt(i + 2));
                if (d1 != -1 && d2 != -1) {
                    out.writeByte((d1 << 4) + d2);
                    i += 2;
                    continue;
                }
            } else if (codePoint == '+' && plusIsSpace) {
                out.writeByte(' ');
                continue;
            }
            out.writeUtf8CodePoint(codePoint);
        }
    }

    static int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }

    static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
        return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly);
    }

    static String canonicalize(String input, int pos, int limit, String encodeSet,
                               boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))
                    || codePoint == '+' && plusIsSpace) {
                // Slow path: the character at i requires encoding!
                Buffer out = new Buffer();
                out.writeUtf8(input, pos, i);
                canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace,
                        asciiOnly);
                return out.readUtf8();
            }
        }

        // Fast path: no characters in [pos..limit) required encoding.
        return input.substring(pos, limit);
    }

    static boolean percentEncoded(String encoded, int pos, int limit) {
        return pos + 2 < limit
                && encoded.charAt(pos) == '%'
                && decodeHexDigit(encoded.charAt(pos + 1)) != -1
                && decodeHexDigit(encoded.charAt(pos + 2)) != -1;
    }

    static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet,
                             boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly) {
        Buffer utf8Buffer = null; // Lazily allocated.
        int codePoint;
        for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(i);
            if (alreadyEncoded
                    && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
                // Skip this character.
            } else if (codePoint == '+' && plusIsSpace) {
                // Encode '+' as '%2B' since we permit ' ' to be encoded as either '+' or '%20'.
                out.writeUtf8(alreadyEncoded ? "+" : "%2B");
            } else if (codePoint < 0x20
                    || codePoint == 0x7f
                    || codePoint >= 0x80 && asciiOnly
                    || encodeSet.indexOf(codePoint) != -1
                    || codePoint == '%' && (!alreadyEncoded || strict && !percentEncoded(input, i, limit))) {
                // Percent encode this character.
                if (utf8Buffer == null) {
                    utf8Buffer = new Buffer();
                }
                utf8Buffer.writeUtf8CodePoint(codePoint);
                while (!utf8Buffer.exhausted()) {
                    int b = utf8Buffer.readByte() & 0xff;
                    out.writeByte('%');
                    out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                    out.writeByte(HEX_DIGITS[b & 0xf]);
                }
            } else {
                // This character doesn't need encoding. Just copy it over.
                out.writeUtf8CodePoint(codePoint);
            }
        }
    }

    private static final char[] HEX_DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
