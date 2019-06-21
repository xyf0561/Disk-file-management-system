package projectPCB;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
 
import projectPCB.FileModel;
import com.sun.xml.internal.bind.v2.util.FatalAdapter;
 
public class OSManager {
	
	public Map<String, FileModel> totalFiles = new HashMap<String, FileModel>();
	//����FAT��
	private int[] fat = new int[128]; 
	//������Ŀ¼ ʹ��fat��ĵ�һ��
	private FileModel root = new FileModel("root", 0);
	private FileModel nowCatalog = root;
    public int  available = 0;
 
	
	public OSManager() {
		// TODO Auto-generated consructor stub
		//��FAT���ʼ��ȫ��Ϊ0��������һλ��Ϊ��Ŀ¼�Ŀռ�
		for(int i=0; i<fat.length ; i++ ) {
			fat[i] = 0;
		}
		fat[0] = 255; //255��ʾ���̿���ռ��
		available = 127; //��¼����ʣ�����	
		root.setFather(root);
		totalFiles.put("root", root);
	}
	
	public int setFat(int size) {
		int[] startNum = new int[128];
		int i = 1; //��¼fatѭ����λ
		for(int j=0; j<size; i++) {
			if(fat[i] == 0) {
				startNum[j] = i; //��¼���ļ����д��̿�
				if(j>0) {
					fat[startNum[j-1]] = i; //fat��һ���̿�ָ����һ���̿��ַ
				}
				j++;
			}
		}
		fat[i-1] = 255;
		return startNum[0]; //���ظ��ļ���ʼ���̺�
	}
	/*
	 * 
	 * �÷�������ɾ��ʱ�ͷ�FAT��Ŀռ�
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
	 * ����Ϊ׷������ʱ�޸�fat��
	 * 
	 */
	
	public void reAddFat(int startNum, int addSize) {
		int nowPoint = startNum;
		int nextPoint = fat[startNum];
		while(fat[nowPoint] != 255) {
			nowPoint = nextPoint;
			nextPoint = fat[nowPoint];
		}//�ҵ����ļ��ս��̿�
 
		for(int i=1, count = 0; count <addSize ; i++ ) {
			if(fat[i] == 0) {
				fat[nowPoint] = i;
				nowPoint = i;
				count++;
				fat[nowPoint] = 255;//��Ϊ��ǰ�ļ��ս��̿�
			}
		}
	}
	
	/*
	 * 	����Ϊ�����ļ���Ŀ¼����
	 * 	14R5��־��  
	 */
	public void createFile(String name, String type, int size) {
		
		if(available >= size) {	//�жϴ���ʣ��ռ��Ƿ��㹻�����ļ�
			FileModel value = nowCatalog.subMap.get(name); //��Ŀ¼���Ƿ�Ѱ��ͬ��Ŀ¼���ļ�
			if(value != null) {  //�жϸ��ļ��Ƿ����
				if(value.getAttr() == 3) {   //������ͬ��Ŀ¼ ���������ļ�
					int startNum = setFat(size); 
					FileModel file = new FileModel(name, type, startNum, size);
					file.setFather(nowCatalog); //��¼��һ��Ŀ¼
					nowCatalog.subMap.put(name, file); //�ڸ�Ŀ¼��Ӹ��ļ�
					totalFiles.put(file.getName(), file);
					available -= size;
					System.out.println("�����ļ��ɹ���");
					showFile();
				} else if(value.getAttr() == 2) { //��ͬ���ļ��Ѵ��ڣ�����ʧ��
					System.out.println("����ʧ�ܣ����ļ��Ѵ��ڣ�"); 
					showFile();
				}
			} else if(value == null) { //����ͬ���ļ����ļ��У����������ļ�
				int startNum = setFat(size); 
				FileModel file = new FileModel(name, type, startNum, size);
				file.setFather(nowCatalog); //��¼��һ��Ŀ¼
				nowCatalog.subMap.put(name, file); //�ڸ�Ŀ¼��Ӹ��ļ�
				totalFiles.put(file.getName(), file);
				available -= size;
				System.out.println("�����ļ��ɹ���");
				showFile();
				}
		} else {
			System.out.println("�����ļ�ʧ�ܣ����̿ռ䲻�㣡");
		}
	
	}
	
	public void createCatolog(String name) {
		
		if(available >= 1) { //�жϴ��̿ռ��Ƿ��㹻�����ļ���
			
			FileModel value = nowCatalog.subMap.get(name); //�жϸ�Ŀ¼���Ƿ����ͬ��Ŀ¼���ļ�
			if(value != null) {
				if(value.getAttr() == 2) {
					int startNum = setFat(1);
					FileModel catalog = new FileModel(name, startNum);
					catalog.setFather(nowCatalog); //��¼��һ��Ŀ¼
					nowCatalog.subMap.put(name, catalog);
					available--;
					totalFiles.put(catalog.getName(), catalog);
					System.out.println("����Ŀ¼�ɹ���");
					showFile();
				} else if(value.getAttr() == 3) {
					System.out.println("����Ŀ¼ʧ�ܣ���Ŀ¼�Ѵ��ڣ�");
					showFile();
				} 
			} else if(value == null) {
				int startNum = setFat(1);
				FileModel catalog = new FileModel(name, startNum);
				catalog.setFather(nowCatalog); //��¼��һ��Ŀ¼
				nowCatalog.subMap.put(name, catalog);
				available--;
				totalFiles.put(catalog.getName(), catalog);
				System.out.println("����Ŀ¼�ɹ���");
				showFile();
			}			
		} else {
			System.out.println("����Ŀ¼ʧ�ܣ����̿ռ䲻�㣡");
		}
	}
	
	
	/*
	 * 
	 * ����Ϊ��ʾ��Ŀ¼�µ������ļ���Ϣ
	 * 
	 */
	 
	public void showFile() {
		System.out.println("��ǰĿ¼�� " + nowCatalog.getName() + "");
		int k = 1;
		if(!nowCatalog.subMap.isEmpty()) {
			for(FileModel value : nowCatalog.subMap.values()) {
				if(value.getAttr() == 3) { //Ŀ¼�ļ�
					System.out.println("\n"+k);
					System.out.println("�ļ��� : " + value.getName());
					System.out.println("���� �� " + "�ļ���");
					System.out.println("��ʼ�̿� �� " + value.getStartNum());
					System.out.println("��С : " + value.getSize());
					
				}
				else if(value.getAttr() == 2) {
					System.out.println("\n"+k);
					System.out.println("�ļ��� : " + value.getName() + "." + value.getType());
					System.out.println("���� �� " + "�ļ�("+value.getType()+ "����)");
					System.out.println("��ʼ�̿� �� " + value.getStartNum());
					System.out.println("��С : " + value.getSize());
				}
				k++;
			}
		}
		for(int i =0; i<2; i++)	
		System.out.println();
		System.out.println("����ʣ��ռ� ��" + available + "            " + "�˳�ϵͳ������exit");
		System.out.println();
	}
	/*
	 * 
	 * ����Ϊɾ����Ŀ¼��ĳ���ļ�
	 * 
	 */
	public void deleteFile(String name) {
		
		FileModel value = nowCatalog.subMap.get(name);
		if(value == null) {
			System.out.println("ɾ��ʧ�ܣ�û�и��ļ����ļ���!");
		} else if(!value.subMap.isEmpty()) {
			System.out.println("ɾ��ʧ�ܣ����ļ����ں����ļ���");
		} else {
			nowCatalog.subMap.remove(name);
			delFat(value.getStartNum());
			if(value.getAttr() == 3) {
				System.out.println("�ļ��� " + value.getName() + " �ѳɹ�ɾ��");
				showFile();
			} else if(value.getAttr() == 2) {
				System.out.println("�ļ� " + value.getName() + "�ѳɹ�ɾ��");
				showFile();
			}
		}
	}
	
	public void reName(String name, String newName) {
		if(nowCatalog.subMap.containsKey(name)) {
			if(nowCatalog.subMap.containsKey(newName)) {
				System.out.println("������ʧ�ܣ�ͬ���ļ��Ѵ��ڣ�");	
				showFile();
			} else {
				//nowCatalog.subMap.get(name).setName(newName);
				FileModel value = nowCatalog.subMap.get(name);
				value.setName(newName);
				nowCatalog.subMap.remove(name);
				nowCatalog.subMap.put(newName, value);
				System.out.println("�������ɹ���");
				System.out.println();
				showFile();
			}
		} else {
			System.out.println("������ʧ�ܣ�û�и��ļ���");
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
				System.out.println("�޸����ͳɹ���");
				System.out.println("�ļ��رգ�");
				showFile();
			} else if(value.getAttr() == 3) {
				System.out.println("�޸Ĵ����ļ����޷��޸����ͣ�");
				open(value.getName());
			}
		} else {
			System.out.println("�޸Ĵ������������ļ����Ƿ���ȷ��");
		}
	}
	
	/*
	 * ����Ϊ���ļ����ļ��з���
	 * 
	 */
	
	public void open(String name) {
		if(nowCatalog.subMap.containsKey(name)) {
			FileModel value = nowCatalog.subMap.get(name);
			if(value.getAttr() == 2) {
				nowCatalog = value;
				System.out.println("�ļ��Ѵ򿪣��ļ���СΪ : " + value.getSize());				
			} else if(value.getAttr() == 3) {
				nowCatalog = value;
				System.out.println("�ļ����Ѵ򿪣�");
				showFile();
			}
		} else {
			System.out.println("��ʧ�ܣ��ļ������ڣ�");
		}
	}
	
	public void close() {
		
		if(nowCatalog.getAttr()==2) {
			
			System.out.println("�ļ��ѹرգ�");
			backFile() ;
		}
		else
			System.out.println("�ر�ʧ�ܣ���ǰû�д��ļ���");
	}
	
	
	public void Add(String name, int addSize) {
		
		if(available >= addSize) {
			nowCatalog = nowCatalog.getFather();
			if(nowCatalog.subMap.containsKey(name)) {
				FileModel value = nowCatalog.subMap.get(name);
				if(value.getAttr() == 2) {
					value.setSize(value.getSize() + addSize);
					reAddFat(value.getStartNum(), addSize);
					System.out.println("׷�����ݳɹ����������´��ļ�...");
					open(name);
				} else{
					System.out.println("׷������ʧ�ܣ���ȷ���ļ����Ƿ���ȷ���롣");					
				}
			} else {
				System.out.println("׷������ʧ�ܣ���ȷ���ļ����Ƿ���ȷ���룡");
				showFile();
			}
		} else {
			System.out.println("׷������ʧ�ܣ��ڴ�ռ䲻�㣡");
		}
	}
	
	
	/*
	 * 
	 * ����Ϊ������һ��Ŀ¼
	 * 
	 */
	
	public void backFile() {
		if(nowCatalog.getFather() == null) {
			System.out.println("���ļ�û���ϼ�Ŀ¼��");
		} else {
			nowCatalog = nowCatalog.getFather();
			showFile();
		}
	}
	
	public void FAT() {
 
		for(int j=0; j<120; j+=10) {
			System.out.println("��� | " + j + "   \t" + (j+1) + "   \t" + (j+2) + "   \t"
					+ (j+3) + "   \t" + (j+4)+ "   \t" + (j+5)+ "   \t" + (j+6)+ "   \t" + (j+7)
					+ "   \t"+ (j+8)+ "   \t"+ (j+9));
			System.out.println("ֵ     | " + fat[j] + "   \t" + fat[j+1] + "   \t"+ fat[j+2]
					 + "   \t" + fat[j+3] + "   \t" + fat[j+4]+ "   \t" + fat[j+5]+"   \t" + fat[j+6]
							 + "   \t" + fat[j+7]+ "   \t" + fat[j+8]+ "   \t"+ fat[j+9]);
			System.out.println();
		}
		int j = 125;
		System.out.println("��� | " + j + "   \t"+ (j+1) + "   \t" + (j+2));
		System.out.println("ֵ     | " + fat[j] + "   \t" + fat[j+1] + "   \t" + fat[j+2]);
		System.out.println();
		showFile();
	}
}
