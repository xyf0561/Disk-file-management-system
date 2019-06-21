package projectPCB;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
 
import projectPCB.FileModel;
import com.sun.xml.internal.bind.v2.util.FatalAdapter;
 
public class OSManager {
	
	public Map<String, FileModel> totalFiles = new HashMap<String, FileModel>();
	//定义FAT表
	private int[] fat = new int[128]; 
	//创建根目录 使用fat表的第一项
	private FileModel root = new FileModel("root", 0);
	private FileModel nowCatalog = root;
    public int  available = 0;
 
	
	public OSManager() {
		// TODO Auto-generated consructor stub
		//将FAT表初始化全部为0，并将第一位设为根目录的空间
		for(int i=0; i<fat.length ; i++ ) {
			fat[i] = 0;
		}
		fat[0] = 255; //255表示磁盘块已占用
		available = 127; //纪录磁盘剩余块数	
		root.setFather(root);
		totalFiles.put("root", root);
	}
	
	public int setFat(int size) {
		int[] startNum = new int[128];
		int i = 1; //纪录fat循环定位
		for(int j=0; j<size; i++) {
			if(fat[i] == 0) {
				startNum[j] = i; //纪录该文件所有磁盘块
				if(j>0) {
					fat[startNum[j-1]] = i; //fat上一磁盘块指向下一磁盘块地址
				}
				j++;
			}
		}
		fat[i-1] = 255;
		return startNum[0]; //返回该文件起始块盘号
	}
	/*
	 * 
	 * 该方法用于删除时释放FAT表的空间
	 */
	public void delFat(int startNum) {
		int nextPoint = fat[startNum];
		int nowPoint = startNum;
		int count = 0;
		while(fat[nowPoint] != 0) {
			nextPoint = fat[nowPoint];
			if(nextPoint == 255) {
				fat[nowPoint] =0;
				count++;
				break;
			} else {
				fat[nowPoint] = 0;
				count++;
				nowPoint = nextPoint;
			}
		}
		available += count;
	}
	
	/*
	 * 
	 * 以下为追加内容时修改fat表
	 * 
	 */
	
	public void reAddFat(int startNum, int addSize) {
		int nowPoint = startNum;
		int nextPoint = fat[startNum];
		while(fat[nowPoint] != 255) {
			nowPoint = nextPoint;
			nextPoint = fat[nowPoint];
		}//找到该文件终结盘块
 
		for(int i=1, count = 0; count <addSize ; i++ ) {
			if(fat[i] == 0) {
				fat[nowPoint] = i;
				nowPoint = i;
				count++;
				fat[nowPoint] = 255;//作为当前文件终结盘块
			}
		}
	}
	
	/*
	 * 	以下为创建文件和目录方法
	 * 	14R5黎志亮  
	 */
	public void createFile(String name, String type, int size) {
		
		if(available >= size) {	//判断磁盘剩余空间是否足够建立文件
			FileModel value = nowCatalog.subMap.get(name); //该目录下是否寻找同名目录或文件
			if(value != null) {  //判断该文件是否存在
				if(value.getAttr() == 3) {   //若存在同名目录 继续创建文件
					int startNum = setFat(size); 
					FileModel file = new FileModel(name, type, startNum, size);
					file.setFather(nowCatalog); //纪录上一层目录
					nowCatalog.subMap.put(name, file); //在父目录添加该文件
					totalFiles.put(file.getName(), file);
					available -= size;
					System.out.println("创建文件成功！");
					showFile();
				} else if(value.getAttr() == 2) { //若同名文件已存在，创建失败
					System.out.println("创建失败，该文件已存在！"); 
					showFile();
				}
			} else if(value == null) { //若无同名文件或文件夹，继续创建文件
				int startNum = setFat(size); 
				FileModel file = new FileModel(name, type, startNum, size);
				file.setFather(nowCatalog); //纪录上一层目录
				nowCatalog.subMap.put(name, file); //在父目录添加该文件
				totalFiles.put(file.getName(), file);
				available -= size;
				System.out.println("创建文件成功！");
				showFile();
				}
		} else {
			System.out.println("创建文件失败，磁盘空间不足！");
		}
	
	}
	
	public void createCatolog(String name) {
		
		if(available >= 1) { //判断磁盘空间是否足够创建文件夹
			
			FileModel value = nowCatalog.subMap.get(name); //判断该目录下是否存在同名目录或文件
			if(value != null) {
				if(value.getAttr() == 2) {
					int startNum = setFat(1);
					FileModel catalog = new FileModel(name, startNum);
					catalog.setFather(nowCatalog); //纪录上一层目录
					nowCatalog.subMap.put(name, catalog);
					available--;
					totalFiles.put(catalog.getName(), catalog);
					System.out.println("创建目录成功！");
					showFile();
				} else if(value.getAttr() == 3) {
					System.out.println("创建目录失败，该目录已存在！");
					showFile();
				} 
			} else if(value == null) {
				int startNum = setFat(1);
				FileModel catalog = new FileModel(name, startNum);
				catalog.setFather(nowCatalog); //纪录上一层目录
				nowCatalog.subMap.put(name, catalog);
				available--;
				totalFiles.put(catalog.getName(), catalog);
				System.out.println("创建目录成功！");
				showFile();
			}			
		} else {
			System.out.println("创建目录失败，磁盘空间不足！");
		}
	}
	
	
	/*
	 * 
	 * 以下为显示该目录下的所有文件信息
	 * 
	 */
	 
	public void showFile() {
		System.out.println("当前目录： " + nowCatalog.getName() + "");
		int k = 1;
		if(!nowCatalog.subMap.isEmpty()) {
			for(FileModel value : nowCatalog.subMap.values()) {
				if(value.getAttr() == 3) { //目录文件
					System.out.println("\n"+k);
					System.out.println("文件名 : " + value.getName());
					System.out.println("类型 ： " + "文件夹");
					System.out.println("起始盘块 ： " + value.getStartNum());
					System.out.println("大小 : " + value.getSize());
					
				}
				else if(value.getAttr() == 2) {
					System.out.println("\n"+k);
					System.out.println("文件名 : " + value.getName() + "." + value.getType());
					System.out.println("类型 ： " + "文件("+value.getType()+ "类型)");
					System.out.println("起始盘块 ： " + value.getStartNum());
					System.out.println("大小 : " + value.getSize());
				}
				k++;
			}
		}
		for(int i =0; i<2; i++)	
		System.out.println();
		System.out.println("磁盘剩余空间 ：" + available + "            " + "退出系统请输入exit");
		System.out.println();
	}
	/*
	 * 
	 * 以下为删除该目录下某个文件
	 * 
	 */
	public void deleteFile(String name) {
		
		FileModel value = nowCatalog.subMap.get(name);
		if(value == null) {
			System.out.println("删除失败，没有该文件或文件夹!");
		} else if(!value.subMap.isEmpty()) {
			System.out.println("删除失败，该文件夹内含有文件！");
		} else {
			nowCatalog.subMap.remove(name);
			delFat(value.getStartNum());
			if(value.getAttr() == 3) {
				System.out.println("文件夹 " + value.getName() + " 已成功删除");
				showFile();
			} else if(value.getAttr() == 2) {
				System.out.println("文件 " + value.getName() + "已成功删除");
				showFile();
			}
		}
	}
	
	public void reName(String name, String newName) {
		if(nowCatalog.subMap.containsKey(name)) {
			if(nowCatalog.subMap.containsKey(newName)) {
				System.out.println("重命名失败，同名文件已存在！");	
				showFile();
			} else {
				//nowCatalog.subMap.get(name).setName(newName);
				FileModel value = nowCatalog.subMap.get(name);
				value.setName(newName);
				nowCatalog.subMap.remove(name);
				nowCatalog.subMap.put(newName, value);
				System.out.println("重命名成功！");
				System.out.println();
				showFile();
			}
		} else {
			System.out.println("重命名失败，没有该文件！");
			showFile();
		}
	}
	
	
	
	public void change(String name, String type) {
		
		nowCatalog = nowCatalog.getFather();
		if(nowCatalog.subMap.containsKey(name)) {
			FileModel value = nowCatalog.subMap.get(name);
			if(value.getAttr() == 2) {
				value.setType(type);
				nowCatalog.subMap.remove(name);
				nowCatalog.subMap.put(name, value);
				System.out.println("修改类型成功！");
				System.out.println("文件关闭！");
				showFile();
			} else if(value.getAttr() == 3) {
				System.out.println("修改错误，文件夹无法修改类型！");
				open(value.getName());
			}
		} else {
			System.out.println("修改错误，请检查输入文件名是否正确！");
		}
	}
	
	/*
	 * 以下为打开文件或文件夹方法
	 * 
	 */
	
	public void open(String name) {
		if(nowCatalog.subMap.containsKey(name)) {
			FileModel value = nowCatalog.subMap.get(name);
			if(value.getAttr() == 2) {
				nowCatalog = value;
				System.out.println("文件已打开，文件大小为 : " + value.getSize());				
			} else if(value.getAttr() == 3) {
				nowCatalog = value;
				System.out.println("文件夹已打开！");
				showFile();
			}
		} else {
			System.out.println("打开失败，文件不存在！");
		}
	}
	
	public void close() {
		
		if(nowCatalog.getAttr()==2) {
			
			System.out.println("文件已关闭！");
			backFile() ;
		}
		else
			System.out.println("关闭失败，当前没有打开文件！");
	}
	
	
	public void Add(String name, int addSize) {
		
		if(available >= addSize) {
			nowCatalog = nowCatalog.getFather();
			if(nowCatalog.subMap.containsKey(name)) {
				FileModel value = nowCatalog.subMap.get(name);
				if(value.getAttr() == 2) {
					value.setSize(value.getSize() + addSize);
					reAddFat(value.getStartNum(), addSize);
					System.out.println("追加内容成功！正在重新打开文件...");
					open(name);
				} else{
					System.out.println("追加内容失败，请确认文件名是否正确输入。");					
				}
			} else {
				System.out.println("追加内容失败，请确认文件名是否正确输入！");
				showFile();
			}
		} else {
			System.out.println("追加内容失败，内存空间不足！");
		}
	}
	
	
	/*
	 * 
	 * 以下为返回上一层目录
	 * 
	 */
	
	public void backFile() {
		if(nowCatalog.getFather() == null) {
			System.out.println("该文件没有上级目录！");
		} else {
			nowCatalog = nowCatalog.getFather();
			showFile();
		}
	}
	
	public void FAT() {
 
		for(int j=0; j<120; j+=10) {
			System.out.println("序号 | " + j + "   \t" + (j+1) + "   \t" + (j+2) + "   \t"
					+ (j+3) + "   \t" + (j+4)+ "   \t" + (j+5)+ "   \t" + (j+6)+ "   \t" + (j+7)
					+ "   \t"+ (j+8)+ "   \t"+ (j+9));
			System.out.println("值     | " + fat[j] + "   \t" + fat[j+1] + "   \t"+ fat[j+2]
					 + "   \t" + fat[j+3] + "   \t" + fat[j+4]+ "   \t" + fat[j+5]+"   \t" + fat[j+6]
							 + "   \t" + fat[j+7]+ "   \t" + fat[j+8]+ "   \t"+ fat[j+9]);
			System.out.println();
		}
		int j = 125;
		System.out.println("序号 | " + j + "   \t"+ (j+1) + "   \t" + (j+2));
		System.out.println("值     | " + fat[j] + "   \t" + fat[j+1] + "   \t" + fat[j+2]);
		System.out.println();
		showFile();
	}
}
