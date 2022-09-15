package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class PlayerServiceImpl implements PlayerService {
    private final static Logger logger = LogManager.getLogger(PlayerServiceImpl.class);

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private void parameterChecker(Player player) {
        if (player.getName() != null && (player.getName().length() < 1 || player.getName().length() > 12)) {
            throw new BadRequestException("Character name is too long or missing.");
        }

        if (player.getTitle() != null && (player.getTitle().length() < 1 || player.getTitle().length() > 30)) {
            throw new BadRequestException("Character title is too long or missing.");
        }

        if (player.getExperience() != null && (player.getExperience() < 1 || player.getExperience() > 10000000)) {
            throw new BadRequestException("Character experience out of range.");
        }

        if (player.getBirthday() != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(player.getBirthday());
            if (date.get(Calendar.YEAR) < 2000 || date.get(Calendar.YEAR) > 3000) {
                throw new BadRequestException("Registration date out of range.");
            }
        }
    }

    @Override
    public Long idChecker(String id) {
        try {
            Long idLong = Long.parseLong(id);
            if (idLong <= 0) {
                throw new BadRequestException("ID is incorrect.");
            } else {
                return idLong;
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID isn't a number", e);
        }
    }

    private Integer calculateCurrentLevel(Player player) {
        return (int) ((Math.sqrt(2500 + 200 * player.getExperience())) - 50) / 100;
    }

    private Integer calculateUntilNextLevel(Player player) {
        return 50 * (calculateCurrentLevel(player) + 1) * (calculateCurrentLevel(player) + 2) - player.getExperience();

    }

    @Override
    public List<Player> getAllRegisteredPlayersList(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<Player> getAllRegisteredPlayersList(Specification<Player> specification, Pageable sortedByName) {
        return playerRepository.findAll(specification, sortedByName);
    }

    @Override
    public Player createPlayer(Player player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null) {
            throw new BadRequestException("Please fill in all required fields");
        }

        parameterChecker(player);

        if (player.getBanned() == null) {
            player.setBanned(false);
        }

        player.setLevel(calculateCurrentLevel(player));
        player.setUntilNextLevel(calculateUntilNextLevel(player));

        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Player updatePlayer(String idString, Player player) {
        logger.trace("Зашли в метод(trace)");
        logger.debug("так же зашли (debug)");

        //Если id не валидный
        Long id;
        if ((id = idChecker(idString)) == null)
            throw new BadRequestException("ID is incorrect.");

        //Если элемента с таким id нет в базе
        if (!playerRepository.existsById(id))
            throw new PlayerNotFoundException("Player is not found.");
        else {
            Player changedPlayer = playerRepository.findById(id).get();

            // Если тело запроса пустое
            if (player.getName() == null && player.getTitle() == null
                    && player.getRace() == null && player.getProfession() == null
                    && player.getBirthday() == null && player.getBanned() == null && player.getExperience() == null)
                //Возвращяем корабль из базы
                player = changedPlayer;
            else {
                //Если поля не валидные
                if ((player.getName() != null && (player.getName().length() < 1 || player.getName().length() > 12))
                        || (player.getTitle() != null && (player.getTitle().length() < 1 || player.getTitle().length() > 30))
                        || (player.getExperience() != null && (player.getExperience() < 1 || player.getExperience() > 10000000))
                        || (player.getBirthday() != null && (player.getBirthday().getYear() + 1900 < 2000 || player.getBirthday().getYear() + 1900 > 3000)))
                    throw new BadRequestException("Incorrect fields.");

                //Заменяем непустыми значениями
                if (player.getName() != null) changedPlayer.setName(player.getName());
                if (player.getTitle() != null) changedPlayer.setTitle(player.getTitle());
                if (player.getRace() != null) changedPlayer.setRace(player.getRace());
                if (player.getProfession() != null) changedPlayer.setProfession(player.getProfession());
                if (player.getBirthday() != null) changedPlayer.setBirthday(player.getBirthday());
                if (player.getBanned() != null) changedPlayer.setBanned(player.getBanned());
                if (player.getExperience() != null) changedPlayer.setExperience(player.getExperience());
                logger.info("Вычисление уровня");
                changedPlayer.setLevel(calculateCurrentLevel(changedPlayer));
                logger.info("Уровень вычислен " + changedPlayer.getLevel());
                changedPlayer.setUntilNextLevel(calculateUntilNextLevel(changedPlayer));

            }
            return playerRepository.save(changedPlayer);
        }
/*
        if (playerRepository.existsById(id)) {
            throw new PlayerNotFoundException("Player is not found.");
        } else {

            parameterChecker(player);

            Player changePlayer = playerRepository.findById(id).get();

            if (player.getName() != null) {
                changePlayer.setName(player.getName());
            }

            if (player.getTitle() != null) {
                changePlayer.setTitle(player.getTitle());
            }

            if (player.getRace() != null) {
                changePlayer.setRace(player.getRace());
            }

            if (player.getProfession() != null) {
                changePlayer.setProfession(player.getProfession());
            }

            if (player.getBirthday() != null) {
                changePlayer.setBirthday(player.getBirthday());
            }

            if (player.getBanned() != null) {
                changePlayer.setBanned(player.getBanned());
            }

            if (player.getExperience() != null) {
                changePlayer.setExperience(player.getExperience());
            }

       *//* Integer level = calculateCurrentLevel(player);
        changePlayer.setLevel(level);

        Integer untilNextLevel = calculateUntilNextLevel(player);
        changePlayer.setUntilNextLevel(untilNextLevel);*//*


            return playerRepository.save(changePlayer);
        }*/
    }

    @Override
    public void deletePlayer(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
        } else {
            throw new PlayerNotFoundException("Player is not found.");
        }
    }

    @Override
    public Player getPlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException("Player is not found.");
        }
        return playerRepository.findById(id).get();
    }

    @Override
    public Specification<Player> nameFilter(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Player> titleFilter(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> raceFilter(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> professionFilter(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> birthdayFilter(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                Date bf = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), bf);
            }
            if (before == null) {
                Date af = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), af);
            }

            Date bf = new Date(before - 3600001);
            Date af = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), af, bf);
        };
    }

    @Override
    public Specification<Player> bannedFilter(Boolean banned) {
        return (root, query, criteriaBuilder) -> {
            if (banned == null) {
                return null;
            }

            if (banned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else {
                return criteriaBuilder.isFalse(root.get("banned"));
            }
        };
    }

    @Override
    public Specification<Player> experienceFilter(Integer minExperience, Integer maxExperience) {
        return (root, query, criteriaBuilder) -> {
            if (minExperience == null && maxExperience == null) {
                return null;
            }

            if (minExperience == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience);
            }

            if (maxExperience == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience);
            }

            return criteriaBuilder.between(root.get("experience"), minExperience, maxExperience);
        };
    }

    @Override
    public Specification<Player> levelFilter(Integer minLevel, Integer maxLevel) {
        return (root, query, criteriaBuilder) -> {
            if (minLevel == null && maxLevel == null) {
                return null;
            }

            if (minLevel == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel);
            }

            if (maxLevel == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel);
            }

            return criteriaBuilder.between(root.get("level"), minLevel, maxLevel);
        };
    }
}
