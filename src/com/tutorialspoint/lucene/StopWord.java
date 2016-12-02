package com.tutorialspoint.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWord {
	
	private Set<String> stopword = new HashSet<String>();
	
	public StopWord(String path) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(path));
		String read;
		while ((read=in.readLine()) != null)
		{
			stopword.add(read);
		}
		in.close();
	}

	/**
	 * @return the stopword
	 */
	public Set<String> getStopword() {
		return this.stopword;
	}
	
	

}
