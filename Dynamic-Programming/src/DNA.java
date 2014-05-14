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

	private static String[] commonDNA2(int[][] g, String seqOne, String seqTwo) {

		seqOne = "-" + seqOne;
		seqTwo = "-" + seqTwo;
		String resultOne = "";
		String resultTwo = "";
		int i = g.length - 1;
		int j = g[0].length - 1;
		while ((i > 0 || j > 0)) {
			int left = Integer.MIN_VALUE;
			if (j != 0) {
				left = g[i][j - 1] + phi;
			}
			int up = Integer.MIN_VALUE;
			if (i != 0) {
				up = g[i - 1][j] + phi;
			}
			int diag = Integer.MIN_VALUE;
			if (j != 0 && i != 0) {
				diag = g[i - 1][j - 1]
						+ BLOSUM.get(Character.toString(seqOne.charAt(i))
								+ Character.toString(seqTwo.charAt(j)));
			}

			// Math.max(BLOSUM.get(key) + graph[i - 1][j - 1],Math.max(phi +
			// graph[i - 1][j], phi + graph[i][j - 1])

			int biggest = biggestOf(up, left, diag);
			if (diag == biggest) {
				resultTwo += seqTwo.charAt(j);
				resultOne += seqOne.charAt(i);
				if (i > 0)
					i--;
				if (j > 0)
					j--;

			} else if (left == biggest) {
				resultTwo += "-";
				resultOne += seqOne.charAt(i);
				if (j > 0)
					j--;
			} else if (up == biggest) {
				resultOne += "-";
				resultTwo += seqTwo.charAt(j);
				if (i > 0)
					i--;

			}

		}

		String[] list = { resultOne, resultTwo };
		return list;
	}

	private static String[] commonDNA(int[][] g, String seqRow, String seqCol) {
		// seqRow = "#" + seqRow;
		// seqCol = "#" + seqCol;
		int row = g.length - 1;
		int column = g[0].length - 1;
		String dnaRow = "";
		String dnaCol = "";

		while (row != 0 || column != 0) {
			int compensate = 1;
			if (row == 0) {
				compensate = 0;
			}
			int compensate2 = 1;
			if (column == 0) {
				compensate2 = 0;
			}
			String key = Character.toString(seqRow.charAt(row - compensate))
					+ Character.toString(seqCol.charAt(column - compensate2));

			int diag;
			if (row == 0 || column == 0) {
				diag = Integer.MIN_VALUE;
			} else {
				diag = BLOSUM.get(key) + g[row - 1][column - 1];
			}
			int left = Integer.MIN_VALUE;
			int up = Integer.MIN_VALUE;
			if (row != 0) {
				up = phi + g[row - 1][column];
			}
			if (column != 0) {
				left = phi + g[row][column - 1];
			}

			int current = g[row][column];

			if (current == diag) {
				dnaRow = Character.toString(seqRow.charAt(row - 1)) + dnaRow;
				dnaCol = Character.toString(seqCol.charAt(column - 1)) + dnaCol;
				if (row != 0) {
					row--;
				}
				if (column != 0) {
					column--;
				}

			} else if (current == up) {
				dnaRow = Character.toString(seqRow.charAt(row - 1)) + dnaRow;
				dnaCol = "-" + dnaCol;
				if (row != 0) {
					row--;
				}
			} else if (current == left) {
				dnaCol = Character.toString(seqCol.charAt(column - 1)) + dnaCol;
				dnaRow = "-" + dnaRow;
				if (column != 0) {
					column--;
				}
			}

		}

		String[] list = { dnaCol, dnaRow };
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
