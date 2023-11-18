package com.company;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LEACHSimulation extends JFrame {

    private JPanel simulationPanel;
    private List<Node> nodes = new ArrayList<>();
    private Node selectedNode = null;
    private Point offset = new Point();
    private JLabel infoLabel;
    private Color lineColor = Color.GREEN;  // Initial line color
    private Color lineColorSink = Color.BLUE;  // Initial line color
    private int roundNumber = 0; // Added round number variable
    private DefaultListModel<String> roundInfoModel; // Added array model for round information
    private JTextArea round = new JTextArea();
    private JTextArea lastRoundResults;
    private JTextArea historique = new JTextArea();
    private List<Map<Node, String>> allRounds = new ArrayList<>();
    private List<Integer> xData = new ArrayList<>();
    private List<Integer> yData = new ArrayList<>();
    private CategoryChart chart;




    public LEACHSimulation() {
        initUI();
        getContentPane().setBackground(Color.BLACK);
    }

    private void initUI() {
        setTitle("WSN LEACH Simulation");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lastRoundResults = new JTextArea();
        lastRoundResults.setEditable(false);
        lastRoundResults.setFont(new Font("Monospaced", Font.BOLD, 12));
        lastRoundResults.setBackground(new Color(8, 12, 10));
        lastRoundResults.setForeground (Color.white);

        JScrollPane lastRoundScrollPane = new JScrollPane(lastRoundResults);
        lastRoundScrollPane.setPreferredSize(new Dimension(300, 200));
        // Create a new panel to hold the lastRoundScrollPane
        JPanel lastRoundPanel = new JPanel();
        lastRoundPanel.setLayout(new BorderLayout());
        lastRoundPanel.add(lastRoundScrollPane, BorderLayout.CENTER);


        round.setFont(new Font("Monospaced", Font.BOLD, 20));
        round.setForeground(new Color(255, 255, 0));
        round.setText(" Round: ");
        round.setBackground(Color.BLACK);
        historique.setText ("Historique");
        historique. setFont(new Font("Monospaced", Font.BOLD, 15));
        historique.setForeground(new Color(255, 255, 0));
        historique.setBackground(Color.BLACK);

        JButton graphButton = new JButton("Graph");
        graphButton.setForeground(Color.YELLOW);
        graphButton.setBackground (Color.gray);
        graphButton.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayChart();

            }
        });

        JButton addNodesButton = new JButton("Add Nodes");
        addNodesButton.setForeground(Color.YELLOW);
        addNodesButton.setBackground (Color.gray);
        addNodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddNodesDialog();
            }
        });

        JButton simulateButton = new JButton("Simulate");
        simulateButton.setForeground(Color.YELLOW);
        simulateButton.setBackground (Color.gray);

        simulateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateLEACH();
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.setForeground(Color.YELLOW);
        clearButton.setBackground (Color.gray);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearNodes();
            }
        });

        simulationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLines(g);
                drawNodes(g);
            }
            @Override
            public void setBackground(Color bg) {
                super.setBackground(Color.BLACK); // Set background color of the simulation panel
            }
        };

        simulationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleNodeSelection(e.getPoint());
            }
        });

        simulationPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleNodeDrag(e.getPoint());
            }
        });

        infoLabel = new JLabel();
        JPanel infoPanel = new JPanel();
        infoPanel.add(infoLabel);


        // Create the array for round information
        roundInfoModel = new DefaultListModel<>();
        JList<String> roundInfoList = new JList<>(roundInfoModel);
        roundInfoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roundInfoList.setFont(new Font("Monospaced", Font.BOLD, 12));
        roundInfoList.setForeground(new Color(125, 249, 255));
        roundInfoList.setBackground(Color.black);


        JScrollPane roundInfoScrollPane = new JScrollPane(roundInfoList);
        roundInfoScrollPane.setPreferredSize(new Dimension(200, 150));


        // Update the layout to properly display the historical information
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.BLACK);
        controlPanel.add(addNodesButton);
        controlPanel.add(simulateButton);
        controlPanel.add(clearButton);
        controlPanel.add (graphButton);
        controlPanel.add(round);

        JPanel roundPanel = new JPanel();
        roundPanel.setBackground(Color.BLACK);
        roundPanel.add(historique);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(infoLabel, BorderLayout.NORTH);
        southPanel.add(roundPanel, BorderLayout.CENTER);
        southPanel.add(roundInfoScrollPane, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.NORTH);
        add(simulationPanel, BorderLayout.CENTER);
        add(lastRoundPanel, BorderLayout.EAST);


        setLocationRelativeTo(null);
    }

    private void showAddNodesDialog() {
        String cluster1Nodes = JOptionPane.showInputDialog(this, "Enter the number of nodes for Cluster 1:");
        //String cluster1CH = JOptionPane.showInputDialog(this, "Enter the number of cluster heads for Cluster 1:");
        String cluster2Nodes = JOptionPane.showInputDialog(this, "Enter the number of nodes for Cluster 2:");
        //String cluster2CH = JOptionPane.showInputDialog(this, "Enter the number of cluster heads for Cluster 2:");
        String cluster3Nodes = JOptionPane.showInputDialog(this, "Enter the number of nodes for Cluster 3:");
        //String cluster3CH = JOptionPane.showInputDialog(this, "Enter the number of cluster heads for Cluster 3:");
        String cluster4Nodes = JOptionPane.showInputDialog(this, "Enter the number of nodes for Cluster 4:");
       // String cluster4CH = JOptionPane.showInputDialog(this, "Enter the number of cluster heads for Cluster 4:");

        try {
            int nodesC1 = Integer.parseInt(cluster1Nodes);
            int chC1 =1;
            int nodesC2 = Integer.parseInt(cluster2Nodes);
            int chC2 =1;
            int nodesC3 = Integer.parseInt(cluster3Nodes);
            int chC3 = 1;
            int nodesC4 = Integer.parseInt(cluster4Nodes);
            int chC4 = 1;

            addNodes(nodesC1, chC1, nodesC2, chC2, nodesC3, chC3, nodesC4, chC4);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
        }
    }

    private void addNodes(int nodesC1, int chC1, int nodesC2, int chC2, int nodesC3, int chC3, int nodesC4, int chC4) {
        // Clear existing nodes
        nodes.clear();

        // Add nodes for Cluster 1 (10, 10)
        addClusterNodes(nodesC1, chC1, 20, 20, "N", "CH1", 1);

        // Add nodes for Cluster 2 (350, 10)
        addClusterNodes(nodesC2, chC2, 350, 30, "N", "CH2", 2);

        // Add nodes for Cluster 3 (10, 200)
        addClusterNodes(nodesC3, chC3, 20, 220, "N", "CH3", 3);

        // Add nodes for Cluster 4 (350, 200)
        addClusterNodes(nodesC4, chC4, 350, 230, "N", "CH4", 4);

        // Create cluster head links to the sink
        connectClusterHeadToSink("CH1");
        connectClusterHeadToSink("CH2");
        connectClusterHeadToSink("CH3");
        connectClusterHeadToSink("CH4");

        // Create SINK node on the right side
        int sinkX = simulationPanel.getWidth() - 30;
        int sinkY = simulationPanel.getHeight() / 2;
        nodes.add(new Node(Color.BLUE, "SINK", sinkX, sinkY, false, 0, 0)); // Initial energy set to 0

        simulationPanel.repaint(); // Refresh the panel after adding nodes
    }


    private void connectClusterHeadToSink(String chTag) {
        for (Node chNode : nodes) {
            if (chNode.tag.equals(chTag)) {
                for (Node sinkNode : nodes) {
                    if (sinkNode.tag.equals("SINK")) {
                        // Connect cluster head to sink
                        connectNodes(chNode, sinkNode);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void connectNodes(Node node1, Node node2) {
        // Connect two nodes with a line
        Graphics g = simulationPanel.getGraphics();
        g.setColor(Color.white);
        g.drawLine(node1.x, node1.y, node2.x, node2.y);
    }

    private void simulateLEACH() {
        roundInfoModel.clear(); // Clear previous round information

        for (int i = 0; i < allRounds.size(); i++) {
            roundInfoModel.addElement("Round: " + (i + 1)); // Display the round number

            Map<Node, String> roundData = allRounds.get(i);
            for (Map.Entry<Node, String> entry : roundData.entrySet()) {
                roundInfoModel.addElement(entry.getValue()); // Add node information to the list
            }
        }
        int numc = 0;String num = null;long eng = 0;

        for (int i = 0; i < 1; i++) { // Simulate for 5 rounds (you can adjust this)
            lastRoundResults.setText (null);
            roundNumber++;
            simulateRound();
            // Display round information in the array
            roundInfoModel.addElement("Round: " + roundNumber);
            lastRoundResults.append("Last Round Results ==> Round n° "+roundNumber+"\n");
            for (Node node : nodes) {
                roundInfoModel.addElement(node.toString ());
                numc=node.clusterNum;num=node.tag;eng=node.energy;
                lastRoundResults.append("Cluster: "+numc+"   ID: "+num+
                  "     Energy: "+eng +"\n");
            }
        }

        round.setText(" Round: " + roundNumber);
        int totalEnergy = 0;
        for (Node node : nodes) {
            totalEnergy += node.energy;
        }

        xData.add( roundNumber);
        yData.add(totalEnergy);

    }

    private void displayChart() {
        chart = new CategoryChartBuilder()
          .width(800)
          .height(600)
          .title("Total energy dissipation for all nodes")
          .xAxisTitle("Rounds")
          .yAxisTitle("Energy en joules")
          .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setLabelsVisible(false);
        chart.getStyler().setPlotGridLinesVisible(false);

        // Add series to the chart using your data
        chart.addSeries("Leach", xData, yData);

        // Create a new JFrame to display the chart
        JFrame chartFrame = new JFrame("Energy variation graph");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.getContentPane().add(new XChartPanel<> (chart)); // XChartPanel is used for compatibility with XChart charts

        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null); // Center the frame on the screen
        chartFrame.setVisible(true);
    }


    // Inside the LEACHSimulation class
    private void simulateRound() {
        double P = 0.1; // Set the value of P
        long E_MAX = 33; // Set E_MAX for regular nodes
        long E_MAX_CH = 55; // Set E_MAX for cluster heads

        for (Node node : nodes) {
            long currentE_MAX = node.isClusterHead ? E_MAX_CH : E_MAX; // Determine E_MAX based on node type

            double thresholdNode = (P / ((1 - P) * (roundNumber % (1 / P)))) * (node.getEnergy() / 33);
            double thresholdCH = (P / ((1 - P) * (roundNumber % (1 / P)))) * (node.getEnergy() / 55);

            double randomValue = Math.random(); // Generate a random value between 0 and 1

            if (node.getEnergy() > 0 && !node.tag.equals("SINK")) {
                if (!node.isClusterHead) { node.isClusterHead=false;node.color=Color.green;

                }
                    if (node.isClusterHead) {
                        if (randomValue < thresholdCH) {System.out.println ("ch in");
                            node.isClusterHead = true;
                            node.color = Color.RED;
                        } else {
                            node.isClusterHead = false;
                            node.color = Color.GREEN;
                        }
                    } else {
                        if (randomValue < thresholdNode) {System.out.println (node.tag+"node in"+node.clusterNum);
                        node.isClusterHead=true;
                        node.color=Color.red;
                        ReplaceCH(node.clusterNum,node.tag);
                        }
                    }
                    if(!clusterHasHead (node.clusterNum)){ selectClusterHead (node.clusterNum);}


            } else if (node.getEnergy() <= 0 && !node.tag.equals("SINK")) {
                selectClusterHead (node.clusterNum);
                node.color = Color.GRAY;

            }
            // Decrement energy for regular nodes and cluster heads
            long energyDecrement = node.isClusterHead ? 3 : 1;
            long currentEnergy = node.getEnergy();
            if (currentEnergy > 0) {
                node.setEnergy(Math.max(0, currentEnergy - energyDecrement));
            }
        }
        // Inside the simulateRound() method
        Map<Node, String> roundData = new HashMap<> ();
        for (Node node : nodes) {
            // Store information about each node in the current round
            roundData.put(node,
            " Cluster: " + node.clusterNum + "           " + "Node ID: " + node.tag + " " + " "
              + "                   " + "Is CH: "
              + node.isClusterHead + "              " + " Energy: " + node.energy);
        }
        allRounds.add(roundData); // Add the current round's node information to the list

    }


    private void ReplaceCH(int clusterNum,String tag) {
        for (Node node : nodes) {
            if (node.clusterNum == clusterNum && node.tag!=tag) {
              node.isClusterHead=false;node.color=Color.green;
            }
        }

    }

    // Method to check if a cluster has at least one cluster head
    private boolean clusterHasHead(int clusterNum) {
        for (Node node : nodes) {
            if (node.isClusterHead && node.clusterNum == clusterNum) {
                return true;
            }
        }
        return false;
    }


    private Node selectClusterHead(int clusterNum) {
        Node maxEnergyNode = null;
        int countClusterHead = 0;

        for (Node node : nodes) {
            if (node.clusterNum == clusterNum) {
                if (node.isClusterHead) {
                    countClusterHead++;
                }
                if (maxEnergyNode == null  || (!maxEnergyNode.isClusterHead && node.energy > maxEnergyNode.energy)) {
                    maxEnergyNode = node;
                }
            }
        }
        if (countClusterHead == 0) {
            maxEnergyNode.isClusterHead = true;
            maxEnergyNode.color=Color.red;
            return maxEnergyNode;
        } else {
            return null; // No need to select a new cluster head
        }
    }


    private void clearNodes() {
        roundNumber = 0;
        roundInfoModel.clear();
        nodes.clear();
        simulationPanel.repaint();
    }

    private void drawLines(Graphics g) {
        // Draw lines between nodes and their respective cluster heads
        for (Node node : nodes) {
            if (!node.isClusterHead  && node.color != Color.gray) {
                for (Node chNode : nodes) {
                    if (chNode.isClusterHead && (node.clusterNum) == (chNode.clusterNum)) {
                        g.setColor(lineColor);
                        g.drawLine(node.x, node.y, chNode.x, chNode.y);
                        break;
                    }
                }
            }
        }

        // Draw lines between cluster heads and the sink
        for (Node node : nodes) {
            if (node.isClusterHead && node.color != Color.gray) {
                for (Node sinkNode : nodes) {
                    if (sinkNode.tag.equals("SINK")) {
                        g.setColor(lineColorSink);
                        g.drawLine(node.x, node.y, sinkNode.x, sinkNode.y);
                        break;
                    }
                }
            }
        }
        // Periodically change line color
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lineColor = (lineColor == Color.GREEN) ? Color.YELLOW : Color.GREEN;
                lineColorSink = (lineColorSink == Color.BLUE) ? Color.YELLOW : Color.BLUE;

                simulationPanel.repaint();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void drawNodes(Graphics g) {
        for (Node node : nodes) {
            g.setColor(node.color);
            g.fillOval(node.x - 5, node.y - 5, 10, 10);
            g.setColor(new Color(125, 249, 255));
            g.drawString(node.tag, node.x - 14, node.y + 14);

            if (node.tag.equals("SINK")) {
                g.setColor(Color.RED);
                g.drawOval(node.x - 6, node.y - 6, 12, 12);
            }
        }
    }

    private void handleNodeSelection(Point clickPoint) {
        for (Node node : nodes) {
            if (node.contains(clickPoint)) {
                selectedNode = node;
                offset.x = clickPoint.x - node.x;
                offset.y = clickPoint.y - node.y;

                // Update information label
               // updateInfoLabel(node);

                // Show tooltip near the selected node
                showNodeTooltip(node);

                simulationPanel.repaint();
                break;
            }
        }
    }

    private void showNodeTooltip(Node node) {
        JToolTip tooltip = new JToolTip();
        tooltip.setTipText(node.toStringWithEnergy());  // Use node information with energy as tooltip text
        tooltip.setBackground(new Color(255, 255, 225));  // Set tooltip background color

        // Calculate tooltip position relative to the frame
        Point tooltipLocation = SwingUtilities.convertPoint(simulationPanel, node.x, node.y - 20, this);

        tooltip.setLocation(tooltipLocation);

        // Create a new JFrame to hold the tooltip
        JFrame tooltipFrame = new JFrame();
        tooltipFrame.setSize(200, 70);  // Adjust size to accommodate energy information
        tooltipFrame.setLocation(tooltipLocation);
        tooltipFrame.setUndecorated(true);
        tooltipFrame.getContentPane().add(tooltip);

        // Make the tooltip frame visible
        tooltipFrame.setVisible(true);

        // Schedule tooltip removal after a delay (adjust as needed)
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tooltipFrame.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }




    private void handleNodeDrag(Point dragPoint) {
        if (selectedNode != null) {
            selectedNode.x = dragPoint.x - offset.x;
            selectedNode.y = dragPoint.y - offset.y;
            simulationPanel.repaint();
        }
    }

    private void updateInfoLabel(Node node) {
        infoLabel.setText( " Cluster: " +  node.clusterNum +"                       " + "Node ID: " +
          ""+node.tag+"                             " +"Is CH: "
          +node.isClusterHead+"                          " + " Energy: " + node.getEnergy());
    }

    private void addClusterNodes(int nodeCount, int clusterHeadCount, int startX, int startY, String nodeTag, String chTag, int clusterNum) {
        // Generate random coordinates for nodes in the cluster
        int clusterNumber = nodes.size() / (nodeCount + clusterHeadCount) + 1;

        for (int i = 0; i < nodeCount; i++) {
            int x = startX + new Random().nextInt(100);
            int y = startY + new Random().nextInt(100);
            long initialEnergy = 33L;  // Set an initial energy value (adjust as needed)
            nodes.add(new Node(Color.GREEN, nodeTag + i, x, y, false, clusterNum, initialEnergy));
        }

        // Generate random coordinates for cluster heads
        for (int i = 0; i < clusterHeadCount; i++) {
            int x = startX + new Random().nextInt(100);
            int y = startY + new Random().nextInt(100);
            long initialEnergy = 55L;  // Set an initial energy value for cluster heads (adjust as needed)
            nodes.add(new Node(Color.RED, chTag + i, x, y, true, clusterNum, initialEnergy));
        }
    }

    private static class Node {
        Color color;
        String tag;
        int x, y;
        boolean isClusterHead;
        int clusterNum; // Added attribute
        long energy;     // Added attribute

        public Node(Color color, String tag, int x, int y, boolean isClusterHead, int clusterNum, long energy) {
            this.color = color;
            this.tag = tag;
            this.x = x;
            this.y = y;
            this.isClusterHead = isClusterHead;
            this.clusterNum = clusterNum;
            this.energy = energy;
        }

        public boolean contains(Point point) {
            int distance = (x - point.x) * (x - point.x) + (y - point.y) * (y - point.y);
            return distance <= 100; // Assuming a radius of 10 for the nodes
        }
        public boolean getClusterHeadTrue(){return isClusterHead;}
        public boolean setClusterHeadFalse(){return this.isClusterHead=false;}
        public long getEnergy() {
            return energy;
        }

        public void setEnergy(long energy) {
            this.energy = energy;
        }

        @Override
        public String toString() {
            return " Cluster: " + clusterNum + "           " + "Node ID: " + tag + " " + " "
              + "                   " + "Is CH: "
              + isClusterHead + "              " + " Energy: " + energy;
        }

        public String toStringWithEnergy() {
            return "<html> Cluster: " + clusterNum + "<br/> Node ID: " + tag + "<br/> Energy: " + energy + "</html>";
        }


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LEACHSimulation leachSimulation = new LEACHSimulation();
                leachSimulation.setVisible(true);


            }
        });
    }
}
