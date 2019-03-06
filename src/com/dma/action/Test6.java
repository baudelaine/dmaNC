package com.dma.action;

import java.util.Arrays;
import java.util.List;

public class Test6 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<String> l = Arrays.asList("AR, FR, DE".replaceAll("\\s", "").split(","));
		for(String s: l) {
			System.out.println(s);
		}
		
	}

}
