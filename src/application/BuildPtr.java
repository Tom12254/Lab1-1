package application;

import java.io.*;
import java.awt.*;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
//this is nonsense
public class BuildPtr{
	final int INF = 1000;
	final int Max = 100;
	public String mystr = null;
	String outtext = "";
	String oldstr = null;
	String newstr = null;
	Vector<String> myvec = new Vector<String>();
	Vector<String> element = new Vector<String>();
	int [][] myptr = new int[Max][Max];
	int [][] path = new int[Max][Max];
	int [][] floyd = new int [Max][Max];
	boolean [][] isvisited = new boolean[Max][Max];
	Set <Integer> set1 = new HashSet<Integer>();
	Set <Integer> set2 = new HashSet<Integer>();
	public String getfile(){
		Frame f = new Frame("FileDialog Test");
		 FileDialog d1 = new FileDialog(f, "Open File", FileDialog.LOAD);
		 d1.setVisible(true);
		 f.setVisible(true);
		 f.dispose();
		 return d1.getDirectory() + d1.getFile();
	}

	public void init(String dir){
		try{
			File f = new File(dir);
			FileInputStream rf = new FileInputStream(f);
			int len = 512, n = 512;
			StringBuilder sb = new StringBuilder();
			byte buffer[] = new byte[n];
			while(true){
				len = rf.read(buffer,0,n);
				sb.append(new String(buffer,0,len));
				if(len < n) break;
			}
			rf.close();
			this.mystr = sb.toString().toLowerCase();
		}catch (FileNotFoundException e1) {
            // 抛出文件路径找不到异常
            e1.printStackTrace();
        } catch (IOException e1) {
            // 抛出IO异常
            e1.printStackTrace();
        }
		if(mystr != null){
			for(int i = 0; i < Max; i ++){
				for(int j = 0; j < Max; j++){
					myptr[i][j] = INF;
					path[i][j] = -1;
				}
			}
			Pattern p=Pattern.compile("[A-Za-z]+");
			Matcher m=p.matcher(mystr);
			newstr = oldstr = null;
			while(m.find()) {
				if(myvec.indexOf(m.group()) == -1)
					myvec.addElement(m.group());
			     if(newstr == null){
			    	 newstr = m.group();
			     }
			     else{
			    	 oldstr = newstr;
			    	 newstr = m.group();
			    	 if(myptr[myvec.indexOf(oldstr)]
			    		[myvec.indexOf(newstr)] == INF)
			    		 myptr[myvec.indexOf(oldstr)]
						    		[myvec.indexOf(newstr)]  = 1;
			    	 else myptr[myvec.indexOf(oldstr)]
					    		[myvec.indexOf(newstr)]++;

			     }
			}
			for(int i = 0; i < myvec.size(); i++)
				for(int j = 0; j <myvec.size();j++){
					floyd[i][j] = myptr[i][j];
					if(myptr[i][j] < INF){
						path[i][j] = j;
					}
				}
					
			for(int k = 0; k < myvec.size(); k++)
				for(int i = 0; i <myvec.size();i++)
					for(int j = 0; j < myvec.size();j++)
						if(floyd[i][k] + floyd[k][j] < floyd[i][j]){
							floyd[i][j] = floyd[i][k] + floyd[k][j];
							path[i][j] = path[i][k];
						}
		}
	}

	public Set<Integer> BridgeWords(String s1, String s2){
		set1.clear();
		set2.clear();
		if(myvec.indexOf(s1) != -1 && myvec.indexOf(s2) != -1 ){
		for(int i = 0; i < myvec.size();i++){
			if(myptr[myvec.indexOf(s1)][i]!=INF)
				set1.add(i);
			if(myptr[i][myvec.indexOf(s2)]!=INF)
				set2.add(i);
		}
		set1.retainAll(set2);
		}
		return set1;
	}

	public String queryBridgeWords(String s1, String s2){
		outtext = "";
		if(myvec.indexOf(s1) == -1 ||myvec.indexOf(s2) == -1 ){
			return "No "+s1+" or "+s2+" in graph!";
		}
		if(!BridgeWords(s1,s2).isEmpty()){
			System.out.printf("The bridge words from \"%s\" to \"%s\" are:",s1,s2);
			for(int k: BridgeWords(s1,s2)){
				outtext += myvec.get(k)+" ";
			}
			return outtext;
		}
		else{
			return "No bridge words from "+s1+" to "+s2;
		}
	}

	public String generateNewText(String inputText){
		int sub = -1;
		newstr = oldstr = null;
		outtext = "";
		String[] input = inputText.split("\\s+");
		for(String ss: input){
			if(newstr == null){
				outtext += ss + " ";
				newstr = ss.toLowerCase();
			}
			else{
				oldstr = newstr;
				newstr = ss.toLowerCase();
				if(myvec.indexOf(oldstr) != -1 && myvec.indexOf(newstr) != -1){
					if(!BridgeWords(oldstr, newstr).isEmpty()){
						for(int k : BridgeWords(oldstr, newstr)){
							sub = k;
						}
						outtext += myvec.get(sub) + " ";
					}
				}
				outtext += ss + " ";
			}
		}
		return outtext;
	}
	public Vector<String> pathcollect(String s1, String s2){
		Vector<String> spath = new Vector<String>();
		if(myvec.indexOf(s1)==-1||myvec.indexOf(s2)==-1||path[myvec.indexOf(s1)][myvec.indexOf(s2)] == -1)
			return spath;
		spath.addElement(s1);
		String u = s1;
	    while( !u.equals(s2)){
		   u = myvec.get(path[myvec.indexOf(u)][myvec.indexOf(s2)]);
		 spath.addElement(u);
	   }
	   return spath;
	}
	public String calcShortestPath(String s1, String s2){
		outtext = "";
		if(myvec.indexOf(s1) == -1 || myvec.indexOf(s2) == -1)
			return outtext = s1 + " or "+s2+" not in grapg!";
		else if(floyd[myvec.indexOf(s1)][myvec.indexOf(s2)] >= INF)
			return outtext = s1 + " can not reach "+s2;
		Vector<String> sspath = pathcollect(s1, s2);
		if(sspath.size()>0){
			outtext += sspath.get(0);
			for(int i = 1; i<sspath.size();i++){
				outtext += "->"+ sspath.get(i);
			}
		}
		return outtext;
	}

	public String randomWalk(){
		outtext = "";
		element.clear();
		boolean existout = true;
		for(int i = 0; i < myvec.size(); i++)
			for(int j = 0; j < myvec.size(); j++)
				this.isvisited[i][j] = false;
		Random rand = new Random();
		int sub = rand.nextInt(myvec.size())  ;
		int sub2 = -1;
		while(true){
			existout =false;
			for(int i = 0; i < myvec.size(); i++){
				if(myptr[sub][i] != INF && !this.isvisited[sub][i]){
					existout = true;
					break;
				}
			}
			if(existout){
				while(myptr[sub][sub2 = rand.nextInt(myvec.size())] == INF && !this.isvisited[sub][sub2]);
				if(!this.isvisited[sub][sub2] ){
					this.isvisited[sub][sub2] = true;
					outtext += myvec.get(sub)+" ";
					element.addElement(myvec.get(sub));
					sub = sub2;
				}
				else break;
			}
			else break;
		}
		element.addElement(myvec.get(sub2));
		return outtext += myvec.get(sub2);
	}
}



