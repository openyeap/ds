package ltd.fdsa.ds.api.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ltd.fdsa.ds.api.cbor.CborBuilder;
import ltd.fdsa.ds.api.cbor.CborDecoder;
import ltd.fdsa.ds.api.cbor.CborEncoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.DataItem;

/**
 * ["a", {"b": "c"}] -> 0x826161a161626163
 */
public class Example70Test {

    private static final List<DataItem> VALUE = new CborBuilder().addArray().add("a").addMap().put("b", "c").end().end()
        .build();

    private static final byte[] ENCODED_VALUE = new byte[] { (byte) 0x82, 0x61, 0x61, (byte) 0xa1, 0x61, 0x62, 0x61,
            0x63 };

    @Test
    public void shouldEncode() throws CborException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(byteOutputStream);
        encoder.encode(VALUE);
        Assert.assertArrayEquals(ENCODED_VALUE, byteOutputStream.toByteArray());
    }

    @Test
    public void shouldDecode() throws CborException {
        InputStream inputStream = new ByteArrayInputStream(ENCODED_VALUE);
        CborDecoder decoder = new CborDecoder(inputStream);
        List<DataItem> dataItems = decoder.decode();
        Assert.assertArrayEquals(VALUE.toArray(), dataItems.toArray());
    }

}