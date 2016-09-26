package edu.upenn.cis.cis455.webserver;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TaskTest {
    @Test
    public void path() throws IOException {
        Path path = Paths.get("/usr/bin/").toAbsolutePath();
//        String path2 = path.resolve("../passwd").toString();
        System.out.println(path.resolve("../lib").toRealPath(LinkOption.NOFOLLOW_LINKS));
//        assertTrue(path.resolve("../lib").toRealPath(LinkOption.NOFOLLOW_LINKS).startsWith(path));
    }
}