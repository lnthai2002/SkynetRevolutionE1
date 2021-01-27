package darkportal.skynetrevolutione1;

import java.util.*;
import java.util.stream.Collectors;

class Player {

    public static class Node implements Comparable<Node>{
        Integer nodeVal;
        List<Node> adjacentNodes = new LinkedList<>();
        boolean gateway = false;

        public Node(Integer val){
            nodeVal = val;
        }

        public void addAdjacentNode(Node aNode) {
            adjacentNodes.add(aNode);
        }

        public void setGateway() {
            gateway = true;
        }

        public boolean isGateway() {
            return gateway;
        }

        public Integer getVal() {
            return nodeVal;
        }

        public List<Node> getAdjs() {
            return adjacentNodes;
        }

        public String toString() {
            String adjs = adjacentNodes.stream()
                    .map(n -> n.getVal().toString())
                    .collect(Collectors.joining(","));
            return nodeVal + (gateway ? " is Gateway" : "") + " linked to " + adjs;
        }

        @Override
        public int compareTo(Player.Node o) {
            return this.nodeVal.compareTo(o.getVal());
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the total number of nodes in the level, including the gateways

        Map<Integer, Node> nodes = new HashMap<>();

        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways
        for (int i = 0; i < L; i++) {
            int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            Node n1 = nodes.get(N1);
            if (n1 == null) {
                n1 = new Node(N1);
                nodes.put(N1, n1);
            }

            int N2 = in.nextInt();
            Node n2 = nodes.get(N2);
            if (n2 == null) {
                n2 = new Node(N2);
                nodes.put(N2, n2);
            }

            n1.addAdjacentNode(n2);
            n2.addAdjacentNode(n1);
        }
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            Node gw = nodes.get(EI);
            if(gw != null){
                gw.setGateway();
            }
            else {
                throw new RuntimeException("No such node to set as gateway");
            }
        }

        //debug
        for (Node n : nodes.values()) {
            System.err.println(n);
        }

        // game loop
        while (true) {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn

            // Example: 0 1 are the indices of the nodes you wish to sever the link between
            //System.out.println("0 1");
            System.out.println(bfs(SI, nodes));
        }
    }

    public static String bfs(Integer startFrom, Map<Integer, Node> nodes) {
        List<Integer> visited = new ArrayList<>();
        Queue<Integer> toVisited = new LinkedList<>();

        Node root = nodes.get(startFrom);
        toVisited.add(root.getVal());

        while (!toVisited.isEmpty()) {
            //checking phase: open the first node in the list to be explored and see if it is what we are looking for
            Node cur = nodes.get(toVisited.remove());
            System.err.println("Root: " + root.getVal() + " Cur: " + cur.getVal());

            visited.add(cur.getVal());
            if (cur.isGateway()) {
                //disconnect the gateway from the last node
                root.getAdjs().remove(cur);
                cur.getAdjs().remove(root);

                return root.getVal() + " " + cur.getVal();
            }

            //exploring phase: add the adjacent nodes of the current node to list to be discovered
            List<Node> adjs = cur.getAdjs();
            Collections.sort(adjs); //to ease debug/tracing, i want to visit nodes in a specific order
            for (Node adj : adjs) {
                int adjVal = adj.getVal();
                if (!visited.contains(adjVal) && !toVisited.contains(adjVal)) {
                    toVisited.add(adjVal);
                }
            }
            System.err.println("toVisit " + toVisited.stream().map(n -> n.toString()).collect(Collectors.joining(", ")));
        }
        return null;
    }
}