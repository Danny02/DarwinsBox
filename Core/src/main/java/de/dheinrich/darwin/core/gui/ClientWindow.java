package de.dheinrich.darwin.core.gui;

import com.jogamp.opengl.util.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.swing.*;
import org.apache.log4j.*;
import org.apache.log4j.spi.*;

/**
 * Kein Fenster im eigentlichen Sinne, sondern mehr eine Ansammlung der
 * Objekte, aus denen das Fenster aufgebaut ist.
 * Dient zur Initialisierung des Renderers und dem Zugriff auf dessen
 * Komponenten.
 *
 * @author Daniel Heinrich
 *
 */
public class ClientWindow extends JFrame implements ShutdownListener, AnimatorFactory {

    public final Client client;

    public ClientWindow(int xSize, int ySize, boolean fullscreen) {
        client = new Client(this);
        client.addShutdownListener(this);
        client.addLogAppender(new AppenderSkeleton() {

            @Override
            protected void append(LoggingEvent event) {
                if (event.getLevel() == Level.FATAL) {
                    ThrowableInformation ti = event.getThrowableInformation();
                    if(ti!=null)
                        ti.getThrowable().printStackTrace();
                    System.exit(1);
                }
            }

            @Override
            public void close() {
            }

            @Override
            public boolean requiresLayout() {
                return false;
            }
        });

        // TODO: Fullscreen
        iniFrame(xSize, ySize);
//        canvas.setLayout(new OverlayLayout(canvas));
//        JPanel p = new ColorCorrection();
//        p.setAlignmentX(0);
//        p.setAlignmentY(0);
//        canvas.add(p);
    }

    public void startUp() {
        client.iniClient(this);
    }

    @Override
    public AnimatorBase createAnimator(GLAutoDrawable drawable) {
        Animator a = new Animator(drawable);
        a.setRunAsFastAsPossible(true);
        return a;
    }

    @Override
    public void doShutDown() {
        /*
         * Zur Sicherheit in eigenem Thread ausführen, da das
         * Swing-System blockieren könnte.
         */
        new Thread(new Runnable() {

            @Override
            public void run() {
                client.shutdown();
                System.exit(0);
            }
        }).start();
    }

    private void iniFrame(int xSize, int ySize) {
        setSize(xSize, ySize);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createClosingHandler();
    }

    private void createClosingHandler() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                doShutDown();
            }
        });
    }
}
