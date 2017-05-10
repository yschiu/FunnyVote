package com.heaton.funnyvote;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class FileUtilsTest {
    @Test
    public void testGetExtensionShouldReturnNullIfInputNull() {
        String result = FileUtils.getExtension(null);
        assertThat(result, nullValue());
    }

    @Test
    public void testGetExtensionShouldReturnPngIfInputPngFile() {
        String result = FileUtils.getExtension("taiwannumberone.png");
        assertThat(result, is(".png"));
    }
}
