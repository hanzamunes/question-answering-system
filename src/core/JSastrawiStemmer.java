package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import jsastrawi.cli.LemmatizeCmd;
import jsastrawi.morphology.DefaultLemmatizer;
import jsastrawi.morphology.Lemmatizer;

public class JSastrawiStemmer
{
	private Set<String> dictionary;
	DefaultLemmatizer lemmatizer;
	
	public JSastrawiStemmer()
	{
		// Mulai setup JSastrawi, cukup dijalankan 1 kali

		// JSastrawi lemmatizer membutuhkan kamus kata dasar
		// dalam bentuk Set<String>
		dictionary = new HashSet<String>();

		// Memuat file kata dasar dari distribusi JSastrawi
		// Jika perlu, anda dapat mengganti file ini dengan kamus anda sendiri
		InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		try
		{
			while ((line = br.readLine()) != null) {
			    dictionary.add(line);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lemmatizer = new DefaultLemmatizer(this.dictionary);
	}
	
	
	
	/**
	 * @return the dictionary
	 */
	public Set<String> getDictionary()
	{
		return dictionary;
	}



	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(Set<String> dictionary)
	{
		this.dictionary = dictionary;
	}
	
	public String stem (String token)
	{
		return lemmatizer.lemmatize(token);
	}



	public static void main (String[] Args)
	{
		JSastrawiStemmer stemmer = new JSastrawiStemmer();
		DefaultLemmatizer lemmatizer = new DefaultLemmatizer(stemmer.getDictionary());
		// Selesai setup JSastrawi
		// lemmatizer bisa digunakan berkali-kali

		System.out.println(lemmatizer.lemmatize("menculik dan memotong"));
		System.out.println(lemmatizer.lemmatize("menyontek"));
		System.out.println(lemmatizer.lemmatize("memotong"));
		
	}

}
