package com.knu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//variant 2
public class TheoryOfAutomata {
    public static void main(String[] args) throws FileNotFoundException {

        int a;
        int s;
        int s0;
        Set<Integer> finalStates = new HashSet<>();
        Set<String> alphabet = new HashSet<>();
        Set<Integer> unattainableStates = new HashSet<>();
        Set<Integer> deadlocks = new HashSet<>();

        File file = new File("automataConfig.txt");

        try (Scanner scanner = new Scanner(file)) {
            a = Integer.parseInt(scanner.nextLine());
            s = Integer.parseInt(scanner.nextLine());
            s0 = Integer.parseInt(scanner.nextLine());

            int[] finalStatesFromFile = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            for (int finalState : finalStatesFromFile) {
                finalStates.add(finalState);
            }

            Graph graph = new Graph(s);

            while (scanner.hasNextLine()) {

                String[] row = scanner.nextLine().split(" ");

                int start = Integer.parseInt(row[0]);
                int finish = Integer.parseInt(row[2]);

                if(start > s || start < 0){
                    System.out.println("Wrong input: state is out of possible states. State: " + start);
                    return;
                }

                if(finish > s || finish < 0){
                    System.out.println("Wrong input: state is out of possible states. State: " + finish);
                    return;
                }

                alphabet.add(row[1]);
                graph.addEdge(start, finish);
            }

            if(alphabet.size() > a){
                System.out.println("Wrong input: power of alphabet is bigger than given.\n" +
                        "Given power of alphabet: " + a + "\n" +
                        "Current power of alphabet: " + alphabet.size());
                return;
            }

            findUnattainableStates(graph, unattainableStates, finalStates, s0);
            System.out.println("Unattainable states: " + Collections.singletonList(unattainableStates));
            findDeadlocks(graph, finalStates, unattainableStates, deadlocks);
            System.out.println("Deadlocks: " + Collections.singletonList(deadlocks));
        }
    }

    public static void findUnattainableStates(Graph graph, Set<Integer> unattainableStates, Set<Integer> finalStates, int s0) {

        findAttainableStates(graph, unattainableStates, s0);

        for (int i = 0; i < graph.numOfVertices; ++i) {
            if(unattainableStates.contains(i)){
                unattainableStates.remove(i);
            }
            else{
                unattainableStates.add(i);
            }
        }

        for (int unattainableState: unattainableStates) {
            finalStates.remove(unattainableState);
        }
    }

    public static void findAttainableStates(Graph graph, Set<Integer> attainableStates, int s0) {
        attainableStates.add(s0);
        for (int i = 0; i < graph.numOfVertices; i++) {
            if(graph.isAttainable(s0, i)){
                attainableStates.add(i);
            }
        }
    }

    public static void findDeadlocks(Graph graph, Set<Integer> finalStates, Set<Integer> unattainableStates, Set<Integer> deadlocks){

        for (int i = 0; i < graph.numOfVertices; i++) {
            deadlocks.add(i);
        }

        for (Integer unattainableState: unattainableStates) {
            deadlocks.remove(unattainableState);
        }

        for (Integer finalState: finalStates) {
            for (int i = 0; i < graph.numOfVertices; i++) {
                if(graph.isAttainable(i, finalState) || i == finalState){
                    deadlocks.remove(i);
                }
            }
        }
    }

    static class Graph {
        private final int numOfVertices;
        private final LinkedList<Integer>[] adjacency;

        Graph(int s) {
            numOfVertices = s;
            adjacency = new LinkedList[s];
            for (int i = 0; i < s; ++i)
                adjacency[i] = new LinkedList();
        }

        void addEdge(int start, int finish) {
            adjacency[start].add(finish);
        }

        boolean isAttainable(int start, int finish) {

            boolean[] visited = new boolean[numOfVertices];

            LinkedList<Integer> queue = new LinkedList<>();

            visited[start] = true;
            queue.add(start);

            Iterator<Integer> i;
            while (!queue.isEmpty()) {

                start = queue.poll();

                int n;
                i = adjacency[start].listIterator();

                while (i.hasNext()) {
                    n = i.next();

                    if (n == finish)
                        return true;

                    if (!visited[n]) {
                        visited[n] = true;
                        queue.add(n);
                    }
                }
            }
            return false;
        }
    }

}
