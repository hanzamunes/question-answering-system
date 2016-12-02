package prIR;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import answerProcessing.Util;
import core.Utils;

public class PRIR
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		String passage = "Terlepas dari segala kekurangannya, Presiden Hamid Karzai yang dipilih melalui pemilu Oktober 2004 mengklaim pemilu parlemen 2005 merupakan tonggak sejarah Afganistan. Setelah 30 tahun berperang, (mengalami) intervensi dan kesengsaraan, hari ini rakyat Afghan bergerak maju. Kami sedang membuat sejarah, tegas Karzaidi ibu kota Kabul. Menteri Luar Negeri Afganistan Abdullah Abdullah juga menegaskan bahwa perubahan sedang berlangsung di negara yang sempat dikuasai rezim Taliban hingga 2001. Perubahan yang dia maksud terutama di bidang pendidikan dan hak perempuan.";
		Tokenizer source = new StandardTokenizer(Version.LUCENE_36,new StringReader(passage));
		TokenStream result = new StandardFilter (Version.LUCENE_36,source);
		result = new LowerCaseFilter (Version.LUCENE_36,result);
		IndonesianAnalyzer analyze = new IndonesianAnalyzer (Version.LUCENE_36);
		result = new StopFilter (Version.LUCENE_36,result,analyze.getStopwordSet());
		String hasil = "";
		try
		{
			while (result.incrementToken())
			{
				String tok = result.getAttribute(CharTermAttribute.class).toString();
				tok = Util.stem(tok);
				hasil = hasil + tok+" ";
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hasil = hasil.trim();
		System.out.println(hasil);
	}

}
