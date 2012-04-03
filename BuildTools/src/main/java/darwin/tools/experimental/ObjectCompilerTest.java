package darwin.tools.experimental;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import darwin.geometrie.io.*;
import darwin.geometrie.io.obj.*;

/**
 *
 * @author some
 */
public class ObjectCompilerTest
{

    public void compile()
    {
        System.out.println("----- Compiling Object Files -----");
        File folder = new File("src/resources/Models");
        File[] files = folder.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith("obj");
            }
        });

        long parse = 0, save = 0, total = 0, comp = 0;
        int fileCount = files.length;
        for (File obj : files) {
            long time = System.currentTimeMillis();
            try (InputStream in = ResourcesLoader.getRessource("resources/Models/" + obj.getName());) {
                ModelReader mr = new ObjModelReader();
                ModelWriter mw = new CtmModelWriter();
                try (FileOutputStream fos = new FileOutputStream(
                                obj.getAbsolutePath() + mw.getDefaultFileExtension())) {
                    try {
                        mw.writeModel(fos, mr.readModel(in));
                    } catch (WrongFileTypeException ex) {
                        ex.printStackTrace();
                    }
                    fos.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                fileCount--;
            }
        }
        System.out.println("Objects Compressed: " + fileCount);
        System.out.println("Parse time: " + parse
                + "ms;  Write/Compress time: " + save + "ms");
        System.out.println("Compression-Ratio: " + (float) comp / total);
        System.out.println();
    }

    public void load()
    {
        System.out.println("----- Load Compressed Object Files -----");
        File folder = new File("src/resources/Models");
        File[] files = folder.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith("obj.bin");
            }
        });
        long load = 0;
        for (File obj : files) {
            try {
                FileInputStream fos = new FileInputStream(
                        obj.getAbsolutePath());
                try (ObjectInputStream oos = new ObjectInputStream(fos)) {
                    try {
                        long time = System.currentTimeMillis();
                        Object o = oos.readObject();
                        load += System.currentTimeMillis() - time;
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Loading time: " + load + "ms");
        System.out.println();
    }

    public static void main(String[] args) throws IOException
    {
        new ObjectCompilerTest().compile();
    }
}
