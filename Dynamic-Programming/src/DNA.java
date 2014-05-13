import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class DNA {
	private final static String TESTDATA_DIR = "/h/d8/l/dat12jkr/git/Dynamic-Programming/Dynamic-Programming/lab5/";
	private final static char SC = File.separatorChar;
	private static final String CLOSEST = "HbB_FASTAs.out";
	static HashMap<String, Integer> BLOSUM;
	static HashMap<String, String> seq;
	static int phi = -4;
	static String infile = TESTDATA_DIR + SC + "HbB_FASTAs.in";
	static String outfile = TESTDATA_DIR + SC + CLOSEST;

	public static void main(String[] args) throws IOException {
		seq = new HashMap<String, String>();
		parse();
		File fileResult = new File(outfile);
		fillAnimals();
		FileReader fr = new FileReader(fileResult);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null) {
			if (line.contains(":")) {
				String[] split = line.split(" ");
				String expected = split[1];
				split = split[0].split("(--)|(\\:)");
				String dnaName1 = split[0];
				String dnaName2 = split[1];
				System.out.println("Calculating: " + dnaName1 + " -- "
						+ dnaName2);
				int result = buildGraph(dnaName1, dnaName2);
				if (result == Integer.parseInt(expected)) {
					System.out.println("Excpected was: " + expected
							+ " Result was: " + result);
				} else {
					System.err.println("FAILURE");
				}
				System.out.println();
			}
			line = br.readLine();

		}
		br.close();

	}

	private static String[] commonDNA(int[][] g, String name1, String name2) {
		String word1 = "";
		String word2 = "";
		int i = g.length - 1;
		int j = g[0].length - 1;
		while (!(i == 1 && j == 1)) {
			final int left = g[i][j - 1];
			final int up = g[i - 1][j];
			final int diag = g[i - 1][j - 1];

			int biggest = biggestOf(up, left, diag);
			if (diag == biggest) {
				word2 += name1.charAt(i - 1);
				word1 += name2.charAt(j - 1);
				if (i > 0)
					i--;
				if (j > 0)
					j--;

			} else if (left == biggest) {
				word2 += "-";
				word1 += name2.charAt(j - 1);
				if (i > 0)
					j--;
			} else if (up == biggest) {
				word1 += "-";
				word2 += name2.charAt(i - 1);
				if (j > 0)
					i--;

			}

		}

		String[] list = { word1, word2 };
		return list;
	}

	private static int biggestOf(int a, int b, int c) {
		int[] numbers = { a, b, c };
		{
			int max = numbers[0];
			for (int i = 1; i < numbers.length; i++) {
				if (numbers[i] > max) {
					max = numbers[i];
				}
			}
			return max;
		}

	}

	public static void fillAnimals() throws IOException {
		System.out.println("Filling animals");
		File fileIn = new File(infile);
		FileReader fr = new FileReader(fileIn);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null && !line.contains("EOF")) {
			if (line.startsWith(">")) {
				String animal = line.split(" ")[0].substring(1);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 3; i++) {
					line = br.readLine();
					sb.append(line);
				}
				seq.put(animal, sb.toString());
				line = br.readLine();
			}
		}
		br.close();
	}

	private static void print() {
		// for (int i = 0; i < graph.length; i++) {
		// for (int j = 0; j < graph[0].length; j++) {
		// System.out.print(graph[i][j] + "\t");
		// }
		// System.out.println();
		// }

	}

	private static int buildGraph(String dnaName1, String dnaName2) {
		String dna1 = seq.get(dnaName1);
		String dna2 = seq.get(dnaName2);

		int[][] graph = new int[dna1.length() + 1][dna2.length() + 1];
		init(graph);
		for (int i = 1; i < graph.length; i++) {
			for (int j = 1; j < graph[0].length; j++) {
				String key = Character.toString(dna1.charAt(i - 1))
						+ Character.toString(dna2.charAt(j - 1));
				graph[i][j] = Math.max(BLOSUM.get(key) + graph[i - 1][j - 1],
						Math.max(phi + graph[i - 1][j], phi + graph[i][j - 1]));
			}
		}

		String[] out = commonDNA(graph, dna1, dna2);
		System.out.println(out[0]);
		System.out.println(out[1]);

		return graph[graph.length - 1][graph[0].length - 1];
	}

	private static int[][] init(int[][] graph) {
		for (int i = 0; i < graph.length; i++) {
			graph[i][0] = i * phi;
		}
		for (int i = 0; i < graph[0].length; i++) {
			graph[0][i] = i * phi;
		}
		return graph;
	}

	private static void parse() throws IOException {
		System.out.println("Parsing BLOSSUM");
		FileReader fr = new FileReader(new File("lab5/BLOSUM62.txt"));
		BufferedReader br = new BufferedReader(fr);
		String line = "hej";
		for (int i = 0; i < 6; i++) {
			br.readLine();
		}

		String letters = br.readLine();
		letters = letters.trim();
		String[] rowLetters = letters.split("\\s+");
		int index = 0;
		BLOSUM = new HashMap<String, Integer>();
		while (br.ready()) {
			line = br.readLine();
			String[] values = line.split("\\s+");

			for (int i = 1; i < values.length; i++) {
				String key = rowLetters[i - 1] + values[0];
				BLOSUM.put(key, Integer.parseInt(values[i]));
			}
			index++;
		}

		br.close();
	}
}
