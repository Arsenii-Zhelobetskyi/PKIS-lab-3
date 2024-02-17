package BouncingBalls;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BouncingBalls extends JFrame {

    public BouncingBalls() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // усі потоки завершуються при закритті вікна
        setLayout(new BorderLayout());// розміщує компоненти у вікні по краям (зверху, знизу, зліва, справа) та в центрі.

        BouncingBallsPanel bouncingBallsPanel = new BouncingBallsPanel();// створюємо панель, на якій будуть відображатися кульки
        add(bouncingBallsPanel, BorderLayout.CENTER); // додаємо панель на вікно

        JButton addButton = new JButton("Add Ball"); // створюємо кнопку
        addButton.addActionListener(e -> bouncingBallsPanel.addBall()); // додаємо слухача подій на кнопку
        add(addButton, BorderLayout.SOUTH); // додаємо кнопку на вікно

        pack(); // використовується для автоматичного зміщення розміру вікна так, щоб воно точно вмістило всі компоненти, які ми до нього додали.
        setVisible(true);// встановлюємо видимість нашого вікна
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BouncingBalls::new);//  код який треба виконати у потоці, й який має взаємодіяти з графічним інтерфейсом - треба запустити у цьому методі.
        // Отож, нам потрібно оновлювати інтерфейс, це потрібно роботи в цьому методі, щоб уникнути проблем з потоковою безпекою і взаємодією з компонентами Swing.
    }
}

class BouncingBallsPanel extends JPanel { // клас, який наслідується від класу JPanel

    private final List<Ball> balls; // список кульок

    public BouncingBallsPanel() {
        balls = new ArrayList<>(); // створюємо список кульок
        setPreferredSize(new Dimension(400, 400)); // встановлюємо розмір панелі
    }

    public void addBall() { // метод, який додає кульку
        Random random = new Random(); // створюємо об'єкт класу Random, який буде генерувати випадкові числа
        int startX = random.nextInt(getWidth() - Ball.DIAMETER); // генеруємо випадкову початкову позицію по осі X
        int startY = random.nextInt(getHeight() - Ball.DIAMETER); // генеруємо випадкову початкову позицію по осі Y
        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)); // генеруємо випадковий колір
        Ball ball = new Ball(startX, startY, color, getWidth(), getHeight(), this::repaint); // створюємо об'єкт класу Ball
        balls.add(ball); // додаємо кульку в список

        Thread thread = new Thread(ball); // створюємо потік
        int priority = random.nextInt(10) + 1; // Генеруємо випадковий пріоритет від 1 до 10
        thread.setPriority(priority); // встановлюємо пріоритет потоку
        thread.start(); // запускаємо потік

    }

    /**
     *  Метод викликається для малювання кулі на панелі.
     *  Він встановлює колір кулі і малює її як круг за допомогою методу fillOval().
     *  @param g - об'єкт класу Graphics, який використовується для малювання на панелі.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //  Відповідає за виконання функціональності базового класу JPanel для малювання компоненту.
        //  Клас JPanel має свою власну реалізацію методу paintComponent(Graphics g), яка відповідає за малювання всіх компонентів,
        //  що знаходяться на панелі. Викликаючи super.paintComponent(g), ми виконуємо цю базову реалізацію, яка виконує попередній внутрішній малюнок
        //  і очищає область малювання перед малюванням нових елементів. Без цього будемо мати артефакти малювання, інші проблеми.
        for (Ball ball : balls) {
            ball.draw(g);
        }
    }
}

class Ball implements Runnable { // клас, який реалізує інтерфейс Runnable
    interface Repaint {
        void repaint();
    } // ми будемо передавати функцію repaint() з класу BouncingBallsPanel в конструктор класу Ball, щоб малювати кульку
    public static final int DIAMETER = 30; // діаметр кульки
    private int x; // Це координата по осі X, яка вказує на поточне положення кулі по горизонталі на панелі.
    private int y; //  Це координата по осі Y, яка вказує на поточне положення кулі по вертикалі на панелі.
    private int dx; // Вказує на швидкість руху кулі по горизонталі. Наприклад, якщо dx дорівнює 1, куля буде рухатися вправо; якщо -1, то вліво.
    private int dy; // Вказує на швидкість руху кулі по вертикалі. Наприклад, якщо dy дорівнює 1, куля буде рухатися вниз; якщо -1, то вгору.
    private final Color color; // Колір кулі
    private final int panelWidth; // ширина панелі
    private final int panelHeight; // висота панелі

    private final Repaint repaint; // функція repaint() з класу BouncingBallsPanel

    public Ball(int x, int y, Color color, int panelWidth, int panelHeight, Repaint repaint) { // конструктор класу Ball
        this.x = x;
        this.y = y;
        this.color = color;
        dx = getRandomSpeed();
        dy = getRandomSpeed();
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.repaint = repaint;

    }

    private int getRandomSpeed() { // метод, який генерує випадкову швидкість
        return (int) (Math.random() * 5) + 1;
    }

    public void draw(Graphics g) { // метод, який малює кульку
        g.setColor(color);
        g.fillOval(x, y, DIAMETER, DIAMETER);
    }

    /**
     *  Цей метод оновлює координати кулі відповідно до її поточної швидкості.
     *
     *  Якщо куля дійде до меж панелі, вона змінить напрямок руху.
     */
    public void move(int panelWidth, int panelHeight) {
        x += dx; // змінюємо координату x кулі на величину dx
        y += dy; // змінюємо координату y кулі на величину dy

        if (x < 0 || x > panelWidth - DIAMETER) {
            // x  < 0 - значить куля вийшла за межі панелі зліва
            // x  > getWidth() - diameter - значить куля вийшла за межі панелі справа
            dx = -dx; // змінюємо напрямок руху кулі по горизонталі
        }
        if (y < 0 || y > panelHeight - DIAMETER) {
            // y  < 0 - значить куля вийшла за межі панелі зверху
            // y +> getHeight() - diameter - значить куля вийшла за межі панелі знизу
            dy = -dy;
        }
    }

    /**
     * Основна логіка анімації кулі відбувається тут.
     *
     * Цикл while(true) виконується безперервно, тобто куля буде рухатися по панелі безперервно. Це потрібно для анімації.
     *
     * Кожен крок циклу оновлює положення кулі методом move() та перерисовує панель методом repaint();
     *
     * Метод Thread.sleep(10) відповідає за затримку виконання потоку на 10 мілісекунд між кроками анімації.
     * Це робиться для того, щоб контролювати швидкість анімації. Якщо не встановити затримку, анімація
     * відображатиметься надто швидко, і користувач може не встигнути розрізнити кожен кадр.
     */
    @Override
    public void run() {
        while (true) {
            move(panelWidth, panelHeight);
            repaint.repaint(); // перемальовує екземпляр класу Ball(панелі). Ініціює запит на оновлення панелі та викликає метод paintComponent(Graphics g).
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
