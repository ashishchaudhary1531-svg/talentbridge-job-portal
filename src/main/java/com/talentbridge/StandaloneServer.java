package com.talentbridge;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/** Runs the packaged WAR with the provided embedded Tomcat runtime. */
public final class StandaloneServer {
    private StandaloneServer() { }

    public static void main(String[] args) throws Exception {
        Path war = Path.of("target", "talentbridge.war").toAbsolutePath();
        if (!Files.isRegularFile(war)) {
            throw new IllegalStateException("Build the WAR first: run mvnw.cmd -DskipTests package");
        }

        Path base = Path.of("target", "embedded-tomcat").toAbsolutePath();
        Files.createDirectories(base);
        Path pidFile = base.resolve("talentbridge.pid");
        Path stopRequest = base.resolve("stop.request");
        Files.deleteIfExists(pidFile);
        Files.deleteIfExists(stopRequest);
        System.setProperty("catalina.base", base.toString());
        System.setProperty("catalina.home", base.toString());

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(base.toString());
        tomcat.setPort(Integer.getInteger("server.port", 8080));
        tomcat.getConnector().setProperty("address", System.getProperty("server.address", "127.0.0.1"));
        Context context = tomcat.addWebapp("", war.toString());
        context.setParentClassLoader(StandaloneServer.class.getClassLoader());
        tomcat.start();

        long processId = ProcessHandle.current().pid();
        Files.writeString(pidFile, Long.toString(processId));
        AtomicBoolean stopping = new AtomicBoolean();
        Runnable stopServer = () -> {
            if (!stopping.compareAndSet(false, true)) return;
            try { tomcat.stop(); tomcat.destroy(); }
            catch (Exception ex) { System.err.println("Tomcat shutdown: " + ex.getMessage()); }
            finally {
                try { Files.deleteIfExists(pidFile); Files.deleteIfExists(stopRequest); }
                catch (IOException ex) { System.err.println("Shutdown marker cleanup: " + ex.getMessage()); }
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(stopServer, "talentbridge-shutdown"));

        Thread stopMonitor = new Thread(() -> {
            while (!stopping.get()) {
                try {
                    if (Files.exists(stopRequest)) {
                        String requestedProcessId = Files.readString(stopRequest).trim();
                        if (Long.toString(processId).equals(requestedProcessId)) {
                            System.out.println("TalentBridge stop request received.");
                            stopServer.run();
                            return;
                        }
                        Files.deleteIfExists(stopRequest);
                    }
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (IOException ex) {
                    System.err.println("Shutdown monitor: " + ex.getMessage());
                }
            }
        }, "talentbridge-stop-monitor");
        stopMonitor.setDaemon(true);
        stopMonitor.start();

        String address = System.getProperty("server.address", "127.0.0.1");
        int port = tomcat.getConnector().getLocalPort();
        System.out.printf("TalentBridge is running at http://%s:%d/%nPress Ctrl+C to stop.%n", address, port);
        tomcat.getServer().await();
    }
}
