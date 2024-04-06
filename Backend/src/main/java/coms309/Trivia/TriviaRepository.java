package coms309.Trivia;

import coms309.Profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TriviaRepository extends JpaRepository<Trivia, Long> {

    Trivia findById(int id);

    boolean existsByQuestion(String question);

    boolean existsById(int id);

    @Query(value = "select id from trivia.trivia", nativeQuery = true)
    List<Integer> getAllIds();

    @Query(value = "select id from trivia.trivia where question_theme = :theme", nativeQuery = true)
    List<Integer> getAllIdsByQuestionTheme(@Param("theme") String theme);

    @Query(value = "select * from trivia.trivia where question_theme = ?1", nativeQuery = true)
    List<Trivia> findAllByQuestionTheme(String questionTheme);
}
