package coms309.Trivia;

import coms309.Profile.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@RestController
class TriviaController {

    @Autowired
    TriviaRepository triviaRepository;

    /**
     *         end point for creating a trivia question
     *
     * @param  trivia: JSON containing new trivia info
     * @return "success" if trivia question was created
     * 		   "failure" if question, answer choice or answer index was not included
     *         "exists" if question is already in use
     */
    @PostMapping("/trivia")
    public String createTriviaQuestion(@RequestBody Trivia trivia ) {
        if(trivia.getQuestion() == null || trivia.getAnswerIndex() == 0 || trivia.getAnswerChoices() == null || trivia.getQuestionTheme() == null) {
            return "failure";
        }
        else if (triviaRepository.existsByQuestion(trivia.getQuestion())) {
            return "exists";
        }

        triviaRepository.save(trivia);
        return "success";
    }

    /**
     *         end point getting a trivia question by id
     *
     * @param  id: id of the question that is being retrieved
     * @return trivia object if trivia question exists
     * 		   null if trivia question does not exist
     */
    @GetMapping("/trivia/{id}")
    public Trivia getTriviaById(@PathVariable String id) {
        if(triviaRepository.existsById(Integer.parseInt(id))) {
            return triviaRepository.findById(Integer.parseInt(id));
        }
        return null;
    }

    /**
     * 		   end point used for updating Trivia Questions
     *
     * @param  id: primary key lookup
     * @param  request: JSON containing values to change
     * @return "success" on profile update
     * 		   "failure" if request is null
     */
    @PutMapping("/trivia/{id}")
    public String putTrivia(@PathVariable String id, @RequestBody Trivia request) {
        Trivia user = triviaRepository.findById(Integer.parseInt(id));

        if(request == null) {
            return "failure";
        }

        user.updateTrivia(request);
        triviaRepository.save(user);

        return "success";
    }

    /**
     * 		   end point to get random Trivia Questions
     *
     * @param  num: number of random questions
     * @return List of random questions
     */
    @GetMapping("trivia/random/{num}")
    public List<Trivia> getRandomQuestion(@PathVariable String num) {
        int n = Integer.parseInt(num);
        if(n >= triviaRepository.count()) {
            return triviaRepository.findAll();
        }
        List<Integer> allId = triviaRepository.getAllIds();
        HashMap<Integer, Trivia> randomQuestion = new HashMap<>();

        for(int i = 0; i < n; i++) {
            Random rand = new Random();
            int qId = allId.get(rand.nextInt(allId.size()));
            while(randomQuestion.containsKey(qId)) {
                qId = (int) allId.get(rand.nextInt(n));
            }
            randomQuestion.put(qId, triviaRepository.findById(qId));
        }
        List<Trivia> list = new ArrayList<Trivia>(randomQuestion.values());
        return list;
    }

    /**
     * 		   end point to get random Trivia Questions of a specific question type
     *
     * @param  theme: theme of the questions
     * @param  num: number of random questions
     * @return List of random questions
     */
    @GetMapping("trivia/random/{theme}/{num}")
    public List<Trivia> getRandomQuestionByTheme(@PathVariable String theme, @PathVariable String num) {
        int n = Integer.parseInt(num);
        List<Integer> allId = triviaRepository.getAllIdsByQuestionTheme(theme);
        if(n >= allId.size()) {
            return triviaRepository.findAllByQuestionTheme(theme);
        }
        HashMap<Integer, Trivia> randomQuestion = new HashMap<>();

        for(int i = 0; i < n; i++) {
            Random rand = new Random();
            int qId = allId.get(rand.nextInt(allId.size()));
            while(randomQuestion.containsKey(qId)) {
                qId = (int) allId.get(rand.nextInt(n));
            }
            randomQuestion.put(qId, triviaRepository.findById(qId));
        }
        List<Trivia> list = new ArrayList<Trivia>(randomQuestion.values());
        return list;
    }

}
