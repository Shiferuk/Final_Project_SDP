# QuizMaster

**Course project - Software Design Patterns (Final Project)**
**Group:** SE-2516
**Team:** Abdulla Nurdaulet, Angsar Baibossinov, Yernur Inkarbek

QuizMaster is a JavaFX desktop application for creating and taking quizzes. A user can create, edit and delete quizzes, take a quiz against a countdown timer with a hint, 50/50 and bonus, choose the scoring algorithm, see the result with the mistakes, and review the attempt history with statistics and charts. Quizzes and attempts are saved to JSON files and are still there after a restart.

Main user journey: **create or select a quiz -> configure it (scoring algorithm, time and question limit) -> take it -> view and save the result.**

## 1. Environment requirements

| Tool | Version |
|---|---|
| JDK | 21 |
| Maven | 3.9 or newer |
| Internet | needed once: Maven downloads JavaFX 21.0.6, Jackson 2.18.2 and JUnit 5.11.4 |
| OS | Windows, macOS or Linux with a desktop (the application opens a JavaFX window) |

## 2. Setup and launch

```
git clone https://github.com/Shiferuk/Final_Project_SDP.git
cd Final_Project_SDP-main
mvn clean test          # runs all automated tests (no GUI needed)
mvn javafx:run          # starts the application (main class com.quizmaster.app.Main)
```

If the project was received as a ZIP archive, unpack it and run the same commands inside the unpacked folder (the one that contains `pom.xml`).

In IntelliJ IDEA: open the folder as a Maven project, wait until the dependencies are downloaded, then run `com.quizmaster.app.Main` (or the `javafx:run` Maven goal).

## 3. Sample data

The application can be demonstrated immediately. On the first start, when there are no saved quizzes, three sample quizzes are created (`SampleQuizData`):

| Quiz | Category | Questions | Limits |
|---|---|---|---|
| Programming Basics | Programming | Multiple Choice, Multiple Select | 10 minutes, up to 5 questions |
| Mathematics Basics | Mathematics | Multiple Choice, True/False | 10 minutes, up to 5 questions |
| English Grammar | English | True/False, Multiple Choice | 10 minutes, up to 5 questions |

All three question types are present in the samples, and every question has a hint.

Files are stored in the `data/` folder next to the project (created automatically, not committed to Git):
- `data/quizzes.json` - all quizzes (loaded again on every start);
- `data/attempts.json` - history of finished attempts (appears after the first finished quiz).

Delete the `data/` folder to return to the original sample data and an empty history.

## 4. How to check the application

The window has two tabs.

### Tab "Create / Edit Quiz" (Factory Method, Builder)

1. **Browse and edit.** Select a quiz in the list: its data is loaded into the form. Change it and press *Update Quiz*, or press *Delete Quiz* (a confirmation is shown).
2. **Create a quiz.** Fill title, category, difficulty, time limit (minutes) and question limit. Add questions of type *Multiple Choice*, *True/False* or *Multiple Select* (the hint field is optional), then press *Save Quiz*. A message "Quiz saved successfully." is shown and the quiz appears in the list.
3. **Validation (error cases).** Each of these shows an error message and nothing is saved:
   - empty title;
   - a quiz without questions;
   - a question without a correct answer;
   - more questions than the question limit.
4. Restart the application: the new quiz is still in the list.

### Tab "Take Quiz & History" (Strategy, Decorator, Observer, Facade)

1. Select a quiz and a scoring algorithm:
   - *Standard* - 1 point per correct answer;
   - *Difficulty-Based* - Easy 1 / Medium 2 / Hard 3 points;
   - *Time Bonus* - 1 point + 2 if answered within 10 s, + 1 if within 20 s (maximum 3 per question).

   The algorithm can also be changed while the quiz is running.
2. Press *Start Quiz*. The timer counts down. At 00:00 the quiz finishes automatically and the attempt is saved. (To see this quickly, create a quiz with a time limit of 1 minute and wait.)
3. On a question use *Hint* (shows the hint), *50/50* (removes two wrong answers) and *Bonus (+1)* (one extra point for a correct answer). Move with *Previous* / *Next* (answers are remembered), then press *Finish*. *Cancel Quiz* stops the quiz and saves nothing.
4. The result shows the score, the percent and the questions that were answered wrongly.
5. Below the result: the history of attempts, the statistics (number of attempts, best and average percent, correct answers), a pie chart (correct vs wrong) and a bar chart (result of each attempt).
6. **Switching behavior check:** run the same quiz twice with the same answers and different scoring algorithms - the score is different.
7. Close and start the application again: quizzes and the history are still there.

## 5. Design patterns

| Pattern | Main classes | Where to see it |
|---|---|---|
| Factory Method | `factory.QuestionFactory`, `MultipleChoiceFactory`, `TrueFalseFactory`, `MultipleSelectFactory` | Adding a question of each type |
| Builder | `builder.QuizBuilder` | Saving a quiz; validation messages |
| Strategy | `strategy.ScoringStrategy`, `StandardScoring`, `DifficultyBasedScoring`, `TimeBonusScoring`, `ScoreCalculator` | Different score for the same answers |
| Decorator | `decorator.QuestionView`, `BasicQuestionView`, `QuestionDecorator`, `HintDecorator`, `FiftyFiftyDecorator`, `BonusDecorator` | Hint, 50/50, Bonus buttons |
| Observer | `observer.QuizTimer`, `TimerObserver` | Timer label and automatic finish |
| Facade | `facade.QuizFacade`, `HistoryStatistics` | The play screen talks only to the facade |

Responsible students: Factory Method and Builder - Abdulla Nurdaulet; Strategy and Decorator - Angsar Baibossinov; Observer and Facade - Yernur Inkarbek. See the report for the full table, UML diagrams and code excerpts.

## 6. Project structure

```
src/main/java/com/quizmaster
  app/          Main (JavaFX start, two tabs)
  controller/   QuizCreationController, QuizPlayController (UI only)
  service/      QuizManagementService, SampleQuizData (business logic)
  repository/   QuizRepository, AttemptRepository (JSON storage)
  model/        Quiz, Question (+3 types), Answer, QuizAttempt, QuizResult, enums
  factory/ builder/ strategy/ decorator/ observer/ facade/   (the six patterns)
src/test/java   JUnit 5 tests
docsUml/        UML diagrams (class diagram, sequence diagram)
```

The code is split into three layers: UI (`app`, `controller`), business logic (`service`, the six pattern packages, `model`) and data storage (`repository`). The business logic does not depend on JavaFX.

## 7. Tests

```
mvn test
```

65 JUnit 5 tests run without the GUI. They cover: factory validation, builder validation, quiz CRUD, scoring strategies (including switching the strategy), decorators (Hint, 50/50, Bonus and stacking), the timer and the automatic finish, saving and reloading quizzes and attempts, and statistics. The test scenarios with inputs, expected and actual results are in section 2.6 of the report.

## 8. AI use

AI assistance (Claude, Anthropic) was used for code review, fixing the integration of the three parts, and drafting this README and the report. See the "AI Use Disclosure" section of the report. External libraries: JavaFX, Jackson, JUnit 5.
