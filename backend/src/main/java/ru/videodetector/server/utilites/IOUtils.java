package ru.videodetector.server.utilites;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public class IOUtils {
    private final static int BUFFER_SIZE = 1024;

    public static void transferTo(InputStream is, OutputStream os, long start, long end) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        System.out.println("Skipped:" + start);
        System.out.println("End:" + end);
        System.out.println("delta:" + (end-start));
        long pos = 0;
        while (pos < start) {
            pos += is.read(buffer);
        }
        if (pos > start) {
            int len = (int)(pos-start);
            os.write(buffer, BUFFER_SIZE-len, len);
        }
        while (pos < end) {
            int readed = is.read(buffer);
            if (readed > 0) {
                readed = Math.min(readed, (int) (end - pos));
                pos += readed;
                os.write(buffer, 0, readed);
            } else {
                break;
            }
        }
        System.out.println("pos:" + (pos));
    }
}
