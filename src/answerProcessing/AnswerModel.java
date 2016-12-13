package answerProcessing;

import java.io.Serializable;
import java.util.Map;

public class AnswerModel implements Serializable {
	
	private String answer;
	private int frequency;
	private int distance;
	private int passageRank;
	private int documentRank;
	private int processingOrder;
	private String answerDocumentTitle;
	private String answerDocumentPath;
	private int totalScore;
	
	
	
	
	/**
	 * @return the passageRank
	 */
	public int getPassageRank()
	{
		return passageRank;
	}

	/**
	 * @param passageRank the passageRank to set
	 */
	public void setPassageRank(int passageRank)
	{
		this.passageRank = passageRank;
		this.totalScore = this.totalScore + this.passageRank;
	}

	/**
	 * @return the answerDocumentTitle
	 */
	public String getAnswerDocumentTitle() {
		return answerDocumentTitle;
	}

	/**
	 * @param answerDocumentTitle the answerDocumentTitle to set
	 */
	public void setAnswerDocumentTitle(String answerDocumentTitle) {
		this.answerDocumentTitle = answerDocumentTitle;
	}

	/**
	 * @return the answerDocumentPath
	 */
	public String getAnswerDocumentPath() {
		return answerDocumentPath;
	}

	/**
	 * @param answerDocumentPath the answerDocumentPath to set
	 */
	public void setAnswerDocumentPath(String answerDocumentPath) {
		this.answerDocumentPath = answerDocumentPath;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * initialize all variable
	 */
	public AnswerModel()
	{
		this.frequency = 0;
		this.distance = Integer.MAX_VALUE;
		this.documentRank = Integer.MAX_VALUE;
		this.passageRank = Integer.MAX_VALUE;
		this.processingOrder = Integer.MAX_VALUE;
		this.totalScore = 0;
	}
	
	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
		//this.totalScore = this.totalScore - this.frequency;
	}
	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
		this.totalScore = this.totalScore + this.distance;
	}
	
	/**
	 * @return the documentRank
	 */
	public int getDocumentRank() {
		return documentRank;
	}
	/**
	 * @param documentRank the documentRank to set
	 */
	public void setDocumentRank(int documentRank) {
		this.documentRank = documentRank;
		this.totalScore = this.totalScore + this.documentRank;
	}
	/**
	 * @return the processingOrder
	 */
	public int getProcessingOrder() {
		return processingOrder;
	}
	/**
	 * @param processingOrder the processingOrder to set
	 */
	public void setProcessingOrder(int processingOrder) {
		this.processingOrder = processingOrder;
	}

	/**
	 * @return the totalScore
	 */
	public int getTotalScore()
	{
		return totalScore;
	}
	
	

}
