package rikkei.academy.service;

import rikkei.academy.model.Song;

import java.util.List;

public interface ISongService {
    List<Song> findAll();

    void save(Song song);

    Song findById(int id);

    void remove(int id);
}
