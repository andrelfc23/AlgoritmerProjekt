package app;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.SwingUtilities;

import graph.Graph;

public class Main {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
			Graph<String> graph = GUI.generateNewGraph(10, 12, 1400, 900);
			GUI<String> gui = new GUI<>(graph);
			gui.setVisible(true);
		});

	}

}
