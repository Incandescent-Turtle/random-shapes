import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.Random;
import java.util.function.Supplier;

public class RandomShape extends JPanel {
	private final Random rand = new Random();
	private Area shape = new Area(new Rectangle(0,0,0,0));
	private final JFrame f;
	private KeyboardHandler keyboardHandler;
	private Color left, right;
	private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public RandomShape() {
		f = new JFrame();
		f.setType(javax.swing.JFrame.Type.UTILITY);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		var dim = new Dimension(100, 100);
		f.setPreferredSize(dim);
		f.setMinimumSize(dim);
		f.setLocationRelativeTo(null);
		f.setAlwaysOnTop(true);
		setOpaque(false);
		keyboardHandler = new KeyboardHandler(this);
		f.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (centerShape().contains(e.getPoint()))
				{
					toggleFrameTransparency();
				}
			}
		});

		f.add(this);
		f.setVisible(true);

		new Thread(() -> {
			try {
				while (true) {
					var width = Math.min(getWidth(), getHeight());
					shape = randomShape(rand, (getWidth() - width) / 2, 0, width, width, 3, 30);
					left = randomColor();
					right = randomColor();
					repaint();
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void keyPressed(int keyCode)
	{
		if (keyCode == KeyEvent.VK_H)
		{
			toggleFrameTransparency();
		}

		if (keyboardHandler.isDown(KeyEvent.VK_G) || keyboardHandler.isDown(KeyEvent.VK_S))
		{
			var b = f.getBounds();
			int dir = keyboardHandler.isDown(KeyEvent.VK_G) ? 1 : -1;
			f.setSize(b.width + 2 * dir, b.height + 2 * dir);
			snapToCorners();
		}

		var loc = f.getLocation();

		if (keyboardHandler.isDown(KeyEvent.VK_UP))
			loc.translate(0, -4);
		if (keyboardHandler.isDown(KeyEvent.VK_DOWN))
			loc.translate(0, 4);
		if (keyboardHandler.isDown(KeyEvent.VK_LEFT))
			loc.translate(-4, 0);
		if (keyboardHandler.isDown(KeyEvent.VK_RIGHT))
			loc.translate(4, 0);

		f.setLocation(loc);

		if(keyboardHandler.isDown(KeyEvent.VK_SHIFT))
		{
			var b = f.getBounds();
			int x = b.x, y = b.y;

			if (keyboardHandler.isDown(KeyEvent.VK_DOWN))
				y = screenSize.height - b.height;
			if (keyboardHandler.isDown(KeyEvent.VK_UP))
				y = 0;
			if (keyboardHandler.isDown(KeyEvent.VK_LEFT))
				x = 0;
			if (keyboardHandler.isDown(KeyEvent.VK_RIGHT))
				x = screenSize.width - b.width;
			f.setLocation(new Point(x, y));
		}
	}

	private Shape centerShape()
	{
		AffineTransform at = new AffineTransform();
		var b = shape.getBounds();
		at.translate(-b.x, -b.y);
		at.translate((getWidth() - b.width) / 2, (getHeight() - b.height) / 2);
		return at.createTransformedShape(shape);
	}

	private void toggleFrameTransparency()
	{
		f.dispose();
		if(f.isUndecorated())
			f.setBackground(Color.WHITE);
		f.setUndecorated(!f.isUndecorated());
		if(f.isUndecorated())
			f.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
		f.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		var grad = new GradientPaint(0,0,left, getWidth(),0, right);
		g.setPaint(grad);
		g.fill(centerShape());
	}

	private static Area randomShape(Random rand, int x, int y, int width, int height, int minPoints, int maxPoints)
	{
		Polygon shape = new Polygon();
		int n = rand.nextInt(maxPoints - minPoints) + minPoints;
		for(int i = 0; i < n; i++)
		{
			shape.addPoint(rand.nextInt(width) + x, rand.nextInt(height) + y);
		}
		return new Area(shape);
	}

	private void snapToCorners()
	{
		var b = f.getBounds();
		var left = b.x <= 30;
		var right = screenSize.width - (b.x + b.width) <= 30;
		var top = b.y <= 30;
		var bottom = screenSize.height - (b.y + b.height) <= 30;

		int x = b.x, y = b.y;

		if (bottom && (left || right))
			y = screenSize.height - b.height;

		if (top && (left || right))
			y = 0;

		if (left && (top || bottom))
			x = 0;

		if (right && (top || bottom))
			x = screenSize.width - b.width;

		f.setLocation(new Point(x, y));
	}

	public Color randomColor()
	{
		return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	}

	public static void main(String[] args)
	{
		new RandomShape();
	}
}
