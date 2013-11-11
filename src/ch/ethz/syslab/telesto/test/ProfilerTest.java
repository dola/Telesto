package ch.ethz.syslab.telesto.test;

import org.junit.Test;

import ch.ethz.syslab.telesto.profile.BenchmarkLog;
import ch.ethz.syslab.telesto.profile.SimpleProfiler;

public class ProfilerTest {

    @Test
    public void testSimpleProfiler() throws InterruptedException {
        SimpleProfiler p = SimpleProfiler.get();
        p.startSection("Full Test");

        Thread.sleep(1000);
        p.startSection("inner part1");
        Thread.sleep(1200);
        p.endSection("inner part1");

        p.startSection("inner part2");
        Thread.sleep(200);
        p.endSection("inner part2");

        p.endSection("Full Test");

    }

    @Test
    public void overlappingProfiling() throws InterruptedException {
        SimpleProfiler p = SimpleProfiler.get();
        p.startSection("Full Test");

        Thread.sleep(1000);
        p.startSection("inner part1");
        Thread.sleep(500);
        p.startSection("inner part2");
        Thread.sleep(300);
        p.endSection("inner part1");
        Thread.sleep(200);
        p.endSection("inner part2");

        p.endSection("Full Test");

    }

    @Test
    // throws exception
    public void doubleNameProfiling() throws InterruptedException {
        SimpleProfiler p = SimpleProfiler.get();
        p.startSection("Full Test");

        Thread.sleep(1000);
        p.startSection("inner part1");
        Thread.sleep(500);
        p.startSection("inner part1");
        Thread.sleep(300);
        p.endSection("inner part1");
        Thread.sleep(200);
        p.endSection("inner part1");

        p.endSection("Full Test");

    }

    @Test
    public void testBenchmarkLog() throws InterruptedException {
        SimpleProfiler p = SimpleProfiler.get();
        BenchmarkLog l = new BenchmarkLog("testingLog");
        p.startSection("Full Test");

        Thread.sleep(1000);
        p.startSection("inner part1");
        Thread.sleep(500);
        p.startSection("inner part2");
        Thread.sleep(300);
        l.addEntry("inner part1", p.endSection("inner part1"));
        Thread.sleep(200);
        l.addEntry("inner part2", p.endSection("inner part2"));

        l.addEntry("Full Test", p.endSection("Full Test"));
        l.closeFile();
    }
}
