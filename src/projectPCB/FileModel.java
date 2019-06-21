package projectPCB;

import java.util.ArrayList;
import java.util.HashMap;
 
import java.util.Map;
 
public class FileModel {
	
	public Map<String, FileModel> subMap = new HashMap<String, FileModel>();
	private String name; //�ļ�����Ŀ¼��
	private	String type; //�ļ�����
	private int attr; //����ʶ�����ļ�����Ŀ¼ 
	private int startNum;	//��FAT������ʼλ��
	private int size;	//�ļ��Ĵ�С
	private FileModel father = null;	//���ļ���Ŀ¼���ϼ�Ŀ¼
	
	public FileModel(String name, String type, int startNum, int size){
		this.name = name;
		this.type = type;
		this.attr = 2;
		this.startNum = startNum;
		this.size = size;		
	}
	
	public FileModel(String name, int startNum) {
		this.name = name;
		this.attr = 3;
		this.startNum = startNum;
		this.type = "  ";
		this.size = 1;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getAttr() {
		return attr;
	}
	public void setAttr(int attr) {
		this.attr = attr;
	}
	public int getStartNum() {
		return startNum;
	}
	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
 
	public FileModel getFather() {
		return father;
	}
 
	public void setFather(FileModel father) {
		this.father = father;
	}
	
}

