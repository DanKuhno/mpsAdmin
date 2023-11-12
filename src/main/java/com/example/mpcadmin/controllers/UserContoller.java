package com.example.mpcadmin.controllers;

import com.example.mpcadmin.graphClass.Node;
import com.example.mpcadmin.models.Attempt;
import com.example.mpcadmin.models.Users;
import com.example.mpcadmin.models.Writing;
import com.example.mpcadmin.repo.AttemptRepository;
import com.example.mpcadmin.repo.WritingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.mpcadmin.repo.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path="/user")
public class UserContoller {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private WritingRepository writingRepository;

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<Users> usersList = usersRepository.findAll();
        List<Attempt> attemptList = attemptRepository.findAll();


        List<Writing> writingList = writingRepository.findAll();
        model.addAttribute("users", usersList);
        model.addAttribute("attempt", attemptList);
        model.addAttribute("writing", writingList);
        return "users";
    }

    @GetMapping("/graph")
    public String getGraph(Model model) {
        List<Writing> writingList = writingRepository.findAll();
        ArrayList<Node> nodes = new ArrayList<Node>() {
        };
        for (int i=0; i<writingList.size(); i++)
        {
            if (writingList.get(i).idAttempt == 22) {
                Writing a = writingList.get(i);
                try {
                    Node b = new Node(a.id_writing, a.id_previous_writing, a.text_writing_user);
                    nodes.add(b);
                }
                catch (Exception e) {
                    Node b = new Node(a.id_writing, -1, a.text_writing_user);
                    nodes.add(b);
                }

            }
        }
        //for (int i=0; i<nodes.size();i++)
        //    System.out.println(nodes.get(i).id_writing+" "+nodes.get(i).id_previous+" "+nodes.get(i).text_writing);
        model.addAttribute("nodess", nodes);
        return "graph";
    }


}
