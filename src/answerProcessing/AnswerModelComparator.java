package answerProcessing;

import java.util.Comparator;


public enum AnswerModelComparator implements Comparator<AnswerModel> {
	TOTAL_SCORE_SORT{
		public int compare(AnswerModel o1, AnswerModel o2) {
        	return Integer.valueOf(o1.getTotalScore()).compareTo(o2.getTotalScore());
        }
	},
	PASSAGE_RANK_SORT{
		public int compare(AnswerModel o1, AnswerModel o2) {
        	return Integer.valueOf(o1.getPassageRank()).compareTo(o2.getPassageRank());
        }
	},
    FREQUENCY_SORT {
        public int compare(AnswerModel o1, AnswerModel o2) {
            return Integer.valueOf(o2.getFrequency()).compareTo(o1.getFrequency());
        }},
    DISTANCE_SORT {
        public int compare(AnswerModel o1, AnswerModel o2) {
        	return Integer.valueOf(o1.getDistance()).compareTo(o2.getDistance());
        }},
    RANK_SORT {
		public int compare(AnswerModel o1, AnswerModel o2) {
		    return Integer.valueOf(o1.getDocumentRank()).compareTo(o2.getDocumentRank());
		}},
    ORDER_SORT {
		public int compare(AnswerModel o1, AnswerModel o2) {
		    return Integer.valueOf(o1.getProcessingOrder()).compareTo(o2.getProcessingOrder());
		}};
			    

    public static Comparator<AnswerModel> decending(final Comparator<AnswerModel> other) {
        return new Comparator<AnswerModel>() {
            public int compare(AnswerModel o1, AnswerModel o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }

    public static Comparator<AnswerModel> getComparator(final AnswerModelComparator... multipleOptions) {
        return new Comparator<AnswerModel>() {
            public int compare(AnswerModel o1, AnswerModel o2) {
                for (AnswerModelComparator option : multipleOptions) {
                    int result = option.compare(o1, o2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }
}