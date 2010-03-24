package org.aitools.programd.lsa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * TODO javadoc
 * 
 * @author Mario Scriminaci
 *
 */
public class OctaveSupport {
	
	public static boolean exec(String exec){
		boolean ret = false;

		Runtime r= Runtime.getRuntime();
		try{
			System.out.println("octave --eval \""+exec+"\"");
			Process child = r.exec("octave --eval "+exec+"");
			
			InputStream stream = child.getInputStream();
			
			InputStream estream = child.getErrorStream();

			InputStreamReader inputStreamReader = new InputStreamReader(stream);
			InputStreamReader einputStreamReader = new InputStreamReader(estream);
			
			while(einputStreamReader.ready()){
				System.out.print(einputStreamReader.read());
			}

			ArrayList<String> list = new ArrayList<String>(); 
			InputStream istr = child.getErrorStream(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(istr));
			String str;
			
			while ((str = br.readLine()) != null) list.add(str);

			int n = -1;
			n = child.waitFor();

			br.close();

			for(String s:list){
				System.out.println(s);
			}

			if(n==0){
				return true;
			}
			else{
				return false;
			}	
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static boolean testOctave(){
		Runtime r= Runtime.getRuntime();
		try {
			Process child = r.exec("octave -x -v");

			InputStream stream = child.getInputStream();

			InputStreamReader inputStreamReader = new InputStreamReader(stream);

			while(inputStreamReader.ready()){
				System.out.print(inputStreamReader.read());
			}

			ArrayList<String> list = new ArrayList<String>(); 
			InputStream istr = child.getInputStream(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(istr));
			String str;
			
			while ((str = br.readLine()) != null) list.add(str);

			int n = -1;
			n = child.waitFor();

			br.close();

			for(String s:list){
				System.out.println(s);
			}

			if(n==0){
				return true;
			}
			else{
				return false;
			}
		} catch (IOException e) {
			System.err.println("Octave not installed");
			return false;
		} catch (InterruptedException e) {
			System.err.println("Octave is not working");
			return false;
		}		
	}	

}
