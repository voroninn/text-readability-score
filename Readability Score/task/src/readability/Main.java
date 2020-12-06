package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private final static Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u', 'y');
    private static double sumOfIndexes = 0.0;


    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        String text = new String(Files.readAllBytes(Paths.get(fileName)));
        String[] sentences = text.split("[.!?]+\\s*");
        int sentenceCount = sentences.length;
        ArrayList<String> words = new ArrayList<>();
        int wordCount = 0;

        for (String sentence : sentences) {
            String[] wordArray = sentence.split("\\s+");
            wordCount += wordArray.length;
            words.addAll(Arrays.asList(wordArray));
        }

        int characterCount = text.replaceAll(" ", "").length();
        int syllableCount = 0;
        int polysyllableCount = 0;

        for (String word : words) {
            int vowelCount = 0;
            boolean wasIncremented = false;
            for (int i = 0; i < word.length(); i++) {
                if (VOWELS.contains(word.charAt(i)) && !wasIncremented &&
                        !(i == word.length() - 1 && word.charAt(i) == 'e')) {
                    vowelCount++;
                    wasIncremented = true;
                } else {
                    wasIncremented = false;
                }
            }
            if (vowelCount == 0) {
                syllableCount++;
            } else {
                syllableCount += vowelCount;
            }
            if (vowelCount > 2) {
                polysyllableCount++;
            }
        }

        System.out.println("Words: " + wordCount);
        System.out.println("Sentences: " + sentenceCount);
        System.out.println("Characters: " + characterCount);
        System.out.println("Syllables: " + syllableCount);
        System.out.println("Polysyllables: " + polysyllableCount);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");

        Scanner scanner = new Scanner(System.in);
        switch (scanner.next()) {
            case "ARI":
                calculateARI(sentenceCount, wordCount, characterCount);
                break;
            case "FK":
                calculateFK(sentenceCount, wordCount, syllableCount);
                break;
            case "SMOG":
                calculateSMOG(sentenceCount, polysyllableCount);
                break;
            case "CL":
                calculateCL(sentenceCount, wordCount, characterCount);
                break;
            case "all":
                calculateARI(sentenceCount, wordCount, characterCount);
                calculateFK(sentenceCount, wordCount, syllableCount);
                calculateSMOG(sentenceCount, polysyllableCount);
                calculateCL(sentenceCount, wordCount, characterCount);
                System.out.printf("%n%nThis text should be understood in average by %.2f year olds.",
                        sumOfIndexes / 4);
                break;
        }
    }

    public static int determineSuitableAge(double readabilityScore) {
        int roundedScore = (int) Math.round(readabilityScore);
        if (roundedScore < 3) {
            return roundedScore + 5;
        } else if (roundedScore < 13) {
            return roundedScore + 6;
        } else {
            return 24;
        }
    }

    public static void calculateARI(int sentenceCount, int wordCount, int characterCount) {
        double readabilityScore = 4.71 * ((double) characterCount / wordCount) +
                0.5 * ((double) wordCount / sentenceCount) - 21.43;
        sumOfIndexes += readabilityScore;
        int suitableAge = determineSuitableAge(readabilityScore);
        System.out.printf("%nAutomated Readability Index: %.2f (about %d year olds).",
                readabilityScore, suitableAge);
    }

    public static void calculateFK(int sentenceCount, int wordCount, int syllableCount) {
        double readabilityScore = 0.39 * ((double) wordCount / sentenceCount) +
                11.8 * ((double) syllableCount / wordCount) - 15.59;
        sumOfIndexes += readabilityScore;
        int suitableAge = determineSuitableAge(readabilityScore);
        System.out.printf("%nFlesch–Kincaid readability tests: %.2f (about %d year olds).",
                readabilityScore, suitableAge);
    }

    public static void calculateSMOG(int sentenceCount, int polysyllableCount) {
        double readabilityScore = 1.043 * Math.sqrt(polysyllableCount * (30.0 / sentenceCount)) + 3.1291;
        sumOfIndexes += readabilityScore;
        int suitableAge = determineSuitableAge(readabilityScore);
        System.out.printf("%nSimple Measure of Gobbledygook: %.2f (about %d year olds).",
                readabilityScore, suitableAge);
    }

    public static void calculateCL(int sentenceCount, int wordCount, int characterCount) {
        double averageLetters = (double) characterCount / wordCount * 100;
        double averageSentences = (double) sentenceCount / wordCount * 100;
        double readabilityScore = 0.0588 * averageLetters - 0.296 * averageSentences - 15.8;
        sumOfIndexes += readabilityScore;
        int suitableAge = determineSuitableAge(readabilityScore);
        System.out.printf("%nColeman–Liau index: %.2f (about %d year olds).",
                readabilityScore, suitableAge);
    }
}
