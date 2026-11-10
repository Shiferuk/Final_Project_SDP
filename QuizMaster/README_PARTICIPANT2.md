# Участник 2: Strategy + Decorator

**Ответственность:** оценка ответов, подсказки, 50/50, бонусы.

## Strategy (`strategy/`)
- `ScoringStrategy` - интерфейс алгоритма (`pointsFor(Question)`)
- `StandardScoring` - 1 балл за верный ответ
- `DifficultyBasedScoring` - Easy 1 / Medium 2 / Hard 3
- `ScoreCalculator` - Context, `setStrategy()` меняет алгоритм без правки кода теста
- `ScoreReport` - баллы, максимум, процент, список ошибок

## Decorator (`decorator/`)
- `QuestionView` - Component; `BasicQuestionView` - ConcreteComponent
- `QuestionDecorator` - базовый декоратор
- `HintDecorator` - показывает подсказку
- `FiftyFiftyDecorator` - убирает 2 неверных варианта (для True/False ничего не ломает)
- `BonusDecorator` - дополнительные баллы за верный ответ
- Декораторы комбинируются: `new BonusDecorator(new HintDecorator(new BasicQuestionView(q)))`

## Стыки с другими участниками
- Участник 1 даёт `Question`/`Answer`/`Difficulty` (здесь минимальные версии в `model/`; заменить на полные).
- Участник 3 вызывает `ScoreCalculator.calculate(views, answers)` при завершении теста.

## Тесты
`src/test/java/com/quizmaster/Participant2Test.java` - 9 тестов (JUnit 5): `mvn test`.
