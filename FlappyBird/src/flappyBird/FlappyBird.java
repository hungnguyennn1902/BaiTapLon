package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JFrame;

public class FlappyBird implements ActionListener, MouseListener {

	public static FlappyBird flappyBird;

	public final int WIDTH = 800, HEIGHT = 800;

	public Renderer renderer; // ket xuat

	public Rectangle bird; // bird

	public ArrayList<Rectangle> columns; // tạo 1 dãy các chướng ngại vật

	public Random rand;

	public int ticks, yMotion, score; // tích tắc và chuyển động của tọa độ y, điểm

	public boolean gameOver, started;

	public FlappyBird() {

		JFrame jframe = new JFrame();
		Timer timer = new Timer(20, this); // Timer(int delay, ActionListener listener)

		renderer = new Renderer();

		rand = new Random();

		jframe.add(renderer);

		jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.setTitle("Flappy Bird");
		jframe.setVisible(true);
		jframe.setResizable(false);

		jframe.addMouseListener(this);

		bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); // tạo tọa độ, width, height cho bird
		columns = new ArrayList<Rectangle>(); // tạo các chướng ngại vật ngay sau bird

		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		timer.start();
	}

	public void addColumn(boolean start) {
		int space = 300; // khoảng trống giữa các cột
		int width = 100; // chiều rộng của cột
		int height = 50 + rand.nextInt(300); // chiều cao của cột

		// tạo các chướng ngại vật
		if (start) {
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
		} else {
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
		}
	}

	public void paintColumn(Graphics g, Rectangle column) { // tạo các cột - chướng ngại vật
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void jump() {
		if (gameOver) {
			gameOver = false;
			started = true;
			bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); // tạo tọa độ, width, height cho bird
			columns.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
		}

		if (!started) {
			started = true;
		} else if (!gameOver) {
			if (yMotion > 0) {
				yMotion = 0;
			}
			yMotion -= 10;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ticks++;

		int speed = 10;

		if (started) {

			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);
				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}

			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);
				if (column.x + column.y < 0) {

//					columns.remove(column); // khi các cột đã qua -> xóa cột

					if (column.y == 0) {
						addColumn(false);
					}
				}

			}

			bird.y += yMotion;

			for (Rectangle column : columns) {

				if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10
						&& bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
					score++;
				}
				if (column.intersects(bird)) // phát hiện điểm va chạm
				{
					gameOver = true;

					bird.x = column.x - bird.width;
				}
			}
			if (bird.y >= HEIGHT - bird.height - 120 || bird.y < 0) {
				gameOver = true;
			}
			if (bird.y + yMotion >= HEIGHT - 120) {

				bird.y = HEIGHT - 120 - bird.height;
			}

		}

		renderer.repaint();

	}

	public void repaint(Graphics g) {
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT); // được sử dụng để điền màu mặc định và độ rộng và chiều cao đã cho vào hình
											// chữ nhật

		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT - 120, WIDTH, 150); // tô màu cho đất

		g.setColor(Color.green);
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);

		g.setColor(Color.red);
		g.fillRect(bird.x, bird.y, bird.width, bird.height); // tô màu cho bird

		for (Rectangle column : columns) { // tô màu cho chướng ngại vật
			paintColumn(g, column);
		}

		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100)); // Font(String name, int style, int size)

		if (!started) {
			g.drawString("Click to Start!!", 60, HEIGHT / 2 - 50); // tọa độ Start
		}

		if (gameOver) {
			g.drawString("Game Over!", 100, HEIGHT / 2 - 50); // tọa độ Game Over
		}

		if (!gameOver && started) { // hiển thị điểm
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}

		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 70)); // Font(String name, int style, int size)

		if (gameOver) {
			g.drawString("Score: " + String.valueOf(score), WIDTH / 2 - 130, HEIGHT / 2 + 50); // hiển thị điểm của bạn
																								// khi gameOver
		}
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		jump();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
