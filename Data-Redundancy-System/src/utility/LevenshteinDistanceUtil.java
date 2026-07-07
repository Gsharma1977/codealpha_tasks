package utility;

/**
 * LevenshteinDistanceUtil
 * ──────────────────────────────────────────────────────────────────
 * Computes the edit distance between two strings and converts it
 * into a similarity percentage (0–100).
 *
 * ALGORITHM EXPLANATION (for viva):
 *   Levenshtein Distance = minimum number of single-character edits
 *   (insert, delete, replace) needed to turn string A into string B.
 *
 *   Example:  "Rahul Kumar"  →  "Rahul Kumer"
 *             One substitution (a → e) → distance = 1 → ~91% similar
 *
 * TIME COMPLEXITY : O(m × n)  where m, n = lengths of the two strings
 * SPACE COMPLEXITY: O(m × n)  for the DP table
 */
public class LevenshteinDistanceUtil {

    /**
     * Calculates raw Levenshtein distance between two strings.
     * Case-insensitive comparison.
     */
    public static int calculateDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        s1 = s1.toLowerCase().trim();
        s2 = s2.toLowerCase().trim();

        int m = s1.length();
        int n = s2.length();

        // dp[i][j] = edit distance between s1[0..i-1] and s2[0..j-1]
        int[][] dp = new int[m + 1][n + 1];

        // Base cases: transforming empty string ↔ s1/s2
        for (int i = 0; i <= m; i++) dp[i][0] = i;   // delete all chars of s1
        for (int j = 0; j <= n; j++) dp[0][j] = j;   // insert all chars of s2

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    // Characters match → no edit needed
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    // Take minimum of: replace, delete, insert
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],   // replace
                                    Math.min(dp[i - 1][j],        // delete
                                             dp[i][j - 1]));      // insert
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Converts raw distance into a 0–100 similarity percentage.
     * similarity = (1 - distance / maxLength) × 100
     */
    public static double calculateSimilarity(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 100.0;   // both empty → identical

        int distance = calculateDistance(s1, s2);
        return (1.0 - (double) distance / maxLen) * 100.0;
    }

    /**
     * Quick helper: returns true if similarity >= threshold.
     * Default threshold used in the system: 70%
     */
    public static boolean isSimilar(String s1, String s2, double threshold) {
        return calculateSimilarity(s1, s2) >= threshold;
    }
}
