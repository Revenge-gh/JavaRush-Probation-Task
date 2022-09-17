package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerFilters;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerRestController {

    private PlayerService playerService;

    @Autowired
    public PlayerRestController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getAllRegisteredPlayersList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.getAllRegisteredPlayersList(
                Specification.where(
                        PlayerFilters.nameFilter(name)
                        .and(PlayerFilters.titleFilter(title)))
                        .and(PlayerFilters.raceFilter(race))
                        .and(PlayerFilters.professionFilter(profession))
                        .and(PlayerFilters.birthdayFilter(after, before))
                        .and(PlayerFilters.bannedFilter(banned))
                        .and(PlayerFilters.experienceFilter(minExperience, maxExperience))
                        .and(PlayerFilters.levelFilter(minLevel, maxLevel)), pageable)
                .getContent();

    }


    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return playerService.getAllRegisteredPlayersList(
                Specification.where(
                                PlayerFilters.nameFilter(name)
                        .and(PlayerFilters.titleFilter(title)))
                        .and(PlayerFilters.raceFilter(race))
                        .and(PlayerFilters.professionFilter(profession))
                        .and(PlayerFilters.birthdayFilter(after, before))
                        .and(PlayerFilters.bannedFilter(banned))
                        .and(PlayerFilters.experienceFilter(minExperience, maxExperience))
                        .and(PlayerFilters.levelFilter(minLevel, maxLevel)))
                .size();

    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public Player createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable("id") String id) {
        Long iD = playerService.validateId(id);
        return playerService.getPlayer(iD);
    }

    @PostMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player updatePlayer(@PathVariable("id") String id, @RequestBody Player player) {
        Long iD = playerService.validateId(id);
        return playerService.updatePlayer(iD, player);
    }

    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable("id") String id) {
        Long iD = playerService.validateId(id);
        playerService.deletePlayer(iD);
    }

}
