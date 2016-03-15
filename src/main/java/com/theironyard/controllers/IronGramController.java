package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by alexanderhughes on 3/15/16.
 */
@RestController
public class IronGramController {
    @Autowired
    UserRepository users;
    @Autowired
    PhotoRepository photos;

    Server dbui = null;

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findByName(username);
        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(password, user.getPasswordHash())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("userName", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        return users.findByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(int timeToView, String recipient, MultipartFile photo, HttpServletResponse response, HttpSession session) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            throw new Exception("Not logged in");
        }
        User sender = users.findByName(userName);
        File photoFile = File.createTempFile("image", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());
        Photo p = new Photo(sender, (users.findByName(recipient) == null) ? null : users.findByName(recipient), photoFile.getName(), timeToView);
        photos.save(p);
        response.sendRedirect("/");
        return p;
    }
    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public List<Photo> showPhotos(HttpSession session) {
        User user = users.findByName((String) session.getAttribute("userName"));
        List<Photo> list = (List<Photo>) photos.findAll();
        List<Photo> receiptList = photos.findByRecipient(user);
        for (Photo photo : list) {
            if (photo.getRecipient() == null) {
                receiptList.add(photo);
            }
        }
        for (Photo photo : receiptList) {
            if (photo.getTime() == null) {
                photo.setTime(LocalDateTime.now());
                photos.save(photo);
            }
            else if (LocalDateTime.now().isAfter(photo.getTime().plusSeconds(photo.getTimeToView()))) {
                photos.delete(photo);
                File f = new File("public/" + photo.getFileName());
                f.delete();
            }
        }
        return receiptList;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session) {
        session.invalidate();
    }
}