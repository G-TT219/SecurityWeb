package org.example.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.RemoteStub;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.EncryptedPrivateKeyInfo;
public class FileSM4 {
	//C:\Users\23126\Desktop\1.jpg
	//C:\Users\23126\Desktop\1(encrypt).jpg
	//@*#!@*#!@*#!@*#!
	private static final int MESSAGE_LENGTH=16;
	private String filepath;
//	CBC加密模式需要的数组异或操作
	private static byte[] xorArray(byte[] a,byte[] b) {
		byte[] result=new byte[MESSAGE_LENGTH];
		for(int i=0;i<MESSAGE_LENGTH;i++) {
			result[i]=(byte) (a[i]^b[i]);
		}
		return result;
	}
//	加密得到的文件的新路径
	private static String newPath_encrypt(String path) {
		int index=path.lastIndexOf(".");
		StringBuilder stringBuilder=new StringBuilder(path.substring(0,index));
		stringBuilder.append("(encrypt)");
		stringBuilder.append(path.substring(index));
		return stringBuilder.toString();
	}
//	解密得到的文件的新路径
	private static String newPath_decrypt(String path) {
		int index=path.lastIndexOf(".");
		StringBuilder stringBuilder=new StringBuilder(path.substring(0,index));
		stringBuilder.append("(decrypt)");
		stringBuilder.append(path.substring(index));
		return stringBuilder.toString();
	}
//	将用户输入的不足十六位的密码补足成十六位
	public static String keyPadding(String key) {
		if(key.length()<16) {
			int paddinglength=16-key.length();
			StringBuilder stringBuilder=new StringBuilder(key);
			for(int i=0;i<paddinglength;i++) {
				stringBuilder.append(1);
			}
		return stringBuilder.toString();
		}
		return key;
	}

//	加密函数
	public static void encrypt(String filepath,byte[] key) throws IOException {
		byte[] V=Sm4.SM4(key, key, 0);//	初始向量为用户输入的口令用自身做SM4加密
		FileInputStream fileInputStream=new FileInputStream(new File(filepath));
		File file=new File(newPath_encrypt(filepath));
		FileOutputStream fileOutputStream=new FileOutputStream(file);
		byte[] temp = new byte[MESSAGE_LENGTH];
		int len=0;
		fileOutputStream.write(V,0,MESSAGE_LENGTH);//	将初始向量写入文件头
//		开始加密
		while((len=fileInputStream.read(temp, 0, MESSAGE_LENGTH))>0) {
			if(len<MESSAGE_LENGTH) {//	末尾的情况
				for(int i=len;i<MESSAGE_LENGTH;i++) {
					if(i!=MESSAGE_LENGTH-1)//	在最后一个字节前填充0
						temp[i]=0;
					else {//	在最后一个字节填充末尾小于16个字节的字节数
						temp[i]=(byte)(len);
					}
				}
				byte[] cbc=xorArray(V, temp);
				byte[] resultBytes=Sm4.SM4(cbc, V, 0);
				V=resultBytes;
				fileOutputStream.write(resultBytes);
			}else {//	不在末尾的情况
				byte[] cbc=xorArray(V, temp);
				byte[] resultBytes=Sm4.SM4(cbc, V, 0);
				V=resultBytes;
				fileOutputStream.write(resultBytes);
			}
		}
		fileInputStream.close();
		fileOutputStream.close();
		System.out.println("加密成功");
	}
//	解密函数
	public static int decrypt(String filepath,byte[] key) throws IOException {
		byte[] V=new byte[MESSAGE_LENGTH];
		File inputFile =new File(filepath);
		FileInputStream fileInputStream=new FileInputStream(inputFile);
		File file=new File(newPath_decrypt(filepath));
		FileOutputStream fileOutputStream=new FileOutputStream(file);
		fileInputStream.read(V, 0, MESSAGE_LENGTH);//	文件头十六个字节为初始向量
		if(checkKey(V, Sm4.SM4(key,key,0))) { //	检查口令的正确性
			byte[] temp = new byte[MESSAGE_LENGTH];
			while(fileInputStream.read(temp, 0, MESSAGE_LENGTH)>0) {
				byte[] desm4=Sm4.SM4(temp, V, 1);
				byte[] resultBytes=xorArray(V, desm4);
				System.arraycopy(temp, 0, V, 0, MESSAGE_LENGTH);
				if(fileInputStream.available()==0) {//文件末尾的情况
					int len=(int)resultBytes[MESSAGE_LENGTH-1];
					if(len>0&&len<16) {
						for(int i=0;i<len;i++) {
							fileOutputStream.write(resultBytes[i]);
						}
					}else {
						fileOutputStream.write(resultBytes);
					}
				}else {
					fileOutputStream.write(resultBytes);
				}
			}
			System.out.println("解密成功");
			fileInputStream.close();
			fileOutputStream.close();
			return 1;
		}else {
			System.out.println("秘钥错误");
			fileInputStream.close();
			fileOutputStream.close();
			return 0;
		}
	}
//	解密前检查密钥
	private static boolean checkKey(byte[] a,byte[] b) {
		for(int i=0;i<16;i++) {
			if(a[i]!=b[i]) {
				return false;
			}
		}
		return true;
	}
}
