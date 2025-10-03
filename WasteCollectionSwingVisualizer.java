package project;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Collections;


public class Prefix {
	private static List<String> communityPrograms = new ArrayList<>();
    private static List<String> policyUpdates = new ArrayList<>();


    private static class Graph {
        private final Map<String, List<Route>> adjacencyList = new HashMap<>();

        public void addArea(String area) {
            adjacencyList.putIfAbsent(area, new ArrayList<>());
        }

        public void addRoute(String from, String to, int distance) {
            adjacencyList.get(from).add(new Route(to, distance));
            adjacencyList.get(to).add(new Route(from, distance)); // Assuming undirected graph
        }

        public List<String> getOptimizedRoute(String start, String destination) {
            PriorityQueue<Route> pq = new PriorityQueue<>(Comparator.comparingInt(r -> r.distance));
            Map<String, Integer> distances = new HashMap<>();
            Map<String, String> previous = new HashMap<>();

            for (String area : adjacencyList.keySet()) {
                distances.put(area, Integer.MAX_VALUE);
            }
            distances.put(start, 0);
            pq.add(new Route(start, 0));

            while (!pq.isEmpty()) {
                Route current = pq.poll();
                String currentArea = current.area;

                if (currentArea.equals(destination)) break;

                for (Route neighbor : adjacencyList.get(currentArea)) {
                    int newDist = distances.get(currentArea) + neighbor.distance;
                    if (newDist < distances.get(neighbor.area)) {
                        distances.put(neighbor.area, newDist);
                        previous.put(neighbor.area, currentArea);
                        pq.add(new Route(neighbor.area, newDist));
                    }
                }
            }

            List<String> path = new ArrayList<>();
            for (String at = destination; at != null; at = previous.get(at)) {
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
    }

    private static class Route {
        String area;
        int distance;

        Route(String area, int distance) {
            this.area = area;
            this.distance = distance;
        }
    }

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        String recyclingTip = null;
    }

    private static class RecyclingTrie {
        private final TrieNode root = new TrieNode();

        public void addRecyclingTip(String item, String tip) {
            TrieNode current = root;
            for (char c : item.toLowerCase().toCharArray()) {
                current.children.putIfAbsent(c, new TrieNode());
                current = current.children.get(c);
            }
            current.recyclingTip = tip;
        }

        // New method for suggestions
        public List<String> getTipsByPrefix(String prefix) {
            TrieNode current = root;
            for (char c : prefix.toLowerCase().toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return Collections.emptyList(); // no suggestions
                }
                current = current.children.get(c);
            }
            List<String> suggestions = new ArrayList<>();
            collectTips(current, suggestions);
            return suggestions;
        }

        private void collectTips(TrieNode node, List<String> tips) {
            if (node.recyclingTip != null) tips.add(node.recyclingTip);
            for (TrieNode child : node.children.values()) {
                collectTips(child, tips);
            }
        }
     // In RecyclingTrie class
        public List<String> getMaterialsByPrefix(String prefix) {
            TrieNode current = root;
            for (char c : prefix.toLowerCase().toCharArray()) {
                if (!current.children.containsKey(c)) return Collections.emptyList();
                current = current.children.get(c);
            }
            List<String> results = new ArrayList<>();
            collectMaterials(current, new StringBuilder(prefix.toLowerCase()), results);
            return results;
        }

        private void collectMaterials(TrieNode node, StringBuilder prefix, List<String> materials) {
            if (node.recyclingTip != null) materials.add(prefix.toString());
            for (var entry : node.children.entrySet()) {
                prefix.append(entry.getKey());
                collectMaterials(entry.getValue(), prefix, materials);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        public String getRecyclingTip(String material) {
            TrieNode current = root;
            for (char c : material.toLowerCase().toCharArray()) {
                if (!current.children.containsKey(c)) return "No tip available.";
                current = current.children.get(c);
            }
            return current.recyclingTip != null ? current.recyclingTip : "No tip available.";
        }


    }

    private static final Graph areaGraph = new Graph();
    private static final RecyclingTrie recyclingTrie = new RecyclingTrie();
    private static final Map<String, Integer> wasteProductionData = new HashMap<>();
    private static final Map<String, String> wasteCategories = new HashMap<>();
    private static final List<String> citizenFeedback = new ArrayList<>();

    public static void main(String[] args) {
        initializeData();
        setGlobalUI(); // <-- add this
        SwingUtilities.invokeLater(Prefix::showMainMenu);
    }

    private static void initializeData() {
        initializeGraph();
        initializeRecyclingTips();
        initializeWasteProductionData();
        initializeWasteCategories();
    }

    private static void initializeGraph() {
        areaGraph.addArea("A");
        areaGraph.addArea("B");
        areaGraph.addArea("C");
        areaGraph.addArea("D");
        areaGraph.addArea("E");
        
        areaGraph.addRoute("A", "B", 5);
        areaGraph.addRoute("A", "C", 10);
        areaGraph.addRoute("B", "C", 2);
        areaGraph.addRoute("C", "D", 3);
        areaGraph.addRoute("B", "D", 8);
        areaGraph.addRoute("A", "E", 7);
    }

    private static void initializeRecyclingTips() {
        recyclingTrie.addRecyclingTip("plastic", "Rinse and place in blue recycling bin.");
        recyclingTrie.addRecyclingTip("glass", "Separate by color and recycle in green bin.");
        recyclingTrie.addRecyclingTip("paper", "Flatten and put in paper recycling bin.");
        recyclingTrie.addRecyclingTip("cardboard", "Flatten and put in the cardboard recycling bin.");
        recyclingTrie.addRecyclingTip("metal", "Clean and place in the metal recycling bin.");
        recyclingTrie.addRecyclingTip("aluminum", "Rinse and recycle in aluminum bin.");
        recyclingTrie.addRecyclingTip("steel", "Place in the metal recycling bin.");
        recyclingTrie.addRecyclingTip("tin cans", "Rinse and recycle in metal bin.");
        recyclingTrie.addRecyclingTip("plastic_bottles", "Rinse and recycle in the plastic bin.");
        recyclingTrie.addRecyclingTip("magazines", "Flatten and recycle in the paper bin.");
        recyclingTrie.addRecyclingTip("newspapers", "Flatten and recycle in the paper bin.");
        recyclingTrie.addRecyclingTip("envelopes", "Remove any plastic windows and recycle in paper bin.");
        recyclingTrie.addRecyclingTip("cereal_boxes", "Flatten and recycle in the cardboard bin.");
        recyclingTrie.addRecyclingTip("junk_mail", "Flatten and recycle in paper bin.");
        recyclingTrie.addRecyclingTip("egg_cartons", "Recycling in the paper or cardboard bin.");
        recyclingTrie.addRecyclingTip("plastic_bags", "Recycling at special plastic bag collection points.");
        recyclingTrie.addRecyclingTip("batteries", "Take to a designated e-waste or hazardous waste facility.");
        recyclingTrie.addRecyclingTip("electronics", "Drop off at designated e-waste collection centers.");
        recyclingTrie.addRecyclingTip("light_bulbs", "Dispose of at a hazardous waste collection point.");
        recyclingTrie.addRecyclingTip("printer_cartridges", "Take to a recycling center for printer cartridges.");
        recyclingTrie.addRecyclingTip("cds_dvds", "Recycle at electronic waste collection points.");
        recyclingTrie.addRecyclingTip("paint", "Take to a hazardous waste recycling center.");
        recyclingTrie.addRecyclingTip("motor_oil", "Take to a hazardous waste collection site.");
        recyclingTrie.addRecyclingTip("furniture", "Donate if in good condition, otherwise check local waste programs.");
        recyclingTrie.addRecyclingTip("clothing", "Donate to charity or take to textile recycling centers.");
        recyclingTrie.addRecyclingTip("shoes", "Donate if still usable or take to textile recycling bins.");
        recyclingTrie.addRecyclingTip("tvs", "Recycle at an electronic waste recycling center.");
        recyclingTrie.addRecyclingTip("computers", "Take to a designated e-waste facility.");
        recyclingTrie.addRecyclingTip("phones", "Recycle at an electronic waste recycling center.");
        recyclingTrie.addRecyclingTip("bicycles", "Donate to local charity or recycle at a bike recycling center.");
        recyclingTrie.addRecyclingTip("fabrics", "Take to textile recycling centers.");
        recyclingTrie.addRecyclingTip("carpet", "Check for local carpet recycling programs.");
        recyclingTrie.addRecyclingTip("mirrors", "Dispose of in specialized recycling centers.");
        recyclingTrie.addRecyclingTip("kitchenware", "Donate or check with local recycling centers.");
        recyclingTrie.addRecyclingTip("toys", "Donate if in good condition or recycle at a toy recycling center.");
        recyclingTrie.addRecyclingTip("furniture", "Donate if in good condition or check with local programs.");
        recyclingTrie.addRecyclingTip("batteries", "Recycle at designated collection points.");
        recyclingTrie.addRecyclingTip("tyres", "Check with local tire recycling programs.");
        recyclingTrie.addRecyclingTip("wood", "Can be recycled at specialized recycling facilities.");
        recyclingTrie.addRecyclingTip("plastics", "Separate types of plastic before recycling.");
        recyclingTrie.addRecyclingTip("straws", "Avoid using plastic straws, use alternatives like paper.");
        recyclingTrie.addRecyclingTip("plastic_cutlery", "Recycle if possible or avoid disposable plastic.");
        recyclingTrie.addRecyclingTip("styrofoam", "Take to specific recycling centers for styrofoam.");
        recyclingTrie.addRecyclingTip("wine_bottles", "Rinse and place in glass recycling bins.");
        recyclingTrie.addRecyclingTip("beer_bottles", "Rinse and recycle in glass bins.");
        recyclingTrie.addRecyclingTip("food_cans", "Rinse and recycle in the metal bin.");
        recyclingTrie.addRecyclingTip("pizza_boxes", "Recycle if free from food, otherwise compost.");
        recyclingTrie.addRecyclingTip("wrapping_paper", "Recycle if free from plastic and glitter.");
        recyclingTrie.addRecyclingTip("gift_bags", "Recycle paper gift bags, reuse plastic ones.");
        recyclingTrie.addRecyclingTip("shrink_wrap", "Take to a specialized plastic recycling center.");
        recyclingTrie.addRecyclingTip("bubble_wrap", "Check with local recycling centers.");
        recyclingTrie.addRecyclingTip("aluminum_foil", "Rinse and recycle in aluminum recycling bins.");
    }

    private static void initializeWasteProductionData() {
        wasteProductionData.put("A", 500);
        wasteProductionData.put("B", 750);
        wasteProductionData.put("C", 600);
        wasteProductionData.put("D", 900);
    }

    private static void initializeWasteCategories() {
        wasteCategories.put("plastic", "Recyclable");
        wasteCategories.put("glass", "Recyclable");
        wasteCategories.put("paper", "Recyclable");
        wasteCategories.put("food", "Organic");
        wasteCategories.put("electronics", "Hazardous");
        wasteCategories.put("metal", "Recyclable");
        wasteCategories.put("aluminum", "Recyclable");
        wasteCategories.put("steel", "Recyclable");
        wasteCategories.put("cardboard", "Recyclable");
        wasteCategories.put("wood", "Compostable");
        wasteCategories.put("rubber", "Non-Recyclable");
        wasteCategories.put("textiles", "Recyclable");
        wasteCategories.put("fabric", "Recyclable");
        wasteCategories.put("batteries", "Hazardous");
        wasteCategories.put("lightbulbs", "Hazardous");
        wasteCategories.put("cellphones", "Electronic Waste");
        wasteCategories.put("computers", "Electronic Waste");
        wasteCategories.put("fridges", "Electronic Waste");
        wasteCategories.put("microwaves", "Electronic Waste");
        wasteCategories.put("toiletries", "Organic");
        wasteCategories.put("fruit_peels", "Organic");
        wasteCategories.put("vegetable_scraps", "Organic");
        wasteCategories.put("flowers", "Organic");
        wasteCategories.put("coffee_grounds", "Organic");
        wasteCategories.put("eggshells", "Organic");
        wasteCategories.put("meat_bones", "Non-Recyclable");
        wasteCategories.put("chips_bags", "Non-Recyclable");
        wasteCategories.put("plastic_bottles", "Recyclable");
        wasteCategories.put("plastic_bags", "Non-Recyclable");
        wasteCategories.put("shoes", "Non-Recyclable");
        wasteCategories.put("car_tires", "Non-Recyclable");
        wasteCategories.put("glass_bottles", "Recyclable");
        wasteCategories.put("paper_towels", "Compostable");
        wasteCategories.put("napkins", "Compostable");
        wasteCategories.put("milk_cartons", "Recyclable");
        wasteCategories.put("juice_boxes", "Recyclable");
        wasteCategories.put("magazines", "Recyclable");
        wasteCategories.put("newspapers", "Recyclable");
        wasteCategories.put("books", "Recyclable");
        wasteCategories.put("paint_cans", "Hazardous");
        wasteCategories.put("oil_filters", "Hazardous");
        wasteCategories.put("cleaning_products", "Hazardous");
        wasteCategories.put("clothing", "Recyclable");
        wasteCategories.put("dishes", "Recyclable");
        wasteCategories.put("cutlery", "Recyclable");
        wasteCategories.put("cans", "Recyclable");
        wasteCategories.put("bottles", "Recyclable");
        wasteCategories.put("jars", "Recyclable");
        wasteCategories.put("packaging_materials", "Recyclable");
        wasteCategories.put("plastic_wrap", "Non-Recyclable");
        wasteCategories.put("styrofoam", "Non-Recyclable");
        wasteCategories.put("syringes", "Hazardous");
        wasteCategories.put("medicine", "Hazardous");
        wasteCategories.put("sharp_objects", "Hazardous");
        wasteCategories.put("furniture", "Non-Recyclable");
        wasteCategories.put("garden_tools", "Non-Recyclable");
    }

    
    private static void showMainMenu() {
        JFrame frame = new JFrame("Smart Waste Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLocationRelativeTo(null); // center on screen

        // Panel with vertical layout and padding
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250)); // light lavender
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Title
        JLabel title = new JLabel("Smart Waste Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 25, 112)); // dark blue
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Buttons with uniform style
        JButton citizenButton = createStyledButton("Citizen");
        JButton collectorButton = createStyledButton("Collector");
        JButton staffButton = createStyledButton("Waste Management Staff");
        JButton routesButton = createStyledButton("Display All Routes");
        JButton exitButton = createStyledButton("Exit");

        // Button actions
        citizenButton.addActionListener(e -> showCitizenDashboard());
        collectorButton.addActionListener(e -> showCollectorDashboard());
        staffButton.addActionListener(e -> showWasteManagementDashboard());
        routesButton.addActionListener(e -> displayAllRoutes());
        exitButton.addActionListener(e -> frame.dispose());

        // Add components to panel with spacing
        panel.add(title);
        panel.add(citizenButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(collectorButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(staffButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(routesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Helper method for styled buttons
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(0, 0, 0)); // steel blue
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // lighter blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }


    private static void showCitizenDashboard() {
        JFrame frame = new JFrame("Citizen Dashboard");
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null); // center on screen

        // Main panel with vertical layout and padding
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250)); // light lavender
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Title
        JLabel title = new JLabel("Citizen Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 25, 112)); // dark blue
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Buttons
        JButton feedbackButton = createStyledButton("Submit Feedback");
        JButton recyclingTipButton = createStyledButton("Get Recycling Tips");
        JButton backButton = createStyledButton("Back to Main Menu");

        // Button actions
        feedbackButton.addActionListener(e -> submitFeedback());
//        recyclingTipButton.addActionListener(e -> getRecyclingTips());
        recyclingTipButton.addActionListener(e -> showRecyclingTipUI());

        backButton.addActionListener(e -> frame.dispose());

        // Add components with spacing
        panel.add(title);
        panel.add(feedbackButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(recyclingTipButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }
    
    
    private static void submitFeedback() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setLayout(new BorderLayout(5, 5));

        JLabel label = new JLabel("Enter your feedback:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(25, 25, 112));

        JTextField textField = new JTextField(20);

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel, "Submit Feedback",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !textField.getText().trim().isEmpty()) {
            citizenFeedback.add(textField.getText().trim());
            JOptionPane.showMessageDialog(null, "Thank you for your feedback!", 
                    "Feedback Received", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void getRecyclingTips() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setLayout(new BorderLayout(5, 5));

        JLabel label = new JLabel("Enter item name for recycling tips:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(25, 25, 112));

        JTextField textField = new JTextField(20);

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel, "Recycling Tips",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !textField.getText().trim().isEmpty()) {
            String input = textField.getText().trim();
            
            // Get all tips that match the prefix
            List<String> tips = recyclingTrie.getTipsByPrefix(input);

            if (tips.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "No recycling tips found for \"" + input + "\".", 
                    "Recycling Tip", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Join all suggestions into one message
                String message = String.join("\n", tips);
                JOptionPane.showMessageDialog(null, message, "Recycling Tips", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private static void showRecyclingTipUI() {
        JFrame frame = new JFrame("Recycling Tips");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(5, 5));

        JTextField textField = new JTextField();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> suggestionList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(suggestionList);

        // Listen to typing
        textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateList(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateList(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateList(); }

            private void updateList() {
                String input = textField.getText().trim();
                listModel.clear();
                if (!input.isEmpty()) {
                    // Get matching materials (keys) from Trie
                    List<String> suggestions = recyclingTrie.getMaterialsByPrefix(input);
                    for (String material : suggestions) {
                        listModel.addElement(material);
                    }
                }
            }
        });

        // Show tip when user selects a material
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedMaterial = suggestionList.getSelectedValue();
                if (selectedMaterial != null) {
                    String tip = recyclingTrie.getRecyclingTip(selectedMaterial);
                    JOptionPane.showMessageDialog(frame, tip, "Recycling Tip", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        frame.add(textField, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }




    private static void showCollectorDashboard() {
    	
        JFrame frame = new JFrame("Collector Dashboard");
        frame.setSize(650, 600);
        frame.setLocationRelativeTo(null); // center on screen
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(230, 230, 250)); // light lavender
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Top panel: Inputs ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(new Color(230, 230, 250));

        JLabel startLabel = new JLabel("Start Area:");
        startLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        startLabel.setForeground(new Color(25, 25, 112));
        JTextField startField = new JTextField();

        JLabel destinationLabel = new JLabel("Destination Area:");
        destinationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        destinationLabel.setForeground(new Color(25, 25, 112));
        JTextField destinationField = new JTextField();

        JButton findRouteButton = createStyledButton("Find Optimized Route");

        inputPanel.add(startLabel);
        inputPanel.add(startField);
        inputPanel.add(destinationLabel);
        inputPanel.add(destinationField);
        inputPanel.add(new JLabel()); // empty cell
        inputPanel.add(findRouteButton);

        // --- Center panel: Graph ---
        GraphPanel graphPanel = new GraphPanel();
        graphPanel.setBackground(new Color(245, 245, 255)); // slightly lighter for contrast

        // --- Bottom panel: dashboard buttons ---
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(1, 3, 10, 0));
        dashboardPanel.setBackground(new Color(230, 230, 250));

        JButton communityProgramsButton = createStyledButton("Community Programs");
        JButton policyUpdatesButton = createStyledButton("Policy Updates");
        JButton backButton = createStyledButton("Back to Main Menu");

        dashboardPanel.add(communityProgramsButton);
        dashboardPanel.add(policyUpdatesButton);
        dashboardPanel.add(backButton);

        // --- Button Actions ---
        findRouteButton.addActionListener(e -> {
            String start = startField.getText().trim();
            String destination = destinationField.getText().trim();
            if (start.isEmpty() || destination.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both start and destination areas.");
                return;
            }
            List<String> route = areaGraph.getOptimizedRoute(start, destination);
            if (route.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No route found between " + start + " and " + destination);
            } else {
                JOptionPane.showMessageDialog(frame, "Optimized route: " + String.join(" -> ", route));
                graphPanel.setHighlightedRoute(route);
            }
        });

        communityProgramsButton.addActionListener(e -> viewCommunityPrograms());
        policyUpdatesButton.addActionListener(e -> viewPolicyUpdates());
        backButton.addActionListener(e -> frame.dispose());

        // --- Add panels to main panel ---
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(graphPanel, BorderLayout.CENTER);
        mainPanel.add(dashboardPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
 // View community programs
    private static void viewCommunityPrograms() {
        if (communityPrograms.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No active community programs.");
        } else {
            String programs = String.join("\n", communityPrograms);
            JOptionPane.showMessageDialog(null, "Community Waste Reduction Programs:\n" + programs);
        }
    }

    // View policy updates
    private static void viewPolicyUpdates() {
        if (policyUpdates.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No policy updates available.");
        } else {
            String policies = String.join("\n", policyUpdates);
            JOptionPane.showMessageDialog(null, "Waste Disposal Policy Updates:\n" + policies);
        }
    }

    // Add a new community program (for Staff dashboard)
    private static void addCommunityProgram() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setLayout(new BorderLayout(5, 5));

        JLabel label = new JLabel("Enter new community waste reduction program:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(25, 25, 112));
        JTextField textField = new JTextField(20);

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Community Program",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !textField.getText().trim().isEmpty()) {
            communityPrograms.add(textField.getText().trim());
            JOptionPane.showMessageDialog(null, "New community program added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Add a new policy update (for Staff dashboard)
    private static void addPolicyUpdate() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setLayout(new BorderLayout(5, 5));

        JLabel label = new JLabel("Enter new waste disposal policy update:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(25, 25, 112));
        JTextField textField = new JTextField(20);

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Policy Update",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !textField.getText().trim().isEmpty()) {
            policyUpdates.add(textField.getText().trim());
            JOptionPane.showMessageDialog(null, "New policy update added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private static void showCommunityProgramDashboard() {
        JFrame frame = new JFrame("Community Waste Reduction Program");
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel title = new JLabel("Community Programs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(25, 25, 112));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton viewProgramsButton = createStyledButton("View Community Programs");
        JButton addProgramButton = createStyledButton("Add New Program");
        JButton backButton = createStyledButton("Back to Main Menu");

        viewProgramsButton.addActionListener(e -> viewCommunityPrograms());
        addProgramButton.addActionListener(e -> addCommunityProgram());
        backButton.addActionListener(e -> frame.dispose());

        panel.add(title);
        panel.add(viewProgramsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addProgramButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showPolicyUpdateDashboard() {
        JFrame frame = new JFrame("Waste Disposal Policy Updates");
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel title = new JLabel("Policy Updates");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(25, 25, 112));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton viewPoliciesButton = createStyledButton("View Policy Updates");
        JButton addPolicyButton = createStyledButton("Add New Policy Update");
        JButton backButton = createStyledButton("Back to Main Menu");

        viewPoliciesButton.addActionListener(e -> viewPolicyUpdates());
        addPolicyButton.addActionListener(e -> addPolicyUpdate());
        backButton.addActionListener(e -> frame.dispose());

        panel.add(title);
        panel.add(viewPoliciesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addPolicyButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }
    
    private static void viewWasteCategories() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250));
        panel.setLayout(new BorderLayout(5, 5));

        JLabel label = new JLabel("Enter product name to check its waste category:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(25, 25, 112));
        JTextField textField = new JTextField(20);

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel, "Waste Categories",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && !textField.getText().trim().isEmpty()) {
            String category = wasteCategories.get(textField.getText().trim().toLowerCase());
            if (category != null) {
                JOptionPane.showMessageDialog(null, 
                    "The product '" + textField.getText().trim() + "' is categorized as: " + category,
                    "Waste Category", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Sorry, no category found for the product '" + textField.getText().trim() + "'.",
                    "Waste Category", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private static void showWasteManagementDashboard() {
        JFrame frame = new JFrame("Waste Management Dashboard");
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // center on screen

        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250)); // light lavender
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Title
        JLabel title = new JLabel("Waste Management Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 25, 112)); // dark blue
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Buttons
        JButton viewDataButton = createStyledButton("View Waste Production Data");
        JButton viewFeedbackButton = createStyledButton("View Citizen Feedback");
        JButton addRouteButton = createStyledButton("Add New Route");
        JButton communityProgramButton = createStyledButton("Community Waste Reduction Program");
        JButton policyUpdatesButton = createStyledButton("Waste Disposal Policy Updates");
        JButton backButton = createStyledButton("Back to Main Menu");

        // Button actions
        viewDataButton.addActionListener(e -> viewWasteProductionData());
        viewFeedbackButton.addActionListener(e -> viewCitizenFeedback());
        addRouteButton.addActionListener(e -> addNewRoute());
        communityProgramButton.addActionListener(e -> showCommunityProgramDashboard());
        policyUpdatesButton.addActionListener(e -> showPolicyUpdateDashboard());
        backButton.addActionListener(e -> frame.dispose());

        // Add buttons with spacing
        panel.add(title);
        panel.add(viewDataButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(viewFeedbackButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addRouteButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(communityProgramButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(policyUpdatesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void viewWasteProductionData() {
        StringBuilder data = new StringBuilder("Waste Production Data:\n");
        for (var entry : wasteProductionData.entrySet()) {
            data.append(entry.getKey()).append(": ").append(entry.getValue()).append(" kg\n");
        }
        JOptionPane.showMessageDialog(null, data.toString());
    }

    private static void viewCitizenFeedback() {
        String feedback = citizenFeedback.isEmpty() ? "No feedback submitted." : String.join("\n", citizenFeedback);
        JOptionPane.showMessageDialog(null, "Citizen Feedback:\n" + feedback);
    }

    private static void addNewRoute() {
        String startArea = JOptionPane.showInputDialog("Enter starting area:");
        String destinationArea = JOptionPane.showInputDialog("Enter destination area:");
        int distance = Integer.parseInt(JOptionPane.showInputDialog("Enter distance (in km):"));

        areaGraph.addArea(startArea);
        areaGraph.addArea(destinationArea);
        areaGraph.addRoute(startArea, destinationArea, distance);
        JOptionPane.showMessageDialog(null, "New route added successfully!");
    }

    private static void displayAllRoutes() {
        JFrame frame = new JFrame("All Routes");
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null); // center on screen
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        GraphPanel panel = new GraphPanel();
        panel.setBackground(new Color(245, 245, 245)); // light background
        frame.add(panel);
        
        frame.setVisible(true);
    }

    private static class GraphPanel extends JPanel {
        private final int nodeRadius = 40;
        private final Map<String, Point> nodePositions = new HashMap<>();
        private List<String> highlightedRoute = new ArrayList<>(); // Store the optimized route

        public void setHighlightedRoute(List<String> route) {
            highlightedRoute = route;
            repaint(); // Trigger re-render
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            calculateNodePositions();

            // Draw edges first
            g2d.setStroke(new BasicStroke(2));
            for (var entry : areaGraph.adjacencyList.entrySet()) {
                String from = entry.getKey();
                Point fromPoint = nodePositions.get(from);

                for (Route route : entry.getValue()) {
                    String to = route.area;
                    Point toPoint = nodePositions.get(to);
                    if (fromPoint != null && toPoint != null) {
                        boolean isHighlighted = isRouteHighlighted(from, to);
                        g2d.setColor(isHighlighted ? Color.RED : new Color(100, 100, 100));
                        g2d.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);

                        // Draw distance label
                        int midX = (fromPoint.x + toPoint.x) / 2;
                        int midY = (fromPoint.y + toPoint.y) / 2;
                        g2d.setColor(isHighlighted ? Color.RED.darker() : Color.DARK_GRAY);
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        g2d.drawString(route.distance + " km", midX, midY - 5);
                    }
                }
            }

            // Draw nodes on top
            for (var entry : nodePositions.entrySet()) {
                boolean isHighlighted = highlightedRoute.contains(entry.getKey());
                drawNode(g2d, entry.getKey(), entry.getValue(), isHighlighted);
            }
        }

        private void calculateNodePositions() {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = Math.min(getWidth(), getHeight()) / 3;
            int totalNodes = areaGraph.adjacencyList.size();
            int angleStep = 360 / totalNodes;

            int i = 0;
            for (String area : areaGraph.adjacencyList.keySet()) {
                int angle = i * angleStep;
                int x = centerX + (int) (radius * Math.cos(Math.toRadians(angle)));
                int y = centerY + (int) (radius * Math.sin(Math.toRadians(angle)));
                nodePositions.put(area, new Point(x, y));
                i++;
            }
        }

        private void drawNode(Graphics2D g2d, String label, Point position, boolean isHighlighted) {
            g2d.setColor(isHighlighted ? Color.RED : new Color(70, 130, 180)); // steel blue
            g2d.fillOval(position.x - nodeRadius / 2, position.y - nodeRadius / 2, nodeRadius, nodeRadius);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int stringWidth = fm.stringWidth(label);
            int stringHeight = fm.getAscent();
            g2d.drawString(label, position.x - stringWidth / 2, position.y + stringHeight / 4);
        }

        private boolean isRouteHighlighted(String from, String to) {
            for (int i = 0; i < highlightedRoute.size() - 1; i++) {
                if ((highlightedRoute.get(i).equals(from) && highlightedRoute.get(i + 1).equals(to)) ||
                    (highlightedRoute.get(i).equals(to) && highlightedRoute.get(i + 1).equals(from))) {
                    return true;
                }
            }
            return false;
        }
    }

    
    private static void setGlobalUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", new Color(245, 245, 245));
            UIManager.put("Panel.background", new Color(245, 245, 245));
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
