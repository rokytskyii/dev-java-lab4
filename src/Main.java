import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Головний клас програми для виконання Лабораторної роботи №4.
 * Тема: Відношення між класами в мові програмування Java.
 *
 * @author Рокицький Олександр
 */
public class Main {

    /**
     * Точка входу в програму.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        System.out.println("Рокицький Олександр Сергійович");
        System.out.println("Студент групи ІО-33");
        System.out.println("Номер у списку групи: 20\n");

        try {
            // Розрахунок варіанту
            int studentNumber = 3320;
            int c3 = studentNumber % 3;
            int c17 = studentNumber % 17;

            System.out.println("C3 = " + studentNumber + " mod 3 = " + c3);
            System.out.println("C17 = " + studentNumber + " mod 17 = " + c17);

            System.out.println("\nЗавдання: Надрукувати слова без повторень "
                    + "в алфавітному порядку за першою літерою.");
            System.out.println("Вимога: Використати об'єктну структуру (Text, Sentence, Word, Letter).\n");

            // Вхідний текст (з зайвими пробілами та табуляціями для перевірки очищення)
            String rawString = "Я   люблю програмування\tі  програмування люблю я. " +
                    "Java це круто!   Круто це Java.";

            System.out.println("--- Вхідний 'брудний' текст ---");
            System.out.println(rawString);

            // 1. Створення об'єкта Text (парсинг та очищення)
            Text text = new Text(rawString);

            System.out.println("\n--- Відновлений текст (після cleaning) ---");
            System.out.println(text);

            // 2. Виконання бізнес-логіки варіанту 5 (C17=5)
            TextProcessor processor = new TextProcessor();
            processor.processAndPrintUniqueSortedWords(text);

        } catch (Exception e) {
            System.err.println("Критична помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Клас-обгортка для символу.
 * Представляє найменшу частину тексту.
 */
class Letter {
    private final char value;

    /**
     * Конструктор літери.
     * @param value символ
     */
    public Letter(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    /**
     * Повертає літеру в нижньому регістрі для порівняння.
     * @return нова літера в нижньому регістрі
     */
    public char toLowerCase() {
        return Character.toLowerCase(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Letter letter = (Letter) o;
        return value == letter.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

/**
 * Інтерфейс для елементів речення (слів або розділових знаків).
 */
interface SentencePart {
    String toString();
}

/**
 * Клас для представлення розділового знаку.
 */
class Punctuation implements SentencePart {
    private final String value;

    public Punctuation(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

/**
 * Клас для представлення слова.
 * Складається з масиву (списку) літер.
 * Реалізує Comparable для сортування.
 */
class Word implements SentencePart, Comparable<Word> {
    private final List<Letter> letters;

    /**
     * Конструктор слова з рядка.
     * @param wordString рядок, що містить слово
     */
    public Word(String wordString) {
        letters = new ArrayList<>();
        for (char c : wordString.toCharArray()) {
            letters.add(new Letter(c));
        }
    }

    /**
     * Отримати перший символ слова (для сортування, якщо потрібно).
     */
    public char getFirstChar() {
        if (letters.isEmpty()) return 0;
        return letters.get(0).toLowerCase();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Letter l : letters) {
            sb.append(l.toString());
        }
        return sb.toString();
    }

    /**
     * Реалізація порівняння для сортування (без урахування регістру).
     */
    @Override
    public int compareTo(Word other) {
        String s1 = this.toString().toLowerCase();
        String s2 = other.toString().toLowerCase();
        return s1.compareTo(s2);
    }

    /**
     * Перевизначення equals для коректної роботи distinct() (видалення дублікатів).
     * Порівняння без урахування регістру (Case Insensitive), згідно з логікою Лаб 2.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return this.toString().equalsIgnoreCase(word.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().toLowerCase().hashCode();
    }
}

/**
 * Клас для представлення речення.
 * Складається з елементів речення (слів та розділових знаків).
 */
class Sentence {
    private final List<SentencePart> parts;

    public Sentence() {
        this.parts = new ArrayList<>();
    }

    public void addPart(SentencePart part) {
        parts.add(part);
    }

    public List<Word> getWords() {
        List<Word> words = new ArrayList<>();
        for (SentencePart part : parts) {
            if (part instanceof Word) {
                words.add((Word) part);
            }
        }
        return words;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            SentencePart part = parts.get(i);
            sb.append(part.toString());

            // Додаємо пробіл, якщо поточний елемент слово, і наступний теж слово
            if (part instanceof Word && i < parts.size() - 1 && parts.get(i + 1) instanceof Word) {
                sb.append(" ");
            }
            // Логіка для розділових знаків: зазвичай вони "прилипають" до слова,
            // але після розділового знаку часто йде пробіл (це спрощена реалізація)
        }
        return sb.toString();
    }
}

/**
 * Клас для представлення тексту.
 * Складається з масиву речень.
 */
class Text {
    private final List<Sentence> sentences;

    /**
     * Конструктор, який приймає сирий рядок, очищує його та парсить.
     * @param rawText вхідний текст
     */
    public Text(String rawText) {
        this.sentences = new ArrayList<>();
        parseText(cleanText(rawText));
    }

    /**
     * Замінює послідовність табуляцій та пробілів одним пробілом (Вимога Лаб 4).
     * @param text неочищений текст
     * @return очищений текст
     */
    private String cleanText(String text) {
        if (text == null) return "";
        // Регулярний вираз \s+ знаходить 1 або більше пробільних символів (пробіл, таб, перенос)
        return text.replaceAll("[\\s\\t]+", " ");
    }

    /**
     * Парсинг тексту на речення та слова.
     * @param text очищений текст
     */
    private void parseText(String text) {
        // Розбиваємо текст на речення за допомогою регулярного виразу (крапка, знак оклику/питання)
        // (?<=[.!?]) - lookbehind, щоб залишити розділовий знак у реченні, або сплітити і потім додати
        // Простіший підхід: ітерувати і збирати. Використаємо Pattern Matcher для гнучкості.

        // Паттерн для пошуку слів та розділових знаків
        Pattern pattern = Pattern.compile("(?<word>[\\wа-яА-ЯїієґЇІЄҐ]+)|(?<punct>[^\\w\\sа-яА-ЯїієґЇІЄҐ])");

        String[] rawSentences = text.split("(?<=[.!?])\\s*"); // Грубе розбиття на речення

        for (String rawSentence : rawSentences) {
            Sentence sentence = new Sentence();
            Matcher matcher = pattern.matcher(rawSentence);

            while (matcher.find()) {
                String wordStr = matcher.group("word");
                String punctStr = matcher.group("punct");

                if (wordStr != null) {
                    sentence.addPart(new Word(wordStr));
                } else if (punctStr != null) {
                    sentence.addPart(new Punctuation(punctStr));
                }
            }
            sentences.add(sentence);
        }
    }

    /**
     * Отримати всі слова з усього тексту.
     * @return список слів
     */
    public List<Word> getAllWords() {
        List<Word> allWords = new ArrayList<>();
        for (Sentence s : sentences) {
            allWords.addAll(s.getWords());
        }
        return allWords;
    }

    @Override
    public String toString() {
        return sentences.stream()
                .map(Sentence::toString)
                .collect(Collectors.joining(" "));
    }
}

/**
 * Клас, що містить бізнес-логіку завдання.
 */
class TextProcessor {

    /**
     * Знаходить унікальні слова та сортує їх.
     * @param text об'єкт тексту
     */
    public void processAndPrintUniqueSortedWords(Text text) {
        List<Word> words = text.getAllWords();

        if (words.isEmpty()) {
            System.out.println("Текст не містить слів.");
            return;
        }

        // Використовуємо Stream API для сортування та видалення дублікатів.
        // Distinct працює завдяки перевизначеним equals() та hashCode() у класі Word.
        List<Word> sortedUniqueWords = words.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Результат обробки (слова без повторень за алфавітом):");
        System.out.println("-----------------------------------------------------");
        for (Word word : sortedUniqueWords) {
            System.out.println(word);
        }
    }
}