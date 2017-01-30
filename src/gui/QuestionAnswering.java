package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatter;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

import com.opencsv.CSVWriter;
import com.tutorialspoint.lucene.LuceneConstants;
import com.tutorialspoint.lucene.LuceneTester;
import com.tutorialspoint.lucene.StopWord;

import answerProcessing.AnswerExtraction;
import answerProcessing.AnswerModel;
import core.MRRImprover;
import core.Query;
import core.Utils;
import evaluation.MeanReciprocalRank;
import evaluation.PrecisionAndRecall;
import passage.PassageModel;
import passage.PassageRanking;
import passage.PassageRetrieval;
import passage.QueryFunction;
import rocchio.RocchioExpander;
import suggestion.SuggestionSearch;

import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import java.awt.Font;
import javax.swing.SwingConstants;

public class QuestionAnswering
{

	private JFrame frame;
	private JTextField textFieldPertanyaan;
	private JTextField textFieldJawaban;
	private AnswerModel jawaban;
	private static JButton btnGetAnswer;
	private static boolean finished;
	private JTextField textField;
	private String filePath;
	private JTextField textField_1;
	private String retrievedDocumentPath;
	private JTextField textField_2;
	private JTextField textField_3;
	private String csvAnswerPath;
	private JTextField alphaTextField;
	private JTextField betaTextField;
	private JTextField gammaTextField;
	private JTextField termLimitTextField;
	private boolean sentenceBased=true;
	private boolean relevanceFeedback=true;
	private JSpinner answerSpinner;
	private JSpinner passageSpinner;
	private JSpinner spinnerPercobaan;
	private JTextField textField_5;
	private String csvDocumentPathFolder;
	private JTextField docLimitTextField;
	
	class runQuestionAnswering extends SwingWorker<Integer,Integer>
	{
		protected Integer doInBackground() throws Exception
	    {
			LuceneTester tester = new LuceneTester();
			jawaban = tester.runSearcher(textFieldPertanyaan.getText(),false,true,true).get(0);
			
	        return 42;
	    }

	    protected void done()
	    {
	    	textFieldJawaban.setText(jawaban.getAnswer());
	    	btnGetAnswer.setEnabled(true);
	    	
	    }
	}
	
	class calculateMeanReciprocalRank extends SwingWorker<Integer,Integer>
	{
		protected Integer doInBackground() throws Exception
	    {
			MRRImprover im = new MRRImprover("improveList.txt");
			ArrayList<String> list = im.getImprovedQuery();
			File dir = new File (csvAnswerPath);
			File[] dirList = Utils.sortFiles(dir.listFiles());
			List<String[]> dataset = new ArrayList<String[]>();
			dataset.add( new String[]{"file name","Mean Reciprocal Rank"});
			for (File file:dirList)
			{
				if (!file.getName().equals("allMRR.csv") && file.isFile())
				{
					MeanReciprocalRank mrr = new MeanReciprocalRank(file.getAbsolutePath()/*,list*/);
					double hasil = mrr.calculateMRR();
					//long persen = Math.round(Math.ceil(hasil * 100));
					long persen = Math.round(hasil*100);
					String per = persen+"%";
					dataset.add(new String[]{file.getName(),per});
				}
				
			}
			String savePath = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\hasil bersih\\listRetrievedAnswer\\allMRR.csv";
			CSVWriter write = new CSVWriter (new FileWriter (savePath));
			write.writeAll(dataset);
			write.close();
	        return 42;
	    }

	    protected void done()
	    {
	    	JOptionPane.showMessageDialog(null, "penghitungan selesai", "selesai", JOptionPane.INFORMATION_MESSAGE);
	    	
	    }
	}
	
	class calculatePrecisionAndRecall extends SwingWorker<Integer,Integer>
	{
		protected Integer doInBackground() throws Exception
	    {
			File dir = new File (retrievedDocumentPath);
			File[] dirList = dir.listFiles();
			ArrayList<Double> averagePrecision = new ArrayList<Double>();
			for (File file:dirList)
			{
				if (file.isFile())
				{
					String question = file.getName();
					PrecisionAndRecall par = new PrecisionAndRecall(file.getAbsolutePath());
					double[] precision = par.calculatePrecision();
					double[] recall = par.calculateRecall();
					double avgPrecision = par.calculateAvaragePrecision(precision);
					averagePrecision.add(avgPrecision);
					par.savePrecisionAndRecall(Utils.savePrecisionAndRecallResultPath, file.getAbsolutePath(), question, precision, recall, avgPrecision);
				}
			}
			double meanAveragePrecision = PrecisionAndRecall.calculateMeanAveragePrecision (averagePrecision);
			long percent = Math.round(meanAveragePrecision * 100);
			textField_2.setText(percent+"%");
			
	        return 42;
	    }

	    protected void done()
	    {
	    	JOptionPane.showMessageDialog(null, "penghitungan selesai", "selesai", JOptionPane.INFORMATION_MESSAGE);
	    	
	    }
	}
	
	class calculatePrecisionAndRecallBatch extends SwingWorker<Integer,Integer>
	{
		protected Integer doInBackground() throws Exception
	    {
			MRRImprover im = new MRRImprover("improveList.txt");
			ArrayList<String> list = im.getImprovedQuery();
			for (int i=0;i<list.size();i++)
			{
				String fix = list.get(i).replaceAll("\\p{Punct}","").toLowerCase().trim();
				list.set(i, fix);
			}
			File root = new File (csvDocumentPathFolder);
			File[] rootList = Utils.sortDir(root.listFiles());
			for (int i=0;i<rootList.length;i++)
			{
				System.out.println(rootList[i]);
			}
			List<String[]> meanAveragePrecision = new ArrayList<String[]>();
			meanAveragePrecision.add(new String[]{"Percobaan ke","Mean Average Precision"});
			int i=0;
			for (File dir:rootList)
			{
				i++;
				if (dir.isDirectory())
				{
					File[] dirList = dir.listFiles();
					ArrayList<Double> averagePrecision = new ArrayList<Double>();
					for (File file:dirList)
					{
						if (file.isFile())
						{
							String question = file.getName();
							PrecisionAndRecall par = new PrecisionAndRecall(file.getAbsolutePath());
							double[] precision = par.calculatePrecision();
							double[] recall = par.calculateRecall();
							double avgPrecision = par.calculateAvaragePrecision(precision);
							String temp = question.replace(".csv", "").toLowerCase().trim();
							if (!list.contains(temp))
							{
								averagePrecision.add(avgPrecision);
							}
							String savePath = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\listPrecisionAndRecallResult\\percobaan "+i+"\\";
							par.savePrecisionAndRecall(savePath, file.getAbsolutePath(), question, precision, recall, avgPrecision);
						}
					}
					System.out.println(averagePrecision.size());
					double te = PrecisionAndRecall.calculateMeanAveragePrecision (averagePrecision);
					long percent = Math.round(te * 100);
					String per = percent+"%";
					meanAveragePrecision.add(new String[]{dir.getName(),per});
				}
				
			}
			
			String savePath = "C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\hasil bersih\\listPrecisionAndRecallResult\\allMAP.csv";
			CSVWriter write = new CSVWriter (new FileWriter (savePath));
			write.writeAll(meanAveragePrecision);
			write.close();
			
	        return 42;
	    }

	    protected void done()
	    {
	    	JOptionPane.showMessageDialog(null, "penghitungan selesai", "selesai", JOptionPane.INFORMATION_MESSAGE);
	    	
	    }
	}
	
	class runCreateCSV extends SwingWorker<Integer,Integer>
	{
		protected Integer doInBackground() throws Exception
	    {
			String answerPath = Utils.saveRetrievedAnswerPath+"percobaan_"+Utils.percobaanKe+".csv";
			BufferedWriter writeRetrievedAnswer =  new BufferedWriter (new OutputStreamWriter(new FileOutputStream(answerPath),"UTF-8"));
			int urutan=0;
			writeRetrievedAnswer.write("urutan,pertanyaan,jawaban,verdict\n");
			LuceneTester tester = new LuceneTester();
			File listQuestion = new File (filePath);
			BufferedReader bf = null;
			try {
				bf = new BufferedReader (new FileReader (listQuestion));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<String> queryList = new ArrayList<String>();
			  String querys = "";
			  try {
				while ((querys = bf.readLine())!=null)
				  {
					queryList.add(querys);
				  }
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  System.out.println(queryList.size());
			  ProgressMonitor1 pbar = new ProgressMonitor1(queryList.size(),"processing query...");
			  int in = 0;
			for (String query : queryList)
			{
				in++;
				String temp = query;
				temp = temp.replaceAll("\\p{Punct}", "");
				String path = Utils.saveRetrievedDocumentPath+temp+".csv";
				boolean createIndex = false;
				if (in==1)
				{
					createIndex = false;
				}
				ArrayList<AnswerModel>listJawaban =  tester.runSearcherWithDebug(query, path, relevanceFeedback ,sentenceBased,createIndex);
				for (int i=0;i<Utils.topNAnswer;i++)
				{
					if (!listJawaban.isEmpty())
					{
						if (i<listJawaban.size())
						{
							urutan++;
							writeRetrievedAnswer.write(urutan+","+query+","+listJawaban.get(i).getAnswer()+",\n");
						}
						else
						{
							urutan++;
							writeRetrievedAnswer.write(urutan+","+query+",NO ANSWER,WA\n");
						}
					}
					else
					{
						urutan++;
						writeRetrievedAnswer.write(urutan+","+query+",NO ANSWER,WA\n");
					}
					
				}
				pbar.counter= in;
			}
			writeRetrievedAnswer.close();
	        return 42;
	    }

	    protected void done()
	    {
	    	spinnerPercobaan.setValue(spinnerPercobaan.getNextValue());
	    	JOptionPane.showMessageDialog(null, "csv berhasil dibuat", "notice", JOptionPane.INFORMATION_MESSAGE);
	    	
	    }
	}
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					QuestionAnswering window = new QuestionAnswering();
					window.frame.setVisible(true);
					window.frame.getRootPane().setDefaultButton(btnGetAnswer);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public QuestionAnswering()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 577, 299);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(null);
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		
		textFieldPertanyaan = new JTextField();
		textFieldPertanyaan.setBounds(19, 48, 504, 28);
		panel1.add(textFieldPertanyaan);
		textFieldPertanyaan.setColumns(10);
		
		JLabel lblInputPertanyaan = new JLabel("Input Pertanyaan:");
		lblInputPertanyaan.setBounds(19, 25, 105, 16);
		panel1.add(lblInputPertanyaan);
		
		btnGetAnswer = new JButton("Get Answer");
		btnGetAnswer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnGetAnswer.setEnabled(false);
				new runQuestionAnswering().execute();
			}
		});
		btnGetAnswer.setBounds(216, 89, 123, 28);
		panel1.add(btnGetAnswer);
		
		textFieldJawaban = new JTextField();
		textFieldJawaban.setEditable(false);
		textFieldJawaban.setBounds(19, 145, 504, 28);
		panel1.add(textFieldJawaban);
		textFieldJawaban.setColumns(10);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 533, 246);
		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		panel2.setLayout(null);
		panel2.setLayout(null);
		tabbedPane.add("run evaluation question", panel2);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(22, 28, 403, 28);
		panel2.add(textField);
		textField.setColumns(10);
		
		JButton btnCreateCsv = new JButton("create csv");
		btnCreateCsv.setEnabled(false);
		btnCreateCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("tombol ditekan");
				if (relevanceFeedback)
				{
					Utils.alpha = Double.parseDouble(alphaTextField.getText());
					Utils.beta = Double.parseDouble(betaTextField.getText());
					Utils.gamma = Double.parseDouble(gammaTextField.getText());
					 
					Utils.termLimit = Integer.parseInt(termLimitTextField.getText());
				}
				Utils.documentLimit = Integer.parseInt(docLimitTextField.getText());
				Utils.topNPassage = Integer.parseInt(passageSpinner.getValue().toString());
				Utils.topNAnswer = Integer.parseInt(answerSpinner.getValue().toString());
				Utils.percobaanKe = Integer.parseInt(spinnerPercobaan.getValue().toString());
				Utils.changePath(Integer.parseInt(spinnerPercobaan.getValue().toString()));
				System.out.println(Utils.saveRetrievedDocumentPath);
				Utils.folderPreparation();
				new runCreateCSV().execute();
			}
		});
		btnCreateCsv.setBounds(425, 97, 90, 28);
		panel2.add(btnCreateCsv);
		
		JButton btnBrowse = new JButton("browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser j = new JFileChooser();
				j.setCurrentDirectory(new java.io.File("."));
				j.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter ("TXT Files","txt");
				j.setFileFilter(filter);
				j.setAcceptAllFileFilterUsed(false);
				int rVal = j.showOpenDialog(null);
		        if (rVal == JFileChooser.APPROVE_OPTION) {
		          textField.setText(j.getSelectedFile().toString());
		          filePath = j.getSelectedFile().toString();
		          btnCreateCsv.setEnabled(true);
		        }
			}
		});
		btnBrowse.setBounds(425, 28, 90, 28);
		panel2.add(btnBrowse);
		
		JLabel lblInputQuestion = new JLabel("input question file");
		lblInputQuestion.setBounds(22, 6, 108, 16);
		panel2.add(lblInputQuestion);
		
		JToggleButton tglbtnSentenceBased = new JToggleButton("sentence based");
		tglbtnSentenceBased.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                if (selected)
                {
                	sentenceBased = true;
                }
                else
                {
                	sentenceBased = false;
                }
			}
		});
		tglbtnSentenceBased.setSelected(true);
		tglbtnSentenceBased.setBounds(22, 63, 117, 28);
		panel2.add(tglbtnSentenceBased);
		
		JToggleButton tglbtnRelevanceFeedback = new JToggleButton("Relevance Feedback");
		tglbtnRelevanceFeedback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                if (selected)
                {
                	relevanceFeedback = true;
                	alphaTextField.setEnabled(true);
                	betaTextField.setEnabled(true);
                	gammaTextField.setEnabled(true);
                	termLimitTextField.setEnabled(true);
                	alphaTextField.setText("1");
                	betaTextField.setText("0.5");
                	gammaTextField.setText("0");
                	termLimitTextField.setText("10");
                }
                else
                {
                	relevanceFeedback = false;
                	alphaTextField.setText("");
                	betaTextField.setText("");
                	gammaTextField.setText("");
                	termLimitTextField.setText("");
                	alphaTextField.setEnabled(false);
                	betaTextField.setEnabled(false);
                	gammaTextField.setEnabled(false);
                	termLimitTextField.setEnabled(false);
                }
			}
		});
		tglbtnRelevanceFeedback.setSelected(true);
		tglbtnRelevanceFeedback.setBounds(22, 103, 143, 28);
		panel2.add(tglbtnRelevanceFeedback);
		
		JLabel lblTopNPassage = new JLabel("Top N Passage");
		lblTopNPassage.setBounds(199, 69, 90, 16);
		panel2.add(lblTopNPassage);
		
		SpinnerModel passageSpinnerModel = new SpinnerNumberModel(10,5,20,5);
		passageSpinner = new JSpinner(passageSpinnerModel);
		passageSpinner.setBounds(301, 63, 55, 28);
		panel2.add(passageSpinner);
		
		JLabel lblTopNAnswer = new JLabel("Top N Answer");
		lblTopNAnswer.setBounds(199, 109, 90, 16);
		panel2.add(lblTopNAnswer);
		
		SpinnerModel answerSpinnerModel = new SpinnerNumberModel (5,1,20,1);
		answerSpinner = new JSpinner(answerSpinnerModel);
		answerSpinner.setBounds(301, 103, 55, 28);
		panel2.add(answerSpinner);
		
		JLabel lblAlpha = new JLabel("Alpha");
		lblAlpha.setBounds(22, 143, 39, 16);
		panel2.add(lblAlpha);
		
		JLabel lblBeta = new JLabel("Beta");
		lblBeta.setBounds(84, 143, 30, 16);
		panel2.add(lblBeta);
		
		JLabel lblGamma = new JLabel("Gamma");
		lblGamma.setBounds(126, 143, 55, 16);
		panel2.add(lblGamma);
		
		JLabel lblTermLimit = new JLabel("Term Limit");
		lblTermLimit.setBounds(177, 144, 69, 16);
		panel2.add(lblTermLimit);
		
		alphaTextField = new JTextField();
		alphaTextField.setText("1");
		alphaTextField.setFont(new Font("SansSerif", Font.PLAIN, 10));
		alphaTextField.setBounds(17, 161, 39, 28);
		panel2.add(alphaTextField);
		alphaTextField.setColumns(10);
		
		betaTextField = new JTextField();
		betaTextField.setText("0.5");
		betaTextField.setFont(new Font("SansSerif", Font.PLAIN, 10));
		betaTextField.setColumns(10);
		betaTextField.setBounds(75, 160, 39, 28);
		panel2.add(betaTextField);
		
		gammaTextField = new JTextField();
		gammaTextField.setText("0");
		gammaTextField.setFont(new Font("SansSerif", Font.PLAIN, 10));
		gammaTextField.setColumns(10);
		gammaTextField.setBounds(126, 161, 39, 28);
		panel2.add(gammaTextField);
		
		termLimitTextField = new JTextField();
		termLimitTextField.setText("10");
		termLimitTextField.setFont(new Font("SansSerif", Font.PLAIN, 10));
		termLimitTextField.setColumns(10);
		termLimitTextField.setBounds(187, 161, 39, 28);
		panel2.add(termLimitTextField);
		
		JButton btnCheck = new JButton("check");
		btnCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "alpha = "+alphaTextField.getText()+"\nrelevanceFeedback = "+relevanceFeedback+"\nSentenceBased = "+sentenceBased,"check",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnCheck.setBounds(404, 137, 90, 28);
		btnCheck.setVisible(false);
		panel2.add(btnCheck);
		
		JLabel lblPercobaanKe = new JLabel("Percobaan ke");
		lblPercobaanKe.setBounds(370, 68, 76, 16);
		panel2.add(lblPercobaanKe);
		
		SpinnerModel percobaanModel = new SpinnerNumberModel (1,1,1000,1);
		spinnerPercobaan = new JSpinner(percobaanModel);
		spinnerPercobaan.setBounds(458, 63, 57, 28);
		panel2.add(spinnerPercobaan);
		
		JLabel lblTopNDokumen = new JLabel("Top N Dokumen");
		lblTopNDokumen.setBounds(268, 143, 98, 16);
		panel2.add(lblTopNDokumen);
		
		docLimitTextField = new JTextField();
		docLimitTextField.setText("10");
		docLimitTextField.setFont(new Font("SansSerif", Font.PLAIN, 10));
		docLimitTextField.setColumns(10);
		docLimitTextField.setBounds(295, 161, 39, 28);
		panel2.add(docLimitTextField);
		tabbedPane.add("input pertanyaan manual", panel1);
		tabbedPane.add("Precision and Recall",panel3);
		panel3.setLayout(null);
		
		JLabel lblRetrievedDocumentFolder = new JLabel("Retrieved Document Folder");
		lblRetrievedDocumentFolder.setBounds(6, 6, 201, 16);
		panel3.add(lblRetrievedDocumentFolder);
		
		textField_1 = new JTextField();
		textField_1.setBounds(6, 34, 431, 28);
		panel3.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnBrowse_1 = new JButton("Browse");
		btnBrowse_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser j = new JFileChooser();
				j.setCurrentDirectory(new java.io.File("."));
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				j.setAcceptAllFileFilterUsed(false);
				 
		        int rVal = j.showOpenDialog(null);
		        if (rVal == JFileChooser.APPROVE_OPTION) {
		          textField_1.setText(j.getSelectedFile().toString());
		          retrievedDocumentPath = j.getSelectedFile().toString();
		        }
			}
		});
		btnBrowse_1.setBounds(443, 34, 90, 28);
		panel3.add(btnBrowse_1);
		
		JButton btnCalculatePrecisionAnd = new JButton("Calculate Precision And Recall");
		btnCalculatePrecisionAnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField_2.setText("");
				new calculatePrecisionAndRecall().execute();
			}
		});
		btnCalculatePrecisionAnd.setBounds(198, 74, 212, 28);
		panel3.add(btnCalculatePrecisionAnd);
		
		JLabel lblMeanAveragePrecision = new JLabel("Mean Average Precision:");
		lblMeanAveragePrecision.setBounds(6, 117, 150, 16);
		panel3.add(lblMeanAveragePrecision);
		
		textField_2 = new JTextField();
		textField_2.setEditable(false);
		textField_2.setBounds(145, 114, 73, 28);
		panel3.add(textField_2);
		textField_2.setColumns(10);
		
		JLabel lbluntukPrecisionRecall = new JLabel("*untuk precision, recall dan average precision harap lihat di folder:");
		lbluntukPrecisionRecall.setBounds(6, 145, 403, 16);
		panel3.add(lbluntukPrecisionRecall);
		
		JLabel lblSaveLocation = new JLabel("<html>C:\\Users\\hobert\\workspace\\tesLuceneLowerVersion1\\debug question answering\\listPrecisionAndRecallResult\\percobaan 100\\</html>");
		lblSaveLocation.setVerticalAlignment(SwingConstants.TOP);
		lblSaveLocation.setBounds(6, 163, 529, 32);
		panel3.add(lblSaveLocation);
		
		JLabel lblNomorPercobaan = new JLabel("Nomor Percobaan");
		lblNomorPercobaan.setBounds(6, 80, 107, 16);
		panel3.add(lblNomorPercobaan);
		
		SpinnerModel percobaanModel1 = new SpinnerNumberModel (Utils.percobaanKe,1,1000,1);
		JSpinner spinnerPercobaanPrecisionAndRecall = new JSpinner(percobaanModel1);
		JComponent comp = spinnerPercobaanPrecisionAndRecall.getEditor();
	    JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
	    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
	    formatter.setCommitsOnValidEdit(true);
	    spinnerPercobaanPrecisionAndRecall.addChangeListener(new ChangeListener() {

	        @Override
	        public void stateChanged(ChangeEvent e) {
	            //LOG.info("value changed: " + spinnerPercobaanPrecisionAndRecall.getValue());
	        	Utils.changePath((Integer)spinnerPercobaanPrecisionAndRecall.getValue());
	        	lblSaveLocation.setText("<html>"+Utils.savePrecisionAndRecallResultPath+"<html>");
	        	
	        }
	    });
		spinnerPercobaanPrecisionAndRecall.setBounds(125, 74, 61, 28);
		panel3.add(spinnerPercobaanPrecisionAndRecall);
		tabbedPane.add("Mean Reciprocal Rank",panel4);
		panel4.setLayout(null);
		
		JLabel lblRetrievedAnswerCsv = new JLabel("Retrieved Answer CSV");
		lblRetrievedAnswerCsv.setBounds(6, 6, 155, 16);
		panel4.add(lblRetrievedAnswerCsv);
		
		textField_3 = new JTextField();
		textField_3.setBounds(6, 28, 411, 28);
		panel4.add(textField_3);
		textField_3.setColumns(10);
		
		JButton btnBrowse_2 = new JButton("Browse");
		btnBrowse_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser j = new JFileChooser();
				j.setCurrentDirectory(new java.io.File("."));
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				j.setAcceptAllFileFilterUsed(false);
				 
		        int rVal = j.showOpenDialog(null);
		        if (rVal == JFileChooser.APPROVE_OPTION) {
		          textField_3.setText(j.getSelectedFile().toString());
		          csvAnswerPath = j.getSelectedFile().toString();
		        }
			}
		});
		btnBrowse_2.setBounds(429, 28, 90, 28);
		panel4.add(btnBrowse_2);
		
		JButton btnCalculateMeanReciprocal = new JButton("Calculate Mean Reciprocal Rank");
		btnCalculateMeanReciprocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//textField_4.setText("");
				new calculateMeanReciprocalRank().execute();
			}
		});
		btnCalculateMeanReciprocal.setBounds(131, 68, 257, 28);
		panel4.add(btnCalculateMeanReciprocal);
		frame.getContentPane().add(tabbedPane);
		
		JPanel panel5 = new JPanel();
		panel5.setLayout(null);
		
		tabbedPane.add("precision and recall Batch", panel5);
		
		JLabel lblRetrievedDocumentPath = new JLabel("Retrieved document path");
		lblRetrievedDocumentPath.setBounds(6, 6, 156, 16);
		panel5.add(lblRetrievedDocumentPath);
		
		textField_5 = new JTextField();
		textField_5.setEditable(false);
		textField_5.setBounds(6, 28, 419, 28);
		panel5.add(textField_5);
		textField_5.setColumns(10);
		
		JButton btnBrowse_3 = new JButton("Browse");
		btnBrowse_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser j = new JFileChooser();
				j.setCurrentDirectory(new java.io.File("."));
				j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				j.setAcceptAllFileFilterUsed(false);
				 
		        int rVal = j.showOpenDialog(null);
		        if (rVal == JFileChooser.APPROVE_OPTION) {
		          textField_5.setText(j.getSelectedFile().toString());
		          csvDocumentPathFolder = j.getSelectedFile().toString();
		        }
			}
		});
		btnBrowse_3.setBounds(437, 28, 90, 28);
		panel5.add(btnBrowse_3);
		
		JButton btnRunBatch = new JButton("Run batch");
		btnRunBatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new calculatePrecisionAndRecallBatch().execute();
			}
		});
		btnRunBatch.setBounds(220, 80, 90, 28);
		panel5.add(btnRunBatch);
	}
}
