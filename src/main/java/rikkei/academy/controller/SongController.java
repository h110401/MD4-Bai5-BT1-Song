package rikkei.academy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rikkei.academy.model.Song;
import rikkei.academy.model.SongForm;
import rikkei.academy.service.ISongService;

import java.io.File;
import java.io.IOException;

@Controller
@PropertySource("classpath:upload_file.properties")
public class SongController {

    @Autowired
    private ISongService songService;

    @GetMapping({"/", "/song"})
    public String showList(Model model) {
        model.addAttribute("songList", songService.findAll());
        return "index";
    }

    @GetMapping({"/create"})
    public String formCreate(Model model) {
        model.addAttribute("song", new SongForm());
        return "create";
    }

    @Value("${file-upload}")
    private String fileUpload;

    @PostMapping("/save")
    public String save(SongForm songForm, RedirectAttributes redirectAttributes) {
        MultipartFile file = songForm.getFile();
        String message;
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            try {
                FileCopyUtils.copy(songForm.getFile().getBytes(), new File(fileUpload + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Song song = new Song(songForm.getId(), songForm.getName(), songForm.getSinger(), songForm.getCategory(), "/song/" + fileName);
            songService.save(song);
            message = "Create song successfully!";
        } else {
            message = "Please input a song!";
        }
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/create";
    }

    @GetMapping("/play/{id}")
    public String playSong(@PathVariable int id, Model model) {
        Song song = songService.findById(id);
        model.addAttribute("song", song);
        return "play";
    }

    @GetMapping("/edit/{id}")
    public String formEdit(@PathVariable int id, Model model) {
        model.addAttribute("songEdit", songService.findById(id));
        return "edit";
    }

    @PostMapping("/update")
    public String update(Song song) {
        songService.save(song);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String formDelete(@PathVariable int id, Model model) {
        model.addAttribute("id", id);
        return "delete";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam int id, @RequestParam String submit) {
        if (submit.equals("Yes")) {
            songService.remove(id);
        }
        return "redirect:/";
    }
}
