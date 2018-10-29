package com.dma.web;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class Test0 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Path dir = Paths.get("/root/e");
		
		Set<PosixFilePermission> perms = new HashSet<>();
	    perms.add(PosixFilePermission.OWNER_READ);
	    perms.add(PosixFilePermission.OWNER_WRITE);
	    perms.add(PosixFilePermission.OWNER_EXECUTE);

	    perms.add(PosixFilePermission.OTHERS_READ);
	    perms.add(PosixFilePermission.OTHERS_WRITE);
	    perms.add(PosixFilePermission.OTHERS_EXECUTE);

	    perms.add(PosixFilePermission.GROUP_READ);
	    perms.add(PosixFilePermission.GROUP_WRITE);
	    perms.add(PosixFilePermission.GROUP_EXECUTE);		
	    
		
		try {

		    Files.createDirectories(dir);
		    Files.setPosixFilePermissions(dir, perms);
		    
		    perms.remove(PosixFilePermission.OWNER_EXECUTE);
		    perms.remove(PosixFilePermission.GROUP_EXECUTE);
		    perms.remove(PosixFilePermission.OTHERS_EXECUTE);
		    
		    Path f0 = Paths.get(dir.toString() + "/a");
		    Files.createFile(f0);

		    Path f1 = Paths.get(dir.toString() + "/b");
		    Files.createFile(f1);

		    Path f2 = Paths.get(dir.toString() + "/c");
		    Files.createFile(f2);
		    
			DirectoryStream<Path> ds = Files.newDirectoryStream(dir);
			for(Path path: ds){
				System.out.println(path.toString());
				Files.setPosixFilePermissions(path, perms);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
