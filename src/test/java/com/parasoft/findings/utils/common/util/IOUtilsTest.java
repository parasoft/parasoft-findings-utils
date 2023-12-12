package com.parasoft.findings.utils.common.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class IOUtilsTest {

    private final IOException ioException = new IOException("Expected test error");

    @Test
    public void testCloseInputStream_normal() throws IOException {
        InputStream inputStream = mock(InputStream.class);

        IOUtils.close(inputStream);

        verify(inputStream, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testCloseInputStream_withIOException() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        doThrow(ioException).when(inputStream).close();

        IOUtils.close(inputStream);

        assertThrows(IOException.class, inputStream::close);
    }

    @Test
    public void testCloseReader_normal() throws IOException {
        Reader reader = mock(Reader.class);
        IOUtils.close(reader);

        verify(reader, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testCloseReader_withIOException() throws IOException {
        Reader reader = mock(Reader.class);
        doThrow(ioException).when(reader).close();

        IOUtils.close(reader);

        assertThrows(IOException.class, reader::close);
    }

    @Test
    public void testCloseWriter_normal() throws IOException {
        Writer writer = mock(Writer.class);

        IOUtils.close(writer);

        verify(writer, Mockito.atLeastOnce()).close();
    }

    @Test
    public void testCloseWriter_withIOException() throws IOException {
        Writer writer = mock(Writer.class);
        doThrow(ioException).when(writer).close();

        IOUtils.close(writer);

        assertThrows(IOException.class, writer::close);
    }
}
