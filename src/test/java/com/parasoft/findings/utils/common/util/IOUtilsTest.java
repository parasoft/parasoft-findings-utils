package com.parasoft.findings.utils.common.util;

import com.parasoft.findings.utils.common.logging.FindingsLogger;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IOUtilsTest {

    private final IOException ioException = new IOException("Expected test error");

    /**
     * Test {@link IOUtils#close(InputStream)}
     */
    @Test
    public void testClose1_normal() throws IOException {
        InputStream inputStream = mock(InputStream.class);

        IOUtils.close(inputStream);

        verify(inputStream, Mockito.atLeastOnce()).close();
    }

    /**
     * Test {@link IOUtils#close(InputStream)}
     */
    @Test
    public void testClose1_withIOException() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        doThrow(ioException).when(inputStream).close();
        try (MockedStatic<Logger> mockedStatic = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStatic.when(Logger::getLogger).thenReturn(logger);

            IOUtils.close(inputStream);

            verify(logger, times(1)).error(eq("Error while closing input stream."), any());
            assertThrows(IOException.class, inputStream::close);
        }
    }

    /**
     * Test {@link IOUtils#close(OutputStream)}
     */
    @Test
    public void testClose2_normal() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);

        IOUtils.close(outputStream);

        verify(outputStream, Mockito.atLeastOnce()).close();
    }

    /**
     * Test {@link IOUtils#close(OutputStream)}
     */
    @Test
    public void testClose2_withIOException() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);
        doThrow(ioException).when(outputStream).close();
        try (MockedStatic<Logger> mockedStatic = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStatic.when(Logger::getLogger).thenReturn(logger);

            IOUtils.close(outputStream);

            verify(logger, times(1)).error(eq("Error while closing output stream"), any());
            assertThrows(IOException.class, outputStream::close);
        }
    }

    /**
     * Test {@link IOUtils#close(Reader)}
     */
    @Test
    public void testClose3_normal() throws IOException {
        Reader reader = mock(Reader.class);
        IOUtils.close(reader);

        verify(reader, Mockito.atLeastOnce()).close();
    }

    /**
     * Test {@link IOUtils#close(Reader)}
     */
    @Test
    public void testClose3_withIOException() throws IOException {
        Reader reader = mock(Reader.class);
        doThrow(ioException).when(reader).close();
        try (MockedStatic<Logger> mockedStatic = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStatic.when(Logger::getLogger).thenReturn(logger);

            IOUtils.close(reader);

            verify(logger, times(1)).error(eq("Error while closing input stream."), any());
            assertThrows(IOException.class, reader::close);
        }
    }

    /**
     * Test {@link IOUtils#close(Writer)}
     */
    @Test
    public void testClose4_normal() throws IOException {
        Writer writer = mock(Writer.class);

        IOUtils.close(writer);

        verify(writer, Mockito.atLeastOnce()).close();
    }

    /**
     * Test {@link IOUtils#close(Writer)}
     */
    @Test
    public void testClose4_withIOException() throws IOException {
        Writer writer = mock(Writer.class);
        doThrow(ioException).when(writer).close();
        try (MockedStatic<Logger> mockedStatic = Mockito.mockStatic(Logger.class)) {
            FindingsLogger logger = mock(FindingsLogger.class);
            mockedStatic.when(Logger::getLogger).thenReturn(logger);

            IOUtils.close(writer);

            verify(logger, times(1)).error(eq("Error while closing output stream."), any());
            assertThrows(IOException.class, writer::close);
        }

        IOUtils.close(writer);

        assertThrows(IOException.class, writer::close);
    }

    @Test
    public void testMkdirs() {
        File existingDir = new File("src/test/resources/xml/staticanalysis");
        File incorrectDir = new File("");

        //When given path is null
        assertNull(IOUtils.mkdirs(null));

        //When dir could not be created
        assertNull(IOUtils.mkdirs(incorrectDir.getPath()));

        //When given path is existing directory
        assertEquals(existingDir, IOUtils.mkdirs(existingDir.getPath()));

        //When dir is not existing - create a new dir
        File dirToBeCreated = new File("src/test/resources/xml/staticanalysis/dirToBeCreated");
        try {
            assertEquals(dirToBeCreated, IOUtils.mkdirs(dirToBeCreated.getPath()));
        } finally {
            if (dirToBeCreated.exists()) {
                dirToBeCreated.delete();
            }
        }
    }

    @Test
    public void testCopy() throws IOException {
        File sourceFile = new File("src/test/resources/xml/staticanalysis/", "cpptest_pro_report_202001.xml");
        File destinationFile = new File("src/test/resources/xml/", "destinationFile.xml");
        try {
            InputStream source = Files.newInputStream(sourceFile.toPath());
            OutputStream destination = Files.newOutputStream(destinationFile.toPath());
            assertEquals(0, destinationFile.length());

            IOUtils.copy(source, destination);
            assertEquals(sourceFile.length(), destinationFile.length());
        } finally {
            if (destinationFile.exists()) {
                destinationFile.delete();
            }
        }
    }
}
