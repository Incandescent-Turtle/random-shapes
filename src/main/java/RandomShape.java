import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.Random;

public class RandomShape extends JPanel
{
	private final Random rand = new Random();
	private Area shape = randomShape(rand, 0, 0, 200, 200, 3, 100);
	private final JFrame f;
	private KeyboardHandler keyboardHandler;

	public RandomShape()
	{
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		var dim = new Dimension(100, 100);
		f.setPreferredSize(dim);
		f.setMinimumSize(dim);
		f.setLocationRelativeTo(null);
		f.setAlwaysOnTop(true);
		setOpaque(false);
		keyboardHandler = new KeyboardHandler(this);

//
//		InputMap im = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
//		ActionMap am = getActionMap();
//
//		class Mover extends AbstractAction
//		{
//			int x, y;
//
//			Mover(int x, int y)
//			{
//				this.x = x;
//				this.y = y;
//			}
//
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				var loc = f.getLocation();
//				loc.translate(x, y);
//				f.setLocation(loc);
//				System.out.println(x);
//			}
//		}
//
//		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
//		am.put("up", new Mover(0,-4));
//
//		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
//		am.put("down", new Mover(0,4));
//
//		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
//		am.put("left", new Mover(-4,0));
//
//		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
//		am.put("right", new Mover(4,0));

		f.add(this);
		f.setVisible(true);



		new Thread(() -> {

			try
			{
				while(true)
				{
					var width = Math.min(getWidth(), getHeight());
					shape = randomShape(rand, (getWidth() - width) / 2, 0, width, width, 3, 30);
					repaint();
					Thread.sleep(1000);
				}
			} catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public void keyPressed(int keyCode)
	{
		if(keyCode == KeyEvent.VK_H)
		{
			f.dispose();
			if(f.isUndecorated())
				f.setBackground(Color.WHITE);
			f.setUndecorated(!f.isUndecorated());
			if(f.isUndecorated())
				f.setBackground(!f.isUndecorated() ? Color.WHITE : new Color(1.0f, 1.0f, 1.0f, 0.0f));
			f.setVisible(true);
		}

		if(keyboardHandler.isDown(KeyEvent.VK_G) || keyboardHandler.isDown(KeyEvent.VK_S))
		{
			var b = f.getBounds();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			var left = b.x <= 30;
			var right = screenSize.width-(b.x+b.width) <= 30;
			var top = b.y <= 30;
			var bottom = screenSize.height-(b.y+b.height) <= 30;

			int dir = keyboardHandler.isDown(KeyEvent.VK_G) ? 1 : -1;
			f.setSize(b.width+2*dir, b.height+2*dir);
			b = f.getBounds();
			int x=b.x, y=b.y;

			if(bottom && (left || right))
				y = screenSize.height-b.height;

			if(top && (left || right))
				y = 0;

			if(left && (top || bottom))
				x = 0;

			if(right && (top || bottom))
				x = screenSize.width-b.width;

			f.setLocation(new Point(x,y));
		}

		var loc = f.getLocation();

		if(keyboardHandler.isDown(KeyEvent.VK_UP))
			loc.translate(0, -4);
		if(keyboardHandler.isDown(KeyEvent.VK_DOWN))
			loc.translate(0, 4);
		if(keyboardHandler.isDown(KeyEvent.VK_LEFT))
			loc.translate(-4, 0);
		if(keyboardHandler.isDown(KeyEvent.VK_RIGHT))
			loc.translate(4, 0);
		f.setLocation(loc);
	}

	@Override
	protected void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		var grad = new GradientPaint(0,0,randomColor(), getWidth(),0, randomColor());
		AffineTransform at=new AffineTransform();
		var b = shape.getBounds();
		at.translate(-b.x, -b.y);
		at.translate((getWidth() - b.width)/2, (getHeight()-b.height)/2);
		g.setPaint(grad);
		g.fill(at.createTransformedShape(shape));
	}

	protected static Area randomShape(Random rand, int x, int y, int width, int height, int minPoints, int maxPoints)
	{
		Polygon shape = new Polygon();
		int n = rand.nextInt(maxPoints - minPoints) + minPoints;
		for(int i = 0; i < n; i++)
		{
			shape.addPoint(rand.nextInt(width) + x, rand.nextInt(height) + y);
		}
		return new Area(shape);
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
