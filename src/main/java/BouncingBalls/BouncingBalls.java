package BouncingBalls;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;


public final class BouncingBalls extends JFrame{


    public BouncingBalls() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2)); //CHECK

        Ball ball1 = new Ball(0, 0, Color.RED);
        Ball ball2 = new Ball(200, 200, Color.BLUE);

        add(ball1);
        add(ball2);

        Thread thread1 = new Thread(ball1);
        Thread thread2 = new Thread(ball2);

        thread1.start();
        thread2.start();

        pack(); //CHECK
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BouncingBalls::new);//  код який треба виконати у потоці, й який має взаємодіяти з графічним інтерфейсом - треба запустити у цьому методі.
        // Отож, нам потрібно оновлювати інтерфейс, це потрібно роботи в цьому методі, щоб уникнути проблем з потоковою безпекою і взаємодією з компонентами Swing.
    }


    // PRIVATE

    public class Ball extends JPanel implements Runnable {
        private int x; // Це координата по осі X, яка вказує на поточне положення кулі по горизонталі на панелі.
        private int y; //  Це координата по осі Y, яка вказує на поточне положення кулі по вертикалі на панелі.
        private int dx; // Вказує на швидкість руху кулі по горизонталі. Наприклад, якщо dx дорівнює 1, куля буде рухатися вправо; якщо -1, то вліво.
        private int dy; // Вказує на швидкість руху кулі по вертикалі. Наприклад, якщо dy дорівнює 1, куля буде рухатися вниз; якщо -1, то вгору.

        private final int diameter = 30; // Діаметр кулі
        private final Color color; // Колір кулі
        private final Random random = new Random(); // Генератор випадкових чисел

        public Ball(int startX, int startY, Color color) {
            this.x = startX;
            this.y = startY;
            this.color = color;
            this.dx = getRandomSpeed(); //CHECK
            this.dy = getRandomSpeed();
//            this.dx = 3;
//            this.dy = 3;

        }
        private int getRandomSpeed() {
            return random.nextInt(5) + 1; // speed range: 1 to 5
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
                move();
                repaint(); // перемальовує екземпляр класу Ball(панелі). Ініціює запит на оновлення панелі та викликає метод paintComponent(Graphics g).

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         *  Цей метод оновлює координати кулі відповідно до її поточної швидкості.
         *
         *  Якщо куля дійде до меж панелі, вона змінить напрямок руху.
         */
        private void move() {

            if (x + dx < 0 || x + dx > getWidth() - diameter) {
                // x + dx < 0 - значить куля вийшла за межі панелі зліва
                // x + dx > getWidth() - diameter - значить куля вийшла за межі панелі справа
                dx = -dx; // змінюємо напрямок руху кулі по горизонталі
            }
            if (y + dy < 0 || y + dy > getHeight() - diameter) {
                // y + dy < 0 - значить куля вийшла за межі панелі зверху
                // y + dy > getHeight() - diameter - значить куля вийшла за межі панелі знизу
                dy = -dy;
            }
            x += dx; // змінюємо координату x кулі на величину dx
            y += dy; // змінюємо координату y кулі на величину dy
        }


        /**
         *  Метод викликається для малювання кулі на панелі.
         *  Він встановлює колір кулі і малює її як круг за допомогою методу fillOval().
         * @param g - об'єкт класу Graphics, який використовується для малювання на панелі.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // відповідає за виконання функціональності базового класу JPanel для малювання компоненту.
            //  Клас JPanel має свою власну реалізацію методу paintComponent(Graphics g), яка відповідає за малювання всіх компонентів,
            //  що знаходяться на панелі. Викликаючи super.paintComponent(g), ми виконуємо цю базову реалізацію, яка виконує попередній внутрішній малюнок
            //  і очищає область малювання перед малюванням нових елементів. Без цього будемо мати артефакти малювання, інші проблеми.
            g.setColor(color);
            g.fillOval(x, y, diameter, diameter);
        }


        /**
         *  Цей метод визначає рекомендовані розміри компоненту (у цьому випадку, Ball), коли я додаю його до контейнера(Frame).
         *
         *  Метод pack() використовує ці рекомендовані розміри для визначення гарного розміру вікна(Frame), щоб усе влізло як треба.
         * @return
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400); // adjust panel size here
        }
    }

}
