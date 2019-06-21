package projectPCB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import projectPCB.OSManager;
 
public class TestFileSystem {
	public static void main(String[] args) {
		try{
		OSManager manager = new OSManager();
		meun(manager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public static void meun(OSManager manager) {
		Scanner s = new Scanner(System.in);
		String str = null;
		System.out.println("��Ŀ�������һ��С�ʹ����ļ�����ϵͳ");
		System.out.println();
		manager.showFile();
 
		while ((str = s.nextLine()) != null) {
			if (str.equals("exit")) {
				System.out.println("��л����ʹ�ã�");
				break;
			}
 
			String[] strs = editStr(str);
			switch (strs[0]) {
			case "new":
				if (strs.length < 4) {
					System.out.println("���������������������");
				} else {
					manager.createFile(strs[1], strs[2],
							Integer.parseInt(strs[3]));
				}
				break;
			case "md":
				if (strs.length < 2) {
					System.out.println("��������������������飡");
				} else {
					manager.createCatolog(strs[1]);
				}
				break;
			case "open":
				if (strs.length < 2) {
					System.out.println("��������������������飡");
				} else {
					manager.open(strs[1]);
				}
				break;
			case "close":
				if (strs.length < 2) {
					System.out.println("��������������������飡");
				} else {
					manager.close();
				}
				break;
			case "cd":
				if (strs.length < 2) {
					System.out.println("��������������������飡");
				} else {
					manager.open(strs[1]);
				}
				break;
			case "cd..":
				manager.backFile();
				break;
			case "delete":
				if (strs.length < 2) {
					System.out.println("��������������������飡");
				} else {
					manager.deleteFile(strs[1]);
				}
				break;
			case "rename":
				if (strs.length < 3) {
					System.out.println("��������������������飡");
				} else {
					manager.reName(strs[1], strs[2]);
				}
				break;
			case "FAT":
				manager.FAT();
				break;
			case "add":
				if (strs.length < 3) {
					System.out.println("��������������������飡");
				} else {
					manager.Add(strs[1], Integer.parseInt(strs[2]));
				}
				break;
			case "change":
				if (strs.length < 3) {
					System.out.println("��������������������飡");
				} else {
					manager.change(strs[1], strs[2]);
				}
				break;
			default:{
				for(String st : strs)
					System.out.println(st);
				System.out.println("��������������������飡");
			}
			}
		}
	}
 
	public static String[] editStr(String str) {
		Pattern pattern = Pattern.compile("([a-zA-Z0-9.\\\\/]*) *");// ���ݿո�ָ���������
		Matcher m = pattern.matcher(str);
		ArrayList<String>  list = new ArrayList<String>();
		while(m.find()){
			list.add(m.group(1));
		}
		String[] strs = list.toArray(new String[list.size()]);
		
		for (int i = 1; i < strs.length; i++) { // �жϳ���������ÿһ���������Ƿ��� "."
			int j = strs[i].indexOf(".");
			if (j != -1) { // ������"." �����и� ȡǰ������Ϊ�ļ���
				String[] index = strs[i].split("\\."); // ʹ��ת���ַ�"\\."
				strs[i] = index[0];
			}
		} 
		return strs;
	}
}

