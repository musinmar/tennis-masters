package tm.ui;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.DRAWITEMSTRUCT;

import tm.lib.engine.*;

public class PitchWidget extends Canvas
{
    private static final double PLAYER_SIZE = 15;
    private static final double BALL_SIZE = 6;
    private static final double TARGET_SIZE = 2;
    private static final double VISIBLE_TARGET_SIZE = 8;
    
    private static final boolean DRAW_BLOCKED_ZONES = true;
    
    public Label top_label;
    public Label bottom_label;
    Pitch pitch;
    Point pitch_size;
    Point pitch_pos;
    public static final int HMARGIN = 5;
    public static final int VMARGIN = 10;
    final Color PITCH_COLOR = new Color(TenisMasters.display, 254, 251, 126);
    final Color DARK_PITCH_COLOR = new Color(TenisMasters.display, 203, 200, 75);
    final Color LABEL_COLOR = TenisMasters.display.getSystemColor(SWT.COLOR_WHITE);
    Font font;
	//Image buffer;

    public PitchWidget(Composite parent, Pitch p)
    {
        super(parent, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        pitch = p;

        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        setLayout(layout);

        top_label = new Label(this, SWT.CENTER | SWT.BORDER);
        top_label.setText("---");
        //top_label.setBackground(PITCH_COLOR);
        top_label.setBackground(LABEL_COLOR);

        GridData data = new GridData();
        //data.horizontalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.END;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        top_label.setLayoutData(data);

        bottom_label = new Label(this, SWT.CENTER | SWT.BORDER);
        bottom_label.setText("+++");
        bottom_label.setBackground(LABEL_COLOR);
        data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.BEGINNING;
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        bottom_label.setLayoutData(data);

        FontData[] font_data = top_label.getFont().getFontData();
        font_data[0].setHeight(18);
        font = new Font(TenisMasters.display, font_data[0]);
        top_label.setFont(font);
        bottom_label.setFont(font);

        top_label.setVisible(false);
        bottom_label.setVisible(false);

        pack();

		//buffer = new Image(TenisMasters.display, getClientArea());
        addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                font.dispose();
                //buffer.dispose();
            }
        });

        addPaintListener(new PaintListener()
        {
            public void paintControl(PaintEvent e)
            {
				//update_buffer();
                //buffer = new Image(TenisMasters.display, getClientArea().width, getClientArea().height);
                //GC gc = new GC(buffer);
                //draw_pitch(gc);
                //e.gc.drawImage(buffer, 0, 0);
                //buffer = new Image(TenisMasters.display, getClientArea());
                draw_pitch(e.gc);
            }
        });
    }

    public void set_upper_text(String text)
    {
        top_label.setText(text);
        top_label.pack();
        GridData data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.END;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        top_label.setLayoutData(data);
        top_label.setVisible(true);
        layout();
    }

    public void set_bottom_text(String text)
    {
        bottom_label.setText(text);
        bottom_label.pack();
        GridData data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.BEGINNING;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        bottom_label.setLayoutData(data);
        bottom_label.setVisible(true);
        layout();
    }

    private void update_pitch_pos()
    {
        Point s = getSize();
        s.x -= 2 * HMARGIN;
        s.y -= 2 * VMARGIN;

        Vector2D psize = new Vector2D(Pitch.WIDTH, Pitch.HEIGHT);
        double coef = s.y / Pitch.HEIGHT;
        psize = psize.scalarMultiply(coef);

        if ((int) psize.getX() <= s.x)
        {
            pitch_size = VectorUtils.toPoint(psize);
            pitch_pos = new Point((s.x - pitch_size.x) / 2 + HMARGIN, VMARGIN);
        }
        else
        {
            coef = s.x / Pitch.WIDTH;
            psize = new Vector2D(Pitch.WIDTH, Pitch.HEIGHT);
            psize = psize.scalarMultiply(coef);
            pitch_size = VectorUtils.toPoint(psize);
            pitch_pos = new Point(HMARGIN, (s.y - pitch_size.y) / 2 + VMARGIN);
        }
    }

    private Point widget_pos(double x, double y)
    {
        double coef = pitch_size_coef();
        int px = (int) (x * coef + pitch_pos.x);
        int py = (int) ((Pitch.HALF_HEIGHT - y) * coef) + pitch_pos.y;
        return new Point(px, py);
    }
    
    private Point widget_pos(Vector2D pos)
    {
        return widget_pos(pos.getX(), pos.getY());
    }
    
    private Rectangle widget_rect(double x1, double y1, double x2, double y2) {
        Point p1 = widget_pos(x1, y1);
        Point p2 = widget_pos(x2, y2);
        return new Rectangle(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
    }
    
    private Rectangle widget_rect(Vector2D topLeft, Vector2D bottomRight) {
        return widget_rect(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
    }

    private double pitch_size_coef()
    {
        return pitch_size.x / Pitch.WIDTH;
    }

    /*public void update_buffer() {
     Rectangle a = this.getClientArea();
     Rectangle b = buffer.getBounds();
		
     if (a.width != b.width || a.height != b.height) {
     buffer.dispose();
     buffer = new Image(TenisMasters.display, a);
     buffer.setBackground(getBackground());
     }
     }*/
    public void update()
    {
        redraw();

        /*final Image buffer = new Image(TenisMasters.display, this.getClientArea());
         GC gc = new GC(buffer);		
         draw_pitch(gc);
         GC pitch_gc = new GC(this);
         pitch_gc.drawImage(buffer, 0, 0);
         buffer.dispose();
         gc.dispose();
         pitch_gc.dispose();*/
		//update_buffer();
		/*draw_pitch_borders(gc);
         draw_player(gc, pitch.player_1);
         draw_player(gc, pitch.player_2);
         draw_ball(gc);*/
		//draw_pitch(gc);
        //update_pitch_pos();
        //this.redraw(pitch_pos.x, pitch_pos.y, pitch_size.x, pitch_size.y, false);
		/*gc.setBackground(PITCH_COLOR); 
         gc.fillRectangle(pitch_pos.x + 1, pitch_pos.y + 1, pitch_size.x - 2, pitch_size.y / 2 - 2);
         gc.fillRectangle(pitch_pos.x + 1, pitch_pos.y + pitch_size.y / 2 + 1, pitch_size.x - 2, pitch_size.y / 2 - 3);		
         draw_player(gc, pitch.player_1);
         draw_player(gc, pitch.player_2);
         draw_ball(gc);		*/
    }

    public void draw_pitch(GC gc)
    {
        update_pitch_pos();
        draw_pitch_borders(gc);
        draw_player(gc, pitch.getPlayer(Side.HOME));
        draw_player(gc, pitch.getPlayer(Side.AWAY));
        draw_ball(gc);
    }

    public void draw_pitch_borders(GC gc)
    {
        gc.setBackground(PITCH_COLOR);
        gc.setLineWidth(2);
        Rectangle pitchRectangle = widget_rect(0, Pitch.HALF_HEIGHT, Pitch.WIDTH, -Pitch.HALF_HEIGHT);
        gc.fillRectangle(pitchRectangle);
        gc.setBackground(DARK_PITCH_COLOR);

        if (DRAW_BLOCKED_ZONES) {
            double homePlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.HOME).getPosition());
            gc.fillRectangle(widget_rect(0, 0, Pitch.WIDTH, -homePlayerBlockedZoneLength));
            double awayPlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.AWAY).getPosition());
            gc.fillRectangle(widget_rect(0, awayPlayerBlockedZoneLength, Pitch.WIDTH, 0));
        }
        
        gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_DARK_RED));
        gc.drawRectangle(pitchRectangle);
        gc.drawLine(pitch_pos.x, pitch_pos.y + pitch_size.y / 2, pitch_pos.x + pitch_size.x, pitch_pos.y + pitch_size.y / 2);
    }

    public void draw_player(GC gc, Player p)
    {
        double coef = pitch_size_coef();
        int radius = (int) (coef * PLAYER_SIZE / 2);
        if (radius < 1)
        {
            radius = 1;
        }
        Point pos = widget_pos(p.getPosition());
        if (!p.isLying())
        {
            gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_DARK_BLUE));
            gc.setBackground(TenisMasters.display.getSystemColor(SWT.COLOR_BLUE));
        }
        else
        {
            gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_DARK_RED));
            gc.setBackground(TenisMasters.display.getSystemColor(SWT.COLOR_RED));
        }
        gc.fillOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
        gc.setLineWidth(2);
        gc.drawOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
    }

    public void draw_ball(GC gc)
    {
        Ball ball = pitch.getBall();
        Point pos = widget_pos(ball.getPosition());
        double coef = pitch_size_coef();
        int radius = (int) (coef * BALL_SIZE / 2);
        if (radius < 1)
        {
            radius = 1;
        }
        gc.setBackground(TenisMasters.display.getSystemColor(SWT.COLOR_BLACK));
        gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_BLACK));
        gc.fillOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);

        pos = widget_pos(ball.getRealTarget());
        int size = (int) (coef * TARGET_SIZE / 2);
        if (size < 1)
        {
            size = 1;
        }
        gc.drawOval(pos.x - size, pos.y - size, size * 2, size * 2);

        pos = widget_pos(ball.getVisibleTarget());
        size = (int) (coef * VISIBLE_TARGET_SIZE / 2);
        if (size < 1)
        {
            size = 1;
        }
        gc.setLineWidth(1);
        gc.drawLine(pos.x - size, pos.y - size, pos.x + size, pos.y + size);
        gc.drawLine(pos.x - size, pos.y + size, pos.x + size, pos.y - size);
    }
}
