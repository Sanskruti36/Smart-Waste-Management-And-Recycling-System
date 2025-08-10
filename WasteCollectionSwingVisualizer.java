package project2;

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

public class WasteCollectionSwingVisualizer{

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

        public String getRecyclingTip(String item) {
            TrieNode current = root;
            for (char c : item.toLowerCase().toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return "No recycling tips available for this item.";
                }
                current = current.children.get(c);
            }
            return current.recyclingTip != null ? current.recyclingTip : "No specific tip available for this item.";
        }
    }

    private static final Graph areaGraph = new Graph();
    private static final RecyclingTrie recyclingTrie = new RecyclingTrie();
    private static final Map<String, Integer> wasteProductionData = new HashMap<>();
    private static final Map<String, String> wasteCategories = new HashMap<>();
    private static final List<String> citizenFeedback = new ArrayList<>();
    private static final List<String> communityPrograms = new ArrayList<>();
    private static final List<String> policyUpdates = new ArrayList<>();


    public static void main(String[] args) {
        initializeData();
        initializeWasteCategories();
        SwingUtilities.invokeLater(WasteCollectionSwingVisualizer::showMainMenu);
    }
    
    
   
    private static void initializeData() {
        initializeGraph();
        initializeRecyclingTips();
        initializeWasteCategories();
        initializeCommunityAndPolicyData(); // Initialize new data
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
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 1));

        JButton citizenButton = new JButton("Citizen");
        JButton collectorButton = new JButton("Collector");
        JButton staffButton = new JButton("Waste Management Staff");
        JButton routesButton = new JButton("Display All Routes");
        JButton exitButton = new JButton("Exit");

        citizenButton.addActionListener(e -> showCitizenDashboard());
        collectorButton.addActionListener(e -> showCollectorDashboard());
        staffButton.addActionListener(e -> showWasteManagementDashboard());
        routesButton.addActionListener(e -> displayAllRoutes());
        exitButton.addActionListener(e -> frame.dispose());

        frame.add(citizenButton);
        frame.add(collectorButton);
        frame.add(staffButton);
        frame.add(routesButton);
        frame.add(exitButton);

        frame.setVisible(true);
    }

    private static void showCitizenDashboard() {
        JFrame frame = new JFrame("Citizen Dashboard");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));

        JButton feedbackButton = new JButton("Submit Feedback");
        JButton recyclingTipButton = new JButton("Get Recycling Tips");
        JButton communityProgramsButton = new JButton("View Community Programs"); // New button
        JButton policyUpdatesButton = new JButton("View Policy Updates");
        JButton wasteCategoriesButton = new JButton("View Waste Categories"); 
        JButton backButton = new JButton("Back to Main Menu");

        feedbackButton.addActionListener(e -> submitFeedback());
        recyclingTipButton.addActionListener(e -> getRecyclingTips());
        communityProgramsButton.addActionListener(e -> viewCommunityPrograms()); // Action for community programs
        policyUpdatesButton.addActionListener(e -> viewPolicyUpdates());
        wasteCategoriesButton.addActionListener(e -> viewWasteCategories());
        backButton.addActionListener(e -> frame.dispose());

        frame.add(feedbackButton);
        frame.add(recyclingTipButton);
        frame.add(communityProgramsButton); // Add new button
        frame.add(policyUpdatesButton);
        frame.add(wasteCategoriesButton);
        frame.add(backButton);

        frame.setVisible(true);
    }
    
    private static void viewWasteCategories() {
        String product = JOptionPane.showInputDialog("Enter a product name to check its waste category:");

        if (product != null && !product.isEmpty()) {
            // Check if the product exists in the waste categories map
            String category = wasteCategories.get(product.toLowerCase());
            
            if (category != null) {
                JOptionPane.showMessageDialog(null, "The product '" + product + "' is categorized as: " + category);
            } else {
                JOptionPane.showMessageDialog(null, "Sorry, no category found for the product '" + product + "'.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a valid product name.");
        }
    }

    private static void submitFeedback() {
        String feedback = JOptionPane.showInputDialog("Enter your feedback:");
        if (feedback != null && !feedback.isEmpty()) {
            citizenFeedback.add(feedback);
            JOptionPane.showMessageDialog(null, "Thank you for your feedback!");
        }
    }

    private static void getRecyclingTips() {
        String item = JOptionPane.showInputDialog("Enter item name for recycling tips:");
        if (item != null && !item.isEmpty()) {
            String tip = recyclingTrie.getRecyclingTip(item);
            JOptionPane.showMessageDialog(null, tip);
        }
    }

    
    
 // Method to initialize programs and policy updates
    private static void initializeCommunityAndPolicyData() {
        // Example community programs
        communityPrograms.add("Plastic Waste Reduction Campaign - Organizing community cleanups every weekend.");
        communityPrograms.add("Composting Program - Encouraging households to compost organic waste.");

        // Example policy updates
        policyUpdates.add("New Policy on Electronic Waste - All e-waste should be dropped off at designated collection points.");
        policyUpdates.add("Ban on Single-Use Plastics - Effective from next month, single-use plastics will be banned in public spaces.");
    }

    // Method to view community programs
    private static void viewCommunityPrograms() {
        if (communityPrograms.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No active community programs.");
        } else {
            String programs = String.join("\n", communityPrograms);
            JOptionPane.showMessageDialog(null, "Community Waste Reduction Programs:\n" + programs);
        }
    }

    // Method to view policy updates
    private static void viewPolicyUpdates() {
        if (policyUpdates.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No policy updates available.");
        } else {
            String policies = String.join("\n", policyUpdates);
            JOptionPane.showMessageDialog(null, "Waste Disposal Policy Updates:\n" + policies);
        }
    }

    // Method to add a new community program
    private static void addCommunityProgram() {
        String program = JOptionPane.showInputDialog("Enter new community waste reduction program:");
        if (program != null && !program.isEmpty()) {
            communityPrograms.add(program);
            JOptionPane.showMessageDialog(null, "New community program added successfully!");
        }
    }

    // Method to add a new policy update
    private static void addPolicyUpdate() {
        String update = JOptionPane.showInputDialog("Enter new waste disposal policy update:");
        if (update != null && !update.isEmpty()) {
            policyUpdates.add(update);
            JOptionPane.showMessageDialog(null, "New policy update added successfully!");
        }
    }

 

    private static void showWasteManagementDashboard() {
        JFrame frame = new JFrame("Waste Management Dashboard");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));

        
        JButton viewFeedbackButton = new JButton("View Citizen Feedback");
        JButton addRouteButton = new JButton("Add New Route");
        JButton backButton = new JButton("Back to Main Menu");
        JButton communityProgramButton = new JButton("Community Waste Reduction Program");
        JButton policyUpdatesButton = new JButton("Waste Disposal Policy Updates");

        
        viewFeedbackButton.addActionListener(e -> viewCitizenFeedback());
        addRouteButton.addActionListener(e -> addNewRoute());
        backButton.addActionListener(e -> frame.dispose());
        communityProgramButton.addActionListener(e -> showCommunityProgramDashboard());
        policyUpdatesButton.addActionListener(e -> showPolicyUpdateDashboard());
        
        frame.add(viewFeedbackButton);
        frame.add(addRouteButton);
        frame.add(communityProgramButton);
        frame.add(policyUpdatesButton);
        frame.add(backButton);
        

        frame.setVisible(true);
    }
    
    private static void showCommunityProgramDashboard() {
        JFrame frame = new JFrame("Community Waste Reduction Program");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));

        JButton viewProgramsButton = new JButton("View Community Programs");
        JButton addProgramButton = new JButton("Add New Program");
        JButton backButton = new JButton("Back to Main Menu");

        viewProgramsButton.addActionListener(e -> viewCommunityPrograms());
        addProgramButton.addActionListener(e -> addCommunityProgram());
        backButton.addActionListener(e -> frame.dispose());

        frame.add(viewProgramsButton);
        frame.add(addProgramButton);
        frame.add(backButton);

        frame.setVisible(true);
    }

    private static void showPolicyUpdateDashboard() {
        JFrame frame = new JFrame("Waste Disposal Policy Updates");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));

        JButton viewPoliciesButton = new JButton("View Policy Updates");
        JButton addPolicyButton = new JButton("Add New Policy Update");
        JButton backButton = new JButton("Back to Main Menu");

        viewPoliciesButton.addActionListener(e -> viewPolicyUpdates());
        addPolicyButton.addActionListener(e -> addPolicyUpdate());
        backButton.addActionListener(e -> frame.dispose());

        frame.add(viewPoliciesButton);
        frame.add(addPolicyButton);
        frame.add(backButton);

        frame.setVisible(true);
    }

    private static class GraphPanel extends JPanel {
        private final int nodeRadius = 30;
        private final int panelPadding = 50;

        // Store calculated node positions to reuse across different paints
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

            // Draw edges
            for (var entry : areaGraph.adjacencyList.entrySet()) {
                String from = entry.getKey();
                Point fromPoint = nodePositions.get(from);

                for (Route route : entry.getValue()) {
                    String to = route.area;
                    Point toPoint = nodePositions.get(to);

                    if (toPoint != null && fromPoint != null) {
                        boolean isHighlighted = isRouteHighlighted(from, to);
                        drawEdge(g2d, from, to, route.distance, fromPoint, toPoint, isHighlighted);
                    }
                }
            }

            // Draw nodes
            for (var entry : nodePositions.entrySet()) {
                String label = entry.getKey();
                Point position = entry.getValue();
                boolean isHighlighted = highlightedRoute.contains(label);
                drawNode(g2d, label, position, isHighlighted);
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
            g2d.setColor(isHighlighted ? Color.RED : Color.BLUE);
            g2d.fillOval(position.x - nodeRadius / 2, position.y - nodeRadius / 2, nodeRadius, nodeRadius);
            g2d.setColor(Color.WHITE);
            g2d.drawString(label, position.x - 5, position.y + 5);
        }

        private void drawEdge(Graphics2D g2d, String from, String to, int distance, Point fromPoint, Point toPoint, boolean isHighlighted) {
            g2d.setColor(isHighlighted ? Color.RED : Color.BLACK);
            g2d.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);

            int midX = (fromPoint.x + toPoint.x) / 2;
            int midY = (fromPoint.y + toPoint.y) / 2;
            g2d.setColor(Color.BLACK);
            g2d.drawString(distance + " km", midX, midY);
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
    
    

    private static void showCollectorDashboard() {
        JFrame frame = new JFrame("Collector Dashboard");
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField startField = new JTextField();
        JTextField destinationField = new JTextField();
        JButton findRouteButton = new JButton("Find Optimized Route");
        
        inputPanel.add(new JLabel("Start Area:"));
        inputPanel.add(startField);
        inputPanel.add(new JLabel("Destination Area:"));
        inputPanel.add(destinationField);
        inputPanel.add(findRouteButton);

        GraphPanel graphPanel = new GraphPanel();

        findRouteButton.addActionListener(e -> {
            String start = startField.getText().trim();
            String destination = destinationField.getText().trim();
            List<String> route = areaGraph.getOptimizedRoute(start, destination);
            if (route.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No route found between " + start + " and " + destination);
            } else {
                JOptionPane.showMessageDialog(frame, "Optimized route: " + String.join(" -> ", route));
                graphPanel.setHighlightedRoute(route);
            }
        });

        JPanel dashboardPanel = new JPanel(new GridLayout(3, 1));
        JButton communityProgramsButton = new JButton("View Community Programs"); // New button
        JButton policyUpdatesButton = new JButton("View Policy Updates"); // New button
        JButton backButton = new JButton("Back to Main Menu");


        communityProgramsButton.addActionListener(e -> viewCommunityPrograms()); // Action for community programs
        policyUpdatesButton.addActionListener(e -> viewPolicyUpdates()); // Action for policy updates
        backButton.addActionListener(e -> frame.dispose());
        dashboardPanel.add(communityProgramsButton);
        dashboardPanel.add(policyUpdatesButton);
        dashboardPanel.add(backButton);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(graphPanel, BorderLayout.CENTER);
        frame.add(dashboardPanel, BorderLayout.SOUTH); 
       


        frame.setVisible(true);
    }

    private static void displayAllRoutes() {
        JFrame frame = new JFrame("All Routes");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new GraphPanel());
        frame.setVisible(true);
    }
    
    
}
