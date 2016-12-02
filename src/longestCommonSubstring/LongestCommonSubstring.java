package longestCommonSubstring;

import java.util.ArrayList;

public class LongestCommonSubstring {
	
	
	
	public static int LCSubStr (ArrayList<String> x, ArrayList<String> y, int m, int n)
	{
		int[][] dp = new int[m+1][n+1];
		int result = 0;
		for (int i=0;i<=m;i++)
		{
			for (int j=0;j<=n;j++)
			{
				
				if (i==0 || j==0)
				{
					dp[i][j] = 0;
				}
				else if (x.get(i-1).equals(y.get(j-1)))
				{
					dp[i][j] = dp[i-1][j-1]+1;
					result = Math.max(result, dp[i][j]);
				}
				else
				{
					dp[i][j] = 0;
				}
			}
		}
		return result;
	}

}
