package mathax.client.utils.base91;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Joachim Henke (Original version)
 * @author Benedikt Waldvogel (Modifications)
 *
 */

public class Base91 {
    private static final float AVERAGE_ENCODING_RATIO = 1.2297f;

    private static final byte[] DECODING_TABLE;
    static final byte[] ENCODING_TABLE;

    static final int BASE;

    static {
        String ts = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"";
        ENCODING_TABLE = ts.getBytes(StandardCharsets.ISO_8859_1);
        BASE = ENCODING_TABLE.length;
        assert BASE == 91;

        DECODING_TABLE = new byte[256];
        for (int i = 0; i < 256; ++i) DECODING_TABLE[i] = -1;

        for (int i = 0; i < BASE; ++i) DECODING_TABLE[ENCODING_TABLE[i]] = (byte) i;
    }

    public static byte[] encode(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base91OutputStream base91OutputStream = new Base91OutputStream(out);

        try {
            base91OutputStream.write(data);
            base91OutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode", e);
        }

        return out.toByteArray();
    }

    public static String encodeToString(byte[] data) {
        return new String(encode(data), StandardCharsets.UTF_8);
    }

    public static byte[] decode(byte[] data) {
        int dbq = 0;
        int dn = 0;
        int dv = -1;

        int estimatedSize = Math.round(data.length / AVERAGE_ENCODING_RATIO);
        ByteArrayOutputStream output = new ByteArrayOutputStream(estimatedSize);

        for (int i = 0; i < data.length; ++i) {
            assert DECODING_TABLE[data[i]] != -1;
            if (dv == -1) dv = DECODING_TABLE[data[i]];
            else {
                dv += DECODING_TABLE[data[i]] * BASE;
                dbq |= dv << dn;
                dn += (dv & 8191) > 88 ? 13 : 14;

                do {
                    output.write((byte) dbq);
                    dbq >>= 8;
                    dn -= 8;
                } while (dn > 7);

                dv = -1;
            }
        }

        if (dv != -1) output.write((byte) (dbq | dv << dn));

        return output.toByteArray();
    }

    public static byte[] decode(String data) {
        return decode(data.getBytes(StandardCharsets.US_ASCII));
    }
}
