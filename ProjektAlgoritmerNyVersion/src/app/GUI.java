package app;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import graph.Edge;
import graph.Graph;
import graph.GraphHelper;
import graph.ServerHall;
import graph.UpdatedPosition;

public class GUI<T> extends JFrame {

	private GraphPanel graphPanel;
	private double zoomFactor = 1.0;
	private final String[] themes = { "Slump", "Pastell", "Mörk", "Neon" };
	private JComboBox<String> themeBox;

	public GUI(Graph<T> graph) {
		setTitle("Graph Viewer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1200, 900);
		setLocationRelativeTo(null);

		JPanel controlPanel = new JPanel(new FlowLayout());

		JComboBox<String> startBox = new JComboBox<>();
		JComboBox<String> endBox = new JComboBox<>();
		JLabel resultLabel = new JLabel(" ");
		JButton dijkstraButton = new JButton("Beräkna kortaste väg");
		JButton clearButton = new JButton("Generera ny graf");
		JButton saveButton = new JButton("Spara som bild");
		JButton colorButton = new JButton("Slumpa färger");
		JButton zoomIn = new JButton("+");
		JButton zoomOut = new JButton("-");
		JButton showUpdatesBtn = new JButton("Visa GPS-förflyttningar");

		for (ServerHall<T> v : graph.getAllServerHalls()) {
			String info = v.getInfo().toString();
			startBox.addItem(info);
			endBox.addItem(info);
		}

		controlPanel.add(new JLabel("Start:"));
		controlPanel.add(startBox);
		controlPanel.add(new JLabel("Mål:"));
		controlPanel.add(endBox);
		controlPanel.add(dijkstraButton);
		controlPanel.add(resultLabel);
		controlPanel.add(clearButton);
		controlPanel.add(saveButton);
		controlPanel.add(colorButton);
		controlPanel.add(zoomOut);
		controlPanel.add(zoomIn);
		themeBox = new JComboBox<>(themes);
		themeBox.setSelectedItem("Mörk");
		applyColorTheme(graph, "Mörk");
		controlPanel.add(new JLabel("Tema:"));
		controlPanel.add(themeBox);
		controlPanel.add(showUpdatesBtn);

		graphPanel = new GraphPanel(graph);
		graphPanel.setPreferredSize(new Dimension(1200, 800));

		dijkstraButton.addActionListener(e -> {
			String start = (String) startBox.getSelectedItem();
			String end = (String) endBox.getSelectedItem();
			if (start != null && end != null && !start.equals(end)) {
				Map<ServerHall<T>, Double> result = graph.shortestPaths((T) start);
				ServerHall<T> endNode = graph.getServerHall((T) end);
				double dist = result.getOrDefault(endNode, Double.POSITIVE_INFINITY);
				if (dist < Double.POSITIVE_INFINITY) {
					resultLabel.setText("Avstånd: " + String.format("%.2f", dist));
					List<Edge<T>> pathEdges = graph.getShortestPathEdges((T) start, (T) end);
					graphPanel.setHighlightedPath(pathEdges);
					graphPanel.startCarAnimation(pathEdges); // starta bilanimation
				} else {
					resultLabel.setText("Ingen väg hittades");
					graphPanel.setHighlightedPath(new ArrayList<>());
				}
			}
		});

		clearButton.addActionListener(e -> {
			Graph<String> nyGraf = GUI.generateNewGraph(10, 12, 1400, 900);
			GUI<String> nyttFönster = new GUI<>(nyGraf);
			nyttFönster.setVisible(true);
			dispose();
		});

		saveButton.addActionListener(e -> {
			BufferedImage image = new BufferedImage(graphPanel.getWidth(), graphPanel.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = image.createGraphics();
			graphPanel.paint(g2d);
			g2d.dispose();
			try {
				File outputfile = new File("graf_export.png");
				ImageIO.write(image, "png", outputfile);
				JOptionPane.showMessageDialog(this, "Graf sparad som graf_export.png");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Misslyckades med att spara bilden.");
			}
		});

		colorButton.addActionListener(e -> {
			applyColorTheme(graph, (String) themeBox.getSelectedItem());
			graphPanel.repaint();
		});

		zoomIn.addActionListener(e -> {
			zoomFactor *= 1.1;
			graphPanel.repaint();
		});

		zoomOut.addActionListener(e -> {
			zoomFactor /= 1.1;
			graphPanel.repaint();
		});
		showUpdatesBtn.addActionListener(e -> {
			List<ServerHall<T>> original = graph.getAllServerHalls();
			List<UpdatedPosition<T>> simulated = GraphHelper.moveLocation(original); // ← use your helper method
			graphPanel.showUpdatedPoints(simulated);
		});

		themeBox.addActionListener(e -> {
			applyColorTheme(graph, (String) themeBox.getSelectedItem());
			graphPanel.repaint();
		});

		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		getContentPane().add(graphPanel, BorderLayout.CENTER);
	}

	private void applyColorTheme(Graph<T> graph, String selected) {
		Random rand = new Random();
		for (ServerHall<T> node : graph.getAllServerHalls()) {
			Color c;
			switch (selected) {
			case "Pastell":
				c = new Color(200 + rand.nextInt(55), 200 + rand.nextInt(55), 200 + rand.nextInt(55));
				break;
			case "Mörk":
				Color[] darkPalette = { new Color(80, 0, 80), // Lila
						new Color(0, 80, 160), // Blå
						new Color(0, 120, 60), // Grön
						new Color(100, 50, 0), // Brun
						new Color(130, 0, 0), // Vinröd
						new Color(0, 60, 60), // Petrol
						new Color(70, 70, 0), // Oliv
						new Color(0, 0, 100) // Mörkblå
				};
				c = darkPalette[rand.nextInt(darkPalette.length)];
				break;

			case "Neon":
				Color[] neonPalette = { new Color(57, 255, 20), // Neon Grön
						new Color(255, 20, 147), // Neon Rosa
						new Color(0, 255, 255), // Cyan
						new Color(255, 255, 0), // Gul
						new Color(255, 105, 180), // Hot Pink
						new Color(0, 255, 127), // Spring Green
						new Color(0, 191, 255), // Deep Sky Blue
						new Color(255, 0, 255) // Magenta
				};
				c = neonPalette[rand.nextInt(neonPalette.length)];
				break;
			default:
				c = new Color(rand.nextInt(180), rand.nextInt(180), rand.nextInt(180));
			}
			node.setColor(c);
		}
	}

	public static Graph<String> generateNewGraph(int numNodes, int numEdges, int width, int height) {
		Graph<String> graph = new Graph<>();
		Random rand = new Random();
		double padding = 150;
		int cols = (int) Math.ceil(Math.sqrt(numNodes));
		int rows = (int) Math.ceil((double) numNodes / cols);
		int spacingX = (int) ((width - 2 * padding) / Math.max(1, cols - 1));
		int spacingY = (int) ((height - 2 * padding) / Math.max(1, rows - 1));
		int nodeIndex = 0;
		for (int row = 0; row < rows && nodeIndex < numNodes; row++) {
			for (int col = 0; col < cols && nodeIndex < numNodes; col++) {
				double x = padding + col * spacingX + rand.nextInt(60) - 30;
				double y = padding + row * spacingY + rand.nextInt(60) - 30;
				String name = String.valueOf((char) ('A' + nodeIndex));
				graph.addServerHall(x, y, name);
				nodeIndex++;
			}
		}
		Set<String> existing = new HashSet<>();
		while (existing.size() < numEdges) {
			String from = String.valueOf((char) ('A' + rand.nextInt(numNodes)));
			String to = String.valueOf((char) ('A' + rand.nextInt(numNodes)));
			if (!from.equals(to)) {
				String key = from + "-" + to;
				String revKey = to + "-" + from;
				if (!existing.contains(key) && !existing.contains(revKey)) {
					try {
						graph.addEdge(from, to);
						existing.add(key);
					} catch (IllegalArgumentException ignored) {
					}
				}
			}
		}
		return graph;
	}

	// GraphPanel with animation will be added next

// Den här klassen ska läggas in i GUI.java som en innerklass.
// Den hanterar rendering och animation av en bil-ikon längs kortaste vägen.

	private class GraphPanel extends JPanel {
		private final Graph<T> graph;
		private final int RADIUS = 15;
		private List<Edge<T>> highlightedPath = new ArrayList<>();
		private ServerHall<T> hoveredNode = null;
		private Point dragStart = null;
		private ServerHall<T> draggedNode = null;

		private Timer carTimer;
		private List<Point> carPath = new ArrayList<>();
		private int carIndex = 0;
		private Image carImage;
		private List<UpdatedPosition<T>> updatedPoints = new ArrayList<>(); 
		private UpdatedPosition<T> maxMismatchPoint = null; 
		private ServerHall<T> maxMismatchOriginal = null; 
		private boolean showUpdatedPoints = false;
		private UpdatedPosition<T> hoveredUpdated = null;
		private final double MOVEMENT_THRESHOLD = 10.0; 

		public GraphPanel(Graph<T> graph) {
			this.graph = graph;
			setBackground(Color.WHITE);
			ToolTipManager.sharedInstance().registerComponent(this);

			try {
				carImage = ImageIO.read(getClass().getResource("/app/car1.png"));
			} catch (IOException e) {
				carImage = null;
			}

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					Point p = e.getPoint();
					for (ServerHall<T> node : graph.getAllServerHalls()) {
						int dx = (int) (p.x / zoomFactor - node.getX());
						int dy = (int) (p.y / zoomFactor - node.getY());
						if (Math.sqrt(dx * dx + dy * dy) <= RADIUS) {
							draggedNode = node;
							dragStart = p;
							break;
						}
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					draggedNode = null;
					dragStart = null;
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					for (ServerHall<T> node : graph.getAllServerHalls()) {
						int dx = e.getX() - (int) node.getX();
						int dy = e.getY() - (int) node.getY();
						if (Math.sqrt(dx * dx + dy * dy) <= RADIUS) {
							JOptionPane.showMessageDialog(GraphPanel.this,
									"Nod: " + node.getInfo() + "\nX: " + node.getX() + "\nY: " + node.getY(), "Nodinfo",
									JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					hoveredNode = null;
					hoveredUpdated = null;

					// Check server halls first
					for (ServerHall<T> node : graph.getAllServerHalls()) {
						int dx = e.getX() - (int) node.getX();
						int dy = e.getY() - (int) node.getY();
						if (Math.sqrt(dx * dx + dy * dy) <= RADIUS) {
							hoveredNode = node;
							break;
						}
					}

					// Then check updated positions (red dots)
					if (showUpdatedPoints) {
						for (UpdatedPosition<T> updated : updatedPoints) {
							int ux = (int) updated.getX();
							int uy = (int) updated.getY();
							int dx = e.getX() - ux;
							int dy = e.getY() - uy;
							if (Math.sqrt(dx * dx + dy * dy) <= RADIUS) {
								hoveredUpdated = updated;
								break;
							}
						}
					}

					if (hoveredUpdated != null) {
						ServerHall<T> original = graph.getServerHall(hoveredUpdated.getId());
						if (original != null) {
							double dx = original.getX() - hoveredUpdated.getX();
							double dy = original.getY() - hoveredUpdated.getY();
							double dist = Math.sqrt(dx * dx + dy * dy);
							String warning = (dist > MOVEMENT_THRESHOLD) ? " ⚠️ För långt!" : "";
							setToolTipText("Flyttat: " + String.format("%.2f", dist) + " meter" + warning);
						}
					} else if (hoveredNode != null) {
						setToolTipText(hoveredNode.getInfo().toString());
					} else {
						setToolTipText(null);
					}
				}

				
				@Override
				public void mouseDragged(MouseEvent e) {
					if (draggedNode != null && dragStart != null) {
						double dx = (e.getX() - dragStart.x) / zoomFactor;
						double dy = (e.getY() - dragStart.y) / zoomFactor;
						draggedNode.setX(draggedNode.getX() + dx);
						draggedNode.setY(draggedNode.getY() + dy);
						dragStart = e.getPoint();
						repaint();
					}
				}
			});
		}

		public void setHighlightedPath(List<Edge<T>> path) {
			this.highlightedPath = path;
			repaint();
		}

		// Saves list of new GPS positions
		public void showUpdatedPoints(List<UpdatedPosition<T>> updated) {
			this.updatedPoints = updated;
			this.showUpdatedPoints = true;

			// Find the updated point with the largest distance from original
			double maxDistance = -1;
			maxMismatchPoint = null;
			maxMismatchOriginal = null;

			for (UpdatedPosition<T> u : updated) {
				ServerHall<T> original = graph.getServerHall(u.getId());
				if (original != null) {
					double dx = u.getX() - original.getX();
					double dy = u.getY() - original.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					if (dist > maxDistance) {
						maxDistance = dist;
						maxMismatchPoint = u;
						maxMismatchOriginal = original;
					}
				}
			}

			repaint();
		}

		public void startCarAnimation(List<Edge<T>> pathEdges) {
			if (carTimer != null && carTimer.isRunning())
				carTimer.stop();
			carPath.clear();
			for (Edge<T> edge : pathEdges) {
				Point from = new Point((int) edge.getFrom().getX(), (int) edge.getFrom().getY());
				Point to = new Point((int) edge.getTo().getX(), (int) edge.getTo().getY());
				int steps = 20;
				for (int i = 0; i <= steps; i++) {
					double t = i / (double) steps;
					int x = (int) ((1 - t) * from.x + t * to.x);
					int y = (int) ((1 - t) * from.y + t * to.y);
					carPath.add(new Point(x, y));
				}
			}
			carIndex = 0;
			carTimer = new Timer(100, e -> {
				if (carIndex < carPath.size() - 1) {
					carIndex++;
					repaint();
				} else {
					carTimer.stop();
				}
			});
			carTimer.start();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.scale(zoomFactor, zoomFactor);

			// Draw edges
			Set<String> drawn = new HashSet<>();
			g2.setStroke(new BasicStroke(2));
			for (Edge<T> edge : graph.getAllEdges()) {
				ServerHall<T> from = edge.getFrom();
				ServerHall<T> to = edge.getTo();
				String key = from.getInfo() + "-" + to.getInfo();
				String reverseKey = to.getInfo() + "-" + from.getInfo();
				if (!drawn.contains(key) && !drawn.contains(reverseKey)) {
					drawn.add(key);
					int x1 = (int) from.getX();
					int y1 = (int) from.getY();
					int x2 = (int) to.getX();
					int y2 = (int) to.getY();
					g2.setColor(edge.getColor() != null ? edge.getColor() : Color.GRAY);
					g2.drawLine(x1, y1, x2, y2);
					String dist = String.format("%.1f", edge.getDistance());
					g2.setColor(Color.BLACK);
					g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
					g2.drawString(dist, (x1 + x2) / 2 + 5, (y1 + y2) / 2 - 5);
				}
			}

			// Draw highlighted path
			if (highlightedPath != null) {
				g2.setColor(Color.GREEN);
				g2.setStroke(new BasicStroke(3));
				for (Edge<T> edge : highlightedPath) {
					ServerHall<T> from = edge.getFrom();
					ServerHall<T> to = edge.getTo();
					g2.drawLine((int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());
				}
				g2.setStroke(new BasicStroke(1));
			}

			
			if (carPath != null && carIndex < carPath.size() && carImage != null) {
				Point p = carPath.get(carIndex);
				g2.drawImage(carImage, p.x - 10, p.y - 10, 20, 20, this);
			}

			
			for (ServerHall<T> v : graph.getAllServerHalls()) {
				int x = (int) v.getX();
				int y = (int) v.getY();
				g2.setColor(new Color(0, 0, 0, 40));
				g2.fillOval(x - RADIUS + 3, y - RADIUS + 3, RADIUS * 2, RADIUS * 2);
				g2.setColor(v.getColor() != null ? v.getColor() : Color.BLACK);
				g2.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
				String label = v.getInfo() != null ? v.getInfo().toString() : "?";
				FontMetrics fm = g2.getFontMetrics();
				int textWidth = fm.stringWidth(label);
				int textHeight = fm.getAscent();
				g2.setColor(Color.WHITE);
				g2.drawString(label, x - textWidth / 2, y + textHeight / 4);
			}
			/*
			 * Draws each new (simulated) GPS point in light red. Draws a light gray line
			 * from the old location to the new one. Draws a thick red line and circle on
			 * the point that moved the furthest — helps visualize major GPS mismatch.
			 */
			if (showUpdatedPoints && updatedPoints != null) {
				g2.setColor(new Color(255, 100, 100)); // Light red for updated points

				for (UpdatedPosition<T> u : updatedPoints) {
					int x = (int) u.getX();
					int y = (int) u.getY();
					g2.fillOval(x - RADIUS / 2, y - RADIUS / 2, RADIUS, RADIUS);

					ServerHall<T> original = graph.getServerHall(u.getId());
					if (original != null) {
						double dx = u.getX() - original.getX();
						double dy = u.getY() - original.getY();
						double dist = Math.sqrt(dx * dx + dy * dy);

						// Draw line to original
						g2.setColor(new Color(160, 160, 160));
						g2.drawLine((int) original.getX(), (int) original.getY(), x, y);

						// Highlight in red if movement is too big
						if (dist > MOVEMENT_THRESHOLD) {
							g2.setColor(Color.RED);
							g2.drawOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
						}

						g2.setColor(new Color(255, 100, 100)); 
					}
				}

				
				if (maxMismatchPoint != null && maxMismatchOriginal != null) {
					g2.setColor(Color.RED);
					g2.setStroke(new BasicStroke(3));
					g2.drawLine((int) maxMismatchOriginal.getX(), (int) maxMismatchOriginal.getY(),
							(int) maxMismatchPoint.getX(), (int) maxMismatchPoint.getY());
					g2.drawOval((int) maxMismatchPoint.getX() - RADIUS, (int) maxMismatchPoint.getY() - RADIUS,
							RADIUS * 2, RADIUS * 2);
					g2.setStroke(new BasicStroke(1)); 
				}
			}
		}
	}

}
