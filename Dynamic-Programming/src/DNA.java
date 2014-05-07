import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DNA {
	static HashMap<String, Integer> BLOSUM;
	static int[][] graph;
	static int phi = -4;
	
	public static void main(String[] args) throws IOException {
		parse();
		buildGraph("MVHLTPEEKSAVTALWGKVNVDEVGGEALGRLLVVYPWTQRFFESFGDLSTPDAVMGNPKVKAHGKKVLGAFSDGLAHLDNLKGTFATLSELHCDKLHVDPENFKLLGNVLVCVLAHHFGKEFTPPVQAAYQKVVAGVANALAHKYH", 
				"VHLTGEEKAAVTALWGKVNVDEVGGEALGRLLVVYPWTQRFFESFGDLSTPDAVMSNPKVKAHGKKVLGAFSDGLAHLDNLKGTFAQLSELHCDKLHVDPENFRLLGNVLVCVLAHHFGKEFTPQLQAAYQKVVAGVANALAHKYH");
		print();
	}

	private static void print() {
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph[0].length; j++) {
				System.out.print(graph[i][j]+ "\t");
			}
			System.out.println();
		}
		
	}

	private static void buildGraph(String dna1, String dna2) {
		graph = new int[dna1.length() + 1][dna2.length() + 1];
		init();
		for (int i = 1; i < graph.length; i++) {
			for (int j = 1; j < graph[0].length; j++) {
				String key = Character.toString(dna1.charAt(i-1)) + Character.toString(dna2.charAt(j-1));
				System.out.println(key);
				graph[i][j] = Math.max(BLOSUM.get(key) + graph[i-1][j-1], Math.max(phi + graph[i-1][j], phi + graph[i][j-1]));
				
			}
		}
	}



	private static void init() {
		for (int i = 0; i < graph.length; i++) {
			graph[i][0] = i*phi;
		}		
		for (int i = 0; i < graph[0].length; i++) {
			graph[0][i] = i*phi;
		}	
	}

	private static void parse() throws IOException {
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
				String key = rowLetters[i-1] + values[0];
				BLOSUM.put(key, Integer.parseInt(values[i]));
			}
			index++;
		}
		
		
		br.close();
	}
}
