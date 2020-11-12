package com.bjsasc.FTPClientSDK.service;

public class T {

	public static void main(String[] args)  {
		try {
			grade(0);
		}catch (Exception e){

		}

	}
	public static void grade(int i) throws Exception {
		for ( ; i <= 100; i++) {
			System.out.print("\r");
			System.out.print("[");
			int j = 0;
			for(; j<=i; j++) {
				if(j % 5 == 0)
					System.out.print("=");
			}
			for(; j<=100; j++) {
				if(j % 5 == 0)
					System.out.print(" ");
			}
			System.out.print("]");
			System.out.print("\t"+i+"%\t"+i+"%\t"+i+"%\t");
			Thread.sleep(100);
		}
	}


}
