//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.glsx.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileUtils {
    public FileUtils() {
    }

    public static void createDir(String path) throws Exception {
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }

        } catch (Exception var2) {
            throw var2;
        }
    }

    public static void createFile(String path) throws Exception {
        if (!StringUtils.isEmpty(path)) {
            try {
                File f = new File(path);
                if (!f.exists()) {
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }

                    f.createNewFile();
                }
            } catch (Exception var2) {
                throw var2;
            }
        }
    }

    public static ArrayList<String> getFileName(String dirpath) {
        File f = new File(dirpath);
        if (!f.exists()) {
            return null;
        } else {
            ArrayList<String> result = new ArrayList();
            File file = new File(dirpath);
            File[] tempList = file.listFiles();

            for(int i = 0; i < tempList.length; ++i) {
                if (tempList[i].isFile()) {
                    result.add(tempList[i].getName());
                }
            }

            return result;
        }
    }

    public static ArrayList<String> getDirName(String dirpath) {
        File f = new File(dirpath);
        if (!f.exists()) {
            return null;
        } else {
            ArrayList<String> result = new ArrayList();
            File file = new File(dirpath);
            File[] tempList = file.listFiles();

            for(int i = 0; i < tempList.length; ++i) {
                if (tempList[i].isDirectory()) {
                    result.add(tempList[i].getName());
                }
            }

            return result;
        }
    }

    public static ArrayList<String> getFilePath(String dirpath) {
        File f = new File(dirpath);
        if (!f.exists()) {
            return null;
        } else {
            File file = new File(dirpath);
            File[] files = file.listFiles();
            ArrayList<String> filesPath = new ArrayList();
            File[] var5 = files;
            int var6 = files.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                File a = var5[var7];
                if (a.isFile()) {
                    filesPath.add(a.getAbsolutePath());
                }
            }

            return filesPath;
        }
    }

    public static ArrayList<String> getAllFileName(String dirpath) {
        File f = new File(dirpath);
        if (!f.exists()) {
            return null;
        } else {
            ArrayList<String> fileList = new ArrayList();
            File file = new File(dirpath);
            File[] files = file.listFiles();
            File[] var5 = files;
            int var6 = files.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                File a = var5[var7];
                if (a.isFile()) {
                    fileList.add(a.getName());
                } else if (a.isDirectory()) {
                    fileList.addAll(getAllFileName(a.getAbsolutePath()));
                }
            }

            return fileList;
        }
    }

    public static ArrayList<String> getAllFilePath(String dirpath) {
        File f = new File(dirpath);
        if (!f.exists()) {
            return null;
        } else {
            ArrayList<String> filepathList = new ArrayList();
            File file = new File(dirpath);
            File[] files = file.listFiles();
            File[] var5 = files;
            int var6 = files.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                File a = var5[var7];
                if (a.isFile()) {
                    filepathList.add(a.getAbsolutePath());
                } else if (a.isDirectory()) {
                    filepathList.addAll(getAllFilePath(a.getAbsolutePath()));
                }
            }

            return filepathList;
        }
    }

    public static ArrayList<String> readFileToList(String filename) {
        File file = new File(filename);
        BufferedReader reader = null;
        ArrayList temp = new ArrayList();

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String str = "";

            while(null != (str = reader.readLine())) {
                temp.add(str);
            }
        } catch (IOException var13) {
            var13.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

        return temp;
    }

    public static ArrayList<String> readLineToList(String filename, String split) {
        File file = new File(filename);
        BufferedReader reader = null;
        ArrayList temp = new ArrayList();

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String str = reader.readLine();
            List<String> list = Arrays.asList(str.split(split));
            temp.addAll(list);
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

        return temp;
    }

    public static HashMap<String, String> readFileToMap(String filename, String split) {
        File file = new File(filename);
        BufferedReader reader = null;
        HashMap temp = new HashMap();

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String str = "";
            String[] tmp = null;

            while(null != (str = reader.readLine())) {
                tmp = str.split(split);
                if (tmp.length > 1) {
                    temp.put(tmp[0], tmp[1]);
                }
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

        return temp;
    }

    public static HashMap<String, Long> readFileToMap1(String filename, String split) {
        File file = new File(filename);
        BufferedReader reader = null;
        HashMap temp = new HashMap();

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String str = "";
            String[] tmp = null;

            while(null != (str = reader.readLine())) {
                tmp = str.split(split);
                if (tmp.length > 1) {
                    temp.put(tmp[0], Long.parseLong(tmp[1]));
                }
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

        return temp;
    }

    public static HashMap<String, List<String>> readFileToMapList(String filename, String split1, String split2) {
        File file = new File(filename);
        BufferedReader reader = null;
        HashMap temp = new HashMap();

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String str = "";
            String[] tmp = null;

            while(null != (str = reader.readLine())) {
                tmp = str.split(split1);
                if (tmp.length > 1) {
                    temp.put(tmp[0], Arrays.asList(tmp[1].split(split2)));
                }
            }
        } catch (IOException var16) {
            var16.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

        return temp;
    }

    public static void writeStringToFile(String fileName, String input) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            createFile(fileName);
        }

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            writer.write(input + "\n");
        } catch (IOException var13) {
            var13.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

    }

    public static void writeStringToFile1(String fileName, String input) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            createFile(fileName);
        }

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            writer.write(input + "\n");
        } catch (IOException var13) {
            var13.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

    }

    public static void writeListToFile(String fileName, List<String> input) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            createFile(fileName);
        }

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            Iterator var4 = input.iterator();

            while(var4.hasNext()) {
                String inp = (String)var4.next();
                writer.write(inp + "\n");
            }
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

    }

    public static void writeMapToFile(String fileName, Map<String, Long> input) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            createFile(fileName);
        }

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            Iterator iter = input.entrySet().iterator();

            while(iter.hasNext()) {
                Entry entry = (Entry)iter.next();
                writer.write(entry.getKey().toString() + "\t" + entry.getValue().toString() + "\n");
            }
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

    }

    public static void writeMapToFile1(String fileName, Map<String, String> input) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            createFile(fileName);
        }

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            Iterator iter = input.entrySet().iterator();

            while(iter.hasNext()) {
                Entry entry = (Entry)iter.next();
                writer.write(entry.getKey().toString() + "\t" + entry.getValue().toString() + "\n");
            }
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

    }

    public static void main(String[] args) throws Exception {
    }
}
