/*
 * 版权：2012-2013 ChinaTsp Co.,Ltd
 × 描述：用于进行文件操作的工具类
 * 创建人：liuderu
 * 创建时间：2018-1-26
 */
package com.chinatsp.vehiclesetting.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author liuderu
 * @version [版本号， 2018-1-26]
 * @since [产品/模块版本]
 */
public class TspFileUtils {

	/**
	 * 删除文件操作
	 * @param file 要删除的文件对象
	 * @return 是否删除成功
	 */
	public static boolean deleteFile(File file) {
		if (file == null || !file.exists() || !file.isFile()) {
			return false;
		}

		return file.delete();
	}

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

	/**
	 * 删除目录中的文件，不删除目录
	 * @param path 要操作的目录路径
	 * @return 是否操作成功
	 */
	public static boolean deleteFilesInFolder(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				deleteFilesInFolder(path + File.separator + tempList[i]);
				deleteFolder(path + File.separator + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 删除目录（包括子目录和所有文件）
	 * @param folderPath 要操作的目录路径
	 */
	public static boolean deleteFolder(String folderPath) {
	    boolean ret = false;
		try {
            ret = deleteFilesInFolder(folderPath);
			File myFilePath = new File(folderPath);
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
            ret = false;
		}
		return ret;
	}

	/**
	 * 获取文本类型文件的内容
	 * @param path 文件路径
	 * @return 文件内容
	 */
	public static String readTextFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return "";
		}

		if (file.isDirectory()) {
			return "";
		}

		FileInputStream localFileInputStream;
		try {
			localFileInputStream = new FileInputStream(file);
			InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream, "UTF-8");
			String str = new BufferedReader(localInputStreamReader).readLine();
			if (str == null) {
				str = "";
			}
			localFileInputStream.close();
			localInputStreamReader.close();
			
			return str;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String md5 = bigInt.toString(16);
        int md5length = md5.length();
        String tmpdataString = "000000000000000000000000000000000000000000";
        if (md5length < 32) {
            int tmp = 32 - md5length;
            String ss = tmpdataString.substring(0, tmp);
            md5 = ss + md5;
        }
        return md5;
    }
}
