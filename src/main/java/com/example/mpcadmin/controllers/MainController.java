package com.example.mpcadmin.controllers;

import com.example.mpcadmin.models.Attempt;
import com.example.mpcadmin.models.Users;
import com.example.mpcadmin.models.Writing;
import com.example.mpcadmin.repo.AttemptRepository;
import com.example.mpcadmin.repo.UsersRepository;
import com.example.mpcadmin.repo.WritingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class MainController {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private WritingRepository writingRepository;
    @GetMapping("/")
    public String home(Model model) {
        List<Users> usersList = usersRepository.findAll();
        Collections.reverse(usersList);
        model.addAttribute("users", usersList);
        return "home";
    }

    @GetMapping("/users")
    @ResponseBody
    public List<Users> getUsers(@RequestParam String sort) {
        List<Users> usersList = usersRepository.findAll();

        if (sort.equals("alpha")) {
            Comparator<Users> usernameComparator = new Comparator<Users>() {
                public int compare(Users user1, Users user2) {
                    return user1.username.compareTo(user2.username);
                }
            };
            Collections.sort(usersList, usernameComparator);
        } else if (sort.equals("last")) {
            Collections.reverse(usersList);
        }

        return usersList;
    }

    @GetMapping("/about")
    public String about(Model model) {
        List<Users> usersList = usersRepository.findAll();
        Collections.reverse(usersList);
        model.addAttribute("users", usersList);
        return "about";
    }


    @GetMapping("/user/{id}")
    public String showUserPage(@PathVariable("id") Long userId, @RequestParam(name = "idattempt", required = false) Long idAttempt, Model model) {

            Optional<Users> user = usersRepository.findById(userId);
        List<Attempt> attempts = attemptRepository.findAllByidUsers(userId);
        for (int i=0; i<attempts.size();i++) {
            if (attempts.get(i).id_attempt==209) {
                Attempt obj = attempts.get(i);
                attempts.remove(i);
                attempts.add(0, obj);
            }
        }
        System.out.println(attempts.get(0).id_attempt);
        //List<Writing> writings = writingRepository.findAll();
        if (idAttempt==null) { idAttempt= Long.valueOf(attempts.get(0).getIdAttempt()); }
        System.out.println(attempts.get(0).getIdAttempt());
        List<Writing> writings = writingRepository.findAllByidAttempt(idAttempt);


        writings.removeIf(writing -> writing.text_writing_bot.equals("Сколько тебе лет?"));
        writings.removeIf(writing -> writing.text_writing_bot.equals("Ты мужчина или женщина?"));

        List<Object> nodes = new ArrayList<>();
        List<Object> links = new ArrayList<>();

        for (Writing writing : writings) {
            // Создание объекта node для записи Writing
            Map<String, Object> node = new HashMap<>();

            node.put("id", "1"+writing.id_writing);
            node.put("text", writing.text_writing_bot);
            if (writing.id_previous_writing!=null)
                node.put("class", "red");
            else
                node.put("class", "blue");
            nodes.add(node);

            node = new HashMap<>();
            Map<String, Object> link = new HashMap<>();
            if (writing.text_writing_user != null) {
                node.put("id", "2"+writing.id_writing);
                node.put("text", writing.text_writing_user);
                node.put("class", "green");
                nodes.add(node);
                link.put("source", "2"+writing.id_writing);
                link.put("target", "1"+writing.id_writing);
                links.add(link);
            }
            link = new HashMap<>();

            if (writing.id_previous_writing != null) {
                // Создание объекта link для связи между записями Writing
                link.put("source", "2"+writing.id_previous_writing);
                link.put("target", "1"+writing.id_writing);

                links.add(link);
            }

        }
        model.addAttribute("user", user.get());
        model.addAttribute("attempts", attempts);
        model.addAttribute("nodes", nodes);
        model.addAttribute("links", links);
        model.addAttribute("idAttempt", idAttempt);

        System.out.println(links);
        System.out.println(nodes);

        List<Map<String, Object>> graph =  createGraph(nodes, links);


        Long nodeStartId = Long.valueOf(0);
        for (Map<String, Object> node : graph)
            if (node.get("class").equals("blue"))
                nodeStartId = Long.parseLong(node.get("id").toString());

        List<String> verticesWithEmptyLinksIds = new ArrayList<>();

        for (Map<String, Object> vertex : graph) {
            List<Integer> linksEnd = (List<Integer>) vertex.get("links");
            if (linksEnd.isEmpty()) {
                verticesWithEmptyLinksIds.add((String) vertex.get("id"));
            }
        }
        int NPK = getNpk(graph);
        int NUK = getNuk(graph);
        int countAnswer = getCountAnswer(graph);
        double averagePathLength = findAverageShortestPathLength(graph, nodeStartId.toString(), verticesWithEmptyLinksIds);

        model.addAttribute("NPK", NPK);
        model.addAttribute("NUK", NUK);
        model.addAttribute("countAnswer", countAnswer);
        model.addAttribute("averagePathLength", averagePathLength);

        return "user";
    }

    public List<Map<String, Object>> createGraph(List<Object> nodes, List<Object> links) {
        List<Map<String, Object>> graph = new ArrayList<>();

        // добавляем все записи nodes в новый список graph
        for (Object nodeObj : nodes) {
            Map<String, Object> node = (Map<String, Object>) nodeObj;
            node.put("links", new ArrayList<Long>());
            graph.add(node);
        }

        // добавляем массив ссылок в каждую запись nodes
        for (Map<String, Object> node : graph) {
            Long nodeId = Long.parseLong(node.get("id").toString());
            String color = (String) node.get("class");
            List<Long> nodeLinks = (List<Long>) node.get("links");
            for (Object linkObj : links) {
                Map<String, Object> link = (Map<String, Object>) linkObj;
                Long source = Long.parseLong(link.get("source").toString());
                Long target = Long.parseLong(link.get("target").toString());

                int firstDigit = Integer.parseInt(String.valueOf(nodeId).substring(0, 1));
                int firstDigitSource = Integer.parseInt(String.valueOf(source).substring(0, 1));
                int firstDigitTarget = Integer.parseInt(String.valueOf(target).substring(0, 1));
                    if (nodeId.equals(source) || nodeId.equals(target)) {

                        if ((color.equals("blue") || color.equals("red"))&&firstDigitSource==1)
                            nodeLinks.add(target);
                        if ((color.equals("blue") || color.equals("red"))&&firstDigitSource==2)
                            nodeLinks.add(source);
                        if (color.equals("green") &&firstDigitSource==1)
                            nodeLinks.add(source);
                        if (color.equals("green") &&firstDigitSource==2)
                            nodeLinks.add(target);
                    }
            }
            try {
                Long minLink = Collections.min(nodeLinks);
                if (!color.equals("blue"))
                    nodeLinks.remove(minLink);
            }
            catch (Exception e) {}
            node.put("links", nodeLinks);
        }

        System.out.println(graph);
        return graph;
    }

    public int getNpk(List<Map<String, Object>> graph) {
        int count = 0;
        for (Map<String, Object> node : graph) {
            List<Long> links = (List<Long>) node.get("links");
            String nodeClass = (String) node.get("class");
            if (links.isEmpty() && "green".equals(nodeClass)) {
                count++;
            }
        }
        return count;
    }

    public int getNuk(List<Map<String, Object>> graph) {
        int count = 0;
        for (Map<String, Object> node : graph) {
            List<Long> links = (List<Long>) node.get("links");
            String nodeClass = (String) node.get("class");
            if (links.size()>1) {
                count++;
            }
        }
        System.out.println(count);
        return count;
    }

    public int getCountAnswer(List<Map<String, Object>> graph) {
        int count = 0;
        for (Map<String, Object> node : graph) {
            List<Long> links = (List<Long>) node.get("links");
            String nodeClass = (String) node.get("class");
            if ("green".equals(nodeClass)) {
                count++;
            }
        }
        return count;
    }

    public double findAverageShortestPathLength(List<Map<String, Object>> graph, String startVertex, List<String> endVertices) {
        // создаем словарь для хранения кратчайших расстояний от начальной вершины
        Map<String, Integer> shortestDistances = new HashMap<>();
        // добавляем начальную вершину в словарь и устанавливаем расстояние до нее равным 0
        shortestDistances.put(startVertex, 0);
        // создаем очередь для BFS
        Queue<String> queue = new LinkedList<>();
        queue.offer(startVertex);
        // выполняем BFS для поиска кратчайших путей от начальной вершины до всех остальных вершин
        while (!queue.isEmpty()) {
            String currentVertex = queue.poll();
            // находим все смежные вершины
            List<String> neighbors = ((List<Object>) graph.stream()
                    .filter(node -> node.get("id").equals(currentVertex))
                    .findFirst().get().get("links")).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            for (String neighbor : neighbors) {
                // если смежная вершина еще не была посещена, добавляем ее в очередь
                if (!shortestDistances.containsKey(neighbor)) {
                    shortestDistances.put(neighbor, shortestDistances.get(currentVertex) + 1);
                    queue.offer(neighbor);
                }
            }
        }

        // суммируем длины кратчайших путей до каждой конечной вершины
        double totalShortestPathLength = 0.0;
        for (String endVertex : endVertices) {
            if (shortestDistances.containsKey(endVertex)) {
                totalShortestPathLength += shortestDistances.get(endVertex);
            }
        }
        // находим среднее арифметическое длин всех возможных путей от начальной вершины до конечных
        double averageShortestPathLength = totalShortestPathLength / endVertices.size();
        return averageShortestPathLength;
    }


}

