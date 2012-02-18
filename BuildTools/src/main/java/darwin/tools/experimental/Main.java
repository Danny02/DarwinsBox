package darwin.tools.experimental;

///*
// *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  (at your option) any later version.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package experimental;
//
//import LZMA.LzmaInputStream;
//import LZMA.LzmaOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
///**
// *
// * @author Daniel Heinrich <DannyNullZwo@gmail.com>
// */
//public class Main
//{
//    /**
//     * @param args the command line arguments
//     */
//    public static final String src =
//                               "C:/Users/some/Documents/Eigene Projekte/TheogoniaApplet/";
//    public static final String path = src + "src/resources/";
//
//    public static void main(String[] args) {
//        File dir = new File(path + "compressed/");
//        delete(dir);
//
////        dir = new File(path + "original/");
////        if (dir.exists()) {
////            for (File f : dir.listFiles())
////                f.delete();
////            dir.delete();
////        }
//
//        File folder = new File(path);
//        File[] files = folder.listFiles();
//        dir.mkdir();
//
//        Main m = new Main();
//        m.compressAll(files);
////        m.readAll();
//    }
//
//    private static void delete(File dir) {
//        if (dir.exists()) {
//            for (File f : dir.listFiles())
//                if (f.isDirectory())
//                    delete(f);
//                else
//                    f.delete();
//            dir.delete();
//        }
//    }
//
//    public void compressAll(File[] files) {
//        long time = System.currentTimeMillis();
//        for (File file : files)
//            if (file.isFile())
//                compress(file);
//            else if (!file.getName().equals(".svn"))
//                compressAll(file.listFiles());
//        time = System.currentTimeMillis() - time;
//        System.out.println(time);
//    }
//
//    public void readAll() {
//
//        File dir = new File(path + "compressed/");
//        File[] files = dir.listFiles();
//
//        dir = new File(path + "original/");
//        dir.mkdir();
//
//        long time = System.currentTimeMillis();
//        for (File obj : files)
//            read(obj);
//        time = System.currentTimeMillis() - time;
//        System.out.println(time);
//    }
//
//    public void compress(File obj) {
//        LzmaOutputStream los = null;
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(obj);
//
//            File dir = new File(path + "compressed/" + obj.getParentFile().
//                    getName());
//            dir.mkdir();
//            File f =
//                 new File(dir.getAbsolutePath() + "/" + obj.getName() + ".lzma");
//
//            FileOutputStream fos =
//                             new FileOutputStream(f);
//            los = new LzmaOutputStream(fos, 19, 273, 4);
//
//            int d;
//            while ((d = fis.read()) != -1)
//                los.write(d);
//
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (los != null) {
//                    los.flush();
//                    los.close();
//                }
//                if (fis != null)
//                    fis.close();
//            } catch (Exception ex) {
//            }
//        }
//    }
//
//    public void read(File file) {
//        FileOutputStream fos = null;
//        LzmaInputStream lis = null;
//        try {
//            FileInputStream fis = new FileInputStream(file);
//            lis = new LzmaInputStream(fis);
//            String name = file.getName();
//            File f =
//                 new File(path + "original/" + name.substring(0,
//                                                              name.length() - 5));
//            fos = new FileOutputStream(f);
//
//            int d;
//            while ((d = lis.read()) != -1)
//                fos.write(d);
//
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                lis.close();
//                fos.flush();
//                fos.close();
//            } catch (Exception ex) {
//            }
//        }
//    }
//}
